<div align="center">

<img src="https://img.shields.io/badge/ZPrinter-Capacitor%20Plugin-6C63FF?style=for-the-badge&logo=ionic&logoColor=white" alt="ZPrinter" />

<h1>🖨️ ZPrinter — Complete Capacitor Thermal Printer Plugin</h1>

<p>A production-ready native Capacitor plugin for Ionic (Vue, React, Angular) and Capacitor apps to print receipts, logos, barcodes, QR codes, and open cash drawers on <strong>old &amp; new thermal printers</strong> via <strong>Bluetooth</strong>, <strong>USB OTG</strong>, and <strong>Network (Wi-Fi / LAN)</strong> — with built-in <strong>Auto-Save &amp; Auto-Reconnect</strong>.</p>

<br/>
<p align="center">

[![বাংলা](https://img.shields.io/badge/%E0%A6%AC%E0%A6%BE%E0%A6%82%E0%A6%B2%E0%A6%BE%E0%A7%9F-%E0%A6%AA%E0%A7%9C%E0%A7%81%E0%A6%A8-orange?style=for-the-badge)](https://github.com/zakirjarir/zprinter/blob/main/README.bn.md)

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

- [🤔 What is ZPrinter? (For Beginners)](#-what-is-zprinter-for-beginners)
- [🖨️ Supported & Unsupported Printers](#️-supported--unsupported-printers)
- [🚀 Key Features](#-key-features)
- [📦 Step-by-Step Installation](#-step-by-step-installation)
- [⚙️ Native Setup (Android & iOS)](#️-native-setup-android--ios)
- [🔁 Connect Once, Print Forever (Auto-Save & Auto-Reconnect)](#-connect-once-print-forever-auto-save--auto-reconnect)
- [⚡ Ready-to-Use Ionic Vue 3 Example](#-ready-to-use-ionic-vue-3-example)
- [💻 Usage Examples (Bluetooth, USB, Network, Bangla, Raw)](#-usage-examples)
- [📖 Complete API Reference](#-complete-api-reference)
- [🧪 Local Development & Testing](#-local-development--testing)
- [🔧 Troubleshooting & FAQ](#-troubleshooting--faq)
- [🤝 Contributing & Support](#-contributing--support)
- [📄 License](#-license)

---

## 🤔 What is ZPrinter? (For Beginners)

If you are building a **POS (Point of Sale)**, shop billing, restaurant, pharmacy, or delivery app with **Ionic / Capacitor (Vue, React, or Angular)**, mobile browsers cannot directly talk to receipt printers.

**ZPrinter** solves this by bridging your JavaScript/TypeScript code directly to native Android (Java) and iOS (Swift) printer drivers:

1. **You connect your printer once** (Bluetooth, USB cable, or Wi-Fi IP address).
2. **ZPrinter saves the printer automatically** on the phone.
3. **Every time you tap Print**, even after closing and reopening the app or if the printer goes to sleep, **ZPrinter automatically reconnects and prints** without asking you to connect again.

---

## 🖨️ Supported & Unsupported Printers

### Platform Connectivity Matrix

| Connection Type | Android | iOS | Best For |
|---|---|---|---|
| **Bluetooth** | ✅ Classic SPP (with 3-tier legacy fallback) | ✅ BLE (Bluetooth 4.0+ / Dual-mode) | Portable 58mm/80mm pocket & counter printers |
| **Network (Wi-Fi / LAN TCP 9100)** | ✅ Full Support | ✅ Full Support (`Network.framework`) | Kitchen, restaurant & counter LAN/Wi-Fi printers |
| **USB (OTG Cable)** | ✅ Full Support | ❌ Not supported by iOS | Countertop USB receipt printers on Android tablets/phones |
| **Thermal (2nd USB Session)** | ✅ Full Support | ❌ Not supported by iOS | Connecting a second USB thermal printer simultaneously |

### ✅ Printers That Work Great
- Any **58mm or 80mm ESC/POS Thermal Receipt Printer**, old or new:
  - **Xprinter** (XP-58, XP-80, XP-Q80I, XP-N160II, XP-P300, etc.)
  - **Rongta** (RP58, RP80, RPP02, RPP300, etc.)
  - **Epson** (TM-T20, TM-T82, TM-T88, etc.)
  - **Bixolon, Citizen, Munbyn, Sunmi, Gprinter, Hoin, Zjiang** and generic Chinese thermal printers
- **Very Old ESC/POS Printers:** Supported via 3-tier Bluetooth fallback (`SPP` → `Insecure SPP` → `Channel 1`), custom `codePage`/`encoding`, and legacy `mode: 'bitImage'` (`ESC * 33`) image printing.
- **Label / Barcode / Star Printers (TSPL, ZPL, CPCL):** Supported via `sendRawBluetooth`, `sendRawUsb`, `sendRawThermal`, and `sendRawNetwork`.

### ❌ Printers That Do NOT Work (With Direct ESC/POS)
- **Regular Home/Office Laser or Inkjet Printers** (HP LaserJet/DeskJet, Canon PIXMA, Epson EcoTank, Samsung/Brother office printers) do not use ESC/POS — **however, you CAN print to ALL of them using `ZPrinter.printHtmlNative({ html })`** via Android Print Framework / iOS AirPrint!

---

## 🚀 Key Features

- 🔁 **Auto-Save & Auto-Reconnect** — Saves your printer in native storage (`SharedPreferences` / `UserDefaults`), auto-reconnects after app restarts, and auto-retries on broken pipes/idle timeouts.
- 🔵 **Bluetooth (Old & New)** — 3-tier Android RFCOMM fallback for legacy Bluetooth 2.0/2.1 printers + paced BLE chunked writing on iOS.
- 🌐 **Network Printing (Android & iOS)** — Fast TCP/IP printing on port `9100` (configurable port & timeout).
- 🔌 **USB OTG (Android)** — Smart printer detection, automatic permission reuse, chunked bulk transfer, and dual USB/Thermal sessions.
- 📝 **Rich Text Control** — Font size, alignment (`left` / `center` / `right`), bold, underline, line feeds, hardware `codePage`, and custom `encoding`.
- 🖼️ **Smart Image Printing** — Accepts `data:image/png;base64,...` or raw base64, auto-flattens transparent PNGs to white background, auto-scales height from `width`, and supports both `raster` (`GS v 0`) and legacy `bitImage` (`ESC * 33`) modes.
- 📊 **1D Barcodes & QR Codes** — Native ESC/POS `CODE128`, `CODE39`, `EAN13`, `EAN8`, `UPCA`, `UPCE`, `ITF`, `CODABAR`, plus UTF-8-safe QR codes with `L`/`M`/`Q`/`H` error correction.
- ✂️ **Full Hardware Control** — Full/partial paper cut (with pre-cut feed lines), cash drawer kick (Pin 2 or Pin 5), connection status checks, and raw byte sending across **all** transports.
- 📄 **HTML Engine (Image / PDF / Native Print / Thermal)** — Pass any HTML + CSS invoice/receipt (including Bangla, Arabic, tables & logos) to natively convert to **PNG/JPEG Image**, **multi-page or receipt PDF**, open the **OS Native Print Dialog** (Android Print Framework / iOS AirPrint for HP, Canon, Epson, Brother & Save as PDF), or **print HTML directly to any Thermal Printer** in one call!

---

## 📦 Step-by-Step Installation

### Requirements

| Requirement | Version |
|---|---|
| Node.js | 18+ recommended |
| Capacitor | `>= 8.0.0` |
| Android | API 24+ (Android 7.0 – Android 16) |
| iOS | iOS 15.0+ (Xcode on macOS) |

### Option 1 — Install from npm
```bash
npm install zprinter
npx cap sync
```

### Option 2 — Install directly from GitHub
```bash
npm install zakirjarir/zprinter
npx cap sync
```

### Option 3 — Install from a local folder
```bash
npm install /path/to/zprinter
npx cap sync
```

---

## ⚙️ Native Setup (Android & iOS)

### 🤖 Android

ZPrinter automatically merges all required permissions (`INTERNET`, `ACCESS_NETWORK_STATE`, `BLUETOOTH_SCAN` with `neverForLocation`, `BLUETOOTH_CONNECT`, `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, and `android.hardware.usb.host`) into your app's manifest and requests runtime permissions automatically when scanning or connecting.

> You usually do **not** need to edit `AndroidManifest.xml` manually — just run `npx cap sync android`.

If you want to verify your app's `android/app/src/main/AndroidManifest.xml`, these are the permissions used:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission
    android:name="android.permission.BLUETOOTH_SCAN"
    android:usesPermissionFlags="neverForLocation"
    tools:targetApi="s" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-feature android:name="android.hardware.usb.host" android:required="false" />
```

---

### 🍎 iOS

Add these keys to your app's `ios/App/App/Info.plist`:

```xml
<!-- Required for Bluetooth (BLE) printers -->
<key>NSBluetoothAlwaysUsageDescription</key>
<string>Used to discover and print receipts to Bluetooth thermal printers.</string>

<key>NSBluetoothPeripheralUsageDescription</key>
<string>Used to connect and print to Bluetooth thermal printers.</string>

<!-- Required for Wi-Fi / LAN Network printers -->
<key>NSLocalNetworkUsageDescription</key>
<string>Used to connect and print receipts to Wi-Fi and LAN thermal printers.</string>
```

Then run:
```bash
npx cap sync ios
```

---

## 🔁 Connect Once, Print Forever (Auto-Save & Auto-Reconnect)

In traditional POS apps, printers drop idle connections after 10–30 seconds or when the app restarts, forcing cashiers to reconnect before every invoice. **ZPrinter handles this automatically:**

1. **Connect once** (e.g., on your Settings screen):
   ```ts
   await ZPrinter.connectBluetooth({ address: '00:11:22:33:44:55' });
   ```
2. **Print anytime from any page** — even after restarting the app or after the printer goes idle:
   ```ts
   // No need to call connectBluetooth() again!
   await ZPrinter.printBluetoothText({ text: 'Invoice #105\nTotal: 550 Tk' });
   await ZPrinter.cutBluetoothPaper();
   ```
3. **Inspect, manually reconnect, or forget saved printers:**
   ```ts
   // See which printers are saved on the device
   const saved = await ZPrinter.getSavedPrinters();
   console.log(saved.bluetooth, saved.usb, saved.network);

   // Optional: proactively reconnect when app launches
   if (saved.bluetooth) {
     await ZPrinter.reconnectSavedPrinter({ type: 'bluetooth' });
   }

   // Forget a printer when switching to a new one
   await ZPrinter.disconnectBluetooth({ forget: true });
   // Or clear directly:
   await ZPrinter.clearSavedPrinter({ type: 'bluetooth' }); // or 'all'
   ```

> **USB Note (Android):** After a full phone reboot, Android resets USB OTG permission for security. Simply call `connectUsbPrinter()` once after reboot and tap **Allow** — auto-reconnect will work smoothly again after that.

---

## ⚡ Ready-to-Use Ionic Vue 3 Example

Copy and paste this into any Ionic Vue 3 page (e.g., `src/views/PrinterPage.vue`):

```vue
<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>POS Printer</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-button expand="block" @click="scanPrinters" :disabled="loading">
        {{ loading ? 'Scanning...' : '1. Scan Bluetooth Printers' }}
      </ion-button>

      <ion-list v-if="devices.length">
        <ion-item
          v-for="device in devices"
          :key="device.address"
          button
          @click="connectPrinter(device)"
        >
          <ion-label>
            <h2>{{ device.name }}</h2>
            <p>{{ device.address }}</p>
          </ion-label>
        </ion-item>
      </ion-list>

      <p v-if="savedName" style="margin-top: 12px;">
        Saved Printer: <strong>{{ savedName }}</strong>
      </p>

      <ion-button expand="block" color="success" @click="printSampleReceipt">
        2. Print Sample Receipt
      </ion-button>

      <ion-button expand="block" color="warning" @click="openDrawer">
        Open Cash Drawer
      </ion-button>

      <ion-button expand="block" color="danger" fill="outline" @click="forgetPrinter">
        Forget Saved Printer
      </ion-button>

      <p v-if="status" style="margin-top: 12px;">{{ status }}</p>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import {
  IonButton,
  IonContent,
  IonHeader,
  IonItem,
  IonLabel,
  IonList,
  IonPage,
  IonTitle,
  IonToolbar,
} from '@ionic/vue';
import { ZPrinter, type BluetoothPrinterDevice } from 'zprinter';

const loading = ref(false);
const status = ref('');
const savedName = ref('');
const devices = ref<BluetoothPrinterDevice[]>([]);

onMounted(async () => {
  const saved = await ZPrinter.getSavedPrinters();
  if (saved.bluetooth) {
    savedName.value = `${saved.bluetooth.deviceName} (${saved.bluetooth.address})`;
  }
});

async function scanPrinters() {
  loading.value = true;
  status.value = 'Scanning for Bluetooth printers...';
  try {
    const res = await ZPrinter.scanBluetoothDevices();
    devices.value = res.devices;
    status.value = `Found ${res.count} device(s)`;
  } catch (err: any) {
    status.value = `Scan failed: ${err?.message ?? err}`;
  } finally {
    loading.value = false;
  }
}

async function connectPrinter(device: BluetoothPrinterDevice) {
  status.value = `Connecting to ${device.name}...`;
  try {
    const res = await ZPrinter.connectBluetooth({ address: device.address });
    savedName.value = `${res.deviceName} (${res.deviceAddress})`;
    status.value = `Connected & saved: ${res.deviceName}`;
  } catch (err: any) {
    status.value = `Connect failed: ${err?.message ?? err}`;
  }
}

async function printSampleReceipt() {
  status.value = 'Printing...';
  try {
    // Auto-reconnects automatically if disconnected!
    await ZPrinter.printBluetoothText({
      text: 'MY SUPER SHOP',
      fontSize: 32,
      align: 'center',
      isBold: true,
      feedLines: 1,
    });

    await ZPrinter.printBluetoothText({
      text: '--------------------------------\nItem A x2              200.00\nItem B x1              350.00\n--------------------------------\nTOTAL:                 550.00 Tk',
      fontSize: 24,
      align: 'left',
      feedLines: 1,
    });

    await ZPrinter.printBluetoothBarcode({
      data: 'INV2026001',
      type: 'CODE128',
      align: 'center',
    });

    await ZPrinter.printBluetoothQRCode({
      data: 'https://myshop.example/invoice/INV2026001',
      size: 6,
      errorCorrection: 'M',
      align: 'center',
    });

    await ZPrinter.cutBluetoothPaper({ partial: false, feedLines: 3 });
    status.value = 'Printed successfully!';
  } catch (err: any) {
    status.value = `Print failed: ${err?.message ?? err}`;
  }
}

async function openDrawer() {
  try {
    await ZPrinter.kickBluetoothDrawer({ pin: 0 });
    status.value = 'Cash drawer opened!';
  } catch (err: any) {
    status.value = `Drawer failed: ${err?.message ?? err}`;
  }
}

async function forgetPrinter() {
  await ZPrinter.disconnectBluetooth({ forget: true });
  savedName.value = '';
  status.value = 'Printer disconnected and forgotten.';
}
</script>
```

---

## 💻 Usage Examples

### 🔵 Bluetooth (Android & iOS)

```ts
import { ZPrinter } from 'zprinter';

const { devices } = await ZPrinter.scanBluetoothDevices();
await ZPrinter.connectBluetooth({ address: devices[0].address });

await ZPrinter.printBluetoothText({
  text: 'Welcome to ZPrinter',
  fontSize: 32,
  align: 'center',
  isBold: true,
  isUnderline: true,
  feedLines: 2,
});

await ZPrinter.printBluetoothImage({
  base64: 'data:image/png;base64,...', // Data URI or pure base64 both work
  width: 384, // 384px for 58mm paper | 576px for 80mm paper
  align: 'center',
  mode: 'raster', // Use 'bitImage' for very old printers
});

await ZPrinter.printBluetoothBarcode({ data: '1234567890', type: 'CODE128' });
await ZPrinter.printBluetoothQRCode({ data: 'https://example.com', size: 6, errorCorrection: 'M' });
await ZPrinter.cutBluetoothPaper({ partial: false, feedLines: 3 });
await ZPrinter.kickBluetoothDrawer({ pin: 0 });
```

### 🔌 USB OTG (Android)

```ts
import { ZPrinter } from 'zprinter';

const { devices } = await ZPrinter.listUsbPrinters();
await ZPrinter.connectUsbPrinter({
  vendorId: devices[0].vendorId,
  productId: devices[0].productId,
});

await ZPrinter.printUsbText({ text: 'USB Receipt Print', align: 'center', isBold: true });
await ZPrinter.printUsbQRCode({ data: 'ORDER-501', size: 6 });
await ZPrinter.cutUsbPaper({ partial: false, feedLines: 3 });
await ZPrinter.kickUsbDrawer({ pin: 0 });
```

### 🌐 Network / Wi-Fi / LAN (Android & iOS)

```ts
import { ZPrinter } from 'zprinter';

await ZPrinter.connectNetworkPrinter({
  address: '192.168.1.100',
  port: 9100, // default is 9100
  timeout: 5000, // 5 seconds
});

await ZPrinter.printNetworkText({ text: 'Kitchen Order #12', fontSize: 32, align: 'center', isBold: true });
await ZPrinter.printNetworkBarcode({ data: 'ORD12', type: 'CODE128' });
await ZPrinter.cutNetworkPaper({ partial: true, feedLines: 3 });
```

### 📄 Native HTML → Image, PDF, OS Print & Direct Thermal Print (Easiest Way for Bangla & Complex Invoices!)

No `<canvas>` code needed! Pass any HTML + CSS structure (tables, logos, Bangla/Arabic/Unicode fonts) directly to ZPrinter:

```ts
import { ZPrinter } from 'zprinter';

const invoiceHtml = `
  <div style="padding: 12px; font-family: sans-serif;">
    <h2 style="text-align:center; margin:0;">আমার দোকান POS</h2>
    <p style="text-align:center; margin:4px 0;">ইনভয়েস #INV-2026-101</p>
    <hr/>
    <table style="width:100%; font-size:16px;">
      <tr><td>চাল ৫ কেজি</td><td style="text-align:right;">৩৫০ টাকা</td></tr>
      <tr><td>তেল ২ লিটার</td><td style="text-align:right;">৩২০ টাকা</td></tr>
      <tr><td><strong>মোট</strong></td><td style="text-align:right;"><strong>৬৭০ টাকা</strong></td></tr>
    </table>
  </div>
`;

// 1️⃣ Print HTML directly to a Bluetooth / USB / Thermal / Network receipt printer (with auto-cut & drawer!)
await ZPrinter.printHtmlToThermal({
  html: invoiceHtml,
  printerType: 'bluetooth', // 'bluetooth' | 'usb' | 'thermal' | 'network'
  width: 384, // 384 for 58mm printer | 576 for 80mm printer
  mode: 'raster', // use 'bitImage' for very old thermal printers
  cut: true,
  kickDrawer: true,
});

// 2️⃣ Convert HTML natively to a PNG or JPEG Base64 image
const img = await ZPrinter.htmlToImage({
  html: invoiceHtml,
  width: 576,
  format: 'png',
});
console.log(img.dataUrl, img.width, img.height);

// 3️⃣ Convert HTML natively to a PDF (A4, A5, Letter, Receipt58mm, Receipt80mm, or custom)
const pdf = await ZPrinter.htmlToPdf({
  html: invoiceHtml,
  pageSize: 'A4', // or 'Receipt80mm' for a continuous receipt PDF
  orientation: 'portrait',
  fileName: 'invoice-101.pdf',
  saveToFile: true,
});
console.log(pdf.filePath, pdf.pageCount, pdf.base64);

// 4️⃣ Print HTML via Device Native Print Dialog (HP, Canon, Epson, AirPrint, Mopria, Save as PDF)
await ZPrinter.printHtmlNative({
  html: invoiceHtml,
  jobName: 'Invoice 101',
  pageSize: 'A4',
});
```

### 🇧🇩 Printing Bangla / Arabic / Custom Fonts (Canvas → Image)

Most thermal printers do **not** have Bangla or custom Unicode fonts installed in firmware, so sending raw Bangla text directly to `print*Text` prints `???`. The universal, crisp solution is rendering your Bangla receipt on an HTML `<canvas>` and printing it with `print*Image`:

```ts
async function printBanglaReceipt() {
  const width = 384; // 384 for 58mm printer, 576 for 80mm printer
  const canvas = document.createElement('canvas');
  canvas.width = width;
  canvas.height = 180;

  const ctx = canvas.getContext('2d')!;
  ctx.fillStyle = '#FFFFFF';
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  ctx.fillStyle = '#000000';
  ctx.textAlign = 'center';
  ctx.font = 'bold 28px sans-serif';
  ctx.fillText('আমার দোকান POS', width / 2, 42);

  ctx.textAlign = 'left';
  ctx.font = '22px sans-serif';
  ctx.fillText('পণ্য: চাল ৫ কেজি       ৩৫০ টাকা', 10, 95);
  ctx.fillText('মোট বিল:              ৩৫০ টাকা', 10, 135);

  const base64 = canvas.toDataURL('image/png');

  await ZPrinter.printBluetoothImage({
    base64,
    width,
    align: 'center',
    mode: 'raster', // Switch to 'bitImage' for very old printers
  });
  await ZPrinter.cutBluetoothPaper();
}
```

### 🛠️ Raw Commands (Label Printers — TSPL / ZPL / CPCL / Custom ESC/POS)

```ts
// Example: Sending TSPL commands to a barcode label printer over Bluetooth/USB/Network
await ZPrinter.sendRawBluetooth({
  text: 'SIZE 50 mm, 30 mm\r\nCLS\r\nTEXT 20,20,"3",0,1,1,"PRODUCT A"\r\nPRINT 1\r\n',
});

// Or send raw ESC/POS bytes directly
await ZPrinter.sendRawNetwork({
  bytes: [0x1b, 0x40, 0x1b, 0x64, 0x03],
});
```

---

## 📖 Complete API Reference

### Saved Printers & Reconnection

| Method | Description |
|---|---|
| `getSavedPrinters()` | Returns saved `{ bluetooth?, usb?, thermal?, network? }` printer info |
| `reconnectSavedPrinter({ type })` | Reconnects to saved `'bluetooth' \| 'usb' \| 'thermal' \| 'network'` printer |
| `clearSavedPrinter({ type? })` | Clears saved info for one type or `'all'` (default: `'all'`) |

### HTML Document Engine (Android & iOS)

| Method | Description |
|---|---|
| `htmlToImage(options)` | Renders HTML+CSS natively to PNG/JPEG (`{ base64, dataUrl, width, height, mimeType }`) |
| `htmlToPdf(options)` | Renders HTML+CSS natively to multi-page or receipt PDF (`{ base64, dataUrl, pageCount, filePath? }`) |
| `printHtmlNative(options)` | Opens Android Print Framework / iOS AirPrint dialog (prints to any office/Wi-Fi printer or Save as PDF) |
| `printHtmlToThermal(options)` | Renders HTML+CSS (including Bangla/Unicode) natively and prints directly to a thermal printer with optional cut & drawer |

### All Transport Methods (Symmetric Across Bluetooth, USB, Thermal & Network)

| Action | Bluetooth | USB *(Android)* | Thermal *(Android)* | Network *(Android & iOS)* |
|---|---|---|---|---|
| Discover | `scanBluetoothDevices()` | `listUsbPrinters()` | `listUsbPrinters()` | *(IP & Port)* |
| Connect | `connectBluetooth({ address })` | `connectUsbPrinter(opts?)` | `connectThermalPrinter(opts?)` | `connectNetworkPrinter(opts)` |
| Check Status | `isBluetoothConnected()` | `isUsbConnected()` | `isThermalConnected()` | `isNetworkConnected()` |
| Print Text | `printBluetoothText(opts)` | `printUsbText(opts)` | `printThermalText(opts)` | `printNetworkText(opts)` |
| Print Image | `printBluetoothImage(opts)` | `printUsbImage(opts)` | `printThermalImage(opts)` | `printNetworkImage(opts)` |
| Print QR Code | `printBluetoothQRCode(opts)` | `printUsbQRCode(opts)` | `printThermalQRCode(opts)` | `printNetworkQRCode(opts)` |
| Print Barcode | `printBluetoothBarcode(opts)` | `printUsbBarcode(opts)` | `printThermalBarcode(opts)` | `printNetworkBarcode(opts)` |
| Send Raw Data | `sendRawBluetooth(opts)` | `sendRawUsb(opts)` | `sendRawThermal(opts)` | `sendRawNetwork(opts)` |
| Cash Drawer | `kickBluetoothDrawer(opts?)` | `kickUsbDrawer(opts?)` | `kickThermalDrawer(opts?)` | `kickNetworkDrawer(opts?)` |
| Cut Paper | `cutBluetoothPaper(opts?)` | `cutUsbPaper(opts?)` | `cutThermalPaper(opts?)` | `cutNetworkPaper(opts?)` |
| Disconnect | `disconnectBluetooth(opts?)` | `disconnectUsbPrinter(opts?)` | `disconnectThermalPrinter(opts?)` | `disconnectNetworkPrinter(opts?)` |

### Option Reference

#### `PrinterTextOptions`
| Option | Type | Default | Description |
|---|---|---|---|
| `text` | `string` | *(required)* | Text to print |
| `fontSize` | `number` | `24` | `<32` normal, `>=32` double (2x), `>=48` triple (3x) |
| `align` | `'left' \| 'center' \| 'right'` | `'left'` | Alignment |
| `isBold` | `boolean` | `false` | Bold text |
| `isUnderline` | `boolean` | `false` | Underlined text |
| `feedLines` | `number` | `2` | Line feeds after text |
| `encoding` | `string` | `'UTF-8'` | Character encoding (e.g., `'CP437'`, `'CP850'`, `'ISO-8859-1'`, `'GBK'`) |
| `codePage` | `number` | — | Optional hardware code table byte (`ESC t n`, e.g., `0` for PC437) |
| `reset` | `boolean` | `true` | Send `ESC @` before printing |

#### `PrinterImageOptions`
| Option | Type | Default | Description |
|---|---|---|---|
| `base64` | `string` | *(required)* | Base64 string (with or without `data:image/...;base64,` prefix) |
| `width` | `number` | `0` | Target width in px (`384` for 58mm, `576` for 80mm). Scales height automatically if `height` is omitted |
| `height` | `number` | `0` | Optional target height in px |
| `align` | `'left' \| 'center' \| 'right'` | `'left'` | Alignment |
| `threshold` | `number` | `128` | Black/white darkness threshold (`0–255`) |
| `mode` | `'raster' \| 'bitImage'` | `'raster'` | `'raster'` (`GS v 0`, fastest) or `'bitImage'` (`ESC * 33`, for very old printers) |
| `feedLines` | `number` | `1` | Line feeds after image |

#### `PrinterQRCodeOptions`
| Option | Type | Default | Description |
|---|---|---|---|
| `data` | `string` | *(required)* | QR content (supports UTF-8) |
| `size` | `number` | `8` | Module size (`1–16`) |
| `align` | `'left' \| 'center' \| 'right'` | `'center'` | Alignment |
| `errorCorrection` | `'L' \| 'M' \| 'Q' \| 'H'` | `'L'` | Error correction level |
| `feedLines` | `number` | `1` | Line feeds after QR |

#### `PrinterBarcodeOptions`
| Option | Type | Default | Description |
|---|---|---|---|
| `data` | `string` | *(required)* | Barcode content |
| `type` | `'CODE128' \| 'CODE39' \| 'EAN13' \| 'EAN8' \| 'UPCA' \| 'UPCE' \| 'ITF' \| 'CODABAR'` | `'CODE128'` | Barcode symbology |
| `height` | `number` | `80` | Height in dots (`1–255`) |
| `width` | `number` | `2` | Bar thickness (`2–6`) |
| `align` | `'left' \| 'center' \| 'right'` | `'center'` | Alignment |
| `hriPosition` | `'none' \| 'above' \| 'below' \| 'both'` | `'below'` | Human-readable text position |
| `feedLines` | `number` | `1` | Line feeds after barcode |

#### `PrinterCutOptions`, `PrinterDrawerOptions`, `PrinterRawDataOptions`, `PrinterDisconnectOptions`
- **`PrinterCutOptions`:** `{ partial?: boolean; feedLines?: number }` (defaults: `partial: false`, `feedLines: 3`)
- **`PrinterDrawerOptions`:** `{ pin?: 0 | 1; onTime?: number; offTime?: number }` (defaults: `pin: 0`, `onTime: 25`, `offTime: 250`)
- **`PrinterRawDataOptions`:** `{ base64?: string; bytes?: number[]; text?: string }` (provide any one)
- **`PrinterDisconnectOptions`:** `{ forget?: boolean }` (default: `false`; set `true` to stop auto-reconnecting)

---

## 🧪 Local Development & Testing

```bash
# Install dependencies & build TypeScript/Rollup + docs
npm install
npm run build

# Lint & check formatting
npm run eslint
npm run prettier -- --check

# Verify Android build, Lint & Unit/Mock-Printer tests
npm run verify:android

# Verify iOS build (requires macOS + Xcode)
npm run verify:ios
```

---

## 🔧 Troubleshooting & FAQ

| Problem | Solution |
|---|---|
| 🔁 **Do I have to reconnect before every print?** | **No!** ZPrinter automatically saves your printer and reconnects/retries on every print, even after app restart or printer sleep. |
| 🇧🇩 **Bangla / Arabic text prints as `???`** | Most thermal printers lack built-in Bangla fonts. Render your receipt on an HTML `<canvas>` and print it using `print*Image` (see the Bangla example above). |
| 🖼️ **Very old printer prints garbage for images** | Pass `mode: 'bitImage'` in `print*Image(...)` to use legacy `ESC * 33` graphics mode instead of `GS v 0`. |
| 💵 **Cash drawer does not open** | Some cash drawers are wired to Pin 5 instead of Pin 2. Try `kick*Drawer({ pin: 1 })`. |
| 🔵 **Bluetooth scan returns 0 devices** | Ensure Bluetooth is ON and grant the runtime permission prompt. On Android 7–11, Location/GPS must also be turned ON for Bluetooth discovery. |
| 🔌 **USB printer not detected on Android** | Ensure your phone supports USB OTG, use a working OTG adapter/cable, and tap **Allow** on the USB permission prompt. |
| 🌐 **Network printer fails to connect** | Confirm the phone and printer are on the same Wi-Fi/LAN network, the IP & port (`9100`) are correct, and on iOS you added `NSLocalNetworkUsageDescription` to `Info.plist`. |
| 🍎 **USB printer on iOS?** | Apple iOS does not allow generic USB printer access. Use a **Bluetooth (BLE)** or **Wi-Fi / LAN Network** thermal printer on iOS. |

---

## 🤝 Contributing & Support

Contributions are welcome!

> **Fork** → **Branch** → **Pull Request**

### 📬 Support

| Channel | Link |
|---|---|
| 📧 Email | [zakirjarir@gmail.com](mailto:zakirjarir@gmail.com) |
| 🐛 Issues | [github.com/zakirjarir/zprinter/issues](https://github.com/zakirjarir/zprinter/issues) |

---

## 📄 License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

---

<div align="center">

Made with ❤️ by [zakirjarir](https://github.com/zakirjarir)

⭐ If this plugin helped you, please consider starring the repo!

</div>
