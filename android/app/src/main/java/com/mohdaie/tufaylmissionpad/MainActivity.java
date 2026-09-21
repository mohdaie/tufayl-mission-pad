package com.mohdaie.tufaylmissionpad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textToSpeech = new TextToSpeech(this, this);

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(253, 251, 247));
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setHapticFeedbackEnabled(false);
        configureWebView(webView);
        setContentView(webView);

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
