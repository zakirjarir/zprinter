<div align="center">

<img src="https://img.shields.io/badge/ZPrinter-Capacitor%20Plugin-6C63FF?style=for-the-badge&logo=ionic&logoColor=white" alt="ZPrinter" />

<h1>🖨️ ZPrinter — ক্যাপাসিটর প্রিন্টার প্লাগইন</h1>

<p>একটি নেটিভ Capacitor প্লাগইন যা Ionic/Capacitor অ্যাপকে থার্মাল রিসিট প্রিন্টারে প্রিন্ট করতে সক্ষম করে — <strong>ব্লুটুথ</strong>, <strong>USB</strong> (Android), অথবা <strong>নেটওয়ার্ক (TCP/IP)</strong> এর মাধ্যমে।</p>

<p align="center">

[![বাংলা](https://img.shields.io/badge/%E0%A6%AC%E0%A6%BE%E0%A6%82%E0%A6%B2%E0%A6%BE-%E0%A6%AA%E0%A6%A5%E0%A6%A8-orange?style=for-the-badge)](https://github.com/zakirjarir/zprinter/blob/main/README.bn.md) &nbsp; [![English](https://img.shields.io/badge/English-Read-blue?style=for-the-badge&logo=github)](https://github.com/zakirjarir/zprinter/blob/main/README.md)

</p>

<br/>

<br/>

[![npm](https://img.shields.io/badge/npm-zprinter-CB3837?style=flat-square&logo=npm)](https://www.npmjs.com/package/zprinter)
[![Capacitor](https://img.shields.io/badge/Capacitor-8.x%2B-119EFF?style=flat-square&logo=capacitor)](https://capacitorjs.com/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green?style=flat-square)](https://github.com/zakirjarir/zprinter)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![Issues](https://img.shields.io/badge/Issues-GitHub-black?style=flat-square&logo=github)](https://github.com/zakirjarir/zprinter/issues)

</div>

---

## 📋 বিষয়সূচি

- [✨ সংক্ষিপ্ত পরিচিতি](#-সংক্ষিপ্ত-পরিচিতি)
- [🚀 বৈশিষ্ট্যসমূহ](#-বৈশিষ্ট্যসমূহ)
- [📦 ইনস্টলেশন](#-ইনস্টলেশন)
- [⚙️ নেটিভ সেটআপ](#️-নেটিভ-সেটআপ)
- [💻 ব্যবহারের উদাহরণ](#-ব্যবহারের-উদাহরণ)
- [📖 API রেফারেন্স](#-api-রেফারেন্স)
- [🧪 উদাহরণ অ্যাপ ও লোকাল ডেভেলপমেন্ট](#-উদাহরণ-অ্যাপ--লোকাল-ডেভেলপমেন্ট)
- [🏗️ বিল্ড ও পাবলিশ](#️-বিল্ড--পাবলিশ)
- [🔧 সমস্যা সমাধান ও FAQ](#-সমস্যা-সমাধান--faq)
- [🤝 অবদান ও সাপোর্ট](#-অবদান--সাপোর্ট)
- [📄 লাইসেন্স](#-লাইসেন্স)

---

## ✨ সংক্ষিপ্ত পরিচিতি

| বিষয় | বিবরণ |
|---|---|
| **সংযোগ পদ্ধতি** | ব্লুটুথ (Android ও iOS), USB (Android), নেটওয়ার্ক/TCP (Android) |
| **প্রিন্টের ধরন** | টেক্সট, ছবি (base64), QR কোড, ক্যাশ ড্রয়ার কিক, অটো-কাট |
| **Capacitor** | **8.x+** এর সাথে সামঞ্জস্যপূর্ণ |

---

## 🚀 বৈশিষ্ট্যসমূহ

- 🔵 **ব্লুটুথ** — BLE/ক্লাসিক থার্মাল প্রিন্টার স্ক্যান ও সংযোগ
- 🔌 **USB OTG** — Android-এর জন্য পূর্ণ USB সাপোর্ট
- 🌐 **নেটওয়ার্ক** — র‍্যাও TCP/IP প্রিন্টিং (ডিফল্ট পোর্ট: 9100)
- 🖼️ **ছবি রেন্ডারিং** — থার্মাল প্রিন্টারের জন্য অপ্টিমাইজড base64 ইমেজ আউটপুট
- 📱 **QR কোড** — নেটিভ ESC/POS QR কোড জেনারেশন
- ✂️ **এক্সট্রা ফিচার** — ক্যাশ ড্রয়ার কিক ও অটো পেপার কাট কমান্ড

---

## 📦 ইনস্টলেশন

### ✅ সেরা পদ্ধতি — GitHub থেকে ইনস্টল (রিপোর সাথে সর্বদা আপডেটেড থাকে)

```bash
npm install zakirjarir/zprinter
npx cap sync
```

### 🛠️ লোকাল ডেভেলপমেন্ট

```bash
# রিপোর রুট থেকে
git clone git@github.com:zakirjarir/zprinter.git
npm install

# এরপর example-app এ যান
cd example-app
npm install
npm start
```

### 📋 প্রয়োজনীয় সরঞ্জাম

| প্রয়োজনীয়তা | সংস্করণ |
|---|---|
| Node | ১৮+ (প্রস্তাবিত) |
| npm | ৮+ বা সামঞ্জস্যপূর্ণ |
| Capacitor | ৮+ (peer dependency) |
| Xcode | iOS নেটিভ যাচাইয়ের জন্য (শুধু macOS) |
| Android SDK + Gradle | Android নেটিভ যাচাইয়ের জন্য |

---

## ⚙️ নেটিভ সেটআপ

### 🤖 Android

**১. `AndroidManifest.xml` আপডেট করুন** (`app/src/main/AndroidManifest.xml`):

```xml
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
<uses-feature android:name="android.hardware.usb.host" android:required="false" />
```

**২. রানটাইম পারমিশন:**
**Android 12+** এ স্ক্যান বা সংযোগের আগে `BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT` এবং `ACCESS_FINE_LOCATION` পারমিশন চাইতে হবে।

**৩. SDK সংস্করণ:**
`targetSdkVersion` ও `compileSdkVersion` যেন Android 12+ পারমিশন মডেলের সাথে সামঞ্জস্যপূর্ণ হয় তা নিশ্চিত করুন।

---

### 🍎 iOS

**১. `Info.plist` আপডেট করুন** — ব্লুটুথ ব্যবহারের বিবরণ যোগ করুন:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>থার্মাল প্রিন্টার খুঁজে পেতে এবং প্রিন্ট করতে প্রয়োজন।</string>

<key>NSBluetoothPeripheralUsageDescription</key>
<string>থার্মাল প্রিন্টার খুঁজে পেতে এবং প্রিন্ট করতে প্রয়োজন।</string>
```

**২. ব্যাকগ্রাউন্ড মোড (ঐচ্ছিক):**
অ্যাপে ব্লুটুথ ব্যাকগ্রাউন্ড মোড প্রয়োজন হলে, উপযুক্ত মোড যোগ করুন এবং App Store সাবমিশনে তা ব্যাখ্যা করুন।

---

## 💻 ব্যবহারের উদাহরণ

### 🔵 ব্লুটুথ — স্ক্যান, সংযোগ, প্রিন্ট, বিচ্ছিন্ন

```typescript
import { ZPrinter } from 'zprinter';

// ১️⃣ ডিভাইস স্ক্যান করুন
const { devices } = await ZPrinter.scanBluetoothDevices();
const addr = devices[0].address; // Android: MAC অ্যাড্রেস | iOS: UUID

// ২️⃣ সংযোগ করুন
await ZPrinter.connectBluetooth({ address: addr });

// ৩️⃣ টেক্সট প্রিন্ট করুন
await ZPrinter.printBluetoothText({
  text: 'ZPrinter থেকে হ্যালো!\n',
  fontSize: 24,
  align: 'left',
  feedLines: 2
});

// ৪️⃣ ছবি প্রিন্ট করুন (base64)
await ZPrinter.printBluetoothImage({
  base64: 'data:image/png;base64,...',
  width: 384,
  align: 'center'
});

// ৫️⃣ অতিরিক্ত অ্যাকশন
await ZPrinter.cutBluetoothPaper();
await ZPrinter.kickBluetoothDrawer();

// ৬️⃣ সংযোগ বিচ্ছিন্ন করুন
await ZPrinter.disconnectBluetooth();
```

---

### 🔌 USB — শুধুমাত্র Android

```typescript
const { devices } = await ZPrinter.listUsbPrinters();

await ZPrinter.connectUsbPrinter({
  vendorId: devices[0].vendorId,
  productId: devices[0].productId
});

await ZPrinter.printUsbText({ text: 'USB প্রিন্ট\n' });
```

---

### 🌐 নেটওয়ার্ক / TCP — শুধুমাত্র Android

```typescript
await ZPrinter.connectNetworkPrinter({
  address: '192.168.1.100',
  port: 9100
});

await ZPrinter.printNetworkText({ text: 'নেটওয়ার্ক প্রিন্ট\n' });
```

---

### 🖼️ ছবি ও QR কোড টিপস

> 💡 **ছবির জন্য:** সাদা ব্যাকগ্রাউন্ড সহ মনোক্রোম ছবি ব্যবহার করুন। সেরা ফলাফলের জন্য **১-বিট ডিদারড PNG** ফরম্যাটে রূপান্তর করুন।
>
> 📱 **QR কোডের জন্য:** থার্মাল প্রিন্টারে সর্বোত্তম ESC/POS রেন্ডারিংয়ের জন্য প্লাগইনের বিল্ট-ইন QR API ব্যবহার করুন।

---

## 📖 API রেফারেন্স

### ব্লুটুথ

| মেথড | বিবরণ |
|---|---|
| `scanBluetoothDevices()` | `{ devices: Device[] }` রিটার্ন করে — আবিষ্কৃত ব্লুটুথ প্রিন্টারগুলো |
| `connectBluetooth({ address })` | MAC (Android) বা UUID (iOS) দিয়ে প্রিন্টারে সংযোগ |
| `disconnectBluetooth()` | বর্তমান ব্লুটুথ প্রিন্টার থেকে সংযোগ বিচ্ছিন্ন |
| `printBluetoothText(options)` | ফরম্যাটিং সহ টেক্সট প্রিন্ট |
| `printBluetoothImage(options)` | base64 ছবি প্রিন্ট |
| `printBluetoothQRCode(options)` | ESC/POS এর মাধ্যমে QR কোড প্রিন্ট |
| `cutBluetoothPaper()` | অটো পেপার কাট চালু করুন |
| `kickBluetoothDrawer()` | ক্যাশ ড্রয়ার কিক কমান্ড পাঠান |

#### `printBluetoothText` অপশনসমূহ

| অপশন | ধরন | ডিফল্ট | বিবরণ |
|---|---|---|---|
| `text` | `string` | — | প্রিন্ট করার টেক্সট |
| `fontSize` | `number` | — | ফন্ট সাইজ |
| `align` | `'left' \| 'center' \| 'right'` | `'left'` | টেক্সট অ্যালাইনমেন্ট |
| `isBold` | `boolean` | `false` | বোল্ড ফরম্যাটিং |
| `feedLines` | `number` | — | টেক্সটের পরে লাইন ফিড সংখ্যা |

#### `printBluetoothImage` অপশনসমূহ

| অপশন | ধরন | বিবরণ |
|---|---|---|
| `base64` | `string` | Base64 এনকোডেড ছবির স্ট্রিং |
| `width` | `number` | পিক্সেলে প্রিন্টের প্রস্থ |
| `align` | `string` | ছবির অ্যালাইনমেন্ট |

#### `printBluetoothQRCode` অপশনসমূহ

| অপশন | ধরন | বিবরণ |
|---|---|---|
| `data` | `string` | QR-এ এনকোড করার ডেটা |
| `size` | `number` | QR কোডের আকার |
| `align` | `string` | QR কোডের অ্যালাইনমেন্ট |

---

### USB *(শুধুমাত্র Android)*

| মেথড | বিবরণ |
|---|---|
| `listUsbPrinters()` | `{ devices: UsbDevice[] }` রিটার্ন করে |
| `connectUsbPrinter({ vendorId, productId })` | USB প্রিন্টারে সংযোগ |
| `printUsbText(options)` | USB এর মাধ্যমে টেক্সট প্রিন্ট |

---

### নেটওয়ার্ক *(শুধুমাত্র Android)*

| মেথড | বিবরণ |
|---|---|
| `connectNetworkPrinter({ address, port })` | TCP/IP এর মাধ্যমে সংযোগ |
| `printNetworkText(options)` | নেটওয়ার্কের মাধ্যমে টেক্সট প্রিন্ট |

> 📁 সম্পূর্ণ TypeScript টাইপ ও সকল অপশন ফিল্ডের জন্য `src/` দেখুন।

---

## 🧪 উদাহরণ অ্যাপ ও লোকাল ডেভেলপমেন্ট

`example-app` ডিরেক্টরিতে সম্পূর্ণ স্ক্যানিং, সংযোগ ও প্রিন্টিং ফ্লো দেখানো হয়েছে।

```bash
# উদাহরণ অ্যাপ লোকালি চালান
cd example-app
npm install
npm start
```

**নেটিভ পরিবর্তন পরীক্ষা করতে:**
```bash
npx cap sync
# এরপর Xcode বা Android Studio-তে প্ল্যাটফর্ম প্রজেক্ট খুলুন
```

---

## 🏗️ বিল্ড ও পাবলিশ

| কমান্ড | বিবরণ |
|---|---|
| `npm run build` | প্লাগইন কম্পাইল করুন — আউটপুট `dist/` এ তৈরি হবে |
| `npm run verify:ios` | iOS বিল্ড যাচাই *(macOS-এ Xcode প্রয়োজন)* |
| `npm run verify:android` | Android বিল্ড যাচাই *(Android SDK প্রয়োজন)* |

> ⚠️ npm বা GitHub Packages-এ পাবলিশ করার সময় নিশ্চিত করুন যে বিল্ড আর্টিফ্যাক্ট (`dist/`) প্যাকেজে অন্তর্ভুক্ত আছে।

---

## 🔧 সমস্যা সমাধান ও FAQ

| সমস্যা | সমাধান |
|---|---|
| 🖼️ **ছবি সম্পূর্ণ কালো প্রিন্ট হচ্ছে** | প্রিন্টের আগে ছবিকে সাদা ব্যাকগ্রাউন্ড সহ ১-বিট মনোক্রোম ফরম্যাটে রূপান্তর করুন |
| 🔵 **কোনো ব্লুটুথ ডিভাইস পাওয়া যাচ্ছে না** | Android-এ রানটাইম পারমিশন চাইুন; লোকেশন সার্ভিস চালু আছে কিনা নিশ্চিত করুন |
| 🔌 **USB প্রিন্টার ধরা পড়ছে না** | পাওয়ারড OTG কেবল ব্যবহার করুন; Android manifest-এ `usb.host` ফিচার আছে কিনা এবং প্রয়োজনীয় পারমিশন আছে কিনা যাচাই করুন |
| 🌐 **নেটওয়ার্ক সংযোগ ব্যর্থ হচ্ছে** | IP অ্যাড্রেস, পোর্ট নম্বর এবং ডিভাইসটি একই সাবনেটে আছে কিনা দুবার যাচাই করুন |

---

### 🔀 মাইগ্রেশন নোট

পুরনো Capacitor বা Android SDK সংস্করণ থেকে আপগ্রেড করলে:
- **Android 12+** এ আনা পারমিশন পরিবর্তনগুলো পর্যালোচনা করুন
- উপরের নেটিভ সেটআপ অনুযায়ী `AndroidManifest.xml` আপডেট করুন

---

## 🤝 অবদান ও সাপোর্ট

অবদান স্বাগত! অনুগ্রহ করে এই প্রক্রিয়া অনুসরণ করুন:

> **Fork করুন** → **Branch তৈরি করুন** → **Pull Request দিন**

- সাবমিট করার আগে লোকালি লিন্টার ও টেস্ট চালান
- বড় বা ব্রেকিং পরিবর্তনের জন্য আগে একটি Issue খুলুন

### 📬 সাপোর্ট

| চ্যানেল | লিংক |
|---|---|
| 📧 ইমেইল | [zakirjarir@gmail.com](mailto:zakirjarir@gmail.com) |
| 🐛 ইস্যু | [github.com/zakirjarir/zprinter/issues](https://github.com/zakirjarir/zprinter/issues) |

---

## 📄 লাইসেন্স

এই প্রজেক্ট **MIT লাইসেন্স** এর অধীনে প্রকাশিত।

---

<div align="center">

❤️ দিয়ে তৈরি করেছেন [zakirjarir](https://github.com/zakirjarir)

⭐ এই প্লাগইন যদি আপনার কাজে লেগে থাকে, তাহলে রিপোতে একটি স্টার দিন!

</div>