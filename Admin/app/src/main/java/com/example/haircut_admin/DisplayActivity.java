// DisplayActivity.java
package com.example.haircut_admin;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ReelAdapter mAdapter;

    private DatabaseReference mDatabaseRef;
    private List<Reel> mReels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReels = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("reels");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mReels.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Reel reel = postSnapshot.getValue(Reel.class);
                    mReels.add(reel);
                }
                mAdapter = new ReelAdapter(DisplayActivity.this, mReels);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DisplayActivity", "Failed to load reels", error.toException());
            }
        });
    }
}
