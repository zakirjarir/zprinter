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
import android.util.Log;

@CapacitorPlugin(name = "ZPrinter", requestCodes = {
        ZPrinterPlugin.REQUEST_BLUETOOTH_PERMISSIONS
})
public class ZPrinterPlugin extends Plugin {

    private static final String TAG = "ZPrinterPlugin";
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

    // Store calls for async operations
    private PluginCall pendingScanCall;
    private PluginCall pendingUsbCall;
    private PluginCall pendingThermalCall;

    @Override
    public void load() {
        super.load();
        initializeComponents();
    }

    private void initializeComponents() {
        try {
            bluetoothScanner = new BluetoothScanner();
            bluetoothConnection = new BluetoothConnection();
            bluetoothPrinter = new BluetoothPrinter();
            usbPrinterManager = new UsbPrinterManager();
            thermalPrinterManager = new ThermalPrinterManager();
            Log.d(TAG, "Components initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize components: " + e.getMessage());
        }
    }

    // =========================
    // 1. BLUETOOTH PRINTER METHODS
    // =========================

    /**
     * Scan for Bluetooth devices
     * Maps to: scanDevices()
     */
    @PluginMethod
    public void scanBluetoothDevices(PluginCall call) {
        pendingScanCall = call;

        try {
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
                    if (pendingScanCall != null) {
                        notifyListeners("scanProgress", device);
                    }
                }
            });
        } catch (Exception e) {
            call.reject("Scan failed: " + e.getMessage());
            pendingScanCall = null;
        }
    }

    /**
     * Connect to Bluetooth printer
     * Maps to: connect(options)
     */
    @PluginMethod
    public void connectBluetooth(PluginCall call) {
        String address = call.getString("address", "");
        if (address.isEmpty()) {
            call.reject("Bluetooth address is required");
            return;
        }

        try {
            bluetoothConnection.connect(address);

            // Try to get device name (you might want to cache this from scan)
            String deviceName = "Bluetooth Printer"; // Default name

            JSObject result = new JSObject();
            result.put("connected", true);
            result.put("deviceAddress", address);
            result.put("deviceName", deviceName);
            call.resolve(result);

        } catch (Exception e) {
            Log.e(TAG, "Connect failed: " + e.getMessage());
            call.reject("Bluetooth connect failed: " + e.getMessage());
        }
    }

    /**
     * Print text on Bluetooth printer
     * Maps to: printText(options)
     */
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

            JSObject result = new JSObject();
            result.put("printed", true);
            call.resolve(result);

        } catch (Exception e) {
            Log.e(TAG, "Print failed: " + e.getMessage());
            call.reject("Bluetooth print failed: " + e.getMessage());
        }
    }

    /**
     * Format text with ESC/POS commands
     */
    private String formatText(String text, int fontSize, String align, boolean isBold) {
        StringBuilder formatted = new StringBuilder();

        // Initialize printer
        formatted.append((char) 0x1B).append((char) 0x40); // ESC @

        // Bold
        if (isBold) {
            formatted.append((char) 0x1B).append((char) 0x45).append((char) 0x01); // ESC E 1
        }

        // Alignment
        switch (align) {
            case "center":
                formatted.append((char) 0x1B).append((char) 0x61).append((char) 0x01); // ESC a 1
                break;
            case "right":
                formatted.append((char) 0x1B).append((char) 0x61).append((char) 0x02); // ESC a 2
                break;
            default: // left
                formatted.append((char) 0x1B).append((char) 0x61).append((char) 0x00); // ESC a 0
        }

        // Font size (GS ! n)
        int sizeCode = 0;
        if (fontSize >= 48) sizeCode = 0x33; // 3x3
        else if (fontSize >= 32) sizeCode = 0x22; // 2x2
        else if (fontSize >= 24) sizeCode = 0x11; // 1.5x1.5
        else sizeCode = 0x00; // normal

        formatted.append((char) 0x1D).append((char) 0x21).append((char) sizeCode); // GS ! n

        // Text
        formatted.append(text).append("\n");

        // Reset
        if (isBold) {
            formatted.append((char) 0x1B).append((char) 0x45).append((char) 0x00); // ESC E 0
        }

        return formatted.toString();
    }

    /**
     * Cut paper on Bluetooth printer
     * Maps to: cut()
     */
    @PluginMethod
    public void cutBluetoothPaper(PluginCall call) {
        try {
            bluetoothPrinter.cut(bluetoothConnection.getOutputStream());

            JSObject result = new JSObject();
            result.put("cut", true);
            call.resolve(result);

        } catch (Exception e) {
            Log.e(TAG, "Cut failed: " + e.getMessage());
            call.reject("Bluetooth cut failed: " + e.getMessage());
        }
    }

    /**
     * Disconnect Bluetooth printer
     * Maps to: disconnect()
     */
    @PluginMethod
    public void disconnectBluetooth(PluginCall call) {
        try {
            bluetoothConnection.disconnect();
            call.resolve(); // Promise<void>

        } catch (Exception e) {
            Log.e(TAG, "Disconnect failed: " + e.getMessage());
            call.reject("Bluetooth disconnect failed: " + e.getMessage());
        }
    }

    // =========================
    // 2. USB PRINTER METHODS (ADDED)
    // =========================

    /**
     * Connect to USB printer
     * Maps to: connectUsb()
     */
    @PluginMethod
    public void connectUsbPrinter(PluginCall call) {
        pendingUsbCall = call;

        try {
            usbPrinterManager.connect(getContext(), new UsbPrinterManager.UsbListener() {
                @Override
                public void onConnected(String deviceName) {
                    if (pendingUsbCall != null) {
                        pendingUsbCall.resolve(); // Promise<void>
                        pendingUsbCall = null;
                    }
                }

                @Override
                public void onError(String message) {
                    if (pendingUsbCall != null) {
                        pendingUsbCall.reject("USB connection failed: " + message);
                        pendingUsbCall = null;
                    }
                }
            });
        } catch (Exception e) {
            call.reject("USB connect error: " + e.getMessage());
            pendingUsbCall = null;
        }
    }

    /**
     * Print text on USB printer
     * Maps to: printUsb(options)
     */
    @PluginMethod
    public void printUsbText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is empty");
            return;
        }

        try {
            usbPrinterManager.print(text.getBytes(), new UsbPrinterManager.UsbListener() {
                @Override
                public void onConnected(String msg) {
                    call.resolve(); // Promise<void>
                }

                @Override
                public void onError(String message) {
                    call.reject("USB print failed: " + message);
                }
            });
        } catch (Exception e) {
            call.reject("USB print error: " + e.getMessage());
        }
    }

    /**
     * Disconnect USB printer
     * Maps to: disconnectUsb()
     */
    @PluginMethod
    public void disconnectUsbPrinter(PluginCall call) {
        try {
            usbPrinterManager.close();
            call.resolve(); // Promise<void>

        } catch (Exception e) {
            call.reject("USB disconnect failed: " + e.getMessage());
        }
    }

    // =========================
    // 3. THERMAL PRINTER METHODS (ADDED)
    // =========================

    /**
     * Connect to Thermal printer
     * Maps to: connectThermal()
     */
    @PluginMethod
    public void connectThermalPrinter(PluginCall call) {
        pendingThermalCall = call;

        try {
            thermalPrinterManager.connect(getContext(), new ThermalPrinterManager.ThermalListener() {
                @Override
                public void onConnected(String deviceName) {
                    if (pendingThermalCall != null) {
                        pendingThermalCall.resolve(); // Promise<void>
                        pendingThermalCall = null;
                    }
                }

                @Override
                public void onError(String message) {
                    if (pendingThermalCall != null) {
                        pendingThermalCall.reject("Thermal connection failed: " + message);
                        pendingThermalCall = null;
                    }
                }
            });
        } catch (Exception e) {
            call.reject("Thermal connect error: " + e.getMessage());
            pendingThermalCall = null;
        }
    }

    /**
     * Print text on Thermal printer
     * Maps to: printThermal(options)
     */
    @PluginMethod
    public void printThermalText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is empty");
            return;
        }

        try {
            thermalPrinterManager.print(text.getBytes(), new ThermalPrinterManager.ThermalListener() {
                @Override
                public void onConnected(String msg) {
                    call.resolve(); // Promise<void>
                }

                @Override
                public void onError(String message) {
                    call.reject("Thermal print failed: " + message);
                }
            });
        } catch (Exception e) {
            call.reject("Thermal print error: " + e.getMessage());
        }
    }

    /**
     * Disconnect Thermal printer
     * Maps to: disconnectThermal()
     */
    @PluginMethod
    public void disconnectThermalPrinter(PluginCall call) {
        try {
            thermalPrinterManager.close();
            call.resolve(); // Promise<void>

        } catch (Exception e) {
            call.reject("Thermal disconnect failed: " + e.getMessage());
        }
    }

    // =========================
    // 4. PERMISSION HANDLING (FIXED)
    // =========================

    @Override
    protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            boolean allGranted = true;

            // ✅ Fixed: Added null check
            if (grantResults != null && grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allGranted = false;
                        break;
                    }
                }
            } else {
                allGranted = false;
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