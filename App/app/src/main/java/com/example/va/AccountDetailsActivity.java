package com.example.va;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AccountDetailsActivity extends BaseActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        findViewById(R.id.btn_change_name).setOnClickListener(v -> changeName());
        findViewById(R.id.btn_delete_account).setOnClickListener(v -> deleteAccount());
    }

    private void changeName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change Name");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newName = input.getText().toString();
            if (!newName.isEmpty()) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    databaseReference.child(uid).child("name").setValue(newName)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(AccountDetailsActivity.this, "Name changed successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AccountDetailsActivity.this, "Failed to change name", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteAccount() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String uid = user.getUid();
                        user.delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                databaseReference.child(uid).removeValue()
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                auth.signOut();
                                                Intent intent = new Intent(AccountDetailsActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(AccountDetailsActivity.this, "Failed to delete user data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(AccountDetailsActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}
