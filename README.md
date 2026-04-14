# zprinter

`zprinter` is a Capacitor native printer plugin for Ionic/Capacitor mobile apps.

Its main goal is simple:

- Connect Bluetooth printers
- Connect USB printers on Android
- Connect thermal receipt printers
- Print formatted text with basic ESC/POS commands
- Cut paper on supported Bluetooth receipt printers

This plugin is designed for receipt-style printing in POS, billing, shop, restaurant, pharmacy, and service apps.

## What This Plugin Supports

### Android

- Bluetooth printer scan
- Bluetooth printer connect
- Bluetooth text print
- Bluetooth paper cut
- USB printer list
- USB printer connect
- USB text print
- USB thermal printer connect
- USB thermal text print

### iOS

- Bluetooth printer scan
- Bluetooth printer connect
- Bluetooth text print
- Bluetooth disconnect

iOS does **not** provide generic USB printer access in the same way Android does.  
Because of that:

- `connectUsbPrinter()` is not supported on iOS
- `printUsbText()` is not supported on iOS
- `connectThermalPrinter()` is not supported on iOS for generic USB thermal printers
- `printThermalText()` is not supported on iOS for generic USB thermal printers

### Web

Web support is only a fallback for development.

- It is not real native printer communication
- It should not be treated as production printer support

## Best Use Case

This plugin is best suited for:

- Bluetooth receipt printers
- ESC/POS thermal printers
- Android USB receipt printers through OTG

This plugin is **not** a full enterprise driver stack for every printer brand and every printing protocol.

If your printer uses:

- ESC/POS
- simple text printing
- standard Bluetooth SPP
- Android USB bulk transfer

then this plugin is a practical fit.

## Install

```bash
npm install zakirjarir/zprinter
npx cap sync
```

## Required Native Setup

### Android

In most Capacitor projects, the plugin permissions are merged automatically when you sync the app.

This plugin uses Bluetooth permissions and USB host support.

Important notes:

- Android 12+ needs runtime Bluetooth permissions
- For USB printing, the Android device usually needs OTG support
- The printer must be connected physically through OTG for USB printing

### iOS

For Bluetooth printing on iOS, your app should contain Bluetooth usage descriptions in `Info.plist`.

Example:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app uses Bluetooth to discover and print to receipt printers.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app uses Bluetooth to discover and print to receipt printers.</string>
```

## Import

```ts
import { ZPrinter } from 'zprinter';
```

## Typical Workflow

### Bluetooth printer workflow

1. Scan devices
2. Let the user select one device
3. Connect to that device
4. Send print text
5. Optionally cut paper
6. Disconnect when done

### USB printer workflow on Android

1. List USB printers
2. Let the user choose one printer
3. Connect to that printer
4. Print text
5. Disconnect when done

## Quick Example

### Bluetooth example

```ts
import { ZPrinter } from 'zprinter';

async function printBluetoothReceipt() {
  const scanned = await ZPrinter.scanBluetoothDevices();

  if (!scanned.devices.length) {
    throw new Error('No Bluetooth printer found');
  }

  const printer = scanned.devices[0];

  await ZPrinter.connectBluetooth({
    address: printer.address,
  });

  await ZPrinter.printBluetoothText({
    text: 'Invoice #1001\nTotal: 500 BDT\nThank you',
    fontSize: 24,
    align: 'left',
    isBold: false,
    feedLines: 2,
  });

  await ZPrinter.cutBluetoothPaper();
  await ZPrinter.disconnectBluetooth();
}
```

### USB example on Android

```ts
import { ZPrinter } from 'zprinter';

async function printUsbReceipt() {
  const printers = await ZPrinter.listUsbPrinters();

  if (!printers.devices.length) {
    throw new Error('No USB printer found');
  }

  const printer = printers.devices[0];

  await ZPrinter.connectUsbPrinter({
    vendorId: printer.vendorId,
    productId: printer.productId,
    deviceName: printer.deviceName,
  });

  await ZPrinter.printUsbText({
    text: 'USB Invoice\nAmount: 250 BDT',
    fontSize: 24,
    align: 'left',
    isBold: false,
    feedLines: 2,
  });

  await ZPrinter.disconnectUsbPrinter();
}
```

### Thermal printer example on Android

```ts
import { ZPrinter } from 'zprinter';

async function printThermalReceipt() {
  const printers = await ZPrinter.listUsbPrinters();

  if (!printers.devices.length) {
    throw new Error('No thermal printer found');
  }

  const printer = printers.devices[0];

  await ZPrinter.connectThermalPrinter({
    vendorId: printer.vendorId,
    productId: printer.productId,
    deviceName: printer.deviceName,
  });

  await ZPrinter.printThermalText({
    text: 'Thermal Receipt\nItem A  100\nItem B  200\nTotal   300',
    fontSize: 24,
    align: 'left',
    isBold: false,
    feedLines: 3,
  });

  await ZPrinter.disconnectThermalPrinter();
}
```

## API Overview

Below is the generated API reference from the TypeScript definitions.

## API

<docgen-index>

* [`scanBluetoothDevices()`](#scanbluetoothdevices)
* [`connectBluetooth(...)`](#connectbluetooth)
* [`printBluetoothText(...)`](#printbluetoothtext)
* [`cutBluetoothPaper()`](#cutbluetoothpaper)
* [`disconnectBluetooth()`](#disconnectbluetooth)
* [`listUsbPrinters()`](#listusbprinters)
* [`connectUsbPrinter(...)`](#connectusbprinter)
* [`printUsbText(...)`](#printusbtext)
* [`disconnectUsbPrinter()`](#disconnectusbprinter)
* [`connectThermalPrinter(...)`](#connectthermalprinter)
* [`printThermalText(...)`](#printthermaltext)
* [`disconnectThermalPrinter()`](#disconnectthermalprinter)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### scanBluetoothDevices()

```typescript
scanBluetoothDevices() => Promise<{ devices: BluetoothPrinterDevice[]; count: number; }>
```

**Returns:** <code>Promise&lt;{ devices: BluetoothPrinterDevice[]; count: number; }&gt;</code>

--------------------


### connectBluetooth(...)

```typescript
connectBluetooth(options: { address: string; }) => Promise<PrinterConnectionResult>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;<a href="#printerconnectionresult">PrinterConnectionResult</a>&gt;</code>

--------------------


### printBluetoothText(...)

```typescript
printBluetoothText(options: PrinterTextOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code><a href="#printertextoptions">PrinterTextOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### cutBluetoothPaper()

```typescript
cutBluetoothPaper() => Promise<{ cut: boolean; }>
```

**Returns:** <code>Promise&lt;{ cut: boolean; }&gt;</code>

--------------------


### disconnectBluetooth()

```typescript
disconnectBluetooth() => Promise<void>
```

--------------------


### listUsbPrinters()

```typescript
listUsbPrinters() => Promise<{ devices: UsbPrinterDevice[]; count: number; }>
```

**Returns:** <code>Promise&lt;{ devices: UsbPrinterDevice[]; count: number; }&gt;</code>

--------------------


### connectUsbPrinter(...)

```typescript
connectUsbPrinter(options?: UsbPrinterConnectOptions | undefined) => Promise<PrinterConnectionResult>
```

| Param         | Type                                                                          |
| ------------- | ----------------------------------------------------------------------------- |
| **`options`** | <code><a href="#usbprinterconnectoptions">UsbPrinterConnectOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#printerconnectionresult">PrinterConnectionResult</a>&gt;</code>

--------------------


### printUsbText(...)

```typescript
printUsbText(options: PrinterTextOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code><a href="#printertextoptions">PrinterTextOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### disconnectUsbPrinter()

```typescript
disconnectUsbPrinter() => Promise<void>
```

--------------------


### connectThermalPrinter(...)

```typescript
connectThermalPrinter(options?: UsbPrinterConnectOptions | undefined) => Promise<PrinterConnectionResult>
```

| Param         | Type                                                                          |
| ------------- | ----------------------------------------------------------------------------- |
| **`options`** | <code><a href="#usbprinterconnectoptions">UsbPrinterConnectOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#printerconnectionresult">PrinterConnectionResult</a>&gt;</code>

--------------------


### printThermalText(...)

```typescript
printThermalText(options: PrinterTextOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code><a href="#printertextoptions">PrinterTextOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### disconnectThermalPrinter()

```typescript
disconnectThermalPrinter() => Promise<void>
```

--------------------


### Interfaces


#### BluetoothPrinterDevice

| Prop           | Type                 |
| -------------- | -------------------- |
| **`name`**     | <code>string</code>  |
| **`address`**  | <code>string</code>  |
| **`isPaired`** | <code>boolean</code> |


#### PrinterConnectionResult

| Prop                | Type                 |
| ------------------- | -------------------- |
| **`connected`**     | <code>boolean</code> |
| **`deviceName`**    | <code>string</code>  |
| **`deviceAddress`** | <code>string</code>  |
| **`vendorId`**      | <code>number</code>  |
| **`productId`**     | <code>number</code>  |


#### PrinterTextOptions

| Prop            | Type                                       |
| --------------- | ------------------------------------------ |
| **`text`**      | <code>string</code>                        |
| **`fontSize`**  | <code>number</code>                        |
| **`align`**     | <code>'left' \| 'center' \| 'right'</code> |
| **`isBold`**    | <code>boolean</code>                       |
| **`feedLines`** | <code>number</code>                        |


#### UsbPrinterDevice

| Prop                   | Type                |
| ---------------------- | ------------------- |
| **`deviceName`**       | <code>string</code> |
| **`vendorId`**         | <code>number</code> |
| **`productId`**        | <code>number</code> |
| **`manufacturerName`** | <code>string</code> |
| **`productName`**      | <code>string</code> |
| **`deviceClass`**      | <code>number</code> |


#### UsbPrinterConnectOptions

| Prop             | Type                |
| ---------------- | ------------------- |
| **`vendorId`**   | <code>number</code> |
| **`productId`**  | <code>number</code> |
| **`deviceName`** | <code>string</code> |

</docgen-api>

## Function Usage Notes

### `scanBluetoothDevices()`

Use this first before `connectBluetooth()`.

Example:

```ts
const result = await ZPrinter.scanBluetoothDevices();
console.log(result.devices);
```

### `connectBluetooth()`

Pass the `address` returned by `scanBluetoothDevices()`.

Example:

```ts
await ZPrinter.connectBluetooth({
  address: selectedPrinter.address,
});
```

### `printBluetoothText()`

Use this after a successful Bluetooth connection.

Example:

```ts
await ZPrinter.printBluetoothText({
  text: 'Hello printer',
  fontSize: 24,
  align: 'center',
  isBold: true,
  feedLines: 2,
});
```

### `listUsbPrinters()`

Use this on Android to find available USB printers before connecting.

Example:

```ts
const usb = await ZPrinter.listUsbPrinters();
console.log(usb.devices);
```

### `connectUsbPrinter()`

Use the selected USB device values for better matching.

Example:

```ts
await ZPrinter.connectUsbPrinter({
  vendorId: printer.vendorId,
  productId: printer.productId,
  deviceName: printer.deviceName,
});
```

### `printUsbText()`

Print after connecting the USB printer.

```ts
await ZPrinter.printUsbText({
  text: 'USB print test',
  fontSize: 24,
  align: 'left',
  isBold: false,
  feedLines: 2,
});
```

### `connectThermalPrinter()` and `printThermalText()`

These are Android USB thermal-printer helpers.

Example:

```ts
await ZPrinter.connectThermalPrinter({
  vendorId: printer.vendorId,
  productId: printer.productId,
});

await ZPrinter.printThermalText({
  text: 'Thermal print test',
  fontSize: 24,
  align: 'left',
  isBold: false,
  feedLines: 3,
});
```

## Error Handling Example

```ts
try {
  const scan = await ZPrinter.scanBluetoothDevices();

  if (!scan.devices.length) {
    throw new Error('No printer found');
  }

  await ZPrinter.connectBluetooth({
    address: scan.devices[0].address,
  });

  await ZPrinter.printBluetoothText({
    text: 'Print successful',
    fontSize: 24,
    align: 'left',
    isBold: false,
    feedLines: 2,
  });
} catch (error) {
  console.error('Printer error:', error);
}
```

## Practical Limitations

- USB support is Android-only
- iOS generic USB printer access is not provided
- Some printers may need brand-specific ESC/POS command tuning
- Not every printer supports paper cut
- Some Bluetooth printers require pairing from system settings first
- Non-ESC/POS printers may need different command formats

## Demo App

This repository includes an example Capacitor app inside [`example-app`](./example-app).

That example shows:

- Bluetooth scan
- Bluetooth connect
- Bluetooth print
- Bluetooth cut
- USB printer listing
- USB printer connect
- USB print
- Thermal printer connect
- Thermal print

## Summary

If your target is:

- Ionic app
- Capacitor plugin
- Bluetooth receipt printer support
- Android USB printer support
- Thermal printer text printing

then `zprinter` is built for that exact job.
