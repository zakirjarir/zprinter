'use strict';

var core = require('@capacitor/core');

const ZPrinter = core.registerPlugin('ZPrinter', {
    web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.ZPrinterWeb()),
});

class ZPrinterWeb extends core.WebPlugin {
    async scanBluetoothDevices() {
        console.warn('Web: scanBluetoothDevices not supported');
        return { devices: [], count: 0 };
    }
    async connectBluetooth() {
        console.warn('Web: connectBluetooth not supported');
        return {
            connected: false,
            deviceName: '',
            deviceAddress: '',
        };
    }
    async printBluetoothText(options) {
        console.log('Web Bluetooth print fallback:', options.text);
        const printWindow = window.open('', '_blank');
        if (printWindow) {
            printWindow.document.write('<pre>' + options.text + '</pre>');
            printWindow.print();
            printWindow.close();
        }
        return { printed: true };
    }
    async printBluetoothImage() {
        console.warn('Web: printBluetoothImage not supported');
        return { printed: false };
    }
    async printBluetoothQRCode() {
        console.warn('Web: printBluetoothQRCode not supported');
        return { printed: false };
    }
    async kickBluetoothDrawer() {
        console.warn('Web: kickBluetoothDrawer not supported');
        return { kicked: false };
    }
    async cutBluetoothPaper() {
        console.warn('Web: cutBluetoothPaper not supported');
        return { cut: false };
    }
    async disconnectBluetooth() {
        console.warn('Web: disconnectBluetooth not supported');
    }
    async listUsbPrinters() {
        console.warn('Web: listUsbPrinters not supported');
        return { devices: [], count: 0 };
    }
    async connectUsbPrinter() {
        console.warn('Web: connectUsbPrinter not supported');
        return {
            connected: false,
            deviceName: '',
            vendorId: 0,
            productId: 0,
        };
    }
    async printUsbText(options) {
        console.log('Web USB print fallback:', options.text);
        return { printed: true };
    }
    async printUsbImage() {
        console.warn('Web: printUsbImage not supported');
        return { printed: false };
    }
    async printUsbQRCode() {
        console.warn('Web: printUsbQRCode not supported');
        return { printed: false };
    }
    async kickUsbDrawer() {
        console.warn('Web: kickUsbDrawer not supported');
        return { kicked: false };
    }
    async disconnectUsbPrinter() {
        console.warn('Web: disconnectUsbPrinter not supported');
    }
    async connectThermalPrinter() {
        console.warn('Web: connectThermalPrinter not supported');
        return {
            connected: false,
            deviceName: '',
            vendorId: 0,
            productId: 0,
        };
    }
    async printThermalText(options) {
        console.log('Web Thermal print fallback:', options.text);
        return { printed: true };
    }
    async printThermalImage() {
        console.warn('Web: printThermalImage not supported');
        return { printed: false };
    }
    async printThermalQRCode() {
        console.warn('Web: printThermalQRCode not supported');
        return { printed: false };
    }
    async kickThermalDrawer() {
        console.warn('Web: kickThermalDrawer not supported');
        return { kicked: false };
    }
    async disconnectThermalPrinter() {
        console.warn('Web: disconnectThermalPrinter not supported');
    }
    async connectNetworkPrinter() {
        console.warn('Web: connectNetworkPrinter not supported');
        return {
            connected: false,
            deviceName: '',
        };
    }
    async printNetworkText(options) {
        console.log('Web Network print fallback:', options.text);
        return { printed: true };
    }
    async printNetworkImage() {
        console.warn('Web: printNetworkImage not supported');
        return { printed: false };
    }
    async printNetworkQRCode() {
        console.warn('Web: printNetworkQRCode not supported');
        return { printed: false };
    }
    async kickNetworkDrawer() {
        console.warn('Web: kickNetworkDrawer not supported');
        return { kicked: false };
    }
    async disconnectNetworkPrinter() {
        console.warn('Web: disconnectNetworkPrinter not supported');
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    ZPrinterWeb: ZPrinterWeb
});

exports.ZPrinter = ZPrinter;
//# sourceMappingURL=plugin.cjs.js.map
