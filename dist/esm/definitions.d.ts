export type PrinterType = 'bluetooth' | 'usb' | 'thermal' | 'network';
export interface BluetoothPrinterDevice {
    name: string;
    address: string;
    isPaired?: boolean;
    rssi?: number;
}
export interface UsbPrinterDevice {
    deviceName: string;
    vendorId: number;
    productId: number;
    manufacturerName?: string;
    productName?: string;
    deviceClass?: number;
}
export interface PrinterConnectionResult {
    connected: boolean;
    deviceName: string;
    deviceAddress?: string;
    vendorId?: number;
    productId?: number;
}
export interface PrinterStatusResult {
    connected: boolean;
    deviceName?: string;
    deviceAddress?: string;
}
export interface SavedBluetoothPrinter {
    address: string;
    deviceName: string;
}
export interface SavedUsbPrinter {
    deviceName?: string;
    vendorId?: number;
    productId?: number;
}
export interface SavedNetworkPrinter {
    address: string;
    port: number;
    timeout?: number;
}
export interface SavedPrintersResult {
    bluetooth?: SavedBluetoothPrinter;
    usb?: SavedUsbPrinter;
    thermal?: SavedUsbPrinter;
    network?: SavedNetworkPrinter;
}
export interface PrinterDisconnectOptions {
    /**
     * If true, also clears the saved printer so it will not auto-reconnect on future prints (default: false).
     */
    forget?: boolean;
}
export interface PrinterTextOptions {
    text: string;
    fontSize?: number;
    align?: 'left' | 'center' | 'right';
    isBold?: boolean;
    isUnderline?: boolean;
    feedLines?: number;
    /**
     * Character encoding for legacy/international ESC/POS printers (default: 'UTF-8').
     * Examples: 'CP437', 'CP850', 'CP858', 'ISO-8859-1', 'GBK', 'UTF-8'.
     */
    encoding?: string;
    /**
     * Optional ESC/POS hardware character code table (ESC t n), e.g. 0 (PC437), 2 (PC850), 16 (WPC1252).
     */
    codePage?: number;
    /**
     * Send ESC @ reset command before printing (default: true).
     */
    reset?: boolean;
}
export interface PrinterImageOptions {
    base64: string;
    /**
     * Target width in pixels (e.g., 384 for 58mm printers, 576 for 80mm printers).
     * If only width is provided, height scales proportionally.
     */
    width?: number;
    height?: number;
    align?: 'left' | 'center' | 'right';
    /**
     * Grayscale threshold between 0 and 255 (default: 128).
     */
    threshold?: number;
    /**
     * 'raster' uses GS v 0 (default, fastest on modern printers).
     * 'bitImage' uses ESC * 33 (compatibility mode for very old ESC/POS printers).
     */
    mode?: 'raster' | 'bitImage';
    /**
     * Blank lines to feed after printing the image (default: 1).
     */
    feedLines?: number;
}
export interface PrinterQRCodeOptions {
    data: string;
    size?: number;
    align?: 'left' | 'center' | 'right';
    errorCorrection?: 'L' | 'M' | 'Q' | 'H';
    feedLines?: number;
}
export type BarcodeType = 'CODE128' | 'CODE39' | 'EAN13' | 'EAN8' | 'UPCA' | 'UPCE' | 'ITF' | 'CODABAR';
export interface PrinterBarcodeOptions {
    data: string;
    type?: BarcodeType;
    /**
     * Barcode height in dots (1-255, default: 80).
     */
    height?: number;
    /**
     * Barcode module width (2-6, default: 2).
     */
    width?: number;
    align?: 'left' | 'center' | 'right';
    /**
     * Human-readable text placement (default: 'below').
     */
    hriPosition?: 'none' | 'above' | 'below' | 'both';
    feedLines?: number;
}
export interface PrinterCutOptions {
    /**
     * Perform a partial cut instead of a full cut (default: false).
     */
    partial?: boolean;
    /**
     * Feed lines before cutting so the last line of text is not clipped (default: 3).
     */
    feedLines?: number;
}
export interface PrinterDrawerOptions {
    /**
     * Cash drawer connector pin: 0 (Pin 2, default) or 1 (Pin 5).
     */
    pin?: 0 | 1;
    /**
     * Pulse ON duration (0-255, default: 25).
     */
    onTime?: number;
    /**
     * Pulse OFF duration (0-255, default: 250).
     */
    offTime?: number;
}
export interface PrinterRawDataOptions {
    /**
     * Base64-encoded raw command bytes (ESC/POS, TSPL, ZPL, CPCL, Star, etc.).
     */
    base64?: string;
    /**
     * Array of unsigned byte values (0-255).
     */
    bytes?: number[];
    /**
     * Plain command text encoded as UTF-8 (useful for TSPL/ZPL/CPCL label printers).
     */
    text?: string;
}
export interface NetworkPrinterConnectOptions {
    address: string;
    port?: number;
    /**
     * Connection timeout in milliseconds (default: 5000).
     */
    timeout?: number;
}
export interface UsbPrinterConnectOptions {
    vendorId?: number;
    productId?: number;
    deviceName?: string;
}
export type HtmlPageSize = 'A4' | 'A5' | 'Letter' | 'Receipt58mm' | 'Receipt80mm' | 'custom';
export type HtmlPageOrientation = 'portrait' | 'landscape';
export interface HtmlToImageOptions {
    /**
     * HTML string or fragment (supports CSS, tables, logos, Bangla/Arabic/Unicode fonts).
     */
    html: string;
    /**
     * Output image width in pixels (default: 576; use 384 for 58mm thermal, 576 for 80mm thermal, 794 for A4).
     */
    width?: number;
    /**
     * Output image format: 'png' (default) or 'jpeg'.
     */
    format?: 'png' | 'jpeg';
    /**
     * JPEG compression quality 1-100 (default: 90).
     */
    quality?: number;
    /**
     * Optional base URL for resolving relative image/CSS assets.
     */
    baseUrl?: string;
    /**
     * Extra delay in milliseconds after page load before capturing (useful for web fonts/remote images, default: 150).
     */
    delayMs?: number;
}
export interface HtmlToImageResult {
    base64: string;
    dataUrl: string;
    width: number;
    height: number;
    mimeType: string;
}
export interface HtmlToPdfOptions {
    /**
     * HTML string or fragment to convert into a PDF document.
     */
    html: string;
    /**
     * Page size preset (default: 'A4'). Use 'Receipt58mm' or 'Receipt80mm' for continuous receipt PDFs.
     */
    pageSize?: HtmlPageSize;
    /**
     * Page orientation (default: 'portrait').
     */
    orientation?: HtmlPageOrientation;
    /**
     * Custom page width in points (72 pt = 1 inch), used when pageSize is 'custom'.
     */
    customWidthPt?: number;
    /**
     * Custom page height in points (72 pt = 1 inch), used when pageSize is 'custom'.
     */
    customHeightPt?: number;
    /**
     * Page margin in points on all sides (default: 24 for standard pages, 4 for receipt presets).
     */
    marginPt?: number;
    /**
     * Optional output file name (e.g., 'invoice-101.pdf'). When set or when saveToFile is true, saves PDF to app cache.
     */
    fileName?: string;
    /**
     * Whether to save the generated PDF to a local temporary/cache file and return filePath (default: false).
     */
    saveToFile?: boolean;
    /**
     * Optional base URL for resolving relative assets.
     */
    baseUrl?: string;
    /**
     * Extra delay in milliseconds after page load before rendering PDF (default: 150).
     */
    delayMs?: number;
}
export interface HtmlToPdfResult {
    base64: string;
    dataUrl: string;
    pageCount: number;
    mimeType: string;
    filePath?: string;
}
export interface HtmlNativePrintOptions {
    /**
     * HTML string or fragment to print via the OS native print dialog (Android Print Framework / iOS AirPrint).
     * Works with Wi-Fi office printers (HP, Canon, Epson, Brother), Mopria, AirPrint, and Save as PDF.
     */
    html: string;
    /**
     * Print job name shown in the OS print dialog (default: 'ZPrinter Document').
     */
    jobName?: string;
    /**
     * Suggested paper size (default: 'A4').
     */
    pageSize?: 'A4' | 'A5' | 'Letter';
    /**
     * Suggested orientation (default: 'portrait').
     */
    orientation?: HtmlPageOrientation;
    /**
     * Optional base URL for resolving relative assets.
     */
    baseUrl?: string;
}
export interface HtmlThermalPrintOptions {
    /**
     * HTML string or fragment (supports full CSS, tables, logos, and Bangla/Arabic/Unicode text).
     */
    html: string;
    /**
     * Target thermal printer transport.
     */
    printerType: PrinterType;
    /**
     * Print width in pixels (default: 384 for 58mm printers; use 576 for 80mm printers).
     */
    width?: number;
    /**
     * Alignment on the thermal roll (default: 'center').
     */
    align?: 'left' | 'center' | 'right';
    /**
     * Grayscale threshold 0-255 (default: 128).
     */
    threshold?: number;
    /**
     * 'raster' (GS v 0, default) or 'bitImage' (ESC * 33 for very old ESC/POS printers).
     */
    mode?: 'raster' | 'bitImage';
    /**
     * Blank lines to feed after printing (default: 2).
     */
    feedLines?: number;
    /**
     * Automatically cut paper after printing (default: false).
     */
    cut?: boolean;
    /**
     * Use partial cut when cut is true (default: false).
     */
    partialCut?: boolean;
    /**
     * Automatically kick cash drawer after printing (default: false).
     */
    kickDrawer?: boolean;
    /**
     * Cash drawer pin when kickDrawer is true: 0 (Pin 2, default) or 1 (Pin 5).
     */
    drawerPin?: 0 | 1;
    /**
     * Optional base URL for resolving relative assets.
     */
    baseUrl?: string;
    /**
     * Extra delay in milliseconds after HTML load before capturing (default: 150).
     */
    delayMs?: number;
}
export interface ZPrinterPlugin {
    /**
     * Returns all printers saved persistently on the device for automatic reconnection.
     */
    getSavedPrinters(): Promise<SavedPrintersResult>;
    /**
     * Explicitly reconnects to a previously saved printer.
     */
    reconnectSavedPrinter(options: {
        type: PrinterType;
    }): Promise<PrinterConnectionResult>;
    /**
     * Clears saved printer info for one transport or all transports.
     */
    clearSavedPrinter(options?: {
        type?: PrinterType | 'all';
    }): Promise<void>;
    /**
     * Natively renders an HTML structure into a PNG or JPEG Base64 image.
     */
    htmlToImage(options: HtmlToImageOptions): Promise<HtmlToImageResult>;
    /**
     * Natively converts an HTML structure into a multi-page or receipt PDF (Base64 + optional saved filePath).
     */
    htmlToPdf(options: HtmlToPdfOptions): Promise<HtmlToPdfResult>;
    /**
     * Opens the device's native print dialog (Android Print Framework / iOS AirPrint) for an HTML document.
     * Supports office/home Wi-Fi printers (HP, Canon, Epson, Brother) and Save as PDF.
     */
    printHtmlNative(options: HtmlNativePrintOptions): Promise<{
        printed: boolean;
        jobName: string;
    }>;
    /**
     * Natively renders HTML (with CSS & Bangla/Unicode fonts) to an image and prints directly to a
     * Bluetooth, USB, Thermal, or Network thermal receipt printer (with auto-reconnect, optional cut & cash drawer).
     */
    printHtmlToThermal(options: HtmlThermalPrintOptions): Promise<{
        printed: boolean;
    }>;
    scanBluetoothDevices(): Promise<{
        devices: BluetoothPrinterDevice[];
        count: number;
    }>;
    connectBluetooth(options: {
        address: string;
    }): Promise<PrinterConnectionResult>;
    isBluetoothConnected(): Promise<PrinterStatusResult>;
    printBluetoothText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printBluetoothImage(options: PrinterImageOptions): Promise<{
        printed: boolean;
    }>;
    printBluetoothQRCode(options: PrinterQRCodeOptions): Promise<{
        printed: boolean;
    }>;
    printBluetoothBarcode(options: PrinterBarcodeOptions): Promise<{
        printed: boolean;
    }>;
    sendRawBluetooth(options: PrinterRawDataOptions): Promise<{
        printed: boolean;
    }>;
    kickBluetoothDrawer(options?: PrinterDrawerOptions): Promise<{
        kicked: boolean;
    }>;
    cutBluetoothPaper(options?: PrinterCutOptions): Promise<{
        cut: boolean;
    }>;
    disconnectBluetooth(options?: PrinterDisconnectOptions): Promise<void>;
    listUsbPrinters(): Promise<{
        devices: UsbPrinterDevice[];
        count: number;
    }>;
    connectUsbPrinter(options?: UsbPrinterConnectOptions): Promise<PrinterConnectionResult>;
    isUsbConnected(): Promise<PrinterStatusResult>;
    printUsbText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printUsbImage(options: PrinterImageOptions): Promise<{
        printed: boolean;
    }>;
    printUsbQRCode(options: PrinterQRCodeOptions): Promise<{
        printed: boolean;
    }>;
    printUsbBarcode(options: PrinterBarcodeOptions): Promise<{
        printed: boolean;
    }>;
    sendRawUsb(options: PrinterRawDataOptions): Promise<{
        printed: boolean;
    }>;
    kickUsbDrawer(options?: PrinterDrawerOptions): Promise<{
        kicked: boolean;
    }>;
    cutUsbPaper(options?: PrinterCutOptions): Promise<{
        cut: boolean;
    }>;
    disconnectUsbPrinter(options?: PrinterDisconnectOptions): Promise<void>;
    connectThermalPrinter(options?: UsbPrinterConnectOptions): Promise<PrinterConnectionResult>;
    isThermalConnected(): Promise<PrinterStatusResult>;
    printThermalText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printThermalImage(options: PrinterImageOptions): Promise<{
        printed: boolean;
    }>;
    printThermalQRCode(options: PrinterQRCodeOptions): Promise<{
        printed: boolean;
    }>;
    printThermalBarcode(options: PrinterBarcodeOptions): Promise<{
        printed: boolean;
    }>;
    sendRawThermal(options: PrinterRawDataOptions): Promise<{
        printed: boolean;
    }>;
    kickThermalDrawer(options?: PrinterDrawerOptions): Promise<{
        kicked: boolean;
    }>;
    cutThermalPaper(options?: PrinterCutOptions): Promise<{
        cut: boolean;
    }>;
    disconnectThermalPrinter(options?: PrinterDisconnectOptions): Promise<void>;
    connectNetworkPrinter(options: NetworkPrinterConnectOptions): Promise<PrinterConnectionResult>;
    isNetworkConnected(): Promise<PrinterStatusResult>;
    printNetworkText(options: PrinterTextOptions): Promise<{
        printed: boolean;
    }>;
    printNetworkImage(options: PrinterImageOptions): Promise<{
        printed: boolean;
    }>;
    printNetworkQRCode(options: PrinterQRCodeOptions): Promise<{
        printed: boolean;
    }>;
    printNetworkBarcode(options: PrinterBarcodeOptions): Promise<{
        printed: boolean;
    }>;
    sendRawNetwork(options: PrinterRawDataOptions): Promise<{
        printed: boolean;
    }>;
    kickNetworkDrawer(options?: PrinterDrawerOptions): Promise<{
        kicked: boolean;
    }>;
    cutNetworkPaper(options?: PrinterCutOptions): Promise<{
        cut: boolean;
    }>;
    disconnectNetworkPrinter(options?: PrinterDisconnectOptions): Promise<void>;
}
