import { registerPlugin } from '@capacitor/core';
const ZPrinter = registerPlugin('ZPrinter', {
    web: () => import('./web').then((m) => new m.ZPrinterWeb()),
});
export * from './definitions';
export { ZPrinter };
//# sourceMappingURL=index.js.map