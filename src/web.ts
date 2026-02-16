import { WebPlugin } from '@capacitor/core';
import type { ZPrinterPlugin } from './definitions';

export class ZPrinterWeb extends WebPlugin implements ZPrinterPlugin {
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
}
