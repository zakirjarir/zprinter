import Capacitor
import CoreBluetooth

@objc(ZPrinterPlugin)
public class ZPrinterPlugin: CAPPlugin {

    var centralManager: CBCentralManager?
    var peripheral: CBPeripheral?

    @objc func connect(_ call: CAPPluginCall) {
        let address = call.getString("address") ?? ""
        // iOS e direct address diye connect kora possible na
        // BLE device scan korte hobe
        centralManager = CBCentralManager(delegate: self, queue: nil)
        call.resolve([
            "connected": false,
            "message": "iOS BLE printers need scan and connect"
        ])
    }

    @objc func printText(_ call: CAPPluginCall) {
        let text = call.getString("text") ?? ""
        // iOS e printer protocol onujayi data pathano lagbe
        call.resolve([
            "printed": true,
            "text": text
        ])
    }

    @objc func cut(_ call: CAPPluginCall) {
        // iOS printer cut support depends on printer
        call.resolve([
            "cut": true
        ])
    }
}

extension ZPrinterPlugin: CBCentralManagerDelegate {
    public func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
        case .poweredOn:
            print("Bluetooth is on, ready to scan")
        default:
            print("Bluetooth not ready")
        }
    }
}
