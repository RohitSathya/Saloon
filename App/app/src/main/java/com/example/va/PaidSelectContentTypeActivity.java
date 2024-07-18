package com.example.va;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class PaidSelectContentTypeActivity extends AppCompatActivity {

    private String channelName, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_content_type);

        channelName = getIntent().getStringExtra("channelName");
        category = getIntent().getStringExtra("category");

        Button imageViewVideo = findViewById(R.id.imageViewVideo);
        Button imageViewSeries = findViewById(R.id.imageViewSeries);

        imageViewVideo.setOnClickListener(v -> openVideoListActivity("Video"));
        imageViewSeries.setOnClickListener(v -> openVideoListActivity("Series"));
    }

    private void openVideoListActivity(String contentType) {
        Intent intent = new Intent(PaidSelectContentTypeActivity.this, PaidVideoListActivity.class);
        intent.putExtra("channelName", channelName);
        intent.putExtra("category", category);
        intent.putExtra("contentType", contentType);
        startActivity(intent);
    }
}
