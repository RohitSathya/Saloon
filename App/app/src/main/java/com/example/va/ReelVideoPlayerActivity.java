package com.example.va;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ReelVideoPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer_webview);

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String trailerLink = getIntent().getStringExtra("trailer_link");
        if (trailerLink != null) {
            String embedUrl = getEmbedUrl(trailerLink);
            webView.loadUrl(embedUrl);
        }
    }

    private String getEmbedUrl(String url) {
        if (url.contains("youtube.com") || url.contains("youtu.be")) {
            // For YouTube URLs
            if (url.contains("watch?v=")) {
                return url.replace("watch?v=", "embed/");
            } else if (url.contains("youtu.be/")) {
                return url.replace("youtu.be/", "youtube.com/embed/");
            }
        } else if (url.contains("drive.google.com")) {
            // For Google Drive URLs
            return url.replace("view", "preview");
        }
        // Return original URL if not YouTube or Google Drive
        return url;
    }
}
