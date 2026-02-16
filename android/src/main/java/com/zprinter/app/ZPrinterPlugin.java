package com.zprinter.app;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;

import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

@CapacitorPlugin(name = "ZPrinter")
public class ZPrinterPlugin extends Plugin {

    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothDevice connectedDevice;

    // =========================
    // Scan paired Bluetooth devices
    // =========================
    @PluginMethod
    public void scanDevices(PluginCall call) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            call.reject("Bluetooth not supported on this device");
            return;
        }
        if (!adapter.isEnabled()) {
            call.reject("Bluetooth is disabled");
            return;
        }

        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
        JSArray devicesArray = new JSArray();

        for (BluetoothDevice device : pairedDevices) {
            JSObject d = new JSObject();
            d.put("name", device.getName());
            d.put("address", device.getAddress());
            devicesArray.put(d);
        }

        JSObject ret = new JSObject();
        ret.put("devices", devicesArray);
        call.resolve(ret);
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
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
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
