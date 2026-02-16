import Capacitor
import CoreBluetooth

@objc(ZPrinterPlugin)
public class ZPrinterPlugin: CAPPlugin {

    var centralManager: CBCentralManager?
    var discoveredPeripherals: [CBPeripheral] = []
    var connectedPeripheral: CBPeripheral?
    var writeCharacteristic: CBCharacteristic?

    @objc func scanDevices(_ call: CAPPluginCall) {
        centralManager = CBCentralManager(delegate: self, queue: nil)
        discoveredPeripherals = []
        call.resolve([
            "message": "Scanning started"
        ])
    }

    @objc func connect(_ call: CAPPluginCall) {
        guard let peripheralName = call.getString("name") else {
            call.reject("Peripheral name is required")
            return
        }

        if let peripheral = discoveredPeripherals.first(where: { $0.name == peripheralName }) {
            centralManager?.connect(peripheral, options: nil)
            connectedPeripheral = peripheral
            call.resolve([
                "connected": true,
                "name": peripheral.name ?? ""
            ])
        } else {
            call.reject("Peripheral not found")
        }
    }

    @objc func printText(_ call: CAPPluginCall) {
        guard let text = call.getString("text") else {
            call.reject("Text is empty")
            return
        }

        guard let peripheral = connectedPeripheral, let characteristic = writeCharacteristic else {
            call.reject("No printer connected")
            return
        }

        if let data = text.data(using: .utf8) {
            peripheral.writeValue(data, for: characteristic, type: .withResponse)
            call.resolve(["printed": true, "text": text])
        } else {
            call.reject("Failed to encode text")
        }
    }

    @objc func cut(_ call: CAPPluginCall) {
        guard let peripheral = connectedPeripheral, let characteristic = writeCharacteristic else {
            call.reject("No printer connected")
            return
        }

        // ESC/POS cut command example (printer dependent)
        let cutData = Data([0x1D, 0x56, 0x00])
        peripheral.writeValue(cutData, for: characteristic, type: .withResponse)
        call.resolve(["cut": true])
    }
}

extension ZPrinterPlugin: CBCentralManagerDelegate, CBPeripheralDelegate {

    public func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
        case .poweredOn:
            print("Bluetooth is on, ready to scan")
            central.scanForPeripherals(withServices: nil, options: nil)
        default:
            print("Bluetooth not ready")
        }
    }

    public func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral,
    advertisementData: [String : Any], rssi RSSI: NSNumber) {
        if !discoveredPeripherals.contains(peripheral) {
            discoveredPeripherals.append(peripheral)
            notifyListeners("bleDeviceDiscovered", data: [
                "name": peripheral.name ?? "Unknown",
                "identifier": peripheral.identifier.uuidString
            ])
        }
    }

    public func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        peripheral.delegate = self
        peripheral.discoverServices(nil)
    }

    public func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        guard let services = peripheral.services else { return }
        for service in services {
            peripheral.discoverCharacteristics(nil, for: service)
        }
    }

    public func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        guard let characteristics = service.characteristics else { return }
        for characteristic in characteristics {
            // writable characteristic
            if characteristic.properties.contains(.write) || characteristic.properties.contains(.writeWithoutResponse) {
                writeCharacteristic = characteristic
                notifyListeners("printerReady", data: [
                    "message": "Printer ready"
                ])
            }
        }
    }
}
