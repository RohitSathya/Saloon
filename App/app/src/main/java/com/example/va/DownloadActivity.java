package com.example.va;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    private static final String TAG = "DownloadActivity";
    private RecyclerView recyclerViewDownloads;
    private DatabaseReference downloadRef;
    private List<ReelVideoItem> downloads;
    private DownloadAdapter adapter;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        recyclerViewDownloads = findViewById(R.id.recycler_view_downloads);
        recyclerViewDownloads.setLayoutManager(new LinearLayoutManager(this));
        downloads = new ArrayList<>();
        adapter = new DownloadAdapter(this, downloads);
        recyclerViewDownloads.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        downloadRef = FirebaseDatabase.getInstance().getReference("downloads").child(currentUser.getUid());

        fetchDownloads();
    }

    private void fetchDownloads() {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<ReelVideoItem> videoList = db.videoDao().getAllVideos();
            runOnUiThread(() -> {
                downloads.clear();
                downloads.addAll(videoList);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

        private Context context;
        private List<ReelVideoItem> downloads;

        public DownloadAdapter(Context context, List<ReelVideoItem> downloads) {
            this.context = context;
            this.downloads = downloads;
        }

        @NonNull
        @Override
        public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
            return new DownloadViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
            ReelVideoItem video = downloads.get(position);
            holder.textViewVideoName.setText(video.getVideoName());
            Glide.with(context).load(video.getThumbnailLink()).into(holder.imageViewThumbnail);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(DownloadActivity.this, WDVideoPlayerActivity.class);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), video.getVideoName() + ".mp4");
                Uri fileUri = FileProvider.getUriForFile(context, "com.example.va.fileprovider", file);
                intent.putExtra(WDVideoPlayerActivity.VIDEO_URL_EXTRA, fileUri.toString());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            });

            holder.buttonRemove.setOnClickListener(v -> {
                removeVideoFromDownloads(video.getVideoId());
            });
        }

        @Override
        public int getItemCount() {
            return downloads.size();
        }

        private void removeVideoFromDownloads(String videoId) {
            downloadRef.child(videoId).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    new Thread(() -> {
                        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                        db.videoDao().deleteVideo(videoId);
                        runOnUiThread(() -> {
                            Toast.makeText(context, "Removed from downloads", Toast.LENGTH_SHORT).show();
                            fetchDownloads();
                        });
                    }).start();
                } else {
                    Toast.makeText(context, "Failed to remove from downloads", Toast.LENGTH_SHORT).show();
                }
            });
        }

        class DownloadViewHolder extends RecyclerView.ViewHolder {
            TextView textViewVideoName;
            ImageView imageViewThumbnail;
            Button buttonRemove;

            public DownloadViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewVideoName = itemView.findViewById(R.id.video_title);
                imageViewThumbnail = itemView.findViewById(R.id.lock_icon);
                buttonRemove = itemView.findViewById(R.id.buttonRemove);
            }
        }
    }
}
