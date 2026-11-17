package com.bukutoko.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.webkit.WebViewAssetLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class MainActivity extends Activity {
    static final String HOST = "appassets.androidplatform.net";
    static final int REQ_FILE = 11;
    static final int BG = Color.parseColor("#F4F1EA");

    WebView web;
    ImageView splash;
    FrameLayout root;
    ValueCallback<Uri[]> fileCb;
    boolean pageReady = false, minDone = false;
    final Handler h = new Handler(Looper.getMainLooper());

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        root = new FrameLayout(this);
        root.setBackgroundColor(BG);
        web = new WebView(this);
        web.setBackgroundColor(BG);
        root.addView(web, new FrameLayout.LayoutParams(-1, -1));
        splash = new ImageView(this);
        splash.setBackgroundColor(BG);
        splash.setScaleType(ImageView.ScaleType.FIT_CENTER);
        splash.setImageResource(R.drawable.splash);
        root.addView(splash, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(false);
        s.setSupportZoom(false);
        s.setTextZoom(100);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
                return loader.shouldInterceptRequest(r.getUrl());
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                if (HOST.equals(u.getHost())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) {}
                return true;
            }

            @Override
            public void onPageFinished(WebView v, String url) {
                pageReady = true;
                hideSplash();
            }
        });

        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView v, String url, String msg, final JsResult r) {
                new AlertDialog.Builder(MainActivity.this).setMessage(msg)
                        .setPositiveButton("OK", (d, w) -> r.confirm())
                        .setOnCancelListener(d -> r.cancel()).show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView v, String url, String msg, final JsResult r) {
                new AlertDialog.Builder(MainActivity.this).setMessage(msg)
                        .setPositiveButton("OK", (d, w) -> r.confirm())
                        .setNegativeButton("Batal", (d, w) -> r.cancel())
                        .setOnCancelListener(d -> r.cancel()).show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView v, String url, String msg, String def, final JsPromptResult r) {
                final EditText et = new EditText(MainActivity.this);
                et.setText(def == null ? "" : def);
                et.setInputType(def != null && def.matches("\\d*")
                        ? InputType.TYPE_CLASS_NUMBER : InputType.TYPE_CLASS_TEXT);
                new AlertDialog.Builder(MainActivity.this).setMessage(msg).setView(et)
                        .setPositiveButton("OK", (d, w) -> r.confirm(et.getText().toString()))
                        .setNegativeButton("Batal", (d, w) -> r.cancel())
                        .setOnCancelListener(d -> r.cancel()).show();
                return true;
            }

            @Override
            public boolean onShowFileChooser(WebView w, ValueCallback<Uri[]> cb, FileChooserParams p) {
                if (fileCb != null) fileCb.onReceiveValue(null);
                fileCb = cb;
                try {
                    startActivityForResult(p.createIntent(), REQ_FILE);
                } catch (Exception e) {
                    fileCb = null;
                    return false;
                }
                return true;
            }
        });

        web.addJavascriptInterface(new Bridge(), "TokoApp");
        web.loadUrl("https://" + HOST + "/assets/index.html");
        h.postDelayed(() -> { minDone = true; hideSplash(); }, 1200);
    }

    void hideSplash() {
        if (!pageReady || !minDone || splash == null) return;
        final ImageView sp = splash;
        splash = null;
        sp.animate().alpha(0f).setDuration(300).withEndAction(() -> root.removeView(sp)).start();
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        if (req == REQ_FILE && fileCb != null) {
            fileCb.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res, data));
            fileCb = null;
            return;
        }
        super.onActivityResult(req, res, data);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack(); else super.onBackPressed();
    }

    void toast(final String m) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, m, Toast.LENGTH_LONG).show());
    }

    static byte[] decode(String dataUrl) {
        int i = dataUrl.indexOf(',');
        return Base64.decode(i >= 0 ? dataUrl.substring(i + 1) : dataUrl, Base64.DEFAULT);
    }

    static String safe(String n) {
        String s = (n == null ? "struk" : n).replaceAll("[^A-Za-z0-9._-]", "_");
        return s.isEmpty() ? "struk" : s;
    }

    class Bridge {

        @JavascriptInterface
        public void saveText(String text, String name) {
            try {
                ContentValues v = new ContentValues();
                v.put(MediaStore.MediaColumns.DISPLAY_NAME, safe(name));
                v.put(MediaStore.MediaColumns.MIME_TYPE, "application/json");
                v.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/BukuToko");
                v.put(MediaStore.MediaColumns.IS_PENDING, 1);
                Uri u = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
                if (u == null) throw new Exception("tidak bisa membuat file");
                try (OutputStream os = getContentResolver().openOutputStream(u)) {
                    os.write(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                }
                v.clear();
                v.put(MediaStore.MediaColumns.IS_PENDING, 0);
                getContentResolver().update(u, v, null, null);
                toast("Cadangan tersimpan di Download/BukuToko");
            } catch (Exception e) {
                toast("Gagal menyimpan: " + e.getMessage());
            }
        }
        @JavascriptInterface
        public void saveImage(String dataUrl, String name) {
            try {
                byte[] png = decode(dataUrl);
                ContentValues v = new ContentValues();
                v.put(MediaStore.Images.Media.DISPLAY_NAME, safe(name) + ".png");
                v.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                v.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/BukuToko");
                v.put(MediaStore.Images.Media.IS_PENDING, 1);
                Uri u = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, v);
                if (u == null) throw new Exception("tidak bisa membuat file");
                try (OutputStream os = getContentResolver().openOutputStream(u)) {
                    os.write(png);
                }
                v.clear();
                v.put(MediaStore.Images.Media.IS_PENDING, 0);
                getContentResolver().update(u, v, null, null);
                toast("Struk tersimpan di Galeri (Pictures/BukuToko)");
            } catch (Exception e) {
                toast("Gagal menyimpan: " + e.getMessage());
            }
        }

        @JavascriptInterface
        public void shareImage(String dataUrl, String name) {
            try {
                byte[] png = decode(dataUrl);
                File dir = new File(getCacheDir(), "shared");
                dir.mkdirs();
                File[] old = dir.listFiles();
                if (old != null) for (File f : old) f.delete();
                final File f = new File(dir, safe(name) + ".png");
                try (FileOutputStream fo = new FileOutputStream(f)) { fo.write(png); }
                final Uri uri = FileProvider.getUriForFile(MainActivity.this,
                        getPackageName() + ".fileprovider", f);
                runOnUiThread(() -> {
                    Intent send = new Intent(Intent.ACTION_SEND);
                    send.setType("image/png");
                    send.putExtra(Intent.EXTRA_STREAM, uri);
                    send.setClipData(ClipData.newRawUri("Struk", uri));
                    send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent ch = Intent.createChooser(send, "Bagikan struk");
                    ch.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(ch);
                });
            } catch (Exception e) {
                toast("Gagal membagikan: " + e.getMessage());
            }
        }
    }
}
