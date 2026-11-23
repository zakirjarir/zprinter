package com.zprinter.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Base64;
import com.getcapacitor.JSArray;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.json.JSONException;

public final class PrinterPayloadFormatter {

    private PrinterPayloadFormatter() {}

    public static byte[] formatText(String text, int fontSize, String align, boolean isBold, int feedLines) {
        return formatText(text, fontSize, align, isBold, false, feedLines, "UTF-8", null, true);
    }

    public static byte[] formatText(
        String text,
        int fontSize,
        String align,
        boolean isBold,
        boolean isUnderline,
        int feedLines,
        String encoding,
        Integer codePage,
        boolean reset
    ) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        if (reset) {
            output.write(0x1B);
            output.write(0x40);
        }

        if (codePage != null && codePage >= 0 && codePage <= 255) {
            output.write(0x1B);
            output.write(0x74);
            output.write(codePage & 0xFF);
        }

        output.write(0x1B);
        output.write(0x61);
        output.write(getAlignmentValue(align));

        output.write(0x1B);
        output.write(0x45);
        output.write(isBold ? 0x01 : 0x00);

        output.write(0x1B);
        output.write(0x2D);
        output.write(isUnderline ? 0x01 : 0x00);

        output.write(0x1D);
        output.write(0x21);
        output.write(getSizeValue(fontSize));

        Charset charset = resolveCharset(encoding);
        byte[] textBytes = text.getBytes(charset);
        output.write(textBytes, 0, textBytes.length);

        int normalizedFeedLines = Math.max(feedLines, 0);
        for (int i = 0; i < normalizedFeedLines; i++) {
            output.write(0x0A);
        }

        // Reset text attributes back to defaults
        output.write(0x1B);
        output.write(0x45);
        output.write(0x00);

        output.write(0x1B);
        output.write(0x2D);
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
        return formatQRCode(data, size, align, "L", 1);
    }

    public static byte[] formatQRCode(String data, int size, String align, String errorCorrection, int feedLines) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(0x1B);
        output.write(0x61);
        output.write(getAlignmentValue(align));

        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        int storeLen = dataBytes.length + 3;
        byte storePL = (byte) (storeLen % 256);
        byte storePH = (byte) (storeLen / 256);

        // Select QR model 2
        output.write(new byte[] { 0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00 }, 0, 9);
        // Set module size (1-16)
        output.write(new byte[] { 0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, (byte) Math.min(Math.max(size, 1), 16) }, 0, 8);
        // Set error correction level
        output.write(new byte[] { 0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, getQrErrorCorrectionByte(errorCorrection) }, 0, 8);
        // Store UTF-8 payload using exact byte length
        output.write(new byte[] { 0x1D, 0x28, 0x6B, storePL, storePH, 0x31, 0x50, 0x30 }, 0, 8);
        output.write(dataBytes, 0, dataBytes.length);
        // Print QR symbol
        output.write(new byte[] { 0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30 }, 0, 8);

        int normalizedFeedLines = Math.max(feedLines, 1);
        for (int i = 0; i < normalizedFeedLines; i++) {
            output.write(0x0A);
        }

        output.write(0x1B);
        output.write(0x61);
        output.write(0x00);

        return output.toByteArray();
    }

    public static byte[] formatBarcode(String data, String type, int height, int width, String align, String hriPosition, int feedLines) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(0x1B);
        output.write(0x61);
        output.write(getAlignmentValue(align));

        int clampedHeight = Math.min(Math.max(height, 1), 255);
        int clampedWidth = Math.min(Math.max(width, 2), 6);

        // GS h n - Barcode height
        output.write(0x1D);
        output.write(0x68);
        output.write(clampedHeight);

        // GS w n - Barcode module width
        output.write(0x1D);
        output.write(0x77);
        output.write(clampedWidth);

        // GS H n - HRI text position
        output.write(0x1D);
        output.write(0x48);
        output.write(getHriPositionValue(hriPosition));

        String normalizedType = type != null ? type.toUpperCase(Locale.ROOT) : "CODE128";
        int barcodeCode = getBarcodeTypeCode(normalizedType);
        String payloadData = data;
        if ("CODE128".equals(normalizedType) && !data.startsWith("{A") && !data.startsWith("{B") && !data.startsWith("{C")) {
            payloadData = "{B" + data;
        }

        byte[] dataBytes = payloadData.getBytes(StandardCharsets.US_ASCII);
        int length = Math.min(dataBytes.length, 255);

        // GS k m n d1...dn (Function B)
        output.write(0x1D);
        output.write(0x6B);
        output.write(barcodeCode);
        output.write(length);
        output.write(dataBytes, 0, length);

        int normalizedFeedLines = Math.max(feedLines, 1);
        for (int i = 0; i < normalizedFeedLines; i++) {
            output.write(0x0A);
        }

        output.write(0x1B);
        output.write(0x61);
        output.write(0x00);

        return output.toByteArray();
    }

    public static byte[] formatImage(String base64, int width, int height, String align) {
        return formatImage(base64, width, height, align, 128, "raster", 1);
    }

    public static byte[] formatImage(String base64, int width, int height, String align, int threshold, String mode, int feedLines) {
        String cleanBase64 = stripDataUriPrefix(base64);
        byte[] decoded = Base64.decode(cleanBase64, Base64.DEFAULT);
        Bitmap source = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
        if (source == null) {
            throw new IllegalArgumentException("Invalid base64 image data");
        }
        return formatBitmap(source, width, height, align, threshold, mode, feedLines);
    }

    public static byte[] formatBitmap(Bitmap source, int width, int height, String align, int threshold, String mode, int feedLines) {
        if (source == null) {
            throw new IllegalArgumentException("Bitmap is required");
        }

        int targetWidth = source.getWidth();
        int targetHeight = source.getHeight();

        if (width > 0 && height > 0) {
            targetWidth = width;
            targetHeight = height;
        } else if (width > 0) {
            targetWidth = width;
            targetHeight = Math.max(1, (int) Math.round(((double) source.getHeight() * width) / source.getWidth()));
        } else if (height > 0) {
            targetHeight = height;
            targetWidth = Math.max(1, (int) Math.round(((double) source.getWidth() * height) / source.getHeight()));
        }

        Bitmap scaled = (targetWidth != source.getWidth() || targetHeight != source.getHeight())
            ? Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
            : source;

        // Composite onto white background so transparent PNGs do not print solid black
        Bitmap flattened = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(flattened);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(scaled, 0, 0, null);

        if (scaled != source) {
            scaled.recycle();
        }
        source.recycle();

        int clampedThreshold = Math.min(Math.max(threshold, 0), 255);
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(0x1B);
        output.write(0x61);
        output.write(getAlignmentValue(align));

        if ("bitImage".equalsIgnoreCase(mode)) {
            writeBitImageMode(output, flattened, clampedThreshold);
        } else {
            writeRasterMode(output, flattened, clampedThreshold);
        }

        flattened.recycle();

        int normalizedFeedLines = Math.max(feedLines, 0);
        for (int i = 0; i < normalizedFeedLines; i++) {
            output.write(0x0A);
        }

        output.write(0x1B);
        output.write(0x61);
        output.write(0x00);

        return output.toByteArray();
    }

    private static void writeRasterMode(ByteArrayOutputStream output, Bitmap bitmap, int threshold) {
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        int bytesPerRow = (bw + 7) / 8;
        int xL = bytesPerRow % 256;
        int xH = bytesPerRow / 256;
        int yL = bh % 256;
        int yH = bh / 256;

        output.write(new byte[] { 0x1D, 0x76, 0x30, 0x00, (byte) xL, (byte) xH, (byte) yL, (byte) yH }, 0, 8);

        int[] rowPixels = new int[bw];
        for (int y = 0; y < bh; y++) {
            bitmap.getPixels(rowPixels, 0, bw, 0, y, bw, 1);
            for (int x = 0; x < bw; x += 8) {
                int b = 0;
                for (int bit = 0; bit < 8; bit++) {
                    if (x + bit < bw && isDarkPixel(rowPixels[x + bit], threshold)) {
                        b |= (0x80 >> bit);
                    }
                }
                output.write(b);
            }
        }
    }

    /**
     * ESC * 33 (24-dot double-density bit image) compatibility mode for legacy ESC/POS printers
     * that do not support GS v 0 raster graphics.
     */
    private static void writeBitImageMode(ByteArrayOutputStream output, Bitmap bitmap, int threshold) {
        int bw = bitmap.getWidth();
        int bh = bitmap.getHeight();
        int nL = bw % 256;
        int nH = bw / 256;
        int[] pixels = new int[bw * bh];
        bitmap.getPixels(pixels, 0, bw, 0, 0, bw, bh);

        // Set line spacing to 24 dots (ESC 3 24)
        output.write(0x1B);
        output.write(0x33);
        output.write(24);

        for (int y = 0; y < bh; y += 24) {
            output.write(0x1B);
            output.write(0x2A);
            output.write(33); // 24-dot double density
            output.write(nL);
            output.write(nH);

            for (int x = 0; x < bw; x++) {
                for (int k = 0; k < 3; k++) {
                    int slice = 0;
                    for (int bit = 0; bit < 8; bit++) {
                        int py = y + (k * 8) + bit;
                        if (py < bh && isDarkPixel(pixels[py * bw + x], threshold)) {
                            slice |= (0x80 >> bit);
                        }
                    }
                    output.write(slice);
                }
            }
            output.write(0x0A);
        }

        // Restore default line spacing (ESC 2)
        output.write(0x1B);
        output.write(0x32);
    }

    private static boolean isDarkPixel(int pixel, int threshold) {
        int red = (pixel >> 16) & 0xFF;
        int green = (pixel >> 8) & 0xFF;
        int blue = pixel & 0xFF;
        int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
        return gray < threshold;
    }

    public static byte[] decodeRawData(String base64, JSArray bytes, String text) throws JSONException {
        if (base64 != null && !base64.isEmpty()) {
            return Base64.decode(stripDataUriPrefix(base64), Base64.DEFAULT);
        }

        if (bytes != null && bytes.length() > 0) {
            byte[] raw = new byte[bytes.length()];
            for (int i = 0; i < bytes.length(); i++) {
                raw[i] = (byte) (bytes.getInt(i) & 0xFF);
            }
            return raw;
        }

        if (text != null && !text.isEmpty()) {
            return text.getBytes(StandardCharsets.UTF_8);
        }

        throw new IllegalArgumentException("Raw data requires base64, bytes, or text");
    }

    public static byte[] kickDrawer() {
        return kickDrawer(0, 25, 250);
    }

    public static byte[] kickDrawer(int pin, int onTime, int offTime) {
        byte pinByte = (byte) (pin == 1 ? 0x01 : 0x00);
        byte t1 = (byte) (Math.min(Math.max(onTime, 0), 255) & 0xFF);
        byte t2 = (byte) (Math.min(Math.max(offTime, 0), 255) & 0xFF);
        return new byte[] { 0x1B, 0x70, pinByte, t1, t2 };
    }

    public static byte[] cut() {
        return cut(false, 3);
    }

    public static byte[] cut(boolean partial, int feedLines) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int normalizedFeed = Math.max(feedLines, 0);
        for (int i = 0; i < normalizedFeed; i++) {
            output.write(0x0A);
        }
        output.write(0x1D);
        output.write(0x56);
        output.write(partial ? 0x01 : 0x00);
        return output.toByteArray();
    }

    private static String stripDataUriPrefix(String base64) {
        String trimmed = base64.trim();
        int commaIndex = trimmed.indexOf(',');
        if (trimmed.startsWith("data:") && commaIndex >= 0) {
            return trimmed.substring(commaIndex + 1).trim();
        }
        return trimmed;
    }

    private static Charset resolveCharset(String encoding) {
        if (encoding == null || encoding.trim().isEmpty()) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName(encoding.trim());
        } catch (Exception ignored) {
            return StandardCharsets.UTF_8;
        }
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

    private static byte getQrErrorCorrectionByte(String level) {
        if ("M".equalsIgnoreCase(level)) {
            return 0x31;
        }
        if ("Q".equalsIgnoreCase(level)) {
            return 0x32;
        }
        if ("H".equalsIgnoreCase(level)) {
            return 0x33;
        }
        return 0x30; // L
    }

    private static int getHriPositionValue(String position) {
        if ("none".equalsIgnoreCase(position)) {
            return 0x00;
        }
        if ("above".equalsIgnoreCase(position)) {
            return 0x01;
        }
        if ("both".equalsIgnoreCase(position)) {
            return 0x03;
        }
        return 0x02; // below
    }

    private static int getBarcodeTypeCode(String type) {
        switch (type) {
            case "UPCA":
                return 65;
            case "UPCE":
                return 66;
            case "EAN13":
                return 67;
            case "EAN8":
                return 68;
            case "CODE39":
                return 69;
            case "ITF":
                return 70;
            case "CODABAR":
                return 71;
            case "CODE128":
            default:
                return 73;
        }
    }
}
