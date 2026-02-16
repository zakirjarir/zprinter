export interface ZPrinterPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
