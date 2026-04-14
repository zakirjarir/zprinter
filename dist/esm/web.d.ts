import { WebPlugin } from '@capacitor/core';
import type { ZPrinterPlugin } from './definitions';
export declare class ZPrinterWeb extends WebPlugin implements ZPrinterPlugin {
    scanBluetoothDevices(): Promise<{
        devices: {
            name: string;
            address: string;
        }[];
        count: number;
    }>;
    connectBluetooth(): Promise<{
        connected: boolean;
        deviceName: string;
        deviceAddress: string;
    }>;
    printBluetoothText(options: {
        text: string;
    }): Promise<{
        printed: boolean;
    }>;
    cutBluetoothPaper(): Promise<{
        cut: boolean;
    }>;
    disconnectBluetooth(): Promise<void>;
    listUsbPrinters(): Promise<{
        devices: {
            deviceName: string;
            vendorId: number;
            productId: number;
        }[];
        count: number;
    }>;
    connectUsbPrinter(): Promise<{
        connected: boolean;
        deviceName: string;
    }>;
    printUsbText(options: {
        text: string;
    }): Promise<{
        printed: boolean;
    }>;
    disconnectUsbPrinter(): Promise<void>;
    connectThermalPrinter(): Promise<{
        connected: boolean;
        deviceName: string;
    }>;
    printThermalText(options: {
        text: string;
    }): Promise<{
        printed: boolean;
    }>;
    disconnectThermalPrinter(): Promise<void>;
}
