import XCTest
@testable import ZPrinterPlugin

class ZPrinterTests: XCTestCase {
    func testPluginMetadata() {
        let plugin = ZPrinterPlugin()
        XCTAssertEqual(plugin.jsName, "ZPrinter")
        XCTAssertEqual(plugin.pluginMethods.count, 12)
    }
}
