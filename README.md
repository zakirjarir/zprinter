# 🖨️ zprinter

[![NPM Version](https://img.shields.io/npm/v/zprinter.svg)](https://www.npmjs.com/package/zprinter)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Capacitor](https://img.shields.io/badge/Capacitor-8.0+-blue.svg)](https://capacitorjs.com/)
[![Android Support](https://img.shields.io/badge/Android-BT%20%7C%20USB%20%7C%20Network-green.svg)](#android)
[![iOS Support](https://img.shields.io/badge/iOS-Bluetooth-lightgrey.svg)](#ios)

`zprinter` is a high-performance, native thermal printer plugin for **Ionic** and **Capacitor** apps. Built for reliability in demanding POS, billing, and retail environments.

---

## 🚀 Key Features

*   ✅ **Multi-Transport Support:** Bluetooth, USB (Android), and Network/WiFi (Android).
*   🎨 **Advanced Formatting:** Print images/logos, custom font sizes, bold text, and alignments.
*   📱 **Native QR Codes:** High-speed ESC/POS QR code generation.
*   📦 **Paper Handling:** Integrated paper cutting support.
*   💵 **Cash Drawer:** Open cash drawers connected via DK port.
*   ⚡ **Async/Await:** Fully promise-based API for seamless integration.

---

## 📦 Installation

```bash
npm install zakirjarir/zprinter
npx cap sync
```

---

## 🔧 Native Setup

### Android
Add permissions to your `AndroidManifest.xml` (usually handled automatically by Capacitor):
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.INTERNET" />
```

### iOS
Add usage descriptions to your `Info.plist`:
```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app uses Bluetooth to connect to thermal receipt printers.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app uses Bluetooth to connect to thermal receipt printers.</string>
```

---

## 📖 Usage Examples

### 🔵 Bluetooth Printing (Android & iOS)
```typescript
import { ZPrinter } from 'zprinter';

const printReceipt = async () => {
  // 1. Scan for devices
  const { devices } = await ZPrinter.scanBluetoothDevices();
  
  // 2. Connect
  await ZPrinter.connectBluetooth({ address: devices[0].address });

  // 3. Print Image/Logo
  await ZPrinter.printBluetoothImage({
    base64: 'iVBORw0KGgoAAAANSUhEUgAA...', // Base64 string
    width: 200,
    align: 'center'
  });

  // 4. Print Text
  await ZPrinter.printBluetoothText({
    text: 'Z-Printer Official\n----------------\nItem: Coffee   $5.00',
    fontSize: 24,
    align: 'center'
  });

  // 5. Print QR Code
  await ZPrinter.printBluetoothQRCode({
    data: 'https://github.com/zakirjarir/zprinter',
    size: 8
  });

  // 6. Cut & Disconnect
  await ZPrinter.cutBluetoothPaper();
  await ZPrinter.disconnectBluetooth();
};
```

### 🌐 Network/WiFi Printing (Android Only)
```typescript
await ZPrinter.connectNetworkPrinter({
  address: '192.168.1.100',
  port: 9100
});

await ZPrinter.printNetworkText({ text: 'Order for Table 5' });
await ZPrinter.disconnectNetworkPrinter();
```

---

## 🛠️ API Reference

### Bluetooth Methods
| Method | Description |
| :--- | :--- |
| `scanBluetoothDevices()` | Scans for available Bluetooth printers. |
| `connectBluetooth(options)` | Connects to a specific Bluetooth printer. |
| `printBluetoothText(options)` | Prints formatted text. |
| `printBluetoothImage(options)` | Prints a Base64 image. |
| `printBluetoothQRCode(options)` | Prints a native QR code. |
| `kickBluetoothDrawer()` | Opens the cash drawer. |
| `cutBluetoothPaper()` | Cuts the printer paper. |

### USB & Thermal Methods (Android)
| Method | Description |
| :--- | :--- |
| `listUsbPrinters()` | Returns a list of connected USB devices. |
| `connectUsbPrinter(options)` | Connects via USB OTG. |
| `printUsbText(options)` | Prints text over USB. |
| `kickUsbDrawer()` | Opens cash drawer via USB. |

### Network Methods (Android)
| Method | Description |
| :--- | :--- |
| `connectNetworkPrinter(options)` | Connects via TCP/IP. |
| `printNetworkText(options)` | Prints text over network. |
| `kickNetworkDrawer()` | Opens cash drawer via network. |

---

## 🤝 Contributing

We welcome contributions! Please see our [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to get started.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ✨ Author

**Zakir Jarir**
*   GitHub: [@zakirjarir](https://github.com/zakirjarir)
*   LinkedIn: [Zakir Jarir](https://linkedin.com/in/zakirjarir)

---
*Built with ❤️ for the Capacitor community.*
