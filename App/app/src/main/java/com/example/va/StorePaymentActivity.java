//package com.example.va;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.stripe.android.PaymentConfiguration;
//import com.stripe.android.paymentsheet.PaymentSheet;
//import com.stripe.android.paymentsheet.PaymentSheetResult;
//import org.json.JSONObject;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Scanner;
//
//public class StorePaymentActivity extends AppCompatActivity {
//
//    private PaymentSheet paymentSheet;
//    private PaymentSheet.CustomerConfiguration customerConfig;
//    private String paymentIntentClientSecret;
//
//    private static final String TAG = "StorePaymentActivity";
//    private RentedVideoItem videoItem;
//    private int amount;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_store_payment);
//
//        Intent intent = getIntent();
//        videoItem = (RentedVideoItem) intent.getSerializableExtra("video_item");
//        amount = intent.getIntExtra("amount", 0);
//
//        String publishableKey = "pk_live_51H0KEiLvH497ultUUOVKsimw3dnRORfEIiyYqt0KBW9pLQfkS66F721HfPDJcCNRNwY3wHKUlwtOtLwf6bp5FvF6007xdoeXY6";
//        PaymentConfiguration.init(getApplicationContext(), publishableKey);
//        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
//
//        if (videoItem == null || amount == 0) {
//            Toast.makeText(this, "No video selected or amount is invalid", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        startPaymentFlow(videoItem, amount);
//
//        findViewById(R.id.cancel_button).setOnClickListener(v -> {
//            // Handle cancellation
//            finish();
//        });
//    }
//
//    private void startPaymentFlow(RentedVideoItem videoItem, int amount) {
//        new Thread(() -> {
//            try {
//                String response = createPaymentIntent(amount);
//                JSONObject jsonResponse = new JSONObject(response);
//                paymentIntentClientSecret = jsonResponse.getString("clientSecret");
//
//                String customerId = "customer_id_example";
//                String ephemeralKey = "ephemeral_key_example";
//
//                customerConfig = new PaymentSheet.CustomerConfiguration(customerId, ephemeralKey);
//
//                runOnUiThread(this::presentPaymentSheet);
//            } catch (Exception e) {
//                Log.e(TAG, "Failed to create PaymentIntent: " + e.getMessage(), e);
//                runOnUiThread(() -> Toast.makeText(StorePaymentActivity.this, "Failed to create PaymentIntent", Toast.LENGTH_SHORT).show());
//            }
//        }).start();
//    }
//
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
//
//        Scanner scanner = new Scanner(connection.getInputStream());
//        String response = scanner.useDelimiter("\\A").next();
//        Log.d(TAG, "PaymentIntent response: " + response);
//        return response;
//    }
//
//    private void presentPaymentSheet() {
//        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration("Example, Inc.", customerConfig);
//        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
//    }
//
//    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
//        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
//            unlockCategory(videoItem);
//        } else {
//            Log.e(TAG, "Payment failed: " + paymentSheetResult);
//            Toast.makeText(this, "Payment failed. Please try again.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void unlockCategory(RentedVideoItem videoItem) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            DatabaseReference rentedRef = FirebaseDatabase.getInstance().getReference("rented").child(user.getUid()).child(videoItem.getCategoryName());
//            rentedRef.setValue(true).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(StorePaymentActivity.this, "Category unlocked successfully", Toast.LENGTH_SHORT).show();
//                    Intent resultIntent = new Intent();
//                    resultIntent.putExtra("category", videoItem.getCategoryName());
//                    setResult(RESULT_OK, resultIntent);
//                    finish();
//                } else {
//                    Toast.makeText(StorePaymentActivity.this, "Failed to unlock category. Please try again.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//}
