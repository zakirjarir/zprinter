# ZPrinter — Capacitor Thermal Printer Plugin (বাংলা)

এই READMEটি পুরোপুরি বাংলায় লেখা হয়েছে যাতে আপনি ইনস্টলেশন, নেটিভ সেটআপ, ইউজেজ, ডিবাগ এবং কনট্রিবিউশন সহজে বুঝতে পারেন।

সারাংশ
ZPrinter একটি Capacitor প্লাগইন যা থার্মাল রিসিপ্ট প্রিন্টারে প্রিন্ট করার সুবিধা দেয় — ব্লুটুথ (Android & iOS), USB (Android) এবং নেটওয়ার্ক (TCP/IP, Android)। এটি টেক্সট, ইমেজ (বেইজ64), কিউআর, কাশ ড্রয়ারের কিক এবং অটো-কাট সাপোর্ট করে।

ইনস্টলেশন (গিটহাব থেকে — সুপারিশকৃত)

```bash
npm install zakirjarir/zprinter
npx cap sync
```

অথবা npm রেজিস্ট্রি থেকে:

```bash
npm install zprinter
npx cap sync
```

লোকাল ডেভেলপমেন্ট

```bash
# রেপো রুট থেকে
npm install
cd example-app
npm install
npm start
```

নেটিভ সেটআপ

Android
- android/app/src/main/AndroidManifest.xml-এ নিচের পারমিশনগুলো যোগ করুন:

```xml
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.usb.host" android:required="false" />
```

- Android 12+ এ রানটাইম পারমিশন (BLUETOOTH_SCAN/CONNECT, ACCESS_FINE_LOCATION) চাইবে — এগুলো অনুরোধ করে নিন।

iOS
- Info.plist-এ ব্লুটুথ ইউসেজ স্ট্রিং যোগ করুন:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>থার্মাল প্রিন্টার খুঁজে পেতে ও প্রিন্ট করতে ব্লুটুথ প্রয়োজন।</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>থার্মাল প্রিন্টার খুঁজে পেতে ও প্রিন্ট করতে ব্লুটুথ প্রয়োজন।</string>
```

ব্যবহার (সংক্ষিপ্ত উদাহরণ)

```ts
import { ZPrinter } from 'zprinter';

const { devices } = await ZPrinter.scanBluetoothDevices();
await ZPrinter.connectBluetooth({ address: devices[0].address });
await ZPrinter.printBluetoothText({ text: 'হ্যালো\n', fontSize: 24 });
await ZPrinter.printBluetoothImage({ base64: 'data:image/png;base64,...', width: 384 });
await ZPrinter.cutBluetoothPaper();
await ZPrinter.kickBluetoothDrawer();
await ZPrinter.disconnectBluetooth();
```

ট্রাবলশুটিং
- ইমেজ কালো আসে: ইমেজের ব্যাকগ্রাউন্ড সাদা রাখুন ও 1-bit মনোক্রোম কনভার্সন ব্যবহার করুন।
- ব্লুটুথ ডিভাইস খুঁজে না পাই: Android-এ লোকেশন চালু করুন এবং পারমিশন দিন; iOS-এ ব্লুটুথ অন আছে কিনা ও Info.plist স্ট্রিং আছে কিনা দেখুন।
- USB ডিটেকশন নেই: OTG কেবল ভালো কিনা দেখুন ও প্রিন্টার চালু আছে কিনা নিশ্চিত করুন।
- নেটওয়ার্ক কানেক্ট ব্যর্থ: IP/পোর্ট ঠিক আছে কিনা ও একই সাবনেটে আছে কিনা যাচাই করুন।

কন্ট্রিবিউশন
- Fork করুন, ব্রাঞ্চ করে PR দিন। বড় পরিবর্তনের জন্য আগে ইস্যু খুলে আলোচনা করুন।

সাপোর্ট
- Email: zakirjarir@gmail.com
- Issues: https://github.com/zakirjarir/zprinter/issues

লাইসেন্স
- MIT
