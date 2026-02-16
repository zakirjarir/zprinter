import { WebPlugin } from '@capacitor/core';
import type { ZPrinterPlugin } from './definitions';

export class ZPrinterWeb extends WebPlugin implements ZPrinterPlugin {
  // =========================
  // Bluetooth printer
  // =========================
  async scanDevices(): Promise<{ devices: { name: string; address: string }[] }> {
    console.warn('Web: scanDevices not supported');
    return { devices: [] };
  }

  async connect(): Promise<{ connected: boolean }> {
    console.warn('Web: connect not supported for classic Bluetooth printers');
    return { connected: false };
  }

  async printText(options: { text: string }): Promise<{ printed: boolean }> {
    console.log('Web printText fallback:', options.text);
    // Browser fallback: open print dialog
    const printWindow = window.open('', '_blank');
    if (printWindow) {
      printWindow.document.write('<pre>' + options.text + '</pre>');
      printWindow.print();
      printWindow.close();
    }
    return { printed: true };
  }

  async cut(): Promise<{ cut: boolean }> {
    console.warn('Web: cut not supported');
    return { cut: false };
  }

  async disconnect(): Promise<void> {
    console.warn('Web: disconnect not supported');
    return;
  }

  // =========================
  // USB printer
  // =========================
  async connectUsb(): Promise<void> {
    console.warn('Web: connectUsb not supported');
    return;
  }

  async printUsb(options: { text: string }): Promise<void> {
    console.log('Web printUsb fallback:', options.text);
    return;
  }

  async disconnectUsb(): Promise<void> {
    console.warn('Web: disconnectUsb not supported');
    return;
  }

  // =========================
  // Thermal printer
  // =========================
  async connectThermal(): Promise<void> {
    console.warn('Web: connectThermal not supported');
    return;
  }

  async printThermal(options: { text: string }): Promise<void> {
    console.log('Web printThermal fallback:', options.text);
    return;
  }

  async disconnectThermal(): Promise<void> {
    console.warn('Web: disconnectThermal not supported');
    return;
  }
}
