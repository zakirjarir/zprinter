package com.zprinter.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public final class PrinterPayloadFormatter {

    private PrinterPayloadFormatter() {}

    public static byte[] formatText(String text, int fontSize, String align, boolean isBold, int feedLines) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(0x1B);
        output.write(0x40);

        output.write(0x1B);
        output.write(0x61);
        output.write(getAlignmentValue(align));

        output.write(0x1B);
        output.write(0x45);
        output.write(isBold ? 0x01 : 0x00);

        output.write(0x1D);
        output.write(0x21);
        output.write(getSizeValue(fontSize));

        byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);
        output.write(textBytes, 0, textBytes.length);

        int normalizedFeedLines = Math.max(feedLines, 1);
        for (int i = 0; i < normalizedFeedLines; i++) {
            output.write(0x0A);
        }

        output.write(0x1B);
        output.write(0x45);
        output.write(0x00);

        output.write(0x1D);
        output.write(0x21);
        output.write(0x00);

        output.write(0x1B);
        output.write(0x61);
        output.write(0x00);

        return output.toByteArray();
    }

    public static byte[] formatQRCode(String data, int size, String align) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        // Alignment
        output.write(0x1B);
        output.write(0x61);
        output.write(getAlignmentValue(align));

        // QR Code Setup
        int store_len = data.length() + 3;
        byte store_pL = (byte) (store_len % 256);
        byte store_pH = (byte) (store_len / 256);

        // Model
        output.write(new byte[]{0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00}, 0, 9);
        // Size
        output.write(new byte[]{0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, (byte) Math.min(Math.max(size, 1), 16)}, 0, 8);
        // Error Correction (Level L)
        output.write(new byte[]{0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30}, 0, 8);
        // Store data
        output.write(new byte[]{0x1D, 0x28, 0x6B, store_pL, store_pH, 0x31, 0x50, 0x30}, 0, 8);
        output.write(data.getBytes(StandardCharsets.UTF_8), 0, data.length());
        // Print
        output.write(new byte[]{0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30}, 0, 8);

        // Reset alignment and feed
        output.write(0x0A);
        output.write(0x1B);
        output.write(0x61);
        output.write(0x00);

        return output.toByteArray();
    }

    public static byte[] formatImage(String base64, int width, int height, String align) {
        try {
            byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            if (bitmap == null) return new byte[0];

            // Resize if needed
            if (width > 0 && height > 0) {
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            
            // Alignment
            output.write(0x1B);
            output.write(0x61);
            output.write(getAlignmentValue(align));

            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            int xL = (bw / 8) % 256;
            int xH = (bw / 8) / 256;
            int yL = bh % 256;
            int yH = bh / 256;

            output.write(new byte[]{0x1D, 0x76, 0x30, 0x00, (byte) xL, (byte) xH, (byte) yL, (byte) yH}, 0, 8);

            for (int y = 0; y < bh; y++) {
                for (int x = 0; x < bw; x += 8) {
                    int b = 0;
                    for (int bit = 0; bit < 8; bit++) {
                        if (x + bit < bw) {
                            int pixel = bitmap.getPixel(x + bit, y);
                            int red = (pixel >> 16) & 0xff;
                            int green = (pixel >> 8) & 0xff;
                            int blue = pixel & 0xff;
                            int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
                            if (gray < 128) {
                                b |= (0x80 >> bit);
                            }
                        }
                    }
                    output.write(b);
                }
            }

            output.write(0x0A);
            output.write(0x1B);
            output.write(0x61);
            output.write(0x00);

            return output.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static byte[] kickDrawer() {
        return new byte[]{0x1B, 0x70, 0x00, 0x19, (byte) 0xFA};
    }

    public static byte[] cut() {
        return new byte[] { 0x1D, 0x56, 0x00 };
    }

    private static int getAlignmentValue(String align) {
        if ("center".equalsIgnoreCase(align)) {
            return 0x01;
        }

        if ("right".equalsIgnoreCase(align)) {
            return 0x02;
        }

        return 0x00;
    }

    private static int getSizeValue(int fontSize) {
        if (fontSize >= 48) {
            return 0x22;
        }

        if (fontSize >= 32) {
            return 0x11;
        }

        return 0x00;
    }
}
