package com.zprinter.app;

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
