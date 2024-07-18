package com.example.va;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideoPlayerActivity extends BaseActivity {

    private PlayerView playerView;
    private ExoPlayer player;
    private FloatingActionButton downloadButton;
    private FloatingActionButton addToWatchlistButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String videoId;
    private String videoTitle;
    private String videoUrl;
    private String videoThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.player_view);
        downloadButton = findViewById(R.id.download_button);
        addToWatchlistButton = findViewById(R.id.add_to_watchlist_button);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        videoId = getIntent().getStringExtra("video_id");
        videoUrl = getIntent().getStringExtra("video_url");
        videoTitle = getIntent().getStringExtra("video_title");
        videoThumbnail = getIntent().getStringExtra("video_thumbnail");
        boolean showOptions = getIntent().getBooleanExtra("show_options", true);

        if (!showOptions) {
            downloadButton.setVisibility(View.GONE);
            addToWatchlistButton.setVisibility(View.GONE);
        }

        initializePlayer(videoUrl);

        downloadButton.setOnClickListener(v -> checkAndRemoveOrSaveVideo("downloads"));
        addToWatchlistButton.setOnClickListener(v -> checkAndRemoveOrSaveVideo("watchlist"));
    }

    private void initializePlayer(String videoUrl) {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.setMediaItem(MediaItem.fromUri(Uri.parse(videoUrl)));
        player.prepare();
        player.play();
    }

    private void checkAndRemoveOrSaveVideo(String table) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        databaseReference.child(table).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean videoExists = false;
                String videoKey = null;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VideoItem videoItem = dataSnapshot.getValue(VideoItem.class);
                    if (videoItem != null && videoItem.getVideoUrl() != null && videoItem.getVideoUrl().equals(videoUrl)) {
                        videoExists = true;
                        videoKey = dataSnapshot.getKey();
                        break;
                    }
                }

                if (videoExists) {
                    // Video exists, prompt to remove
                    showRemoveDialog(table, videoKey);
                } else {
                    // Video does not exist, save it
                    saveVideoToDatabase(table);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VideoPlayerActivity.this, "Failed to check video status", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRemoveDialog(String table, String videoKey) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Video")
                .setMessage("Do you want to remove this video from " + table + "?")
                .setPositiveButton("Yes", (dialog, which) -> removeVideoFromDatabase(table, videoKey))
                .setNegativeButton("No", null)
                .show();
    }

    private void removeVideoFromDatabase(String table, String videoKey) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        databaseReference.child(table).child(userId).child(videoKey).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String message = table.equals("downloads") ? "Video removed from downloads" : "Video removed from watchlist";
                Toast.makeText(VideoPlayerActivity.this, message, Toast.LENGTH_SHORT).show();
            } else {
                String message = table.equals("downloads") ? "Failed to remove video from downloads" : "Failed to remove video from watchlist";
                Toast.makeText(VideoPlayerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveVideoToDatabase(String table) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String videoKey = databaseReference.child(table).child(userId).push().getKey();
        VideoItem videoItem = new VideoItem(videoKey, videoTitle, videoThumbnail, videoUrl, false);

        databaseReference.child(table).child(userId).child(videoKey).setValue(videoItem).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String message = table.equals("downloads") ? "Video downloaded" : "Video added to watchlist";
                Toast.makeText(VideoPlayerActivity.this, message, Toast.LENGTH_SHORT).show();
            } else {
                String message = table.equals("downloads") ? "Failed to download video" : "Failed to add video to watchlist";
                Toast.makeText(VideoPlayerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
