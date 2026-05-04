package com.zprinter.app;

import android.Manifest;
import android.os.Build;
import android.util.Log;
import androidx.annotation.Nullable;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.zprinter.app.bluetooth.BluetoothConnection;
import com.zprinter.app.bluetooth.BluetoothPrinter;
import com.zprinter.app.bluetooth.BluetoothScanner;
import com.zprinter.app.usb.ThermalPrinterManager;
import com.zprinter.app.usb.UsbPrinterManager;
import com.zprinter.app.network.NetworkPrinterManager;

@CapacitorPlugin(
    name = "ZPrinter",
    permissions = {
        @Permission(
            alias = "bluetooth",
            strings = {
                Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION
            }
        )
    }
)
public class ZPrinterPlugin extends Plugin {

    private static final String TAG = "ZPrinterPlugin";

    private BluetoothScanner bluetoothScanner;
    private BluetoothConnection bluetoothConnection;
    private BluetoothPrinter bluetoothPrinter;
    private UsbPrinterManager usbPrinterManager;
    private ThermalPrinterManager thermalPrinterManager;
    private NetworkPrinterManager networkPrinterManager;

    private PluginCall pendingScanCall;

    @Override
    public void load() {
        bluetoothScanner = new BluetoothScanner();
        bluetoothConnection = new BluetoothConnection();
        bluetoothPrinter = new BluetoothPrinter();
        usbPrinterManager = new UsbPrinterManager();
        thermalPrinterManager = new ThermalPrinterManager();
        networkPrinterManager = new NetworkPrinterManager();
    }

    @PluginMethod
    public void scanBluetoothDevices(PluginCall call) {
        pendingScanCall = call;

        if (requiresBluetoothRuntimePermission() && !hasRequiredPermissions()) {
            requestPermissionForAlias("bluetooth", call, "bluetoothPermissionCallback");
            return;
        }

        startBluetoothScan(call);
    }

    @PermissionCallback
    private void bluetoothPermissionCallback(PluginCall call) {
        if (getPermissionState("bluetooth") != com.getcapacitor.PermissionState.GRANTED) {
            if (pendingScanCall != null) {
                pendingScanCall.reject("Bluetooth permission denied");
                pendingScanCall = null;
            }
            return;
        }

        PluginCall effectiveCall = pendingScanCall != null ? pendingScanCall : call;
        startBluetoothScan(effectiveCall);
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
            result.put("deviceName", "Bluetooth Printer");
            result.put("deviceAddress", address);
            call.resolve(result);
        } catch (Exception exception) {
            Log.e(TAG, "Bluetooth connect failed", exception);
            call.reject("Bluetooth connect failed: " + exception.getMessage());
        }
    }

    @PluginMethod
    public void printBluetoothText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is required");
            return;
        }

        try {
            byte[] payload = PrinterPayloadFormatter.formatText(
                text,
                call.getInt("fontSize", 24),
                call.getString("align", "left"),
                call.getBoolean("isBold", false),
                call.getInt("feedLines", 2)
            );

            bluetoothPrinter.print(bluetoothConnection.getOutputStream(), payload);

            JSObject result = new JSObject();
            result.put("printed", true);
            call.resolve(result);
        } catch (Exception exception) {
            Log.e(TAG, "Bluetooth print failed", exception);
            call.reject("Bluetooth print failed: " + exception.getMessage());
        }
    }

    @PluginMethod
    public void printBluetoothImage(PluginCall call) {
        String base64 = call.getString("base64", "");
        if (base64.isEmpty()) {
            call.reject("Base64 image string is required");
            return;
        }

        try {
            byte[] payload = PrinterPayloadFormatter.formatImage(
                base64,
                call.getInt("width", 0),
                call.getInt("height", 0),
                call.getString("align", "left")
            );

            bluetoothPrinter.print(bluetoothConnection.getOutputStream(), payload);

            JSObject result = new JSObject();
            result.put("printed", true);
            call.resolve(result);
        } catch (Exception exception) {
            Log.e(TAG, "Bluetooth image print failed", exception);
            call.reject("Bluetooth image print failed: " + exception.getMessage());
        }
    }

    @PluginMethod
    public void printBluetoothQRCode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("QR data is required");
            return;
        }

        try {
            byte[] payload = PrinterPayloadFormatter.formatQRCode(
                data,
                call.getInt("size", 8),
                call.getString("align", "center")
            );

            bluetoothPrinter.print(bluetoothConnection.getOutputStream(), payload);

            JSObject result = new JSObject();
            result.put("printed", true);
            call.resolve(result);
        } catch (Exception exception) {
            Log.e(TAG, "Bluetooth QR print failed", exception);
            call.reject("Bluetooth QR print failed: " + exception.getMessage());
        }
    }

    @PluginMethod
    public void kickBluetoothDrawer(PluginCall call) {
        try {
            bluetoothPrinter.print(bluetoothConnection.getOutputStream(), PrinterPayloadFormatter.kickDrawer());
            JSObject result = new JSObject();
            result.put("kicked", true);
            call.resolve(result);
        } catch (Exception exception) {
            Log.e(TAG, "Bluetooth drawer kick failed", exception);
            call.reject("Bluetooth drawer kick failed: " + exception.getMessage());
        }
    }

    @PluginMethod
    public void cutBluetoothPaper(PluginCall call) {
        try {
            bluetoothPrinter.cut(bluetoothConnection.getOutputStream(), PrinterPayloadFormatter.cut());
            JSObject result = new JSObject();
            result.put("cut", true);
            call.resolve(result);
        } catch (Exception exception) {
            Log.e(TAG, "Bluetooth cut failed", exception);
            call.reject("Bluetooth cut failed: " + exception.getMessage());
        }
    }

    @PluginMethod
    public void disconnectBluetooth(PluginCall call) {
        try {
            bluetoothConnection.disconnect();
            call.resolve();
        } catch (Exception exception) {
            call.reject("Bluetooth disconnect failed: " + exception.getMessage());
        }
    }

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
    public void printUsbText(PluginCall call) {
        printUsbTextInternal(call, usbPrinterManager, "USB");
    }

    @PluginMethod
    public void printUsbImage(PluginCall call) {
        printUsbImageInternal(call, usbPrinterManager, "USB");
    }

    @PluginMethod
    public void printUsbQRCode(PluginCall call) {
        printUsbQRCodeInternal(call, usbPrinterManager, "USB");
    }

    @PluginMethod
    public void kickUsbDrawer(PluginCall call) {
        kickUsbDrawerInternal(call, usbPrinterManager, "USB");
    }

    @PluginMethod
    public void disconnectUsbPrinter(PluginCall call) {
        usbPrinterManager.close();
        call.resolve();
    }

    @PluginMethod
    public void connectThermalPrinter(PluginCall call) {
        connectUsbPrinterInternal(call, thermalPrinterManager, true);
    }

    @PluginMethod
    public void printThermalText(PluginCall call) {
        printUsbTextInternal(call, thermalPrinterManager, "Thermal");
    }

    @PluginMethod
    public void printThermalImage(PluginCall call) {
        printUsbImageInternal(call, thermalPrinterManager, "Thermal");
    }

    @PluginMethod
    public void printThermalQRCode(PluginCall call) {
        printUsbQRCodeInternal(call, thermalPrinterManager, "Thermal");
    }

    @PluginMethod
    public void kickThermalDrawer(PluginCall call) {
        kickUsbDrawerInternal(call, thermalPrinterManager, "Thermal");
    }

    @PluginMethod
    public void disconnectThermalPrinter(PluginCall call) {
        thermalPrinterManager.close();
        call.resolve();
    }

    @PluginMethod
    public void connectNetworkPrinter(PluginCall call) {
        String address = call.getString("address", "");
        Integer port = call.getInt("port", 9100);

        if (address.isEmpty()) {
            call.reject("Network address is required");
            return;
        }

        new Thread(() -> {
            try {
                networkPrinterManager.connect(address, port);
                JSObject result = new JSObject();
                result.put("connected", true);
                result.put("deviceName", "Network Printer");
                result.put("deviceAddress", address);
                call.resolve(result);
            } catch (Exception exception) {
                Log.e(TAG, "Network connect failed", exception);
                call.reject("Network connect failed: " + exception.getMessage());
            }
        }).start();
    }

    @PluginMethod
    public void printNetworkText(PluginCall call) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is required");
            return;
        }

        new Thread(() -> {
            try {
                byte[] payload = PrinterPayloadFormatter.formatText(
                    text,
                    call.getInt("fontSize", 24),
                    call.getString("align", "left"),
                    call.getBoolean("isBold", false),
                    call.getInt("feedLines", 2)
                );

                networkPrinterManager.print(payload);

                JSObject result = new JSObject();
                result.put("printed", true);
                call.resolve(result);
            } catch (Exception exception) {
                Log.e(TAG, "Network print failed", exception);
                call.reject("Network print failed: " + exception.getMessage());
            }
        }).start();
    }

    @PluginMethod
    public void printNetworkImage(PluginCall call) {
        String base64 = call.getString("base64", "");
        if (base64.isEmpty()) {
            call.reject("Base64 image string is required");
            return;
        }

        new Thread(() -> {
            try {
                byte[] payload = PrinterPayloadFormatter.formatImage(
                    base64,
                    call.getInt("width", 0),
                    call.getInt("height", 0),
                    call.getString("align", "left")
                );

                networkPrinterManager.print(payload);

                JSObject result = new JSObject();
                result.put("printed", true);
                call.resolve(result);
            } catch (Exception exception) {
                Log.e(TAG, "Network image print failed", exception);
                call.reject("Network image print failed: " + exception.getMessage());
            }
        }).start();
    }

    @PluginMethod
    public void printNetworkQRCode(PluginCall call) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("QR data is required");
            return;
        }

        new Thread(() -> {
            try {
                byte[] payload = PrinterPayloadFormatter.formatQRCode(
                    data,
                    call.getInt("size", 8),
                    call.getString("align", "center")
                );

                networkPrinterManager.print(payload);

                JSObject result = new JSObject();
                result.put("printed", true);
                call.resolve(result);
            } catch (Exception exception) {
                Log.e(TAG, "Network QR print failed", exception);
                call.reject("Network QR print failed: " + exception.getMessage());
            }
        }).start();
    }

    @PluginMethod
    public void kickNetworkDrawer(PluginCall call) {
        new Thread(() -> {
            try {
                networkPrinterManager.print(PrinterPayloadFormatter.kickDrawer());
                JSObject result = new JSObject();
                result.put("kicked", true);
                call.resolve(result);
            } catch (Exception exception) {
                Log.e(TAG, "Network drawer kick failed", exception);
                call.reject("Network drawer kick failed: " + exception.getMessage());
            }
        }).start();
    }

    @PluginMethod
    public void disconnectNetworkPrinter(PluginCall call) {
        networkPrinterManager.close();
        call.resolve();
    }

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
                    pendingScanCall = null;
                }

                @Override
                public void onError(String message) {
                    call.reject(message);
                    pendingScanCall = null;
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

        manager.connect(
            getContext(),
            vendorId,
            productId,
            deviceName,
            new UsbPrinterManager.UsbListener() {
                @Override
                public void onConnected(JSObject device) {
                    JSObject result = new JSObject();
                    result.put("connected", true);
                    result.put("deviceName", device.getString("deviceName"));
                    result.put("vendorId", device.getInteger("vendorId"));
                    result.put("productId", device.getInteger("productId"));
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject((thermal ? "Thermal" : "USB") + " connection failed: " + message);
                }
            }
        );
    }

    private void printUsbTextInternal(PluginCall call, UsbPrinterManager manager, String printerType) {
        String text = call.getString("text", "");
        if (text.isEmpty()) {
            call.reject("Text is required");
            return;
        }

        byte[] payload = PrinterPayloadFormatter.formatText(
            text,
            call.getInt("fontSize", 24),
            call.getString("align", "left"),
            call.getBoolean("isBold", false),
            call.getInt("feedLines", 2)
        );

        manager.print(
            payload,
            new UsbPrinterManager.UsbListener() {
                @Override
                public void onConnected(JSObject device) {
                    JSObject result = new JSObject();
                    result.put("printed", true);
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject(printerType + " print failed: " + message);
                }
            }
        );
    }

    private void printUsbImageInternal(PluginCall call, UsbPrinterManager manager, String printerType) {
        String base64 = call.getString("base64", "");
        if (base64.isEmpty()) {
            call.reject("Base64 image string is required");
            return;
        }

        byte[] payload = PrinterPayloadFormatter.formatImage(
            base64,
            call.getInt("width", 0),
            call.getInt("height", 0),
            call.getString("align", "left")
        );

        manager.print(
            payload,
            new UsbPrinterManager.UsbListener() {
                @Override
                public void onConnected(JSObject device) {
                    JSObject result = new JSObject();
                    result.put("printed", true);
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject(printerType + " image print failed: " + message);
                }
            }
        );
    }

    private void printUsbQRCodeInternal(PluginCall call, UsbPrinterManager manager, String printerType) {
        String data = call.getString("data", "");
        if (data.isEmpty()) {
            call.reject("QR data is required");
            return;
        }

        byte[] payload = PrinterPayloadFormatter.formatQRCode(
            data,
            call.getInt("size", 8),
            call.getString("align", "center")
        );

        manager.print(
            payload,
            new UsbPrinterManager.UsbListener() {
                @Override
                public void onConnected(JSObject device) {
                    JSObject result = new JSObject();
                    result.put("printed", true);
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject(printerType + " QR print failed: " + message);
                }
            }
        );
    }

    private void kickUsbDrawerInternal(PluginCall call, UsbPrinterManager manager, String printerType) {
        manager.print(
            PrinterPayloadFormatter.kickDrawer(),
            new UsbPrinterManager.UsbListener() {
                @Override
                public void onConnected(JSObject device) {
                    JSObject result = new JSObject();
                    result.put("kicked", true);
                    call.resolve(result);
                }

                @Override
                public void onError(String message) {
                    call.reject(printerType + " drawer kick failed: " + message);
                }
            }
        );
    }

    private boolean requiresBluetoothRuntimePermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S;
    }
}
