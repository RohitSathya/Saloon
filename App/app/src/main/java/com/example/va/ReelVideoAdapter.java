//package com.example.va;
//
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//public class ReelVideoAdapter extends RecyclerView.Adapter<ReelVideoAdapter.ReelVideoViewHolder> {
//    private List<ReelVideoItem> reelVideoList;
//    private Context context;
//
//    public ReelVideoAdapter(Context context, List<ReelVideoItem> reelVideoList) {
//        this.context = context;
//        this.reelVideoList = reelVideoList;
//    }
//
//    @NonNull
//    @Override
//    public ReelVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_reel_video, parent, false);
//        return new ReelVideoViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ReelVideoViewHolder holder, int position) {
//        ReelVideoItem reelVideoItem = reelVideoList.get(position);
//        holder.title.setText(reelVideoItem.getTitle());
//        Picasso.get().load(reelVideoItem.getThumbnailUrl()).into(holder.thumbnailImageView);
//        holder.thumbnailImageView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ReelVideoPlayerActivity.class);
//            intent.putExtra("videoUrl", reelVideoItem.getVideoUrl());
//            context.startActivity(intent);
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return reelVideoList.size();
//    }
//
//    public static class ReelVideoViewHolder extends RecyclerView.ViewHolder {
//        ImageView thumbnailImageView;
//        TextView title;
//
//        public ReelVideoViewHolder(@NonNull View itemView) {
//            super(itemView);
//            thumbnailImageView = itemView.findViewById(R.id.thumbnail_image);
//            title = itemView.findViewById(R.id.video_title);
//        }
//    }
//}