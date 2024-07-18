package com.example.va;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {

    private FirebaseAuth auth;
    private TextView profileName;
    private ImageView profileIcon;
    private ImageView crownIcon;
    private Button logoutButton;
    private DatabaseReference subscriptionRef;
    private ViewPager2 viewPager;
    private SliderAdapter sliderAdapter;
    private Handler sliderHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions");

        profileName = findViewById(R.id.profile_name);
        profileIcon = findViewById(R.id.profile_icon);
        crownIcon = findViewById(R.id.crown_icon);
        logoutButton = findViewById(R.id.logout_button);
        viewPager = findViewById(R.id.viewPager);

        List<String> imageUrls = Arrays.asList(
                "https://firebasestorage.googleapis.com/v0/b/va123-5d836.appspot.com/o/reels%2Fthumbnails%2F1720540631338.jpg?alt=media&token=b51b3ac7-7562-4f00-b534-69aa26a87be4",
                "https://firebasestorage.googleapis.com/v0/b/va123-5d836.appspot.com/o/reels%2Fthumbnails%2F1720540613796.jpg?alt=media&token=112dfd91-5b4d-40d7-9a75-14bbb67db77c",
                "https://firebasestorage.googleapis.com/v0/b/va123-5d836.appspot.com/o/reels%2Fthumbnails%2F1720540595954.jpg?alt=media&token=acbdf9f8-e1c8-45e4-a50a-9e70adfb9c25",
                "https://firebasestorage.googleapis.com/v0/b/va123-5d836.appspot.com/o/reels%2Fthumbnails%2F1720540575550.jpg?alt=media&token=9ac3e063-5497-48f4-a468-df07d05c6e96"
        );

        sliderAdapter = new SliderAdapter(this, imageUrls);
        viewPager.setAdapter(sliderAdapter);

        // Disable user swipe
        viewPager.setUserInputEnabled(false);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        logoutButton.setOnClickListener(v -> {
            Intent f = new Intent(getApplicationContext(), ChatActivity.class);
            startActivity(f);
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(MainActivity.this, VideoListActivity.class));
                } else if (itemId == R.id.nav_store) {
                    startActivity(new Intent(MainActivity.this, RentVideoListActivity.class));
                } else if (itemId == R.id.nav_membership) {
                    checkSubscriptionStatus();
                } else if (itemId == R.id.nav_settings) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                } else {
                    return false;
                }
                return true;
            }
        });

        // Auto slide
        sliderHandler.postDelayed(sliderRunnable, 3000); // Slide every 3 seconds
    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager.getCurrentItem() < sliderAdapter.getItemCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                viewPager.setCurrentItem(0);
            }
            sliderHandler.postDelayed(this, 3000); // Slide every 3 seconds
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000); // Slide every 3 seconds
    }

    private void checkSubscriptionStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            subscriptionRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("subscribed").getValue(Boolean.class)) {
                        // User is subscribed, show crown icon
                        startActivity(new Intent(MainActivity.this, Decide.class));
                        crownIcon.setVisibility(View.VISIBLE);
                    } else {
                        // User is not subscribed, hide crown icon
                        crownIcon.setVisibility(View.GONE);
                        showSubscriptionDialog();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors
                }
            });
        }
    }

    private void showSubscriptionDialog() {
        SubscriptionDialogFragment dialog = new SubscriptionDialogFragment();
        dialog.show(getSupportFragmentManager(), "SubscriptionDialogFragment");
    }
}
