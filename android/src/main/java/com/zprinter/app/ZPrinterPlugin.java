package com.zprinter.app;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

@CapacitorPlugin(name = "ZPrinter")
public class ZPrinterPlugin extends Plugin {

    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothDevice connectedDevice;

    private BluetoothAdapter adapter;
    private BroadcastReceiver receiver;
    private JSArray devicesArray;

    // =========================
    // Scan paired + nearby Bluetooth devices
    // =========================
    @PluginMethod
    public void scanDevices(PluginCall call) {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            call.reject("Bluetooth not supported on this device");
            return;
        }
        if (!adapter.isEnabled()) {
            call.reject("Bluetooth is disabled");
            return;
        }

        // Runtime permissions for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                call.reject("BLUETOOTH_SCAN & BLUETOOTH_CONNECT permission required at runtime");
                return;
            }
        }

        devicesArray = new JSArray();

        // Add paired devices first
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            JSObject d = new JSObject();
            d.put("name", device.getName());
            d.put("address", device.getAddress());
            devicesArray.put(d);
        }

        // Setup BroadcastReceiver for discovery
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
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    unregisterReceiver();
                    JSObject ret = new JSObject();
                    ret.put("devices", devicesArray);
                    call.resolve(ret);
                }
            }
        };

        // Register receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getContext().registerReceiver(receiver, filter);

        // Start discovery
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        adapter.startDiscovery();
    }

    private void unregisterReceiver() {
        try {
            if (receiver != null) {
                getContext().unregisterReceiver(receiver);
                receiver = null;
            }
        } catch (Exception e) {
            // ignore
        }
    }

    // =========================
    // Connect to a Bluetooth printer
    // =========================
    @PluginMethod
    public void connect(PluginCall call) {
        String address = call.getString("address");
        if (address == null || address.isEmpty()) {
            call.reject("Address is required");
            return;
        }

        try {
            BluetoothDevice device = adapter.getRemoteDevice(address);

            socket = device.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            );
            socket.connect();
            outputStream = socket.getOutputStream();
            connectedDevice = device;

            JSObject ret = new JSObject();
            ret.put("connected", true);
            ret.put("deviceName", device.getName());
            ret.put("deviceAddress", device.getAddress());
            call.resolve(ret);

        } catch (Exception ex) {
            call.reject("Connect failed: " + ex.getMessage());
        }
    }

    // =========================
    // Print Text
    // =========================
    @PluginMethod
    public void printText(PluginCall call) {
        if (outputStream == null) {
            call.reject("No printer connected");
            return;
        }

        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is empty");
            return;
        }

        try {
            outputStream.write(text.getBytes());
            call.resolve();
        } catch (Exception ex) {
            call.reject("Print failed: " + ex.getMessage());
        }
    }

    // =========================
    // Cut Paper
    // =========================
    @PluginMethod
    public void cut(PluginCall call) {
        if (outputStream == null) {
            call.reject("No printer connected");
            return;
        }

        try {
            outputStream.write(new byte[]{0x1D, 0x56, 0x00}); // ESC/POS cut command
            call.resolve();
        } catch (Exception ex) {
            call.reject("Cut failed: " + ex.getMessage());
        }
    }

    // =========================
    // Disconnect printer
    // =========================
    @PluginMethod
    public void disconnect(PluginCall call) {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
                outputStream = null;
                connectedDevice = null;
            }
            call.resolve();
        } catch (Exception ex) {
            call.reject("Disconnect failed: " + ex.getMessage());
        }
    }
}
