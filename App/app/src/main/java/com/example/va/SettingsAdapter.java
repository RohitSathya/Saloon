package com.example.va;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    private final Context context;
    private final List<SettingItem> settingItemList;
    private final FragmentManager fragmentManager;
    private FirebaseAuth auth;
    private DatabaseReference subscriptionRef;
    private DatabaseReference transactionsRef;

    public SettingsAdapter(Context context, List<SettingItem> settingItemList, FragmentManager fragmentManager) {
        this.context = context;
        this.settingItemList = settingItemList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_setting, parent, false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        SettingItem settingItem = settingItemList.get(position);
        holder.title.setText(settingItem.getTitle());
        holder.description.setText(settingItem.getDescription());
        auth = FirebaseAuth.getInstance();
        subscriptionRef = FirebaseDatabase.getInstance().getReference("subscriptions");
        transactionsRef = FirebaseDatabase.getInstance().getReference("transactions");

        holder.itemView.setOnClickListener(v -> {
            switch (settingItem.getTitle()) {
                case "Account Details":
                    context.startActivity(new Intent(context, AddressActivity.class));
                    break;
                case "Shop":
                    context.startActivity(new Intent(context, ShopActivity.class));
                    break;
                case "PurchaseRent":
                    context.startActivity(new Intent(context, RentVideoListActivity.class));
                    break;
                case "PurchaseMemberShip":
                    checkSubscriptionStatus();
                    break;
                case "Download":
                    context.startActivity(new Intent(context, DownloadActivity.class));
                    break;
                case "WatchList":
                    context.startActivity(new Intent(context, WatchlistActivity.class));
                    break;
                case "Transactions":
                    context.startActivity(new Intent(context, TransactionActivity.class));
                    break;
                case "Terms&Conditions":
                    context.startActivity(new Intent(context, TermsActivity.class));
                    break;
                case "Logout":
                    auth.signOut();
                    context.startActivity(new Intent(context,SplashActivity.class));
                    break;
                default:
                    context.startActivity(new Intent(context, SettingsActivity.class));
                    break;
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingItemList.size();
    }

    public static class SettingsViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;

        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.setting_title);
            description = itemView.findViewById(R.id.setting_description);
        }
    }

    private void checkSubscriptionStatus() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            subscriptionRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("subscribed").getValue(Boolean.class)) {
                        showSuccessfulMembershipDialog();
                    } else {
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

    private void checkSubscriptionForTransactionActivity() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            subscriptionRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists() && snapshot.child("subscribed").getValue(Boolean.class)) {
                        createTransactionTable(user.getUid());
                    } else {
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

    private void createTransactionTable(String userId) {
        DatabaseReference userTransactionsRef = transactionsRef.child(userId);
        userTransactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Map<String, Object> transactionData = new HashMap<>();
                    transactionData.put("sno1/amount", "1800");
                    transactionData.put("sno1/plan", "Pro");
                    userTransactionsRef.updateChildren(transactionData);
                }
                navigateToTransactionActivity();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private void navigateToTransactionActivity() {
        Intent intent = new Intent(context, TransactionActivity.class);
        context.startActivity(intent);
    }

    private void showSuccessfulMembershipDialog() {
        SuccessfulMembershipDialogFragment dialog = new SuccessfulMembershipDialogFragment();
        dialog.show(fragmentManager, "SuccessfulMembershipDialogFragment");
    }

    private void showSubscriptionDialog() {
        SubscriptionDialogFragment dialog = new SubscriptionDialogFragment();
        dialog.show(fragmentManager, "SubscriptionDialogFragment");
    }
}
