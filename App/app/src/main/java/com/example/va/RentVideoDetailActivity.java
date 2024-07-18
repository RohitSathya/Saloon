package com.example.va;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.concurrent.Executors;

public class RentVideoDetailActivity extends AppCompatActivity {

    private ImageView imageViewThumbnail;
    private TextView textViewVideoTitle, t1, t2, t3, t4, t5;
    private Button buttonPlay;
    private ImageView fabDownload, fabWatchlist;
    private RentVideoListActivity.RentedVideoItem videoItem;
    private FirebaseUser currentUser;
    private DatabaseReference downloadRef, watchlistRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_video_detail);

        imageViewThumbnail = findViewById(R.id.imageViewThumbnail);
        textViewVideoTitle = findViewById(R.id.textViewVideoTitle);
        t1 = findViewById(R.id.textViewCategory);
        t2 = findViewById(R.id.textViewLanguage);
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);
        t5 = findViewById(R.id.t5);
        buttonPlay = findViewById(R.id.buttonPlay);
        fabDownload = findViewById(R.id.buttonDownload);
        fabWatchlist = findViewById(R.id.buttonWatchlist);

        Intent intent = getIntent();
        videoItem = (RentVideoListActivity.RentedVideoItem) intent.getSerializableExtra("video_item");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        downloadRef = FirebaseDatabase.getInstance().getReference("downloads").child(currentUser.getUid());
        watchlistRef = FirebaseDatabase.getInstance().getReference("watchlist").child(currentUser.getUid());

        if (videoItem != null) {
            textViewVideoTitle.setText(videoItem.getVideoName());
            t3.setText("Category: " + videoItem.getCategories());
            t5.setText("Language: " + videoItem.getlanguageType());
            t1.setText("VideoID: " + videoItem.getVideoId());
            t4.setText("Genre: " + videoItem.getGenre());
            t2.setText("VideoType: " + videoItem.getVideoType());
            Glide.with(this).load(videoItem.getThumbnailLink()).into(imageViewThumbnail);

            buttonPlay.setOnClickListener(v -> {
                Intent playIntent = new Intent(this, RentVideoPlayerActivity.class);
                playIntent.putExtra("video_link", videoItem.getVideoLink());
                playIntent.putExtra("video_id", videoItem.getVideoId());
                playIntent.putExtra("video_name", videoItem.getVideoName());
                playIntent.putExtra("th_link", videoItem.getThumbnailLink());
                startActivity(playIntent);
            });

            fabDownload.setOnClickListener(v -> handleDownload());
            fabWatchlist.setOnClickListener(v -> handleWatchlist());
        }
    }

    private void handleDownload() {
        downloadRef.child(videoItem.getVideoId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    showRemoveDialog("download", downloadRef, videoItem.getVideoId());
                } else {
                    downloadVideo(videoItem.getVideoLink(), videoItem.getVideoName());
                    downloadRef.child(videoItem.getVideoId()).setValue(new RentVideoPlayerActivity.VideoItem(
                            videoItem.getVideoId(), videoItem.getVideoName(), videoItem.getVideoLink(), videoItem.getThumbnailLink()));

                    Executors.newSingleThreadExecutor().execute(() -> {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        if (db.videoDao().getVideoById(videoItem.getVideoId()) == null) {
                            db.videoDao().insert(new ReelVideoItem(videoItem.getVideoId(), videoItem.getVideoLink(), videoItem.getVideoName(), videoItem.getThumbnailLink()));
                        }
                    });

                    Toast.makeText(RentVideoDetailActivity.this, "Added to downloads", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RentVideoDetailActivity.this, "Failed to check downloads", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadVideo(String videoUrl, String videoName) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(videoUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, videoName + ".mp4");

        downloadManager.enqueue(request);
    }

    private void handleWatchlist() {
        watchlistRef.child(videoItem.getVideoId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    showRemoveDialog("watchlist", watchlistRef, videoItem.getVideoId());
                } else {
                    watchlistRef.child(videoItem.getVideoId()).setValue(new RentVideoPlayerActivity.VideoItem(
                            videoItem.getVideoId(), videoItem.getVideoName(), videoItem.getVideoLink(), videoItem.getThumbnailLink()));
                    Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to check watchlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRemoveDialog(String type, DatabaseReference ref, String videoId) {
        new AlertDialog.Builder(this)
                .setTitle("Remove from " + type)
                .setMessage("Do you want to remove this video from your " + type + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    ref.child(videoId).removeValue();
                    Toast.makeText(this, "Removed from " + type, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
