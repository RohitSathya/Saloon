package com.example.va;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class gd extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VideoView videoView = findViewById(R.id.videoView);

        // URL of the video on Google Drive
        String videoUrl = "https://drive.google.com/uc?export=download&id=1ikL-JkL4dKtFatd33Y7ynQi6ZY8qPCpm";

        // Set the video URI and start playing
        Uri uri = Uri.parse(videoUrl);
        videoView.setVideoURI(uri);

        // Set MediaController to enable play, pause, forward, etc options.
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Start the video
        videoView.start();
    }
}
