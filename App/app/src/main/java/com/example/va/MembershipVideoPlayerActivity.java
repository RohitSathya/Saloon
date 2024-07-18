package com.example.va;

import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MembershipVideoPlayerActivity extends AppCompatActivity {

    private WebView webView;
    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_video_player);

        webView = findViewById(R.id.web_view);
        videoUrl = getIntent().getStringExtra("video_url");

        if (isYouTubeUrl(videoUrl)) {
            loadYouTubeVideo();
        } else if (isGoogleDriveUrl(videoUrl)) {
            loadGoogleDriveVideo();
        } else {
            loadOtherVideo();
        }
    }

    private boolean isYouTubeUrl(String url) {
        return !TextUtils.isEmpty(url) && (url.contains("youtube.com") || url.contains("youtu.be"));
    }

    private boolean isGoogleDriveUrl(String url) {
        return !TextUtils.isEmpty(url) && url.contains("drive.google.com");
    }

    private void loadYouTubeVideo() {
        String videoId = extractYouTubeVideoId(videoUrl);
        String embeddedYouTubeUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=1&controls=1&rel=0&modestbranding=1&iv_load_policy=3&cc_load_policy=1";
        loadUrlInWebView(embeddedYouTubeUrl, true);
    }

    private void loadGoogleDriveVideo() {
        String embeddedGoogleDriveUrl = videoUrl.replace("/view", "/preview");
        loadUrlInWebView(embeddedGoogleDriveUrl, false);
    }

    private void loadOtherVideo() {
        loadUrlInWebView(videoUrl, false);
    }

    private void loadUrlInWebView(String url, boolean isYouTube) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (isGoogleDriveUrl(url) || url.contains("drive.google.com")) {
                    return true;  // Prevent navigation
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (isYouTube) {
                    hideYouTubeElements(view);
                } else if (isGoogleDriveUrl(url)) {
                    hideGoogleDriveElements(view);
                }
            }
        });

        webView.loadUrl(url);
    }

    private void hideYouTubeElements(WebView view) {
        view.evaluateJavascript(
                "(function() { " +
                        "var style = document.createElement('style'); " +
                        "style.type = 'text/css'; " +
                        "style.innerHTML = ' .ytp-chrome-top, " +
                        ".ytp-share-button-visible, " +
                        ".ytp-watermark, " +  // Hides the YouTube watermark
                        ".ytp-title, " +       // Hides the video title
                        ".ytp-logo, " +        // Hides the YouTube logo
                        ".ytp-spinner { display: none !important; }'; " +  // Hides the spinner
                        "document.head.appendChild(style); " +
                        "setTimeout(function() { " +                // Delay autoplay to hide initial logo
                        "document.querySelector('video').play(); " +
                        "}, 0); " +
                        "})()", null);
    }

    private void hideGoogleDriveElements(WebView view) {
        view.evaluateJavascript(
                "(function() { " +
                        "var style = document.createElement('style'); " +
                        "style.type = 'text/css'; " +
                        "style.innerHTML = ' .ndfHFb-c4YZDc, " +  // Hide the 'Open in Drive' button
                        ".ndfHFb-ic4v6b, " +  // Hide the 'Open in Drive' button container
                        ".ndfHFb-c4YZDc-LgbsSe-Bz112c, " +  // Hide the button text
                        ".ndfHFb-mvxztd, " +  // Hide the upper menu
                        ".ndfHFb-oyBOqy, " + // Hide the top right button
                        "{ display: none !important; }'; " +
                        "document.head.appendChild(style); " +
                        "})()", null);
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
