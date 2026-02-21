package com.zprinter.app;

import com.getcapacitor.*;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;

import com.zprinter.app.bluetooth.BluetoothScanner;
import com.zprinter.app.bluetooth.BluetoothConnection;
import com.zprinter.app.bluetooth.BluetoothPrinter;
import com.zprinter.app.usb.UsbPrinterManager;
import com.zprinter.app.usb.ThermalPrinterManager;

import android.content.pm.PackageManager;
@CapacitorPlugin(name = "ZPrinter", requestCodes = {
        ZPrinterPlugin.REQUEST_BLUETOOTH_PERMISSIONS
})
public class ZPrinterPlugin extends Plugin {

    public static final int REQUEST_BLUETOOTH_PERMISSIONS = 10001;

    // =========================
    // Bluetooth
    // =========================
    private BluetoothScanner bluetoothScanner;
    private BluetoothConnection bluetoothConnection;
    private BluetoothPrinter bluetoothPrinter;

    // =========================
    // USB & Thermal
    // =========================
    private UsbPrinterManager usbPrinterManager;
    private ThermalPrinterManager thermalPrinterManager;

    // Store call for async operations
    private PluginCall pendingScanCall;

    @Override
    public void load() {
        super.load();
        bluetoothScanner = new BluetoothScanner();
        bluetoothConnection = new BluetoothConnection();
        bluetoothPrinter = new BluetoothPrinter();
        usbPrinterManager = new UsbPrinterManager();
        thermalPrinterManager = new ThermalPrinterManager();
    }

    // =========================
    // Bluetooth Methods
    // =========================
    @PluginMethod
    public void scanBluetoothDevices(PluginCall call) {
        pendingScanCall = call;

        bluetoothScanner.scan(getActivity(), new BluetoothScanner.ScanListener() {
            @Override
            public void onFinished(JSArray devices) {
                if (pendingScanCall != null) {
                    JSObject result = new JSObject();
                    result.put("devices", devices);
                    result.put("count", devices.length());
                    pendingScanCall.resolve(result);
                    pendingScanCall = null;
                }
            }

            @Override
            public void onError(String message) {
                if (pendingScanCall != null) {
                    pendingScanCall.reject(message);
                    pendingScanCall = null;
                }
            }

            @Override
            public void onProgress(JSObject device) {
                // Optional: Send progress events
                if (pendingScanCall != null) {
                    // You can use notifyListeners for real-time updates
                    notifyListeners("scanProgress", device);
                }
            }
        });
    }

    @PluginMethod
    public void connectBluetooth(PluginCall call) {
        String address = call.getString("address", "");
        if (address.isEmpty()) {
            call.reject("Bluetooth address is required");
            return;
        }

        try {
            bluetoothConnection.connect(address);
            JSObject result = new JSObject();
            result.put("connected", true);
            result.put("address", address);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Bluetooth connect failed: " + e.getMessage());
        }
    }

    @PluginMethod
    public void printBluetoothText(PluginCall call) {
        String text = call.getString("text", "");
        Integer fontSize = call.getInt("fontSize", 24);
        String align = call.getString("align", "left");
        Boolean isBold = call.getBoolean("isBold", false);

        if (text.isEmpty()) {
            call.reject("Text is empty");
            return;
        }

        try {
            // Format text with ESC/POS commands
            String formattedText = formatText(text, fontSize, align, isBold);
            bluetoothPrinter.print(bluetoothConnection.getOutputStream(), formattedText);
            call.resolve();
        } catch (Exception e) {
            call.reject("Bluetooth print failed: " + e.getMessage());
        }
    }

    private String formatText(String text, int fontSize, String align, boolean isBold) {
        StringBuilder formatted = new StringBuilder();

        // ESC/POS commands
        if (isBold) {
            formatted.append((char) 0x1B).append((char) 0x45).append((char) 0x01); // Bold on
        }

        // Alignment
        switch (align) {
            case "center":
                formatted.append((char) 0x1B).append((char) 0x61).append((char) 0x01);
                break;
            case "right":
                formatted.append((char) 0x1B).append((char) 0x61).append((char) 0x02);
                break;
            default: // left
                formatted.append((char) 0x1B).append((char) 0x61).append((char) 0x00);
        }

        // Font size (GS ! n)
        int sizeCode = 0;
        if (fontSize >= 48) sizeCode = 0x33; // 3x3
        else if (fontSize >= 32) sizeCode = 0x22; // 2x2
        else if (fontSize >= 24) sizeCode = 0x11; // 1.5x1.5
        else sizeCode = 0x00; // normal

        formatted.append((char) 0x1D).append((char) 0x21).append((char) sizeCode);

        formatted.append(text).append("\n");

        if (isBold) {
            formatted.append((char) 0x1B).append((char) 0x45).append((char) 0x00); // Bold off
        }

        return formatted.toString();
    }

    @PluginMethod
    public void cutBluetoothPaper(PluginCall call) {
        try {
            bluetoothPrinter.cut(bluetoothConnection.getOutputStream());
            call.resolve();
        } catch (Exception e) {
            call.reject("Bluetooth cut failed: " + e.getMessage());
        }
    }

    @PluginMethod
    public void disconnectBluetooth(PluginCall call) {
        try {
            bluetoothConnection.disconnect();
            call.resolve();
        } catch (Exception e) {
            call.reject("Bluetooth disconnect failed: " + e.getMessage());
        }
    }

    // Handle permission results
    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted && pendingScanCall != null) {
                // Retry scan
                scanBluetoothDevices(pendingScanCall);
            } else if (pendingScanCall != null) {
                pendingScanCall.reject("Bluetooth permissions denied");
                pendingScanCall = null;
            }
        }
    }
}