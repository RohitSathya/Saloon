package com.example.va;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class StoreVideoAdapter extends RecyclerView.Adapter<StoreVideoAdapter.VideoViewHolder> {

    public interface OnVideoClickListener {
        void onVideoClick(VideoItem videoItem);
    }

    private final Context context;
    private final List<VideoItem> videoList;
    private final OnVideoClickListener listener;

    public StoreVideoAdapter(Context context, List<VideoItem> videoList, OnVideoClickListener listener) {
        this.context = context;
        this.videoList = videoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoItem video = videoList.get(position);
        holder.title.setText(video.getTitle());
        Picasso.get().load(video.getThumbnailUrl()).into(holder.thumbnail);

        if (video.isLocked()) {
            holder.lockIcon.setVisibility(View.VISIBLE);
        } else {
            holder.lockIcon.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;
        ImageView lockIcon;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
            lockIcon = itemView.findViewById(R.id.lock_icon);
        }
    }
}
