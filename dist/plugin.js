var capacitorZPrinter = (function (exports, core) {
    'use strict';

    const ZPrinter = core.registerPlugin('ZPrinter', {
        web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.ZPrinterWeb()),
    });

    class ZPrinterWeb extends core.WebPlugin {
        // =========================
        // Bluetooth Printer
        // =========================
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
        async cutBluetoothPaper() {
            console.warn('Web: cutBluetoothPaper not supported');
            return { cut: false };
        }
        async disconnectBluetooth() {
            console.warn('Web: disconnectBluetooth not supported');
        }
        // =========================
        // USB Printer
        // =========================
        async connectUsbPrinter() {
            console.warn('Web: connectUsbPrinter not supported');
        }
        async printUsbText(options) {
            console.log('Web USB print fallback:', options.text);
        }
        async disconnectUsbPrinter() {
            console.warn('Web: disconnectUsbPrinter not supported');
        }
        // =========================
        // Thermal Printer
        // =========================
        async connectThermalPrinter() {
            console.warn('Web: connectThermalPrinter not supported');
        }
        async printThermalText(options) {
            console.log('Web Thermal print fallback:', options.text);
        }
        async disconnectThermalPrinter() {
            console.warn('Web: disconnectThermalPrinter not supported');
        }
    }

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        ZPrinterWeb: ZPrinterWeb
    });

    exports.ZPrinter = ZPrinter;

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
