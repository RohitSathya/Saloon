package com.example.va;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private final Context context;
    private final List<VideoItem> videoList;

    public StoreAdapter(Context context, List<VideoItem> videoList) {
        this.context = context;
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store_video, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        VideoItem video = videoList.get(position);
        holder.title.setText(video.getTitle());
        Picasso.get().load(video.getThumbnailUrl()).into(holder.thumbnail);

        if (video.isLocked()) {
            holder.lockIcon.setVisibility(View.VISIBLE);
            holder.thumbnail.setOnClickListener(v -> {
                // Show payment dialog
            });
        } else {
            holder.lockIcon.setVisibility(View.GONE);
            holder.thumbnail.setOnClickListener(v -> {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("video_url", video.getVideoUrl());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail, lockIcon;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
            lockIcon = itemView.findViewById(R.id.lock_icon);
        }
    }
}
