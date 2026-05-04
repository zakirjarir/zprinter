# ZPrinter — Capacitor Thermal Printer Plugin

বাংলা: এই README ফাইলটি প্রোজেক্ট ইনস্টলেশন, ব্যবহার এবং ডিবাগ করার জন্য সম্পূর্ণ নির্দেশনা দেয়। ইংরেজি কোড স্নিপেটগুলো সরাসরি কপি-পেস্ট করে ব্যবহার করা যাবে।

---

## Quick overview
ZPrinter is a Capacitor native plugin for printing to thermal printers (Bluetooth, USB, Network). It supports text, images, QR codes, cash drawer kick and auto-cut.

## Install (app using the plugin)
1) Install from npm (published package):

```bash
npm install zprinter
npx cap sync
```

2) If you are developing locally (example-app in this repo):
```bash
# from example-app folder
npm install
# top-level package is linked via file:.. in example-app/package.json; run the example
npm start
```

Notes:
- Ensure Node 18+ and npm 8+ (or current LTS) are installed.
- Capacitor 8+ is required (peer dependency).

## Native setup
### Android
Add these permissions to android/app/src/main/AndroidManifest.xml (update according to your target SDK):

```xml
<!-- Required for Bluetooth (Android 12+) -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Network & USB -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.usb.host" android:required="false" />
```

Runtime permissions (BLUETOOTH_SCAN/CONNECT and Location) must be requested on Android 12+.

### iOS
Add Bluetooth usage descriptions to your Info.plist:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>Required to discover and print to thermal printers.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>Required to discover and print to thermal printers.</string>
```

Also ensure you follow Apple guidelines for background/bluetooth if your app needs it.

## Basic usage (JavaScript/TypeScript)
```ts
import { ZPrinter } from 'zprinter';

// Scan
const { devices } = await ZPrinter.scanBluetoothDevices();
// Connect (Android: MAC, iOS: UUID)
await ZPrinter.connectBluetooth({ address: 'DEVICE_ADDRESS' });
// Print text
await ZPrinter.printBluetoothText({ text: 'Hello\n', fontSize: 24 });
// Print image (base64)
await ZPrinter.printBluetoothImage({ base64: 'data:image/png;base64,...', width: 384 });
// Cut & drawer
await ZPrinter.cutBluetoothPaper();
await ZPrinter.kickBluetoothDrawer();
// Disconnect
await ZPrinter.disconnectBluetooth();
```

## API surface (summary)
- scanBluetoothDevices()
- connectBluetooth({ address })
- disconnectBluetooth()
- printBluetoothText(options)
- printBluetoothImage({ base64, width })
- printBluetoothQRCode({ data, size })
- cutBluetoothPaper()
- kickBluetoothDrawer()
- listUsbPrinters() (Android)
- connectUsbPrinter({ vendorId, productId }) (Android)
- printUsbText(options) (Android)
- connectNetworkPrinter({ address, port }) (Android)
- printNetworkText(options) (Android)

(Refer to plugin types in src/ for full option fields.)

## Example (full flow)
See the example-app folder for a minimal Capacitor app wired to this plugin. It shows scanning, connecting and printing flows.

## Troubleshooting
- Image prints as solid black: use white background and convert to 1-bit monochrome.
- No Bluetooth devices found: enable Location (Android) and grant BLUETOOTH_SCAN permission; on iOS ensure Bluetooth is enabled and permission strings are present.
- USB not detected: use powered OTG cable and check android:requestLegacyExternalStorage if older Android versions.
- Network issues: confirm device and printer are on same network and port 9100 is open.

## What was fixed/checked
- README reorganized for clarity and Bengali summary added.
- Verified example-app uses the local plugin via file:.. in package.json.
- Podspec reads package.json version; source_files glob is correct.

If you want, run the verification scripts in package.json (requires Xcode/Android SDK):

```bash
npm run verify
```

## Contributing
Fork, open an issue for large changes and submit PRs. Keep tests and platform build green.

## License
MIT

---

Support / Contact: zakirjarir@gmail.com
