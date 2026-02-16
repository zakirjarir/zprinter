'use strict';

var core = require('@capacitor/core');

const ZPrinter = core.registerPlugin('ZPrinter', {
    web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.ZPrinterWeb()),
});

class ZPrinterWeb extends core.WebPlugin {
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

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    ZPrinterWeb: ZPrinterWeb
});

exports.ZPrinter = ZPrinter;
//# sourceMappingURL=plugin.cjs.js.map
