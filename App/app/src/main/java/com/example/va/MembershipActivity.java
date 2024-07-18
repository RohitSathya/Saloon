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

public class MembershipActivity extends AppCompatActivity implements MembershipVideoAdapter.OnVideoClickListener {

    private RecyclerView paidVideosRecyclerView;
    private MembershipVideoAdapter paidVideosAdapter;
    private List<MembershipVideoItem> paidVideosList;
    private DatabaseReference subscriptionRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        paidVideosRecyclerView = findViewById(R.id.recycler_view_paid_videos);
        paidVideosRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns

        paidVideosList = new ArrayList<>();
        paidVideosAdapter = new MembershipVideoAdapter(this, paidVideosList, true, this);
        paidVideosRecyclerView.setAdapter(paidVideosAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions");

        fetchVideoData();
        checkSubscription();
    }

    private void fetchVideoData() {
        DatabaseReference membershipRef = FirebaseDatabase.getInstance().getReference("membership");
        membershipRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                paidVideosList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MembershipVideoItem video = snapshot.getValue(MembershipVideoItem.class);
                    if (video != null) {
                        paidVideosList.add(video);
                    }
                }
                paidVideosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MembershipActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkSubscription() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            subscriptionRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("subscribed").getValue(Boolean.class)) {
                        unlockPaidVideos();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MembershipActivity.this, "Failed to check subscription", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void unlockPaidVideos() {
        for (MembershipVideoItem video : paidVideosList) {
            video.setLocked(false);
        }
        paidVideosAdapter.notifyDataSetChanged();
    }

    @Override
    public void onVideoClick(MembershipVideoItem videoItem) {

            Intent intent = new Intent(this, MembershipVideoPlayerActivity.class);
            intent.putExtra("video_url", videoItem.getVideoLink());
            intent.putExtra("video_title", videoItem.getVideoName());
            intent.putExtra("video_thumbnail", videoItem.getThumbnailLink());
            startActivity(intent);

    }

    public void subscribeUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            subscriptionRef.child(user.getUid()).child("subscribed").setValue(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            unlockPaidVideos();
                            Toast.makeText(MembershipActivity.this, "Subscription successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MembershipActivity.this, "Subscription failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
