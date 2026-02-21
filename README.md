# zprinter

this is a printer plugin

## Install

```bash
npm install zakirjarir/zprinter
npx cap sync
```

## API

<docgen-index>

* [`scanBluetoothDevices()`](#scanbluetoothdevices)
* [`connectBluetooth(...)`](#connectbluetooth)
* [`printBluetoothText(...)`](#printbluetoothtext)
* [`cutBluetoothPaper()`](#cutbluetoothpaper)
* [`disconnectBluetooth()`](#disconnectbluetooth)
* [`connectUsbPrinter()`](#connectusbprinter)
* [`printUsbText(...)`](#printusbtext)
* [`disconnectUsbPrinter()`](#disconnectusbprinter)
* [`connectThermalPrinter()`](#connectthermalprinter)
* [`printThermalText(...)`](#printthermaltext)
* [`disconnectThermalPrinter()`](#disconnectthermalprinter)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### scanBluetoothDevices()

```typescript
scanBluetoothDevices() => Promise<{ devices: { name: string; address: string; }[]; count: number; }>
```

**Returns:** <code>Promise&lt;{ devices: { name: string; address: string; }[]; count: number; }&gt;</code>

--------------------


### connectBluetooth(...)

```typescript
connectBluetooth(options: { address: string; }) => Promise<{ connected: boolean; deviceName: string; deviceAddress: string; }>
```

| Param         | Type                              |
| ------------- | --------------------------------- |
| **`options`** | <code>{ address: string; }</code> |

**Returns:** <code>Promise&lt;{ connected: boolean; deviceName: string; deviceAddress: string; }&gt;</code>

--------------------


### printBluetoothText(...)

```typescript
printBluetoothText(options: { text: string; fontSize?: number; align?: 'left' | 'center' | 'right'; isBold?: boolean; }) => Promise<{ printed: boolean; }>
```

| Param         | Type                                                                                                       |
| ------------- | ---------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ text: string; fontSize?: number; align?: 'left' \| 'center' \| 'right'; isBold?: boolean; }</code> |

**Returns:** <code>Promise&lt;{ printed: boolean; }&gt;</code>

--------------------


### cutBluetoothPaper()

```typescript
cutBluetoothPaper() => Promise<{ cut: boolean; }>
```

**Returns:** <code>Promise&lt;{ cut: boolean; }&gt;</code>

--------------------


### disconnectBluetooth()

```typescript
disconnectBluetooth() => Promise<void>
```

--------------------


### connectUsbPrinter()

```typescript
connectUsbPrinter() => Promise<void>
```

--------------------


### printUsbText(...)

```typescript
printUsbText(options: { text: string; }) => Promise<void>
```

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ text: string; }</code> |

--------------------


### disconnectUsbPrinter()

```typescript
disconnectUsbPrinter() => Promise<void>
```

--------------------


### connectThermalPrinter()

```typescript
connectThermalPrinter() => Promise<void>
```

--------------------


### printThermalText(...)

```typescript
printThermalText(options: { text: string; }) => Promise<void>
```

| Param         | Type                           |
| ------------- | ------------------------------ |
| **`options`** | <code>{ text: string; }</code> |

--------------------


### disconnectThermalPrinter()

```typescript
disconnectThermalPrinter() => Promise<void>
```

--------------------

</docgen-api>



<!-- Required for Bluetooth scanning and connection -->
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
<uses-permission android:name="android.permission.BLUETOOTH_SCAN"/>
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT"/>
<uses-permission android:name="android.permission.BLUETOOTH_PRIVILEGED"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>



<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>ZPrinter Demo</ion-title>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <!-- Bluetooth Section -->
      <h2>Bluetooth Printer</h2>
      <ion-button @click="scanBluetooth">Scan Bluetooth Devices</ion-button>
      <ion-list>
        <ion-item
          v-for="device in bluetoothDevices"
          :key="device.address"
          @click="connectBluetooth(device.address)"
        >
          {{ device.name }} - {{ device.address }}
        </ion-item>
      </ion-list>
      <ion-input v-model="bluetoothText" placeholder="Text to print"></ion-input>
      <ion-button @click="printBluetooth">Print</ion-button>
      <ion-button @click="cutBluetooth">Cut</ion-button>
      <ion-button color="danger" @click="disconnectBluetooth">Disconnect</ion-button>

      <ion-divider class="ion-margin"></ion-divider>

      <!-- USB Section -->
      <h2>USB Printer</h2>
      <ion-button @click="connectUSB">Connect USB</ion-button>
      <ion-input v-model="usbText" placeholder="Text to print"></ion-input>
      <ion-button @click="printUSB">Print</ion-button>
      <ion-button color="danger" @click="disconnectUSB">Disconnect USB</ion-button>

      <ion-divider class="ion-margin"></ion-divider>

      <!-- Thermal Section -->
      <h2>Thermal Printer</h2>
      <ion-button @click="connectThermal">Connect Thermal</ion-button>
      <ion-input v-model="thermalText" placeholder="Text to print"></ion-input>
      <ion-button @click="printThermal">Print</ion-button>
      <ion-button color="danger" @click="disconnectThermal">Disconnect Thermal</ion-button>
    </ion-content>
  </ion-page>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { ZPrinter } from 'zprinter';

// --------------------
// State
// --------------------
const bluetoothDevices = ref<{ name: string; address: string }[]>([]);
const bluetoothText = ref('');
const usbText = ref('');
const thermalText = ref('');

// --------------------
// Bluetooth Functions
// --------------------
const scanBluetooth = async () => {
  try {
    const res = await ZPrinter.scanDevices();
    bluetoothDevices.value = res.devices;
  } catch (err: any) {
    alert('Scan failed: ' + err);
  }
};

const connectBluetooth = async (address: string) => {
  try {
    await ZPrinter.connect({ address });
    alert('Bluetooth connected!');
  } catch (err: any) {
    alert('Connect failed: ' + err);
  }
};

const printBluetooth = async () => {
  if (!bluetoothText.value) return;
  try {
    await ZPrinter.printText({ text: bluetoothText.value });
    alert('Bluetooth Printed!');
  } catch (err: any) {
    alert('Print failed: ' + err);
  }
};

const cutBluetooth = async () => {
  try {
    await ZPrinter.cut();
    alert('Paper cut!');
  } catch (err: any) {
    alert('Cut failed: ' + err);
  }
};

const disconnectBluetooth = async () => {
  try {
    await ZPrinter.disconnect();
    alert('Bluetooth disconnected!');
  } catch (err: any) {
    alert('Disconnect failed: ' + err);
  }
};

// --------------------
// USB Functions
// --------------------
const connectUSB = async () => {
  try {
    await ZPrinter.connectUsb();
    alert('USB connected!');
  } catch (err: any) {
    alert('USB connect failed: ' + err);
  }
};

const printUSB = async () => {
  if (!usbText.value) return;
  try {
    await ZPrinter.printUsb({ text: usbText.value });
    alert('USB Printed!');
  } catch (err: any) {
    alert('USB print failed: ' + err);
  }
};

const disconnectUSB = async () => {
  try {
    await ZPrinter.disconnectUsb();
    alert('USB disconnected!');
  } catch (err: any) {
    alert('USB disconnect failed: ' + err);
  }
};

// --------------------
// Thermal Functions
// --------------------
const connectThermal = async () => {
  try {
    await ZPrinter.connectThermal();
    alert('Thermal connected!');
  } catch (err: any) {
    alert('Thermal connect failed: ' + err);
  }
};

const printThermal = async () => {
  if (!thermalText.value) return;
  try {
    await ZPrinter.printThermal({ text: thermalText.value });
    alert('Thermal Printed!');
  } catch (err: any) {
    alert('Thermal print failed: ' + err);
  }
};

const disconnectThermal = async () => {
  try {
    await ZPrinter.disconnectThermal();
    alert('Thermal disconnected!');
  } catch (err: any) {
    alert('Thermal disconnect failed: ' + err);
  }
};
</script>

<style scoped>
ion-input {
  margin: 10px 0;
}
ion-button {
  margin: 5px 0;
}
</style>
