package com.zprinter.app.bluetooth;

import java.io.OutputStream;

public class BluetoothPrinter {

    public void print(OutputStream stream, byte[] bytes) throws Exception {
        if (stream == null) {
            throw new Exception("Bluetooth printer is not connected");
        }

        stream.write(bytes);
        stream.flush();
    }

    public void cut(OutputStream stream, byte[] bytes) throws Exception {
        print(stream, bytes);
    }
}
