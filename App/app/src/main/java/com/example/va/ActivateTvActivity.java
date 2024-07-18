package com.example.va;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivateTvActivity extends AppCompatActivity {

    private EditText activationCodeEditText;
    private Button activateButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_tv);

        activationCodeEditText = findViewById(R.id.activation_code_edit_text);
        activateButton = findViewById(R.id.activate_button);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("activate_tv");

        // Set input filter to allow only 6 digits
        activationCodeEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        activationCodeEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activationCode = activationCodeEditText.getText().toString();
                if (activationCode.length() == 6) {
                    saveActivationCode(activationCode);
                } else {
                    Toast.makeText(ActivateTvActivity.this, "Please enter a valid 6-digit code", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveActivationCode(String code) {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        databaseReference.child(userId).setValue(code).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ActivateTvActivity.this, "TV activated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ActivateTvActivity.this, "Failed to activate TV", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
