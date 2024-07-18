package com.example.va;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class FreeVideoPlayerActivity extends AppCompatActivity {

    public static final String VIDEO_URL_EXTRA = "video_url";
    public static final String VIDEO_ID_EXTRA = "video_id";
    public static final String VIDEO_NAME_EXTRA = "video_name";
    public static final String THUMBNAIL_LINK_EXTRA = "thumbnail_link";

    private WebView webView;
    private String videoUrl, videoId, videoName, thumbnailLink;
    private FirebaseUser currentUser;
    private DatabaseReference downloadRef, watchlistRef, usersRef;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_video_player);

        webView = findViewById(R.id.web_view);
        videoUrl = getIntent().getStringExtra(VIDEO_URL_EXTRA);
        videoId = getIntent().getStringExtra(VIDEO_ID_EXTRA);
        videoName = getIntent().getStringExtra(VIDEO_NAME_EXTRA);
        thumbnailLink = getIntent().getStringExtra(THUMBNAIL_LINK_EXTRA);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        downloadRef = FirebaseDatabase.getInstance().getReference("downloads").child(currentUser.getUid());
        watchlistRef = FirebaseDatabase.getInstance().getReference("watchlist").child(currentUser.getUid());
        usersRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());

        // Fetch the current user's email
        fetchCurrentUserEmail();
    }

    private void fetchCurrentUserEmail() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserEmail = snapshot.child("email").getValue(String.class);
                if (isGoogleDriveUrl(videoUrl)) {
                    openGoogleDriveInBrowser();
                } else if (isYouTubeUrl(videoUrl)) {
                    loadYouTubeVideo();
                } else {
                    loadOtherVideo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private boolean isYouTubeUrl(String url) {
        return !TextUtils.isEmpty(url) && (url.contains("youtube.com") || url.contains("youtu.be"));
    }

    private boolean isGoogleDriveUrl(String url) {
        return !TextUtils.isEmpty(url) && url.contains("drive.google.com");
    }

    private void openGoogleDriveInBrowser() {
        AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        for (Account account : accounts) {
            if (account.name.equals(currentUserEmail)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                intent.putExtra("email", currentUserEmail);
                startActivity(intent);
                return;
            }
        }
    }

    private void loadGoogleDriveVideo() {
        String embeddedGoogleDriveUrl = videoUrl.replace("/view", "/preview");
        loadUrlInWebView(embeddedGoogleDriveUrl, false);
    }

    private void loadYouTubeVideo() {
        String videoId = extractYouTubeVideoId(videoUrl);
        String embeddedYouTubeUrl = "https://www.youtube.com/embed/" + videoId + "?autoplay=1&controls=1&rel=0&modestbranding=1&iv_load_policy=3&cc_load_policy=1";
        loadUrlInWebView(embeddedYouTubeUrl, true);
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
                        ".ytp-impression-link, " + // Hides the YouTube word link
                        ".ytp-settings-button " +  // Hides the settings button
                        "{ display: none !important; }'; " +
                        "document.head.appendChild(style); " +

                        "function hideElements() { " +
                        "var elements = document.querySelectorAll('.ytp-chrome-top, " +
                        ".ytp-share-button-visible, " +
                        ".ytp-watermark, " +
                        ".ytp-title, " +
                        ".ytp-logo, " +
                        ".ytp-spinner, " +
                        ".ytp-youtube-button, " +
                        ".ytp-impression-link, " +
                        ".ytp-settings-button'); " + // Adds the settings button to the list of elements to hide
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
