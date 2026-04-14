package com.zprinter.app.usb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import java.util.Collection;
import java.util.HashMap;

public class UsbPrinterManager {

    private static final String ACTION_USB_PERMISSION = "com.zprinter.app.USB_PERMISSION";

    private UsbManager usbManager;
    private UsbDeviceConnection connection;
    private UsbEndpoint endpoint;
    private UsbDevice connectedDevice;
    private BroadcastReceiver permissionReceiver;

    public interface UsbListener {
        void onConnected(JSObject device);
        void onError(String message);
    }

    public JSArray listPrinters(Context context) {
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        JSArray devices = new JSArray();

        if (usbManager == null) {
            return devices;
        }

        Collection<UsbDevice> deviceValues = usbManager.getDeviceList().values();
        for (UsbDevice device : deviceValues) {
            if (isPrinter(device)) {
                devices.put(describe(device));
            }
        }

        return devices;
    }

    public void connect(Context context, Integer vendorId, Integer productId, String deviceName, UsbListener listener) {
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            listener.onError("USB manager is unavailable");
            return;
        }

        UsbDevice device = selectDevice(usbManager.getDeviceList(), vendorId, productId, deviceName);
        if (device == null) {
            listener.onError("No matching USB printer found");
            return;
        }

        PendingIntent permissionIntent = PendingIntent.getBroadcast(
            context,
            0,
            new Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_IMMUTABLE
        );

        permissionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context receiverContext, Intent intent) {
                if (!ACTION_USB_PERMISSION.equals(intent.getAction())) {
                    return;
                }

                unregisterReceiver(receiverContext);

                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                    openConnection(device, listener);
                } else {
                    listener.onError("USB permission denied");
                }
            }
        };

        registerReceiver(context, permissionReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        usbManager.requestPermission(device, permissionIntent);
    }

    public void print(byte[] bytes, UsbListener listener) {
        if (connection == null || endpoint == null) {
            listener.onError("USB printer is not connected");
            return;
        }

        int result = connection.bulkTransfer(endpoint, bytes, bytes.length, 5000);
        if (result < 0) {
            listener.onError("USB print failed");
            return;
        }

        listener.onConnected(describe(connectedDevice));
    }

    public void close() {
        if (connection != null) {
            connection.close();
            connection = null;
        }

        endpoint = null;
        connectedDevice = null;
    }

    private void openConnection(UsbDevice device, UsbListener listener) {
        UsbInterface usbInterface = findPrinterInterface(device);
        if (usbInterface == null) {
            listener.onError("No printable USB interface found");
            return;
        }

        UsbEndpoint outEndpoint = findOutEndpoint(usbInterface);
        if (outEndpoint == null) {
            listener.onError("No writable USB endpoint found");
            return;
        }

        UsbDeviceConnection deviceConnection = usbManager.openDevice(device);
        if (deviceConnection == null) {
            listener.onError("Failed to open USB device");
            return;
        }

        if (!deviceConnection.claimInterface(usbInterface, true)) {
            deviceConnection.close();
            listener.onError("Failed to claim USB printer interface");
            return;
        }

        connection = deviceConnection;
        endpoint = outEndpoint;
        connectedDevice = device;
        listener.onConnected(describe(device));
    }

    private UsbDevice selectDevice(HashMap<String, UsbDevice> deviceList, Integer vendorId, Integer productId, String deviceName) {
        for (UsbDevice device : deviceList.values()) {
            if (!isPrinter(device)) {
                continue;
            }

            boolean vendorMatches = vendorId == null || vendorId == device.getVendorId();
            boolean productMatches = productId == null || productId == device.getProductId();
            boolean nameMatches = deviceName == null || deviceName.isEmpty() || deviceName.equalsIgnoreCase(resolveDeviceName(device));

            if (vendorMatches && productMatches && nameMatches) {
                return device;
            }
        }

        for (UsbDevice device : deviceList.values()) {
            if (isPrinter(device)) {
                return device;
            }
        }

        return null;
    }

    private boolean isPrinter(UsbDevice device) {
        if (device.getDeviceClass() == UsbConstants.USB_CLASS_PRINTER) {
            return true;
        }

        for (int i = 0; i < device.getInterfaceCount(); i++) {
            if (device.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER) {
                return true;
            }
        }

        return findPrinterInterface(device) != null;
    }

    private UsbInterface findPrinterInterface(UsbDevice device) {
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbInterface = device.getInterface(i);
            if (findOutEndpoint(usbInterface) != null) {
                return usbInterface;
            }
        }

        return device.getInterfaceCount() > 0 ? device.getInterface(0) : null;
    }

    private UsbEndpoint findOutEndpoint(UsbInterface usbInterface) {
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint candidate = usbInterface.getEndpoint(i);
            if (candidate.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK && candidate.getDirection() == UsbConstants.USB_DIR_OUT) {
                return candidate;
            }
        }

        return null;
    }

    private JSObject describe(UsbDevice device) {
        JSObject json = new JSObject();
        if (device == null) {
            json.put("deviceName", "");
            json.put("vendorId", 0);
            json.put("productId", 0);
            return json;
        }

        json.put("deviceName", resolveDeviceName(device));
        json.put("vendorId", device.getVendorId());
        json.put("productId", device.getProductId());
        json.put("manufacturerName", device.getManufacturerName());
        json.put("productName", device.getProductName());
        json.put("deviceClass", device.getDeviceClass());
        return json;
    }

    private String resolveDeviceName(UsbDevice device) {
        if (device.getProductName() != null && !device.getProductName().isEmpty()) {
            return device.getProductName();
        }

        return device.getDeviceName();
    }

    private void registerReceiver(Context context, BroadcastReceiver receiver, IntentFilter filter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED);
            return;
        }

        context.registerReceiver(receiver, filter);
    }

    private void unregisterReceiver(Context context) {
        if (permissionReceiver == null) {
            return;
        }

        try {
            context.unregisterReceiver(permissionReceiver);
        } catch (IllegalArgumentException ignored) {}

        permissionReceiver = null;
    }
}
