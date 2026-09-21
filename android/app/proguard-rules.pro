# Mission Pad v2.1 currently ships without code shrinking.
# Keep the JavaScript bridge explicit if minification is enabled in a future release.
-keepclassmembers class com.mohdaie.tufaylmissionpad.MainActivity$TtsBridge {
    @android.webkit.JavascriptInterface <methods>;
}
