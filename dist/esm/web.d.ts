import { WebPlugin } from '@capacitor/core';
import type { BluetoothPrinterDevice, HtmlNativePrintOptions, HtmlToImageResult, HtmlToPdfResult, PrinterConnectionResult, PrinterStatusResult, PrinterTextOptions, SavedPrintersResult, UsbPrinterDevice, ZPrinterPlugin } from './definitions';
export declare class ZPrinterWeb extends WebPlugin implements ZPrinterPlugin {
    getSavedPrinters(): Promise<SavedPrintersResult>;
    reconnectSavedPrinter(): Promise<PrinterConnectionResult>;
    clearSavedPrinter(): Promise<void>;
    htmlToImage(): Promise<HtmlToImageResult>;
    htmlToPdf(): Promise<HtmlToPdfResult>;
    printHtmlNative(options: HtmlNativePrintOptions): Promise<{
        printed: boolean;
        jobName: string;
    }>;
    printHtmlToThermal(): Promise<{
        printed: boolean;
    }>;
    scanBluetoothDevices(): Promise<{
        devices: BluetoothPrinterDevice[];
        count: number;
    }>;
    connectBluetooth(): Promise<PrinterConnectionResult>;
    isBluetoothConnected(): Promise<PrinterStatusResult>;
    printBluetoothText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printBluetoothImage(): Promise<{
        printed: boolean;
    }>;
    printBluetoothQRCode(): Promise<{
        printed: boolean;
    }>;
    printBluetoothBarcode(): Promise<{
        printed: boolean;
    }>;
    sendRawBluetooth(): Promise<{
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
        devices: UsbPrinterDevice[];
        count: number;
    }>;
    connectUsbPrinter(): Promise<PrinterConnectionResult>;
    isUsbConnected(): Promise<PrinterStatusResult>;
    printUsbText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printUsbImage(): Promise<{
        printed: boolean;
    }>;
    printUsbQRCode(): Promise<{
        printed: boolean;
    }>;
    printUsbBarcode(): Promise<{
        printed: boolean;
    }>;
    sendRawUsb(): Promise<{
        printed: boolean;
    }>;
    kickUsbDrawer(): Promise<{
        kicked: boolean;
    }>;
    cutUsbPaper(): Promise<{
        cut: boolean;
    }>;
    disconnectUsbPrinter(): Promise<void>;
    connectThermalPrinter(): Promise<PrinterConnectionResult>;
    isThermalConnected(): Promise<PrinterStatusResult>;
    printThermalText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printThermalImage(): Promise<{
        printed: boolean;
    }>;
    printThermalQRCode(): Promise<{
        printed: boolean;
    }>;
    printThermalBarcode(): Promise<{
        printed: boolean;
    }>;
    sendRawThermal(): Promise<{
        printed: boolean;
    }>;
    kickThermalDrawer(): Promise<{
        kicked: boolean;
    }>;
    cutThermalPaper(): Promise<{
        cut: boolean;
    }>;
    disconnectThermalPrinter(): Promise<void>;
    connectNetworkPrinter(): Promise<PrinterConnectionResult>;
    isNetworkConnected(): Promise<PrinterStatusResult>;
    printNetworkText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printNetworkImage(): Promise<{
        printed: boolean;
    }>;
    printNetworkQRCode(): Promise<{
        printed: boolean;
    }>;
    printNetworkBarcode(): Promise<{
        printed: boolean;
    }>;
    sendRawNetwork(): Promise<{
        printed: boolean;
    }>;
    kickNetworkDrawer(): Promise<{
        kicked: boolean;
    }>;
    cutNetworkPaper(): Promise<{
        cut: boolean;
    }>;
    disconnectNetworkPrinter(): Promise<void>;
}
