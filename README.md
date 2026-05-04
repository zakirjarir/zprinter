# 🖨️ zprinter v3.0.0

### Created & Developed by **Zakir Jarir**

[![NPM Version](https://img.shields.io/npm/v/zprinter.svg)](https://www.npmjs.com/package/zprinter)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Capacitor](https://img.shields.io/badge/Capacitor-8.0+-blue.svg)](https://capacitorjs.com/)
[![Support](https://img.shields.io/badge/Support-Gmail%20%7C%20LinkedIn-orange.svg)](#📬-support--contact)
[![Android Support](https://img.shields.io/badge/Android-BT%20%7C%20USB%20%7C%20Network-green.svg)](#android)
[![iOS Support](https://img.shields.io/badge/iOS-Bluetooth-lightgrey.svg)](#ios)

`zprinter` is a high-performance, enterprise-grade Capacitor plugin designed for thermal receipt printing. It provides a unified, cross-platform API to communicate with **Bluetooth**, **USB**, and **Network (TCP/IP)** printers using industry-standard ESC/POS commands.

---

## 🌟 Premium Features

*   ✅ **Universal Connectivity:** One plugin for Bluetooth, USB OTG (Android), and LAN/Network printers.
*   🖼️ **High-Fidelity Image Printing:** Advanced grayscale algorithm to print logos and images clearly on thermal paper.
*   🔳 **Native QR Codes:** High-speed, native generation of QR codes for payments and tracking.
*   💵 **Point of Sale Ready:** Support for cash drawer kick-out (DK port) and automatic paper cutting.
*   🎨 **Rich Text Formatting:** Easily control font sizes, bold styles, and text alignment (Left, Center, Right).
*   🚀 **Performance Optimized:** Asynchronous, non-blocking API for smooth UI performance.

---

## 📬 Support & Contact

Need custom development, integration support, or found a bug?

*   📧 **Gmail:** [zakirjarir@gmail.com](mailto:zakirjarir@gmail.com)
*   🔗 **LinkedIn:** [Zakir Jarir](https://www.linkedin.com/in/zakirjarir/)
*   🌐 **GitHub:** [@zakirjarir](https://github.com/zakirjarir)

---

## 📦 Installation

```bash
npm install zakirjarir/zprinter
npx cap sync
```

---

## 🔧 Native Configuration

### Android Setup
Add the following permissions to your `AndroidManifest.xml` (Capacitor usually handles this, but verify for best results):
```xml
<!-- Bluetooth & Location -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Network & USB Features -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.usb.host" />
```

### iOS Setup
Add the following keys to your `Info.plist` to request Bluetooth access:
```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app requires Bluetooth access to discover and connect to your receipt printer.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app requires Bluetooth access to discover and connect to your receipt printer.</string>
```

---

## 🚀 Advanced Usage Examples

### 💎 Professional Receipt (Combined Features)
```typescript
import { ZPrinter } from 'zprinter';

async function printInvoice() {
  try {
    // 1. Connect to a Bluetooth Printer
    await ZPrinter.connectBluetooth({ address: '00:11:22:33:44:55' });

    // 2. Print Store Logo
    await ZPrinter.printBluetoothImage({
      base64: 'YOUR_BASE64_IMAGE_DATA',
      width: 180,
      align: 'center'
    });

    // 3. Print Header
    await ZPrinter.printBluetoothText({
      text: 'Z-TECH SOLUTIONS\nDhaka, Bangladesh\n----------------',
      fontSize: 24,
      align: 'center',
      isBold: true
    });

    // 4. Print Table Content
    await ZPrinter.printBluetoothText({
      text: 'Item            Qty     Price\nItem A           01     $10.00\nItem B           02     $20.00\n-----------------------------',
      align: 'left'
    });

    // 5. Print Payment QR Code
    await ZPrinter.printBluetoothQRCode({
      data: 'https://payment.example.com/inv123',
      size: 8,
      align: 'center'
    });

    // 6. Paper Cut & Open Drawer
    await ZPrinter.cutBluetoothPaper();
    await ZPrinter.kickBluetoothDrawer();
    
    await ZPrinter.disconnectBluetooth();
  } catch (error) {
    console.error('Print Error:', error);
  }
}
```

### 🌐 WiFi/Network Printing (Android)
```typescript
// Connect to a printer in the kitchen or remote area
await ZPrinter.connectNetworkPrinter({
  address: '192.168.1.100',
  port: 9100
});

await ZPrinter.printNetworkText({ 
  text: 'NEW ORDER RECEIVED\nTable: 05',
  fontSize: 32,
  isBold: true 
});

await ZPrinter.disconnectNetworkPrinter();
```

---

## 🛠️ API Reference

The following API is generated from the source code.

<docgen-index>

* [`scanBluetoothDevices()`](#scanbluetoothdevices)
* [`connectBluetooth(...)`](#connectbluetooth)
* [`printBluetoothText(...)`](#printbluetoothtext)
* [`printBluetoothImage(...)`](#printbluetoothimage)
* [`printBluetoothQRCode(...)`](#printbluetoothqrcode)
* [`kickBluetoothDrawer()`](#kickbluetoothdrawer)
* [`cutBluetoothPaper()`](#cutbluetoothpaper)
* [`disconnectBluetooth()`](#disconnectbluetooth)
* [`listUsbPrinters()`](#listusbprinters)
* [`connectUsbPrinter(...)`](#connectusbprinter)
* [`printUsbText(...)`](#printusbtext)
* [`printUsbImage(...)`](#printusbimage)
* [`printUsbQRCode(...)`](#printusbqrcode)
* [`kickUsbDrawer()`](#kickusbdrawer)
* [`disconnectUsbPrinter()`](#disconnectusbprinter)
* [`connectThermalPrinter(...)`](#connectthermalprinter)
* [`printThermalText(...)`](#printthermaltext)
* [`printThermalImage(...)`](#printthermalimage)
* [`printThermalQRCode(...)`](#printthermalqrcode)
* [`kickThermalDrawer()`](#kickthermaldrawer)
* [`disconnectThermalPrinter()`](#disconnectthermalprinter)
* [`connectNetworkPrinter(...)`](#connectnetworkprinter)
* [`printNetworkText(...)`](#printnetworktext)
* [`printNetworkImage(...)`](#printnetworkimage)
* [`printNetworkQRCode(...)`](#printnetworkqrcode)
* [`kickNetworkDrawer()`](#kicknetworkdrawer)
* [`disconnectNetworkPrinter()`](#disconnectnetworkprinter)
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


### printBluetoothImage(...)

```typescript
printBluetoothImage(options: PrinterImageOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#printerimageoptions">PrinterImageOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### printBluetoothQRCode(...)

```typescript
printBluetoothQRCode(options: PrinterQRCodeOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#printerqrcodeoptions">PrinterQRCodeOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### kickBluetoothDrawer()

```typescript
kickBluetoothDrawer() => Promise<{ kicked: boolean; }>
```

**Returns:** <code>Promise&lt;{ kicked: boolean; }&gt;</code>

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


### printUsbImage(...)

```typescript
printUsbImage(options: PrinterImageOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#printerimageoptions">PrinterImageOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### printUsbQRCode(...)

```typescript
printUsbQRCode(options: PrinterQRCodeOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#printerqrcodeoptions">PrinterQRCodeOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### kickUsbDrawer()

```typescript
kickUsbDrawer() => Promise<{ kicked: boolean; }>
```

**Returns:** <code>Promise&lt;{ kicked: boolean; }&gt;</code>

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


### printThermalImage(...)

```typescript
printThermalImage(options: PrinterImageOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#printerimageoptions">PrinterImageOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### printThermalQRCode(...)

```typescript
printThermalQRCode(options: PrinterQRCodeOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#printerqrcodeoptions">PrinterQRCodeOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### kickThermalDrawer()

```typescript
kickThermalDrawer() => Promise<{ kicked: boolean; }>
```

**Returns:** <code>Promise&lt;{ kicked: boolean; }&gt;</code>

--------------------


### disconnectThermalPrinter()

```typescript
disconnectThermalPrinter() => Promise<void>
```

--------------------


### connectNetworkPrinter(...)

```typescript
connectNetworkPrinter(options: NetworkPrinterConnectOptions) => Promise<PrinterConnectionResult>
```

| Param         | Type                                                                                  |
| ------------- | ------------------------------------------------------------------------------------- |
| **`options`** | <code><a href="#networkprinterconnectoptions">NetworkPrinterConnectOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#printerconnectionresult">PrinterConnectionResult</a>&gt;</code>

--------------------


### printNetworkText(...)

```typescript
printNetworkText(options: PrinterTextOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                              |
| ------------- | ----------------------------------------------------------------- |
| **`options`** | <code><a href="#printertextoptions">PrinterTextOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### printNetworkImage(...)

```typescript
printNetworkImage(options: PrinterImageOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                                |
| ------------- | ------------------------------------------------------------------- |
| **`options`** | <code><a href="#printerimageoptions">PrinterImageOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### printNetworkQRCode(...)

```typescript
printNetworkQRCode(options: PrinterQRCodeOptions) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                                  |
| ------------- | --------------------------------------------------------------------- |
| **`options`** | <code><a href="#printerqrcodeoptions">PrinterQRCodeOptions</a></code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### kickNetworkDrawer()

```typescript
kickNetworkDrawer() => Promise<{ kicked: boolean; }>
```

**Returns:** <code>Promise&lt;{ kicked: boolean; }&gt;</code>

--------------------


### disconnectNetworkPrinter()

```typescript
disconnectNetworkPrinter() => Promise<void>
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


#### PrinterImageOptions

| Prop         | Type                                       |
| ------------ | ------------------------------------------ |
| **`base64`** | <code>string</code>                        |
| **`width`**  | <code>number</code>                        |
| **`height`** | <code>number</code>                        |
| **`align`**  | <code>'left' \| 'center' \| 'right'</code> |


#### PrinterQRCodeOptions

| Prop        | Type                                       |
| ----------- | ------------------------------------------ |
| **`data`**  | <code>string</code>                        |
| **`size`**  | <code>number</code>                        |
| **`align`** | <code>'left' \| 'center' \| 'right'</code> |


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


#### NetworkPrinterConnectOptions

| Prop          | Type                |
| ------------- | ------------------- |
| **`address`** | <code>string</code> |
| **`port`**    | <code>number</code> |

</docgen-api>

---

## ❓ FAQ & Troubleshooting

#### ⚡ Image quality is poor
Thermal printers use 1-bit dithering. For best results, use high-contrast images with white backgrounds. Transparent backgrounds may print as solid black.

#### 📶 Bluetooth device not found
- **Android:** Ensure "Location Services" are enabled.
- **iOS:** Ensure the printer is not already paired in the iOS System Settings.

#### 🔌 USB OTG Issues
Ensure your Android device supports USB Host mode and you are using a high-quality OTG adapter.

#### 📏 Paper Width
- **58mm printers:** Recommended max 32 characters per line.
- **80mm printers:** Recommended max 48 characters per line.

---

## 🤝 Contributing

We welcome contributions to improve this plugin! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## 📄 License

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more information.

---
### 💖 Created & Maintained by **Zakir Jarir**
*Built with passion for the Capacitor community.*
