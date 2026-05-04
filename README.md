<div align="center">

<img src="https://img.shields.io/badge/ZPrinter-Capacitor%20Plugin-6C63FF?style=for-the-badge&logo=ionic&logoColor=white" alt="ZPrinter" />

<h1>🖨️ ZPrinter — Capacitor Printer Plugin</h1>

<p>A native Capacitor plugin that enables Ionic/Capacitor apps to print to thermal receipt printers via <strong>Bluetooth</strong>, <strong>USB</strong> (Android), or <strong>Network (TCP/IP)</strong>.</p>

<br/>
<p align="center">

[![বাংলা](https://img.shields.io/badge/%E0%A6%AC%E0%A6%BE%E0%A6%82%E0%A6%B2%E0%A6%BE-%E0%A6%AA%E0%A6%A5%E0%A6%A8-orange?style=for-the-badge)](https://github.com/zakirjarir/zprinter/blob/main/README.bn.md)

</p>
<br/>


[![npm](https://img.shields.io/badge/npm-zprinter-CB3837?style=flat-square&logo=npm)](https://www.npmjs.com/package/zprinter)
[![Capacitor](https://img.shields.io/badge/Capacitor-8.x%2B-119EFF?style=flat-square&logo=capacitor)](https://capacitorjs.com/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green?style=flat-square)](https://github.com/zakirjarir/zprinter)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![Issues](https://img.shields.io/badge/Issues-GitHub-black?style=flat-square&logo=github)](https://github.com/zakirjarir/zprinter/issues)

</div>

---

## 📋 Table of Contents

- [✨ Quick Summary](#-quick-summary)
- [🚀 Features](#-features)
- [📦 Installation](#-installation)
- [⚙️ Native Setup](#️-native-setup)
- [💻 Usage Examples](#-usage-examples)
- [📖 API Reference](#-api-reference)
- [🧪 Example App & Local Dev](#-zprinter--local-development)
- [🏗️ Building & Publishing](#️-building--publishing)
- [🔧 Troubleshooting & FAQ](#-troubleshooting--faq)
- [🤝 Contributing & Support](#-contributing--support)
- [📄 License](#-license)

---

## ✨ Quick Summary

| Feature | Details |
|---|---|
| **Connectivity** | Bluetooth (Android & iOS), USB (Android), Network/TCP (Android) |
| **Print Types** | Text, Images (base64), QR Codes, Cash Drawer Kick, Auto-Cut |
| **Capacitor** | Compatible with **8.x+** |

---

## 🚀 Features

- 🔵 **Bluetooth** — Scan and connect to BLE/Classic thermal printers
- 🔌 **USB OTG** — Full USB support for Android
- 🌐 **Network** — Raw TCP/IP printing (default port 9100)
- 🖼️ **Image Rendering** — Optimized base64 image output for thermal printers
- 📱 **QR Codes** — Native ESC/POS QR code generation
- ✂️ **Extras** — Cash drawer kick & auto paper cut commands

---

## 📦 Installation

### ✅ Preferred — Install from GitHub (stays in sync with the repo)

```bash
npm install zakirjarir/zprinter
npx cap sync
```


### 🛠️ Local Development
```bash
# From the repo root
git clone git@github.com:zakirjarir/zprinter.git
npm install
# Then go to the example app
cd zprinter
npm install
npm start
```

### 📋 Requirements

| Requirement | Version |
|---|---|
| Node | 18+ recommended |
| npm | 8+ or compatible |
| Capacitor | 8+ (peer dependency) |
| Xcode | Required for iOS native verification (macOS only) |
| Android SDK + Gradle | Required for Android native verification |

---

## ⚙️ Native Setup

### 🤖 Android

**1. Update `AndroidManifest.xml`** (`app/src/main/AndroidManifest.xml`):

```xml
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.usb.host" android:required="false" />
```

**2. Runtime Permissions:**
Request `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`, and `ACCESS_FINE_LOCATION` on **Android 12+** before scanning or connecting.

**3. SDK Versions:**
Ensure `targetSdkVersion` and `compileSdkVersion` align with the Android 12+ permissions model.

---

### 🍎 iOS

**1. Update `Info.plist`** with Bluetooth usage descriptions:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>Required to discover and print to thermal printers.</string>

<key>NSBluetoothPeripheralUsageDescription</key>
<string>Required to discover and print to thermal printers.</string>
```

**2. Background Modes (Optional):**
If your app requires Bluetooth background modes, add the appropriate modes and justify them in your App Store submission.

---

## 💻 Usage Examples

### 🔵 Bluetooth — Scan, Connect, Print, Disconnect

```typescript
import { ZPrinter } from 'zprinter';

// 1️⃣ Scan for devices
const { devices } = await ZPrinter.scanBluetoothDevices();
const addr = devices[0].address; // Android: MAC address | iOS: UUID

// 2️⃣ Connect
await ZPrinter.connectBluetooth({ address: addr });

// 3️⃣ Print text
await ZPrinter.printBluetoothText({
  text: 'Hello from ZPrinter\n',
  fontSize: 24,
  align: 'left',
  feedLines: 2
});

// 4️⃣ Print image (base64)
await ZPrinter.printBluetoothImage({
  base64: 'data:image/png;base64,...',
  width: 384,
  align: 'center'
});

// 5️⃣ Actions
await ZPrinter.cutBluetoothPaper();
await ZPrinter.kickBluetoothDrawer();

// 6️⃣ Disconnect
await ZPrinter.disconnectBluetooth();
```

---

### 🔌 USB — Android Only

```typescript
const { devices } = await ZPrinter.listUsbPrinters();

await ZPrinter.connectUsbPrinter({
  vendorId: devices[0].vendorId,
  productId: devices[0].productId
});

await ZPrinter.printUsbText({ text: 'USB Print\n' });
```

---

### 🌐 Network / TCP — Android Only

```typescript
await ZPrinter.connectNetworkPrinter({
  address: '192.168.1.100',
  port: 9100
});

await ZPrinter.printNetworkText({ text: 'Network print\n' });
```

---

### 🖼️ Image & QR Tips

> 💡 **Image:** Use monochrome images with a white background. Convert to **1-bit dithered PNG** for best thermal printer results.
>
> 📱 **QR Code:** Use the plugin's built-in QR API for optimal ESC/POS rendering on thermal printers.

---

## 📖 API Reference

### Bluetooth

| Method | Description |
|---|---|
| `scanBluetoothDevices()` | Returns `{ devices: Device[] }` — discovered Bluetooth printers |
| `connectBluetooth({ address })` | Connect to a printer by MAC (Android) or UUID (iOS) |
| `disconnectBluetooth()` | Disconnect from the current Bluetooth printer |
| `printBluetoothText(options)` | Print text with optional formatting |
| `printBluetoothImage(options)` | Print a base64 image |
| `printBluetoothQRCode(options)` | Print a QR code via ESC/POS |
| `cutBluetoothPaper()` | Trigger auto paper cut |
| `kickBluetoothDrawer()` | Send cash drawer kick command |

#### `printBluetoothText` Options

| Option | Type | Default | Description |
|---|---|---|---|
| `text` | `string` | — | Text to print |
| `fontSize` | `number` | — | Font size |
| `align` | `'left' \| 'center' \| 'right'` | `'left'` | Text alignment |
| `isBold` | `boolean` | `false` | Bold formatting |
| `feedLines` | `number` | — | Lines to feed after text |

#### `printBluetoothImage` Options

| Option | Type | Description |
|---|---|---|
| `base64` | `string` | Base64-encoded image string |
| `width` | `number` | Print width in pixels |
| `align` | `string` | Image alignment |

#### `printBluetoothQRCode` Options

| Option | Type | Description |
|---|---|---|
| `data` | `string` | Data to encode in QR |
| `size` | `number` | QR code size |
| `align` | `string` | QR code alignment |

---

### USB *(Android Only)*

| Method | Description |
|---|---|
| `listUsbPrinters()` | Returns `{ devices: UsbDevice[] }` |
| `connectUsbPrinter({ vendorId, productId })` | Connect to a USB printer |
| `printUsbText(options)` | Print text over USB |

---

### Network *(Android Only)*

| Method | Description |
|---|---|
| `connectNetworkPrinter({ address, port })` | Connect via TCP/IP |
| `printNetworkText(options)` | Print text over network |

> 📁 See `src/` for full TypeScript types and all available option fields.

---

## 🧪 Example App & Local Development

The `zprinter` directory demonstrates complete scanning, connecting, and printing flows.

```bash
# Run the example app locally
cd zprinter
npm install
npm start
```

**Testing native changes:**
```bash
npx cap sync
# Then open the platform project in Xcode or Android Studio
```

---

## 🏗️ Building & Publishing

| Command | Description |
|---|---|
| `npm run build` | Compile the plugin — generates output in `dist/` |
| `npm run verify:ios` | Verify iOS build *(requires Xcode on macOS)* |
| `npm run verify:android` | Verify Android build *(requires Android SDK)* |

> ⚠️ When publishing to npm or GitHub Packages, ensure build artifacts (`dist/`) are included in the package.

---

## 🔧 Troubleshooting & FAQ

| Problem | Solution |
|---|---|
| 🖼️ **Image prints completely black** | Convert image to a white background, 1-bit monochrome format before printing |
| 🔵 **No Bluetooth devices found** | Request runtime permissions on Android; ensure Location services are enabled |
| 🔌 **USB printer not detected** | Try a powered OTG cable; verify `usb.host` feature is in the Android manifest and permissions are granted |
| 🌐 **Network connection fails** | Double-check the IP address, port number, and that the device is on the same subnet |

---

### 🔀 Migration Notes

If upgrading from an older Capacitor or Android SDK version:
- Review permission changes introduced in **Android 12+**
- Update `AndroidManifest.xml` accordingly (see Native Setup above)

---

## 🤝 Contributing & Support

Contributions are welcome! Please follow this flow:

> **Fork** → **Branch** → **Pull Request**

- Run existing linters and tests locally before submitting
- Open an issue first for large or breaking changes

### 📬 Support

| Channel | Link |
|---|---|
| 📧 Email | [zakirjarir@gmail.com](mailto:zakirjarir@gmail.com) |
| 🐛 Issues | [github.com/zakirjarir/zprinter/issues](https://github.com/zakirjarir/zprinter/issues) |

---

## 📄 License

This project is licensed under the **MIT License**.

---

<div align="center">

Made with ❤️ by [zakirjarir](https://github.com/zakirjarir)

⭐ If this plugin helped you, please consider starring the repo!

</div>