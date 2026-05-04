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
        CAPPluginMethod(name: "printThermalImage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printThermalQRCode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "kickThermalDrawer", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disconnectThermalPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printBluetoothImage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printBluetoothQRCode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "kickBluetoothDrawer", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printUsbImage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printUsbQRCode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "kickUsbDrawer", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "connectNetworkPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printNetworkText", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printNetworkImage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printNetworkQRCode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "kickNetworkDrawer", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disconnectNetworkPrinter", returnType: CAPPluginReturnPromise)
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

    @objc public func printBluetoothImage(_ call: CAPPluginCall) {
        guard let peripheral = connectedPeripheral, let characteristic = writeCharacteristic else {
            call.reject("No Bluetooth printer connected")
            return
        }

        guard let base64 = call.getString("base64"), !base64.isEmpty else {
            call.reject("Base64 image string is required")
            return
        }

        let width = call.getInt("width") ?? 0
        let height = call.getInt("height") ?? 0
        let align = call.getString("align") ?? "left"

        pendingPrintCall = call
        if let data = formatPrintImage(base64: base64, width: width, height: height, align: align) {
            write(data: data, to: peripheral, characteristic: characteristic, promiseCall: call, resolveKey: "printed")
        } else {
            call.reject("Failed to format image")
        }
    }

    @objc public func printBluetoothQRCode(_ call: CAPPluginCall) {
        guard let peripheral = connectedPeripheral, let characteristic = writeCharacteristic else {
            call.reject("No Bluetooth printer connected")
            return
        }

        guard let dataStr = call.getString("data"), !dataStr.isEmpty else {
            call.reject("QR data is required")
            return
        }

        let size = call.getInt("size") ?? 8
        let align = call.getString("align") ?? "center"

        pendingPrintCall = call
        let data = formatQRCode(data: dataStr, size: size, align: align)
        write(data: data, to: peripheral, characteristic: characteristic, promiseCall: call, resolveKey: "printed")
    }

    @objc public func kickBluetoothDrawer(_ call: CAPPluginCall) {
        guard let peripheral = connectedPeripheral, let characteristic = writeCharacteristic else {
            call.reject("No Bluetooth printer connected")
            return
        }

        let kickData = Data([0x1B, 0x70, 0x00, 0x19, 0xFA])
        write(data: kickData, to: peripheral, characteristic: characteristic, promiseCall: call, resolveKey: "kicked")
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

    @objc public func printUsbImage(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth printers on iOS.")
    }

    @objc public func printUsbQRCode(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth printers on iOS.")
    }

    @objc public func kickUsbDrawer(_ call: CAPPluginCall) {
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

    @objc public func printThermalImage(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth thermal printers on iOS.")
    }

    @objc public func printThermalQRCode(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth thermal printers on iOS.")
    }

    @objc public func kickThermalDrawer(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth thermal printers on iOS.")
    }

    @objc public func disconnectThermalPrinter(_ call: CAPPluginCall) {
        call.resolve()
    }

    @objc public func connectNetworkPrinter(_ call: CAPPluginCall) {
        call.reject("Network printing is not yet implemented on iOS. Use Bluetooth printers on iOS.")
    }

    @objc public func printNetworkText(_ call: CAPPluginCall) {
        call.reject("Network printing is not yet implemented on iOS. Use Bluetooth printers on iOS.")
    }

    @objc public func printNetworkImage(_ call: CAPPluginCall) {
        call.reject("Network printing is not yet implemented on iOS. Use Bluetooth printers on iOS.")
    }

    @objc public func printNetworkQRCode(_ call: CAPPluginCall) {
        call.reject("Network printing is not yet implemented on iOS. Use Bluetooth printers on iOS.")
    }

    @objc public func kickNetworkDrawer(_ call: CAPPluginCall) {
        call.reject("Network printing is not yet implemented on iOS. Use Bluetooth printers on iOS.")
    }

    @objc public func disconnectNetworkPrinter(_ call: CAPPluginCall) {
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

    private func formatQRCode(data: String, size: Int, align: String) -> Data {
        var commands = Data()

        // Alignment
        switch align {
        case "center":
            commands.append(contentsOf: [0x1B, 0x61, 0x01])
        case "right":
            commands.append(contentsOf: [0x1B, 0x61, 0x02])
        default:
            commands.append(contentsOf: [0x1B, 0x61, 0x00])
        }

        let storeLen = data.count + 3
        let storePL = UInt8(storeLen % 256)
        let storePH = UInt8(storeLen / 256)

        // Model
        commands.append(contentsOf: [0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00])
        // Size
        commands.append(contentsOf: [0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, UInt8(min(max(size, 1), 16))])
        // Error Correction (Level L)
        commands.append(contentsOf: [0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, 0x30])
        // Store data
        commands.append(contentsOf: [0x1D, 0x28, 0x6B, storePL, storePH, 0x31, 0x50, 0x30])
        if let dataBytes = data.data(using: .utf8) {
            commands.append(dataBytes)
        }
        // Print
        commands.append(contentsOf: [0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30])

        commands.append(0x0A)
        commands.append(contentsOf: [0x1B, 0x61, 0x00])

        return commands
    }

    private func formatPrintImage(base64: String, width: Int, height: Int, align: String) -> Data? {
        guard let data = Data(base64Encoded: base64), let image = UIImage(data: data) else {
            return nil
        }

        var targetImage = image
        if width > 0 && height > 0 {
            let size = CGSize(width: width, height: height)
            UIGraphicsBeginImageContext(size)
            image.draw(in: CGRect(origin: .zero, size: size))
            targetImage = UIGraphicsGetImageFromCurrentImageContext() ?? image
            UIGraphicsEndImageContext()
        }

        let bw = Int(targetImage.size.width)
        let bh = Int(targetImage.size.height)
        let xL = UInt8((bw / 8) % 256)
        let xH = UInt8((bw / 8) / 256)
        let yL = UInt8(bh % 256)
        let yH = UInt8(bh / 256)

        var commands = Data()
        
        // Alignment
        switch align {
        case "center":
            commands.append(contentsOf: [0x1B, 0x61, 0x01])
        case "right":
            commands.append(contentsOf: [0x1B, 0x61, 0x02])
        default:
            commands.append(contentsOf: [0x1B, 0x61, 0x00])
        }

        commands.append(contentsOf: [0x1D, 0x76, 0x30, 0x00, xL, xH, yL, yH])

        // Get pixel data
        guard let cgImage = targetImage.cgImage else { return nil }
        let colorSpace = CGColorSpaceCreateDeviceGray()
        let bitmapInfo = CGImageAlphaInfo.none.rawValue
        guard let context = CGContext(data: nil, width: bw, height: bh, bitsPerComponent: 8, bytesPerRow: bw, space: colorSpace, bitmapInfo: bitmapInfo) else {
            return nil
        }

        context.draw(cgImage, in: CGRect(x: 0, y: 0, width: bw, height: bh))
        guard let pixelData = context.data else { return nil }
        let pixels = pixelData.bindMemory(to: UInt8.self, capacity: bw * bh)

        for y in 0..<bh {
            for x in stride(from: 0, to: bw, by: 8) {
                var b: UInt8 = 0
                for bit in 0..<8 {
                    if x + bit < bw {
                        let gray = pixels[y * bw + x + bit]
                        if gray < 128 {
                            b |= (0x80 >> bit)
                        }
                    }
                }
                commands.append(b)
            }
        }

        commands.append(0x0A)
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
