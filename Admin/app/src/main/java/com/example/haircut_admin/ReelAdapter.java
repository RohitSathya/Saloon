// ReelAdapter.java
package com.example.haircut_admin;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ReelAdapter extends RecyclerView.Adapter<ReelAdapter.ReelViewHolder> {
    private Context mContext;
    private List<Reel> mReels;

    public ReelAdapter(Context context, List<Reel> reels) {
        mContext = context;
        mReels = reels;
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.reel_item, parent, false);
        return new ReelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        Reel currentReel = mReels.get(position);
        holder.textViewVideoUrl.setText(currentReel.videoUrl);
        Glide.with(mContext)
                .load(currentReel.imageUrl)
                .into(holder.imageViewThumbnail);

        holder.imageViewThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(currentReel.videoUrl), "video/*");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReels.size();
    }

    public class ReelViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewThumbnail;
        public TextView textViewVideoUrl;

        public ReelViewHolder(View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.image_view_thumbnail);
            textViewVideoUrl = itemView.findViewById(R.id.text_view_video_url);
        }
    }
}
