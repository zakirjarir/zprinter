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
    connectUsbPrinter(): Promise<void>;
    printUsbText(options: {
        text: string;
    }): Promise<void>;
    disconnectUsbPrinter(): Promise<void>;
    connectThermalPrinter(): Promise<void>;
    printThermalText(options: {
        text: string;
    }): Promise<void>;
    disconnectThermalPrinter(): Promise<void>;
}
