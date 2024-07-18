package com.example.va;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class FullScreenMediaActivity extends AppCompatActivity {

    private ImageView imageView;
    private VideoView videoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_media);

        imageView = findViewById(R.id.image_view_full_screen);
        videoView = findViewById(R.id.video_view_full_screen);

        Intent intent = getIntent();
        String mediaUrl = intent.getStringExtra("mediaUrl");
        String mediaType = intent.getStringExtra("mediaType");

        if (mediaType != null && mediaType.equals("image")) {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            Glide.with(this).load(mediaUrl).into(imageView);
        } else if (mediaType != null && mediaType.equals("video")) {
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(Uri.parse(mediaUrl));
            videoView.start();
        }
    }
}
