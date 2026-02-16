package com.zprinter.app.bluetooth;

import java.io.OutputStream;

public class BluetoothPrinter {

    public void print(OutputStream stream, String text) throws Exception {
        if (stream == null) throw new Exception("Not connected");
        stream.write(text.getBytes());
    }

    public void cut(OutputStream stream) throws Exception {
        if (stream == null) throw new Exception("Not connected");
        stream.write(new byte[]{0x1D, 0x56, 0x00});
    }
}
