package com.zprinter.app.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothConnection {

    private static final String TAG = "BluetoothConnection";
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothSocket socket;
    private OutputStream outputStream;
    private String connectedDeviceName = "";
    private String connectedDeviceAddress = "";

    @SuppressLint("MissingPermission")
    public synchronized OutputStream connect(Context context, String address) throws Exception {
        disconnectQuietly();

        BluetoothManager bluetoothManager =
            context != null ? (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE) : null;
        BluetoothAdapter adapter = bluetoothManager != null ? bluetoothManager.getAdapter() : null;
        if (adapter == null) {
            throw new Exception("Bluetooth is not supported");
        }

        if (!adapter.isEnabled()) {
            throw new Exception("Bluetooth is turned off");
        }

        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }

        BluetoothDevice device = adapter.getRemoteDevice(address);
        BluetoothSocket connectedSocket = connectWithFallbacks(device);

        this.socket = connectedSocket;
        this.outputStream = connectedSocket.getOutputStream();
        this.connectedDeviceAddress = address;
        try {
            String name = device.getName();
            this.connectedDeviceName = (name != null && !name.isEmpty()) ? name : "Bluetooth Printer";
        } catch (SecurityException ignored) {
            this.connectedDeviceName = "Bluetooth Printer";
        }

        return outputStream;
    }

    /**
     * 3-tier fallback connection strategy to support both modern and legacy Bluetooth SPP printers:
     * 1. Standard secure RFCOMM SPP socket
     * 2. Insecure RFCOMM SPP socket (common for older BT 2.0/2.1 POS printers)
     * 3. Direct RFCOMM channel 1 via reflection (fallback for stubborn legacy printers)
     */
    @SuppressLint("MissingPermission")
    private BluetoothSocket connectWithFallbacks(BluetoothDevice device) throws Exception {
        Exception lastError;

        // Tier 1: Standard secure SPP socket
        BluetoothSocket candidate = null;
        try {
            candidate = device.createRfcommSocketToServiceRecord(SPP_UUID);
            candidate.connect();
            return candidate;
        } catch (Exception firstError) {
            closeSocketQuietly(candidate);
            lastError = firstError;
            Log.w(TAG, "Standard SPP connect failed, trying insecure RFCOMM", firstError);
        }

        // Tier 2: Insecure SPP socket
        candidate = null;
        try {
            candidate = device.createInsecureRfcommSocketToServiceRecord(SPP_UUID);
            candidate.connect();
            return candidate;
        } catch (Exception secondError) {
            closeSocketQuietly(candidate);
            lastError = secondError;
            Log.w(TAG, "Insecure SPP connect failed, trying legacy channel 1 fallback", secondError);
        }

        // Tier 3: Legacy reflection channel 1 fallback
        candidate = null;
        try {
            Method method = device.getClass().getMethod("createRfcommSocket", int.class);
            candidate = (BluetoothSocket) method.invoke(device, 1);
            if (candidate != null) {
                candidate.connect();
                return candidate;
            }
        } catch (Exception thirdError) {
            closeSocketQuietly(candidate);
            lastError = thirdError;
            Log.e(TAG, "Legacy channel 1 RFCOMM connect failed", thirdError);
        }

        throw lastError;
    }

    private void closeSocketQuietly(BluetoothSocket candidate) {
        if (candidate != null) {
            try {
                candidate.close();
            } catch (Exception ignored) {}
        }
    }

    public synchronized void disconnect() throws IOException {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } finally {
                outputStream = null;
                socket = null;
                connectedDeviceName = "";
                connectedDeviceAddress = "";
            }
        }
    }

    public synchronized void disconnectQuietly() {
        try {
            disconnect();
        } catch (Exception ignored) {}
    }

    public synchronized OutputStream getOutputStream() {
        return outputStream;
    }

    public synchronized boolean isConnected() {
        return socket != null && socket.isConnected() && outputStream != null;
    }

    public synchronized String getConnectedDeviceName() {
        return connectedDeviceName;
    }

    public synchronized String getConnectedDeviceAddress() {
        return connectedDeviceAddress;
    }
}
