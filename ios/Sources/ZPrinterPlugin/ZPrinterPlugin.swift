import Capacitor
import CoreBluetooth
import Foundation

@objc(ZPrinterPlugin)
public class ZPrinterPlugin: CAPPlugin, CAPBridgedPlugin, CBCentralManagerDelegate, CBPeripheralDelegate {
    public let identifier = "ZPrinterPlugin"
    public let jsName = "ZPrinter"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "scanBluetoothDevices", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "connectBluetooth", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printBluetoothText", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "cutBluetoothPaper", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disconnectBluetooth", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "listUsbPrinters", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "connectUsbPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printUsbText", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disconnectUsbPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "connectThermalPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printThermalText", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disconnectThermalPrinter", returnType: CAPPluginReturnPromise)
    ]

    private var centralManager: CBCentralManager?
    private var discoveredPeripherals: [CBPeripheral] = []
    private var connectedPeripheral: CBPeripheral?
    private var writeCharacteristic: CBCharacteristic?
    private var pendingScanCall: CAPPluginCall?
    private var pendingConnectCall: CAPPluginCall?
    private var pendingPrintCall: CAPPluginCall?
    private var pendingCutCall: CAPPluginCall?

    @objc public func scanBluetoothDevices(_ call: CAPPluginCall) {
        pendingScanCall = call

        if centralManager == nil {
            centralManager = CBCentralManager(delegate: self, queue: nil)
            return
        }

        guard let centralManager else {
            call.reject("Bluetooth manager is unavailable")
            pendingScanCall = nil
            return
        }

        guard centralManager.state == .poweredOn else {
            call.reject(bluetoothStateMessage(for: centralManager.state))
            pendingScanCall = nil
            return
        }

        startScanning()
    }

    @objc public func connectBluetooth(_ call: CAPPluginCall) {
        guard let address = call.getString("address"), !address.isEmpty else {
            call.reject("Bluetooth address is required")
            return
        }

        guard let centralManager, centralManager.state == .poweredOn else {
            call.reject("Bluetooth is not ready")
            return
        }

        guard let uuid = UUID(uuidString: address) else {
            call.reject("On iOS, address must be the scanned device identifier")
            return
        }

        guard let peripheral = discoveredPeripherals.first(where: { $0.identifier == uuid }) else {
            call.reject("Peripheral not found. Run scanBluetoothDevices first.")
            return
        }

        pendingConnectCall = call
        connectedPeripheral = nil
        writeCharacteristic = nil
        centralManager.connect(peripheral, options: nil)
    }

    @objc public func printBluetoothText(_ call: CAPPluginCall) {
        guard let peripheral = connectedPeripheral, let characteristic = writeCharacteristic else {
            call.reject("No Bluetooth printer connected")
            return
        }

        guard let text = call.getString("text"), !text.isEmpty else {
            call.reject("Text is required")
            return
        }

        let fontSize = call.getInt("fontSize") ?? 24
        let align = call.getString("align") ?? "left"
        let isBold = call.getBool("isBold") ?? false
        let feedLines = max(call.getInt("feedLines") ?? 2, 0)

        pendingPrintCall = call
        let data = formatPrintText(text: text, fontSize: fontSize, align: align, isBold: isBold, feedLines: feedLines)
        write(data: data, to: peripheral, characteristic: characteristic, promiseCall: call, resolveKey: "printed")
    }

    @objc public func cutBluetoothPaper(_ call: CAPPluginCall) {
        guard let peripheral = connectedPeripheral, let characteristic = writeCharacteristic else {
            call.reject("No Bluetooth printer connected")
            return
        }

        pendingCutCall = call
        let cutData = Data([0x1D, 0x56, 0x00])
        write(data: cutData, to: peripheral, characteristic: characteristic, promiseCall: call, resolveKey: "cut")
    }

    @objc public func disconnectBluetooth(_ call: CAPPluginCall) {
        if let connectedPeripheral {
            centralManager?.cancelPeripheralConnection(connectedPeripheral)
        }

        connectedPeripheral = nil
        writeCharacteristic = nil
        call.resolve()
    }

    @objc public func listUsbPrinters(_ call: CAPPluginCall) {
        call.resolve([
            "devices": [],
            "count": 0
        ])
    }

    @objc public func connectUsbPrinter(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth printers on iOS.")
    }

    @objc public func printUsbText(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth printers on iOS.")
    }

    @objc public func disconnectUsbPrinter(_ call: CAPPluginCall) {
        call.resolve()
    }

    @objc public func connectThermalPrinter(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth thermal printers on iOS.")
    }

    @objc public func printThermalText(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth thermal printers on iOS.")
    }

    @objc public func disconnectThermalPrinter(_ call: CAPPluginCall) {
        call.resolve()
    }

    public func centralManagerDidUpdateState(_ central: CBCentralManager) {
        if let pendingScanCall, central.state == .poweredOn {
            startScanning()
            return
        }

        if let pendingScanCall, central.state != .poweredOn {
            pendingScanCall.reject(bluetoothStateMessage(for: central.state))
            self.pendingScanCall = nil
        }
    }

    public func centralManager(
        _ central: CBCentralManager,
        didDiscover peripheral: CBPeripheral,
        advertisementData: [String: Any],
        rssi RSSI: NSNumber
    ) {
        if !discoveredPeripherals.contains(where: { $0.identifier == peripheral.identifier }) {
            discoveredPeripherals.append(peripheral)

            notifyListeners("scanProgress", data: [
                "name": peripheral.name ?? "Unknown Device",
                "address": peripheral.identifier.uuidString,
                "rssi": RSSI.intValue,
                "isPaired": false
            ])
        }

        let manufacturerData = advertisementData[CBAdvertisementDataManufacturerDataKey] as? Data
        notifyListeners("deviceDiscovered", data: [
            "name": peripheral.name ?? "Unknown Device",
            "address": peripheral.identifier.uuidString,
            "rssi": RSSI.intValue,
            "manufacturerData": manufacturerData?.base64EncodedString() ?? ""
        ])
    }

    public func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        connectedPeripheral = peripheral
        peripheral.delegate = self
        peripheral.discoverServices(nil)
    }

    public func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        pendingConnectCall?.reject(error?.localizedDescription ?? "Failed to connect")
        pendingConnectCall = nil
    }

    public func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        if connectedPeripheral?.identifier == peripheral.identifier {
            connectedPeripheral = nil
            writeCharacteristic = nil
        }

        notifyListeners("disconnected", data: [
            "name": peripheral.name ?? "",
            "address": peripheral.identifier.uuidString
        ])
    }

    public func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        if let error {
            pendingConnectCall?.reject(error.localizedDescription)
            pendingConnectCall = nil
            return
        }

        peripheral.services?.forEach { service in
            peripheral.discoverCharacteristics(nil, for: service)
        }
    }

    public func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        if let error {
            pendingConnectCall?.reject(error.localizedDescription)
            pendingConnectCall = nil
            return
        }

        guard let characteristics = service.characteristics else {
            return
        }

        for characteristic in characteristics where characteristic.properties.contains(.write) || characteristic.properties.contains(.writeWithoutResponse) {
            writeCharacteristic = characteristic

            pendingConnectCall?.resolve([
                "connected": true,
                "deviceName": peripheral.name ?? "Bluetooth Printer",
                "deviceAddress": peripheral.identifier.uuidString
            ])
            pendingConnectCall = nil

            notifyListeners("printerReady", data: [
                "deviceName": peripheral.name ?? "Bluetooth Printer",
                "deviceAddress": peripheral.identifier.uuidString
            ])
            return
        }
    }

    public func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        if let error {
            pendingPrintCall?.reject("Bluetooth write failed: \(error.localizedDescription)")
            pendingCutCall?.reject("Bluetooth write failed: \(error.localizedDescription)")
        }

        pendingPrintCall = nil
        pendingCutCall = nil
    }

    private func startScanning() {
        discoveredPeripherals.removeAll()
        centralManager?.stopScan()
        centralManager?.scanForPeripherals(withServices: nil, options: [
            CBCentralManagerScanOptionAllowDuplicatesKey: false
        ])

        DispatchQueue.main.asyncAfter(deadline: .now() + 10) { [weak self] in
            self?.finishScan()
        }
    }

    private func finishScan() {
        centralManager?.stopScan()

        let devices = discoveredPeripherals.map { peripheral in
            [
                "name": peripheral.name ?? "Unknown Device",
                "address": peripheral.identifier.uuidString,
                "isPaired": false
            ]
        }

        pendingScanCall?.resolve([
            "devices": devices,
            "count": devices.count
        ])
        pendingScanCall = nil
    }

    private func bluetoothStateMessage(for state: CBManagerState) -> String {
        switch state {
        case .poweredOff:
            return "Bluetooth is powered off"
        case .unauthorized:
            return "Bluetooth permission denied"
        case .unsupported:
            return "Bluetooth is not supported on this device"
        case .resetting:
            return "Bluetooth is resetting"
        case .unknown:
            return "Bluetooth state is unknown"
        case .poweredOn:
            return "Bluetooth is ready"
        @unknown default:
            return "Bluetooth is unavailable"
        }
    }

    private func write(
        data: Data,
        to peripheral: CBPeripheral,
        characteristic: CBCharacteristic,
        promiseCall call: CAPPluginCall,
        resolveKey: String
    ) {
        let writeType: CBCharacteristicWriteType = characteristic.properties.contains(.write) ? .withResponse : .withoutResponse

        if writeType == .withResponse {
            for chunk in data.chunked(into: 180) {
                peripheral.writeValue(chunk, for: characteristic, type: .withResponse)
            }

            if resolveKey == "printed" {
                call.resolve(["printed": true])
                pendingPrintCall = nil
            } else {
                call.resolve(["cut": true])
                pendingCutCall = nil
            }
            return
        }

        for chunk in data.chunked(into: 180) {
            peripheral.writeValue(chunk, for: characteristic, type: .withoutResponse)
        }

        if resolveKey == "printed" {
            call.resolve(["printed": true])
            pendingPrintCall = nil
        } else {
            call.resolve(["cut": true])
            pendingCutCall = nil
        }
    }

    private func formatPrintText(text: String, fontSize: Int, align: String, isBold: Bool, feedLines: Int) -> Data {
        var commands = Data([0x1B, 0x40])

        switch align {
        case "center":
            commands.append(contentsOf: [0x1B, 0x61, 0x01])
        case "right":
            commands.append(contentsOf: [0x1B, 0x61, 0x02])
        default:
            commands.append(contentsOf: [0x1B, 0x61, 0x00])
        }

        commands.append(contentsOf: [0x1B, 0x45, isBold ? 0x01 : 0x00])

        let sizeCode: UInt8
        if fontSize >= 48 {
            sizeCode = 0x22
        } else if fontSize >= 32 {
            sizeCode = 0x11
        } else {
            sizeCode = 0x00
        }
        commands.append(contentsOf: [0x1D, 0x21, sizeCode])

        if let textData = text.data(using: .utf8) {
            commands.append(textData)
        }

        for _ in 0..<max(feedLines, 1) {
            commands.append(0x0A)
        }

        commands.append(contentsOf: [0x1B, 0x45, 0x00])
        commands.append(contentsOf: [0x1D, 0x21, 0x00])
        commands.append(contentsOf: [0x1B, 0x61, 0x00])

        return commands
    }
}

private extension Data {
    func chunked(into size: Int) -> [Data] {
        guard size > 0, !isEmpty else { return [self] }

        var chunks: [Data] = []
        var index = startIndex

        while index < endIndex {
            let end = self.index(index, offsetBy: size, limitedBy: endIndex) ?? endIndex
            chunks.append(self[index..<end])
            index = end
        }

        return chunks
    }
}
