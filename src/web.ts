import { WebPlugin } from '@capacitor/core';

import type {
  BluetoothPrinterDevice,
  HtmlNativePrintOptions,
  HtmlToImageResult,
  HtmlToPdfResult,
  PrinterConnectionResult,
  PrinterStatusResult,
  PrinterTextOptions,
  SavedPrintersResult,
  UsbPrinterDevice,
  ZPrinterPlugin,
} from './definitions';

export class ZPrinterWeb extends WebPlugin implements ZPrinterPlugin {
  async getSavedPrinters(): Promise<SavedPrintersResult> {
    return {};
  }

  async reconnectSavedPrinter(): Promise<PrinterConnectionResult> {
    console.warn('Web: reconnectSavedPrinter not supported');
    return {
      connected: false,
      deviceName: '',
    };
  }

  async clearSavedPrinter(): Promise<void> {
    console.warn('Web: clearSavedPrinter not supported');
  }

  async htmlToImage(): Promise<HtmlToImageResult> {
    throw this.unimplemented('htmlToImage is only supported natively on Android and iOS.');
  }

  async htmlToPdf(): Promise<HtmlToPdfResult> {
    throw this.unimplemented(
      'htmlToPdf is only supported natively on Android and iOS. Use printHtmlNative on Web to Save as PDF.',
    );
  }

  async printHtmlNative(options: HtmlNativePrintOptions): Promise<{ printed: boolean; jobName: string }> {
    const jobName = options.jobName || 'ZPrinter Document';
    const iframe = document.createElement('iframe');
    iframe.style.position = 'fixed';
    iframe.style.right = '0';
    iframe.style.bottom = '0';
    iframe.style.width = '0';
    iframe.style.height = '0';
    iframe.style.border = '0';
    document.body.appendChild(iframe);

    try {
      const doc = iframe.contentDocument || iframe.contentWindow?.document;
      if (doc) {
        doc.open();
        doc.write(options.html);
        doc.close();
        doc.title = jobName;
      }
      iframe.contentWindow?.focus();
      iframe.contentWindow?.print();
      return { printed: true, jobName };
    } finally {
      setTimeout(() => {
        if (iframe.parentNode) {
          iframe.parentNode.removeChild(iframe);
        }
      }, 1000);
    }
  }

  async printHtmlToThermal(): Promise<{ printed: boolean }> {
    console.warn('Web: printHtmlToThermal is only supported natively on Android and iOS.');
    return { printed: false };
  }

  async scanBluetoothDevices(): Promise<{
    devices: BluetoothPrinterDevice[];
    count: number;
  }> {
    console.warn('Web: scanBluetoothDevices not supported');
    return { devices: [], count: 0 };
  }

  async connectBluetooth(): Promise<PrinterConnectionResult> {
    console.warn('Web: connectBluetooth not supported');
    return {
      connected: false,
      deviceName: '',
      deviceAddress: '',
    };
  }

  async isBluetoothConnected(): Promise<PrinterStatusResult> {
    return { connected: false };
  }

  async printBluetoothText(options: PrinterTextOptions): Promise<{ printed: boolean }> {
    console.log('Web Bluetooth print fallback:', options.text);

    const printWindow = window.open('', '_blank');
    if (printWindow) {
      const pre = printWindow.document.createElement('pre');
      pre.textContent = options.text;
      printWindow.document.body.appendChild(pre);
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

  async printBluetoothBarcode(): Promise<{ printed: boolean }> {
    console.warn('Web: printBluetoothBarcode not supported');
    return { printed: false };
  }

  async sendRawBluetooth(): Promise<{ printed: boolean }> {
    console.warn('Web: sendRawBluetooth not supported');
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
    devices: UsbPrinterDevice[];
    count: number;
  }> {
    console.warn('Web: listUsbPrinters not supported');
    return { devices: [], count: 0 };
  }

  async connectUsbPrinter(): Promise<PrinterConnectionResult> {
    console.warn('Web: connectUsbPrinter not supported');
    return {
      connected: false,
      deviceName: '',
      vendorId: 0,
      productId: 0,
    };
  }

  async isUsbConnected(): Promise<PrinterStatusResult> {
    return { connected: false };
  }

  async printUsbText(options: PrinterTextOptions): Promise<{ printed: boolean }> {
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

  async printUsbBarcode(): Promise<{ printed: boolean }> {
    console.warn('Web: printUsbBarcode not supported');
    return { printed: false };
  }

  async sendRawUsb(): Promise<{ printed: boolean }> {
    console.warn('Web: sendRawUsb not supported');
    return { printed: false };
  }

  async kickUsbDrawer(): Promise<{ kicked: boolean }> {
    console.warn('Web: kickUsbDrawer not supported');
    return { kicked: false };
  }

  async cutUsbPaper(): Promise<{ cut: boolean }> {
    console.warn('Web: cutUsbPaper not supported');
    return { cut: false };
  }

  async disconnectUsbPrinter(): Promise<void> {
    console.warn('Web: disconnectUsbPrinter not supported');
  }

  async connectThermalPrinter(): Promise<PrinterConnectionResult> {
    console.warn('Web: connectThermalPrinter not supported');
    return {
      connected: false,
      deviceName: '',
      vendorId: 0,
      productId: 0,
    };
  }

  async isThermalConnected(): Promise<PrinterStatusResult> {
    return { connected: false };
  }

  async printThermalText(options: PrinterTextOptions): Promise<{ printed: boolean }> {
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

  async printThermalBarcode(): Promise<{ printed: boolean }> {
    console.warn('Web: printThermalBarcode not supported');
    return { printed: false };
  }

  async sendRawThermal(): Promise<{ printed: boolean }> {
    console.warn('Web: sendRawThermal not supported');
    return { printed: false };
  }

  async kickThermalDrawer(): Promise<{ kicked: boolean }> {
    console.warn('Web: kickThermalDrawer not supported');
    return { kicked: false };
  }

  async cutThermalPaper(): Promise<{ cut: boolean }> {
    console.warn('Web: cutThermalPaper not supported');
    return { cut: false };
  }

  async disconnectThermalPrinter(): Promise<void> {
    console.warn('Web: disconnectThermalPrinter not supported');
  }

  async connectNetworkPrinter(): Promise<PrinterConnectionResult> {
    console.warn('Web: connectNetworkPrinter not supported');
    return {
      connected: false,
      deviceName: '',
    };
  }

  async isNetworkConnected(): Promise<PrinterStatusResult> {
    return { connected: false };
  }

  async printNetworkText(options: PrinterTextOptions): Promise<{ printed: boolean }> {
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

  async printNetworkBarcode(): Promise<{ printed: boolean }> {
    console.warn('Web: printNetworkBarcode not supported');
    return { printed: false };
  }

  async sendRawNetwork(): Promise<{ printed: boolean }> {
    console.warn('Web: sendRawNetwork not supported');
    return { printed: false };
  }

  async kickNetworkDrawer(): Promise<{ kicked: boolean }> {
    console.warn('Web: kickNetworkDrawer not supported');
    return { kicked: false };
  }

  async cutNetworkPaper(): Promise<{ cut: boolean }> {
    console.warn('Web: cutNetworkPaper not supported');
    return { cut: false };
  }

  async disconnectNetworkPrinter(): Promise<void> {
    console.warn('Web: disconnectNetworkPrinter not supported');
  }
}
