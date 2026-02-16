package com.zprinter.app.usb;

import android.app.PendingIntent;
import android.content.*;
import android.hardware.usb.*;
import android.os.Build;
import android.content.pm.PackageManager;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;

import java.util.HashMap;

public class ThermalPrinterManager {

    private static final String ACTION_USB_PERMISSION = "com.zprinter.app.USB_PERMISSION_THERMAL";

    private UsbManager usbManager;
    private UsbDeviceConnection connection;
    private UsbEndpoint endpoint;

    public interface ThermalListener {
        void onConnected(String deviceName);
        void onError(String message);
    }

    public void connect(Context context, ThermalListener listener) {

        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        if (deviceList.isEmpty()) {
            listener.onError("No USB device found");
            return;
        }

        // Thermal printer generally first USB device
        UsbDevice device = deviceList.values().iterator().next();

        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
        );

        BroadcastReceiver usbReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        openConnection(device, listener);
                    } else {
                        listener.onError("USB permission denied");
                    }
                    context.unregisterReceiver(this);
                }
            }
        };

        context.registerReceiver(usbReceiver, new IntentFilter(ACTION_USB_PERMISSION));
        usbManager.requestPermission(device, permissionIntent);
    }

    private void openConnection(UsbDevice device, ThermalListener listener) {
        UsbInterface usbInterface = device.getInterface(0);

        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                    ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                endpoint = ep;
                break;
            }
        }

        connection = usbManager.openDevice(device);

        if (connection == null) {
            listener.onError("Connection failed");
            return;
        }

        connection.claimInterface(usbInterface, true);
        listener.onConnected(device.getProductName());
    }

    public void print(byte[] bytes, ThermalListener listener) {
        if (connection == null || endpoint == null) {
            listener.onError("Printer not connected");
            return;
        }

        connection.bulkTransfer(endpoint, bytes, bytes.length, 1000);
        listener.onConnected("Printed");
    }

    public void close() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
}
