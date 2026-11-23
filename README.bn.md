<div align="center">

<img src="https://img.shields.io/badge/ZPrinter-Capacitor%20Plugin-6C63FF?style=for-the-badge&logo=ionic&logoColor=white" alt="ZPrinter" />

<h1>🖨️ ZPrinter — সম্পূর্ণ Capacitor থার্মাল প্রিন্টার প্লাগইন</h1>

<p>Ionic (Vue, React, Angular) এবং Capacitor মোবাইল অ্যাপ থেকে <strong>পুরনো ও নতুন প্রায় সব থার্মাল রিসিট প্রিন্টারে</strong> — <strong>ব্লুটুথ</strong>, <strong>USB OTG</strong> এবং <strong>নেটওয়ার্ক (Wi-Fi / LAN)</strong> এর মাধ্যমে রিসিট, লোগো, বাংলা বিল, বারকোড, QR কোড প্রিন্ট ও ক্যাশ ড্রয়ার খোলার পূর্ণাঙ্গ নেটিভ প্লাগইন — বিল্ট-ইন <strong>অটো-সেভ ও অটো-রিকানেক্ট</strong> সুবিধাসহ।</p>

<br/>
<p align="center">

[![English](https://img.shields.io/badge/English-Read-blue?style=for-the-badge&logo=github)](https://github.com/zakirjarir/zprinter/blob/main/README.md)

</p>
<br/>

[![npm](https://img.shields.io/badge/npm-zprinter-CB3837?style=flat-square&logo=npm)](https://www.npmjs.com/package/zprinter)
[![Capacitor](https://img.shields.io/badge/Capacitor-8.x%2B-119EFF?style=flat-square&logo=capacitor)](https://capacitorjs.com/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-green?style=flat-square)](https://github.com/zakirjarir/zprinter)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![Issues](https://img.shields.io/badge/Issues-GitHub-black?style=flat-square&logo=github)](https://github.com/zakirjarir/zprinter/issues)

</div>

---

## 📋 বিষয়সূচি

- [🤔 ZPrinter কী এবং কেন ব্যবহার করবেন? (একদম নতুনদের জন্য)](#-zprinter-কী-এবং-কেন-ব্যবহার-করবেন-একদম-নতুনদের-জন্য)
- [🖨️ কোন কোন প্রিন্টারে কাজ করবে এবং কোনগুলোতে করবে না](#️-কোন-কোন-প্রিন্টারে-কাজ-করবে-এবং-কোনগুলোতে-করবে-না)
- [🚀 মূল ফিচারসমূহ](#-মূল-ফিচারসমূহ)
- [📦 ধাপে ধাপে ইনস্টলেশন](#-ধাপে-ধাপে-ইনস্টলেশন)
- [⚙️ Android ও iOS সেটআপ](#️-android-ও-ios-সেটআপ)
- [🔁 একবার কানেক্ট করুন, বারবার প্রিন্ট করুন (অটো-সেভ ও অটো-রিকানেক্ট)](#-একবার-কানেক্ট-করুন-বারবার-প্রিন্ট-করুন-অটো-সেভ-ও-অটো-রিকানেক্ট)
- [⚡ রেডিমেড Ionic Vue 3 উদাহরণ (কপি-পেস্ট করেই চালান)](#-রেডিমেড-ionic-vue-3-উদাহরণ-কপি-পেস্ট-করেই-চালান)
- [💻 সব ধরনের প্রিন্টিং উদাহরণ (Bluetooth, USB, Wi-Fi, বাংলা রিসিট, Raw)](#-সব-ধরনের-প্রিন্টিং-উদাহরণ)
- [📖 সম্পূর্ণ API রেফারেন্স](#-সম্পূর্ণ-api-রেফারেন্স)
- [🧪 লোকাল ডেভেলপমেন্ট ও টেস্ট](#-লোকাল-ডেভেলপমেন্ট-ও-টেস্ট)
- [🔧 সাধারণ সমস্যা ও সহজ সমাধান (FAQ)](#-সাধারণ-সমস্যা-ও-সহজ-সমাধান-faq)
- [🤝 অবদান ও সাপোর্ট](#-অবদান-ও-সাপোর্ট)
- [📄 লাইসেন্স](#-লাইসেন্স)

---

## 🤔 ZPrinter কী এবং কেন ব্যবহার করবেন? (একদম নতুনদের জন্য)

আপনি যদি দোকান, রেস্টুরেন্ট, ফার্মেসি, ডেলিভারি বা সুপারশপের জন্য **Ionic / Capacitor (Vue, React বা Angular)** দিয়ে POS অ্যাপ বানান, তবে সাধারণ ওয়েব কোড থেকে সরাসরি থার্মাল রিসিট প্রিন্টারে প্রিন্ট করা যায় না।

**ZPrinter** ঠিক এই কাজটাই একদম সহজ করে দেয়:

1. **প্রথমবার শুধু একবার প্রিন্টার কানেক্ট করবেন** (ব্লুটুথ, USB কেবল, অথবা Wi-Fi IP দিয়ে)।
2. **ZPrinter প্রিন্টারের তথ্য ফোনে স্থায়ীভাবে সেভ করে রাখবে।**
3. **পরের বার যেকোনো পেজ থেকে Print বাটনে চাপলেই** — এমনকি অ্যাপ বন্ধ করে আবার খুললেও বা প্রিন্টার স্লিপ মোডে গেলেও — **প্লাগইন নিজে থেকেই রিকানেক্ট করে প্রিন্ট করে দেবে!** বারবার কানেক্ট করার ঝামেলা নেই।

---

## 🖨️ কোন কোন প্রিন্টারে কাজ করবে এবং কোনগুলোতে করবে না

### প্ল্যাটফর্ম অনুযায়ী সংযোগ সাপোর্ট

| সংযোগ পদ্ধতি | Android | iOS | কখন ব্যবহার করবেন |
|---|---|---|---|
| **Bluetooth (ব্লুটুথ)** | ✅ Classic SPP (পুরনো প্রিন্টারের জন্য ৩-স্তরের ফলব্যাক) | ✅ BLE (Bluetooth 4.0+ / Dual-mode) | পোর্টেবল 58mm / 80mm ও কাউন্টার প্রিন্টার |
| **Network (Wi-Fi / LAN পোর্ট 9100)** | ✅ পূর্ণ সাপোর্ট | ✅ পূর্ণ সাপোর্ট (`Network.framework`) | কিচেন, রেস্টুরেন্ট ও ওয়াই-ফাই/ল্যান প্রিন্টার |
| **USB (OTG কেবল)** | ✅ পূর্ণ সাপোর্ট | ❌ iOS-এ সাপোর্ট নেই | Android ফোন/ট্যাবলেটে তারযুক্ত USB প্রিন্টার |
| **Thermal (২য় USB সেশন)** | ✅ পূর্ণ সাপোর্ট | ❌ iOS-এ সাপোর্ট নেই | একসাথে ২টি USB থার্মাল প্রিন্টার চালানোর জন্য |

### ✅ যেসব প্রিন্টারে চমৎকার কাজ করবে
- যেকোনো **58mm বা 80mm ESC/POS থার্মাল রিসিট প্রিন্টার** (পুরনো ও নতুন সব):
  - **Xprinter** (XP-58, XP-80, XP-Q80I, XP-N160II, XP-P300 ইত্যাদি)
  - **Rongta** (RP58, RP80, RPP02, RPP300 ইত্যাদি)
  - **Epson** (TM-T20, TM-T82, TM-T88 ইত্যাদি)
  - **Bixolon, Citizen, Munbyn, Sunmi, Gprinter, Hoin, Zjiang** এবং যেকোনো চায়না ESC/POS প্রিন্টার
- **খুব পুরনো ESC/POS প্রিন্টার:** ৩-স্তরের Bluetooth কানেকশন (`SPP` → `Insecure SPP` → `Channel 1`), কাস্টম `codePage`/`encoding`, এবং পুরনো প্রিন্টারের জন্য `mode: 'bitImage'` (`ESC * 33`) সাপোর্ট আছে।
- **Barcode / Sticker Label প্রিন্টার (TSPL, ZPL, CPCL) বা Star প্রিন্টার:** `sendRawBluetooth`, `sendRawUsb`, `sendRawThermal`, ও `sendRawNetwork` দিয়ে সরাসরি র-কমান্ড পাঠিয়ে প্রিন্ট করা যাবে।

### ❌ যেসব প্রিন্টারে সরাসরি ESC/POS কাজ করবে না
- **সাধারণ অফিস/হোম লেজার বা ইংকজেট প্রিন্টার** (যেমন HP LaserJet/DeskJet, Canon PIXMA, Epson EcoTank, Samsung লেজার) ESC/POS বোঝে না — **তবে `ZPrinter.printHtmlNative({ html })` ব্যবহার করে Android Print / iOS AirPrint-এর মাধ্যমে এই সব অফিস প্রিন্টারেও প্রিন্ট করতে পারবেন!**

---

## 🚀 মূল ফিচারসমূহ

- 🔁 **অটো-সেভ ও অটো-রিকানেক্ট** — একবার কানেক্ট করলেই ফোনে সেভ থাকে; অ্যাপ রিস্টার্ট বা প্রিন্টার সকেট কেটে দিলে নিজে থেকেই রিকানেক্ট ও রিট্রাই করে।
- 🔵 **পুরনো ও নতুন ব্লুটুথ সাপোর্ট** — Android-এ ৩-স্তরের ফলব্যাক + চাঙ্কড রাইট, iOS-এ নিরাপদ BLE চাঙ্ক কিউ।
- 🌐 **নেটওয়ার্ক প্রিন্টিং (Android ও iOS দুটোতেই)** — Wi-Fi/LAN প্রিন্টারে সরাসরি TCP পোর্ট `9100`-এ প্রিন্ট।
- 🔌 **USB OTG (Android)** — স্মার্ট প্রিন্টার ডিটেকশন, একবার Allow করলেই বারবার পারমিশন না চাওয়া, এবং বড় ইমেজের জন্য চাঙ্কড ট্রান্সফার।
- 📝 **টেক্সট কন্ট্রোল** — ফন্ট সাইজ, অ্যালাইনমেন্ট (`left` / `center` / `right`), বোল্ড, আন্ডারলাইন, লাইন ফিড, `codePage` ও `encoding`।
- 🖼️ **স্মার্ট ইমেজ প্রিন্টিং** — `data:image/png;base64,...` সরাসরি সাপোর্ট, transparent PNG-এর জন্য অটো সাদা ব্যাকগ্রাউন্ড, শুধু `width` দিলেই অটো `height` স্কেলিং, এবং `raster` ও পুরনো প্রিন্টারের জন্য `bitImage` মোড।
- 📊 **বারকোড ও QR কোড** — `CODE128`, `CODE39`, `EAN13`, `EAN8`, `UPCA`, `UPCE`, `ITF`, `CODABAR` এবং বাংলা/UTF-8 সাপোর্টেড QR কোড (`L`/`M`/`Q`/`H`)।
- ✂️ **পূর্ণ হার্ডওয়্যার কন্ট্রোল** — সব কানেকশনেই Full/Partial পেপার কাট, ক্যাশ ড্রয়ার খোলা (Pin 2 / Pin 5), কানেকশন স্ট্যাটাস চেক এবং Raw বাইট পাঠানো।
- 📄 **HTML ইঞ্জিন (Image / PDF / Native Device Print / সরাসরি থার্মাল প্রিন্ট)** — যেকোনো HTML + CSS রিসিট বা ইনভয়েস (বাংলা ফন্ট, টেবিল, লোগোসহ) দিলেই নেটিভভাবে **PNG/JPEG ছবি**, **PDF**, **ফোনের নেটিভ প্রিন্ট ডায়ালগ** (HP, Canon, Epson, AirPrint, Save as PDF), অথবা **সরাসরি থার্মাল প্রিন্টারে** প্রিন্ট করা যাবে!

---

## 📦 ধাপে ধাপে ইনস্টলেশন

### প্রয়োজনীয়তা

| প্রয়োজনীয়তা | ভার্সন |
|---|---|
| Node.js | 18+ |
| Capacitor | `>= 8.0.0` |
| Android | API 24+ (Android 7.0 থেকে Android 16) |
| iOS | iOS 15.0+ (macOS-এ Xcode) |

### পদ্ধতি ১ — npm থেকে ইনস্টল
```bash
npm install zprinter
npx cap sync
```

### পদ্ধতি ২ — সরাসরি GitHub থেকে ইনস্টল
```bash
npm install zakirjarir/zprinter
npx cap sync
```

### পদ্ধতি ৩ — লোকাল ফোল্ডার থেকে ইনস্টল
```bash
npm install /path/to/zprinter
npx cap sync
```

---

## ⚙️ Android ও iOS সেটআপ

### 🤖 Android

ZPrinter নিজে থেকেই প্রয়োজনীয় সব পারমিশন (`INTERNET`, `ACCESS_NETWORK_STATE`, `BLUETOOTH_SCAN` + `neverForLocation`, `BLUETOOTH_CONNECT`, `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` এবং `usb.host`) আপনার অ্যাপের ম্যানিফেস্টে মার্জ করে নেয় এবং রানটাইম পারমিশনও নিজেই চায়।

> সাধারণত আলাদা করে `AndroidManifest.xml` এডিট করার দরকার হয় না — শুধু `npx cap sync android` চালালেই হবে।

প্রয়োজনে যাচাই করার জন্য পারমিশনগুলো নিচে দেওয়া হলো:

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

আপনার অ্যাপের `ios/App/App/Info.plist` ফাইলে নিচের কী-গুলো যোগ করুন:

```xml
<!-- ব্লুটুথ (BLE) প্রিন্টারের জন্য -->
<key>NSBluetoothAlwaysUsageDescription</key>
<string>ব্লুটুথ থার্মাল প্রিন্টার খুঁজতে ও রিসিট প্রিন্ট করতে অনুমতি প্রয়োজন।</string>

<key>NSBluetoothPeripheralUsageDescription</key>
<string>ব্লুটুথ প্রিন্টারের সাথে সংযুক্ত হতে অনুমতি প্রয়োজন।</string>

<!-- Wi-Fi / LAN নেটওয়ার্ক প্রিন্টারের জন্য -->
<key>NSLocalNetworkUsageDescription</key>
<string>Wi-Fi বা LAN থার্মাল প্রিন্টারে রিসিট প্রিন্ট করতে লোকাল নেটওয়ার্ক অনুমতি প্রয়োজন।</string>
```

এরপর চালান:
```bash
npx cap sync ios
```

---

## 🔁 একবার কানেক্ট করুন, বারবার প্রিন্ট করুন (অটো-সেভ ও অটো-রিকানেক্ট)

অনেক POS অ্যাপে একবার প্রিন্ট করার পরের বার আবার নতুন করে প্রিন্টার কানেক্ট করতে হয়। **ZPrinter-এ এই সমস্যা চিরতরে সমাধান করা হয়েছে:**

1. **প্রথমবার Settings পেজ থেকে শুধু একবার কানেক্ট করুন:**
   ```ts
   await ZPrinter.connectBluetooth({ address: '00:11:22:33:44:55' });
   ```
2. **এরপর যেকোনো পেজ থেকে — এমনকি অ্যাপ বন্ধ করে আবার খুললেও — সরাসরি প্রিন্ট করুন:**
   ```ts
   // আর connectBluetooth() কল করার দরকার নেই!
   await ZPrinter.printBluetoothText({ text: 'Invoice #105\nTotal: 550 Tk' });
   await ZPrinter.cutBluetoothPaper();
   ```
3. **সেভ করা প্রিন্টার দেখা, রিকানেক্ট করা বা মুছে ফেলা:**
   ```ts
   // কোন কোন প্রিন্টার সেভ আছে দেখুন
   const saved = await ZPrinter.getSavedPrinters();
   console.log(saved.bluetooth, saved.usb, saved.network);

   // চাইলে অ্যাপ ওপেন হলেই আগে থেকে রিকানেক্ট করে রাখতে পারেন
   if (saved.bluetooth) {
     await ZPrinter.reconnectSavedPrinter({ type: 'bluetooth' });
   }

   // নতুন প্রিন্টার বদলাতে চাইলে পুরনোটা ভুলে যান
   await ZPrinter.disconnectBluetooth({ forget: true });
   // অথবা:
   await ZPrinter.clearSavedPrinter({ type: 'bluetooth' }); // সব মুছতে 'all'
   ```

> **USB নোট (Android):** ফোন পুরোপুরি রিস্টার্ট করলে Android নিরাপত্তার জন্য USB পারমিশন রিসেট করে। তখন শুধু একবার `connectUsbPrinter()` কল করে **Allow** চাপলেই আবার অটো-রিকানেক্ট স্বাভাবিকভাবে চলবে।

---

## ⚡ রেডিমেড Ionic Vue 3 উদাহরণ (কপি-পেস্ট করেই চালান)

যেকোনো Ionic Vue 3 পেজে (যেমন `src/views/PrinterPage.vue`) সরাসরি এই কোড বসিয়ে ব্যবহার করতে পারবেন:

```vue
<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>POS প্রিন্টার</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <ion-button expand="block" @click="scanPrinters" :disabled="loading">
        {{ loading ? 'খোঁজা হচ্ছে...' : '১. ব্লুটুথ প্রিন্টার খুঁজুন' }}
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
        সেভ করা প্রিন্টার: <strong>{{ savedName }}</strong>
      </p>

      <ion-button expand="block" color="success" @click="printSampleReceipt">
        ২. টেস্ট রিসিট প্রিন্ট করুন
      </ion-button>

      <ion-button expand="block" color="warning" @click="openDrawer">
        ক্যাশ ড্রয়ার খুলুন
      </ion-button>

      <ion-button expand="block" color="danger" fill="outline" @click="forgetPrinter">
        সেভ করা প্রিন্টার মুছুন
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
  status.value = 'ব্লুটুথ প্রিন্টার খোঁজা হচ্ছে...';
  try {
    const res = await ZPrinter.scanBluetoothDevices();
    devices.value = res.devices;
    status.value = `${res.count}টি ডিভাইস পাওয়া গেছে`;
  } catch (err: any) {
    status.value = `স্ক্যান ব্যর্থ: ${err?.message ?? err}`;
  } finally {
    loading.value = false;
  }
}

async function connectPrinter(device: BluetoothPrinterDevice) {
  status.value = `${device.name}-এ কানেক্ট হচ্ছে...`;
  try {
    const res = await ZPrinter.connectBluetooth({ address: device.address });
    savedName.value = `${res.deviceName} (${res.deviceAddress})`;
    status.value = `কানেক্ট ও সেভ হয়েছে: ${res.deviceName}`;
  } catch (err: any) {
    status.value = `কানেক্ট ব্যর্থ: ${err?.message ?? err}`;
  }
}

async function printSampleReceipt() {
  status.value = 'প্রিন্ট হচ্ছে...';
  try {
    // ডিসকানেক্ট থাকলেও নিজে থেকেই রিকানেক্ট হয়ে প্রিন্ট হবে!
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
    status.value = 'প্রিন্ট সফল হয়েছে!';
  } catch (err: any) {
    status.value = `প্রিন্ট ব্যর্থ: ${err?.message ?? err}`;
  }
}

async function openDrawer() {
  try {
    await ZPrinter.kickBluetoothDrawer({ pin: 0 });
    status.value = 'ক্যাশ ড্রয়ার খোলা হয়েছে!';
  } catch (err: any) {
    status.value = `ড্রয়ার ব্যর্থ: ${err?.message ?? err}`;
  }
}

async function forgetPrinter() {
  await ZPrinter.disconnectBluetooth({ forget: true });
  savedName.value = '';
  status.value = 'প্রিন্টার ডিসকানেক্ট ও রিমুভ করা হয়েছে।';
}
</script>
```

---

## 💻 সব ধরনের প্রিন্টিং উদাহরণ

### 🔵 ব্লুটুথ (Android ও iOS)

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
  base64: 'data:image/png;base64,...',
  width: 384, // 58mm পেপারে 384 | 80mm পেপারে 576
  align: 'center',
  mode: 'raster', // খুব পুরনো প্রিন্টারে 'bitImage' ব্যবহার করুন
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

### 🌐 নেটওয়ার্ক / Wi-Fi / LAN (Android ও iOS)

```ts
import { ZPrinter } from 'zprinter';

await ZPrinter.connectNetworkPrinter({
  address: '192.168.1.100',
  port: 9100, // ডিফল্ট 9100
  timeout: 5000,
});

await ZPrinter.printNetworkText({ text: 'Kitchen Order #12', fontSize: 32, align: 'center', isBold: true });
await ZPrinter.printNetworkBarcode({ data: 'ORD12', type: 'CODE128' });
await ZPrinter.cutNetworkPaper({ partial: true, feedLines: 3 });
```

### 📄 নেটিভ HTML → ছবি, PDF, ডিভাইস প্রিন্ট ও সরাসরি থার্মাল প্রিন্ট (বাংলা ইনভয়েসের সবচেয়ে সহজ উপায়!)

আলাদা কোনো `<canvas>` কোড লিখতে হবে না! শুধু সাধারণ HTML + CSS দিন—বাংলা লেখা, টেবিল, লোগো সবকিছু নেটিভভাবে সুন্দর হয়ে প্রিন্ট বা PDF/Image হয়ে যাবে:

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

// ১️⃣ HTML সরাসরি Bluetooth / USB / Thermal / Network থার্মাল প্রিন্টারে প্রিন্ট (অটো কাট + ড্রয়ারসহ!)
await ZPrinter.printHtmlToThermal({
  html: invoiceHtml,
  printerType: 'bluetooth', // 'bluetooth' | 'usb' | 'thermal' | 'network'
  width: 384, // 58mm প্রিন্টারে 384 | 80mm প্রিন্টারে 576
  mode: 'raster', // খুব পুরনো প্রিন্টারে 'bitImage' দিন
  cut: true,
  kickDrawer: true,
});

// ২️⃣ HTML থেকে নেটিভভাবে PNG / JPEG ছবি (Base64) তৈরি
const img = await ZPrinter.htmlToImage({
  html: invoiceHtml,
  width: 576,
  format: 'png',
});
console.log(img.dataUrl, img.width, img.height);

// ৩️⃣ HTML থেকে নেটিভভাবে PDF তৈরি (A4, A5, Letter, Receipt58mm, Receipt80mm বা custom)
const pdf = await ZPrinter.htmlToPdf({
  html: invoiceHtml,
  pageSize: 'A4', // রিসিট সাইজ PDF চাইলে 'Receipt80mm' দিন
  orientation: 'portrait',
  fileName: 'invoice-101.pdf',
  saveToFile: true,
});
console.log(pdf.filePath, pdf.pageCount, pdf.base64);

// ৪️⃣ ফোনের নেটিভ প্রিন্ট ডায়ালগ দিয়ে প্রিন্ট (HP, Canon, Epson, AirPrint, Save as PDF)
await ZPrinter.printHtmlNative({
  html: invoiceHtml,
  jobName: 'Invoice 101',
  pageSize: 'A4',
});
```

### 🇧🇩 বাংলা রিসিট কীভাবে প্রিন্ট করবেন? (সবচেয়ে সহজ উপায়)

বেশিরভাগ থার্মাল প্রিন্টারে বিল্ট-ইন বাংলা ফন্ট থাকে না, তাই সরাসরি `print*Text`-এ বাংলা পাঠালে `???` আসতে পারে। **১০০% পরিষ্কার বাংলা রিসিট প্রিন্ট করার সেরা উপায়** হলো HTML `<canvas>`-এ বাংলা লিখে `print*Image` দিয়ে প্রিন্ট করা:

```ts
async function printBanglaReceipt() {
  const width = 384; // 58mm প্রিন্টারে 384, 80mm প্রিন্টারে 576
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
    mode: 'raster', // খুব পুরনো প্রিন্টার হলে 'bitImage' দিন
  });
  await ZPrinter.cutBluetoothPaper();
}
```

### 🛠️ Raw কমান্ড (লেবেল প্রিন্টার — TSPL / ZPL / CPCL / কাস্টম ESC/POS)

```ts
// উদাহরণ: বারকোড স্টিকার লেবেল প্রিন্টারে TSPL কমান্ড পাঠানো
await ZPrinter.sendRawBluetooth({
  text: 'SIZE 50 mm, 30 mm\r\nCLS\r\nTEXT 20,20,"3",0,1,1,"PRODUCT A"\r\nPRINT 1\r\n',
});

// অথবা সরাসরি বাইট অ্যারে পাঠানো
await ZPrinter.sendRawNetwork({
  bytes: [0x1b, 0x40, 0x1b, 0x64, 0x03],
});
```

---

## 📖 সম্পূর্ণ API রেফারেন্স

### সেভ করা প্রিন্টার ও রিকানেক্ট

| মেথড | কাজ |
|---|---|
| `getSavedPrinters()` | ফোনে সেভ থাকা `{ bluetooth?, usb?, thermal?, network? }` প্রিন্টারের তথ্য দেয় |
| `reconnectSavedPrinter({ type })` | সেভ করা `'bluetooth' \| 'usb' \| 'thermal' \| 'network'` প্রিন্টারে রিকানেক্ট করে |
| `clearSavedPrinter({ type? })` | নির্দিষ্ট প্রিন্টার অথবা `'all'` (ডিফল্ট) মুছে ফেলে |

### HTML ডকুমেন্ট ইঞ্জিন (Android ও iOS)

| মেথড | কাজ |
|---|---|
| `htmlToImage(options)` | HTML+CSS থেকে নেটিভ PNG/JPEG ছবি তৈরি করে (`{ base64, dataUrl, width, height, mimeType }`) |
| `htmlToPdf(options)` | HTML+CSS থেকে নেটিভ মাল্টি-পেজ বা রিসিট PDF তৈরি করে (`{ base64, dataUrl, pageCount, filePath? }`) |
| `printHtmlNative(options)` | Android Print / iOS AirPrint ডায়ালগ খোলে (যেকোনো অফিস/Wi-Fi প্রিন্টার বা Save as PDF) |
| `printHtmlToThermal(options)` | বাংলা/HTML রিসিট নেটিভভাবে রেন্ডার করে সরাসরি থার্মাল প্রিন্টারে প্রিন্ট করে (অপশনাল কাট ও ড্রয়ারসহ) |

### সব কানেকশনের পূর্ণাঙ্গ মেথড তালিকা

| কাজ | Bluetooth | USB *(Android)* | Thermal *(Android)* | Network *(Android ও iOS)* |
|---|---|---|---|---|
| খোঁজা | `scanBluetoothDevices()` | `listUsbPrinters()` | `listUsbPrinters()` | *(IP ও Port)* |
| কানেক্ট | `connectBluetooth({ address })` | `connectUsbPrinter(opts?)` | `connectThermalPrinter(opts?)` | `connectNetworkPrinter(opts)` |
| স্ট্যাটাস চেক | `isBluetoothConnected()` | `isUsbConnected()` | `isThermalConnected()` | `isNetworkConnected()` |
| টেক্সট প্রিন্ট | `printBluetoothText(opts)` | `printUsbText(opts)` | `printThermalText(opts)` | `printNetworkText(opts)` |
| ছবি প্রিন্ট | `printBluetoothImage(opts)` | `printUsbImage(opts)` | `printThermalImage(opts)` | `printNetworkImage(opts)` |
| QR কোড | `printBluetoothQRCode(opts)` | `printUsbQRCode(opts)` | `printThermalQRCode(opts)` | `printNetworkQRCode(opts)` |
| বারকোড | `printBluetoothBarcode(opts)` | `printUsbBarcode(opts)` | `printThermalBarcode(opts)` | `printNetworkBarcode(opts)` |
| Raw ডেটা | `sendRawBluetooth(opts)` | `sendRawUsb(opts)` | `sendRawThermal(opts)` | `sendRawNetwork(opts)` |
| ক্যাশ ড্রয়ার | `kickBluetoothDrawer(opts?)` | `kickUsbDrawer(opts?)` | `kickThermalDrawer(opts?)` | `kickNetworkDrawer(opts?)` |
| পেপার কাট | `cutBluetoothPaper(opts?)` | `cutUsbPaper(opts?)` | `cutThermalPaper(opts?)` | `cutNetworkPaper(opts?)` |
| ডিসকানেক্ট | `disconnectBluetooth(opts?)` | `disconnectUsbPrinter(opts?)` | `disconnectThermalPrinter(opts?)` | `disconnectNetworkPrinter(opts?)` |

### অপশনগুলোর বিবরণ

#### `PrinterTextOptions`
| অপশন | ধরন | ডিফল্ট | বিবরণ |
|---|---|---|---|
| `text` | `string` | *(আবশ্যিক)* | যে টেক্সট প্রিন্ট হবে |
| `fontSize` | `number` | `24` | `<32` সাধারণ, `>=32` ২ গুণ বড়, `>=48` ৩ গুণ বড় |
| `align` | `'left' \| 'center' \| 'right'` | `'left'` | বামে, মাঝে বা ডানে |
| `isBold` | `boolean` | `false` | মোটা (বোল্ড) লেখা |
| `isUnderline` | `boolean` | `false` | লেখার নিচে দাগ |
| `feedLines` | `number` | `2` | লেখার পর কয় লাইন ফাঁকা ছাড়বে |
| `encoding` | `string` | `'UTF-8'` | পুরনো প্রিন্টারের এনকোডিং (যেমন `'CP437'`, `'CP850'`, `'ISO-8859-1'`, `'GBK'`) |
| `codePage` | `number` | — | হার্ডওয়্যার কোডপেজ নম্বর (`ESC t n`, যেমন `0`) |
| `reset` | `boolean` | `true` | প্রিন্টের আগে `ESC @` রিসেট পাঠাবে কি না |

#### `PrinterImageOptions`
| অপশন | ধরন | ডিফল্ট | বিবরণ |
|---|---|---|---|
| `base64` | `string` | *(আবশ্যিক)* | Base64 ছবি (`data:image/...;base64,` থাকলেও চলবে) |
| `width` | `number` | `0` | পিক্সেলে প্রস্থ (`384` হলো 58mm, `576` হলো 80mm); শুধু `width` দিলে `height` অটো ঠিক হয় |
| `height` | `number` | `0` | ঐচ্ছিক উচ্চতা |
| `align` | `'left' \| 'center' \| 'right'` | `'left'` | অ্যালাইনমেন্ট |
| `threshold` | `number` | `128` | কালো/সাদা গাঢ়ত্ব (`0–255`) |
| `mode` | `'raster' \| 'bitImage'` | `'raster'` | `'raster'` (`GS v 0`, দ্রুত) অথবা `'bitImage'` (`ESC * 33`, খুব পুরনো প্রিন্টারের জন্য) |
| `feedLines` | `number` | `1` | ছবির পর ফাঁকা লাইন |

#### `PrinterQRCodeOptions` ও `PrinterBarcodeOptions`
- **`PrinterQRCodeOptions`:** `{ data, size?: 1..16, align?, errorCorrection?: 'L' | 'M' | 'Q' | 'H', feedLines? }`
- **`PrinterBarcodeOptions`:** `{ data, type?: 'CODE128' | 'CODE39' | 'EAN13' | 'EAN8' | 'UPCA' | 'UPCE' | 'ITF' | 'CODABAR', height?: 80, width?: 2, align?, hriPosition?: 'none' | 'above' | 'below' | 'both', feedLines? }`
- **`PrinterCutOptions`:** `{ partial?: false, feedLines?: 3 }`
- **`PrinterDrawerOptions`:** `{ pin?: 0 | 1, onTime?: 25, offTime?: 250 }`
- **`PrinterRawDataOptions`:** `{ base64?, bytes?, text? }` (যেকোনো একটি দিন)
- **`PrinterDisconnectOptions`:** `{ forget?: boolean }` (`true` দিলে সেভ করা প্রিন্টারও মুছে যাবে)

---

## 🧪 লোকাল ডেভেলপমেন্ট ও টেস্ট

```bash
# ডিপেন্ডেন্সি ইনস্টল ও বিল্ড
npm install
npm run build

# লিন্ট ও ফরম্যাট চেক
npm run eslint
npm run prettier -- --check

# Android বিল্ড, লিন্ট ও ইউনিট/মক-প্রিন্টার টেস্ট
npm run verify:android

# iOS বিল্ড যাচাই (macOS + Xcode প্রয়োজন)
npm run verify:ios
```

---

## 🔧 সাধারণ সমস্যা ও সহজ সমাধান (FAQ)

| সমস্যা | সহজ সমাধান |
|---|---|
| 🔁 **প্রতিবার প্রিন্টের আগে কি আবার কানেক্ট করতে হবে?** | **না!** একবার কানেক্ট করলেই সেভ থাকে এবং পরের প্রতিটি প্রিন্টে (অ্যাপ রিস্টার্টের পরও) নিজেই রিকানেক্ট ও রিট্রাই করে। |
| 🇧🇩 **বাংলা লেখা `???` প্রিন্ট হচ্ছে** | থার্মাল প্রিন্টারে বাংলা ফন্ট থাকে না। উপরের বাংলা উদাহরণের মতো HTML `<canvas>` দিয়ে ছবি বানিয়ে `print*Image` দিয়ে প্রিন্ট করুন। |
| 🖼️ **খুব পুরনো প্রিন্টারে ছবি ভেঙে/অদ্ভুত আসছে** | `print*Image`-এ `mode: 'bitImage'` অপশন দিন—এটি পুরনো `ESC * 33` ফরম্যাটে প্রিন্ট করবে। |
| 💵 **ক্যাশ ড্রয়ার খুলছে না** | কিছু ক্যাশ ড্রয়ার Pin 2-এর বদলে Pin 5-এ লাগানো থাকে। `kick*Drawer({ pin: 1 })` দিয়ে চেষ্টা করুন। |
| 🔵 **ব্লুটুথ স্ক্যানে কোনো ডিভাইস আসছে না** | ফোনের Bluetooth অন আছে কি না দেখুন এবং পারমিশন Allow করুন। Android 7–11 হলে ফোনের Location/GPS অন রাখতে হবে। |
| 🔌 **Android-এ USB প্রিন্টার পাচ্ছে না** | ফোন OTG সাপোর্ট করে কি না ও ভালো OTG কেবল ব্যবহার করছেন কি না দেখুন, এবং USB পারমিশন ডায়ালগে **Allow** চাপুন। |
| 🌐 **Wi-Fi / LAN প্রিন্টারে কানেক্ট হচ্ছে না** | ফোন ও প্রিন্টার একই Wi-Fi রাউটারে আছে কি না এবং IP ও পোর্ট (`9100`) ঠিক আছে কি না দেখুন। iOS হলে `Info.plist`-এ `NSLocalNetworkUsageDescription` যোগ করুন। |
| 🍎 **iOS-এ USB প্রিন্টার চলবে কি?** | Apple iOS জেনেরিক USB প্রিন্টার সাপোর্ট করে না। iOS-এ **Bluetooth (BLE)** অথবা **Wi-Fi / LAN নেটওয়ার্ক** প্রিন্টার ব্যবহার করুন। |

---

## 🤝 অবদান ও সাপোর্ট

অবদান স্বাগত!

> **Fork করুন** → **Branch তৈরি করুন** → **Pull Request দিন**

### 📬 সাপোর্ট

| মাধ্যম | লিংক |
|---|---|
| 📧 ইমেইল | [zakirjarir@gmail.com](mailto:zakirjarir@gmail.com) |
| 🐛 ইস্যু | [github.com/zakirjarir/zprinter/issues](https://github.com/zakirjarir/zprinter/issues) |

---

## 📄 লাইসেন্স

এই প্রজেক্টটি **MIT লাইসেন্স** এর অধীনে প্রকাশিত। বিস্তারিত দেখতে [LICENSE](LICENSE) দেখুন।

---

<div align="center">

❤️ দিয়ে তৈরি করেছেন [zakirjarir](https://github.com/zakirjarir)

⭐ এই প্লাগইনটি আপনার কাজে লাগলে রিপোতে একটি স্টার দিন!

</div>
