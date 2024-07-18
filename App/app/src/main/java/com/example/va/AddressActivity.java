package com.example.va;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddressActivity extends BaseActivity {

    private EditText editTextFullName;
    private EditText editTextMobileNumber;
    private EditText editTextFlatHouse;
    private EditText editTextAreaStreet;
    private EditText editTextLandmark;
    private EditText editTextPincode;
    private EditText editTextTownCity;
    private EditText editTextState;
    private Button buttonSaveAddress;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);
        editTextFlatHouse = findViewById(R.id.editTextFlatHouse);
        editTextAreaStreet = findViewById(R.id.editTextAreaStreet);
        editTextLandmark = findViewById(R.id.editTextLandmark);
        editTextPincode = findViewById(R.id.editTextPincode);
        editTextTownCity = findViewById(R.id.editTextTownCity);
        editTextState = findViewById(R.id.editTextState);
        buttonSaveAddress = findViewById(R.id.buttonSaveAddress);

        buttonSaveAddress.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String fullName = editTextFullName.getText().toString().trim();
        String mobileNumber = editTextMobileNumber.getText().toString().trim();
        String flatHouse = editTextFlatHouse.getText().toString().trim();
        String areaStreet = editTextAreaStreet.getText().toString().trim();
        String landmark = editTextLandmark.getText().toString().trim();
        String pincode = editTextPincode.getText().toString().trim();
        String townCity = editTextTownCity.getText().toString().trim();
        String state = editTextState.getText().toString().trim();

        if (fullName.isEmpty() || mobileNumber.isEmpty() || flatHouse.isEmpty() || areaStreet.isEmpty() ||
                pincode.isEmpty() || townCity.isEmpty() || state.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            Address address = new Address(fullName, mobileNumber, flatHouse, areaStreet, landmark, pincode, townCity, state);
            databaseReference.child("addresses").child(userId).setValue(address).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddressActivity.this, "Address saved successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddressActivity.this, "Failed to save address", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
