package com.example.va;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class VideoDetailsActivity extends AppCompatActivity {

    private ImageView imageViewThumbnail;
    private TextView textViewVideoTitle, t1, t2, t3, t4, t5, t6, textViewEpisodeNumber;
    private Button buttonPlay;
    private ImageButton buttonDownload, buttonWatchlist;
    private String videoUrl, thumbnailLink, videoCategory, genre, videoid, cn, type, ct, eno;
    private FirebaseUser currentUser;
    private DatabaseReference downloadRef, watchlistRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);

        imageViewThumbnail = findViewById(R.id.imageViewThumbnail);
        textViewVideoTitle = findViewById(R.id.textViewVideoTitle);
        t1 = findViewById(R.id.t1);
        t2 = findViewById(R.id.t2);
        t3 = findViewById(R.id.t3);
        t4 = findViewById(R.id.t4);
        t5 = findViewById(R.id.t5);
        t6 = findViewById(R.id.t6);
        textViewEpisodeNumber = findViewById(R.id.textViewEpisodeNumber);
        buttonPlay = findViewById(R.id.buttonPlay);
        buttonDownload = findViewById(R.id.buttonDownload);
        buttonWatchlist = findViewById(R.id.buttonWatchlist);

        Intent intent = getIntent();
        String videoName = intent.getStringExtra("video_name");
        videoUrl = intent.getStringExtra("video_url");
        thumbnailLink = intent.getStringExtra("th");
        videoCategory = intent.getStringExtra("cat");
        genre = intent.getStringExtra("genre");
        eno = intent.getStringExtra("ep");
        videoid = intent.getStringExtra("video_id");
        cn = intent.getStringExtra("channelName");
        type = intent.getStringExtra("type");
        ct = intent.getStringExtra("ct");

        textViewVideoTitle.setText(videoName);
        t1.setText("VideoId: " + videoid);
        t2.setText("VideoType: " + type);
        t3.setText("ContentType: " + ct);
        t4.setText("ChannelName: " + cn);
        t5.setText("VideoCategory: " + videoCategory);
        t6.setText("HairStyle: " + genre);

        Glide.with(this).load(thumbnailLink).into(imageViewThumbnail);

        if ("Series".equals(ct)) {
            textViewEpisodeNumber.setText("Episode Number: " + eno);
            textViewEpisodeNumber.setVisibility(View.VISIBLE);
        } else {
            textViewEpisodeNumber.setVisibility(View.GONE);
        }

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        downloadRef = FirebaseDatabase.getInstance().getReference("downloads").child(currentUser.getUid());
        watchlistRef = FirebaseDatabase.getInstance().getReference("watchlist").child(currentUser.getUid());

        buttonPlay.setOnClickListener(v -> {
            Intent playIntent = new Intent(VideoDetailsActivity.this, FreeVideoPlayerActivity.class);
            playIntent.putExtra("video_url", videoUrl);
            startActivity(playIntent);

        });

        buttonDownload.setOnClickListener(v -> handleDownload());
        buttonWatchlist.setOnClickListener(v -> handleWatchlist());
    }

    private void handleDownload() {
        downloadRef.child(videoid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    showRemoveDialog("download", downloadRef, videoid);
                } else {
                    if ("Series".equals(ct)) {
                        downloadRef.child(videoid).setValue(new RentVideoPlayerActivity.VideoItem(videoid, textViewVideoTitle.getText().toString()+" "+textViewEpisodeNumber.getText().toString(), videoUrl, thumbnailLink));
                        Toast.makeText(VideoDetailsActivity.this, "Added to downloads", Toast.LENGTH_SHORT).show();
                    } else {
                        downloadRef.child(videoid).setValue(new RentVideoPlayerActivity.VideoItem(videoid, textViewVideoTitle.getText().toString(), videoUrl, thumbnailLink));
                        Toast.makeText(VideoDetailsActivity.this, "Added to downloads", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(VideoDetailsActivity.this, "Failed to check downloads", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleWatchlist() {
        watchlistRef.child(videoid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    showRemoveDialog("watchlist", watchlistRef, videoid);
                } else {
                    if ("Series".equals(ct)) {
                        watchlistRef.child(videoid).setValue(new RentVideoPlayerActivity.VideoItem(videoid, textViewVideoTitle.getText().toString()+" "+textViewEpisodeNumber.getText().toString(), videoUrl, thumbnailLink));
                        Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                    } else {
                        watchlistRef.child(videoid).setValue(new RentVideoPlayerActivity.VideoItem(videoid, textViewVideoTitle.getText().toString(), videoUrl, thumbnailLink));
                        Toast.makeText(getApplicationContext(), "Added to watchlist", Toast.LENGTH_SHORT).show();
                    }


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
