package com.example.va;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VideoListActivity extends BaseActivity {

    private static final String TAG = "VideoListActivity";
    private RecyclerView recyclerViewVideos;
    private DatabaseReference databaseReference;
    private List<Video> videoList;
    private List<Video> filteredVideoList;
    private VideoAdapter adapter;
    private String channelName, category;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        channelName = getIntent().getStringExtra("channelName");
        category = getIntent().getStringExtra("category");
        searchView = findViewById(R.id.searchView);

        recyclerViewVideos = findViewById(R.id.recyclerViewVideos);
        recyclerViewVideos.setLayoutManager(new GridLayoutManager(this, 1));
        videoList = new ArrayList<>();
        filteredVideoList = new ArrayList<>();
        adapter = new VideoAdapter(this, filteredVideoList);
        recyclerViewVideos.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("membership");

        fetchVideos();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void fetchVideos() {
        databaseReference.orderByChild("Free").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoList.clear();
                Set<String> seriesNames = new HashSet<>();
                for (DataSnapshot videoSnapshot : snapshot.getChildren()) {
                    String videoType = videoSnapshot.child("videoType").getValue(String.class);
                    String contentType = videoSnapshot.child("contentType").getValue(String.class);
                    if ("Free".equals(videoType)) {
                        String videoName = videoSnapshot.child("videoName").getValue(String.class);
                        String thumbnailLink = videoSnapshot.child("thumbnailLink").getValue(String.class);
                        String videoLink = videoSnapshot.child("videoLink").getValue(String.class);
                        String videoCategory = videoSnapshot.child("categories").getValue(String.class);
                        String genre = videoSnapshot.child("genre").getValue(String.class);
                        String episodeNumber = videoSnapshot.child("episodeNumber").getValue(String.class);
                        String seriesThumbnailLink = videoSnapshot.child("seriesThumbnailLink").getValue(String.class);
                        String videoId = videoSnapshot.child("videoId").getValue(String.class);
                        String channelName = videoSnapshot.child("channelName").getValue(String.class);
                        String type = videoSnapshot.child("videoType").getValue(String.class);
                        String contentTypes = videoSnapshot.child("contentType").getValue(String.class);
                        if ("Series".equals(contentType) && seriesNames.add(videoName)) {
                            videoList.add(new Video(videoName, thumbnailLink, videoLink, videoCategory, genre, contentType, episodeNumber, true, seriesThumbnailLink, videoId, channelName, type, contentTypes));
                        } else if (!"Series".equals(contentType)) {
                            videoList.add(new Video(videoName, thumbnailLink, videoLink, videoCategory, genre, contentType, episodeNumber, false, "", videoId, channelName, type, contentTypes));
                        }
                    }
                }
                filteredVideoList.clear();
                filteredVideoList.addAll(videoList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching videos: " + error.getMessage());
            }
        });
    }

    private void filter(String text) {
        filteredVideoList.clear();
        if (text.isEmpty()) {
            filteredVideoList.addAll(videoList);
        } else {
            for (Video video : videoList) {
                if (video.getVideoName().toLowerCase().contains(text.toLowerCase())) {
                    filteredVideoList.add(video);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private static class Video {
        private String videoName;
        private String thumbnailLink;
        private String videoLink;
        private String categories;
        private String genre;
        private String contentType;
        private String episodeNumber;
        private boolean isSeries;
        private String seriesThumbnailLink;
        private String videoId;
        private String channelName;
        private String type;
        private String contentTypes;

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public void setThumbnailLink(String thumbnailLink) {
            this.thumbnailLink = thumbnailLink;
        }

        public void setVideoLink(String videoLink) {
            this.videoLink = videoLink;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public void setEpisodeNumber(String episodeNumber) {
            this.episodeNumber = episodeNumber;
        }

        public void setSeries(boolean series) {
            isSeries = series;
        }

        public void setSeriesThumbnailLink(String seriesThumbnailLink) {
            this.seriesThumbnailLink = seriesThumbnailLink;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setvideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentTypes() {
            return contentTypes;
        }

        public void setContentTypes(String contentTypes) {
            this.contentTypes = contentTypes;
        }

        public Video(String videoName, String thumbnailLink, String videoLink, String videoCategory, String genre, String contentType, String episodeNumber, boolean isSeries, String seriesThumbnailLink, String videoId, String channelName, String type, String contentTypes) {
            this.videoName = videoName;
            this.thumbnailLink = thumbnailLink;
            this.videoLink = videoLink;
            this.categories = videoCategory;
            this.genre = genre;
            this.contentType = contentType;
            this.episodeNumber = episodeNumber;
            this.isSeries = isSeries;
            this.seriesThumbnailLink = seriesThumbnailLink;
            this.videoId = videoId;
            this.channelName = channelName;
            this.type = type;
            this.contentTypes = contentTypes;
        }

        public String getCategories() {
            return categories;
        }

        public String getGenre() {
            return genre;
        }

        public String getVideoName() {
            return videoName;
        }

        public String getThumbnailLink() {
            return thumbnailLink;
        }

        public String getVideoLink() {
            return videoLink;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getContentType() {
            return contentType;
        }

        public String getEpisodeNumber() {
            return episodeNumber;
        }

        public boolean isSeries() {
            return isSeries;
        }

        public String getSeriesThumbnailLink() {
            return seriesThumbnailLink;
        }
    }

    private class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_VIDEO = 0;
        private static final int TYPE_SERIES = 1;

        private Context context;
        private List<Video> videos;

        public VideoAdapter(Context context, List<Video> videos) {
            this.context = context;
            this.videos = videos;
        }

        @Override
        public int getItemViewType(int position) {
            Video video = videos.get(position);
            return video.isSeries() ? TYPE_SERIES : TYPE_VIDEO;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_VIDEO) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_videoss, parent, false);
                return new VideoViewHolder(view);
            } else {
                View view = LayoutInflater.from(context).inflate(R.layout.item_series, parent, false);
                return new SeriesViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Video video = videos.get(position);
            if (holder instanceof VideoViewHolder) {
                VideoViewHolder videoViewHolder = (VideoViewHolder) holder;
                videoViewHolder.textViewVideoName.setText(video.getVideoName());
                Glide.with(context).load(video.getThumbnailLink()).into(videoViewHolder.imageViewThumbnail);

                videoViewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(VideoListActivity.this, FreeVideoDetailsActivity.class);
                    intent.putExtra("video_name", video.getVideoName());
                    intent.putExtra("video_url", video.getVideoLink());
                    intent.putExtra("th", video.getThumbnailLink());
                    intent.putExtra("cat", video.getCategories());
                    intent.putExtra("genre", video.getGenre());
                    intent.putExtra("video_id", video.getVideoId());
                    intent.putExtra("channelName", video.getChannelName());
                    intent.putExtra("type", video.getType());
                    intent.putExtra("ct", video.getContentTypes());
                    startActivity(intent);
                });
            } else {
                SeriesViewHolder seriesViewHolder = (SeriesViewHolder) holder;
                seriesViewHolder.textViewSeriesName.setText(video.getVideoName());
                Glide.with(context).load(video.getSeriesThumbnailLink()).into(seriesViewHolder.imageViewSeriesThumbnail);

                seriesViewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(VideoListActivity.this, SeriesActivity.class);
                    intent.putExtra("seriesName", video.getVideoName());
                    intent.putExtra("hh", video.getVideoId());
                    startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return videos.size();
        }

        class VideoViewHolder extends RecyclerView.ViewHolder {
            TextView textViewVideoName;
            ImageView imageViewThumbnail;

            public VideoViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewVideoName = itemView.findViewById(R.id.textViewTitle);
                imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            }
        }

        class SeriesViewHolder extends RecyclerView.ViewHolder {
            TextView textViewSeriesName;
            ImageView imageViewSeriesThumbnail;

            public SeriesViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewSeriesName = itemView.findViewById(R.id.textViewSeriesName);
                imageViewSeriesThumbnail = itemView.findViewById(R.id.imageViewSeriesThumbnail);
            }
        }
    }
}
