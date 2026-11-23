package com.zprinter.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Base64;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public class HtmlDocumentEngine {

    private static final int MAX_CAPTURE_HEIGHT_PX = 16000;
    private static volatile boolean slowDrawEnabled = false;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor();
    private WebView activePrintWebView;

    public interface BitmapCallback {
        void onSuccess(Bitmap bitmap);
        void onError(String message);
    }

    public interface ImageResultCallback {
        void onSuccess(String base64, String dataUrl, int width, int height, String mimeType);
        void onError(String message);
    }

    public interface PdfResultCallback {
        void onSuccess(String base64, String dataUrl, int pageCount, String filePath);
        void onError(String message);
    }

    public interface NativePrintCallback {
        void onSuccess(boolean printed, String jobName);
        void onError(String message);
    }

    public void htmlToImage(
        Activity activity,
        String html,
        int widthPx,
        String format,
        int quality,
        String baseUrl,
        int delayMs,
        ImageResultCallback callback
    ) {
        int safeWidth = widthPx > 0 ? Math.min(widthPx, 2400) : 576;
        renderHtmlToBitmap(
            activity,
            html,
            safeWidth,
            baseUrl,
            delayMs,
            new BitmapCallback() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    ioExecutor.execute(() -> {
                        try {
                            boolean isJpeg = "jpeg".equalsIgnoreCase(format) || "jpg".equalsIgnoreCase(format);
                            Bitmap.CompressFormat compressFormat = isJpeg ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;
                            String mimeType = isJpeg ? "image/jpeg" : "image/png";
                            int safeQuality = Math.min(Math.max(quality, 1), 100);

                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            bitmap.compress(compressFormat, safeQuality, out);
                            int w = bitmap.getWidth();
                            int h = bitmap.getHeight();
                            bitmap.recycle();

                            String base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
                            String dataUrl = "data:" + mimeType + ";base64," + base64;
                            callback.onSuccess(base64, dataUrl, w, h, mimeType);
                        } catch (Exception exception) {
                            bitmap.recycle();
                            callback.onError("Failed to encode image: " + exception.getMessage());
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            }
        );
    }

    public void htmlToPdf(
        Activity activity,
        String html,
        String pageSize,
        String orientation,
        int customWidthPt,
        int customHeightPt,
        Integer marginPtOpt,
        String fileName,
        boolean saveToFile,
        String baseUrl,
        int delayMs,
        PdfResultCallback callback
    ) {
        String preset = pageSize != null ? pageSize : "A4";
        boolean isReceipt58 = "Receipt58mm".equalsIgnoreCase(preset);
        boolean isReceipt80 = "Receipt80mm".equalsIgnoreCase(preset);
        boolean isContinuousReceipt = isReceipt58 || isReceipt80;

        int pageWidthPt;
        int pageHeightPt;
        if (isReceipt58) {
            pageWidthPt = 164;
            pageHeightPt = 842;
        } else if (isReceipt80) {
            pageWidthPt = 227;
            pageHeightPt = 842;
        } else if ("A5".equalsIgnoreCase(preset)) {
            pageWidthPt = 420;
            pageHeightPt = 595;
        } else if ("Letter".equalsIgnoreCase(preset)) {
            pageWidthPt = 612;
            pageHeightPt = 792;
        } else if ("custom".equalsIgnoreCase(preset) && customWidthPt > 0 && customHeightPt > 0) {
            pageWidthPt = customWidthPt;
            pageHeightPt = customHeightPt;
        } else {
            // A4 default
            pageWidthPt = 595;
            pageHeightPt = 842;
        }

        if (!isContinuousReceipt && "landscape".equalsIgnoreCase(orientation)) {
            int tmp = pageWidthPt;
            pageWidthPt = pageHeightPt;
            pageHeightPt = tmp;
        }

        int marginPt = marginPtOpt != null ? Math.max(marginPtOpt, 0) : (isContinuousReceipt ? 4 : 24);
        int printableWidthPt = Math.max(pageWidthPt - (marginPt * 2), 40);
        int printableHeightPt = Math.max(pageHeightPt - (marginPt * 2), 40);

        // Render at 2x DPI (144 DPI) for sharp text and crisp lines in PDF
        final int scaleFactor = 2;
        final int renderWidthPx = Math.min(Math.max(printableWidthPt * scaleFactor, 160), 2400);
        final int finalPageWidthPt = pageWidthPt;
        final int finalPageHeightPt = pageHeightPt;
        final int finalMarginPt = marginPt;
        final int finalPrintableWidthPt = printableWidthPt;
        final int finalPrintableHeightPt = printableHeightPt;
        final Context appContext = activity.getApplicationContext();

        renderHtmlToBitmap(
            activity,
            html,
            renderWidthPx,
            baseUrl,
            delayMs,
            new BitmapCallback() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    ioExecutor.execute(() -> {
                        PdfDocument pdfDocument = new PdfDocument();
                        try {
                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                            int totalBitmapHeight = bitmap.getHeight();
                            int pageCount = 0;

                            if (isContinuousReceipt) {
                                int contentHeightPt = Math.max(1, (int) Math.ceil((double) totalBitmapHeight / scaleFactor));
                                int fullPageHeightPt = contentHeightPt + (finalMarginPt * 2);
                                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                                    finalPageWidthPt,
                                    fullPageHeightPt,
                                    1
                                ).create();
                                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                                Canvas canvas = page.getCanvas();
                                canvas.drawColor(Color.WHITE);
                                Rect src = new Rect(0, 0, bitmap.getWidth(), totalBitmapHeight);
                                Rect dst = new Rect(
                                    finalMarginPt,
                                    finalMarginPt,
                                    finalMarginPt + finalPrintableWidthPt,
                                    finalMarginPt + contentHeightPt
                                );
                                canvas.drawBitmap(bitmap, src, dst, paint);
                                pdfDocument.finishPage(page);
                                pageCount = 1;
                            } else {
                                int sliceHeightPx = Math.max(1, finalPrintableHeightPt * scaleFactor);
                                int yOffset = 0;
                                while (yOffset < totalBitmapHeight) {
                                    pageCount++;
                                    int currentSliceHeightPx = Math.min(sliceHeightPx, totalBitmapHeight - yOffset);
                                    int drawHeightPt = Math.max(1, (int) Math.round((double) currentSliceHeightPx / scaleFactor));

                                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                                        finalPageWidthPt,
                                        finalPageHeightPt,
                                        pageCount
                                    ).create();
                                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                                    Canvas canvas = page.getCanvas();
                                    canvas.drawColor(Color.WHITE);

                                    Rect src = new Rect(0, yOffset, bitmap.getWidth(), yOffset + currentSliceHeightPx);
                                    Rect dst = new Rect(
                                        finalMarginPt,
                                        finalMarginPt,
                                        finalMarginPt + finalPrintableWidthPt,
                                        finalMarginPt + drawHeightPt
                                    );
                                    canvas.drawBitmap(bitmap, src, dst, paint);
                                    pdfDocument.finishPage(page);

                                    yOffset += currentSliceHeightPx;
                                }
                            }

                            ByteArrayOutputStream pdfBytes = new ByteArrayOutputStream();
                            pdfDocument.writeTo(pdfBytes);
                            byte[] rawPdf = pdfBytes.toByteArray();

                            String savedPath = null;
                            if (saveToFile || (fileName != null && !fileName.trim().isEmpty())) {
                                String safeName = sanitizePdfFileName(fileName);
                                File outFile = new File(appContext.getCacheDir(), safeName);
                                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                                    fos.write(rawPdf);
                                }
                                savedPath = outFile.getAbsolutePath();
                            }

                            String base64 = Base64.encodeToString(rawPdf, Base64.NO_WRAP);
                            String dataUrl = "data:application/pdf;base64," + base64;
                            callback.onSuccess(base64, dataUrl, pageCount, savedPath);
                        } catch (Exception exception) {
                            callback.onError("Failed to generate PDF: " + exception.getMessage());
                        } finally {
                            pdfDocument.close();
                            bitmap.recycle();
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    callback.onError(message);
                }
            }
        );
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void printHtmlNative(
        Activity activity,
        String html,
        String jobName,
        String pageSize,
        String orientation,
        String baseUrl,
        NativePrintCallback callback
    ) {
        if (activity == null) {
            callback.onError("Activity is unavailable");
            return;
        }

        final String effectiveJobName = (jobName != null && !jobName.trim().isEmpty()) ? jobName.trim() : "ZPrinter Document";
        final String effectiveBaseUrl = (baseUrl != null && !baseUrl.trim().isEmpty()) ? baseUrl : "about:blank";
        final String normalizedHtml = normalizeHtml(html);

        mainHandler.post(() -> {
            try {
                if (activePrintWebView != null) {
                    activePrintWebView.destroy();
                    activePrintWebView = null;
                }

                WebView webView = new WebView(activity);
                activePrintWebView = webView;
                WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setAllowFileAccess(false);
                settings.setAllowContentAccess(false);
                settings.setDomStorageEnabled(false);

                webView.setWebViewClient(
                    new WebViewClient() {
                        private boolean handled = false;

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            if (handled) {
                                return;
                            }
                            handled = true;

                            PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
                            if (printManager == null) {
                                callback.onError("Android PrintManager is unavailable");
                                return;
                            }

                            PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(effectiveJobName);
                            PrintAttributes.MediaSize mediaSize = resolvePrintMediaSize(pageSize, orientation);
                            PrintAttributes attributes = new PrintAttributes.Builder().setMediaSize(mediaSize).build();

                            printManager.print(effectiveJobName, printAdapter, attributes);
                            callback.onSuccess(true, effectiveJobName);
                        }

                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                            if (request != null && request.isForMainFrame() && !handled) {
                                handled = true;
                                callback.onError("Failed to load HTML for native print");
                            }
                        }
                    }
                );

                webView.loadDataWithBaseURL(effectiveBaseUrl, normalizedHtml, "text/html", "UTF-8", null);
            } catch (Exception exception) {
                callback.onError("Native print failed: " + exception.getMessage());
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void renderHtmlToBitmap(Activity activity, String html, int widthPx, String baseUrl, int delayMs, BitmapCallback callback) {
        if (activity == null) {
            callback.onError("Activity is unavailable");
            return;
        }

        final int safeWidth = Math.max(widthPx, 64);
        final int safeDelay = Math.max(delayMs, 100);
        final String effectiveBaseUrl = (baseUrl != null && !baseUrl.trim().isEmpty()) ? baseUrl : "about:blank";
        final String normalizedHtml = normalizeHtml(html);

        mainHandler.post(() -> {
            try {
                if (!slowDrawEnabled) {
                    WebView.enableSlowWholeDocumentDraw();
                    slowDrawEnabled = true;
                }

                WebView webView = new WebView(activity);
                webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                webView.setBackgroundColor(Color.WHITE);
                webView.setInitialScale(100);

                WebSettings settings = webView.getSettings();
                settings.setJavaScriptEnabled(true);
                settings.setAllowFileAccess(false);
                settings.setAllowContentAccess(false);
                settings.setLoadWithOverviewMode(false);
                settings.setUseWideViewPort(false);
                settings.setDefaultTextEncodingName("UTF-8");

                int widthSpec = View.MeasureSpec.makeMeasureSpec(safeWidth, View.MeasureSpec.EXACTLY);
                int initHeightSpec = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY);
                webView.measure(widthSpec, initHeightSpec);
                webView.layout(0, 0, safeWidth, 100);

                webView.setWebViewClient(
                    new WebViewClient() {
                        private boolean captured = false;

                        @Override
                        public void onPageFinished(WebView view, String url) {
                            if (captured) {
                                return;
                            }
                            captured = true;

                            mainHandler.postDelayed(() -> measureAndCapture(view, safeWidth, callback), safeDelay);
                        }

                        @Override
                        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                            if (request != null && request.isForMainFrame() && !captured) {
                                captured = true;
                                view.destroy();
                                callback.onError("Failed to render HTML document");
                            }
                        }
                    }
                );

                webView.loadDataWithBaseURL(effectiveBaseUrl, normalizedHtml, "text/html", "UTF-8", null);
            } catch (Exception exception) {
                callback.onError("HTML rendering failed: " + exception.getMessage());
            }
        });
    }

    private void measureAndCapture(WebView webView, int widthPx, BitmapCallback callback) {
        String js =
            "(function(){var b=document.body,e=document.documentElement;" +
            "var h=Math.max(b?b.scrollHeight:0,b?b.offsetHeight:0,e?e.scrollHeight:0,e?e.offsetHeight:0);" +
            "return JSON.stringify({h:h,w:window.innerWidth||0});})();";

        webView.evaluateJavascript(js, (rawValue) -> {
            int targetHeight = 200;
            try {
                if (rawValue != null && !"null".equals(rawValue)) {
                    String unwrapped = rawValue;
                    if (unwrapped.startsWith("\"") && unwrapped.endsWith("\"")) {
                        unwrapped = unwrapped.substring(1, unwrapped.length() - 1).replace("\\\"", "\"");
                    }
                    JSONObject metrics = new JSONObject(unwrapped);
                    double cssH = metrics.optDouble("h", 0);
                    double cssW = metrics.optDouble("w", 0);
                    double ratio = cssW > 0 ? ((double) widthPx / cssW) : 1.0;
                    if (cssH > 0) {
                        targetHeight = (int) Math.ceil(cssH * ratio);
                    }
                }
            } catch (Exception ignored) {
                int fallback = webView.getContentHeight();
                if (fallback > 0) {
                    targetHeight = fallback;
                }
            }

            final int clampedHeight = Math.min(Math.max(targetHeight, 1), MAX_CAPTURE_HEIGHT_PX);
            int wSpec = View.MeasureSpec.makeMeasureSpec(widthPx, View.MeasureSpec.EXACTLY);
            int hSpec = View.MeasureSpec.makeMeasureSpec(clampedHeight, View.MeasureSpec.EXACTLY);
            webView.measure(wSpec, hSpec);
            webView.layout(0, 0, widthPx, clampedHeight);

            mainHandler.postDelayed(
                () -> {
                    try {
                        Bitmap bitmap = Bitmap.createBitmap(widthPx, clampedHeight, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        canvas.drawColor(Color.WHITE);
                        webView.draw(canvas);
                        webView.destroy();
                        callback.onSuccess(bitmap);
                    } catch (Exception exception) {
                        webView.destroy();
                        callback.onError("Bitmap capture failed: " + exception.getMessage());
                    }
                },
                80
            );
        });
    }

    public void destroy() {
        mainHandler.post(() -> {
            if (activePrintWebView != null) {
                activePrintWebView.destroy();
                activePrintWebView = null;
            }
        });
        if (!ioExecutor.isShutdown()) {
            ioExecutor.shutdownNow();
        }
    }

    public static String normalizeHtml(String html) {
        String trimmed = html != null ? html.trim() : "";
        if (trimmed.toLowerCase(Locale.ROOT).contains("<html")) {
            return trimmed;
        }
        return (
            "<!DOCTYPE html><html><head><meta charset=\"utf-8\">" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
            "<style>*{box-sizing:border-box;}html,body{margin:0;padding:0;background:#ffffff;color:#000000;font-family:sans-serif;}</style>" +
            "</head><body>" +
            trimmed +
            "</body></html>"
        );
    }

    public static String sanitizePdfFileName(String fileName) {
        String base = (fileName != null && !fileName.trim().isEmpty())
            ? fileName.trim().replaceAll("[^a-zA-Z0-9._-]", "_")
            : ("zprinter_" + System.currentTimeMillis() + ".pdf");
        if (!base.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            base = base + ".pdf";
        }
        return base;
    }

    private PrintAttributes.MediaSize resolvePrintMediaSize(String pageSize, String orientation) {
        PrintAttributes.MediaSize base;
        if ("A5".equalsIgnoreCase(pageSize)) {
            base = PrintAttributes.MediaSize.ISO_A5;
        } else if ("Letter".equalsIgnoreCase(pageSize)) {
            base = PrintAttributes.MediaSize.NA_LETTER;
        } else {
            base = PrintAttributes.MediaSize.ISO_A4;
        }
        return "landscape".equalsIgnoreCase(orientation) ? base.asLandscape() : base.asPortrait();
    }
}
