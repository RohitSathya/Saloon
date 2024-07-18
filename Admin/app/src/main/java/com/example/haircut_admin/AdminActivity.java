package com.example.haircut_admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ListView listViewUsers;
    private List<User> userList;
    private DatabaseReference chatReference;
    private DatabaseReference usersReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listViewUsers = findViewById(R.id.list_view_users);
        userList = new ArrayList<>();
        chatReference = FirebaseDatabase.getInstance().getReference().child("chat");
        usersReference = FirebaseDatabase.getInstance().getReference().child("users");

        loadUsers();

        listViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User selectedUser = userList.get(position);
                Intent intent = new Intent(AdminActivity.this, ChatActivityAdmin.class);
                intent.putExtra("userId", selectedUser.getUserId()); // Pass user ID instead of email
                startActivity(intent);
            }
        });
    }

    private void loadUsers() {
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    String chatId = chatSnapshot.getKey();
                    if (chatId != null) {
                        usersReference.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                User user = userSnapshot.getValue(User.class);
                                if (user != null) {
                                    user.setUserId(chatId); // Set the user ID
                                    userList.add(user);
                                }
                                UserListAdapter adapter = new UserListAdapter(AdminActivity.this, userList);
                                listViewUsers.setAdapter(adapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}
