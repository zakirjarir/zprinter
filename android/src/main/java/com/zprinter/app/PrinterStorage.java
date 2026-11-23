package com.zprinter.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.getcapacitor.JSObject;

public class PrinterStorage {

    private static final String PREFS_NAME = "zprinter_saved_printers";

    private static final String KEY_BT_ADDRESS = "bt_address";
    private static final String KEY_BT_NAME = "bt_name";

    private static final String KEY_USB_VENDOR = "usb_vendor";
    private static final String KEY_USB_PRODUCT = "usb_product";
    private static final String KEY_USB_NAME = "usb_name";

    private static final String KEY_THERMAL_VENDOR = "thermal_vendor";
    private static final String KEY_THERMAL_PRODUCT = "thermal_product";
    private static final String KEY_THERMAL_NAME = "thermal_name";

    private static final String KEY_NET_ADDRESS = "net_address";
    private static final String KEY_NET_PORT = "net_port";
    private static final String KEY_NET_TIMEOUT = "net_timeout";

    private final SharedPreferences prefs;

    public PrinterStorage(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveBluetooth(String address, String name) {
        if (address == null || address.isEmpty()) {
            return;
        }
        prefs.edit().putString(KEY_BT_ADDRESS, address).putString(KEY_BT_NAME, name != null ? name : "Bluetooth Printer").apply();
    }

    public String getBluetoothAddress() {
        return prefs.getString(KEY_BT_ADDRESS, null);
    }

    public String getBluetoothName() {
        return prefs.getString(KEY_BT_NAME, "Bluetooth Printer");
    }

    public void clearBluetooth() {
        prefs.edit().remove(KEY_BT_ADDRESS).remove(KEY_BT_NAME).apply();
    }

    public void saveUsb(Integer vendorId, Integer productId, String deviceName, boolean isThermal) {
        String vKey = isThermal ? KEY_THERMAL_VENDOR : KEY_USB_VENDOR;
        String pKey = isThermal ? KEY_THERMAL_PRODUCT : KEY_USB_PRODUCT;
        String nKey = isThermal ? KEY_THERMAL_NAME : KEY_USB_NAME;

        SharedPreferences.Editor editor = prefs.edit();
        if (vendorId != null && vendorId > 0) {
            editor.putInt(vKey, vendorId);
        } else {
            editor.remove(vKey);
        }
        if (productId != null && productId > 0) {
            editor.putInt(pKey, productId);
        } else {
            editor.remove(pKey);
        }
        if (deviceName != null && !deviceName.isEmpty()) {
            editor.putString(nKey, deviceName);
        } else {
            editor.remove(nKey);
        }
        editor.apply();
    }

    public Integer getUsbVendorId(boolean isThermal) {
        String key = isThermal ? KEY_THERMAL_VENDOR : KEY_USB_VENDOR;
        return prefs.contains(key) ? prefs.getInt(key, -1) : null;
    }

    public Integer getUsbProductId(boolean isThermal) {
        String key = isThermal ? KEY_THERMAL_PRODUCT : KEY_USB_PRODUCT;
        return prefs.contains(key) ? prefs.getInt(key, -1) : null;
    }

    public String getUsbDeviceName(boolean isThermal) {
        String key = isThermal ? KEY_THERMAL_NAME : KEY_USB_NAME;
        return prefs.getString(key, null);
    }

    public boolean hasSavedUsb(boolean isThermal) {
        return getUsbVendorId(isThermal) != null || getUsbProductId(isThermal) != null || getUsbDeviceName(isThermal) != null;
    }

    public void clearUsb(boolean isThermal) {
        if (isThermal) {
            prefs.edit().remove(KEY_THERMAL_VENDOR).remove(KEY_THERMAL_PRODUCT).remove(KEY_THERMAL_NAME).apply();
        } else {
            prefs.edit().remove(KEY_USB_VENDOR).remove(KEY_USB_PRODUCT).remove(KEY_USB_NAME).apply();
        }
    }

    public void saveNetwork(String address, int port, int timeout) {
        if (address == null || address.isEmpty()) {
            return;
        }
        prefs.edit().putString(KEY_NET_ADDRESS, address).putInt(KEY_NET_PORT, port).putInt(KEY_NET_TIMEOUT, timeout).apply();
    }

    public String getNetworkAddress() {
        return prefs.getString(KEY_NET_ADDRESS, null);
    }

    public int getNetworkPort() {
        return prefs.getInt(KEY_NET_PORT, 9100);
    }

    public int getNetworkTimeout() {
        return prefs.getInt(KEY_NET_TIMEOUT, 5000);
    }

    public void clearNetwork() {
        prefs.edit().remove(KEY_NET_ADDRESS).remove(KEY_NET_PORT).remove(KEY_NET_TIMEOUT).apply();
    }

    public void clearAll() {
        prefs.edit().clear().apply();
    }

    public JSObject getAllAsJSObject() {
        JSObject result = new JSObject();

        String btAddress = getBluetoothAddress();
        if (btAddress != null && !btAddress.isEmpty()) {
            JSObject bt = new JSObject();
            bt.put("address", btAddress);
            bt.put("deviceName", getBluetoothName());
            result.put("bluetooth", bt);
        }

        if (hasSavedUsb(false)) {
            JSObject usb = new JSObject();
            if (getUsbDeviceName(false) != null) usb.put("deviceName", getUsbDeviceName(false));
            if (getUsbVendorId(false) != null) usb.put("vendorId", getUsbVendorId(false));
            if (getUsbProductId(false) != null) usb.put("productId", getUsbProductId(false));
            result.put("usb", usb);
        }

        if (hasSavedUsb(true)) {
            JSObject thermal = new JSObject();
            if (getUsbDeviceName(true) != null) thermal.put("deviceName", getUsbDeviceName(true));
            if (getUsbVendorId(true) != null) thermal.put("vendorId", getUsbVendorId(true));
            if (getUsbProductId(true) != null) thermal.put("productId", getUsbProductId(true));
            result.put("thermal", thermal);
        }

        String netAddress = getNetworkAddress();
        if (netAddress != null && !netAddress.isEmpty()) {
            JSObject net = new JSObject();
            net.put("address", netAddress);
            net.put("port", getNetworkPort());
            net.put("timeout", getNetworkTimeout());
            result.put("network", net);
        }

        return result;
    }
}
