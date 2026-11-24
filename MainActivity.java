package com.apurba.jarvis;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.speech.tts.TextToSpeech;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    WebView web;
    TextToSpeech tts;
    SpeechRecognizer recognizer;
    static final int REQ_AUDIO = 41;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(), "Android");
        setContentView(web);
        tts = new TextToSpeech(this, status -> { if(status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.US); });
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_AUDIO);
        web.loadUrl("file:///android_asset/index.html");
    }
    class Bridge {
        @JavascriptInterface public void speak(String text) {
            runOnUiThread(() -> { if(tts != null) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis"); });
        }
        @JavascriptInterface public void listen() {
            runOnUiThread(() -> startVoiceRecognition());
        }
        @JavascriptInterface public void openUrl(String url) {
            runOnUiThread(() -> {
                try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); }
                catch(Exception e) { Toast.makeText(MainActivity.this, "Cannot open link", Toast.LENGTH_SHORT).show(); }
            });
        }
        @JavascriptInterface public void openApp(String packageName) {
            runOnUiThread(() -> {
                Intent i = getPackageManager().getLaunchIntentForPackage(packageName);
                if(i != null) startActivity(i);
                else Toast.makeText(MainActivity.this, "App not found", Toast.LENGTH_SHORT).show();
            });
        }
    }
    void startVoiceRecognition() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQ_AUDIO); return;
        }
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            web.evaluateJavascript("voiceResult('Speech recognition is unavailable on this phone')", null); return;
        }
        if(recognizer != null) recognizer.destroy();
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle p) { web.evaluateJavascript("setListening(true)", null); }
            public void onBeginningOfSpeech() {}
            public void onRmsChanged(float r) {}
            public void onBufferReceived(byte[] b) {}
            public void onEndOfSpeech() { web.evaluateJavascript("setListening(false)", null); }
            public void onError(int e) { web.evaluateJavascript("setListening(false);voiceResult('I could not hear that. Please try again.')", null); }
            public void onResults(Bundle b) {
                ArrayList<String> r = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(r != null && !r.isEmpty()) {
                    String safe = org.json.JSONObject.quote(r.get(0));
                    web.evaluateJavascript("voiceResult(" + safe + ")", null);
                }
            }
            public void onPartialResults(Bundle b) {}
            public void onEvent(int t, Bundle p) {}
        });
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to JARVIS");
        recognizer.startListening(intent);
    }
    @Override public void onDestroy() {
        if(recognizer != null) recognizer.destroy();
        if(tts != null) tts.shutdown();
        super.onDestroy();
    }
}
