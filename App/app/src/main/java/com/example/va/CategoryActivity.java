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

public class CategoryActivity extends BaseActivity {

    private static final String TAG = "CategoryActivity";
    private RecyclerView recyclerViewCategories;
    private DatabaseReference databaseReference;
    private ArrayList<Category> categoryList;
    private CategoryAdapter categoryAdapter;
    private String channelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        channelName = getIntent().getStringExtra("channelName");
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList);
        recyclerViewCategories.setAdapter(categoryAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("membership");

        fetchCategories();
    }

    private void fetchCategories() {
        databaseReference.orderByChild("channelName").equalTo(channelName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String categoryName = dataSnapshot.child("categories").getValue(String.class);
                    String categoryLogo = dataSnapshot.child("categoryLogoLink").getValue(String.class);
                    if (categoryName != null && categoryLogo != null) {
                        categoryList.add(new Category(categoryName, categoryLogo));
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error fetching categories: " + error.getMessage());
            }
        });
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

        private ArrayList<Category> categoryList;

        public CategoryAdapter(ArrayList<Category> categoryList) {
            this.categoryList = categoryList;
        }

        @NonNull
        @Override
        public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
            Category category = categoryList.get(position);
            holder.textViewCategoryName.setText(category.getCategoryName());
            Glide.with(CategoryActivity.this).load(category.getCategoryLogo()).into(holder.imageViewCategoryLogo);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(CategoryActivity.this, VideoListActivity.class);
                intent.putExtra("channelName", channelName);
                intent.putExtra("category", category.getCategoryName());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return categoryList.size();
        }

        public class CategoryViewHolder extends RecyclerView.ViewHolder {

            public TextView textViewCategoryName;
            public ImageView imageViewCategoryLogo;

            public CategoryViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewCategoryName = itemView.findViewById(R.id.textViewCategoryName);
                imageViewCategoryLogo = itemView.findViewById(R.id.imageViewCategoryLogo);
            }
        }
    }

    private class Category {
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
}
