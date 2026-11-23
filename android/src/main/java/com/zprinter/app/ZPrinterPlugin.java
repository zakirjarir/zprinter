package com.zprinter.app;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.zprinter.app.bluetooth.BluetoothConnection;
import com.zprinter.app.bluetooth.BluetoothPrinter;
import com.zprinter.app.bluetooth.BluetoothScanner;
import com.zprinter.app.network.NetworkPrinterManager;
import com.zprinter.app.usb.ThermalPrinterManager;
import com.zprinter.app.usb.UsbPrinterManager;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@CapacitorPlugin(
    name = "ZPrinter",
    permissions = {
        @Permission(alias = "bluetooth", strings = { Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT }),
        @Permission(alias = "location", strings = { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION })
    }
)
public class ZPrinterPlugin extends Plugin {

    private static final String TAG = "ZPrinterPlugin";

    @FunctionalInterface
    private interface PayloadSupplier {
        byte[] get() throws Exception;
    }

    private BluetoothScanner bluetoothScanner;
    private BluetoothConnection bluetoothConnection;
    private BluetoothPrinter bluetoothPrinter;
    private UsbPrinterManager usbPrinterManager;
    private ThermalPrinterManager thermalPrinterManager;
    private NetworkPrinterManager networkPrinterManager;
    private PrinterStorage printerStorage;
    private HtmlDocumentEngine htmlDocumentEngine;

    private ExecutorService bluetoothExecutor;
    private ExecutorService usbExecutor;
    private ExecutorService thermalExecutor;
    private ExecutorService networkExecutor;

    @Override
    public void load() {
        bluetoothScanner = new BluetoothScanner();
        bluetoothConnection = new BluetoothConnection();
        bluetoothPrinter = new BluetoothPrinter();
        usbPrinterManager = new UsbPrinterManager();
        thermalPrinterManager = new ThermalPrinterManager();
        networkPrinterManager = new NetworkPrinterManager();
        printerStorage = new PrinterStorage(getContext());
        htmlDocumentEngine = new HtmlDocumentEngine();

        bluetoothExecutor = Executors.newSingleThreadExecutor();
        usbExecutor = Executors.newSingleThreadExecutor();
        thermalExecutor = Executors.newSingleThreadExecutor();
        networkExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void handleOnDestroy() {
        if (htmlDocumentEngine != null) {
            htmlDocumentEngine.destroy();
        }
        if (bluetoothScanner != null) {
            bluetoothScanner.stop();
        }
        if (bluetoothConnection != null) {
            bluetoothConnection.disconnectQuietly();
        }
        if (usbPrinterManager != null) {
            usbPrinterManager.close();
        }
        if (thermalPrinterManager != null) {
            thermalPrinterManager.close();
        }
        if (networkPrinterManager != null) {
            networkPrinterManager.close();
        }
        shutdownExecutor(bluetoothExecutor);
        shutdownExecutor(usbExecutor);
        shutdownExecutor(thermalExecutor);
        shutdownExecutor(networkExecutor);
        super.handleOnDestroy();
    }

    // =========================
    // SAVED PRINTER & RECONNECT METHODS
    // =========================

    @PluginMethod
    public void getSavedPrinters(PluginCall call) {
        call.resolve(printerStorage.getAllAsJSObject());
    }

    @PluginMethod
    public void clearSavedPrinter(PluginCall call) {
        String type = call.getString("type", "all");
        if (type == null || "all".equalsIgnoreCase(type)) {
            printerStorage.clearAll();
        } else if ("bluetooth".equalsIgnoreCase(type)) {
            printerStorage.clearBluetooth();
        } else if ("usb".equalsIgnoreCase(type)) {
            printerStorage.clearUsb(false);
        } else if ("thermal".equalsIgnoreCase(type)) {
            printerStorage.clearUsb(true);
        } else if ("network".equalsIgnoreCase(type)) {
            printerStorage.clearNetwork();
        }
        call.resolve();
    }

    // =========================
    // HTML TO IMAGE / PDF / NATIVE PRINT / THERMAL PRINT
    // =========================

    @PluginMethod
    public void htmlToImage(PluginCall call) {
        String html = call.getString("html", "");
        if (html == null || html.trim().isEmpty()) {
            call.reject("HTML content is required");
            return;
        }

        int width = call.getInt("width", 576);
        String format = call.getString("format", "png");
        int quality = call.getInt("quality", 90);
        String baseUrl = call.getString("baseUrl");
        int delayMs = call.getInt("delayMs", 150);

        htmlDocumentEngine.htmlToImage(
            getActivity(),
            html,
            width,
            format,
            quality,
            baseUrl,
            delayMs,
            new HtmlDocumentEngine.ImageResultCallback() {
                @Override
                public void onSuccess(String base64, String dataUrl, int w, int h, String mimeType) {
                    JSObject result = new JSObject();
                    result.put("base64", base64);
                    result.put("dataUrl", dataUrl);
                    result.put("width", w);
                    result.put("height", h);
                    result.put("mimeType", mimeType);
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject("htmlToImage failed: " + message);
                }
            }
        );
    }

    @PluginMethod
    public void htmlToPdf(PluginCall call) {
        String html = call.getString("html", "");
        if (html == null || html.trim().isEmpty()) {
            call.reject("HTML content is required");
            return;
        }

        String pageSize = call.getString("pageSize", "A4");
        String orientation = call.getString("orientation", "portrait");
        int customWidthPt = call.getInt("customWidthPt", 595);
        int customHeightPt = call.getInt("customHeightPt", 842);
        Integer marginPt = call.getInt("marginPt");
        String fileName = call.getString("fileName");
        boolean saveToFile = call.getBoolean("saveToFile", false);
        String baseUrl = call.getString("baseUrl");
        int delayMs = call.getInt("delayMs", 150);

        htmlDocumentEngine.htmlToPdf(
            getActivity(),
            html,
            pageSize,
            orientation,
            customWidthPt,
            customHeightPt,
            marginPt,
            fileName,
            saveToFile,
            baseUrl,
            delayMs,
            new HtmlDocumentEngine.PdfResultCallback() {
                @Override
                public void onSuccess(String base64, String dataUrl, int pageCount, String filePath) {
                    JSObject result = new JSObject();
                    result.put("base64", base64);
                    result.put("dataUrl", dataUrl);
                    result.put("pageCount", pageCount);
                    result.put("mimeType", "application/pdf");
                    if (filePath != null) {
                        result.put("filePath", filePath);
                    }
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject("htmlToPdf failed: " + message);
                }
            }
        );
    }

    @PluginMethod
    public void printHtmlNative(PluginCall call) {
        String html = call.getString("html", "");
        if (html == null || html.trim().isEmpty()) {
            call.reject("HTML content is required");
            return;
        }

        String jobName = call.getString("jobName", "ZPrinter Document");
        String pageSize = call.getString("pageSize", "A4");
        String orientation = call.getString("orientation", "portrait");
        String baseUrl = call.getString("baseUrl");

        htmlDocumentEngine.printHtmlNative(
            getActivity(),
            html,
            jobName,
            pageSize,
            orientation,
            baseUrl,
            new HtmlDocumentEngine.NativePrintCallback() {
                @Override
                public void onSuccess(boolean printed, String resolvedJobName) {
                    JSObject result = new JSObject();
                    result.put("printed", printed);
                    result.put("jobName", resolvedJobName);
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject("printHtmlNative failed: " + message);
                }
            }
        );
    }

    @PluginMethod
    public void printHtmlToThermal(PluginCall call) {
        String html = call.getString("html", "");
        String printerType = call.getString("printerType", "");
        if (html == null || html.trim().isEmpty()) {
            call.reject("HTML content is required");
            return;
        }
        if (printerType == null || printerType.trim().isEmpty()) {
            call.reject("printerType is required ('bluetooth', 'usb', 'thermal', or 'network')");
            return;
        }

        int width = call.getInt("width", 384);
        String align = call.getString("align", "center");
        int threshold = call.getInt("threshold", 128);
        String mode = call.getString("mode", "raster");
        int feedLines = call.getInt("feedLines", 2);
        boolean shouldCut = call.getBoolean("cut", false);
        boolean partialCut = call.getBoolean("partialCut", false);
        boolean shouldKickDrawer = call.getBoolean("kickDrawer", false);
        int drawerPin = call.getInt("drawerPin", 0);
        String baseUrl = call.getString("baseUrl");
        int delayMs = call.getInt("delayMs", 150);

        htmlDocumentEngine.renderHtmlToBitmap(
            getActivity(),
            html,
            width,
            baseUrl,
            delayMs,
            new HtmlDocumentEngine.BitmapCallback() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    PayloadSupplier supplier = () -> {
                        ByteArrayOutputStream combined = new ByteArrayOutputStream();
                        combined.write(PrinterPayloadFormatter.formatBitmap(bitmap, width, 0, align, threshold, mode, feedLines));
                        if (shouldCut) {
                            combined.write(PrinterPayloadFormatter.cut(partialCut, 3));
                        }
                        if (shouldKickDrawer) {
                            combined.write(PrinterPayloadFormatter.kickDrawer(drawerPin, 25, 250));
                        }
                        return combined.toByteArray();
                    };

                    if ("bluetooth".equalsIgnoreCase(printerType)) {
                        executeBluetoothWrite(call, supplier, "printed", "Bluetooth HTML print failed");
                    } else if ("usb".equalsIgnoreCase(printerType)) {
                        executeUsbWrite(call, usbPrinterManager, false, usbExecutor, supplier, "printed", "USB HTML print failed");
                    } else if ("thermal".equalsIgnoreCase(printerType)) {
                        executeUsbWrite(
                            call,
                            thermalPrinterManager,
                            true,
                            thermalExecutor,
                            supplier,
                            "printed",
                            "Thermal HTML print failed"
                        );
                    } else if ("network".equalsIgnoreCase(printerType)) {
                        executeNetworkWrite(call, supplier, "printed", "Network HTML print failed");
                    } else {
                        bitmap.recycle();
                        call.reject("Unsupported printerType: " + printerType);
                    }
                }

                @Override
                public void onError(String message) {
                    call.reject("printHtmlToThermal failed: " + message);
                }
            }
        );
    }

    @PluginMethod
    public void reconnectSavedPrinter(PluginCall call) {
        String type = call.getString("type", "");
        if (type == null || type.isEmpty()) {
            call.reject("Printer type is required ('bluetooth', 'usb', 'thermal', or 'network')");
            return;
        }

        if ("bluetooth".equalsIgnoreCase(type)) {
            String savedAddress = printerStorage.getBluetoothAddress();
            if (savedAddress == null || savedAddress.isEmpty()) {
                call.reject("No saved Bluetooth printer found");
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && getPermissionState("bluetooth") != PermissionState.GRANTED) {
                requestPermissionForAlias("bluetooth", call, "bluetoothReconnectPermissionCallback");
                return;
            }
            connectBluetoothInternal(call, savedAddress);
        } else if ("usb".equalsIgnoreCase(type)) {
            if (!printerStorage.hasSavedUsb(false)) {
                call.reject("No saved USB printer found");
                return;
            }
            connectUsbWithParams(
                call,
                usbPrinterManager,
                false,
                printerStorage.getUsbVendorId(false),
                printerStorage.getUsbProductId(false),
                printerStorage.getUsbDeviceName(false)
            );
        } else if ("thermal".equalsIgnoreCase(type)) {
            if (!printerStorage.hasSavedUsb(true)) {
                call.reject("No saved Thermal printer found");
                return;
            }
            connectUsbWithParams(
                call,
                thermalPrinterManager,
                true,
                printerStorage.getUsbVendorId(true),
                printerStorage.getUsbProductId(true),
                printerStorage.getUsbDeviceName(true)
            );
        } else if ("network".equalsIgnoreCase(type)) {
            String savedIp = printerStorage.getNetworkAddress();
            if (savedIp == null || savedIp.isEmpty()) {
                call.reject("No saved Network printer found");
                return;
            }
            connectNetworkInternal(call, savedIp, printerStorage.getNetworkPort(), printerStorage.getNetworkTimeout());
        } else {
            call.reject("Unsupported printer type: " + type);
        }
    }

    @PermissionCallback
    private void bluetoothReconnectPermissionCallback(PluginCall call) {
        if (getPermissionState("bluetooth") != PermissionState.GRANTED) {
            call.reject("Bluetooth connect permission denied");
            return;
        }
        String savedAddress = printerStorage.getBluetoothAddress();
        if (savedAddress == null || savedAddress.isEmpty()) {
            call.reject("No saved Bluetooth printer found");
            return;
        }
        connectBluetoothInternal(call, savedAddress);
    }

    // =========================
    // BLUETOOTH METHODS
    // =========================

    @PluginMethod
    public void scanBluetoothDevices(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (getPermissionState("bluetooth") != PermissionState.GRANTED) {
                requestPermissionForAlias("bluetooth", call, "bluetoothScanPermissionCallback");
                return;
            }
        } else {
            if (getPermissionState("location") != PermissionState.GRANTED) {
                requestPermissionForAlias("location", call, "bluetoothScanPermissionCallback");
                return;
            }
        }

        startBluetoothScan(call);
    }

    @PermissionCallback
    private void bluetoothScanPermissionCallback(PluginCall call) {
        String alias = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? "bluetooth" : "location";
        if (getPermissionState(alias) != PermissionState.GRANTED) {
            call.reject("Bluetooth permission denied");
            return;
        }

        startBluetoothScan(call);
    }

    @PluginMethod
    public void connectBluetooth(PluginCall call) {
        String address = call.getString("address", "");
        if (address.isEmpty()) {
            call.reject("Bluetooth address is required");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && getPermissionState("bluetooth") != PermissionState.GRANTED) {
            requestPermissionForAlias("bluetooth", call, "bluetoothConnectPermissionCallback");
            return;
        }

        connectBluetoothInternal(call, address);
    }

    @PermissionCallback
    private void bluetoothConnectPermissionCallback(PluginCall call) {
        if (getPermissionState("bluetooth") != PermissionState.GRANTED) {
            call.reject("Bluetooth connect permission denied");
            return;
        }

        String address = call.getString("address", "");
        if (address.isEmpty()) {
            call.reject("Bluetooth address is required");
            return;
        }

        connectBluetoothInternal(call, address);
    }

    private void connectBluetoothInternal(PluginCall call, String address) {
        bluetoothExecutor.execute(() -> {
            try {
                bluetoothConnection.connect(getContext(), address);
                printerStorage.saveBluetooth(bluetoothConnection.getConnectedDeviceAddress(), bluetoothConnection.getConnectedDeviceName());

                JSObject result = new JSObject();
                result.put("connected", true);
                result.put("deviceName", bluetoothConnection.getConnectedDeviceName());
                result.put("deviceAddress", bluetoothConnection.getConnectedDeviceAddress());
                call.resolve(result);
            } catch (Exception exception) {
                Log.e(TAG, "Bluetooth connect failed", exception);
                call.reject("Bluetooth connect failed: " + exception.getMessage());
            }
        });
    }

    @PluginMethod
    public void isBluetoothConnected(PluginCall call) {
        JSObject result = new JSObject();
        result.put("connected", bluetoothConnection.isConnected());
        if (bluetoothConnection.isConnected()) {
            result.put("deviceName", bluetoothConnection.getConnectedDeviceName());
            result.put("deviceAddress", bluetoothConnection.getConnectedDeviceAddress());
        }
        call.resolve(result);
    }

    @PluginMethod
    public void printBluetoothText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is required");
            return;
        }
        executeBluetoothWrite(call, () -> buildTextPayload(call, text), "printed", "Bluetooth print failed");
    }

    @PluginMethod
    public void printBluetoothImage(PluginCall call) {
        String base64 = call.getString("base64", "");
        if (base64.isEmpty()) {
            call.reject("Base64 image string is required");
            return;
        }
        executeBluetoothWrite(call, () -> buildImagePayload(call, base64), "printed", "Bluetooth image print failed");
    }

    @PluginMethod
    public void printBluetoothQRCode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("QR data is required");
            return;
        }
        executeBluetoothWrite(call, () -> buildQRCodePayload(call, data), "printed", "Bluetooth QR print failed");
    }

    @PluginMethod
    public void printBluetoothBarcode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("Barcode data is required");
            return;
        }
        executeBluetoothWrite(call, () -> buildBarcodePayload(call, data), "printed", "Bluetooth barcode print failed");
    }

    @PluginMethod
    public void sendRawBluetooth(PluginCall call) {
        executeBluetoothWrite(call, () -> buildRawPayload(call), "printed", "Bluetooth raw print failed");
    }

    @PluginMethod
    public void kickBluetoothDrawer(PluginCall call) {
        executeBluetoothWrite(call, () -> buildDrawerPayload(call), "kicked", "Bluetooth drawer kick failed");
    }

    @PluginMethod
    public void cutBluetoothPaper(PluginCall call) {
        executeBluetoothWrite(call, () -> buildCutPayload(call), "cut", "Bluetooth cut failed");
    }

    @PluginMethod
    public void disconnectBluetooth(PluginCall call) {
        boolean forget = call.getBoolean("forget", false);
        bluetoothExecutor.execute(() -> {
            try {
                bluetoothConnection.disconnect();
                if (forget) {
                    printerStorage.clearBluetooth();
                }
                call.resolve();
            } catch (Exception exception) {
                call.reject("Bluetooth disconnect failed: " + exception.getMessage());
            }
        });
    }

    /**
     * Automatically reconnects to the saved Bluetooth printer if disconnected, and retries once
     * if the printer dropped the idle RFCOMM socket (e.g. Broken pipe after a previous print).
     */
    private void executeBluetoothWrite(PluginCall call, PayloadSupplier supplier, String resolveKey, String errorPrefix) {
        bluetoothExecutor.execute(() -> {
            try {
                byte[] payload = supplier.get();
                String targetAddress = bluetoothConnection.isConnected()
                    ? bluetoothConnection.getConnectedDeviceAddress()
                    : printerStorage.getBluetoothAddress();

                if (!bluetoothConnection.isConnected()) {
                    if (targetAddress == null || targetAddress.isEmpty()) {
                        call.reject(errorPrefix + ": Bluetooth printer is not connected");
                        return;
                    }
                    Log.i(TAG, "Auto-reconnecting to saved Bluetooth printer: " + targetAddress);
                    bluetoothConnection.connect(getContext(), targetAddress);
                }

                try {
                    bluetoothPrinter.print(bluetoothConnection.getOutputStream(), payload);
                } catch (Exception writeError) {
                    if (targetAddress == null || targetAddress.isEmpty()) {
                        throw writeError;
                    }
                    Log.w(TAG, "Bluetooth write failed, attempting single auto-reconnect & retry", writeError);
                    bluetoothConnection.connect(getContext(), targetAddress);
                    bluetoothPrinter.print(bluetoothConnection.getOutputStream(), payload);
                }

                JSObject result = new JSObject();
                result.put(resolveKey, true);
                call.resolve(result);
            } catch (Exception exception) {
                Log.e(TAG, errorPrefix, exception);
                call.reject(errorPrefix + ": " + exception.getMessage());
            }
        });
    }

    // =========================
    // USB METHODS
    // =========================

    @PluginMethod
    public void listUsbPrinters(PluginCall call) {
        JSArray devices = usbPrinterManager.listPrinters(getContext());
        JSObject result = new JSObject();
        result.put("devices", devices);
        result.put("count", devices.length());
        call.resolve(result);
    }

    @PluginMethod
    public void connectUsbPrinter(PluginCall call) {
        connectUsbPrinterInternal(call, usbPrinterManager, false);
    }

    @PluginMethod
    public void isUsbConnected(PluginCall call) {
        JSObject result = new JSObject();
        result.put("connected", usbPrinterManager.isConnected());
        if (usbPrinterManager.isConnected()) {
            result.put("deviceName", usbPrinterManager.getConnectedDeviceName());
        }
        call.resolve(result);
    }

    @PluginMethod
    public void printUsbText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is required");
            return;
        }
        executeUsbWrite(call, usbPrinterManager, false, usbExecutor, () -> buildTextPayload(call, text), "printed", "USB print failed");
    }

    @PluginMethod
    public void printUsbImage(PluginCall call) {
        String base64 = call.getString("base64", "");
        if (base64.isEmpty()) {
            call.reject("Base64 image string is required");
            return;
        }
        executeUsbWrite(
            call,
            usbPrinterManager,
            false,
            usbExecutor,
            () -> buildImagePayload(call, base64),
            "printed",
            "USB image print failed"
        );
    }

    @PluginMethod
    public void printUsbQRCode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("QR data is required");
            return;
        }
        executeUsbWrite(
            call,
            usbPrinterManager,
            false,
            usbExecutor,
            () -> buildQRCodePayload(call, data),
            "printed",
            "USB QR print failed"
        );
    }

    @PluginMethod
    public void printUsbBarcode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("Barcode data is required");
            return;
        }
        executeUsbWrite(
            call,
            usbPrinterManager,
            false,
            usbExecutor,
            () -> buildBarcodePayload(call, data),
            "printed",
            "USB barcode print failed"
        );
    }

    @PluginMethod
    public void sendRawUsb(PluginCall call) {
        executeUsbWrite(call, usbPrinterManager, false, usbExecutor, () -> buildRawPayload(call), "printed", "USB raw print failed");
    }

    @PluginMethod
    public void kickUsbDrawer(PluginCall call) {
        executeUsbWrite(call, usbPrinterManager, false, usbExecutor, () -> buildDrawerPayload(call), "kicked", "USB drawer kick failed");
    }

    @PluginMethod
    public void cutUsbPaper(PluginCall call) {
        executeUsbWrite(call, usbPrinterManager, false, usbExecutor, () -> buildCutPayload(call), "cut", "USB cut failed");
    }

    @PluginMethod
    public void disconnectUsbPrinter(PluginCall call) {
        boolean forget = call.getBoolean("forget", false);
        usbExecutor.execute(() -> {
            usbPrinterManager.close();
            if (forget) {
                printerStorage.clearUsb(false);
            }
            call.resolve();
        });
    }

    // =========================
    // THERMAL (USB) METHODS
    // =========================

    @PluginMethod
    public void connectThermalPrinter(PluginCall call) {
        connectUsbPrinterInternal(call, thermalPrinterManager, true);
    }

    @PluginMethod
    public void isThermalConnected(PluginCall call) {
        JSObject result = new JSObject();
        result.put("connected", thermalPrinterManager.isConnected());
        if (thermalPrinterManager.isConnected()) {
            result.put("deviceName", thermalPrinterManager.getConnectedDeviceName());
        }
        call.resolve(result);
    }

    @PluginMethod
    public void printThermalText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is required");
            return;
        }
        executeUsbWrite(
            call,
            thermalPrinterManager,
            true,
            thermalExecutor,
            () -> buildTextPayload(call, text),
            "printed",
            "Thermal print failed"
        );
    }

    @PluginMethod
    public void printThermalImage(PluginCall call) {
        String base64 = call.getString("base64", "");
        if (base64.isEmpty()) {
            call.reject("Base64 image string is required");
            return;
        }
        executeUsbWrite(
            call,
            thermalPrinterManager,
            true,
            thermalExecutor,
            () -> buildImagePayload(call, base64),
            "printed",
            "Thermal image print failed"
        );
    }

    @PluginMethod
    public void printThermalQRCode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("QR data is required");
            return;
        }
        executeUsbWrite(
            call,
            thermalPrinterManager,
            true,
            thermalExecutor,
            () -> buildQRCodePayload(call, data),
            "printed",
            "Thermal QR print failed"
        );
    }

    @PluginMethod
    public void printThermalBarcode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("Barcode data is required");
            return;
        }
        executeUsbWrite(
            call,
            thermalPrinterManager,
            true,
            thermalExecutor,
            () -> buildBarcodePayload(call, data),
            "printed",
            "Thermal barcode print failed"
        );
    }

    @PluginMethod
    public void sendRawThermal(PluginCall call) {
        executeUsbWrite(
            call,
            thermalPrinterManager,
            true,
            thermalExecutor,
            () -> buildRawPayload(call),
            "printed",
            "Thermal raw print failed"
        );
    }

    @PluginMethod
    public void kickThermalDrawer(PluginCall call) {
        executeUsbWrite(
            call,
            thermalPrinterManager,
            true,
            thermalExecutor,
            () -> buildDrawerPayload(call),
            "kicked",
            "Thermal drawer kick failed"
        );
    }

    @PluginMethod
    public void cutThermalPaper(PluginCall call) {
        executeUsbWrite(call, thermalPrinterManager, true, thermalExecutor, () -> buildCutPayload(call), "cut", "Thermal cut failed");
    }

    @PluginMethod
    public void disconnectThermalPrinter(PluginCall call) {
        boolean forget = call.getBoolean("forget", false);
        thermalExecutor.execute(() -> {
            thermalPrinterManager.close();
            if (forget) {
                printerStorage.clearUsb(true);
            }
            call.resolve();
        });
    }

    // =========================
    // NETWORK (LAN / WI-FI) METHODS
    // =========================

    @PluginMethod
    public void connectNetworkPrinter(PluginCall call) {
        String address = call.getString("address", "");
        int port = call.getInt("port", 9100);
        int timeout = call.getInt("timeout", 5000);

        if (address.isEmpty()) {
            call.reject("Network address is required");
            return;
        }

        connectNetworkInternal(call, address, port, timeout);
    }

    private void connectNetworkInternal(PluginCall call, String address, int port, int timeout) {
        networkExecutor.execute(() -> {
            try {
                networkPrinterManager.connect(address, port, timeout);
                printerStorage.saveNetwork(address, port, timeout);

                JSObject result = new JSObject();
                result.put("connected", true);
                result.put("deviceName", "Network Printer (" + address + ":" + port + ")");
                result.put("deviceAddress", address);
                call.resolve(result);
            } catch (Exception exception) {
                Log.e(TAG, "Network connect failed", exception);
                call.reject("Network connect failed: " + exception.getMessage());
            }
        });
    }

    @PluginMethod
    public void isNetworkConnected(PluginCall call) {
        JSObject result = new JSObject();
        result.put("connected", networkPrinterManager.isConnected());
        if (networkPrinterManager.isConnected()) {
            result.put(
                "deviceName",
                "Network Printer (" + networkPrinterManager.getConnectedAddress() + ":" + networkPrinterManager.getConnectedPort() + ")"
            );
            result.put("deviceAddress", networkPrinterManager.getConnectedAddress());
        }
        call.resolve(result);
    }

    @PluginMethod
    public void printNetworkText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is required");
            return;
        }
        executeNetworkWrite(call, () -> buildTextPayload(call, text), "printed", "Network print failed");
    }

    @PluginMethod
    public void printNetworkImage(PluginCall call) {
        String base64 = call.getString("base64", "");
        if (base64.isEmpty()) {
            call.reject("Base64 image string is required");
            return;
        }
        executeNetworkWrite(call, () -> buildImagePayload(call, base64), "printed", "Network image print failed");
    }

    @PluginMethod
    public void printNetworkQRCode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("QR data is required");
            return;
        }
        executeNetworkWrite(call, () -> buildQRCodePayload(call, data), "printed", "Network QR print failed");
    }

    @PluginMethod
    public void printNetworkBarcode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("Barcode data is required");
            return;
        }
        executeNetworkWrite(call, () -> buildBarcodePayload(call, data), "printed", "Network barcode print failed");
    }

    @PluginMethod
    public void sendRawNetwork(PluginCall call) {
        executeNetworkWrite(call, () -> buildRawPayload(call), "printed", "Network raw print failed");
    }

    @PluginMethod
    public void kickNetworkDrawer(PluginCall call) {
        executeNetworkWrite(call, () -> buildDrawerPayload(call), "kicked", "Network drawer kick failed");
    }

    @PluginMethod
    public void cutNetworkPaper(PluginCall call) {
        executeNetworkWrite(call, () -> buildCutPayload(call), "cut", "Network cut failed");
    }

    @PluginMethod
    public void disconnectNetworkPrinter(PluginCall call) {
        boolean forget = call.getBoolean("forget", false);
        networkExecutor.execute(() -> {
            networkPrinterManager.close();
            if (forget) {
                printerStorage.clearNetwork();
            }
            call.resolve();
        });
    }

    /**
     * Automatically reconnects to the saved LAN/Wi-Fi printer if idle/disconnected, and retries once
     * if the printer closed the TCP socket after a previous print job.
     */
    private void executeNetworkWrite(PluginCall call, PayloadSupplier supplier, String resolveKey, String errorPrefix) {
        networkExecutor.execute(() -> {
            try {
                byte[] payload = supplier.get();
                String targetAddress = networkPrinterManager.isConnected()
                    ? networkPrinterManager.getConnectedAddress()
                    : printerStorage.getNetworkAddress();
                int targetPort = networkPrinterManager.isConnected()
                    ? networkPrinterManager.getConnectedPort()
                    : printerStorage.getNetworkPort();
                int targetTimeout = printerStorage.getNetworkTimeout();

                if (!networkPrinterManager.isConnected()) {
                    if (targetAddress == null || targetAddress.isEmpty()) {
                        call.reject(errorPrefix + ": Network printer not connected");
                        return;
                    }
                    Log.i(TAG, "Auto-reconnecting to saved Network printer: " + targetAddress + ":" + targetPort);
                    networkPrinterManager.connect(targetAddress, targetPort, targetTimeout);
                }

                try {
                    networkPrinterManager.print(payload);
                } catch (Exception writeError) {
                    if (targetAddress == null || targetAddress.isEmpty()) {
                        throw writeError;
                    }
                    Log.w(TAG, "Network write failed, attempting single auto-reconnect & retry", writeError);
                    networkPrinterManager.connect(targetAddress, targetPort, targetTimeout);
                    networkPrinterManager.print(payload);
                }

                JSObject result = new JSObject();
                result.put(resolveKey, true);
                call.resolve(result);
            } catch (Exception exception) {
                Log.e(TAG, errorPrefix, exception);
                call.reject(errorPrefix + ": " + exception.getMessage());
            }
        });
    }

    // =========================
    // INTERNAL HELPERS
    // =========================

    private void startBluetoothScan(PluginCall call) {
        bluetoothScanner.scan(
            getActivity(),
            new BluetoothScanner.ScanListener() {
                @Override
                public void onFinished(JSArray devices) {
                    JSObject result = new JSObject();
                    result.put("devices", devices);
                    result.put("count", devices.length());
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject(message);
                }

                @Override
                public void onProgress(JSObject device) {
                    notifyListeners("scanProgress", device);
                }
            }
        );
    }

    private void connectUsbPrinterInternal(PluginCall call, UsbPrinterManager manager, boolean thermal) {
        Integer vendorId = call.getInt("vendorId");
        Integer productId = call.getInt("productId");
        String deviceName = call.getString("deviceName");
        connectUsbWithParams(call, manager, thermal, vendorId, productId, deviceName);
    }

    private void connectUsbWithParams(
        PluginCall call,
        UsbPrinterManager manager,
        boolean thermal,
        Integer vendorId,
        Integer productId,
        String deviceName
    ) {
        manager.connect(
            getContext(),
            vendorId,
            productId,
            deviceName,
            new UsbPrinterManager.UsbListener() {
                @Override
                public void onConnected(JSObject device) {
                    Integer connectedVendor = device.getInteger("vendorId");
                    Integer connectedProduct = device.getInteger("productId");
                    String connectedName = device.getString("deviceName");
                    printerStorage.saveUsb(connectedVendor, connectedProduct, connectedName, thermal);

                    JSObject result = new JSObject();
                    result.put("connected", true);
                    result.put("deviceName", connectedName);
                    result.put("vendorId", connectedVendor);
                    result.put("productId", connectedProduct);
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject((thermal ? "Thermal" : "USB") + " connection failed: " + message);
                }
            }
        );
    }

    private void executeUsbWrite(
        PluginCall call,
        UsbPrinterManager manager,
        boolean thermal,
        ExecutorService executor,
        PayloadSupplier supplier,
        String resolveKey,
        String errorPrefix
    ) {
        executor.execute(() -> {
            try {
                byte[] payload = supplier.get();
                Integer savedVendor = printerStorage.getUsbVendorId(thermal);
                Integer savedProduct = printerStorage.getUsbProductId(thermal);
                String savedName = printerStorage.getUsbDeviceName(thermal);

                if (!manager.isConnected() && printerStorage.hasSavedUsb(thermal)) {
                    manager.reconnectIfPermitted(getContext(), savedVendor, savedProduct, savedName);
                }

                String error = manager.printSync(payload);
                if (error != null && printerStorage.hasSavedUsb(thermal)) {
                    if (manager.reconnectIfPermitted(getContext(), savedVendor, savedProduct, savedName)) {
                        error = manager.printSync(payload);
                    }
                }

                if (error != null) {
                    call.reject(errorPrefix + ": " + error);
                    return;
                }

                JSObject result = new JSObject();
                result.put(resolveKey, true);
                call.resolve(result);
            } catch (Exception exception) {
                call.reject(errorPrefix + ": " + exception.getMessage());
            }
        });
    }

    private byte[] buildTextPayload(PluginCall call, String text) {
        return PrinterPayloadFormatter.formatText(
            text,
            call.getInt("fontSize", 24),
            call.getString("align", "left"),
            call.getBoolean("isBold", false),
            call.getBoolean("isUnderline", false),
            call.getInt("feedLines", 2),
            call.getString("encoding", "UTF-8"),
            call.getInt("codePage"),
            call.getBoolean("reset", true)
        );
    }

    private byte[] buildImagePayload(PluginCall call, String base64) {
        return PrinterPayloadFormatter.formatImage(
            base64,
            call.getInt("width", 0),
            call.getInt("height", 0),
            call.getString("align", "left"),
            call.getInt("threshold", 128),
            call.getString("mode", "raster"),
            call.getInt("feedLines", 1)
        );
    }

    private byte[] buildQRCodePayload(PluginCall call, String data) {
        return PrinterPayloadFormatter.formatQRCode(
            data,
            call.getInt("size", 8),
            call.getString("align", "center"),
            call.getString("errorCorrection", "L"),
            call.getInt("feedLines", 1)
        );
    }

    private byte[] buildBarcodePayload(PluginCall call, String data) {
        return PrinterPayloadFormatter.formatBarcode(
            data,
            call.getString("type", "CODE128"),
            call.getInt("height", 80),
            call.getInt("width", 2),
            call.getString("align", "center"),
            call.getString("hriPosition", "below"),
            call.getInt("feedLines", 1)
        );
    }

    private byte[] buildRawPayload(PluginCall call) throws Exception {
        return PrinterPayloadFormatter.decodeRawData(call.getString("base64"), call.getArray("bytes"), call.getString("text"));
    }

    private byte[] buildDrawerPayload(PluginCall call) {
        return PrinterPayloadFormatter.kickDrawer(call.getInt("pin", 0), call.getInt("onTime", 25), call.getInt("offTime", 250));
    }

    private byte[] buildCutPayload(PluginCall call) {
        return PrinterPayloadFormatter.cut(call.getBoolean("partial", false), call.getInt("feedLines", 3));
    }

    private void shutdownExecutor(ExecutorService executor) {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }
}
