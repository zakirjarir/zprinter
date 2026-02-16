export interface ZPrinterPlugin {
  // =========================
  // Bluetooth Printer
  // =========================
  scanDevices(): Promise<{ devices: { name: string; address: string }[] }>;
  connect(options: { address: string }): Promise<{ connected: boolean; deviceName?: string; deviceAddress?: string }>;
  printText(options: { text: string }): Promise<{ printed: boolean }>;
  cut(): Promise<{ cut: boolean }>;
  disconnect(): Promise<void>;

  // =========================
  // USB Printer
  // =========================
  connectUsb(): Promise<void>;
  printUsb(options: { text: string }): Promise<void>;
  disconnectUsb(): Promise<void>;

  // =========================
  // Thermal Printer
  // =========================
  connectThermal(): Promise<void>;
  printThermal(options: { text: string }): Promise<void>;
  disconnectThermal(): Promise<void>;
}
