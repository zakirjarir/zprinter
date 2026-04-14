import { WebPlugin } from '@capacitor/core';

import type { ZPrinterPlugin } from './definitions';

export class ZPrinterWeb extends WebPlugin implements ZPrinterPlugin {
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

  async listUsbPrinters(): Promise<{
    devices: {
      deviceName: string;
      vendorId: number;
      productId: number;
    }[];
    count: number;
  }> {
    console.warn('Web: listUsbPrinters not supported');
    return { devices: [], count: 0 };
  }

  async connectUsbPrinter(): Promise<{
    connected: boolean;
    deviceName: string;
  }> {
    console.warn('Web: connectUsbPrinter not supported');
    return {
      connected: false,
      deviceName: '',
    };
  }

  async printUsbText(options: { text: string }): Promise<{ printed: boolean }> {
    console.log('Web USB print fallback:', options.text);
    return { printed: true };
  }

  async disconnectUsbPrinter(): Promise<void> {
    console.warn('Web: disconnectUsbPrinter not supported');
  }

  async connectThermalPrinter(): Promise<{
    connected: boolean;
    deviceName: string;
  }> {
    console.warn('Web: connectThermalPrinter not supported');
    return {
      connected: false,
      deviceName: '',
    };
  }

  async printThermalText(options: { text: string }): Promise<{ printed: boolean }> {
    console.log('Web Thermal print fallback:', options.text);
    return { printed: true };
  }

  async disconnectThermalPrinter(): Promise<void> {
    console.warn('Web: disconnectThermalPrinter not supported');
  }
}
