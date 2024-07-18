package com.example.va;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProductPaymentActivity extends AppCompatActivity {

    private PaymentSheet paymentSheet;
    private PaymentSheet.CustomerConfiguration customerConfig;
    private String paymentIntentClientSecret;

    private int amount;
    private int originalAmount;
    private List<ProductItem> productList; // List of products to be ordered

    private static final String TAG = "ProductPaymentActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        String publishableKey = "pk_live_51H0KEiLvH497ultUUOVKsimw3dnRORfEIiyYqt0KBW9pLQfkS66F721HfPDJcCNRNwY3wHKUlwtOtLwf6bp5FvF6007xdoeXY6";
        PaymentConfiguration.init(getApplicationContext(), publishableKey);

        Intent intent = getIntent();
        originalAmount = intent.getIntExtra("totalPrice", 0);
        amount = originalAmount * 100; // Convert to smallest unit (cents)
        productList = (List<ProductItem>) intent.getSerializableExtra("productList");

        if (originalAmount == 0 || productList == null || productList.isEmpty()) {
            Toast.makeText(this, "Invalid order", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        updateOrderStatus();
        recordTransaction(true, "Order placed successfully", productList, originalAmount);
    }

    private void updateOrderStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(user.getUid());
            for (ProductItem product : productList) {
                String orderId = ordersRef.push().getKey();
                if (orderId != null) {
                    OrderItem orderItem = new OrderItem(orderId, product.getImageUrl(), product.getProductTitle(), product.getProductPrice(), product.getQuantity(), product.isSelected());
                    ordersRef.child(orderId).setValue(orderItem);
                }
            }
            Toast.makeText(getApplicationContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void recordTransaction(boolean success, String message, List<ProductItem> productList, int originalAmount) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("transactions").child(user.getUid());
            String transactionId = transactionsRef.push().getKey();
            if (transactionId != null) {
                StringBuilder productDetails = new StringBuilder();
                for (ProductItem product : productList) {
                    productDetails.append(product.getProductTitle()).append(" (Qty: ").append(product.getQuantity()).append(") ");
                }
                Transaction transaction = new Transaction(transactionId, originalAmount, success, message + ". Products: " + productDetails.toString().trim());
                transactionsRef.child(transactionId).setValue(transaction);
            }
        }
    }
}
