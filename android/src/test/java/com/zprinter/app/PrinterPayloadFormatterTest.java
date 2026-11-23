package com.zprinter.app;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import org.junit.Test;

public class PrinterPayloadFormatterTest {

    @Test
    public void formatTextIncludesResetAlignBoldAndText() {
        byte[] payload = PrinterPayloadFormatter.formatText("Hello POS", 32, "center", true, true, 2, "UTF-8", 0, true);
        assertTrue(payload.length > 15);
        // Starts with ESC @
        assertEquals(0x1B, payload[0] & 0xFF);
        assertEquals(0x40, payload[1] & 0xFF);
        // Codepage ESC t 0
        assertEquals(0x1B, payload[2] & 0xFF);
        assertEquals(0x74, payload[3] & 0xFF);
        assertEquals(0x00, payload[4] & 0xFF);
        // Center align ESC a 1
        assertEquals(0x1B, payload[5] & 0xFF);
        assertEquals(0x61, payload[6] & 0xFF);
        assertEquals(0x01, payload[7] & 0xFF);
    }

    @Test
    public void formatQRCodeUsesUtf8ByteLength() {
        String bangla = "বাংলা";
        byte[] utf8Bytes = bangla.getBytes(StandardCharsets.UTF_8);
        assertTrue("UTF-8 byte length should exceed character count", utf8Bytes.length > bangla.length());

        byte[] payload = PrinterPayloadFormatter.formatQRCode(bangla, 8, "center", "M", 1);
        int expectedStoreLen = utf8Bytes.length + 3;
        byte expectedPL = (byte) (expectedStoreLen % 256);
        byte expectedPH = (byte) (expectedStoreLen / 256);

        // Alignment (3) + Model (9) + Size (8) + EC (8) = 28 bytes before Store command
        assertEquals(0x1D, payload[28] & 0xFF);
        assertEquals(0x28, payload[29] & 0xFF);
        assertEquals(0x6B, payload[30] & 0xFF);
        assertEquals(expectedPL, payload[31]);
        assertEquals(expectedPH, payload[32]);
    }

    @Test
    public void formatBarcodeGeneratesCode128Payload() {
        byte[] payload = PrinterPayloadFormatter.formatBarcode("123456", "CODE128", 80, 2, "center", "below", 1);
        assertTrue(payload.length > 18);
        // Verify GS k 73 (CODE128) is present
        boolean foundCode128 = false;
        for (int i = 0; i < payload.length - 3; i++) {
            if ((payload[i] & 0xFF) == 0x1D && (payload[i + 1] & 0xFF) == 0x6B && (payload[i + 2] & 0xFF) == 73) {
                foundCode128 = true;
                // "{B123456" has 8 bytes
                assertEquals(8, payload[i + 3] & 0xFF);
                break;
            }
        }
        assertTrue("Expected GS k CODE128 header", foundCode128);
    }

    @Test
    public void cutAndKickDrawerGenerateExpectedCommands() {
        byte[] partialCut = PrinterPayloadFormatter.cut(true, 3);
        assertEquals(6, partialCut.length);
        assertEquals(0x0A, partialCut[0]);
        assertEquals(0x0A, partialCut[1]);
        assertEquals(0x0A, partialCut[2]);
        assertEquals(0x1D, partialCut[3] & 0xFF);
        assertEquals(0x56, partialCut[4] & 0xFF);
        assertEquals(0x01, partialCut[5] & 0xFF);

        byte[] drawer = PrinterPayloadFormatter.kickDrawer(1, 30, 200);
        assertArrayEquals(new byte[] { 0x1B, 0x70, 0x01, 30, (byte) 200 }, drawer);
    }

    @Test
    public void htmlDocumentHelpersNormalizeFragmentsAndSanitizePdfNames() {
        String wrapped = HtmlDocumentEngine.normalizeHtml("<h1>বাংলা রিসিট</h1>");
        assertTrue(wrapped.contains("<!DOCTYPE html>"));
        assertTrue(wrapped.contains("<h1>বাংলা রিসিট</h1>"));

        String fullDoc = "<html><body>OK</body></html>";
        assertEquals(fullDoc, HtmlDocumentEngine.normalizeHtml(fullDoc));

        assertEquals("invoice_101.pdf", HtmlDocumentEngine.sanitizePdfFileName("invoice 101"));
        assertEquals("receipt-2026.pdf", HtmlDocumentEngine.sanitizePdfFileName("receipt-2026.pdf"));
    }
}
