import { WebPlugin } from '@capacitor/core';
import type { ZPrinterPlugin } from './definitions';
export declare class ZPrinterWeb extends WebPlugin implements ZPrinterPlugin {
    scanDevices(): Promise<{
        devices: {
            name: string;
            address: string;
        }[];
    }>;
    connect(): Promise<{
        connected: boolean;
    }>;
    printText(options: {
        text: string;
    }): Promise<{
        printed: boolean;
    }>;
    cut(): Promise<{
        cut: boolean;
    }>;
    disconnect(): Promise<void>;
}
