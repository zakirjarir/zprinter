import { WebPlugin } from '@capacitor/core';
import type { ZPrinterPlugin } from './definitions';

export class ZPrinterWeb extends WebPlugin implements ZPrinterPlugin {
  // =========================
  // Bluetooth Printer
  // =========================

  async scanBluetoothDevices(): Promise<{
    devices: { name: string; address: string }[];
    count: number;
  }> {
    console.warn('Web: scanBluetoothDevices not supported');
    return { devices: [], count: 0 };
  }

  async connectBluetooth(): Promise<{
    connected: boolean;
    deviceName: string;
    deviceAddress: string;
  }> {
    console.warn('Web: connectBluetooth not supported');
    return {
      connected: false,
      deviceName: '',
      deviceAddress: '',
    };
  }

  async printBluetoothText(options: { text: string }): Promise<{ printed: boolean }> {
    console.log('Web Bluetooth print fallback:', options.text);

    const printWindow = window.open('', '_blank');
    if (printWindow) {
      printWindow.document.write('<pre>' + options.text + '</pre>');
      printWindow.print();
      printWindow.close();
    }

    return { printed: true };
  }

  async cutBluetoothPaper(): Promise<{ cut: boolean }> {
    console.warn('Web: cutBluetoothPaper not supported');
    return { cut: false };
  }

  async disconnectBluetooth(): Promise<void> {
    console.warn('Web: disconnectBluetooth not supported');
  }

  // =========================
  // USB Printer
  // =========================

  async connectUsbPrinter(): Promise<void> {
    console.warn('Web: connectUsbPrinter not supported');
  }

  async printUsbText(options: { text: string }): Promise<void> {
    console.log('Web USB print fallback:', options.text);
  }

  async disconnectUsbPrinter(): Promise<void> {
    console.warn('Web: disconnectUsbPrinter not supported');
  }

  // =========================
  // Thermal Printer
  // =========================

  async connectThermalPrinter(): Promise<void> {
    console.warn('Web: connectThermalPrinter not supported');
  }

  async printThermalText(options: { text: string }): Promise<void> {
    console.log('Web Thermal print fallback:', options.text);
  }

  async disconnectThermalPrinter(): Promise<void> {
    console.warn('Web: disconnectThermalPrinter not supported');
  }
}
