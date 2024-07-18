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

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryImageViewHolder> {
    private List<GalleryImageItem> galleryImageList;
    private Context context;

    public GalleryAdapter(Context context, List<GalleryImageItem> galleryImageList) {
        this.context = context;
        this.galleryImageList = galleryImageList;
    }

    @NonNull
    @Override
    public GalleryImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_gallery_image, parent, false);
        return new GalleryImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryImageViewHolder holder, int position) {
        GalleryImageItem galleryImageItem = galleryImageList.get(position);
        holder.title.setText(galleryImageItem.getTitle());
        Picasso.get().load(galleryImageItem.getImageUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return galleryImageList.size();
    }

    public static class GalleryImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;

        public GalleryImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            title = itemView.findViewById(R.id.title);
        }
    }
}
