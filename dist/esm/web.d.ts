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
    printBluetoothImage(): Promise<{
        printed: boolean;
    }>;
    printBluetoothQRCode(): Promise<{
        printed: boolean;
    }>;
    kickBluetoothDrawer(): Promise<{
        kicked: boolean;
    }>;
    cutBluetoothPaper(): Promise<{
        cut: boolean;
    }>;
    disconnectBluetooth(): Promise<void>;
    listUsbPrinters(): Promise<{
        devices: any[];
        count: number;
    }>;
    connectUsbPrinter(): Promise<{
        connected: boolean;
        deviceName: string;
        vendorId: number;
        productId: number;
    }>;
    printUsbText(options: {
        text: string;
    }): Promise<{
        printed: boolean;
    }>;
    printUsbImage(): Promise<{
        printed: boolean;
    }>;
    printUsbQRCode(): Promise<{
        printed: boolean;
    }>;
    kickUsbDrawer(): Promise<{
        kicked: boolean;
    }>;
    disconnectUsbPrinter(): Promise<void>;
    connectThermalPrinter(): Promise<{
        connected: boolean;
        deviceName: string;
        vendorId: number;
        productId: number;
    }>;
    printThermalText(options: {
        text: string;
    }): Promise<{
        printed: boolean;
    }>;
    printThermalImage(): Promise<{
        printed: boolean;
    }>;
    printThermalQRCode(): Promise<{
        printed: boolean;
    }>;
    kickThermalDrawer(): Promise<{
        kicked: boolean;
    }>;
    disconnectThermalPrinter(): Promise<void>;
    connectNetworkPrinter(): Promise<{
        connected: boolean;
        deviceName: string;
    }>;
    printNetworkText(options: {
        text: string;
    }): Promise<{
        printed: boolean;
    }>;
    printNetworkImage(): Promise<{
        printed: boolean;
    }>;
    printNetworkQRCode(): Promise<{
        printed: boolean;
    }>;
    kickNetworkDrawer(): Promise<{
        kicked: boolean;
    }>;
    disconnectNetworkPrinter(): Promise<void>;
}
