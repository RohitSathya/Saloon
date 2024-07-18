package com.example.va;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SubscriptionActivity extends BaseActivity {

    private TextView currentPlanTextView;
    private DatabaseReference subscriptionRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        currentPlanTextView = findViewById(R.id.current_plan_text_view);
        firebaseAuth = FirebaseAuth.getInstance();

        // Set window insets for edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkSubscription();

        // Show the subscription dialog
        SubscriptionDialogFragment dialog = SubscriptionDialogFragment.newInstance(null);
        dialog.show(getSupportFragmentManager(), "SubscriptionDialogFragment");
    }

    private void checkSubscription() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions").child(user.getUid());
            subscriptionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("subscribed").getValue(Boolean.class)) {
                        currentPlanTextView.setText("Current Plan: Subscribed");
                    } else {
                        currentPlanTextView.setText("Current Plan: Free");
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(SubscriptionActivity.this, "Failed to check subscription status", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            currentPlanTextView.setText("Current Plan: Free");
        }
    }
}
