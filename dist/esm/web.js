import { WebPlugin } from '@capacitor/core';
export class ZPrinterWeb extends WebPlugin {
    async connect() {
        console.warn('Web: connect not supported for classic Bluetooth printers');
        return { connected: false };
    }
    async printText(options) {
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
    async cut() {
        console.warn('Web: cut not supported');
        return { cut: false };
    }
}
//# sourceMappingURL=web.js.map