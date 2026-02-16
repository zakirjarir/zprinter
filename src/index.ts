import { registerPlugin } from '@capacitor/core';
import type { ZPrinterPlugin } from './definitions';

const ZPrinter = registerPlugin<ZPrinterPlugin>('ZPrinter', {
  web: () => import('./web').then((m) => new m.ZPrinterWeb()),
});

export * from './definitions';
export { ZPrinter };
