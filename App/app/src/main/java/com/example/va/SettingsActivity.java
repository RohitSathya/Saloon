package com.example.va;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SettingsAdapter settingsAdapter;
    private List<SettingItem> settingItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        recyclerView = findViewById(R.id.recycler_view_settings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        settingItemList = new ArrayList<>();
        settingItemList.add(new SettingItem("Account Details", "View and update your account details"));
        settingItemList.add(new SettingItem("Shop", "Browse the shop"));
        settingItemList.add(new SettingItem("PurchaseRent", "Purchase or rent videos"));
        settingItemList.add(new SettingItem("PurchaseMemberShip", "Purchase a membership"));
        settingItemList.add(new SettingItem("Download", "Download videos"));
        settingItemList.add(new SettingItem("WatchList", "View your watchlist"));
        settingItemList.add(new SettingItem("Transactions", "View your transactions"));
        settingItemList.add(new SettingItem("Terms&Conditions", "View terms and conditions"));
        settingItemList.add(new SettingItem("Logout", ""));

        settingsAdapter = new SettingsAdapter(this, settingItemList, getSupportFragmentManager());
        recyclerView.setAdapter(settingsAdapter);
    }
}
