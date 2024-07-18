//package com.example.va;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.SearchView;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ReelsActivity extends BaseActivity {
//
//    private RecyclerView recyclerView;
//    private ReelVideoAdapter reelVideoAdapter;
//    private List<ReelVideoItem> reelVideoList = new ArrayList<>();
//    private List<ReelVideoItem> filteredReelVideoList = new ArrayList<>();
//    private TextView emptyView;
//    private SearchView searchView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reels);
//
//        recyclerView = findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//
//        emptyView = findViewById(R.id.emptyView);
//        searchView = findViewById(R.id.searchView);
//        setSearchViewTextColor(searchView, android.R.color.white);
//
//        reelVideoAdapter = new ReelVideoAdapter(this, filteredReelVideoList);
//        recyclerView.setAdapter(reelVideoAdapter);
//
//        ImageButton reelButton = findViewById(R.id.action_reel);
//        ImageButton galleryButton = findViewById(R.id.action_gallery);
//
//        reelButton.setOnClickListener(v -> {
//            // Stay in the same activity
//        });
//
//        galleryButton.setOnClickListener(v -> {
//            Intent intent = new Intent(ReelsActivity.this, GalleryActivity.class);
//            startActivity(intent);
//        });
//
//        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("reels");
//        mDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                reelVideoList.clear();
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot reelSnapshot : dataSnapshot.getChildren()) {
//                        ReelVideoItem reelVideoItem = reelSnapshot.getValue(ReelVideoItem.class);
//                        reelVideoList.add(reelVideoItem);
//                    }
//                }
//                filteredReelVideoList.clear();
//                filteredReelVideoList.addAll(reelVideoList);
//                reelVideoAdapter.notifyDataSetChanged();
//
//                if (filteredReelVideoList.isEmpty()) {
//                    recyclerView.setVisibility(View.GONE);
//                    emptyView.setVisibility(View.VISIBLE);
//                } else {
//                    recyclerView.setVisibility(View.VISIBLE);
//                    emptyView.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.w("FirebaseLogs", "loadReels:onCancelled", databaseError.toException());
//            }
//        });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                filter(newText);
//                return true;
//            }
//        });
//    }
//
//    private void filter(String text) {
//        filteredReelVideoList.clear();
//        if (text.isEmpty()) {
//            filteredReelVideoList.addAll(reelVideoList);
//        } else {
//            text = text.toLowerCase();
//            for (ReelVideoItem item : reelVideoList) {
//                if (item.getTitle().toLowerCase().contains(text)) {
//                    filteredReelVideoList.add(item);
//                }
//            }
//        }
//        reelVideoAdapter.notifyDataSetChanged();
//
//        if (filteredReelVideoList.isEmpty()) {
//            recyclerView.setVisibility(View.GONE);
//            emptyView.setVisibility(View.VISIBLE);
//        } else {
//            recyclerView.setVisibility(View.VISIBLE);
//            emptyView.setVisibility(View.GONE);
//        }
//    }
//    private void setSearchViewTextColor(SearchView searchView, int color) {
//        try {
//            Field searchTextField = SearchView.class.getDeclaredField("mSearchSrcTextView");
//            searchTextField.setAccessible(true);
//            TextView searchText = (TextView) searchTextField.get(searchView);
//            searchText.setTextColor(getResources().getColor(color));
//            searchText.setHintTextColor(getResources().getColor(android.R.color.darker_gray)); // Optional: Set hint color
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
//    }
//}