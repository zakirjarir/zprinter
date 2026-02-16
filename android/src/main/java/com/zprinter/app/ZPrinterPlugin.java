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

@CapacitorPlugin(name = "ZPrinter")
public class ZPrinterPlugin extends Plugin {

    // =========================
    // Bluetooth
    // =========================
    private final BluetoothScanner bluetoothScanner = new BluetoothScanner();
    private final BluetoothConnection bluetoothConnection = new BluetoothConnection();
    private final BluetoothPrinter bluetoothPrinter = new BluetoothPrinter();

    // =========================
    // USB & Thermal
    // =========================
    private final UsbPrinterManager usbPrinterManager = new UsbPrinterManager();
    private final ThermalPrinterManager thermalPrinterManager = new ThermalPrinterManager();


    // =========================
    // Bluetooth Methods
    // =========================
    @PluginMethod
    public void scanBluetoothDevices(PluginCall call) {
        bluetoothScanner.scan(getContext(), new BluetoothScanner.ScanListener() {
            @Override
            public void onFinished(JSArray devices) {
                JSObject result = new JSObject();
                result.put("devices", devices);
                call.resolve(result);
            }

            @Override
            public void onError(String message) {
                call.reject(message);
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
            call.resolve();
        } catch (Exception e) {
            call.reject("Bluetooth connect failed: " + e.getMessage());
        }
    }

    @PluginMethod
    public void printBluetoothText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is empty");
            return;
        }

        try {
            bluetoothPrinter.print(bluetoothConnection.getOutputStream(), text);
            call.resolve();
        } catch (Exception e) {
            call.reject("Bluetooth print failed: " + e.getMessage());
        }
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


    // =========================
    // USB Methods
    // =========================
    @PluginMethod
    public void connectUsbPrinter(PluginCall call) {
        usbPrinterManager.connect(getContext(), new UsbPrinterManager.UsbListener() {
            @Override
            public void onConnected(String deviceName) {
                call.resolve();
            }

            @Override
            public void onError(String message) {
                call.reject(message);
            }
        });
    }

    @PluginMethod
    public void printUsbText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is empty");
            return;
        }

        usbPrinterManager.print(text.getBytes(), new UsbPrinterManager.UsbListener() {
            @Override
            public void onConnected(String msg) {
                call.resolve();
            }

            @Override
            public void onError(String message) {
                call.reject(message);
            }
        });
    }

    @PluginMethod
    public void disconnectUsbPrinter(PluginCall call) {
        usbPrinterManager.close();
        call.resolve();
    }


    // =========================
    // Thermal Printer Methods
    // =========================
    @PluginMethod
    public void connectThermalPrinter(PluginCall call) {
        thermalPrinterManager.connect(getContext(), new ThermalPrinterManager.ThermalListener() {
            @Override
            public void onConnected(String deviceName) {
                call.resolve();
            }

            @Override
            public void onError(String message) {
                call.reject(message);
            }
        });
    }

    @PluginMethod
    public void printThermalText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is empty");
            return;
        }

        thermalPrinterManager.print(text.getBytes(), new ThermalPrinterManager.ThermalListener() {
            @Override
            public void onConnected(String msg) {
                call.resolve();
            }

            @Override
            public void onError(String message) {
                call.reject(message);
            }
        });
    }

    @PluginMethod
    public void disconnectThermalPrinter(PluginCall call) {
        thermalPrinterManager.close();
        call.resolve();
    }

}
