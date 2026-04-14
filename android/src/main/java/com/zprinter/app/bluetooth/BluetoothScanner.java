package com.zprinter.app.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothScanner {

    private static final String TAG = "BluetoothScanner";
    private static final int SCAN_DURATION_MS = 10000;

    private BluetoothAdapter adapter;
    private BroadcastReceiver receiver;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Activity activity;
    private ScanListener scanListener;
    private JSArray devicesArray;
    private List<String> deviceAddresses;

    public interface ScanListener {
        void onFinished(JSArray devices);
        void onError(String message);
        void onProgress(JSObject device);
    }

    public void scan(Activity activity, ScanListener listener) {
        this.activity = activity;
        this.scanListener = listener;
        this.devicesArray = new JSArray();
        this.deviceAddresses = new ArrayList<>();

        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            listener.onError("Bluetooth is not supported on this device");
            return;
        }

        if (!adapter.isEnabled()) {
            listener.onError("Bluetooth is turned off");
            return;
        }

        startBluetoothScan();
    }

    public void stop() {
        stopInternal(false);
    }

    private void startBluetoothScan() {
        addPairedDevices();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device == null) {
                        return;
                    }

                    String address = device.getAddress();
                    if (address == null || deviceAddresses.contains(address)) {
                        return;
                    }

                    JSObject deviceJson = new JSObject();
                    deviceJson.put("name", device.getName() != null ? device.getName() : "Unknown Device");
                    deviceJson.put("address", address);
                    deviceJson.put("isPaired", device.getBondState() == BluetoothDevice.BOND_BONDED);

                    deviceAddresses.add(address);
                    devicesArray.put(deviceJson);

                    if (scanListener != null) {
                        scanListener.onProgress(deviceJson);
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    stopInternal(true);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(receiver, filter);

        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }

        if (!adapter.startDiscovery()) {
            stopInternal(false);
            if (scanListener != null) {
                scanListener.onError("Failed to start Bluetooth discovery");
            }
            return;
        }

        handler.postDelayed(() -> stopInternal(true), SCAN_DURATION_MS);
    }

    private void addPairedDevices() {
        try {
            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
            if (pairedDevices == null) {
                return;
            }

            for (BluetoothDevice device : pairedDevices) {
                String address = device.getAddress();
                if (address == null || deviceAddresses.contains(address)) {
                    continue;
                }

                JSObject deviceJson = new JSObject();
                deviceJson.put("name", device.getName() != null ? device.getName() : "Unknown Device");
                deviceJson.put("address", address);
                deviceJson.put("isPaired", true);

                deviceAddresses.add(address);
                devicesArray.put(deviceJson);
            }
        } catch (SecurityException exception) {
            Log.e(TAG, "Unable to read paired devices", exception);
        }
    }

    private void stopInternal(boolean notifyFinished) {
        handler.removeCallbacksAndMessages(null);

        if (adapter != null && adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }

        if (receiver != null && activity != null) {
            try {
                activity.unregisterReceiver(receiver);
            } catch (IllegalArgumentException ignored) {}
            receiver = null;
        }

        if (notifyFinished && scanListener != null) {
            scanListener.onFinished(devicesArray != null ? devicesArray : new JSArray());
        }
    }
}
