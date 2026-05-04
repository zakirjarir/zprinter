export interface BluetoothPrinterDevice {
    name: string;
    address: string;
    isPaired?: boolean;
}
export interface UsbPrinterDevice {
    deviceName: string;
    vendorId: number;
    productId: number;
    manufacturerName?: string;
    productName?: string;
    deviceClass?: number;
}
export interface PrinterConnectionResult {
    connected: boolean;
    deviceName: string;
    deviceAddress?: string;
    vendorId?: number;
    productId?: number;
}
export interface PrinterTextOptions {
    text: string;
    fontSize?: number;
    align?: 'left' | 'center' | 'right';
    isBold?: boolean;
    feedLines?: number;
}
export interface PrinterImageOptions {
    base64: string;
    width?: number;
    height?: number;
    align?: 'left' | 'center' | 'right';
}
export interface PrinterQRCodeOptions {
    data: string;
    size?: number;
    align?: 'left' | 'center' | 'right';
}
export interface NetworkPrinterConnectOptions {
    address: string;
    port?: number;
}
export interface UsbPrinterConnectOptions {
    vendorId?: number;
    productId?: number;
    deviceName?: string;
}
export interface ZPrinterPlugin {
    scanBluetoothDevices(): Promise<{
        devices: BluetoothPrinterDevice[];
        count: number;
    }>;
    connectBluetooth(options: {
        address: string;
    }): Promise<PrinterConnectionResult>;
    printBluetoothText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printBluetoothImage(options: PrinterImageOptions): Promise<{
        printed: boolean;
    }>;
    printBluetoothQRCode(options: PrinterQRCodeOptions): Promise<{
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
    connectUsbPrinter(options?: UsbPrinterConnectOptions): Promise<PrinterConnectionResult>;
    printUsbText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printUsbImage(options: PrinterImageOptions): Promise<{
        printed: boolean;
    }>;
    printUsbQRCode(options: PrinterQRCodeOptions): Promise<{
        printed: boolean;
    }>;
    kickUsbDrawer(): Promise<{
        kicked: boolean;
    }>;
    disconnectUsbPrinter(): Promise<void>;
    connectThermalPrinter(options?: UsbPrinterConnectOptions): Promise<PrinterConnectionResult>;
    printThermalText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printThermalImage(options: PrinterImageOptions): Promise<{
        printed: boolean;
    }>;
    printThermalQRCode(options: PrinterQRCodeOptions): Promise<{
        printed: boolean;
    }>;
    kickThermalDrawer(): Promise<{
        kicked: boolean;
    }>;
    disconnectThermalPrinter(): Promise<void>;
    connectNetworkPrinter(options: NetworkPrinterConnectOptions): Promise<PrinterConnectionResult>;
    printNetworkText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printNetworkImage(options: PrinterImageOptions): Promise<{
        printed: boolean;
    }>;
    printNetworkQRCode(options: PrinterQRCodeOptions): Promise<{
        printed: boolean;
    }>;
    kickNetworkDrawer(): Promise<{
        kicked: boolean;
    }>;
    disconnectNetworkPrinter(): Promise<void>;
}
