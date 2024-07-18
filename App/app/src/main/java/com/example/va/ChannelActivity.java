package com.example.va;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChannelActivity extends AppCompatActivity {

    private static final String TAG = "ChannelActivity";
    private RecyclerView recyclerViewChannels;
    private DatabaseReference databaseReference;
    private ArrayList<Channel> channelList;
    private ChannelAdapter channelAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);

        recyclerViewChannels = findViewById(R.id.recyclerViewChannels);
        recyclerViewChannels.setLayoutManager(new LinearLayoutManager(this));
        channelList = new ArrayList<>();
        channelAdapter = new ChannelAdapter(channelList);
        recyclerViewChannels.setAdapter(channelAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("membership");

        fetchChannels();
    }

    private void fetchChannels() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                channelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String channelName = dataSnapshot.child("channelName").getValue(String.class);
                    String channelLogo = dataSnapshot.child("channelLogoLink").getValue(String.class);
                    if (channelName != null && channelLogo != null) {
                        channelList.add(new Channel(channelName, channelLogo));
                    }
                }
                channelAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching channels: " + error.getMessage());
            }
        });
    }

    private class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {

        private ArrayList<Channel> channelList;

        public ChannelAdapter(ArrayList<Channel> channelList) {
            this.channelList = channelList;
        }

        @NonNull
        @Override
        public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_channel, parent, false);
            return new ChannelViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
            Channel channel = channelList.get(position);
            holder.textViewChannelName.setText(channel.getChannelName());
            Glide.with(ChannelActivity.this).load(channel.getChannelLogo()).into(holder.imageViewChannelLogo);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ChannelActivity.this, CategoryActivity.class);
                intent.putExtra("channelName", channel.getChannelName());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return channelList.size();
        }

        public class ChannelViewHolder extends RecyclerView.ViewHolder {

            public TextView textViewChannelName;
            public ImageView imageViewChannelLogo;

            public ChannelViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewChannelName = itemView.findViewById(R.id.textViewChannelName);
                imageViewChannelLogo = itemView.findViewById(R.id.imageViewChannelLogo);
            }
        }
    }

    private class Channel {
        private String channelName;
        private String channelLogo;

        public Channel(String channelName, String channelLogo) {
            this.channelName = channelName;
            this.channelLogo = channelLogo;
        }

        public String getChannelName() {
            return channelName;
        }

        public String getChannelLogo() {
            return channelLogo;
        }
    }
}
