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

  async printBluetoothImage(): Promise<{ printed: boolean }> {
    console.warn('Web: printBluetoothImage not supported');
    return { printed: false };
  }

  async printBluetoothQRCode(): Promise<{ printed: boolean }> {
    console.warn('Web: printBluetoothQRCode not supported');
    return { printed: false };
  }

  async kickBluetoothDrawer(): Promise<{ kicked: boolean }> {
    console.warn('Web: kickBluetoothDrawer not supported');
    return { kicked: false };
  }

  async cutBluetoothPaper(): Promise<{ cut: boolean }> {
    console.warn('Web: cutBluetoothPaper not supported');
    return { cut: false };
  }

  async disconnectBluetooth(): Promise<void> {
    console.warn('Web: disconnectBluetooth not supported');
  }

  async listUsbPrinters(): Promise<{
    devices: any[];
    count: number;
  }> {
    console.warn('Web: listUsbPrinters not supported');
    return { devices: [], count: 0 };
  }

  async connectUsbPrinter(): Promise<{
    connected: boolean;
    deviceName: string;
    vendorId: number;
    productId: number;
  }> {
    console.warn('Web: connectUsbPrinter not supported');
    return {
      connected: false,
      deviceName: '',
      vendorId: 0,
      productId: 0,
    };
  }

  async printUsbText(options: { text: string }): Promise<{ printed: boolean }> {
    console.log('Web USB print fallback:', options.text);
    return { printed: true };
  }

  async printUsbImage(): Promise<{ printed: boolean }> {
    console.warn('Web: printUsbImage not supported');
    return { printed: false };
  }

  async printUsbQRCode(): Promise<{ printed: boolean }> {
    console.warn('Web: printUsbQRCode not supported');
    return { printed: false };
  }

  async kickUsbDrawer(): Promise<{ kicked: boolean }> {
    console.warn('Web: kickUsbDrawer not supported');
    return { kicked: false };
  }

  async disconnectUsbPrinter(): Promise<void> {
    console.warn('Web: disconnectUsbPrinter not supported');
  }

  async connectThermalPrinter(): Promise<{
    connected: boolean;
    deviceName: string;
    vendorId: number;
    productId: number;
  }> {
    console.warn('Web: connectThermalPrinter not supported');
    return {
      connected: false,
      deviceName: '',
      vendorId: 0,
      productId: 0,
    };
  }

  async printThermalText(options: { text: string }): Promise<{ printed: boolean }> {
    console.log('Web Thermal print fallback:', options.text);
    return { printed: true };
  }

  async printThermalImage(): Promise<{ printed: boolean }> {
    console.warn('Web: printThermalImage not supported');
    return { printed: false };
  }

  async printThermalQRCode(): Promise<{ printed: boolean }> {
    console.warn('Web: printThermalQRCode not supported');
    return { printed: false };
  }

  async kickThermalDrawer(): Promise<{ kicked: boolean }> {
    console.warn('Web: kickThermalDrawer not supported');
    return { kicked: false };
  }

  async disconnectThermalPrinter(): Promise<void> {
    console.warn('Web: disconnectThermalPrinter not supported');
  }

  async connectNetworkPrinter(): Promise<{
    connected: boolean;
    deviceName: string;
  }> {
    console.warn('Web: connectNetworkPrinter not supported');
    return {
      connected: false,
      deviceName: '',
    };
  }

  async printNetworkText(options: { text: string }): Promise<{ printed: boolean }> {
    console.log('Web Network print fallback:', options.text);
    return { printed: true };
  }

  async printNetworkImage(): Promise<{ printed: boolean }> {
    console.warn('Web: printNetworkImage not supported');
    return { printed: false };
  }

  async printNetworkQRCode(): Promise<{ printed: boolean }> {
    console.warn('Web: printNetworkQRCode not supported');
    return { printed: false };
  }

  async kickNetworkDrawer(): Promise<{ kicked: boolean }> {
    console.warn('Web: kickNetworkDrawer not supported');
    return { kicked: false };
  }

  async disconnectNetworkPrinter(): Promise<void> {
    console.warn('Web: disconnectNetworkPrinter not supported');
  }
}
