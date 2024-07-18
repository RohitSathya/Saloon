package com.example.va;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WatchlistActivity extends BaseActivity {

    private static final String TAG = "WatchlistActivity";
    private RecyclerView recyclerViewWatchlist;
    private DatabaseReference watchlistRef;
    private List<ReelVideoItem> watchlist;
    private WatchlistAdapter adapter;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        recyclerViewWatchlist = findViewById(R.id.recyclerViewWatchlist);
        recyclerViewWatchlist.setLayoutManager(new LinearLayoutManager(this));
        watchlist = new ArrayList<>();
        adapter = new WatchlistAdapter(this, watchlist);
        recyclerViewWatchlist.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        watchlistRef = FirebaseDatabase.getInstance().getReference("watchlist").child(currentUser.getUid());

        fetchWatchlist();
    }

    private void fetchWatchlist() {
        watchlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                watchlist.clear();
                for (DataSnapshot watchlistSnapshot : snapshot.getChildren()) {
                    ReelVideoItem video = watchlistSnapshot.getValue(ReelVideoItem.class);
                    if (video != null) {
                        watchlist.add(video);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WatchlistActivity.this, "Failed to load watchlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder> {

        private Context context;
        private List<ReelVideoItem> watchlist;

        public WatchlistAdapter(Context context, List<ReelVideoItem> watchlist) {
            this.context = context;
            this.watchlist = watchlist;
        }

        @NonNull
        @Override
        public WatchlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
            return new WatchlistViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WatchlistViewHolder holder, int position) {
            ReelVideoItem video = watchlist.get(position);
            holder.textViewVideoName.setText(video.getVideoName());
            Glide.with(context).load(video.getThumbnailLink()).into(holder.imageViewThumbnail);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(WatchlistActivity.this, WDVideoPlayerActivity.class);
                intent.putExtra(FreeVideoPlayerActivity.VIDEO_URL_EXTRA, video.getVideoLink());
                startActivity(intent);
            });

            holder.buttonRemove.setOnClickListener(v -> {
                removeVideoFromWatchlist(video.getVideoId());
            });
        }

        @Override
        public int getItemCount() {
            return watchlist.size();
        }

        private void removeVideoFromWatchlist(String videoId) {
            watchlistRef.child(videoId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Removed from watchlist", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to remove from watchlist", Toast.LENGTH_SHORT).show();
                }
            });
        }

        class WatchlistViewHolder extends RecyclerView.ViewHolder {
            TextView textViewVideoName;
            ImageView imageViewThumbnail;
            Button buttonRemove;

            public WatchlistViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewVideoName = itemView.findViewById(R.id.video_title);
                imageViewThumbnail = itemView.findViewById(R.id.lock_icon);
                buttonRemove = itemView.findViewById(R.id.buttonRemove);
            }
        }
    }

    public static class ReelVideoItem {
        private String videoId;
        private String videoLink;
        private String videoName;
        private String thumbnailLink;

        public ReelVideoItem() {
            // Default constructor required for calls to DataSnapshot.getValue(ReelVideoItem.class)
        }

        public ReelVideoItem(String videoId, String videoLink, String videoName, String thumbnailLink) {
            this.videoId = videoId;
            this.videoLink = videoLink;
            this.videoName = videoName;
            this.thumbnailLink = thumbnailLink;
        }

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getVideoLink() {
            return videoLink;
        }

        public void setVideoLink(String videoLink) {
            this.videoLink = videoLink;
        }

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public String getThumbnailLink() {
            return thumbnailLink;
        }

        public void setThumbnailLink(String thumbnailLink) {
            this.thumbnailLink = thumbnailLink;
        }
    }
}
