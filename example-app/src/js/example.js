import { ZPrinter } from 'zprinter';

const logElement = document.getElementById('log');
const bluetoothDevicesElement = document.getElementById('bluetoothDevices');
const usbDevicesElement = document.getElementById('usbDevices');

function log(message, payload) {
  const timestamp = new Date().toLocaleTimeString();
  const body = payload ? `${message}\n${JSON.stringify(payload, null, 2)}` : message;
  logElement.textContent = `[${timestamp}] ${body}\n\n${logElement.textContent}`;
}

function getPrintOptions() {
  return {
    text: document.getElementById('printText').value,
    fontSize: Number(document.getElementById('fontSize').value || 24),
    align: document.getElementById('align').value,
    isBold: document.getElementById('isBold').checked,
    feedLines: Number(document.getElementById('feedLines').value || 2),
  };
}

function renderDeviceOptions(selectElement, devices, keyBuilder) {
  selectElement.innerHTML = '';

  if (!devices.length) {
    const option = document.createElement('option');
    option.value = '';
    option.textContent = 'No devices found';
    selectElement.appendChild(option);
    return;
  }

  devices.forEach((device) => {
    const option = document.createElement('option');
    option.value = JSON.stringify(keyBuilder(device));
    option.textContent =
      device.name ||
      device.deviceName ||
      device.productName ||
      `${device.vendorId ?? ''}:${device.productId ?? ''}`;
    selectElement.appendChild(option);
  });
}

async function handleAction(name, action) {
  try {
    const result = await action();
    log(name, result);
  } catch (error) {
    log(`${name} failed`, { message: error?.message ?? String(error) });
  }
}

document.getElementById('scanBluetooth').addEventListener('click', async () => {
  await handleAction('scanBluetoothDevices', async () => {
    const result = await ZPrinter.scanBluetoothDevices();
    renderDeviceOptions(bluetoothDevicesElement, result.devices, (device) => ({
      address: device.address,
    }));
    return result;
  });
});

document.getElementById('printBluetooth').addEventListener('click', async () => {
  await handleAction('printBluetoothText', () => ZPrinter.printBluetoothText(getPrintOptions()));
});

document.getElementById('cutBluetooth').addEventListener('click', async () => {
  await handleAction('cutBluetoothPaper', () => ZPrinter.cutBluetoothPaper());
});

document.getElementById('disconnectBluetooth').addEventListener('click', async () => {
  await handleAction('disconnectBluetooth', () => ZPrinter.disconnectBluetooth());
});

bluetoothDevicesElement.addEventListener('change', async () => {
  const value = bluetoothDevicesElement.value;
  if (!value) {
    return;
  }

  const { address } = JSON.parse(value);
  await handleAction('connectBluetooth', () => ZPrinter.connectBluetooth({ address }));
});

document.getElementById('listUsb').addEventListener('click', async () => {
  await handleAction('listUsbPrinters', async () => {
    const result = await ZPrinter.listUsbPrinters();
    renderDeviceOptions(usbDevicesElement, result.devices, (device) => ({
      vendorId: device.vendorId,
      productId: device.productId,
      deviceName: device.deviceName,
    }));
    return result;
  });
});

document.getElementById('connectUsb').addEventListener('click', async () => {
  await handleAction('connectUsbPrinter', () => {
    const selected = usbDevicesElement.value ? JSON.parse(usbDevicesElement.value) : {};
    return ZPrinter.connectUsbPrinter(selected);
  });
});

document.getElementById('connectThermal').addEventListener('click', async () => {
  await handleAction('connectThermalPrinter', () => {
    const selected = usbDevicesElement.value ? JSON.parse(usbDevicesElement.value) : {};
    return ZPrinter.connectThermalPrinter(selected);
  });
});

document.getElementById('printUsb').addEventListener('click', async () => {
  await handleAction('printUsbText', () => ZPrinter.printUsbText(getPrintOptions()));
});

document.getElementById('printThermal').addEventListener('click', async () => {
  await handleAction('printThermalText', () => ZPrinter.printThermalText(getPrintOptions()));
});

document.getElementById('disconnectUsb').addEventListener('click', async () => {
  await handleAction('disconnectUsbPrinter', () => ZPrinter.disconnectUsbPrinter());
});

document.getElementById('disconnectThermal').addEventListener('click', async () => {
  await handleAction('disconnectThermalPrinter', () => ZPrinter.disconnectThermalPrinter());
});

window.addEventListener('load', () => {
  renderDeviceOptions(bluetoothDevicesElement, [], (device) => device);
  renderDeviceOptions(usbDevicesElement, [], (device) => device);
  log('Example app ready');
});
