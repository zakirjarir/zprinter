package com.zprinter.app;

import static org.junit.Assert.*;

import com.zprinter.app.bluetooth.BluetoothPrinter;
import com.zprinter.app.network.NetworkPrinterManager;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class MockPrinterSimulationTest {

    @Test
    public void networkPrinterSendsExactEscPosReceiptAndRecoversAfterPrinterDropsConnection() throws Exception {
        CopyOnWriteArrayList<byte[]> receivedJobs = new CopyOnWriteArrayList<>();
        CountDownLatch firstJobLatch = new CountDownLatch(1);
        CountDownLatch secondJobLatch = new CountDownLatch(1);

        try (ServerSocket mockPrinterServer = new ServerSocket(0)) {
            int port = mockPrinterServer.getLocalPort();

            Thread serverThread = new Thread(() -> {
                try {
                    // Connection 1: receive first receipt, then immediately close socket (simulates printer idle drop)
                    try (Socket client1 = mockPrinterServer.accept()) {
                        InputStream in = client1.getInputStream();
                        ByteArrayOutputStream buf = new ByteArrayOutputStream();
                        byte[] tmp = new byte[1024];
                        client1.setSoTimeout(300);
                        try {
                            int n;
                            while ((n = in.read(tmp)) != -1) {
                                buf.write(tmp, 0, n);
                            }
                        } catch (Exception ignored) {
                            // Read timeout indicates end of first burst
                        }
                        receivedJobs.add(buf.toByteArray());
                        firstJobLatch.countDown();
                    }

                    // Connection 2: receive second receipt after reconnect
                    try (Socket client2 = mockPrinterServer.accept()) {
                        InputStream in = client2.getInputStream();
                        ByteArrayOutputStream buf = new ByteArrayOutputStream();
                        byte[] tmp = new byte[1024];
                        client2.setSoTimeout(300);
                        try {
                            int n;
                            while ((n = in.read(tmp)) != -1) {
                                buf.write(tmp, 0, n);
                            }
                        } catch (Exception ignored) {}
                        receivedJobs.add(buf.toByteArray());
                        secondJobLatch.countDown();
                    }
                } catch (Exception ignored) {}
            });
            serverThread.setDaemon(true);
            serverThread.start();

            NetworkPrinterManager manager = new NetworkPrinterManager();
            manager.connect("127.0.0.1", port, 3000);
            assertTrue(manager.isConnected());

            byte[] job1 = PrinterPayloadFormatter.formatText("Invoice #101", 32, "center", true, false, 2, "UTF-8", null, true);
            manager.print(job1);
            assertTrue("Mock printer should receive job 1", firstJobLatch.await(3, TimeUnit.SECONDS));
            assertArrayEquals(job1, receivedJobs.get(0));

            // Reconnect and send job 2 (QR + Barcode + Cut + Drawer)
            byte[] qr = PrinterPayloadFormatter.formatQRCode("https://zprinter.test/102", 6, "center", "M", 1);
            byte[] barcode = PrinterPayloadFormatter.formatBarcode("INV102", "CODE128", 80, 2, "center", "below", 1);
            byte[] cut = PrinterPayloadFormatter.cut(false, 3);
            byte[] drawer = PrinterPayloadFormatter.kickDrawer(0, 25, 250);

            ByteArrayOutputStream job2Out = new ByteArrayOutputStream();
            job2Out.write(qr);
            job2Out.write(barcode);
            job2Out.write(cut);
            job2Out.write(drawer);
            byte[] job2 = job2Out.toByteArray();

            manager.connect("127.0.0.1", port, 3000);
            manager.print(job2);
            assertTrue("Mock printer should receive job 2", secondJobLatch.await(3, TimeUnit.SECONDS));
            assertArrayEquals(job2, receivedJobs.get(1));

            manager.close();
            assertFalse(manager.isConnected());
        }
    }

    @Test
    public void bluetoothPrinterChunkedWritePreservesLargePayloadExactly() throws Exception {
        BluetoothPrinter printer = new BluetoothPrinter();
        byte[] largePayload = new byte[5432];
        for (int i = 0; i < largePayload.length; i++) {
            largePayload[i] = (byte) (i % 251);
        }

        ByteArrayOutputStream mockRfcommStream = new ByteArrayOutputStream();
        printer.print(mockRfcommStream, largePayload);

        assertArrayEquals(largePayload, mockRfcommStream.toByteArray());
    }

    @Test
    public void bluetoothPrinterRejectsWhenStreamIsNull() {
        BluetoothPrinter printer = new BluetoothPrinter();
        try {
            printer.print(null, new byte[] { 0x1B, 0x40 });
            fail("Expected exception when Bluetooth stream is null");
        } catch (Exception expected) {
            assertTrue(expected.getMessage().contains("not connected"));
        }
    }
}
