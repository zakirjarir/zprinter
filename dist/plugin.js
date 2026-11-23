var capacitorZPrinter = (function (exports, core) {
    'use strict';

    const ZPrinter = core.registerPlugin('ZPrinter', {
        web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.ZPrinterWeb()),
    });

    class ZPrinterWeb extends core.WebPlugin {
        async getSavedPrinters() {
            return {};
        }
        async reconnectSavedPrinter() {
            console.warn('Web: reconnectSavedPrinter not supported');
            return {
                connected: false,
                deviceName: '',
            };
        }
        async clearSavedPrinter() {
            console.warn('Web: clearSavedPrinter not supported');
        }
        async htmlToImage() {
            throw this.unimplemented('htmlToImage is only supported natively on Android and iOS.');
        }
        async htmlToPdf() {
            throw this.unimplemented('htmlToPdf is only supported natively on Android and iOS. Use printHtmlNative on Web to Save as PDF.');
        }
        async printHtmlNative(options) {
            var _a, _b, _c;
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
                const doc = iframe.contentDocument || ((_a = iframe.contentWindow) === null || _a === void 0 ? void 0 : _a.document);
                if (doc) {
                    doc.open();
                    doc.write(options.html);
                    doc.close();
                    doc.title = jobName;
                }
                (_b = iframe.contentWindow) === null || _b === void 0 ? void 0 : _b.focus();
                (_c = iframe.contentWindow) === null || _c === void 0 ? void 0 : _c.print();
                return { printed: true, jobName };
            }
            finally {
                setTimeout(() => {
                    if (iframe.parentNode) {
                        iframe.parentNode.removeChild(iframe);
                    }
                }, 1000);
            }
        }
        async printHtmlToThermal() {
            console.warn('Web: printHtmlToThermal is only supported natively on Android and iOS.');
            return { printed: false };
        }
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
        async isBluetoothConnected() {
            return { connected: false };
        }
        async printBluetoothText(options) {
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
        async printBluetoothImage() {
            console.warn('Web: printBluetoothImage not supported');
            return { printed: false };
        }
        async printBluetoothQRCode() {
            console.warn('Web: printBluetoothQRCode not supported');
            return { printed: false };
        }
        async printBluetoothBarcode() {
            console.warn('Web: printBluetoothBarcode not supported');
            return { printed: false };
        }
        async sendRawBluetooth() {
            console.warn('Web: sendRawBluetooth not supported');
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
        async isUsbConnected() {
            return { connected: false };
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
        async printUsbBarcode() {
            console.warn('Web: printUsbBarcode not supported');
            return { printed: false };
        }
        async sendRawUsb() {
            console.warn('Web: sendRawUsb not supported');
            return { printed: false };
        }
        async kickUsbDrawer() {
            console.warn('Web: kickUsbDrawer not supported');
            return { kicked: false };
        }
        async cutUsbPaper() {
            console.warn('Web: cutUsbPaper not supported');
            return { cut: false };
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
        async isThermalConnected() {
            return { connected: false };
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
        async printThermalBarcode() {
            console.warn('Web: printThermalBarcode not supported');
            return { printed: false };
        }
        async sendRawThermal() {
            console.warn('Web: sendRawThermal not supported');
            return { printed: false };
        }
        async kickThermalDrawer() {
            console.warn('Web: kickThermalDrawer not supported');
            return { kicked: false };
        }
        async cutThermalPaper() {
            console.warn('Web: cutThermalPaper not supported');
            return { cut: false };
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
        async isNetworkConnected() {
            return { connected: false };
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
        async printNetworkBarcode() {
            console.warn('Web: printNetworkBarcode not supported');
            return { printed: false };
        }
        async sendRawNetwork() {
            console.warn('Web: sendRawNetwork not supported');
            return { printed: false };
        }
        async kickNetworkDrawer() {
            console.warn('Web: kickNetworkDrawer not supported');
            return { kicked: false };
        }
        async cutNetworkPaper() {
            console.warn('Web: cutNetworkPaper not supported');
            return { cut: false };
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

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map
