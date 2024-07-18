package com.example.va;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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

public class ShopActivity extends BaseActivity implements ShopAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private ShopAdapter shopAdapter;
    private List<ProductItem> productList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        recyclerView = findViewById(R.id.recycler_view_shop);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();
        shopAdapter = new ShopAdapter(this, productList, this);
        recyclerView.setAdapter(shopAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        loadProducts();

        ImageView cartIcon = findViewById(R.id.icon_cart);
        cartIcon.setOnClickListener(v -> navigateToCartActivity());

        ImageView ordersIcon = findViewById(R.id.icon_orders);
        ordersIcon.setOnClickListener(v -> navigateToOrdersActivity());
    }

    private void loadProducts() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    ProductItem product = productSnapshot.getValue(ProductItem.class);
                    if (product != null) {
                        product.setId(productSnapshot.getKey()); // Set the ID of the product
                        productList.add(product);
                    }
                }
                shopAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShopActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProductClick(ProductItem product) {
        checkAddressAndProceed(product);
    }

    private void checkAddressAndProceed(ProductItem product) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference.child("addresses").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Address address = snapshot.getValue(Address.class);
                        showAddressDialog(address, product);
                    } else {
                        navigateToAddressActivity(product);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ShopActivity.this, "Failed to check address", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateToAddressActivity(ProductItem product) {
        Intent intent = new Intent(this, AddressActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }

    private void showAddressDialog(Address address, ProductItem product) {
        AddressDialogFragment dialog = AddressDialogFragment.newInstance(address, product);
        dialog.show(getSupportFragmentManager(), "AddressDialogFragment");
    }

    private void navigateToCartActivity() {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
    }

    private void navigateToOrdersActivity() {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }
}
