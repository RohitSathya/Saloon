//package com.example.va;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//public class RentChannelListActivity extends BaseActivity {
//
//    private static final String TAG = "ChannelListActivity";
//    private RecyclerView recyclerViewChannels;
//    private DatabaseReference databaseReference;
//    private List<Channel> channelList;
//    private ChannelAdapter adapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_channel_list);
//
//        recyclerViewChannels = findViewById(R.id.recyclerViewChannels);
//        recyclerViewChannels.setLayoutManager(new GridLayoutManager(this, 2));
//        channelList = new ArrayList<>();
//        adapter = new ChannelAdapter(this, channelList);
//        recyclerViewChannels.setAdapter(adapter);
//
//        databaseReference = FirebaseDatabase.getInstance().getReference("membership");
//
//        fetchChannels();
//    }
//
//    private void fetchChannels() {
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                channelList.clear();
//                Set<String> uniqueChannels = new HashSet<>();
//                for (DataSnapshot videoSnapshot : snapshot.getChildren()) {
//                    String videoType = videoSnapshot.child("videoType").getValue(String.class);
//                    if ("Rent".equals(videoType)) {
//                        String channelLogo = videoSnapshot.child("channelLogoLink").getValue(String.class);
//                        String channelName = videoSnapshot.child("channelName").getValue(String.class);
//                        if (channelName != null && channelLogo != null && uniqueChannels.add(channelName)) {
//                            channelList.add(new Channel(channelName, channelLogo));
//                        }
//                    }
//                }
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "Error fetching channels: " + error.getMessage());
//            }
//        });
//    }
//
//    private static class Channel {
//        private String channelName;
//        private String channelLogo;
//
//        public Channel(String channelName, String channelLogo) {
//            this.channelName = channelName;
//            this.channelLogo = channelLogo;
//        }
//
//        public String getChannelName() {
//            return channelName;
//        }
//
//        public String getChannelLogo() {
//            return channelLogo;
//        }
//    }
//
//    private class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {
//
//        private Context context;
//        private List<Channel> channels;
//
//        public ChannelAdapter(Context context, List<Channel> channels) {
//            this.context = context;
//            this.channels = channels;
//        }
//
//        @NonNull
//        @Override
//        public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
//            return new ChannelViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ChannelViewHolder holder, int position) {
//            Channel channel = channels.get(position);
//            holder.textViewChannelName.setText(channel.getChannelName());
//            Glide.with(context).load(channel.getChannelLogo()).into(holder.imageViewChannelLogo);
//
//            holder.itemView.setOnClickListener(v -> {
//                Intent intent = new Intent(RentChannelListActivity.this, RentCategoryListActivity.class);
//                intent.putExtra("channelName", channel.getChannelName());
//                startActivity(intent);
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return channels.size();
//        }
//
//        class ChannelViewHolder extends RecyclerView.ViewHolder {
//            TextView textViewChannelName;
//            ImageView imageViewChannelLogo;
//
//            public ChannelViewHolder(@NonNull View itemView) {
//                super(itemView);
//                textViewChannelName = itemView.findViewById(R.id.textViewChannelName);
//                imageViewChannelLogo = itemView.findViewById(R.id.imageViewChannelLogo);
//            }
//        }
//    }
//}
