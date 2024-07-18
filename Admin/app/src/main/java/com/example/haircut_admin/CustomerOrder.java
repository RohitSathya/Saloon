package com.example.haircut_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerOrder extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList;
    private Set<String> userIds;
    private DatabaseReference ordersRef, usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userIds = new HashSet<>();
        adapter = new UserAdapter(userList, this::navigateToUserOrders);
        recyclerView.setAdapter(adapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("orders");
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        loadUsers();
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userIds.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    if (!userIds.contains(userId)) {
                        userIds.add(userId);  // Add the userId to the set to prevent duplicates
                        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String userName = snapshot.child("name").getValue(String.class);
                                String email = snapshot.child("email").getValue(String.class);
                                userList.add(new User(userId, email, userName));
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void navigateToUserOrders(User user) {
        Intent intent = new Intent(this, UserOrdersActivity.class);
        intent.putExtra("userId", user.getUserId());
        startActivity(intent);
    }

    private static class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

        private final List<User> userList;
        private final OnItemClickListener listener;

        public UserAdapter(List<User> userList, OnItemClickListener listener) {
            this.userList = userList;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            User user = userList.get(position);
            holder.bind(user, listener);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }

        public interface OnItemClickListener {
            void onItemClick(User user);
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textViewUserName;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewUserName = itemView.findViewById(R.id.user_name);
            }

            public void bind(User user, OnItemClickListener listener) {
                textViewUserName.setText(user.getName());
                itemView.setOnClickListener(v -> listener.onItemClick(user));
            }
        }
    }
}
