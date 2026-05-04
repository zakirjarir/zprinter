# 🖨️ zprinter v3.0.0

### Developed by **Zakir Jarir**
**High-Performance Thermal Printing Plugin for Capacitor (iOS & Android)**

[![NPM Version](https://img.shields.io/npm/v/zprinter.svg)](https://www.npmjs.com/package/zprinter)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Capacitor](https://img.shields.io/badge/Capacitor-8.0+-blue.svg)](https://capacitorjs.com/)
[![Support](https://img.shields.io/badge/Support-Gmail%20%7C%20LinkedIn-orange.svg)](#📬-support--contact)

`zprinter` is a powerful native plugin that enables your Ionic/Capacitor apps to communicate with thermal printers. Whether you are using **Bluetooth**, **USB**, or **Network (WiFi/LAN)**, this plugin provides a rock-solid foundation for POS, Billing, and Logistics applications.

---

## ✨ Key Features

- ✅ **Bluetooth Support**: Scan and connect to BLE/Classic Bluetooth printers (iOS & Android).
- ✅ **USB Support**: Connect via OTG to thermal printers (Android Only).
- ✅ **Network Support**: Print over TCP/IP (WiFi/LAN) with custom ports (Android Only).
- ✅ **Image Rendering**: High-quality monochrome image printing from Base64.
- ✅ **QR Code Support**: Native ESC/POS QR code generation.
- ✅ **Cash Drawer**: Trigger cash drawer kicks (`kickDrawer`).
- ✅ **Auto-Cutter**: Command for automatic paper cutting.
- ✅ **Performance**: Optimized for fast printing with minimal latency.

---

## 🚀 1. Installation

Install the plugin via NPM and sync your project:

```bash
npm install zprinter
npx cap sync
```

---

## 🔧 2. Native Setup

### Android
Add these permissions to `android/app/src/main/AndroidManifest.xml`:
```xml
<!-- Bluetooth Permissions -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Network & USB Permissions -->
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

## 📚 3. API Reference & Platform Support

| Method | Android | iOS | Description |
| :--- | :---: | :---: | :--- |
| `scanBluetoothDevices()` | ✅ | ✅ | Scans for nearby Bluetooth devices. |
| `connectBluetooth()` | ✅ | ✅ | Connects to a Bluetooth printer. |
| `printBluetoothText()` | ✅ | ✅ | Prints text via Bluetooth. |
| `printBluetoothImage()` | ✅ | ✅ | Prints image via Bluetooth. |
| `printBluetoothQRCode()` | ✅ | ✅ | Prints QR code via Bluetooth. |
| `kickBluetoothDrawer()` | ✅ | ✅ | Kicks cash drawer via Bluetooth. |
| `cutBluetoothPaper()` | ✅ | ✅ | Cuts paper via Bluetooth. |
| `disconnectBluetooth()` | ✅ | ✅ | Disconnects Bluetooth printer. |
| `listUsbPrinters()` | ✅ | ❌ | Lists available USB printers. |
| `connectUsbPrinter()` | ✅ | ❌ | Connects to a USB printer. |
| `printUsbText()` | ✅ | ❌ | Prints text via USB. |
| `connectNetworkPrinter()` | ✅ | ❌ | Connects to a Network printer. |
| `printNetworkText()` | ✅ | ❌ | Prints text via Network. |

---

## 🔵 4. Bluetooth Printing Guide

### Step 1: Scan for Devices
```typescript
import { ZPrinter } from 'zprinter';

const { devices } = await ZPrinter.scanBluetoothDevices();
// On iOS, use device.address (UUID) for connection.
// On Android, use device.address (MAC Address).
```

### Step 2: Connect
```typescript
await ZPrinter.connectBluetooth({ address: '00:11:22:33:44:55' });
```

### Step 3: Print Text
```typescript
await ZPrinter.printBluetoothText({
  text: 'Z-PRINTER POS\n',
  fontSize: 32,
  align: 'center',
  isBold: true,
  feedLines: 2
});
```

---

## 🔌 5. USB Printing (Android Only)

Connect your printer via OTG cable.

```typescript
// 1. List printers
const { devices } = await ZPrinter.listUsbPrinters();

// 2. Connect (using vendorId/productId or deviceName)
await ZPrinter.connectUsbPrinter({
  vendorId: devices[0].vendorId,
  productId: devices[0].productId
});

// 3. Print
await ZPrinter.printUsbText({ text: 'USB Print Success!\n' });
```

---

## 🌐 6. Network Printing (Android Only)

Print over WiFi or LAN using the printer's IP address.

```typescript
await ZPrinter.connectNetworkPrinter({
  address: '192.168.1.100',
  port: 9100
});

await ZPrinter.printNetworkText({ text: 'Network Print Success!\n' });
```

---

## 🖼️ 7. Advanced Printing

### Image Printing
For best results, use monochrome images with a **white background**.

```typescript
await ZPrinter.printBluetoothImage({
  base64: 'data:image/png;base64,...',
  width: 384, // Standard 58mm printer width
  align: 'center'
});
```

### QR Code Printing
```typescript
await ZPrinter.printBluetoothQRCode({
  data: 'https://zakirjarir.com',
  size: 8,
  align: 'center'
});
```

---

## 💎 8. Full Implementation Example

```typescript
import { ZPrinter } from 'zprinter';

const handlePrint = async () => {
  try {
    // 1. Connection
    await ZPrinter.connectBluetooth({ address: 'YOUR_DEVICE_ADDRESS' });

    // 2. Branding
    await ZPrinter.printBluetoothImage({
      base64: 'LOGO_BASE64',
      width: 200,
      align: 'center'
    });

    // 3. Content
    await ZPrinter.printBluetoothText({
      text: 'OFFICIAL RECEIPT\n',
      fontSize: 32,
      isBold: true,
      align: 'center'
    });

    await ZPrinter.printBluetoothText({
      text: 'Item: Coffee......$5.00\nTotal: $5.00\n',
      align: 'left'
    });

    // 4. Footer & Actions
    await ZPrinter.printBluetoothQRCode({ data: 'https://zakirjarir.com' });
    await ZPrinter.cutBluetoothPaper();
    await ZPrinter.kickBluetoothDrawer();

    // 5. Cleanup
    await ZPrinter.disconnectBluetooth();
    
  } catch (error) {
    console.error('Print Error:', error);
  }
};
```

---

## ❓ 9. FAQ & Troubleshooting

*   **Q: My image is solid black.**
    *   A: Ensure your image has a **white background**. Transparent backgrounds are often rendered as black by thermal printers.
*   **Q: Bluetooth scan finds nothing.**
    *   A: Ensure **Location Services** are enabled and permissions are granted on Android.
*   **Q: Network printer won't connect.**
    *   A: Ensure the device and printer are on the same subnet and port 9100 is open.
*   **Q: USB printer not listed.**
    *   A: Ensure you are using a high-quality OTG cable and the printer is powered on.

---

## 📬 Support & Contact

Need help or enterprise support? Contact me:

*   📧 **Email:** [zakirjarir@gmail.com](mailto:zakirjarir@gmail.com)
*   🔗 **LinkedIn:** [Zakir Jarir](https://www.linkedin.com/in/zakirjarir/)
*   🌐 **Portfolio:** [zakirjarir.com](https://zakirjarir.com)

---

## 🤝 Contributing

We welcome contributions! Please fork the repo and submit a PR. For major changes, please open an issue first to discuss what you would like to change.

## 📄 License

This project is licensed under the **MIT License**.

---
*Developed with ❤️ by **Zakir Jarir***
