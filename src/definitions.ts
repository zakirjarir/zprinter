export interface ZPrinterPlugin {
  connect(options: { address: string }): Promise<{ connected: boolean }>;
  printText(options: { text: string }): Promise<{ printed: boolean }>;
  cut(): Promise<{ cut: boolean }>;
}
