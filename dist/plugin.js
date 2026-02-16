var capacitorZPrinter = (function (exports, core) {
    'use strict';

    const ZPrinter = core.registerPlugin('ZPrinter', {
        web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.ZPrinterWeb()),
    });

    class ZPrinterWeb extends core.WebPlugin {
        // =========================
        // Bluetooth printer
        // =========================
        async scanDevices() {
            console.warn('Web: scanDevices not supported');
            return { devices: [] };
        }
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
        async disconnect() {
            console.warn('Web: disconnect not supported');
            return;
        }
        // =========================
        // USB printer
        // =========================
        async connectUsb() {
            console.warn('Web: connectUsb not supported');
            return;
        }
        async printUsb(options) {
            console.log('Web printUsb fallback:', options.text);
            return;
        }
        async disconnectUsb() {
            console.warn('Web: disconnectUsb not supported');
            return;
        }
        // =========================
        // Thermal printer
        // =========================
        async connectThermal() {
            console.warn('Web: connectThermal not supported');
            return;
        }
        async printThermal(options) {
            console.log('Web printThermal fallback:', options.text);
            return;
        }
        async disconnectThermal() {
            console.warn('Web: disconnectThermal not supported');
            return;
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
