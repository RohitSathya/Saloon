package com.example.va;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.va.GenreDetailActivity;
import com.example.va.R;
import com.example.va.VideoItem;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private final Context context;
    private final List<VideoItem> genreList;

    public GenreAdapter(Context context, List<VideoItem> genreList) {
        this.context = context;
        this.genreList = genreList;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        VideoItem genre = genreList.get(position);
        holder.genreName.setText(genre.getTitle());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, GenreDetailActivity.class);
            intent.putExtra("genre_name", genre.getTitle());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        TextView genreName;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            genreName = itemView.findViewById(R.id.genre_name);
        }
    }
}
