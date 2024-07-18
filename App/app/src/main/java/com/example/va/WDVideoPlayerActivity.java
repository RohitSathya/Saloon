package com.example.va;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WDVideoPlayerActivity extends BaseActivity {

    public static final String VIDEO_URL_EXTRA = "video_url";
    public static final String VIDEO_ID_EXTRA = "video_id";
    public static final String VIDEO_NAME_EXTRA = "video_name";
    public static final String THUMBNAIL_LINK_EXTRA = "thumbnail_link";

    private WebView webView;
    private String videoUrl, videoId, videoName, thumbnailLink;
    private FirebaseUser currentUser;
    private DatabaseReference downloadRef, watchlistRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wdvideo_player);

        webView = findViewById(R.id.web_view);
        videoUrl = getIntent().getStringExtra(VIDEO_URL_EXTRA);
        videoId = getIntent().getStringExtra(VIDEO_ID_EXTRA);
        videoName = getIntent().getStringExtra(VIDEO_NAME_EXTRA);
        thumbnailLink = getIntent().getStringExtra(THUMBNAIL_LINK_EXTRA);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        downloadRef = FirebaseDatabase.getInstance().getReference("downloads").child(currentUser.getUid());
        watchlistRef = FirebaseDatabase.getInstance().getReference("watchlist").child(currentUser.getUid());



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
                        ".ytp-spinner, " +     // Hides the spinner
                        ".ytp-youtube-button, " + // Hides the YouTube word button
                        ".ytp-impression-link { display: none !important; }'; " + // Hides the YouTube word link
                        "document.head.appendChild(style); " +

                        "function hideElements() { " +
                        "var elements = document.querySelectorAll('.ytp-chrome-top, " +
                        ".ytp-share-button-visible, " +
                        ".ytp-watermark, " +
                        ".ytp-title, " +
                        ".ytp-logo, " +
                        ".ytp-spinner, " +
                        ".ytp-youtube-button, " +
                        ".ytp-impression-link'); " +
                        "elements.forEach(function(element) { " +
                        "element.style.display = 'none'; " +
                        "}); " +
                        "} " +

                        "var observer = new MutationObserver(hideElements); " +
                        "observer.observe(document.body, { childList: true, subtree: true }); " +

                        "hideElements(); " + // Call it initially to hide elements immediately

                        "setTimeout(function() { " + // Delay autoplay to hide initial logo
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

    private static class VideoItem {
        private String videoId;
        private String videoName;
        private String videoLink;
        private String thumbnailLink;

        public VideoItem() {
            // Default constructor required for calls to DataSnapshot.getValue(VideoItem.class)
        }

        public VideoItem(String videoId, String videoName, String videoLink, String thumbnailLink) {
            this.videoId = videoId;
            this.videoName = videoName;
            this.videoLink = videoLink;
            this.thumbnailLink = thumbnailLink;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public String getVideoLink() {
            return videoLink;
        }

        public void setVideoLink(String videoLink) {
            this.videoLink = videoLink;
        }

        public String getThumbnailLink() {
            return thumbnailLink;
        }

        public void setThumbnailLink(String thumbnailLink) {
            this.thumbnailLink = thumbnailLink;
        }
    }
}
