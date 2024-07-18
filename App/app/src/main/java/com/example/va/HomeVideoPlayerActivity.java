package com.example.va;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class HomeVideoPlayerActivity extends AppCompatActivity {

    private WebView webView;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_video_player);

        webView = findViewById(R.id.web_view);
        videoUrl = getIntent().getStringExtra("video_url");

        if (isYouTubeUrl(videoUrl)) {
            loadYouTubeVideo();
        } else {
            loadOtherVideo();
        }
    }

    private boolean isYouTubeUrl(String url) {
        return !TextUtils.isEmpty(url) && (url.contains("youtube.com") || url.contains("youtu.be"));
    }

    private void loadYouTubeVideo() {
        String videoId = extractYouTubeVideoId(videoUrl);
        String embeddedYouTubeUrl = "https://www.youtube.com/embed/" + videoId;
        loadUrlInWebView(embeddedYouTubeUrl);
    }

    private void loadOtherVideo() {
        loadUrlInWebView(videoUrl);
    }

    private void loadUrlInWebView(String url) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(url);
    }

    private String extractYouTubeVideoId(String url) {
        String videoId = null;
        if (url != null) {
            String[] parts = url.split("v=");
            if (parts.length > 1) {
                videoId = parts[1];
                int ampersandIndex = videoId.indexOf('&');
                if (ampersandIndex != -1) {
                    videoId = videoId.substring(0, ampersandIndex);
                }
            } else if (url.contains("youtu.be/")) {
                videoId = url.substring(url.lastIndexOf("/") + 1);
            }
        }
        return videoId;
    }
}
