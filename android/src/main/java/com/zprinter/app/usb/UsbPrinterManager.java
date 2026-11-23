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
import androidx.core.content.ContextCompat;
import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import java.util.Collection;
import java.util.HashMap;

public class UsbPrinterManager {

    private static final String ACTION_USB_PERMISSION = "com.zprinter.app.USB_PERMISSION";
    private static final int BULK_CHUNK_SIZE = 4096;
    private static final int BULK_TIMEOUT_MS = 5000;

    private UsbManager usbManager;
    private UsbDeviceConnection connection;
    private UsbInterface claimedInterface;
    private UsbEndpoint endpoint;
    private UsbDevice connectedDevice;
    private BroadcastReceiver permissionReceiver;
    private Context registeredContext;

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
        Context appContext = context.getApplicationContext();
        usbManager = (UsbManager) appContext.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            listener.onError("USB manager is unavailable");
            return;
        }

        UsbDevice device = selectDevice(usbManager.getDeviceList(), vendorId, productId, deviceName);
        if (device == null) {
            listener.onError("No matching USB printer found");
            return;
        }

        // If permission is already granted, connect immediately without re-prompting
        if (usbManager.hasPermission(device)) {
            openConnection(device, listener);
            return;
        }

        unregisterReceiver(appContext);

        Intent permissionIntentAction = new Intent(ACTION_USB_PERMISSION);
        permissionIntentAction.setPackage(appContext.getPackageName());

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE;
        }

        PendingIntent permissionIntent = PendingIntent.getBroadcast(appContext, 0, permissionIntentAction, flags);

        permissionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context receiverContext, Intent intent) {
                if (!ACTION_USB_PERMISSION.equals(intent.getAction())) {
                    return;
                }

                unregisterReceiver(appContext);

                boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) || usbManager.hasPermission(device);

                if (granted) {
                    openConnection(device, listener);
                } else {
                    listener.onError("USB permission denied");
                }
            }
        };

        registeredContext = appContext;
        ContextCompat.registerReceiver(
            appContext,
            permissionReceiver,
            new IntentFilter(ACTION_USB_PERMISSION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        );
        usbManager.requestPermission(device, permissionIntent);
    }

    public synchronized void print(byte[] bytes, UsbListener listener) {
        if (connection == null || endpoint == null) {
            listener.onError("USB printer is not connected");
            return;
        }

        if (bytes == null || bytes.length == 0) {
            listener.onConnected(describe(connectedDevice));
            return;
        }

        int offset = 0;
        while (offset < bytes.length) {
            int length = Math.min(BULK_CHUNK_SIZE, bytes.length - offset);
            byte[] chunk = new byte[length];
            System.arraycopy(bytes, offset, chunk, 0, length);

            int result = connection.bulkTransfer(endpoint, chunk, length, BULK_TIMEOUT_MS);
            if (result < 0) {
                listener.onError("USB bulk transfer failed at byte offset " + offset);
                return;
            }
            offset += length;
        }

        listener.onConnected(describe(connectedDevice));
    }

    public synchronized boolean isConnected() {
        return connection != null && endpoint != null && connectedDevice != null;
    }

    public synchronized String getConnectedDeviceName() {
        return connectedDevice != null ? resolveDeviceName(connectedDevice) : "";
    }

    public synchronized Integer getConnectedVendorId() {
        return connectedDevice != null ? connectedDevice.getVendorId() : null;
    }

    public synchronized Integer getConnectedProductId() {
        return connectedDevice != null ? connectedDevice.getProductId() : null;
    }

    /**
     * Silently re-opens a previously permitted USB printer synchronously on the caller's background thread.
     * Returns true if reconnected successfully, false if device is missing or permission is not yet granted.
     */
    public synchronized boolean reconnectIfPermitted(Context context, Integer vendorId, Integer productId, String deviceName) {
        if (context == null) {
            return false;
        }
        Context appContext = context.getApplicationContext();
        usbManager = (UsbManager) appContext.getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return false;
        }

        UsbDevice device = selectDevice(usbManager.getDeviceList(), vendorId, productId, deviceName);
        if (device == null || !usbManager.hasPermission(device)) {
            return false;
        }

        close();
        UsbInterface usbInterface = findPrinterInterface(device);
        if (usbInterface == null) {
            return false;
        }
        UsbEndpoint outEndpoint = findOutEndpoint(usbInterface);
        if (outEndpoint == null) {
            return false;
        }
        UsbDeviceConnection deviceConnection = usbManager.openDevice(device);
        if (deviceConnection == null) {
            return false;
        }
        if (!deviceConnection.claimInterface(usbInterface, true)) {
            deviceConnection.close();
            return false;
        }

        connection = deviceConnection;
        claimedInterface = usbInterface;
        endpoint = outEndpoint;
        connectedDevice = device;
        return true;
    }

    /**
     * Synchronous write returning null on success or an error message on failure (enables clean retry).
     */
    public synchronized String printSync(byte[] bytes) {
        if (connection == null || endpoint == null) {
            return "USB printer is not connected";
        }
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        int offset = 0;
        while (offset < bytes.length) {
            int length = Math.min(BULK_CHUNK_SIZE, bytes.length - offset);
            byte[] chunk = new byte[length];
            System.arraycopy(bytes, offset, chunk, 0, length);

            int result = connection.bulkTransfer(endpoint, chunk, length, BULK_TIMEOUT_MS);
            if (result < 0) {
                return "USB bulk transfer failed at byte offset " + offset;
            }
            offset += length;
        }

        return null;
    }

    public synchronized void close() {
        unregisterReceiver(registeredContext);
        if (connection != null) {
            if (claimedInterface != null) {
                try {
                    connection.releaseInterface(claimedInterface);
                } catch (Exception ignored) {}
            }
            try {
                connection.close();
            } catch (Exception ignored) {}
            connection = null;
        }

        claimedInterface = null;
        endpoint = null;
        connectedDevice = null;
    }

    private synchronized void openConnection(UsbDevice device, UsbListener listener) {
        close();

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
        claimedInterface = usbInterface;
        endpoint = outEndpoint;
        connectedDevice = device;
        listener.onConnected(describe(device));
    }

    private UsbDevice selectDevice(HashMap<String, UsbDevice> deviceList, Integer vendorId, Integer productId, String deviceName) {
        boolean hasFilter = vendorId != null || productId != null || (deviceName != null && !deviceName.isEmpty());

        // First try matching against detected printers
        for (UsbDevice device : deviceList.values()) {
            if (!isPrinter(device)) {
                continue;
            }

            boolean vendorMatches = vendorId == null || vendorId == device.getVendorId();
            boolean productMatches = productId == null || productId == device.getProductId();
            boolean nameMatches =
                deviceName == null ||
                deviceName.isEmpty() ||
                deviceName.equalsIgnoreCase(resolveDeviceName(device)) ||
                deviceName.equalsIgnoreCase(device.getDeviceName());

            if (vendorMatches && productMatches && nameMatches) {
                return device;
            }
        }

        // If the user explicitly passed vendorId/productId for an unusual legacy printer, honor exact match even if class differs
        if (hasFilter) {
            for (UsbDevice device : deviceList.values()) {
                boolean vendorMatches = vendorId == null || vendorId == device.getVendorId();
                boolean productMatches = productId == null || productId == device.getProductId();
                boolean nameMatches =
                    deviceName == null ||
                    deviceName.isEmpty() ||
                    deviceName.equalsIgnoreCase(resolveDeviceName(device)) ||
                    deviceName.equalsIgnoreCase(device.getDeviceName());

                if (vendorMatches && productMatches && nameMatches && findPrinterInterface(device) != null) {
                    return device;
                }
            }
        }

        // Fallback to first detected printer when no filter is given
        if (!hasFilter) {
            for (UsbDevice device : deviceList.values()) {
                if (isPrinter(device)) {
                    return device;
                }
            }
        }

        return null;
    }

    private boolean isPrinter(UsbDevice device) {
        int deviceClass = device.getDeviceClass();
        if (deviceClass == UsbConstants.USB_CLASS_PRINTER) {
            return true;
        }

        if (isExcludedNonPrinterClass(deviceClass)) {
            return false;
        }

        boolean hasNonExcludedBulkOut = false;
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbInterface = device.getInterface(i);
            int ifaceClass = usbInterface.getInterfaceClass();
            if (ifaceClass == UsbConstants.USB_CLASS_PRINTER) {
                return true;
            }

            if (
                !isExcludedNonPrinterClass(ifaceClass) &&
                (ifaceClass == UsbConstants.USB_CLASS_VENDOR_SPEC ||
                    ifaceClass == UsbConstants.USB_CLASS_CDC_DATA ||
                    ifaceClass == UsbConstants.USB_CLASS_COMM) &&
                findOutEndpoint(usbInterface) != null
            ) {
                hasNonExcludedBulkOut = true;
            }
        }

        return hasNonExcludedBulkOut;
    }

    private boolean isExcludedNonPrinterClass(int usbClass) {
        return (
            usbClass == UsbConstants.USB_CLASS_MASS_STORAGE ||
            usbClass == UsbConstants.USB_CLASS_HID ||
            usbClass == UsbConstants.USB_CLASS_HUB ||
            usbClass == UsbConstants.USB_CLASS_AUDIO ||
            usbClass == UsbConstants.USB_CLASS_VIDEO ||
            usbClass == UsbConstants.USB_CLASS_WIRELESS_CONTROLLER
        );
    }

    private UsbInterface findPrinterInterface(UsbDevice device) {
        // Prefer standard USB printer class interface with bulk OUT endpoint
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbInterface = device.getInterface(i);
            if (usbInterface.getInterfaceClass() == UsbConstants.USB_CLASS_PRINTER && findOutEndpoint(usbInterface) != null) {
                return usbInterface;
            }
        }

        // Fallback to any non-excluded interface with bulk OUT endpoint (supports legacy vendor-specific USB printers)
        for (int i = 0; i < device.getInterfaceCount(); i++) {
            UsbInterface usbInterface = device.getInterface(i);
            if (!isExcludedNonPrinterClass(usbInterface.getInterfaceClass()) && findOutEndpoint(usbInterface) != null) {
                return usbInterface;
            }
        }

        return null;
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

    private void unregisterReceiver(Context context) {
        if (permissionReceiver == null || context == null) {
            permissionReceiver = null;
            registeredContext = null;
            return;
        }

        try {
            context.unregisterReceiver(permissionReceiver);
        } catch (IllegalArgumentException ignored) {}

        permissionReceiver = null;
        registeredContext = null;
    }
}
