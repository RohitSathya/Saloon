package com.example.va;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.List;

public class HomeVideoAdapter extends RecyclerView.Adapter<HomeVideoAdapter.VideoViewHolder> {

    public interface OnVideoClickListener {
        void onVideoClick(VideoItem videoItem);
    }

    private final Context context;
    private final List<VideoItem> videoList;
    private final OnVideoClickListener listener;
    private final boolean isPaidSection;

    public HomeVideoAdapter(Context context, List<VideoItem> videoList, boolean isPaidSection, OnVideoClickListener listener) {
        this.context = context;
        this.videoList = videoList;
        this.isPaidSection = isPaidSection;
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
        Picasso.get().load(video.getThumbnailUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                if (isPaidSection && video.isLocked()) {
                    bitmap = blurBitmap(bitmap);
                }
                holder.thumbnail.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                holder.thumbnail.setImageResource(R.drawable.error_placeholder);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                holder.thumbnail.setImageDrawable(placeHolderDrawable);
            }
        });

        if (isPaidSection && video.isLocked()) {
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

    private Bitmap blurBitmap(Bitmap bitmap) {
        RenderScript rs = RenderScript.create(context);
        Bitmap blurredBitmap = Bitmap.createBitmap(bitmap);

        Allocation input = Allocation.createFromBitmap(rs, bitmap);
        Allocation output = Allocation.createFromBitmap(rs, blurredBitmap);

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(25f); // Set the blur radius (0 < radius <= 25)
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurredBitmap);

        return blurredBitmap;
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
