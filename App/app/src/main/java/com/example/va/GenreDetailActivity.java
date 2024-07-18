package com.example.va;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.va.VideoAdapter;
import com.example.va.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class GenreDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<VideoItem> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre_detail);

        String genreName = getIntent().getStringExtra("genre_name");
        setTitle(genreName);

        recyclerView = findViewById(R.id.recycler_view_genre_detail);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(this, videoList);
        recyclerView.setAdapter(videoAdapter);

        loadGenreVideos(genreName);
    }

    private void loadGenreVideos(String genreName) {
        // Dummy data
        videoList.add(new VideoItem("1", "Video 1", "https://example.com/video1.jpg", "https://example.com/video1.mp4"));
        videoList.add(new VideoItem("2", "Video 2", "https://example.com/video2.jpg", "https://example.com/video2.mp4"));
        videoList.add(new VideoItem("3", "Video 3", "https://example.com/video3.jpg", "https://example.com/video3.mp4"));
        videoAdapter.notifyDataSetChanged();
    }
}
