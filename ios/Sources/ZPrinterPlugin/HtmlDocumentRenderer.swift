import Foundation
import UIKit
import WebKit

/// Helper for offscreen WKWebView rendering (HTML -> UIImage, HTML -> PDF, HTML -> AirPrint).
internal final class HtmlDocumentRenderer: NSObject, WKNavigationDelegate {
    private var webView: WKWebView?
    private var completion: ((Result<WKWebView, Error>) -> Void)?
    private var delayMs: Int = 150
    private var targetWidth: CGFloat = 576
    private var finished = false

    static func normalizeHtml(_ html: String) -> String {
        let trimmed = html.trimmingCharacters(in: .whitespacesAndNewlines)
        if trimmed.lowercased().contains("<html") {
            return trimmed
        }
        return """
        <!DOCTYPE html><html><head><meta charset="utf-8">\
        <meta name="viewport" content="width=device-width, initial-scale=1.0">\
        <style>*{box-sizing:border-box;}html,body{margin:0;padding:0;background:#ffffff;color:#000000;font-family:-apple-system,sans-serif;}</style>\
        </head><body>\(trimmed)</body></html>
        """
    }

    static func sanitizePdfFileName(_ fileName: String?) -> String {
        let raw = (fileName ?? "").trimmingCharacters(in: .whitespacesAndNewlines)
        var cleaned: String
        if raw.isEmpty {
            cleaned = "zprinter_\(Int(Date().timeIntervalSince1970 * 1000)).pdf"
        } else {
            let allowed = CharacterSet.alphanumerics.union(CharacterSet(charactersIn: "._-"))
            cleaned = raw.unicodeScalars.map { allowed.contains($0) ? Character($0) : "_" }.reduce("") { $0 + String($1) }
        }
        if !cleaned.lowercased().hasSuffix(".pdf") {
            cleaned += ".pdf"
        }
        return cleaned
    }

    func load(
        html: String,
        width: CGFloat,
        baseUrl: URL?,
        delayMs: Int,
        hostView: UIView?,
        completion: @escaping (Result<WKWebView, Error>) -> Void
    ) {
        DispatchQueue.main.async {
            self.targetWidth = max(width, 64)
            self.delayMs = max(delayMs, 100)
            self.completion = completion
            self.finished = false

            let config = WKWebViewConfiguration()
            let view = WKWebView(frame: CGRect(x: -self.targetWidth - 20, y: 0, width: self.targetWidth, height: 100), configuration: config)
            view.navigationDelegate = self
            view.isOpaque = true
            view.backgroundColor = .white
            view.scrollView.backgroundColor = .white
            view.alpha = 0.01
            self.webView = view

            hostView?.addSubview(view)
            let normalized = HtmlDocumentRenderer.normalizeHtml(html)
            view.loadHTMLString(normalized, baseURL: baseUrl)
        }
    }

    func cleanup() {
        DispatchQueue.main.async {
            self.webView?.navigationDelegate = nil
            self.webView?.removeFromSuperview()
            self.webView = nil
            self.completion = nil
        }
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        guard !finished else { return }
        finished = true

        DispatchQueue.main.asyncAfter(deadline: .now() + .milliseconds(delayMs)) { [weak self] in
            guard let self else { return }
            let js = "Math.max(document.body ? document.body.scrollHeight : 0, document.documentElement ? document.documentElement.scrollHeight : 0)"
            webView.evaluateJavaScript(js) { value, _ in
                var height: CGFloat = 200
                if let num = value as? NSNumber {
                    height = CGFloat(num.doubleValue)
                }
                let clampedHeight = min(max(ceil(height), 1), 16000)
                webView.frame = CGRect(x: -self.targetWidth - 20, y: 0, width: self.targetWidth, height: clampedHeight)
                webView.layoutIfNeeded()

                DispatchQueue.main.asyncAfter(deadline: .now() + .milliseconds(80)) {
                    let cb = self.completion
                    self.completion = nil
                    cb?(.success(webView))
                }
            }
        }
    }

    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        failOnce(error)
    }

    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        failOnce(error)
    }

    private func failOnce(_ error: Error) {
        guard !finished else { return }
        finished = true
        let cb = completion
        completion = nil
        cleanup()
        cb?(.failure(error))
    }
}
