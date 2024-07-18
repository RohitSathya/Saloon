package com.example.va;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class CartActivity extends BaseActivity implements CartAdapter.OnCartItemChangeListener, AddressDialogFragment.AddressDialogListener {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<ProductItem> cartList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private TextView totalPriceTextView;
    private Button proceedToPaymentButton;
    private int totalPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_view_cart);
        totalPriceTextView = findViewById(R.id.text_view_total_price);
        proceedToPaymentButton = findViewById(R.id.button_proceed_to_payment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartList, this);
        recyclerView.setAdapter(cartAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadCart();

        proceedToPaymentButton.setOnClickListener(v -> {
            List<ProductItem> selectedProducts = getSelectedProducts();
            if (!selectedProducts.isEmpty()) {
                checkAddressAndProceed(selectedProducts);
            } else {
                Toast.makeText(CartActivity.this, "No products selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCart() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference cartRef = databaseReference.child("carts").child(userId);

            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    cartList.clear();
                    for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                        ProductItem product = cartSnapshot.getValue(ProductItem.class);
                        if (product != null) {
                            cartList.add(product);
                        }
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, "Failed to load cart", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateTotalPrice() {
        totalPrice = 0;
        for (ProductItem product : cartList) {
            if (product.isSelected()) {
                totalPrice += product.getProductPrice() * product.getQuantity();
            }
        }
        totalPriceTextView.setText("Total: ₹" + totalPrice);
    }

    private List<ProductItem> getSelectedProducts() {
        List<ProductItem> selectedProducts = new ArrayList<>();
        for (ProductItem product : cartList) {
            if (product.isSelected()) {
                selectedProducts.add(product);
            }
        }
        return selectedProducts;
    }

    @Override
    public void onIncreaseQuantity(ProductItem product) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference cartRef = databaseReference.child("carts").child(userId).child(product.getId());
            int newQuantity = product.getQuantity() + 1;
            product.setQuantity(newQuantity);
            cartRef.setValue(product);
            updateTotalPrice();
        }
    }

    @Override
    public void onDecreaseQuantity(ProductItem product) {
        if (product.getQuantity() > 1) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                DatabaseReference cartRef = databaseReference.child("carts").child(userId).child(product.getId());
                int newQuantity = product.getQuantity() - 1;
                product.setQuantity(newQuantity);
                cartRef.setValue(product);
                updateTotalPrice();
            }
        }
    }

    @Override
    public void onDeleteProduct(ProductItem product) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference cartRef = databaseReference.child("carts").child(userId).child(product.getId());
            cartRef.removeValue();
            cartList.remove(product);
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice();
        }
    }

    @Override
    public void onSelectProduct(ProductItem product, boolean isSelected) {
        product.setSelected(isSelected);
        updateTotalPrice();
    }

    private void checkAddressAndProceed(List<ProductItem> selectedProducts) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            databaseReference.child("addresses").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Address address = snapshot.getValue(Address.class);
                        showAddressDialog(address, selectedProducts);
                    } else {
                        navigateToAddressActivity(selectedProducts);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(CartActivity.this, "Failed to check address", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateToAddressActivity(List<ProductItem> selectedProducts) {
        Intent intent = new Intent(this, AddressActivity.class);
        intent.putExtra("selectedProducts", new ArrayList<>(selectedProducts));
        startActivity(intent);
    }

    private void showAddressDialog(Address address, List<ProductItem> selectedProducts) {
        AddressDialogFragment dialog = AddressDialogFragment.newInstance(address, totalPrice, selectedProducts);
        dialog.show(getSupportFragmentManager(), "AddressDialogFragment");
    }

    @Override
    public void onProceedToPayment(int totalPrice, List<ProductItem> selectedProducts) {
        Intent intent = new Intent(this, ProductPaymentActivity.class);
        intent.putExtra("totalPrice", totalPrice);
        intent.putExtra("productList", new ArrayList<>(selectedProducts)); // Pass the selected product list
        startActivity(intent);
    }

    @Override
    public void onAddNewAddress(List<ProductItem> selectedProducts) {
        navigateToAddressActivity(selectedProducts);
    }

    @Override
    public void onProceedToPayment(ProductItem product) {
        // No implementation needed for CartActivity
    }

    @Override
    public void onAddNewAddress(ProductItem product) {
        // No implementation needed for CartActivity
    }
}
