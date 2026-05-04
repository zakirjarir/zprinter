package com.zprinter.app.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkPrinterManager {
    private Socket socket;
    private OutputStream outputStream;

    public void connect(String address, int port) throws IOException {
        close();
        socket = new Socket();
        socket.connect(new InetSocketAddress(address, port), 5000); // 5s timeout
        outputStream = socket.getOutputStream();
    }

    public void print(byte[] bytes) throws IOException {
        if (outputStream == null) {
            throw new IOException("Network printer not connected");
        }
        outputStream.write(bytes);
        outputStream.flush();
    }

    public void close() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            // Ignore
        } finally {
            outputStream = null;
            socket = null;
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
