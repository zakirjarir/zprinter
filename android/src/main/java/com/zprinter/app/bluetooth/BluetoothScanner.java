package com.zprinter.app.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.content.ContextCompat;
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
    private boolean finished;

    public interface ScanListener {
        void onFinished(JSArray devices);
        void onError(String message);
        void onProgress(JSObject device);
    }

    public void scan(Activity activity, ScanListener listener) {
        stopInternal(false);

        this.activity = activity;
        this.scanListener = listener;
        this.devicesArray = new JSArray();
        this.deviceAddresses = new ArrayList<>();
        this.finished = false;

        if (activity == null) {
            listener.onError("Activity is unavailable");
            return;
        }

        BluetoothManager bluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        adapter = bluetoothManager != null ? bluetoothManager.getAdapter() : null;
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

    @SuppressLint("MissingPermission")
    private void startBluetoothScan() {
        addPairedDevices();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = getBluetoothDeviceExtra(intent);
                    if (device == null) {
                        return;
                    }

                    String address = device.getAddress();
                    if (address == null || deviceAddresses.contains(address)) {
                        return;
                    }

                    String name = "Unknown Device";
                    boolean isPaired = false;
                    try {
                        if (device.getName() != null && !device.getName().isEmpty()) {
                            name = device.getName();
                        }
                        isPaired = device.getBondState() == BluetoothDevice.BOND_BONDED;
                    } catch (SecurityException exception) {
                        Log.w(TAG, "Unable to read Bluetooth device metadata", exception);
                    }

                    JSObject deviceJson = new JSObject();
                    deviceJson.put("name", name);
                    deviceJson.put("address", address);
                    deviceJson.put("isPaired", isPaired);

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
        ContextCompat.registerReceiver(activity, receiver, filter, ContextCompat.RECEIVER_EXPORTED);

        try {
            if (adapter.isDiscovering()) {
                adapter.cancelDiscovery();
            }

            if (!adapter.startDiscovery()) {
                // Even if active discovery cannot start, return already-paired printers instead of failing
                if (devicesArray.length() > 0) {
                    stopInternal(true);
                } else {
                    stopInternal(false);
                    if (scanListener != null) {
                        scanListener.onError("Failed to start Bluetooth discovery");
                    }
                }
                return;
            }
        } catch (SecurityException exception) {
            stopInternal(false);
            if (scanListener != null) {
                scanListener.onError("Bluetooth scan permission denied: " + exception.getMessage());
            }
            return;
        }

        handler.postDelayed(() -> stopInternal(true), SCAN_DURATION_MS);
    }

    @SuppressLint("MissingPermission")
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

                String name = device.getName() != null && !device.getName().isEmpty() ? device.getName() : "Unknown Device";

                JSObject deviceJson = new JSObject();
                deviceJson.put("name", name);
                deviceJson.put("address", address);
                deviceJson.put("isPaired", true);

                deviceAddresses.add(address);
                devicesArray.put(deviceJson);
            }
        } catch (SecurityException exception) {
            Log.e(TAG, "Unable to read paired devices", exception);
        }
    }

    @SuppressLint("MissingPermission")
    private synchronized void stopInternal(boolean notifyFinished) {
        handler.removeCallbacksAndMessages(null);

        try {
            if (adapter != null && adapter.isDiscovering()) {
                adapter.cancelDiscovery();
            }
        } catch (SecurityException ignored) {}

        if (receiver != null && activity != null) {
            try {
                activity.unregisterReceiver(receiver);
            } catch (IllegalArgumentException ignored) {}
            receiver = null;
        }

        if (notifyFinished && !finished && scanListener != null) {
            finished = true;
            ScanListener listener = scanListener;
            scanListener = null;
            listener.onFinished(devicesArray != null ? devicesArray : new JSArray());
        } else if (!notifyFinished) {
            finished = true;
        }
    }

    @SuppressWarnings("deprecation")
    private BluetoothDevice getBluetoothDeviceExtra(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice.class);
        }
        return intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
    }
}
