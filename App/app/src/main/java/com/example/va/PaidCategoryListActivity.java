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
import androidx.appcompat.app.AppCompatActivity;
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

public class PaidCategoryListActivity extends BaseActivity {

    private static final String TAG = "CategoryListActivity";
    private RecyclerView recyclerViewCategories;
    private DatabaseReference databaseReference;
    private List<Category> categoryList;
    private CategoryAdapter adapter;
    private String channelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        channelName = getIntent().getStringExtra("channelName");
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new GridLayoutManager(this, 2));
        categoryList = new ArrayList<>();
        adapter = new CategoryAdapter(this, categoryList);
        recyclerViewCategories.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("membership");

        fetchCategories();
    }

    private void fetchCategories() {
        databaseReference.orderByChild("channelName").equalTo(channelName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                Set<String> uniqueCategories = new HashSet<>();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String videoType = categorySnapshot.child("videoType").getValue(String.class);
                    if ("Paid".equals(videoType)) {
                        String categoryLogo = categorySnapshot.child("categoryLogoLink").getValue(String.class);
                        String category = categorySnapshot.child("categories").getValue(String.class);
                        if (category != null && categoryLogo != null && uniqueCategories.add(category)) {
                            categoryList.add(new Category(category, categoryLogo));
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching categories: " + error.getMessage());
            }
        });
    }

    private static class Category {
        private String categoryName;
        private String categoryLogo;

        public Category(String categoryName, String categoryLogo) {
            this.categoryName = categoryName;
            this.categoryLogo = categoryLogo;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public String getCategoryLogo() {
            return categoryLogo;
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        private Context context;
        private List<Category> categories;

        public CategoryAdapter(Context context, List<Category> categories) {
            this.context = context;
            this.categories = categories;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = categories.get(position);
            holder.textViewCategoryName.setText(category.getCategoryName());
            Glide.with(context).load(category.getCategoryLogo()).into(holder.imageViewCategoryLogo);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(PaidCategoryListActivity.this, PaidVideoListActivity.class);
                intent.putExtra("channelName", channelName);
                intent.putExtra("category", category.getCategoryName());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return categories.size();
        }

        class CategoryViewHolder extends RecyclerView.ViewHolder {
            TextView textViewCategoryName;
            ImageView imageViewCategoryLogo;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
                imageViewCategoryLogo = itemView.findViewById(R.id.imageViewCategoryLogo);
            }
        }
    }
}
