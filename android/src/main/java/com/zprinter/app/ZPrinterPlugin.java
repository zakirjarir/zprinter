package com.zprinter.app;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.OutputStream;
import java.util.UUID;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "ZPrinter")
public class ZPrinterPlugin extends Plugin {

    private BluetoothSocket socket;
    private OutputStream outputStream;

    @PluginMethod
    public void connect(PluginCall call) {
        String address = call.getString("address");

        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = adapter.getRemoteDevice(address);

            socket = device.createRfcommSocketToServiceRecord(
                    UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
            );

            socket.connect();
            outputStream = socket.getOutputStream();

            JSObject ret = new JSObject();
            ret.put("connected", true);
            call.resolve(ret);

        } catch (Exception ex) {
            call.reject(ex.getMessage());
        }
    }

    @PluginMethod
    public void printText(PluginCall call) {
        String text = call.getString("text");

        try {
            outputStream.write(text.getBytes());
            call.resolve();
        } catch (Exception ex) {
            call.reject(ex.getMessage());
        }
    }

    @PluginMethod
    public void cut(PluginCall call) {
        try {
            outputStream.write(new byte[]{0x1D, 0x56, 0x00});
            call.resolve();
        } catch (Exception ex) {
            call.reject(ex.getMessage());
        }
    }
}
