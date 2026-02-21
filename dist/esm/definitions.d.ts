export interface ZPrinterPlugin {
    scanBluetoothDevices(): Promise<{
        devices: {
            name: string;
            address: string;
        }[];
        count: number;
    }>;
    connectBluetooth(options: {
        address: string;
    }): Promise<{
        connected: boolean;
        deviceName: string;
        deviceAddress: string;
    }>;
    printBluetoothText(options: {
        text: string;
        fontSize?: number;
        align?: 'left' | 'center' | 'right';
        isBold?: boolean;
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
