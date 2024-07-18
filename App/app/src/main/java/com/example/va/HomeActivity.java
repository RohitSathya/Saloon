package com.example.va;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements HomeVideoAdapter.OnVideoClickListener {

    private RecyclerView freeVideosRecyclerView;
    private HomeVideoAdapter freeVideosAdapter;
    private List<VideoItem> freeVideosList;
    private DatabaseReference subscriptionRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        freeVideosRecyclerView = findViewById(R.id.recycler_view_free_videos);
        freeVideosRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        freeVideosList = new ArrayList<>();
        freeVideosAdapter = new HomeVideoAdapter(this, freeVideosList, false, this);
        freeVideosRecyclerView.setAdapter(freeVideosAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions");

        loadDummyData();
        checkSubscription();
    }

    private void loadDummyData() {
        String thumbnailUrl1 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQf29h58oEcSXReZZa8PJAdOeyF_DArmqeiWv7cZ8d3dPmDGbPiAucD20vM2ikfu78D4p0&usqp=CAU";
        String thumbnailUrl2 = "https://www.videogameschronicle.com/files/2020/11/Spider-Man-Remastered-h-scaled.jpg";
        String thumbnailUrl3 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtxWsjGM6Ob90Sd4f1iyuU_ZnK7_soL906VA&s";
        String thumbnailUrl4 = "https://static1.colliderimages.com/wordpress/wp-content/uploads/2023/12/superman-legacy-david-corenswet-dc.jpg";
        String thumbnailUrl5 = "https://playcontestofchampions.com/wp-content/uploads/2021/11/champion-iron-man-infinity-war-720x720.jpg";

        // Free Videos
        freeVideosList.add(new VideoItem("1", "Avengers", thumbnailUrl1, "https://firebasestorage.googleapis.com/v0/b/va123-5d836.appspot.com/o/reels%2Fvideos%2F1720614126117.mp4?alt=media&token=2353ad68-906f-483d-aaea-93837d5210ab"));
        freeVideosList.add(new VideoItem("2", "Spiderman", thumbnailUrl2, "https://baldvids.com/player-embed/id/4017"));
        freeVideosList.add(new VideoItem("3", "Batman", thumbnailUrl3, "https://www.youtube.com/watch?v=xbw4T24hmwM&feature=youtu.be"));
        freeVideosList.add(new VideoItem("4", "Superman", thumbnailUrl4, "https://drive.google.com/file/d/1ikL-JkL4dKtFatd33Y7ynQi6ZY8qPCpm/view?usp=drivesdk"));
        freeVideosList.add(new VideoItem("5", "Iron Man", thumbnailUrl5, "https://www.w3schools.com/html/mov_bbb.mp4"));

        freeVideosAdapter.notifyDataSetChanged();
    }

    private void checkSubscription() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            subscriptionRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("subscribed").getValue(Boolean.class)) {
                        // Handle subscription status
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(HomeActivity.this, "Failed to check subscription", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onVideoClick(VideoItem videoItem) {
        if (videoItem.isLocked()) {
            SubscriptionDialogFragment dialog = SubscriptionDialogFragment.newInstance(videoItem);
            dialog.show(getSupportFragmentManager(), "SubscriptionDialogFragment");
        } else {
            Intent intent = new Intent(this, HomeVideoPlayerActivity.class);
            intent.putExtra("video_url", videoItem.getVideoUrl());
            intent.putExtra("video_title", videoItem.getTitle());
            intent.putExtra("video_thumbnail", videoItem.getThumbnailUrl());
            startActivity(intent);
        }
    }
}
