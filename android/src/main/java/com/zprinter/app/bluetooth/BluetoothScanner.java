package com.zprinter.app.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothScanner {

    private static final String TAG = "BluetoothScanner";
    private static final int SCAN_DURATION = 10000; // 10 seconds

    private BluetoothAdapter adapter;
    private BroadcastReceiver receiver;
    private JSArray devicesArray;
    private List<String> deviceAddresses; // To avoid duplicates
    private ScanListener scanListener;
    private Activity activity;
    private Handler handler;

    public interface ScanListener {
        void onFinished(JSArray devices);
        void onError(String message);
        void onProgress(JSObject device); // Optional: for real-time updates
    }

    public void scan(Activity activity, ScanListener listener) {
        this.activity = activity;
        this.scanListener = listener;
        this.deviceAddresses = new ArrayList<>();
        this.devicesArray = new JSArray();
        this.handler = new Handler(Looper.getMainLooper());

        adapter = BluetoothAdapter.getDefaultAdapter();

        // =========================
        // Check Bluetooth Support
        // =========================
        if (adapter == null) {
            listener.onError("Bluetooth not supported on this device");
            return;
        }

        // =========================
        // Check Bluetooth Enabled
        // =========================
        if (!adapter.isEnabled()) {
            // Try to enable Bluetooth automatically
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivity(enableBtIntent);

            // Wait a moment for Bluetooth to enable
            handler.postDelayed(() -> {
                if (!adapter.isEnabled()) {
                    listener.onError("Please enable Bluetooth manually");
                } else {
                    startScanWithPermissions();
                }
            }, 2000);
            return;
        }

        startScanWithPermissions();
    }

    private void startScanWithPermissions() {
        // =========================
        // Check Permissions (Android 12+)
        // =========================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                // Request permissions
                ActivityCompat.requestPermissions(activity,
                        new String[]{
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        1001);

                // We'll need to handle permission result in the plugin
                scanListener.onError("Bluetooth permissions required");
                return;
            }
        }

        // =========================
        // Check Location Permission (Android 10+)
        // =========================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1002);
                scanListener.onError("Location permission required for Bluetooth scanning");
                return;
            }
        }

        // =========================
        // Start Scanning
        // =========================
        startBluetoothScan();
    }

    private void startBluetoothScan() {
        Log.d(TAG, "Starting Bluetooth scan...");

        // Clear previous data
        deviceAddresses.clear();
        devicesArray = new JSArray();

        // =========================
        // Add Paired Devices First
        // =========================
        addPairedDevices();

        // =========================
        // Setup BroadcastReceiver
        // =========================
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device != null) {
                        String address = device.getAddress();
                        String name = device.getName();

                        // Skip if already added (duplicate check)
                        if (deviceAddresses.contains(address)) {
                            return;
                        }

                        deviceAddresses.add(address);

                        JSObject deviceObj = new JSObject();
                        deviceObj.put("name", name != null ? name : "Unknown Device");
                        deviceObj.put("address", address);
                        deviceObj.put("isPaired", device.getBondState() == BluetoothDevice.BOND_BONDED);

                        devicesArray.put(deviceObj);

                        Log.d(TAG, "Found device: " + name + " [" + address + "]");

                        // Optional: Send real-time update
                        if (scanListener != null) {
                            scanListener.onProgress(deviceObj);
                        }
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.d(TAG, "Scan finished. Found " + devicesArray.length() + " devices");
                    stopScan();
                }
            }
        };

        // =========================
        // Register Receiver
        // =========================
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        try {
            activity.registerReceiver(receiver, filter);
        } catch (Exception e) {
            Log.e(TAG, "Error registering receiver: " + e.getMessage());
            scanListener.onError("Failed to register Bluetooth receiver");
            return;
        }

        // =========================
        // Start Discovery
        // =========================
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }

        boolean started = adapter.startDiscovery();

        if (!started) {
            Log.e(TAG, "Failed to start discovery");
            scanListener.onError("Failed to start Bluetooth scan");
            return;
        }

        Log.d(TAG, "Discovery started successfully");

        // =========================
        // Auto-stop after SCAN_DURATION
        // =========================
        handler.postDelayed(() -> {
            if (adapter.isDiscovering()) {
                Log.d(TAG, "Auto-stopping scan after timeout");
                stopScan();
            }
        }, SCAN_DURATION);
    }

    private void addPairedDevices() {
        try {
            Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

            if (pairedDevices != null && pairedDevices.size() > 0) {
                Log.d(TAG, "Found " + pairedDevices.size() + " paired devices");

                for (BluetoothDevice device : pairedDevices) {
                    String address = device.getAddress();
                    String name = device.getName();

                    deviceAddresses.add(address);

                    JSObject deviceObj = new JSObject();
                    deviceObj.put("name", name != null ? name : "Unknown Device");
                    deviceObj.put("address", address);
                    deviceObj.put("isPaired", true);

                    devicesArray.put(deviceObj);
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception getting paired devices: " + e.getMessage());
        }
    }

    private void stopScan() {
        try {
            if (adapter != null && adapter.isDiscovering()) {
                adapter.cancelDiscovery();
            }

            if (receiver != null && activity != null) {
                try {
                    activity.unregisterReceiver(receiver);
                } catch (IllegalArgumentException e) {
                    // Receiver not registered
                }
                receiver = null;
            }

            handler.removeCallbacksAndMessages(null);

            if (scanListener != null) {
                scanListener.onFinished(devicesArray);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error stopping scan: " + e.getMessage());
            if (scanListener != null) {
                scanListener.onError("Error stopping scan: " + e.getMessage());
            }
        }
    }

    // Call this method when permissions are granted
    public void onPermissionResult(boolean granted) {
        if (granted) {
            startBluetoothScan();
        } else {
            scanListener.onError("Bluetooth permissions denied");
        }
    }
}