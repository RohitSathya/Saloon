package com.example.va;

import android.annotation.SuppressLint;
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

public class FreeVideoDetailsActivity extends AppCompatActivity {

    private ImageView imageViewThumbnail;
    private TextView textViewVideoTitle, t1, t2, t3, t4, t5, t6, textViewEpisodeNumber;
    private Button buttonPlay;
    private ImageButton buttonDownload, buttonWatchlist;
    private String videoUrl, thumbnailLink, videoCategory, genre, videoid, cn, type, ct, eno;
    private FirebaseUser currentUser;
    private DatabaseReference downloadRef, watchlistRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_video_details);

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
            Intent playIntent = new Intent(FreeVideoDetailsActivity.this, FreeVideoPlayerActivity.class);
            playIntent.putExtra("video_url", videoUrl);
            startActivity(playIntent);
        });


    }







}
