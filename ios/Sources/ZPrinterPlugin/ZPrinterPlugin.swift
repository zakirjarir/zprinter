import Capacitor
import CoreBluetooth
import Foundation
import Network
import UIKit
import WebKit

@objc(ZPrinterPlugin)
public class ZPrinterPlugin: CAPPlugin, CAPBridgedPlugin, CBCentralManagerDelegate, CBPeripheralDelegate {
    public let identifier = "ZPrinterPlugin"
    public let jsName = "ZPrinter"
    public let pluginMethods: [CAPPluginMethod] = [
        CAPPluginMethod(name: "getSavedPrinters", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "reconnectSavedPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "clearSavedPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "htmlToImage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "htmlToPdf", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printHtmlNative", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printHtmlToThermal", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "scanBluetoothDevices", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "connectBluetooth", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "isBluetoothConnected", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printBluetoothText", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printBluetoothImage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printBluetoothQRCode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printBluetoothBarcode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "sendRawBluetooth", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "kickBluetoothDrawer", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "cutBluetoothPaper", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disconnectBluetooth", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "listUsbPrinters", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "connectUsbPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "isUsbConnected", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printUsbText", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printUsbImage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printUsbQRCode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printUsbBarcode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "sendRawUsb", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "kickUsbDrawer", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "cutUsbPaper", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disconnectUsbPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "connectThermalPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "isThermalConnected", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printThermalText", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printThermalImage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printThermalQRCode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printThermalBarcode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "sendRawThermal", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "kickThermalDrawer", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "cutThermalPaper", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disconnectThermalPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "connectNetworkPrinter", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "isNetworkConnected", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printNetworkText", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printNetworkImage", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printNetworkQRCode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "printNetworkBarcode", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "sendRawNetwork", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "kickNetworkDrawer", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "cutNetworkPaper", returnType: CAPPluginReturnPromise),
        CAPPluginMethod(name: "disconnectNetworkPrinter", returnType: CAPPluginReturnPromise)
    ]

    // MARK: - Persistence Keys
    private let keyBtAddress = "zprinter_saved_bt_address"
    private let keyBtName = "zprinter_saved_bt_name"
    private let keyNetAddress = "zprinter_saved_net_address"
    private let keyNetPort = "zprinter_saved_net_port"
    private let keyNetTimeout = "zprinter_saved_net_timeout"

    // MARK: - BLE State
    private var centralManager: CBCentralManager?
    private var discoveredPeripherals: [CBPeripheral] = []
    private var connectedPeripheral: CBPeripheral?
    private var writeCharacteristic: CBCharacteristic?
    private var pendingScanCall: CAPPluginCall?
    private var pendingConnectCall: CAPPluginCall?
    private var pendingConnectAddress: String?
    private var connectTimeoutWorkItem: DispatchWorkItem?
    private var connectingPeripheral: CBPeripheral?

    private struct PendingDeferredBlePrint {
        let data: Data
        let call: CAPPluginCall
        let resolveKey: String
    }

    private struct BleWriteJob {
        let chunks: [Data]
        var index: Int
        let peripheral: CBPeripheral
        let characteristic: CBCharacteristic
        let writeType: CBCharacteristicWriteType
        let call: CAPPluginCall
        let resolveKey: String
    }

    private var deferredBlePrints: [PendingDeferredBlePrint] = []
    private var activeBleJob: BleWriteJob?
    private var bleJobQueue: [BleWriteJob] = []

    // MARK: - Network Printer State (TCP Socket via Network.framework)
    private let networkQueue = DispatchQueue(label: "com.zprinter.network", qos: .userInitiated)
    private var networkConnection: NWConnection?
    private var networkHost: String = ""
    private var networkPort: UInt16 = 9100
    private var networkReady: Bool = false

    // Retain active HTML renderers until completion
    private var activeHtmlRenderers: [ObjectIdentifier: HtmlDocumentRenderer] = [:]

    deinit {
        networkConnection?.cancel()
        connectTimeoutWorkItem?.cancel()
    }

    // MARK: - Saved Printer & Reconnect Methods

    @objc public func getSavedPrinters(_ call: CAPPluginCall) {
        let defaults = UserDefaults.standard
        var result: [String: Any] = [:]

        if let btAddr = defaults.string(forKey: keyBtAddress), !btAddr.isEmpty {
            result["bluetooth"] = [
                "address": btAddr,
                "deviceName": defaults.string(forKey: keyBtName) ?? "Bluetooth Printer"
            ]
        }

        if let netAddr = defaults.string(forKey: keyNetAddress), !netAddr.isEmpty {
            let port = defaults.integer(forKey: keyNetPort)
            let timeout = defaults.integer(forKey: keyNetTimeout)
            result["network"] = [
                "address": netAddr,
                "port": port > 0 ? port : 9100,
                "timeout": timeout > 0 ? timeout : 5000
            ]
        }

        call.resolve(result)
    }

    @objc public func clearSavedPrinter(_ call: CAPPluginCall) {
        let type = (call.getString("type") ?? "all").lowercased()
        let defaults = UserDefaults.standard

        if type == "all" || type == "bluetooth" {
            defaults.removeObject(forKey: keyBtAddress)
            defaults.removeObject(forKey: keyBtName)
        }
        if type == "all" || type == "network" {
            defaults.removeObject(forKey: keyNetAddress)
            defaults.removeObject(forKey: keyNetPort)
            defaults.removeObject(forKey: keyNetTimeout)
        }

        call.resolve()
    }

    // MARK: - HTML to Image / PDF / Native Print / Thermal Print

    @objc public func htmlToImage(_ call: CAPPluginCall) {
        guard let html = call.getString("html"), !html.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            call.reject("HTML content is required")
            return
        }

        let width = CGFloat(min(max(call.getInt("width") ?? 576, 64), 2400))
        let format = (call.getString("format") ?? "png").lowercased()
        let quality = CGFloat(min(max(call.getInt("quality") ?? 90, 1), 100)) / 100.0
        let baseUrl = call.getString("baseUrl").flatMap { URL(string: $0) }
        let delayMs = call.getInt("delayMs") ?? 150

        renderHtmlSnapshot(html: html, width: width, baseUrl: baseUrl, delayMs: delayMs) { result in
            switch result {
            case .failure(let error):
                call.reject("htmlToImage failed: \(error.localizedDescription)")
            case .success(let image):
                let isJpeg = format == "jpeg" || format == "jpg"
                let mimeType = isJpeg ? "image/jpeg" : "image/png"
                let imageData = isJpeg ? image.jpegData(compressionQuality: quality) : image.pngData()
                guard let imageData else {
                    call.reject("htmlToImage failed: Unable to encode image")
                    return
                }
                let base64 = imageData.base64EncodedString()
                let pxWidth = Int(round(image.size.width * image.scale))
                let pxHeight = Int(round(image.size.height * image.scale))
                call.resolve([
                    "base64": base64,
                    "dataUrl": "data:\(mimeType);base64,\(base64)",
                    "width": pxWidth,
                    "height": pxHeight,
                    "mimeType": mimeType
                ])
            }
        }
    }

    @objc public func htmlToPdf(_ call: CAPPluginCall) {
        guard let html = call.getString("html"), !html.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            call.reject("HTML content is required")
            return
        }

        let pageSize = call.getString("pageSize") ?? "A4"
        let orientation = call.getString("orientation") ?? "portrait"
        let customWidthPt = CGFloat(call.getInt("customWidthPt") ?? 595)
        let customHeightPt = CGFloat(call.getInt("customHeightPt") ?? 842)
        let isReceipt58 = pageSize.lowercased() == "receipt58mm"
        let isReceipt80 = pageSize.lowercased() == "receipt80mm"
        let isContinuousReceipt = isReceipt58 || isReceipt80
        let marginPt = CGFloat(max(call.getInt("marginPt") ?? (isContinuousReceipt ? 4 : 24), 0))
        let fileName = call.getString("fileName")
        let saveToFile = call.getBool("saveToFile") ?? false
        let baseUrl = call.getString("baseUrl").flatMap { URL(string: $0) }
        let delayMs = call.getInt("delayMs") ?? 150

        var pageWidthPt: CGFloat
        var pageHeightPt: CGFloat
        if isReceipt58 {
            pageWidthPt = 164
            pageHeightPt = 842
        } else if isReceipt80 {
            pageWidthPt = 227
            pageHeightPt = 842
        } else if pageSize.lowercased() == "a5" {
            pageWidthPt = 420
            pageHeightPt = 595
        } else if pageSize.lowercased() == "letter" {
            pageWidthPt = 612
            pageHeightPt = 792
        } else if pageSize.lowercased() == "custom" && customWidthPt > 0 && customHeightPt > 0 {
            pageWidthPt = customWidthPt
            pageHeightPt = customHeightPt
        } else {
            pageWidthPt = 595
            pageHeightPt = 842
        }

        if !isContinuousReceipt && orientation.lowercased() == "landscape" {
            swap(&pageWidthPt, &pageHeightPt)
        }

        let printableWidth = max(pageWidthPt - marginPt * 2, 40)
        let renderer = HtmlDocumentRenderer()
        let key = ObjectIdentifier(renderer)

        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            self.activeHtmlRenderers[key] = renderer
            renderer.load(
                html: html,
                width: printableWidth,
                baseUrl: baseUrl,
                delayMs: delayMs,
                hostView: self.bridge?.viewController?.view
            ) { [weak self] result in
                defer {
                    renderer.cleanup()
                    self?.activeHtmlRenderers.removeValue(forKey: key)
                }

                switch result {
                case .failure(let error):
                    call.reject("htmlToPdf failed: \(error.localizedDescription)")
                case .success(let webView):
                    let finalPageHeight: CGFloat = isContinuousReceipt
                        ? max(webView.frame.height + marginPt * 2, 60)
                        : pageHeightPt

                    let paperRect = CGRect(x: 0, y: 0, width: pageWidthPt, height: finalPageHeight)
                    let printableRect = paperRect.insetBy(dx: marginPt, dy: marginPt)

                    let printRenderer = UIPrintPageRenderer()
                    printRenderer.addPrintFormatter(webView.viewPrintFormatter(), startingAtPageAt: 0)
                    printRenderer.setValue(NSValue(cgRect: paperRect), forKey: "paperRect")
                    printRenderer.setValue(NSValue(cgRect: printableRect), forKey: "printableRect")

                    let pageCount = max(printRenderer.numberOfPages, 1)
                    let pdfData = NSMutableData()
                    UIGraphicsBeginPDFContextToData(pdfData, paperRect, nil)
                    for i in 0..<pageCount {
                        UIGraphicsBeginPDFPage()
                        printRenderer.drawPage(at: i, in: UIGraphicsGetPDFContextBounds())
                    }
                    UIGraphicsEndPDFContext()

                    let rawData = pdfData as Data
                    var savedPath: String?
                    if saveToFile || (fileName != nil && !fileName!.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty) {
                        let safeName = HtmlDocumentRenderer.sanitizePdfFileName(fileName)
                        let fileUrl = FileManager.default.temporaryDirectory.appendingPathComponent(safeName)
                        do {
                            try rawData.write(to: fileUrl, options: .atomic)
                            savedPath = fileUrl.path
                        } catch {
                            call.reject("htmlToPdf failed to save file: \(error.localizedDescription)")
                            return
                        }
                    }

                    let base64 = rawData.base64EncodedString()
                    var payload: [String: Any] = [
                        "base64": base64,
                        "dataUrl": "data:application/pdf;base64,\(base64)",
                        "pageCount": pageCount,
                        "mimeType": "application/pdf"
                    ]
                    if let savedPath {
                        payload["filePath"] = savedPath
                    }
                    call.resolve(payload)
                }
            }
        }
    }

    @objc public func printHtmlNative(_ call: CAPPluginCall) {
        guard let html = call.getString("html"), !html.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            call.reject("HTML content is required")
            return
        }

        let jobName = (call.getString("jobName") ?? "").trimmingCharacters(in: .whitespacesAndNewlines)
        let effectiveJobName = jobName.isEmpty ? "ZPrinter Document" : jobName
        let orientation = (call.getString("orientation") ?? "portrait").lowercased()
        let baseUrl = call.getString("baseUrl").flatMap { URL(string: $0) }

        let renderer = HtmlDocumentRenderer()
        let key = ObjectIdentifier(renderer)

        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            self.activeHtmlRenderers[key] = renderer
            renderer.load(
                html: html,
                width: 595,
                baseUrl: baseUrl,
                delayMs: 150,
                hostView: self.bridge?.viewController?.view
            ) { [weak self] result in
                switch result {
                case .failure(let error):
                    renderer.cleanup()
                    self?.activeHtmlRenderers.removeValue(forKey: key)
                    call.reject("printHtmlNative failed: \(error.localizedDescription)")
                case .success(let webView):
                    let controller = UIPrintInteractionController.shared
                    let printInfo = UIPrintInfo(dictionary: nil)
                    printInfo.outputType = .general
                    printInfo.jobName = effectiveJobName
                    printInfo.orientation = orientation == "landscape" ? .landscape : .portrait
                    controller.printInfo = printInfo
                    controller.printFormatter = webView.viewPrintFormatter()

                    controller.present(animated: true) { [weak self] _, completed, error in
                        renderer.cleanup()
                        self?.activeHtmlRenderers.removeValue(forKey: key)
                        if let error {
                            call.reject("printHtmlNative failed: \(error.localizedDescription)")
                        } else {
                            call.resolve([
                                "printed": completed,
                                "jobName": effectiveJobName
                            ])
                        }
                    }
                }
            }
        }
    }

    @objc public func printHtmlToThermal(_ call: CAPPluginCall) {
        guard let html = call.getString("html"), !html.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else {
            call.reject("HTML content is required")
            return
        }
        guard let printerType = call.getString("printerType")?.lowercased(), !printerType.isEmpty else {
            call.reject("printerType is required ('bluetooth' or 'network' on iOS)")
            return
        }
        if printerType == "usb" || printerType == "thermal" {
            call.reject("Generic USB printers are not supported on iOS. Use 'bluetooth' or 'network'.")
            return
        }
        if printerType != "bluetooth" && printerType != "network" {
            call.reject("Unsupported printerType: \(printerType)")
            return
        }

        let width = min(max(call.getInt("width") ?? 384, 64), 2400)
        let align = call.getString("align") ?? "center"
        let threshold = call.getInt("threshold") ?? 128
        let mode = call.getString("mode") ?? "raster"
        let feedLines = max(call.getInt("feedLines") ?? 2, 0)
        let shouldCut = call.getBool("cut") ?? false
        let partialCut = call.getBool("partialCut") ?? false
        let shouldKickDrawer = call.getBool("kickDrawer") ?? false
        let drawerPin = call.getInt("drawerPin") ?? 0
        let baseUrl = call.getString("baseUrl").flatMap { URL(string: $0) }
        let delayMs = call.getInt("delayMs") ?? 150

        renderHtmlSnapshot(html: html, width: CGFloat(width), baseUrl: baseUrl, delayMs: delayMs) { [weak self] result in
            guard let self else { return }
            switch result {
            case .failure(let error):
                call.reject("printHtmlToThermal failed: \(error.localizedDescription)")
            case .success(let image):
                guard var payload = self.formatPrintUIImage(
                    image: image,
                    width: width,
                    height: 0,
                    align: align,
                    threshold: threshold,
                    mode: mode,
                    feedLines: feedLines
                ) else {
                    call.reject("printHtmlToThermal failed: Unable to convert rendered HTML to ESC/POS")
                    return
                }

                if shouldCut {
                    for _ in 0..<3 {
                        payload.append(0x0A)
                    }
                    payload.append(contentsOf: [0x1D, 0x56, partialCut ? 0x01 : 0x00])
                }
                if shouldKickDrawer {
                    payload.append(contentsOf: [0x1B, 0x70, drawerPin == 1 ? 0x01 : 0x00, 25, 250])
                }

                if printerType == "bluetooth" {
                    self.writeBluetoothWithAutoReconnect(data: payload, call: call, resolveKey: "printed")
                } else {
                    self.sendNetworkPayload(payload, call: call, resolveKey: "printed", errorPrefix: "Network HTML print failed")
                }
            }
        }
    }

    private func renderHtmlSnapshot(
        html: String,
        width: CGFloat,
        baseUrl: URL?,
        delayMs: Int,
        completion: @escaping (Result<UIImage, Error>) -> Void
    ) {
        let renderer = HtmlDocumentRenderer()
        let key = ObjectIdentifier(renderer)

        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            self.activeHtmlRenderers[key] = renderer
            renderer.load(
                html: html,
                width: width,
                baseUrl: baseUrl,
                delayMs: delayMs,
                hostView: self.bridge?.viewController?.view
            ) { [weak self] result in
                switch result {
                case .failure(let error):
                    renderer.cleanup()
                    self?.activeHtmlRenderers.removeValue(forKey: key)
                    completion(.failure(error))
                case .success(let webView):
                    let snapConfig = WKSnapshotConfiguration()
                    snapConfig.rect = CGRect(origin: .zero, size: webView.frame.size)
                    snapConfig.snapshotWidth = NSNumber(value: Double(width))
                    webView.takeSnapshot(with: snapConfig) { [weak self] image, error in
                        renderer.cleanup()
                        self?.activeHtmlRenderers.removeValue(forKey: key)
                        if let error {
                            completion(.failure(error))
                        } else if let image {
                            completion(.success(image))
                        } else {
                            completion(.failure(NSError(
                                domain: "ZPrinter",
                                code: 10,
                                userInfo: [NSLocalizedDescriptionKey: "Snapshot returned empty image"]
                            )))
                        }
                    }
                }
            }
        }
    }

    @objc public func reconnectSavedPrinter(_ call: CAPPluginCall) {
        guard let type = call.getString("type")?.lowercased(), !type.isEmpty else {
            call.reject("Printer type is required ('bluetooth' or 'network')")
            return
        }

        let defaults = UserDefaults.standard
        if type == "bluetooth" {
            guard let savedAddress = defaults.string(forKey: keyBtAddress), !savedAddress.isEmpty else {
                call.reject("No saved Bluetooth printer found")
                return
            }
            DispatchQueue.main.async { [weak self] in
                self?.initiateBluetoothConnect(address: savedAddress, call: call)
            }
        } else if type == "network" {
            guard let savedAddress = defaults.string(forKey: keyNetAddress), !savedAddress.isEmpty else {
                call.reject("No saved Network printer found")
                return
            }
            let savedPort = defaults.integer(forKey: keyNetPort)
            let savedTimeout = defaults.integer(forKey: keyNetTimeout)
            connectNetworkInternal(
                address: savedAddress,
                portInt: savedPort > 0 ? savedPort : 9100,
                timeoutMs: savedTimeout > 0 ? savedTimeout : 5000
            ) { result in
                switch result {
                case .success(let payload):
                    call.resolve(payload)
                case .failure(let error):
                    call.reject(error.localizedDescription)
                }
            }
        } else {
            call.reject("Generic USB printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
        }
    }

    // MARK: - Bluetooth Methods

    @objc public func scanBluetoothDevices(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            self.pendingScanCall = call

            if self.centralManager == nil {
                self.centralManager = CBCentralManager(delegate: self, queue: nil)
                return
            }

            guard let centralManager = self.centralManager else {
                call.reject("Bluetooth manager is unavailable")
                self.pendingScanCall = nil
                return
            }

            guard centralManager.state == .poweredOn else {
                call.reject(self.bluetoothStateMessage(for: centralManager.state))
                self.pendingScanCall = nil
                return
            }

            self.startScanning()
        }
    }

    @objc public func connectBluetooth(_ call: CAPPluginCall) {
        guard let address = call.getString("address"), !address.isEmpty else {
            call.reject("Bluetooth address is required")
            return
        }

        DispatchQueue.main.async { [weak self] in
            self?.initiateBluetoothConnect(address: address, call: call)
        }
    }

    private func initiateBluetoothConnect(address: String, call: CAPPluginCall?) {
        guard let uuid = UUID(uuidString: address) else {
            call?.reject("On iOS, address must be the scanned device identifier")
            failDeferredBlePrints(message: "Invalid saved Bluetooth device identifier")
            return
        }

        if centralManager == nil {
            pendingConnectCall = call
            pendingConnectAddress = address
            centralManager = CBCentralManager(delegate: self, queue: nil)
            return
        }

        guard let centralManager else {
            call?.reject("Bluetooth manager is unavailable")
            failDeferredBlePrints(message: "Bluetooth manager is unavailable")
            return
        }

        if centralManager.state == .unknown || centralManager.state == .resetting {
            pendingConnectCall = call
            pendingConnectAddress = address
            return
        }

        guard centralManager.state == .poweredOn else {
            let msg = bluetoothStateMessage(for: centralManager.state)
            call?.reject(msg)
            failDeferredBlePrints(message: msg)
            return
        }

        var targetPeripheral = discoveredPeripherals.first(where: { $0.identifier == uuid })
        if targetPeripheral == nil {
            targetPeripheral = centralManager.retrievePeripherals(withIdentifiers: [uuid]).first
        }

        guard let peripheral = targetPeripheral else {
            let msg = "Peripheral not found. Run scanBluetoothDevices first."
            call?.reject(msg)
            failDeferredBlePrints(message: msg)
            return
        }

        cancelConnectTimeout()
        pendingConnectCall = call
        pendingConnectAddress = address
        connectingPeripheral = peripheral
        connectedPeripheral = nil
        writeCharacteristic = nil

        let timeout = DispatchWorkItem { [weak self] in
            guard let self, self.pendingConnectAddress != nil else { return }
            if let pending = self.connectingPeripheral {
                self.centralManager?.cancelPeripheralConnection(pending)
            }
            self.connectingPeripheral = nil
            self.pendingConnectAddress = nil
            self.pendingConnectCall?.reject("Bluetooth connect timed out")
            self.pendingConnectCall = nil
            self.failDeferredBlePrints(message: "Bluetooth connect timed out")
        }
        connectTimeoutWorkItem = timeout
        DispatchQueue.main.asyncAfter(deadline: .now() + 10, execute: timeout)

        centralManager.connect(peripheral, options: nil)
    }

    private func cancelConnectTimeout() {
        connectTimeoutWorkItem?.cancel()
        connectTimeoutWorkItem = nil
    }

    @objc public func isBluetoothConnected(_ call: CAPPluginCall) {
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            let isConnected = self.connectedPeripheral?.state == .connected && self.writeCharacteristic != nil
            var result: [String: Any] = ["connected": isConnected]
            if isConnected, let peripheral = self.connectedPeripheral {
                result["deviceName"] = peripheral.name ?? "Bluetooth Printer"
                result["deviceAddress"] = peripheral.identifier.uuidString
            }
            call.resolve(result)
        }
    }

    @objc public func printBluetoothText(_ call: CAPPluginCall) {
        guard let text = call.getString("text"), !text.isEmpty else {
            call.reject("Text is required")
            return
        }

        let data = buildTextPayload(from: call, text: text)
        writeBluetoothWithAutoReconnect(data: data, call: call, resolveKey: "printed")
    }

    @objc public func printBluetoothImage(_ call: CAPPluginCall) {
        guard let base64 = call.getString("base64"), !base64.isEmpty else {
            call.reject("Base64 image string is required")
            return
        }

        guard let data = buildImagePayload(from: call, base64: base64) else {
            call.reject("Failed to decode or format image")
            return
        }

        writeBluetoothWithAutoReconnect(data: data, call: call, resolveKey: "printed")
    }

    @objc public func printBluetoothQRCode(_ call: CAPPluginCall) {
        guard let dataStr = call.getString("data"), !dataStr.isEmpty else {
            call.reject("QR data is required")
            return
        }

        let data = buildQRCodePayload(from: call, dataStr: dataStr)
        writeBluetoothWithAutoReconnect(data: data, call: call, resolveKey: "printed")
    }

    @objc public func printBluetoothBarcode(_ call: CAPPluginCall) {
        guard let dataStr = call.getString("data"), !dataStr.isEmpty else {
            call.reject("Barcode data is required")
            return
        }

        let data = buildBarcodePayload(from: call, dataStr: dataStr)
        writeBluetoothWithAutoReconnect(data: data, call: call, resolveKey: "printed")
    }

    @objc public func sendRawBluetooth(_ call: CAPPluginCall) {
        guard let data = decodeRawData(from: call) else {
            call.reject("Raw data requires base64, bytes, or text")
            return
        }

        writeBluetoothWithAutoReconnect(data: data, call: call, resolveKey: "printed")
    }

    @objc public func kickBluetoothDrawer(_ call: CAPPluginCall) {
        let kickData = buildDrawerPayload(from: call)
        writeBluetoothWithAutoReconnect(data: kickData, call: call, resolveKey: "kicked")
    }

    @objc public func cutBluetoothPaper(_ call: CAPPluginCall) {
        let cutData = buildCutPayload(from: call)
        writeBluetoothWithAutoReconnect(data: cutData, call: call, resolveKey: "cut")
    }

    @objc public func disconnectBluetooth(_ call: CAPPluginCall) {
        let forget = call.getBool("forget") ?? false
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            self.cancelConnectTimeout()
            if let connectedPeripheral = self.connectedPeripheral {
                self.centralManager?.cancelPeripheralConnection(connectedPeripheral)
            }
            if let connectingPeripheral = self.connectingPeripheral {
                self.centralManager?.cancelPeripheralConnection(connectingPeripheral)
            }

            self.failPendingBleJobs(message: "Bluetooth disconnected")
            self.failDeferredBlePrints(message: "Bluetooth disconnected")
            self.connectedPeripheral = nil
            self.connectingPeripheral = nil
            self.writeCharacteristic = nil
            self.pendingConnectAddress = nil

            if forget {
                UserDefaults.standard.removeObject(forKey: self.keyBtAddress)
                UserDefaults.standard.removeObject(forKey: self.keyBtName)
            }

            call.resolve()
        }
    }

    private func writeBluetoothWithAutoReconnect(data: Data, call: CAPPluginCall, resolveKey: String) {
        DispatchQueue.main.async { [weak self] in
            guard let self else { return }
            if let peripheral = self.connectedPeripheral, peripheral.state == .connected, let characteristic = self.writeCharacteristic {
                self.enqueueBleWrite(data: data, to: peripheral, characteristic: characteristic, call: call, resolveKey: resolveKey)
                return
            }

            guard let savedAddress = UserDefaults.standard.string(forKey: self.keyBtAddress), !savedAddress.isEmpty else {
                call.reject("No Bluetooth printer connected")
                return
            }

            self.deferredBlePrints.append(PendingDeferredBlePrint(data: data, call: call, resolveKey: resolveKey))
            if self.pendingConnectAddress == nil {
                self.initiateBluetoothConnect(address: savedAddress, call: nil)
            }
        }
    }

    // MARK: - USB & Thermal Methods (Not supported on iOS)

    @objc public func listUsbPrinters(_ call: CAPPluginCall) {
        call.resolve([
            "devices": [],
            "count": 0
        ])
    }

    @objc public func isUsbConnected(_ call: CAPPluginCall) {
        call.resolve(["connected": false])
    }

    @objc public func connectUsbPrinter(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func printUsbText(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func printUsbImage(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func printUsbQRCode(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func printUsbBarcode(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func sendRawUsb(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func kickUsbDrawer(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func cutUsbPaper(_ call: CAPPluginCall) {
        call.reject("Generic USB printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func disconnectUsbPrinter(_ call: CAPPluginCall) {
        call.resolve()
    }

    @objc public func isThermalConnected(_ call: CAPPluginCall) {
        call.resolve(["connected": false])
    }

    @objc public func connectThermalPrinter(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func printThermalText(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func printThermalImage(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func printThermalQRCode(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func printThermalBarcode(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func sendRawThermal(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func kickThermalDrawer(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func cutThermalPaper(_ call: CAPPluginCall) {
        call.reject("Generic USB thermal printers are not supported on iOS. Use Bluetooth or Network printers on iOS.")
    }

    @objc public func disconnectThermalPrinter(_ call: CAPPluginCall) {
        call.resolve()
    }

    // MARK: - Network (LAN / Wi-Fi TCP 9100) Methods

    @objc public func connectNetworkPrinter(_ call: CAPPluginCall) {
        guard let address = call.getString("address"), !address.isEmpty else {
            call.reject("Network address is required")
            return
        }

        let portInt = call.getInt("port") ?? 9100
        let timeoutMs = max(call.getInt("timeout") ?? 5000, 500)

        connectNetworkInternal(address: address, portInt: portInt, timeoutMs: timeoutMs) { result in
            switch result {
            case .success(let payload):
                call.resolve(payload)
            case .failure(let error):
                call.reject(error.localizedDescription)
            }
        }
    }

    private func connectNetworkInternal(
        address: String,
        portInt: Int,
        timeoutMs: Int,
        completion: @escaping (Result<[String: Any], Error>) -> Void
    ) {
        guard portInt > 0 && portInt <= 65535, let nwPort = NWEndpoint.Port(rawValue: UInt16(portInt)) else {
            completion(.failure(NSError(domain: "ZPrinter", code: 1, userInfo: [NSLocalizedDescriptionKey: "Invalid network port"])))
            return
        }

        networkQueue.async { [weak self] in
            guard let self else { return }

            self.closeNetworkConnectionLocked()

            let tcpOptions = NWProtocolTCP.Options()
            tcpOptions.noDelay = true
            let params = NWParameters(tls: nil, tcp: tcpOptions)
            let connection = NWConnection(host: NWEndpoint.Host(address), port: nwPort, using: params)

            var settled = false
            let timeoutItem = DispatchWorkItem { [weak self] in
                guard !settled else { return }
                settled = true
                connection.cancel()
                self?.networkReady = false
                completion(.failure(NSError(domain: "ZPrinter", code: 2, userInfo: [NSLocalizedDescriptionKey: "Network connect timed out"])))
            }

            self.networkQueue.asyncAfter(deadline: .now() + .milliseconds(timeoutMs), execute: timeoutItem)

            connection.stateUpdateHandler = { [weak self] state in
                guard let self else { return }
                switch state {
                case .ready:
                    guard !settled else { return }
                    settled = true
                    timeoutItem.cancel()
                    self.networkConnection = connection
                    self.networkHost = address
                    self.networkPort = UInt16(portInt)
                    self.networkReady = true

                    UserDefaults.standard.set(address, forKey: self.keyNetAddress)
                    UserDefaults.standard.set(portInt, forKey: self.keyNetPort)
                    UserDefaults.standard.set(timeoutMs, forKey: self.keyNetTimeout)

                    completion(.success([
                        "connected": true,
                        "deviceName": "Network Printer (\(address):\(portInt))",
                        "deviceAddress": address
                    ]))
                case .failed(let error):
                    self.networkReady = false
                    if !settled {
                        settled = true
                        timeoutItem.cancel()
                        connection.cancel()
                        completion(.failure(NSError(
                            domain: "ZPrinter",
                            code: 3,
                            userInfo: [NSLocalizedDescriptionKey: "Network connect failed: \(error.localizedDescription)"]
                        )))
                    }
                case .cancelled:
                    self.networkReady = false
                default:
                    break
                }
            }

            connection.start(queue: self.networkQueue)
        }
    }

    @objc public func isNetworkConnected(_ call: CAPPluginCall) {
        networkQueue.async { [weak self] in
            guard let self else { return }
            var result: [String: Any] = ["connected": self.networkReady && self.networkConnection != nil]
            if self.networkReady && self.networkConnection != nil {
                result["deviceName"] = "Network Printer (\(self.networkHost):\(self.networkPort))"
                result["deviceAddress"] = self.networkHost
            }
            call.resolve(result)
        }
    }

    @objc public func printNetworkText(_ call: CAPPluginCall) {
        guard let text = call.getString("text"), !text.isEmpty else {
            call.reject("Text is required")
            return
        }

        let payload = buildTextPayload(from: call, text: text)
        sendNetworkPayload(payload, call: call, resolveKey: "printed", errorPrefix: "Network print failed")
    }

    @objc public func printNetworkImage(_ call: CAPPluginCall) {
        guard let base64 = call.getString("base64"), !base64.isEmpty else {
            call.reject("Base64 image string is required")
            return
        }

        guard let payload = buildImagePayload(from: call, base64: base64) else {
            call.reject("Failed to decode or format image")
            return
        }

        sendNetworkPayload(payload, call: call, resolveKey: "printed", errorPrefix: "Network image print failed")
    }

    @objc public func printNetworkQRCode(_ call: CAPPluginCall) {
        guard let dataStr = call.getString("data"), !dataStr.isEmpty else {
            call.reject("QR data is required")
            return
        }

        let payload = buildQRCodePayload(from: call, dataStr: dataStr)
        sendNetworkPayload(payload, call: call, resolveKey: "printed", errorPrefix: "Network QR print failed")
    }

    @objc public func printNetworkBarcode(_ call: CAPPluginCall) {
        guard let dataStr = call.getString("data"), !dataStr.isEmpty else {
            call.reject("Barcode data is required")
            return
        }

        let payload = buildBarcodePayload(from: call, dataStr: dataStr)
        sendNetworkPayload(payload, call: call, resolveKey: "printed", errorPrefix: "Network barcode print failed")
    }

    @objc public func sendRawNetwork(_ call: CAPPluginCall) {
        guard let payload = decodeRawData(from: call) else {
            call.reject("Raw data requires base64, bytes, or text")
            return
        }

        sendNetworkPayload(payload, call: call, resolveKey: "printed", errorPrefix: "Network raw print failed")
    }

    @objc public func kickNetworkDrawer(_ call: CAPPluginCall) {
        let payload = buildDrawerPayload(from: call)
        sendNetworkPayload(payload, call: call, resolveKey: "kicked", errorPrefix: "Network drawer kick failed")
    }

    @objc public func cutNetworkPaper(_ call: CAPPluginCall) {
        let payload = buildCutPayload(from: call)
        sendNetworkPayload(payload, call: call, resolveKey: "cut", errorPrefix: "Network cut failed")
    }

    @objc public func disconnectNetworkPrinter(_ call: CAPPluginCall) {
        let forget = call.getBool("forget") ?? false
        networkQueue.async { [weak self] in
            guard let self else { return }
            self.closeNetworkConnectionLocked()
            if forget {
                UserDefaults.standard.removeObject(forKey: self.keyNetAddress)
                UserDefaults.standard.removeObject(forKey: self.keyNetPort)
                UserDefaults.standard.removeObject(forKey: self.keyNetTimeout)
            }
            call.resolve()
        }
    }

    private func sendNetworkPayload(_ data: Data, call: CAPPluginCall, resolveKey: String, errorPrefix: String, allowRetry: Bool = true) {
        networkQueue.async { [weak self] in
            guard let self else { return }

            if let connection = self.networkConnection, self.networkReady {
                connection.send(content: data, completion: .contentProcessed { [weak self] error in
                    guard let self else { return }
                    if let error {
                        if allowRetry, let savedAddr = UserDefaults.standard.string(forKey: self.keyNetAddress), !savedAddr.isEmpty {
                            let savedPort = UserDefaults.standard.integer(forKey: self.keyNetPort)
                            let savedTimeout = UserDefaults.standard.integer(forKey: self.keyNetTimeout)
                            self.connectNetworkInternal(
                                address: savedAddr,
                                portInt: savedPort > 0 ? savedPort : 9100,
                                timeoutMs: savedTimeout > 0 ? savedTimeout : 5000
                            ) { connectResult in
                                switch connectResult {
                                case .success:
                                    self.sendNetworkPayload(data, call: call, resolveKey: resolveKey, errorPrefix: errorPrefix, allowRetry: false)
                                case .failure(let reconnectErr):
                                    call.reject("\(errorPrefix): \(reconnectErr.localizedDescription)")
                                }
                            }
                        } else {
                            call.reject("\(errorPrefix): \(error.localizedDescription)")
                        }
                    } else {
                        call.resolve([resolveKey: true])
                    }
                })
                return
            }

            // Auto-reconnect to saved network printer if currently disconnected
            if allowRetry, let savedAddr = UserDefaults.standard.string(forKey: self.keyNetAddress), !savedAddr.isEmpty {
                let savedPort = UserDefaults.standard.integer(forKey: self.keyNetPort)
                let savedTimeout = UserDefaults.standard.integer(forKey: self.keyNetTimeout)
                self.connectNetworkInternal(
                    address: savedAddr,
                    portInt: savedPort > 0 ? savedPort : 9100,
                    timeoutMs: savedTimeout > 0 ? savedTimeout : 5000
                ) { connectResult in
                    switch connectResult {
                    case .success:
                        self.sendNetworkPayload(data, call: call, resolveKey: resolveKey, errorPrefix: errorPrefix, allowRetry: false)
                    case .failure(let reconnectErr):
                        call.reject("\(errorPrefix): \(reconnectErr.localizedDescription)")
                    }
                }
                return
            }

            call.reject("\(errorPrefix): Network printer not connected")
        }
    }

    private func closeNetworkConnectionLocked() {
        networkConnection?.stateUpdateHandler = nil
        networkConnection?.cancel()
        networkConnection = nil
        networkReady = false
        networkHost = ""
        networkPort = 9100
    }

    // MARK: - CoreBluetooth Delegates

    public func centralManagerDidUpdateState(_ central: CBCentralManager) {
        if let pendingScanCall {
            if central.state == .poweredOn {
                startScanning()
            } else {
                pendingScanCall.reject(bluetoothStateMessage(for: central.state))
                self.pendingScanCall = nil
            }
        }

        if let address = pendingConnectAddress {
            if central.state == .poweredOn {
                let call = pendingConnectCall
                pendingConnectCall = nil
                pendingConnectAddress = nil
                initiateBluetoothConnect(address: address, call: call)
            } else if central.state != .unknown && central.state != .resetting {
                let msg = bluetoothStateMessage(for: central.state)
                pendingConnectCall?.reject(msg)
                pendingConnectCall = nil
                pendingConnectAddress = nil
                failDeferredBlePrints(message: msg)
            }
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
        cancelConnectTimeout()
        connectingPeripheral = nil
        let message = error?.localizedDescription ?? "Failed to connect"
        pendingConnectCall?.reject(message)
        pendingConnectCall = nil
        pendingConnectAddress = nil
        failDeferredBlePrints(message: message)
    }

    public func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        if connectedPeripheral?.identifier == peripheral.identifier {
            failPendingBleJobs(message: "Bluetooth printer disconnected")
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
            cancelConnectTimeout()
            connectingPeripheral = nil
            pendingConnectCall?.reject(error.localizedDescription)
            pendingConnectCall = nil
            pendingConnectAddress = nil
            failDeferredBlePrints(message: error.localizedDescription)
            return
        }

        guard let services = peripheral.services, !services.isEmpty else {
            cancelConnectTimeout()
            connectingPeripheral = nil
            let msg = "No Bluetooth services found on printer"
            pendingConnectCall?.reject(msg)
            pendingConnectCall = nil
            pendingConnectAddress = nil
            failDeferredBlePrints(message: msg)
            return
        }

        services.forEach { service in
            peripheral.discoverCharacteristics(nil, for: service)
        }
    }

    public func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        if let error {
            cancelConnectTimeout()
            connectingPeripheral = nil
            pendingConnectCall?.reject(error.localizedDescription)
            pendingConnectCall = nil
            pendingConnectAddress = nil
            failDeferredBlePrints(message: error.localizedDescription)
            return
        }

        guard let characteristics = service.characteristics else {
            return
        }

        for characteristic in characteristics
            where characteristic.properties.contains(.write) || characteristic.properties.contains(.writeWithoutResponse) {
            cancelConnectTimeout()
            connectingPeripheral = nil
            writeCharacteristic = characteristic

            let deviceName = peripheral.name ?? "Bluetooth Printer"
            let deviceAddress = peripheral.identifier.uuidString
            UserDefaults.standard.set(deviceAddress, forKey: keyBtAddress)
            UserDefaults.standard.set(deviceName, forKey: keyBtName)

            pendingConnectCall?.resolve([
                "connected": true,
                "deviceName": deviceName,
                "deviceAddress": deviceAddress
            ])
            pendingConnectCall = nil
            pendingConnectAddress = nil

            notifyListeners("printerReady", data: [
                "deviceName": deviceName,
                "deviceAddress": deviceAddress
            ])

            flushDeferredBlePrints(peripheral: peripheral, characteristic: characteristic)
            return
        }
    }

    public func peripheral(_ peripheral: CBPeripheral, didWriteValueFor characteristic: CBCharacteristic, error: Error?) {
        guard var job = activeBleJob, job.writeType == .withResponse else {
            return
        }

        if let error {
            activeBleJob = nil
            job.call.reject("Bluetooth write failed: \(error.localizedDescription)")
            processNextBleJob()
            return
        }

        job.index += 1
        if job.index < job.chunks.count {
            activeBleJob = job
            job.peripheral.writeValue(job.chunks[job.index], for: job.characteristic, type: .withResponse)
        } else {
            activeBleJob = nil
            job.call.resolve([job.resolveKey: true])
            processNextBleJob()
        }
    }

    // MARK: - BLE Scanning & Paced Chunk Writing

    private func flushDeferredBlePrints(peripheral: CBPeripheral, characteristic: CBCharacteristic) {
        let pending = deferredBlePrints
        deferredBlePrints.removeAll()
        for item in pending {
            enqueueBleWrite(data: item.data, to: peripheral, characteristic: characteristic, call: item.call, resolveKey: item.resolveKey)
        }
    }

    private func failDeferredBlePrints(message: String) {
        let pending = deferredBlePrints
        deferredBlePrints.removeAll()
        for item in pending {
            item.call.reject(message)
        }
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

    private func enqueueBleWrite(
        data: Data,
        to peripheral: CBPeripheral,
        characteristic: CBCharacteristic,
        call: CAPPluginCall,
        resolveKey: String
    ) {
        let writeType: CBCharacteristicWriteType = characteristic.properties.contains(.write) ? .withResponse : .withoutResponse
        let maxLen = peripheral.maximumWriteValueLength(for: writeType)
        let chunkSize = max(20, min(maxLen, 180))
        let chunks = data.chunked(into: chunkSize)

        let job = BleWriteJob(
            chunks: chunks,
            index: 0,
            peripheral: peripheral,
            characteristic: characteristic,
            writeType: writeType,
            call: call,
            resolveKey: resolveKey
        )

        bleJobQueue.append(job)
        if activeBleJob == nil {
            processNextBleJob()
        }
    }

    private func processNextBleJob() {
        guard activeBleJob == nil, !bleJobQueue.isEmpty else {
            return
        }

        let job = bleJobQueue.removeFirst()
        if job.chunks.isEmpty {
            job.call.resolve([job.resolveKey: true])
            processNextBleJob()
            return
        }

        activeBleJob = job
        if job.writeType == .withResponse {
            job.peripheral.writeValue(job.chunks[0], for: job.characteristic, type: .withResponse)
        } else {
            sendNextChunkWithoutResponse()
        }
    }

    private func sendNextChunkWithoutResponse() {
        guard var job = activeBleJob, job.writeType == .withoutResponse else {
            return
        }

        job.peripheral.writeValue(job.chunks[job.index], for: job.characteristic, type: .withoutResponse)
        job.index += 1

        if job.index < job.chunks.count {
            activeBleJob = job
            DispatchQueue.main.asyncAfter(deadline: .now() + .milliseconds(15)) { [weak self] in
                self?.sendNextChunkWithoutResponse()
            }
        } else {
            activeBleJob = nil
            job.call.resolve([job.resolveKey: true])
            processNextBleJob()
        }
    }

    private func failPendingBleJobs(message: String) {
        if let active = activeBleJob {
            activeBleJob = nil
            active.call.reject(message)
        }
        let queued = bleJobQueue
        bleJobQueue.removeAll()
        for job in queued {
            job.call.reject(message)
        }
    }

    // MARK: - ESC/POS Payload Builders

    private func buildTextPayload(from call: CAPPluginCall, text: String) -> Data {
        return formatPrintText(
            text: text,
            fontSize: call.getInt("fontSize") ?? 24,
            align: call.getString("align") ?? "left",
            isBold: call.getBool("isBold") ?? false,
            isUnderline: call.getBool("isUnderline") ?? false,
            feedLines: max(call.getInt("feedLines") ?? 2, 0),
            encoding: call.getString("encoding") ?? "UTF-8",
            codePage: call.getInt("codePage"),
            reset: call.getBool("reset") ?? true
        )
    }

    private func buildImagePayload(from call: CAPPluginCall, base64: String) -> Data? {
        return formatPrintImage(
            base64: base64,
            width: call.getInt("width") ?? 0,
            height: call.getInt("height") ?? 0,
            align: call.getString("align") ?? "left",
            threshold: call.getInt("threshold") ?? 128,
            mode: call.getString("mode") ?? "raster",
            feedLines: max(call.getInt("feedLines") ?? 1, 0)
        )
    }

    private func buildQRCodePayload(from call: CAPPluginCall, dataStr: String) -> Data {
        return formatQRCode(
            data: dataStr,
            size: call.getInt("size") ?? 8,
            align: call.getString("align") ?? "center",
            errorCorrection: call.getString("errorCorrection") ?? "L",
            feedLines: max(call.getInt("feedLines") ?? 1, 1)
        )
    }

    private func buildBarcodePayload(from call: CAPPluginCall, dataStr: String) -> Data {
        return formatBarcode(
            data: dataStr,
            type: call.getString("type") ?? "CODE128",
            height: call.getInt("height") ?? 80,
            width: call.getInt("width") ?? 2,
            align: call.getString("align") ?? "center",
            hriPosition: call.getString("hriPosition") ?? "below",
            feedLines: max(call.getInt("feedLines") ?? 1, 1)
        )
    }

    private func buildDrawerPayload(from call: CAPPluginCall) -> Data {
        let pin: UInt8 = (call.getInt("pin") ?? 0) == 1 ? 0x01 : 0x00
        let onTime = UInt8(min(max(call.getInt("onTime") ?? 25, 0), 255))
        let offTime = UInt8(min(max(call.getInt("offTime") ?? 250, 0), 255))
        return Data([0x1B, 0x70, pin, onTime, offTime])
    }

    private func buildCutPayload(from call: CAPPluginCall) -> Data {
        let partial = call.getBool("partial") ?? false
        let feedLines = max(call.getInt("feedLines") ?? 3, 0)
        var data = Data()
        for _ in 0..<feedLines {
            data.append(0x0A)
        }
        data.append(contentsOf: [0x1D, 0x56, partial ? 0x01 : 0x00])
        return data
    }

    private func decodeRawData(from call: CAPPluginCall) -> Data? {
        if let base64 = call.getString("base64"), !base64.isEmpty {
            let cleaned = stripDataUriPrefix(base64)
            return Data(base64Encoded: cleaned, options: .ignoreUnknownCharacters)
        }

        if let byteValues = call.getArray("bytes", Int.self), !byteValues.isEmpty {
            let bytes = byteValues.map { UInt8(truncatingIfNeeded: $0) }
            return Data(bytes)
        }

        if let text = call.getString("text"), !text.isEmpty {
            return text.data(using: .utf8)
        }

        return nil
    }

    private func formatPrintText(
        text: String,
        fontSize: Int,
        align: String,
        isBold: Bool,
        isUnderline: Bool,
        feedLines: Int,
        encoding: String,
        codePage: Int?,
        reset: Bool
    ) -> Data {
        var commands = Data()

        if reset {
            commands.append(contentsOf: [0x1B, 0x40])
        }

        if let codePage, codePage >= 0 && codePage <= 255 {
            commands.append(contentsOf: [0x1B, 0x74, UInt8(codePage)])
        }

        commands.append(contentsOf: [0x1B, 0x61, alignmentByte(align)])
        commands.append(contentsOf: [0x1B, 0x45, isBold ? 0x01 : 0x00])
        commands.append(contentsOf: [0x1B, 0x2D, isUnderline ? 0x01 : 0x00])

        let sizeCode: UInt8
        if fontSize >= 48 {
            sizeCode = 0x22
        } else if fontSize >= 32 {
            sizeCode = 0x11
        } else {
            sizeCode = 0x00
        }
        commands.append(contentsOf: [0x1D, 0x21, sizeCode])

        let stringEncoding = resolveStringEncoding(encoding)
        if let textData = text.data(using: stringEncoding, allowLossyConversion: true) ?? text.data(using: .utf8) {
            commands.append(textData)
        }

        for _ in 0..<feedLines {
            commands.append(0x0A)
        }

        commands.append(contentsOf: [0x1B, 0x45, 0x00])
        commands.append(contentsOf: [0x1B, 0x2D, 0x00])
        commands.append(contentsOf: [0x1D, 0x21, 0x00])
        commands.append(contentsOf: [0x1B, 0x61, 0x00])

        return commands
    }

    private func formatQRCode(data: String, size: Int, align: String, errorCorrection: String, feedLines: Int) -> Data {
        var commands = Data()
        commands.append(contentsOf: [0x1B, 0x61, alignmentByte(align)])

        let dataBytes = data.data(using: .utf8) ?? Data()
        let storeLen = dataBytes.count + 3
        let storePL = UInt8(storeLen % 256)
        let storePH = UInt8(storeLen / 256)

        commands.append(contentsOf: [0x1D, 0x28, 0x6B, 0x04, 0x00, 0x31, 0x41, 0x32, 0x00])
        commands.append(contentsOf: [0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x43, UInt8(min(max(size, 1), 16))])
        commands.append(contentsOf: [0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x45, qrErrorCorrectionByte(errorCorrection)])
        commands.append(contentsOf: [0x1D, 0x28, 0x6B, storePL, storePH, 0x31, 0x50, 0x30])
        commands.append(dataBytes)
        commands.append(contentsOf: [0x1D, 0x28, 0x6B, 0x03, 0x00, 0x31, 0x51, 0x30])

        for _ in 0..<feedLines {
            commands.append(0x0A)
        }
        commands.append(contentsOf: [0x1B, 0x61, 0x00])

        return commands
    }

    private func formatBarcode(
        data: String,
        type: String,
        height: Int,
        width: Int,
        align: String,
        hriPosition: String,
        feedLines: Int
    ) -> Data {
        var commands = Data()
        commands.append(contentsOf: [0x1B, 0x61, alignmentByte(align)])
        commands.append(contentsOf: [0x1D, 0x68, UInt8(min(max(height, 1), 255))])
        commands.append(contentsOf: [0x1D, 0x77, UInt8(min(max(width, 2), 6))])
        commands.append(contentsOf: [0x1D, 0x48, hriPositionByte(hriPosition)])

        let upperType = type.uppercased()
        let barcodeCode = barcodeTypeByte(upperType)
        var payloadStr = data
        if upperType == "CODE128" && !data.hasPrefix("{A") && !data.hasPrefix("{B") && !data.hasPrefix("{C") {
            payloadStr = "{B" + data
        }

        let payloadBytes = Array((payloadStr.data(using: .ascii) ?? Data()).prefix(255))
        commands.append(contentsOf: [0x1D, 0x6B, barcodeCode, UInt8(payloadBytes.count)])
        commands.append(contentsOf: payloadBytes)

        for _ in 0..<feedLines {
            commands.append(0x0A)
        }
        commands.append(contentsOf: [0x1B, 0x61, 0x00])

        return commands
    }

    private func formatPrintImage(
        base64: String,
        width: Int,
        height: Int,
        align: String,
        threshold: Int,
        mode: String,
        feedLines: Int
    ) -> Data? {
        let cleaned = stripDataUriPrefix(base64)
        guard let data = Data(base64Encoded: cleaned, options: .ignoreUnknownCharacters),
              let image = UIImage(data: data) else {
            return nil
        }

        return formatPrintUIImage(
            image: image,
            width: width,
            height: height,
            align: align,
            threshold: threshold,
            mode: mode,
            feedLines: feedLines
        )
    }

    private func formatPrintUIImage(
        image: UIImage,
        width: Int,
        height: Int,
        align: String,
        threshold: Int,
        mode: String,
        feedLines: Int
    ) -> Data? {
        let origW = max(Int(image.size.width * image.scale), 1)
        let origH = max(Int(image.size.height * image.scale), 1)

        var bw = origW
        var bh = origH
        if width > 0 && height > 0 {
            bw = width
            bh = height
        } else if width > 0 {
            bw = width
            bh = max(1, Int(round(Double(origH) * Double(width) / Double(origW))))
        } else if height > 0 {
            bh = height
            bw = max(1, Int(round(Double(origW) * Double(height) / Double(origH))))
        }

        let rendererFormat = UIGraphicsImageRendererFormat()
        rendererFormat.scale = 1.0
        rendererFormat.opaque = true
        let renderer = UIGraphicsImageRenderer(size: CGSize(width: bw, height: bh), format: rendererFormat)
        let flattened = renderer.image { ctx in
            UIColor.white.setFill()
            ctx.fill(CGRect(x: 0, y: 0, width: bw, height: bh))
            image.draw(in: CGRect(x: 0, y: 0, width: bw, height: bh))
        }

        guard let cgImage = flattened.cgImage else { return nil }
        let colorSpace = CGColorSpaceCreateDeviceGray()
        let bitmapInfo = CGImageAlphaInfo.none.rawValue
        guard let context = CGContext(
            data: nil,
            width: bw,
            height: bh,
            bitsPerComponent: 8,
            bytesPerRow: bw,
            space: colorSpace,
            bitmapInfo: bitmapInfo
        ) else {
            return nil
        }

        context.draw(cgImage, in: CGRect(x: 0, y: 0, width: bw, height: bh))
        guard let pixelData = context.data else { return nil }
        let pixels = pixelData.bindMemory(to: UInt8.self, capacity: bw * bh)
        let clampedThreshold = UInt8(min(max(threshold, 0), 255))

        var commands = Data()
        commands.append(contentsOf: [0x1B, 0x61, alignmentByte(align)])

        if mode.lowercased() == "bitimage" {
            let nL = UInt8(bw % 256)
            let nH = UInt8(bw / 256)
            commands.append(contentsOf: [0x1B, 0x33, 24])

            for y in stride(from: 0, to: bh, by: 24) {
                commands.append(contentsOf: [0x1B, 0x2A, 33, nL, nH])
                for x in 0..<bw {
                    for k in 0..<3 {
                        var slice: UInt8 = 0
                        for bit in 0..<8 {
                            let py = y + (k * 8) + bit
                            if py < bh && pixels[py * bw + x] < clampedThreshold {
                                slice |= (0x80 >> bit)
                            }
                        }
                        commands.append(slice)
                    }
                }
                commands.append(0x0A)
            }
            commands.append(contentsOf: [0x1B, 0x32])
        } else {
            let bytesPerRow = (bw + 7) / 8
            let xL = UInt8(bytesPerRow % 256)
            let xH = UInt8(bytesPerRow / 256)
            let yL = UInt8(bh % 256)
            let yH = UInt8(bh / 256)

            commands.append(contentsOf: [0x1D, 0x76, 0x30, 0x00, xL, xH, yL, yH])

            for y in 0..<bh {
                for x in stride(from: 0, to: bw, by: 8) {
                    var b: UInt8 = 0
                    for bit in 0..<8 {
                        if x + bit < bw && pixels[y * bw + x + bit] < clampedThreshold {
                            b |= (0x80 >> bit)
                        }
                    }
                    commands.append(b)
                }
            }
        }

        for _ in 0..<feedLines {
            commands.append(0x0A)
        }
        commands.append(contentsOf: [0x1B, 0x61, 0x00])

        return commands
    }

    // MARK: - Formatting Helpers

    private func stripDataUriPrefix(_ value: String) -> String {
        let trimmed = value.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.hasPrefix("data:"), let commaIdx = trimmed.firstIndex(of: ",") {
            return String(trimmed[trimmed.index(after: commaIdx)...]).trimmingCharacters(in: .whitespacesAndNewlines)
        }
        return trimmed
    }

    private func alignmentByte(_ align: String) -> UInt8 {
        switch align.lowercased() {
        case "center":
            return 0x01
        case "right":
            return 0x02
        default:
            return 0x00
        }
    }

    private func qrErrorCorrectionByte(_ level: String) -> UInt8 {
        switch level.uppercased() {
        case "M":
            return 0x31
        case "Q":
            return 0x32
        case "H":
            return 0x33
        default:
            return 0x30
        }
    }

    private func hriPositionByte(_ position: String) -> UInt8 {
        switch position.lowercased() {
        case "none":
            return 0x00
        case "above":
            return 0x01
        case "both":
            return 0x03
        default:
            return 0x02
        }
    }

    private func barcodeTypeByte(_ type: String) -> UInt8 {
        switch type {
        case "UPCA":
            return 65
        case "UPCE":
            return 66
        case "EAN13":
            return 67
        case "EAN8":
            return 68
        case "CODE39":
            return 69
        case "ITF":
            return 70
        case "CODABAR":
            return 71
        default:
            return 73
        }
    }

    private func resolveStringEncoding(_ encoding: String) -> String.Encoding {
        switch encoding.uppercased() {
        case "ISO-8859-1", "LATIN1":
            return .isoLatin1
        case "ASCII", "US-ASCII":
            return .ascii
        case "WINDOWS-1252", "CP1252":
            return .windowsCP1252
        case "CP437":
            let cfEnc = CFStringConvertEncodingToNSStringEncoding(CFStringEncoding(CFStringEncodings.dosLatinUS.rawValue))
            return String.Encoding(rawValue: cfEnc)
        case "CP850":
            let cfEnc = CFStringConvertEncodingToNSStringEncoding(CFStringEncoding(CFStringEncodings.dosLatin1.rawValue))
            return String.Encoding(rawValue: cfEnc)
        default:
            return .utf8
        }
    }
}

private extension Data {
    func chunked(into size: Int) -> [Data] {
        guard size > 0, !isEmpty else { return isEmpty ? [] : [self] }

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
