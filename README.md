# 🖨️ zprinter v3.0.0

[![NPM Version](https://img.shields.io/npm/v/zprinter.svg)](https://www.npmjs.com/package/zprinter)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Capacitor](https://img.shields.io/badge/Capacitor-8.0+-blue.svg)](https://capacitorjs.com/)
[![Android Support](https://img.shields.io/badge/Android-BT%20%7C%20USB%20%7C%20Network-green.svg)](#android)
[![iOS Support](https://img.shields.io/badge/iOS-Bluetooth-lightgrey.svg)](#ios)

`zprinter` is a professional-grade Capacitor plugin for thermal receipt printing. It provides a unified API for Bluetooth, USB, and Network printers, specifically optimized for ESC/POS compatible devices.

---

## 🌟 Major Features

*   📶 **Bluetooth (BLE/Classic):** Seamless discovery and connection on Android & iOS.
*   🔌 **USB OTG (Android):** High-speed printing via USB for POS terminals.
*   🌐 **Network/WiFi (Android):** TCP/IP support for kitchen and remote printers.
*   🖼️ **Image/Logo Printing:** Advanced grayscale conversion for clear logos.
*   🔳 **Native QR Code:** Fast, native ESC/POS QR code generation.
*   ✂️ **Paper Control:** Integrated support for paper cutting.
*   💰 **Cash Drawer:** Open cash drawers via the printer's DK port.

---

## 📦 Installation

```bash
npm install zakirjarir/zprinter
npx cap sync
```

---

## 🔧 Native Setup

### Android
Ensure your `AndroidManifest.xml` includes these permissions:
```xml
<!-- Bluetooth permissions -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Network & USB -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.usb.host" />
```

### iOS
Add these keys to your `Info.plist`:
```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>We need access to Bluetooth to connect to thermal printers for receipts.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>We need access to Bluetooth to connect to thermal printers for receipts.</string>
```

---

## 🚀 Professional Receipt Example

Here is how you can print a complete professional receipt combining multiple features:

```typescript
import { ZPrinter } from 'zprinter';

async function printFullReceipt() {
  try {
    // 1. Connect (Example: Bluetooth)
    await ZPrinter.connectBluetooth({ address: '00:11:22:33:44:55' });

    // 2. Print Logo
    await ZPrinter.printBluetoothImage({
      base64: 'iVBORw0KGgoAAAANSUhEUgAA...', // Your Logo Base64
      width: 150,
      align: 'center'
    });

    // 3. Header
    await ZPrinter.printBluetoothText({
      text: 'Z-PRINTER SHOP\nDhaka, Bangladesh\nTel: +880 12345678',
      fontSize: 24,
      align: 'center',
      isBold: true
    });

    // 4. Body
    await ZPrinter.printBluetoothText({
      text: '\nItem Name         Qty    Price\n------------------------------\nCoffee Large      01     $5.00\nBurger King       01    $12.00\n------------------------------\nTOTAL:                  $17.00\n',
      align: 'left',
      feedLines: 1
    });

    // 5. QR Code for Payment/Feedback
    await ZPrinter.printBluetoothQRCode({
      data: 'https://zakirjarir.com/pay',
      size: 8,
      align: 'center'
    });

    // 6. Footer & Cut
    await ZPrinter.printBluetoothText({
      text: 'Thank you for your visit!',
      align: 'center',
      feedLines: 3
    });
    
    await ZPrinter.cutBluetoothPaper();
    await ZPrinter.kickBluetoothDrawer(); // Open Cash Drawer
    await ZPrinter.disconnectBluetooth();

  } catch (err) {
    console.error('Printing failed:', err);
  }
}
```

---

## 🛠️ Detailed API

### Bluetooth API
| Method | Options | Returns |
| :--- | :--- | :--- |
| `scanBluetoothDevices()` | - | `Promise<{devices: BluetoothPrinterDevice[]}>` |
| `connectBluetooth(opt)` | `{address: string}` | `Promise<PrinterConnectionResult>` |
| `printBluetoothText(opt)` | `PrinterTextOptions` | `Promise<{printed: boolean}>` |
| `printBluetoothImage(opt)`| `PrinterImageOptions`| `Promise<{printed: boolean}>` |
| `printBluetoothQRCode(opt)`| `PrinterQRCodeOptions`| `Promise<{printed: boolean}>` |
| `kickBluetoothDrawer()` | - | `Promise<{kicked: boolean}>` |
| `cutBluetoothPaper()` | - | `Promise<{cut: boolean}>` |

### Network API (Android)
| Method | Options | Returns |
| :--- | :--- | :--- |
| `connectNetworkPrinter(opt)` | `{address: string, port?: number}` | `Promise<ConnectionResult>` |
| `printNetworkText(opt)` | `PrinterTextOptions` | `Promise<{printed: boolean}>` |
| `printNetworkImage(opt)` | `PrinterImageOptions` | `Promise<{printed: boolean}>` |
| `printNetworkQRCode(opt)` | `PrinterQRCodeOptions` | `Promise<{printed: boolean}>` |

---

## ❓ FAQ & Troubleshooting

#### 1. Why does my image look blurry?
Thermal printers are 1-bit (Black or White). Ensure your logo is high-contrast. The plugin uses grayscale dithering, but a pure black/white PNG works best.

#### 2. Bluetooth scan doesn't find any devices?
- On Android: Ensure **Location** is ON and permissions are granted.
- On iOS: Ensure the printer is NOT paired in the system settings; the app should find it directly.

#### 3. USB printing doesn't work?
Make sure your device supports **USB OTG** and you are using a proper OTG adapter. The printer must be set to USB mode.

#### 4. Paper width issues?
Thermal printers usually come in **58mm** or **80mm**.
- For 58mm: Use approx. 32 characters per line.
- For 80mm: Use approx. 48 characters per line.

---

## 🤝 Contributing

We love contributions!
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

## ✨ Author

**Zakir Jarir**
*   GitHub: [@zakirjarir](https://github.com/zakirjarir)
*   LinkedIn: [Zakir Jarir](https://linkedin.com/in/zakirjarir)

---
*If you like this project, please give it a ⭐ on GitHub!*
