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
import java.util.Map;
import java.util.Scanner;

public class PaymentActivity extends AppCompatActivity {

    private PaymentSheet paymentSheet;
    private PaymentSheet.CustomerConfiguration customerConfig;
    private String paymentIntentClientSecret;

    private static final String TAG = "PaymentActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        String publishableKey ="pk_live_51H0KEiLvH497ultUUOVKsimw3dnRORfEIiyYqt0KBW9pLQfkS66F721HfPDJcCNRNwY3wHKUlwtOtLwf6bp5FvF6007xdoeXY6";
        PaymentConfiguration.init(getApplicationContext(), publishableKey);

//        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

        // Retrieve the plan and amount from the intent
        Intent intent = getIntent();
        String plan = intent.getStringExtra("plan");
        int originalAmount = intent.getIntExtra("amount", 0);
        int amount = originalAmount * 100;  // Convert to smallest unit (cents)

        if (plan == null || originalAmount == 0) {
            Toast.makeText(this, "No plan selected or amount is invalid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        updateSubscriptionStatus(plan, amount, originalAmount);
    }

//    private void startPaymentFlow(String plan, int amount) {
//        new Thread(() -> {
//            try {
//                // Create PaymentIntent on the server
//                String response = createPaymentIntent(amount);
//                JSONObject jsonResponse = new JSONObject(response);
//                paymentIntentClientSecret = jsonResponse.getString("clientSecret");
//
//                // Simulate customer configuration (In real scenarios, these values are retrieved from the backend)
//                String customerId = "customer_id_example";
//                String ephemeralKey = "ephemeral_key_example";
//
//                // Initialize CustomerConfig
//                customerConfig = new PaymentSheet.CustomerConfiguration(customerId, ephemeralKey);
//
//                runOnUiThread(this::presentPaymentSheet);
//            } catch (Exception e) {
//                Log.e(TAG, "Failed to create PaymentIntent: " + e.getMessage(), e);
//                runOnUiThread(() -> Toast.makeText(PaymentActivity.this, "Failed to create PaymentIntent", Toast.LENGTH_SHORT).show());
//            }
//        }).start();
//    }

//    private String createPaymentIntent(int amount) throws Exception {
//        URL url = new URL("https://stripeees.vercel.app/create-payment-intent");
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Content-Type", "application/json");
//        connection.setDoOutput(true);
//
//        Map<String, Object> paymentData = new HashMap<>();
//        paymentData.put("currency", "inr");
//        paymentData.put("amount", amount);
//
//        JSONObject json = new JSONObject(paymentData);
//        try (OutputStream outputStream = connection.getOutputStream()) {
//            outputStream.write(json.toString().getBytes());
//        }
////
//        Scanner scanner = new Scanner(connection.getInputStream());
//        String response = scanner.useDelimiter("\\A").next();
//        Log.d(TAG, "PaymentIntent response: " + response);
//        return response;
//    }

//    private void presentPaymentSheet() {
//        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("Example, Inc.", customerConfig);
//        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
//    }
//
//    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
//        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
//            updateSubscriptionStatus();
//        } else {
//            Log.e(TAG, "Payment failed: " + paymentSheetResult);
//            Toast.makeText(this, "Payment failed. Please try again.", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void updateSubscriptionStatus(String plan, int amount, int originalAmount) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions");
            subscriptionRef.child(user.getUid()).child("subscribed").setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(PaymentActivity.this, "Subscription successful", Toast.LENGTH_SHORT).show();
                    recordTransaction(true, "Subscribed to " + plan, originalAmount);
                    finish();
                } else {
                    Toast.makeText(PaymentActivity.this, "Failed to update subscription. Please try again.", Toast.LENGTH_SHORT).show();
                    recordTransaction(false, "Subscription to " + plan + " failed", originalAmount);
                }
            });
        }
    }

    private void recordTransaction(boolean success, String message, int amount) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("transactions").child(user.getUid());
            String transactionId = transactionsRef.push().getKey();
            if (transactionId != null) {
                Transaction transaction = new Transaction(transactionId, amount, success, message);
                transactionsRef.child(transactionId).setValue(transaction);
            }
        }
    }
}
