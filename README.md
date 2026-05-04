# 🖨️ zprinter v3.0.0

### Developed by **Zakir Jarir**
**High-Performance Thermal Printing Plugin for Capacitor (iOS & Android)**

[![NPM Version](https://img.shields.io/npm/v/zprinter.svg)](https://www.npmjs.com/package/zprinter)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Capacitor](https://img.shields.io/badge/Capacitor-8.0+-blue.svg)](https://capacitorjs.com/)
[![Support](https://img.shields.io/badge/Support-Gmail%20%7C%20LinkedIn-orange.svg)](#📬-support--contact)

`zprinter` is a powerful native plugin that enables your Ionic/Capacitor apps to communicate with thermal printers. Whether you are using **Bluetooth**, **USB**, or **Network (WiFi/LAN)**, this plugin provides a rock-solid foundation for POS, Billing, and Logistics applications.

---

## 🚀 1. Installation

Install the plugin via NPM and sync your project:

```bash
npm install zakirjarir/zprinter
npx cap sync
```

---

## 🔧 2. Native Setup

### Android
Add these permissions to `android/app/src/main/AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.usb.host" />
```

### iOS
Add these keys to `ios/App/App/Info.plist`:
```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app uses Bluetooth to discover and print to receipt printers.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app uses Bluetooth to discover and print to receipt printers.</string>
```

---

## 📚 3. Comprehensive Function Reference

### 🔵 Bluetooth Printing Functions

#### `scanBluetoothDevices()`
Scans for nearby Bluetooth printers.
```typescript
const { devices } = await ZPrinter.scanBluetoothDevices();
// Returns: { name: string, address: string, isPaired: boolean }[]
```

#### `connectBluetooth(options)`
Connects to a printer using its MAC address.
```typescript
await ZPrinter.connectBluetooth({ address: '00:11:22:33:44:55' });
```

#### `printBluetoothText(options)`
Prints formatted text.
```typescript
await ZPrinter.printBluetoothText({
  text: 'Hello World\n',
  fontSize: 24,         // Default: 24
  align: 'center',      // 'left' | 'center' | 'right'
  isBold: true,         // Default: false
  feedLines: 2          // Lines to feed after printing
});
```

#### `printBluetoothImage(options)`
Prints a Base64 image.
```typescript
await ZPrinter.printBluetoothImage({
  base64: 'data:image/png;base64,iVBOR...',
  width: 200,           // Desired width in pixels
  align: 'center'
});
```

#### `printBluetoothQRCode(options)`
Prints a native ESC/POS QR code.
```typescript
await ZPrinter.printBluetoothQRCode({
  data: 'https://zakirjarir.com',
  size: 8,              // Size 1-16
  align: 'center'
});
```

#### `kickBluetoothDrawer()`
Opens the cash drawer connected to the Bluetooth printer.
```typescript
await ZPrinter.kickBluetoothDrawer();
```

#### `cutBluetoothPaper()`
Cuts the paper (requires a printer with an auto-cutter).
```typescript
await ZPrinter.cutBluetoothPaper();
```

---

### 🔌 USB Printing Functions (Android Only)

#### `listUsbPrinters()`
Lists all USB devices connected via OTG.
```typescript
const { devices } = await ZPrinter.listUsbPrinters();
```

#### `connectUsbPrinter(options)`
Connects to a specific USB printer.
```typescript
await ZPrinter.connectUsbPrinter({
  vendorId: 1234,
  productId: 5678,
  deviceName: 'usb_printer'
});
```

#### `printUsbText(options)`
Same options as Bluetooth text printing.
```typescript
await ZPrinter.printUsbText({ text: 'USB Print Test\n' });
```

---

### 🌐 Network (WiFi/LAN) Printing (Android Only)

#### `connectNetworkPrinter(options)`
Connects to a printer via IP and Port.
```typescript
await ZPrinter.connectNetworkPrinter({
  address: '192.168.1.100',
  port: 9100            // Default: 9100
});
```

#### `printNetworkText(options)`
```typescript
await ZPrinter.printNetworkText({ text: 'WiFi Print Successful\n' });
```

#### `printNetworkQRCode(options)`
```typescript
await ZPrinter.printNetworkQRCode({ data: 'WiFi-QR', size: 10 });
```

---

## 💎 4. Full Professional Demo Code

```typescript
import { ZPrinter } from 'zprinter';

const printMyReceipt = async () => {
  try {
    // 1. Connect
    await ZPrinter.connectBluetooth({ address: 'YOUR_MAC_ADDRESS' });

    // 2. Logo
    await ZPrinter.printBluetoothImage({
      base64: 'BASE64_STRING',
      width: 150,
      align: 'center'
    });

    // 3. Header
    await ZPrinter.printBluetoothText({
      text: 'Z-PRINTER POS SYSTEM\n',
      fontSize: 32,
      isBold: true,
      align: 'center'
    });

    // 4. Details
    await ZPrinter.printBluetoothText({
      text: 'Date: 2024-05-04\nItem 1: $10.00\nItem 2: $20.00\nTotal: $30.00\n',
      align: 'left'
    });

    // 5. QR Code
    await ZPrinter.printBluetoothQRCode({
      data: 'https://github.com/zakirjarir/zprinter',
      size: 8
    });

    // 6. Finish
    await ZPrinter.cutBluetoothPaper();
    await ZPrinter.kickBluetoothDrawer();
    await ZPrinter.disconnectBluetooth();

    alert('Print Successful!');
  } catch (err) {
    alert('Print Failed: ' + err);
  }
};
```

---

## ❓ 5. FAQ & Troubleshooting

*   **Q: My image is solid black.**
    *   A: Ensure your image has a white background. Transparent backgrounds are often treated as black by thermal heads.
*   **Q: Bluetooth scan finds nothing.**
    *   A: Ensure Location is ON and permissions are granted on Android.
*   **Q: Printer prints gibberish.**
    *   A: This happens if the printer is not ESC/POS compatible or uses a different baud rate (for serial-over-USB).

---

## 📬 Support & Contact

Need help or enterprise support? Contact me:

*   📧 **Email:** [zakirjarir@gmail.com](mailto:zakirjarir@gmail.com)
*   🔗 **LinkedIn:** [Zakir Jarir](https://www.linkedin.com/in/zakirjarir/)
*   🌐 **Portfolio:** [zakirjarir.com](https://zakirjarir.com)

---

## 🤝 Contributing

We welcome contributions! Please fork the repo and submit a PR.

## 📄 License

This project is licensed under the **MIT License**.

---
*Developed with ❤️ by **Zakir Jarir***
