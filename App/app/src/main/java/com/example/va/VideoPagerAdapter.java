//package com.example.va;
//
//import android.content.Context;
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.MediaController;
//import android.widget.VideoView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class VideoPagerAdapter extends RecyclerView.Adapter<VideoPagerAdapter.VideoViewHolder> {
//
//    private List<ReelVideoItem> videoList;
//    private Context context;
//
//    public VideoPagerAdapter(Context context, List<ReelVideoItem> videoList) {
//        this.context = context;
//        this.videoList = videoList;
//    }
//
//    @NonNull
//    @Override
//    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_video_pager, parent, false);
//        return new VideoViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
//        ReelVideoItem videoItem = videoList.get(position);
//        Uri videoUri = Uri.parse(videoItem.getVideoUrl());
//        holder.videoView.setVideoURI(videoUri);
//        MediaController mediaController = new MediaController(context);
//        mediaController.setAnchorView(holder.videoView);
//        holder.videoView.setMediaController(mediaController);
//        holder.videoView.requestFocus();
//        holder.videoView.start();
//    }
//
//    @Override
//    public int getItemCount() {
//        return videoList.size();
//    }
//
//    public static class VideoViewHolder extends RecyclerView.ViewHolder {
//        VideoView videoView;
//
//        public VideoViewHolder(@NonNull View itemView) {
//            super(itemView);
//            videoView = itemView.findViewById(R.id.videoView);
//        }
//    }
//}
