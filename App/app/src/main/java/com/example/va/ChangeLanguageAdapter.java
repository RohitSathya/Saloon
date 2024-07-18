package com.example.va;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.va.R;
import com.example.va.VideoItem;

import java.util.List;

public class ChangeLanguageAdapter extends RecyclerView.Adapter<ChangeLanguageAdapter.LanguageViewHolder> {

    private final Context context;
    private final List<VideoItem> languageList;

    public ChangeLanguageAdapter(Context context, List<VideoItem> languageList) {
        this.context = context;
        this.languageList = languageList;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_language, parent, false);
        return new LanguageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        VideoItem language = languageList.get(position);
        holder.languageName.setText(language.getTitle());
        // Implement click listener if needed
    }

    @Override
    public int getItemCount() {
        return languageList.size();
    }

    public static class LanguageViewHolder extends RecyclerView.ViewHolder {
        TextView languageName;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);
            languageName = itemView.findViewById(R.id.language_name);
        }
    }
}
