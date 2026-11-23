package com.zprinter.app.bluetooth;

import java.io.OutputStream;

public class BluetoothPrinter {

    private static final int CHUNK_SIZE = 1024;
    private static final int CHUNK_DELAY_MS = 10;

    public void print(OutputStream stream, byte[] bytes) throws Exception {
        if (stream == null) {
            throw new Exception("Bluetooth printer is not connected");
        }

        if (bytes == null || bytes.length == 0) {
            return;
        }

        int offset = 0;
        while (offset < bytes.length) {
            int length = Math.min(CHUNK_SIZE, bytes.length - offset);
            stream.write(bytes, offset, length);
            stream.flush();
            offset += length;

            if (offset < bytes.length) {
                try {
                    Thread.sleep(CHUNK_DELAY_MS);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void cut(OutputStream stream, byte[] bytes) throws Exception {
        print(stream, bytes);
    }
}
