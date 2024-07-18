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

public class MembershipVideoAdapter extends RecyclerView.Adapter<MembershipVideoAdapter.VideoViewHolder> {

    public interface OnVideoClickListener {
        void onVideoClick(MembershipVideoItem videoItem);
    }

    private final Context context;
    private final List<MembershipVideoItem> videoList;
    private final OnVideoClickListener listener;
    private final boolean isPaidSection;

    public MembershipVideoAdapter(Context context, List<MembershipVideoItem> videoList, boolean isPaidSection, OnVideoClickListener listener) {
        this.context = context;
        this.videoList = videoList;
        this.isPaidSection = isPaidSection;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.membershipitem_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        MembershipVideoItem video = videoList.get(position);
        holder.title.setText(video.getVideoName());
        holder.channelOwnerName.setText(video.getChannelOwnerName());
        holder.categories.setText(video.getCategories());

        Picasso.get().load(video.getThumbnailLink()).into(holder.thumbnail);

        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView thumbnail;
        TextView channelOwnerName;
        TextView categories;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.video_title);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
            channelOwnerName = itemView.findViewById(R.id.channel_owner_name);
            categories = itemView.findViewById(R.id.categories);
        }
    }
}
