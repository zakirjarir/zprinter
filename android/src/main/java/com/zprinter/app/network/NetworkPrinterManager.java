package com.zprinter.app.network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetworkPrinterManager {

    private static final int DEFAULT_TIMEOUT_MS = 5000;
    private static final int CHUNK_SIZE = 4096;

    private Socket socket;
    private OutputStream outputStream;
    private String connectedAddress = "";
    private int connectedPort = 0;

    public synchronized void connect(String address, int port) throws IOException {
        connect(address, port, DEFAULT_TIMEOUT_MS);
    }

    public synchronized void connect(String address, int port, int timeoutMs) throws IOException {
        close();
        int effectiveTimeout = timeoutMs > 0 ? timeoutMs : DEFAULT_TIMEOUT_MS;
        Socket candidate = new Socket();
        try {
            candidate.setTcpNoDelay(true);
            candidate.connect(new InetSocketAddress(address, port), effectiveTimeout);
            socket = candidate;
            outputStream = candidate.getOutputStream();
            connectedAddress = address;
            connectedPort = port;
        } catch (IOException error) {
            try {
                candidate.close();
            } catch (IOException ignored) {}
            throw error;
        }
    }

    public synchronized void print(byte[] bytes) throws IOException {
        if (outputStream == null || !isConnected()) {
            throw new IOException("Network printer not connected");
        }

        if (bytes == null || bytes.length == 0) {
            return;
        }

        int offset = 0;
        while (offset < bytes.length) {
            int length = Math.min(CHUNK_SIZE, bytes.length - offset);
            outputStream.write(bytes, offset, length);
            outputStream.flush();
            offset += length;
        }
    }

    public synchronized void close() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException ignored) {}

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {}

        outputStream = null;
        socket = null;
        connectedAddress = "";
        connectedPort = 0;
    }

    public synchronized boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed() && outputStream != null;
    }

    public synchronized String getConnectedAddress() {
        return connectedAddress;
    }

    public synchronized int getConnectedPort() {
        return connectedPort;
    }
}
