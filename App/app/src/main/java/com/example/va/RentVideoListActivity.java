package com.example.va;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RentVideoListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RentVideoAdapter adapter;
    private List<RentedVideoItem> videoItemList;
    private DatabaseReference databaseReference;
    private RentedVideoItem selectedVideoItem;
    private int selectedAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_video_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        videoItemList = new ArrayList<>();
        adapter = new RentVideoAdapter(videoItemList, this::checkIfVideoPurchased);
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("membership");

        loadVideos();
    }

    private void loadVideos() {
        databaseReference.orderByChild("videoType").equalTo("Rent").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoItemList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    RentedVideoItem videoItem = dataSnapshot.getValue(RentedVideoItem.class);
                    videoItemList.add(videoItem);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkIfVideoPurchased(RentedVideoItem videoItem) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            DatabaseReference rentedRef = FirebaseDatabase.getInstance().getReference("rented")
                    .child(auth.getCurrentUser().getUid())
                    .child(videoItem.getVideoId());

            rentedRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Video already purchased
                        navigateToRentVideoDetailActivity(videoItem);
                    } else {
                        // Video not purchased
                        showPurchaseDialog(videoItem);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void showPurchaseDialog(RentedVideoItem videoItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_purchase, null);
        builder.setView(view);

        TextView textViewTitle = view.findViewById(R.id.text_view_video_title);
        textViewTitle.setText("Unlock " + videoItem.getVideoName());

        Button purchaseButton = view.findViewById(R.id.purchase_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);

        AlertDialog dialog = builder.create();

        purchaseButton.setOnClickListener(v -> {
            selectedVideoItem = videoItem;
            selectedAmount = Integer.parseInt(videoItem.getVideoPrice());
            unlockCategory(videoItem);
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void unlockCategory(RentedVideoItem videoItem) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            DatabaseReference rentedRef = FirebaseDatabase.getInstance().getReference("rented")
                    .child(auth.getCurrentUser().getUid())
                    .child(videoItem.getVideoId());

            // Ensure videoItem is not null before using it
            if (videoItem != null && videoItem.getVideoId() != null) {
                rentedRef.setValue(true).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        navigateToRentVideoDetailActivity(videoItem);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Error")
                                .setMessage("Failed to unlock video. Please try again.")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error")
                        .setMessage("Failed to unlock video due to null video reference.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    private void navigateToRentVideoDetailActivity(RentedVideoItem videoItem) {
        Intent intent = new Intent(this, RentVideoDetailActivity.class);
        intent.putExtra("video_item", videoItem);
        startActivity(intent);
    }

    private static class RentVideoAdapter extends RecyclerView.Adapter<RentVideoAdapter.ViewHolder> {

        private final List<RentedVideoItem> videoItemList;
        private final OnItemClickListener listener;

        public RentVideoAdapter(List<RentedVideoItem> videoItemList, OnItemClickListener listener) {
            this.videoItemList = videoItemList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rent_video, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RentedVideoItem videoItem = videoItemList.get(position);
            holder.bind(videoItem, listener);
        }

        @Override
        public int getItemCount() {
            return videoItemList.size();
        }

        public interface OnItemClickListener {
            void onItemClick(RentedVideoItem videoItem);
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final ImageView imageViewThumbnail;
            private final TextView textViewVideoName;
            private final TextView textViewVideoPrice;
            private final Button buttonPlayTrailer;
            private final Button buttonGallery;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageViewThumbnail = itemView.findViewById(R.id.imageViewThumbnail);
                textViewVideoName = itemView.findViewById(R.id.textViewVideoName);
                textViewVideoPrice = itemView.findViewById(R.id.textViewVideoPrice);
                buttonPlayTrailer = itemView.findViewById(R.id.buttonPlayTrailer);
                buttonGallery = itemView.findViewById(R.id.buttonGallery);
            }

            public void bind(RentedVideoItem videoItem, OnItemClickListener listener) {
                textViewVideoName.setText(videoItem.getVideoName());
                textViewVideoPrice.setText("₹" + videoItem.getVideoPrice());
                Glide.with(imageViewThumbnail.getContext()).load(videoItem.getThumbnailLink()).into(imageViewThumbnail);

                itemView.setOnClickListener(v -> listener.onItemClick(videoItem));

                buttonPlayTrailer.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), ReelVideoPlayerActivity.class);
                    intent.putExtra("trailer_link", videoItem.gettrailerLink());
                    itemView.getContext().startActivity(intent);
                });

                buttonGallery.setOnClickListener(v -> {
                    Intent intent = new Intent(itemView.getContext(), GalleryActivity.class);
                    ArrayList<String> imageUrls = new ArrayList<>();
                    imageUrls.add(videoItem.getSampleImage1Link());
                    imageUrls.add(videoItem.getSampleImage2Link());
                    imageUrls.add(videoItem.getSampleImage3Link());
                    intent.putStringArrayListExtra("image_urls", imageUrls);
                    itemView.getContext().startActivity(intent);
                });
            }
        }
    }

    public static class RentedVideoItem implements Serializable {
        private String videoId;
        private String videoType;
        private String contentType;
        private String genre;
        private String source;
        private String channelName;
        private String categories;
        private String videoName;
        private String episodeNumber;
        private String thumbnailLink;
        private String videoPrice;
        private String languageType;
        private String videoLink;
        private String trailerLink;
        private String sampleImage1Link;
        private String sampleImage2Link;
        private String sampleImage3Link;

        public RentedVideoItem() {
            // Default constructor required for calls to DataSnapshot.getValue(RentedVideoItem.class)
        }

        // Getters and setters

        public String getVideoId() {
            return videoId;
        }

        public void setVideoId(String videoId) {
            this.videoId = videoId;
        }

        public String getVideoType() {
            return videoType;
        }

        public void setVideoType(String videoType) {
            this.videoType = videoType;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getChannelName() {
            return channelName;
        }

        public void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        public String getCategories() {
            return categories;
        }

        public void setCategories(String categories) {
            this.categories = categories;
        }

        public String getVideoName() {
            return videoName;
        }

        public void setVideoName(String videoName) {
            this.videoName = videoName;
        }

        public String getEpisodeNumber() {
            return episodeNumber;
        }

        public void setEpisodeNumber(String episodeNumber) {
            this.episodeNumber = episodeNumber;
        }

        public String getThumbnailLink() {
            return thumbnailLink;
        }

        public void setThumbnailLink(String thumbnailLink) {
            this.thumbnailLink = thumbnailLink;
        }

        public String getVideoPrice() {
            return videoPrice;
        }

        public void setVideoPrice(String videoPrice) {
            this.videoPrice = videoPrice;
        }

        public String getlanguageType() {
            return languageType;
        }

        public void setLanguage(String language) {
            this.languageType = language;
        }

        public String getVideoLink() {
            return videoLink;
        }

        public void setVideoLink(String videoLink) {
            this.videoLink = videoLink;
        }

        public String gettrailerLink() {
            return trailerLink;
        }

        public void setTrailerLink(String trailerLink) {
            this.trailerLink = trailerLink;
        }

        public String getSampleImage1Link() {
            return sampleImage1Link;
        }

        public void setSampleImage1Link(String sampleImage1Link) {
            this.sampleImage1Link = sampleImage1Link;
        }

        public String getSampleImage2Link() {
            return sampleImage2Link;
        }

        public void setSampleImage2Link(String sampleImage2Link) {
            this.sampleImage2Link = sampleImage2Link;
        }

        public String getSampleImage3Link() {
            return sampleImage3Link;
        }

        public void setSampleImage3Link(String sampleImage3Link) {
            this.sampleImage3Link = sampleImage3Link;
        }
    }

}
