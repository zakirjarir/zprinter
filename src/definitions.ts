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

  connectBluetooth(options: { address: string }): Promise<PrinterConnectionResult>;

  printBluetoothText(options: PrinterTextOptions): Promise<{
    printed: boolean;
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

  disconnectUsbPrinter(): Promise<void>;

  connectThermalPrinter(options?: UsbPrinterConnectOptions): Promise<PrinterConnectionResult>;

  printThermalText(options: PrinterTextOptions): Promise<{
    printed: boolean;
  }>;

  disconnectThermalPrinter(): Promise<void>;
}
