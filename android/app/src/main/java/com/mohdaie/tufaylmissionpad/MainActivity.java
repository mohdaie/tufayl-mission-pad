package com.mohdaie.tufaylmissionpad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.window.OnBackInvokedDispatcher;

import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {

    private WebView webView;
    private TextToSpeech textToSpeech;
    private volatile boolean ttsReady = false;
    private String pendingSpeech = null;
    private float safeTopCssPx = 0f;
    private float safeBottomCssPx = 0f;
    private boolean pageLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textToSpeech = new TextToSpeech(this, this);

        final int cream = Color.rgb(253, 251, 247);
        final int white = Color.WHITE;

        getWindow().setStatusBarColor(cream);
        getWindow().setNavigationBarColor(white);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        );

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(cream);
        root.setFitsSystemWindows(false);

        webView = new WebView(this);
        webView.setBackgroundColor(cream);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setHapticFeedbackEnabled(false);
        configureWebView(webView);

        root.addView(
                webView,
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );

        // Android 15/16 draw modern apps edge-to-edge. Measure the actual
        // system-bar / cutout insets and pass them to the bundled web UI in CSS pixels.
        root.setOnApplyWindowInsetsListener((view, insets) -> {
            int top;
            int bottom;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Insets safeInsets = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout()
                );
                top = safeInsets.top;
                bottom = safeInsets.bottom;
            } else {
                top = insets.getSystemWindowInsetTop();
                bottom = insets.getSystemWindowInsetBottom();
            }

            float density = getResources().getDisplayMetrics().density;
            safeTopCssPx = top / density;
            safeBottomCssPx = bottom / density;
            applySafeAreaToWeb();
            return insets;
        });

        setContentView(root);
        root.requestApplyInsets();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    this::handleBack
            );
        }

        webView.loadUrl("file:///android_asset/www/index.html");
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    private void configureWebView(WebView view) {
        WebSettings settings = view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setSafeBrowsingEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);

        CookieManager.getInstance().setAcceptCookie(false);
        view.addJavascriptInterface(new TtsBridge(), "AndroidTTS");

        view.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pageLoaded = true;
                applySafeAreaToWeb();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();

                if ("file".equalsIgnoreCase(scheme) || "about".equalsIgnoreCase(scheme)) {
                    return false;
                }

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException ignored) {
                    // Ignore links that cannot be handled by the device.
                }
                return true;
            }
        });
    }

    private void applySafeAreaToWeb() {
        if (!pageLoaded || webView == null) {
            return;
        }

        final String script =
                "(function(){" +
                "document.documentElement.style.setProperty('--native-safe-top','" + safeTopCssPx + "px');" +
                "document.documentElement.style.setProperty('--native-safe-bottom','" + safeBottomCssPx + "px');" +
                "})();";

        runOnUiThread(() -> webView.evaluateJavascript(script, null));
    }

    @Override
    public void onInit(int status) {
        if (status != TextToSpeech.SUCCESS || textToSpeech == null) {
            return;
        }

        textToSpeech.setLanguage(Locale.ENGLISH);
        textToSpeech.setSpeechRate(0.85f);
        ttsReady = true;

        if (pendingSpeech != null) {
            String text = pendingSpeech;
            pendingSpeech = null;
            speakNative(text);
        }
    }

    private void speakNative(String text) {
        if (text == null || text.trim().isEmpty() || textToSpeech == null) {
            return;
        }

        if (!ttsReady) {
            pendingSpeech = text;
            return;
        }

        runOnUiThread(() ->
                textToSpeech.speak(
                        text,
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "tufayl-mission-pad"
                )
        );
    }

    private void handleBack() {
        if (webView == null) {
            finishAfterTransition();
            return;
        }

        webView.evaluateJavascript(
                "window.androidBack ? window.androidBack() : false",
                result -> {
                    if (!"true".equals(result)) {
                        finishAfterTransition();
                    }
                }
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            handleBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.removeJavascriptInterface("AndroidTTS");
            webView.stopLoading();
            webView.destroy();
            webView = null;
        }

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }

        super.onDestroy();
    }

    public final class TtsBridge {
        @JavascriptInterface
        public void speak(String text) {
            speakNative(text);
        }
    }
}
