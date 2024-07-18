package com.example.va;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbnailViewHolder> {

    private final Context context;
    private final List<String> thumbnailUrls;

    public ThumbnailAdapter(Context context, List<String> thumbnailUrls) {
        this.context = context;
        this.thumbnailUrls = thumbnailUrls;
    }

    @NonNull
    @Override
    public ThumbnailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thumbnail, parent, false);
        return new ThumbnailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailViewHolder holder, int position) {
        String thumbnailUrl = thumbnailUrls.get(position);
        Picasso.get().load(thumbnailUrl).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return thumbnailUrls.size();
    }

    public static class ThumbnailViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        public ThumbnailViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail_image);
        }
    }
}
