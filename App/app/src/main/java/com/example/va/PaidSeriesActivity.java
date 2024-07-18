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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PaidSeriesActivity extends BaseActivity {

    private static final String TAG = "SeriesActivity";
    private RecyclerView recyclerViewEpisodes;
    private DatabaseReference databaseReference;
    private List<Video> episodeList;
    private EpisodeAdapter adapter;
    private String seriesName;
    String videoId;
    String channelName;
    String type;
    String contentTypes;
    String episodeno;
    String videoName;
    String cnn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_series);

        seriesName = getIntent().getStringExtra("seriesName");


        recyclerViewEpisodes = findViewById(R.id.recyclerViewEpisodes);
        recyclerViewEpisodes.setLayoutManager(new GridLayoutManager(this, 2));
        episodeList = new ArrayList<>();
        adapter = new EpisodeAdapter(this, episodeList);
        recyclerViewEpisodes.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("membership");

        fetchEpisodes();
    }

    private void fetchEpisodes() {
        databaseReference.orderByChild("videoName").equalTo(seriesName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                episodeList.clear();
                for (DataSnapshot videoSnapshot : snapshot.getChildren()) {
                    String videoType = videoSnapshot.child("videoType").getValue(String.class);
                    String cn=videoSnapshot.child("channelName").getValue(String.class);
                    if ("Paid".equals(videoType)) {
                        videoName = videoSnapshot.child("videoName").getValue(String.class);
                        String thumbnailLink = videoSnapshot.child("thumbnailLink").getValue(String.class);
                        String videoLink = videoSnapshot.child("videoLink").getValue(String.class);
                        String videoCategory = videoSnapshot.child("categories").getValue(String.class);
                        String genre = videoSnapshot.child("genre").getValue(String.class);
                        episodeno = videoSnapshot.child("episodeNumber").getValue(String.class);
                        videoId=videoSnapshot.child("videoId").getValue(String.class);
                        channelName=videoSnapshot.child("channelName").getValue(String.class);
                        type=videoSnapshot.child("videoType").getValue(String.class);
                        contentTypes=videoSnapshot.child("contentType").getValue(String.class);
                        if (videoName != null && thumbnailLink != null) {
                            episodeList.add(new Video(videoName, thumbnailLink, videoLink, videoCategory, genre, "Series", episodeno, false,videoId,channelName));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching episodes: " + error.getMessage());
            }
        });
    }

    private static class Video {
        private String videoName;
        private String thumbnailLink;
        private String videoLink;
        private String categories;
        private String genre;
        private String contentType;
        private String episodeNumber;
        String videoId;
        String channelName;



        public Video(String videoName, String thumbnailLink, String videoLink, String videoCategory, String genre, String contentType, String episodeNumber, boolean isSeries, String videoId, String channelName) {
            this.videoName = videoName;
            this.thumbnailLink = thumbnailLink;
            this.videoLink = videoLink;
            this.categories = videoCategory;
            this.genre = genre;
            this.contentType = contentType;
            this.episodeNumber = episodeNumber;
            this.videoId=videoId;
            this.channelName=channelName;
        }

        public String getchannelName() {
            return channelName;
        }

        public void setchannelName(String channelName) {
            this.channelName = channelName;
        }
        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
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

        public String getContentType() {
            return contentType;
        }

        public String getEpisodeNumber() {
            return episodeNumber;
        }
    }

    private class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

        private Context context;
        private List<Video> episodes;

        public EpisodeAdapter(Context context, List<Video> episodes) {
            this.context = context;
            this.episodes = episodes;
        }

        @NonNull
        @Override
        public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_videoss, parent, false);
            return new EpisodeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
            Video episode = episodes.get(position);
            holder.textViewVideoName.setText("Episode " + episode.getEpisodeNumber());
            Glide.with(context).load(episode.getThumbnailLink()).into(holder.imageViewThumbnail);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(PaidSeriesActivity.this, VideoDetailsActivity.class);
                intent.putExtra("video_url", episode.getVideoLink());
                intent.putExtra("th", episode.getThumbnailLink());
                intent.putExtra("cat", episode.getCategories());
                intent.putExtra("genre", episode.getGenre());
                intent.putExtra("ep", episode.getEpisodeNumber());
                intent.putExtra("video_name", videoName);
                intent.putExtra("video_id", episode.getVideoId());
                intent.putExtra("channelName", episode.getchannelName());
                intent.putExtra("type", type);
                intent.putExtra("ct", contentTypes);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return episodes.size();
        }

        class EpisodeViewHolder extends RecyclerView.ViewHolder {
            TextView textViewVideoName;
            ImageView imageViewThumbnail;

            public EpisodeViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewVideoName = itemView.findViewById(R.id.textViewTitle);
                imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
            }
        }
    }
}
