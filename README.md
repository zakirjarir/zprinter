# ZPrinter — Capacitor Thermal Printer Plugin (English)

ZPrinter is a native Capacitor plugin that enables Ionic/Capacitor apps to print to thermal receipt printers using Bluetooth, USB (Android), or Network (TCP/IP). This README is a complete, detailed guide covering installation, native setup, API, examples, development and troubleshooting.

Table of contents
- Quick summary
- Features
- Installation (preferred: GitHub package)
- Native setup (Android & iOS)
- Usage examples (Bluetooth, USB, Network, Image, QR)
- API reference (methods and options)
- Example app & local development
- Building, testing and publishing
- Troubleshooting & FAQ
- Contributing & Support
- License

---

Quick summary

- Supports: Bluetooth (Android & iOS), USB (Android), Network/TCP (Android)
- Print types: text, images (base64), QR codes, cash drawer kick, auto-cut
- Capacitor compatibility: 8.x+

---

Features
- Scan and connect to Bluetooth printers (BLE/classic)
- USB OTG support for Android
- Network printing over raw TCP/IP (port 9100 common)
- Image rendering optimized for thermal printers
- ESC/POS QR code generation
- Cash drawer kick and auto paper cut commands

---

Installation

Preferred (install directly from GitHub repository — keeps package in sync with repo):

```bash
npm install zakirjarir/zprinter
npx cap sync
```

Alternative: install published npm package (if you prefer the npm registry):

```bash
npm install zprinter
npx cap sync
```

Local development (work on the plugin and example-app together):

```bash
# from repo root
npm install
# go to example-app
cd example-app
npm install
npm start
```

Requirements & notes
- Node 18+ recommended; npm 8+ or compatible
- Capacitor 8+ (peer dependency)
- For native verifies you need Xcode (macOS) and Android SDK + Gradle

---

Native setup

Android
1) AndroidManifest.xml (app/src/main/AndroidManifest.xml):

```xml
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.usb.host" android:required="false" />
```

2) Runtime permissions: request BLUETOOTH_SCAN/BLUETOOTH_CONNECT and ACCESS_FINE_LOCATION on Android 12+ before scanning/connecting.
3) Ensure targetSdkVersion and compileSdkVersion align with Android permissions model (12+ adjustments).

iOS
1) Add usage descriptions to Info.plist:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>Required to discover and print to thermal printers.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>Required to discover and print to thermal printers.</string>
```

2) If your app requires Bluetooth background modes, add the appropriate background modes and justify them in App Store submission.

---

Basic usage examples

Bluetooth (scan, connect, print text, disconnect):

```ts
import { ZPrinter } from 'zprinter';

// 1. Scan
const { devices } = await ZPrinter.scanBluetoothDevices();
// choose a device
const addr = devices[0].address; // Android: MAC, iOS: UUID

// 2. Connect
await ZPrinter.connectBluetooth({ address: addr });

// 3. Print text
await ZPrinter.printBluetoothText({ text: 'Hello from ZPrinter\n', fontSize: 24, align: 'left', feedLines: 2 });

// 4. Print image
await ZPrinter.printBluetoothImage({ base64: 'data:image/png;base64,...', width: 384, align: 'center' });

// 5. Actions
await ZPrinter.cutBluetoothPaper();
await ZPrinter.kickBluetoothDrawer();

// 6. Disconnect
await ZPrinter.disconnectBluetooth();
```

USB (Android only)

```ts
const { devices } = await ZPrinter.listUsbPrinters();
await ZPrinter.connectUsbPrinter({ vendorId: devices[0].vendorId, productId: devices[0].productId });
await ZPrinter.printUsbText({ text: 'USB Print\n' });
await ZPrinter.disconnectBluetooth();
```

Network/TCP (Android only)

```ts
await ZPrinter.connectNetworkPrinter({ address: '192.168.1.100', port: 9100 });
await ZPrinter.printNetworkText({ text: 'Network print\n' });
await ZPrinter.disconnectBluetooth();
```

Image and QR guidance
- Use monochrome images with white background; convert to 1-bit dithered PNG for best results.
- For QR codes, use the plugin's QR API for optimal printer rendering.

---

API reference (short)

- scanBluetoothDevices(): Promise<{ devices: Device[] }>
- connectBluetooth({ address: string }): Promise<void>
- disconnectBluetooth(): Promise<void>
- printBluetoothText(options: { text: string; fontSize?: number; align?: 'left'|'center'|'right'; isBold?: boolean; feedLines?: number })
- printBluetoothImage(options: { base64: string; width?: number; align?: string })
- printBluetoothQRCode(options: { data: string; size?: number; align?: string })
- cutBluetoothPaper(): Promise<void>
- kickBluetoothDrawer(): Promise<void>
- listUsbPrinters(): Promise<{ devices: UsbDevice[] }> (Android)
- connectUsbPrinter({ vendorId, productId }): Promise<void> (Android)
- printUsbText(options): Promise<void> (Android)
- connectNetworkPrinter({ address, port }): Promise<void> (Android)
- printNetworkText(options): Promise<void> (Android)

See src/ for TypeScript types and full option fields.

---

Example app & development
- example-app demonstrates scanning, connecting and printing flows.
- To run locally: cd example-app && npm install && npm start
- To test native changes: npx cap sync && open platform project in Xcode or Android Studio.

Building & publishing
- Build: npm run build (generates dist/)
- Verify iOS: npm run verify:ios (requires Xcode)
- Verify Android: npm run verify:android (requires Android SDK)
- When publishing to npm or GitHub Packages, ensure build artifacts (dist/) are included.

Troubleshooting & FAQ
- Image prints black: convert to white-background, 1-bit monochrome.
- No Bluetooth found: request runtime permissions on Android and enable Location services.
- USB not detected: try a powered OTG cable; ensure Android manifest has usb.host feature and app has proper permissions.
- Network connect fails: verify IP, port and network/subnet.

Migration notes
- If upgrading from older Capacitor or Android SDK versions, check permission changes (Android 12+), and update manifest accordingly.

Contributing
- Fork -> branch -> PR. Run existing linters/tests locally. Open an issue for large changes.

Support
- Email: zakirjarir@gmail.com
- Issues: https://github.com/zakirjarir/zprinter/issues

License
- MIT

---

(Install command corrected: prefer `npm install zakirjarir/zprinter` when you want the repository package.)
