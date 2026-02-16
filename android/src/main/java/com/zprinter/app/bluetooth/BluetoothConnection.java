package com.zprinter.app.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.OutputStream;
import java.util.UUID;

public class BluetoothConnection {

    private BluetoothSocket socket;
    private OutputStream outputStream;

    public OutputStream connect(String address) throws Exception {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = adapter.getRemoteDevice(address);

        socket = device.createRfcommSocketToServiceRecord(
                UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        );

        socket.connect();
        outputStream = socket.getOutputStream();
        return outputStream;
    }

    public void disconnect() throws Exception {
        if (socket != null) {
            socket.close();
            socket = null;
            outputStream = null;
        }
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
