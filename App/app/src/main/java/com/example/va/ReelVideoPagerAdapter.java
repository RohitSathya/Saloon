//package com.example.va;
//
//import android.content.Context;
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.MediaController;
//import android.widget.VideoView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import java.util.List;
//
//public class ReelVideoPagerAdapter extends RecyclerView.Adapter<ReelVideoPagerAdapter.VideoViewHolder> {
//
//    private List<ReelVideoItem> reelVideoList;
//    private Context context;
//
//    public ReelVideoPagerAdapter(Context context, List<ReelVideoItem> reelVideoList) {
//        this.context = context;
//        this.reelVideoList = reelVideoList;
//    }
//
//    @NonNull
//    @Override
//    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_video_page, parent, false);
//        return new VideoViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
//        ReelVideoItem reelVideoItem = reelVideoList.get(position);
//        Uri videoUri = Uri.parse(reelVideoItem.getVideoUrl());
//
//        holder.videoView.setVideoURI(videoUri);
//        holder.videoView.setOnPreparedListener(mp -> {
//            mp.setLooping(true);
//        });
//
//        MediaController mediaController = new MediaController(context);
//        mediaController.setAnchorView(holder.videoView);
//        holder.videoView.setMediaController(mediaController);
//    }
//
//    @Override
//    public int getItemCount() {
//        return reelVideoList.size();
//    }
//
//    static class VideoViewHolder extends RecyclerView.ViewHolder {
//        VideoView videoView;
//
//        public VideoViewHolder(@NonNull View itemView) {
//            super(itemView);
//            videoView = itemView.findViewById(R.id.videoView);
//        }
//    }
//}
