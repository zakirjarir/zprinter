import { WebPlugin } from '@capacitor/core';

import type { ZPrinterPlugin } from './definitions';

export class ZPrinterWeb extends WebPlugin implements ZPrinterPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
