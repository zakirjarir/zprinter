import Capacitor
import CoreBluetooth

@objc(ZPrinterPlugin)
public class ZPrinterPlugin: CAPPlugin, CBCentralManagerDelegate, CBPeripheralDelegate {

    // MARK: - Properties
    var centralManager: CBCentralManager?
    var discoveredPeripherals: [CBPeripheral] = []
    var connectedPeripheral: CBPeripheral?
    var writeCharacteristic: CBCharacteristic?
    var scanCall: CAPPluginCall?
    var connectCall: CAPPluginCall?
    var printCall: CAPPluginCall?
    var cutCall: CAPPluginCall?

    // MARK: - Scan Methods
    @objc func scanDevices(_ call: CAPPluginCall) {
        scanCall = call

        // Initialize central manager if needed
        if centralManager == nil {
            centralManager = CBCentralManager(delegate: self, queue: nil)
        } else {
            // Already initialized, check state
            if centralManager?.state == .poweredOn {
                startScanning()
            }
        }

        // Return immediately that scanning started
        call.resolve([
            "status": "scanning",
            "message": "Bluetooth scanning started"
        ])
    }

    private func startScanning() {
        discoveredPeripherals.removeAll()
        centralManager?.scanForPeripherals(withServices: nil, options: [
            CBCentralManagerScanOptionAllowDuplicatesKey: false
        ])

        // Auto stop after 10 seconds
        DispatchQueue.main.asyncAfter(deadline: .now() + 10) { [weak self] in
            self?.stopScanning()
        }
    }

    private func stopScanning() {
        centralManager?.stopScan()
        notifyListeners("scanComplete", data: [
            "devices": discoveredPeripherals.map { [
                "name": $0.name ?? "Unknown",
                "identifier": $0.identifier.uuidString
            ]}
        ])
    }

    // MARK: - Connect Methods
    @objc func connect(_ call: CAPPluginCall) {
        let identifier = call.getString("identifier")
        let name = call.getString("name")

        guard identifier != nil || name != nil else {
            call.reject("Either identifier or name is required")
            return
        }

        connectCall = call

        // Find peripheral by identifier or name
        var peripheralToConnect: CBPeripheral?

        if let identifier = identifier {
            let uuid = UUID(uuidString: identifier)
            peripheralToConnect = discoveredPeripherals.first { $0.identifier == uuid }
        } else if let name = name {
            peripheralToConnect = discoveredPeripherals.first { $0.name == name }
        }

        if let peripheral = peripheralToConnect {
            centralManager?.connect(peripheral, options: nil)
        } else {
            call.reject("Peripheral not found")
            connectCall = nil
        }
    }

    // MARK: - Print Methods
    @objc func printText(_ call: CAPPluginCall) {
        guard let text = call.getString("text"), !text.isEmpty else {
            call.reject("Text is empty")
            return
        }

        let fontSize = call.getInt("fontSize") ?? 24
        let align = call.getString("align") ?? "left"
        let isBold = call.getBool("isBold") ?? false

        guard let peripheral = connectedPeripheral,
        let characteristic = writeCharacteristic else {
            call.reject("No printer connected")
            return
        }

        printCall = call

        // Format text with ESC/POS commands
        let formattedData = formatPrintText(text: text, fontSize: fontSize, align: align, isBold: isBold)

        peripheral.writeValue(formattedData, for: characteristic, type: .withResponse)
    }

    private func formatPrintText(text: String, fontSize: Int, align: String, isBold: Bool) -> Data {
        var commands = Data()

        // Initialize printer
        commands.append(contentsOf: [0x1B, 0x40]) // ESC @ - Initialize

        // Bold
        if isBold {
            commands.append(contentsOf: [0x1B, 0x45, 0x01]) // ESC E 1 - Bold ON
        }

        // Alignment
        switch align {
        case "center":
            commands.append(contentsOf: [0x1B, 0x61, 0x01]) // ESC a 1 - Center
        case "right":
            commands.append(contentsOf: [0x1B, 0x61, 0x02]) // ESC a 2 - Right
        default:
            commands.append(contentsOf: [0x1B, 0x61, 0x00]) // ESC a 0 - Left
        }

        // Font size (GS ! n)
        var sizeCode: UInt8 = 0
        if fontSize >= 48 {
            sizeCode = 0x33 // 3x3
        } else if fontSize >= 32 {
            sizeCode = 0x22 // 2x2
        } else if fontSize >= 24 {
            sizeCode = 0x11 // 1.5x1.5
        } else {
            sizeCode = 0x00 // Normal
        }
        commands.append(contentsOf: [0x1D, 0x21, sizeCode])

        // Text
        if let textData = text.data(using: .utf8) {
            commands.append(textData)
        }
        commands.append(0x0A) // LF - Line Feed

        // Bold OFF
        if isBold {
            commands.append(contentsOf: [0x1B, 0x45, 0x00]) // ESC E 0 - Bold OFF
        }

        return commands
    }

    @objc func printRaw(_ call: CAPPluginCall) {
        guard let base64Data = call.getString("data"), !base64Data.isEmpty else {
            call.reject("Raw data is empty")
            return
        }

        guard let peripheral = connectedPeripheral,
        let characteristic = writeCharacteristic else {
            call.reject("No printer connected")
            return
        }

        printCall = call

        if let data = Data(base64Encoded: base64Data) {
            peripheral.writeValue(data, for: characteristic, type: .withResponse)
        } else {
            call.reject("Failed to decode base64 data")
        }
    }

    // MARK: - Cut Methods
    @objc func cut(_ call: CAPPluginCall) {
        guard let peripheral = connectedPeripheral,
        let characteristic = writeCharacteristic else {
            call.reject("No printer connected")
            return
        }

        cutCall = call

        // ESC/POS cut command
        let cutData = Data([0x1D, 0x56, 0x00]) // GS V 0 - Full cut
        peripheral.writeValue(cutData, for: characteristic, type: .withResponse)
    }

    @objc func partialCut(_ call: CAPPluginCall) {
        guard let peripheral = connectedPeripheral,
        let characteristic = writeCharacteristic else {
            call.reject("No printer connected")
            return
        }

        cutCall = call

        // ESC/POS partial cut command
        let cutData = Data([0x1D, 0x56, 0x01]) // GS V 1 - Partial cut
        peripheral.writeValue(cutData, for: characteristic, type: .withResponse)
    }

    // MARK: - Disconnect Methods
    @objc func disconnect(_ call: CAPPluginCall) {
        if let peripheral = connectedPeripheral {
            centralManager?.cancelPeripheralConnection(peripheral)
        }

        connectedPeripheral = nil
        writeCharacteristic = nil

        call.resolve([
            "disconnected": true
        ])
    }

    @objc func getStatus(_ call: CAPPluginCall) {
        call.resolve([
            "isConnected": connectedPeripheral != nil,
            "deviceName": connectedPeripheral?.name ?? "",
            "deviceIdentifier": connectedPeripheral?.identifier.uuidString ?? "",
            "bluetoothState": centralManager?.state.rawValue ?? 0
        ])
    }

    // MARK: - CBCentralManagerDelegate Methods

    public func centralManagerDidUpdateState(_ central: CBCentralManager) {
        var state = "unknown"
        var isEnabled = false

        switch central.state {
        case .poweredOn:
            state = "poweredOn"
            isEnabled = true
            if scanCall != nil {
                startScanning()
            }
        case .poweredOff:
            state = "poweredOff"
            notifyListeners("error", data: ["message": "Bluetooth is powered off"])
        case .unauthorized:
            state = "unauthorized"
            notifyListeners("error", data: ["message": "Bluetooth permission denied"])
        case .unsupported:
            state = "unsupported"
            notifyListeners("error", data: ["message": "Bluetooth is not supported"])
        case .resetting:
            state = "resetting"
        case .unknown:
            state = "unknown"
        @unknown default:
            state = "unknown"
        }

        notifyListeners("bluetoothStateChanged", data: [
            "state": state,
            "isEnabled": isEnabled
        ])
    }

    public func centralManager(_ central: CBCentralManager,
    didDiscover peripheral: CBPeripheral,
    advertisementData: [String : Any],
    rssi RSSI: NSNumber) {

        // Filter out devices with no name
        guard let deviceName = peripheral.name, !deviceName.isEmpty else { return }

        // Check if already discovered
        if !discoveredPeripherals.contains(where: { $0.identifier == peripheral.identifier }) {
            discoveredPeripherals.append(peripheral)

            // Get advertised data
            let manufacturerData = advertisementData[CBAdvertisementDataManufacturerDataKey] as? Data

            notifyListeners("deviceDiscovered", data: [
                "name": deviceName,
                "identifier": peripheral.identifier.uuidString,
                "rssi": RSSI.intValue,
                "manufacturerData": manufacturerData?.base64EncodedString() ?? ""
            ])
        }
    }

    public func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        peripheral.delegate = self
        peripheral.discoverServices(nil)

        connectedPeripheral = peripheral

        notifyListeners("connected", data: [
            "name": peripheral.name ?? "",
            "identifier": peripheral.identifier.uuidString
        ])

        connectCall?.resolve([
            "connected": true,
            "name": peripheral.name ?? "",
            "identifier": peripheral.identifier.uuidString
        ])
        connectCall = nil
    }

    public func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        let errorMessage = error?.localizedDescription ?? "Failed to connect"

        notifyListeners("connectionFailed", data: [
            "error": errorMessage
        ])

        connectCall?.reject(errorMessage)
        connectCall = nil
    }

    public func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        if connectedPeripheral?.identifier == peripheral.identifier {
            connectedPeripheral = nil
            writeCharacteristic = nil
        }

        notifyListeners("disconnected", data: [
            "name": peripheral.name ?? "",
            "identifier": peripheral.identifier.uuidString
        ])
    }

    // MARK: - CBPeripheralDelegate Methods

    public func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        if let error = error {
            notifyListeners("error", data: ["message": error.localizedDescription])
            return
        }

        guard let services = peripheral.services else { return }

        for service in services {
            peripheral.discoverCharacteristics(nil, for: service)
        }
    }

    public func peripheral(_ peripheral: CBPeripheral,
    didDiscoverCharacteristicsFor service: CBService,
    error: Error?) {
        if let error = error {
            notifyListeners("error", data: ["message": error.localizedDescription])
            return
        }

        guard let characteristics = service.characteristics else { return }

        for characteristic in characteristics {
            // Look for write characteristics
            if characteristic.properties.contains(.write) ||
            characteristic.properties.contains(.writeWithoutResponse) {
                writeCharacteristic = characteristic

                notifyListeners("printerReady", data: [
                    "message": "Printer ready for printing"
                ])
                break
            }
        }
    }

    public func peripheral(_ peripheral: CBPeripheral,
    didWriteValueFor characteristic: CBCharacteristic,
    error: Error?) {
        if let error = error {
            printCall?.reject("Write failed: \(error.localizedDescription)")
            cutCall?.reject("Cut failed: \(error.localizedDescription)")
        } else {
            printCall?.resolve(["printed": true])
            cutCall?.resolve(["cut": true])
        }

        printCall = nil
        cutCall = nil
    }

    // MARK: - Helper Methods

    @objc func stopScan(_ call: CAPPluginCall) {
        stopScanning()
        call.resolve(["stopped": true])
    }
}