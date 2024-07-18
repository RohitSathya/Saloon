//package com.example.va;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class StoreActivity extends AppCompatActivity implements StoreVideoAdapter.OnVideoClickListener {
//
//    private static final int PAYMENT_REQUEST_CODE = 1001;
//
//    private RecyclerView recyclerView;
//    private StoreVideoAdapter videoAdapter;
//    private List<VideoItem> videoList;
//    private DatabaseReference databaseReference;
//    private FirebaseAuth firebaseAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_store);
//
//        recyclerView = findViewById(R.id.recycler_view_store);
//        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
//
//        videoList = new ArrayList<>();
//        videoAdapter = new StoreVideoAdapter(this, videoList, this);
//        recyclerView.setAdapter(videoAdapter);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        loadLockedVideos();
//        checkRentedVideos();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        checkRentedVideos(); // Refresh rented videos when the activity resumes
//    }
//
//    private void loadLockedVideos() {
//        videoList.add(new VideoItem("1", "Locked Video 1", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQf29h58oEcSXReZZa8PJAdOeyF_DArmqeiWv7cZ8d3dPmDGbPiAucD20vM2ikfu78D4p0&usqp=CAU", "https://firebasestorage.googleapis.com/v0/b/va123-5d836.appspot.com/o/reels%2Fvideos%2F1720614156563.mp4?alt=media&token=ade4f171-c480-46be-a7a9-e52c08522dc7", true));
//        videoList.add(new VideoItem("2", "Locked Video 2", "https://www.videogameschronicle.com/files/2020/11/Spider-Man-Remastered-h-scaled.jpg", "https://firebasestorage.googleapis.com/v0/b/va123-5d836.appspot.com/o/reels%2Fvideos%2F1720614126117.mp4?alt=media&token=2353ad68-906f-483d-aaea-93837d5210ab", true));
//        videoList.add(new VideoItem("3", "Locked Video 3", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtxWsjGM6Ob90Sd4f1iyuU_ZnK7_soL906VA&s", "https://www.w3schools.com/html/mov_bbb.mp4", true));
//        videoList.add(new VideoItem("4", "Locked Video 4", "https://static1.colliderimages.com/wordpress/wp-content/uploads/2023/12/superman-legacy-david-corenswet-dc.jpg", "https://www.w3schools.com/html/mov_bbb.mp4", true));
//        videoAdapter.notifyDataSetChanged();
//    }
//
//    private void checkRentedVideos() {
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if (user != null) {
//            String userId = user.getUid();
//            databaseReference.child("rented").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        String videoId = dataSnapshot.getKey();
//                        Boolean isLocked = dataSnapshot.getValue(Boolean.class);
//                        if (isLocked != null && !isLocked) {
//                            unlockVideoInList(videoId);
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    Toast.makeText(StoreActivity.this, "Failed to load rented videos", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    private void unlockVideoInList(String videoId) {
//        for (VideoItem videoItem : videoList) {
//            if (videoItem.getId().equals(videoId)) {
//                videoItem.setLocked(false);
//                videoAdapter.notifyDataSetChanged();
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void onVideoClick(VideoItem videoItem) {
//        if (videoItem.isLocked()) {
//            StoreSubscriptionDialogFragment dialog = StoreSubscriptionDialogFragment.newInstance(videoItem);
//            dialog.setTargetFragment(null, PAYMENT_REQUEST_CODE); // Set the target fragment to handle the result
//            dialog.show(getSupportFragmentManager(), "StoreSubscriptionDialogFragment");
//        } else {
//            // Open video player activity
//            Intent intent = new Intent(this, VideoPlayerActivity.class);
//            intent.putExtra("video_id",videoItem.getId());
//            intent.putExtra("video_url", videoItem.getVideoUrl());
//            intent.putExtra("video_title", videoItem.getTitle());
//            intent.putExtra("video_thumbnail", videoItem.getThumbnailUrl());
//            startActivity(intent);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
//            String videoId = data.getStringExtra("video_id");
//            unlockVideo(videoId);
//        }
//    }
//
//    private void unlockVideo(String videoId) {
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if (user != null) {
//            String userId = user.getUid();
//            DatabaseReference rentedRef = FirebaseDatabase.getInstance().getReference("rented").child(userId).child(videoId);
//            rentedRef.setValue(false).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    unlockVideoInList(videoId);
//                } else {
//                    Toast.makeText(StoreActivity.this, "Failed to unlock video. Please try again.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//}
