package com.example.va;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {

    private Context context;
    private List<ProductItem> productList;
    private OnProductClickListener listener;

    public ShopAdapter(Context context, List<ProductItem> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    public interface OnProductClickListener {
        void onProductClick(ProductItem product);
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ProductItem product = productList.get(position);
        holder.textViewProductName.setText(product.getProductTitle());
        holder.textViewProductPrice.setText("₹" + product.getProductPrice());

        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.imageViewProduct);

        holder.buttonAddToCart.setOnClickListener(v -> addProductToCart(product));

        holder.buttonTrailer.setOnClickListener(v -> playTrailer(product.getTrailerUrl()));

        holder.imageViewProduct.setOnClickListener(v -> {
            List<String> imageUrls = new ArrayList<>();
            imageUrls.add(product.getImageUrl());
            if (product.getSampleImage1Url() != null) imageUrls.add(product.getSampleImage1Url());
            if (product.getSampleImage2Url() != null) imageUrls.add(product.getSampleImage2Url());
            if (product.getSampleImage3Url() != null) imageUrls.add(product.getSampleImage3Url());

            Intent intent = new Intent(context, FullScreenImageViewerActivity.class);
            intent.putStringArrayListExtra(FullScreenImageViewerActivity.EXTRA_IMAGE_URLS, new ArrayList<>(imageUrls));
            intent.putExtra(FullScreenImageViewerActivity.EXTRA_CURRENT_POSITION, 0);
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> listener.onProductClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void addProductToCart(ProductItem product) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(userId);

        cartRef.child(product.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ProductItem existingProduct = snapshot.getValue(ProductItem.class);
                    existingProduct.setQuantity(existingProduct.getQuantity() + 1);
                    cartRef.child(product.getId()).setValue(existingProduct).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    cartRef.child(product.getId()).setValue(product).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to add to cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playTrailer(String trailerUrl) {
        Intent intent = new Intent(context, FreeVideoPlayerActivity.class);
        intent.putExtra(FreeVideoPlayerActivity.VIDEO_URL_EXTRA, trailerUrl);
        context.startActivity(intent);
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder {
        TextView textViewProductName, textViewProductPrice;
        ImageView imageViewProduct;
        Button buttonAddToCart, buttonTrailer;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewProductName = itemView.findViewById(R.id.text_view_product_name);
            textViewProductPrice = itemView.findViewById(R.id.text_view_product_price);
            imageViewProduct = itemView.findViewById(R.id.image_view_product);
            buttonAddToCart = itemView.findViewById(R.id.button_add_to_cart);
            buttonTrailer = itemView.findViewById(R.id.button_trailer);
        }
    }
}
