package com.zprinter.app.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;

import java.util.Set;

public class BluetoothScanner {

    private BluetoothAdapter adapter;
    private BroadcastReceiver receiver;
    private JSArray devicesArray;

    public interface ScanListener {
        void onFinished(JSArray devices);
        void onError(String message);
    }

    public void scan(Activity activity, ScanListener listener) {

        adapter = BluetoothAdapter.getDefaultAdapter();

        // =========================
        // Bluetooth supported?
        // =========================
        if (adapter == null) {
            listener.onError("Bluetooth not supported");
            return;
        }

        // =========================
        // Bluetooth OFF → ask enable
        // =========================
        if (!adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivity(enableBtIntent);
            listener.onError("Please enable bluetooth");
            return;
        }

        // =========================
        // Permission check (Android 12+)
        // =========================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (activity.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    activity.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                // if user denied → send to app settings
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);

                listener.onError("Bluetooth permission required");
                return;
            }
        }

        // =========================
        // Start Scan
        // =========================
        devicesArray = new JSArray();

        // Add paired devices
        Set<BluetoothDevice> paired = adapter.getBondedDevices();
        for (BluetoothDevice device : paired) {
            JSObject d = new JSObject();
            d.put("name", device.getName());
            d.put("address", device.getAddress());
            devicesArray.put(d);
        }

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        JSObject d = new JSObject();
                        d.put("name", device.getName());
                        d.put("address", device.getAddress());
                        devicesArray.put(d);
                    }
                }

                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    try {
                        context.unregisterReceiver(receiver);
                    } catch (Exception ignored) {}
                    listener.onFinished(devicesArray);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(receiver, filter);

        if (adapter.isDiscovering()) adapter.cancelDiscovery();
        adapter.startDiscovery();
    }
}
