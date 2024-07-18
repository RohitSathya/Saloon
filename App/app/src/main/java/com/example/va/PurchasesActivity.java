package com.example.va;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class PurchasesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ThumbnailAdapter thumbnailAdapter;
    private List<String> thumbnailUrls;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);

        recyclerView = findViewById(R.id.recycler_view_purchases);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        thumbnailUrls = new ArrayList<>();
        thumbnailAdapter = new ThumbnailAdapter(this, thumbnailUrls);
        recyclerView.setAdapter(thumbnailAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("purchases").child(user.getUid());
            fetchPurchases();
        }
    }

    private void fetchPurchases() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                thumbnailUrls.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VideoItem videoItem = dataSnapshot.getValue(VideoItem.class);
                    if (videoItem != null) {
                        thumbnailUrls.add(videoItem.getThumbnailUrl());
                    }
                }
                thumbnailAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PurchasesActivity.this, "Failed to load purchases", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
