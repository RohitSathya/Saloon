package com.example.va;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<ProductItem> cartList;
    private OnCartItemChangeListener listener;

    public CartAdapter(Context context, List<ProductItem> cartList, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    public interface OnCartItemChangeListener {
        void onIncreaseQuantity(ProductItem product);
        void onDecreaseQuantity(ProductItem product);
        void onDeleteProduct(ProductItem product);
        void onSelectProduct(ProductItem product, boolean isSelected);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ProductItem product = cartList.get(position);
        holder.textViewProductName.setText(product.getProductTitle());
        holder.textViewProductPrice.setText("₹" + product.getProductPrice());
        holder.textViewQuantity.setText("Quantity: " + product.getQuantity());

        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.imageViewProduct);

        holder.buttonIncreaseQuantity.setOnClickListener(v -> {
            listener.onIncreaseQuantity(product);
        });

        holder.buttonDecreaseQuantity.setOnClickListener(v -> {
            listener.onDecreaseQuantity(product);
        });

        holder.buttonDeleteProduct.setOnClickListener(v -> {
            listener.onDeleteProduct(product);
        });

        holder.checkBoxSelectProduct.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listener.onSelectProduct(product, isChecked);
        });

        holder.checkBoxSelectProduct.setChecked(product.isSelected());
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {

        TextView textViewProductName, textViewProductPrice, textViewQuantity;
        ImageView imageViewProduct;
        Button buttonIncreaseQuantity, buttonDecreaseQuantity, buttonDeleteProduct;
        CheckBox checkBoxSelectProduct;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewProductName = itemView.findViewById(R.id.text_view_product_name);
            textViewProductPrice = itemView.findViewById(R.id.text_view_product_price);
            textViewQuantity = itemView.findViewById(R.id.text_view_quantity);
            imageViewProduct = itemView.findViewById(R.id.image_view_product);
            buttonIncreaseQuantity = itemView.findViewById(R.id.button_increase_quantity);
            buttonDecreaseQuantity = itemView.findViewById(R.id.button_decrease_quantity);
            buttonDeleteProduct = itemView.findViewById(R.id.button_delete_product);
            checkBoxSelectProduct = itemView.findViewById(R.id.checkbox_select_product);
        }
    }
}