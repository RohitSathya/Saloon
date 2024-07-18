package com.example.va;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PaidChannelListActivity extends BaseActivity {

    private static final String TAG = "ChannelListActivity";
    private RecyclerView recyclerViewChannels;
    private DatabaseReference databaseReference;
    private List<Channel> channelList;
    private ChannelAdapter adapter;
    private List<Channel> filteredChannelList;
    private SearchView searchView;
    private String ge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        Intent f=getIntent();
        ge=f.getStringExtra("ge");

        recyclerViewChannels = findViewById(R.id.recyclerViewChannels);
        recyclerViewChannels.setLayoutManager(new GridLayoutManager(this, 2));
        channelList = new ArrayList<>();
        filteredChannelList = new ArrayList<>();
        adapter = new ChannelAdapter(this, filteredChannelList);
        recyclerViewChannels.setAdapter(adapter);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterChannels(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterChannels(newText);
                return false;
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("membership");

        fetchChannels();
    }

    private void fetchChannels() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                channelList.clear();
                filteredChannelList.clear();
                Set<String> uniqueChannels = new HashSet<>();
                for (DataSnapshot videoSnapshot : snapshot.getChildren()) {
                    String videoType = videoSnapshot.child("videoType").getValue(String.class);
                    String ger=videoSnapshot.child("genre").getValue(String.class);
                    if ("Paid".equals(videoType)) {
                        String channelLogo = videoSnapshot.child("channelLogoLink").getValue(String.class);
                        String channelName = videoSnapshot.child("channelName").getValue(String.class);
                        String gen = videoSnapshot.child("genre").getValue(String.class);
                        if (channelName != null && channelLogo != null && uniqueChannels.add(channelName)) {
                            Channel channel = new Channel(channelName, channelLogo,gen);
                            channelList.add(channel);
                            filteredChannelList.add(channel);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching channels: " + error.getMessage());
            }
        });
    }

    private void filterChannels(String query) {
        filteredChannelList.clear();
        if (query.isEmpty()) {
            filteredChannelList.addAll(channelList);
        } else {
            for (Channel channel : channelList) {
                if (channel.getChannelName().toLowerCase().contains(query.toLowerCase())) {
                    filteredChannelList.add(channel);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private static class Channel {
        private String channelName;
        private String channelLogo;
        private String categoryName;
        private String genre;



        public Channel(String channelName, String channelLogo, String genre) {
            this.channelName = channelName;
            this.channelLogo = channelLogo;
            this.genre=genre;
        }
        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }
        public String getChannelName() {
            return channelName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getChannelLogo() {
            return channelLogo;
        }
    }

    private class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {

        private Context context;
        private List<Channel> channels;

        public ChannelAdapter(Context context, List<Channel> channels) {
            this.context = context;
            this.channels = channels;
        }

        @NonNull
        @Override
        public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
            return new ChannelViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
            Channel channel = channels.get(position);
            holder.textViewChannelName.setText(channel.getChannelName());
            Glide.with(context).load(channel.getChannelLogo()).into(holder.imageViewChannelLogo);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(PaidChannelListActivity.this, PaidVideoListActivity.class);
                intent.putExtra("channelName", channel.getChannelName());
                intent.putExtra("category", channel.getCategoryName());
                intent.putExtra("genre", ge);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return channels.size();
        }

        class ChannelViewHolder extends RecyclerView.ViewHolder {
            TextView textViewChannelName;
            ImageView imageViewChannelLogo;

            public ChannelViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewChannelName = itemView.findViewById(R.id.textViewChannelName);
                imageViewChannelLogo = itemView.findViewById(R.id.imageViewChannelLogo);
            }
        }
    }
}
