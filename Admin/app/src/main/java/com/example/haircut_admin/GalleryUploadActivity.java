package com.example.haircut_admin;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class GalleryUploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = "GalleryUploadActivity";

    private Button buttonChooseImage;
    private Button buttonUpload;
    private Button buttonNavigateToUpload;

    private EditText editTextTitle;

    private Uri imageUri;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_upload);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInAnonymously:success");
                FirebaseUser user = mAuth.getCurrentUser();
            } else {
                Log.w(TAG, "signInAnonymously:failure", task.getException());
                Toast.makeText(GalleryUploadActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });

        buttonChooseImage = findViewById(R.id.button_choose_image);
        buttonUpload = findViewById(R.id.button_upload);
        buttonNavigateToUpload = findViewById(R.id.button_navigate_to_upload);

        editTextTitle = findViewById(R.id.edit_text_title);

        storageReference = FirebaseStorage.getInstance().getReference("gallery");

        buttonChooseImage.setOnClickListener(v -> openFileChooser());

        buttonUpload.setOnClickListener(v -> uploadFile());

        buttonNavigateToUpload.setOnClickListener(v -> {
            Intent intent = new Intent(GalleryUploadActivity.this, MainActivity.class);
            startActivity(intent);
        });


    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Log.d(TAG, "Image URI: " + imageUri.toString());
        }
    }

    private void uploadFile() {
        String title = editTextTitle.getText().toString().trim();
        if (title.isEmpty()) {
            showWarningDialog("Title is required");
            return;
        }

        if (imageUri == null) {
            showWarningDialog("Image is required");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        String imagePath = "images/" + System.currentTimeMillis() + getFileExtension(imageUri);

        StorageReference imageReference = storageReference.child(imagePath);

        Log.d(TAG, "Image Path: " + imagePath);

        imageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageReference.getDownloadUrl().addOnSuccessListener(imageUri -> {
                    Log.d(TAG, "Image uploaded successfully. URI: " + imageUri.toString());

                    databaseReference = FirebaseDatabase.getInstance().getReference("gallery");
                    GalleryImage galleryImage = new GalleryImage(imageUri.toString(), title);
                    String uploadId = databaseReference.push().getKey();
                    databaseReference.child(uploadId).setValue(galleryImage)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Database entry created successfully.");
                                progressDialog.dismiss();
                                Toast.makeText(GalleryUploadActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to create database entry: " + e.getMessage());
                                progressDialog.dismiss();
                                Toast.makeText(GalleryUploadActivity.this, "Failed to create database entry", Toast.LENGTH_SHORT).show();
                            });
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get image download URL: " + e.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(GalleryUploadActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed: " + e.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(GalleryUploadActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void showWarningDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private String getFileExtension(Uri uri) {
        String mimeType = getContentResolver().getType(uri);
        if (mimeType == null) {
            return "";
        }
        String[] parts = mimeType.split("/");
        return "." + parts[1];
    }

    public static class GalleryImage {
        public String imageUrl;
        public String title;

        public GalleryImage() {
            // Default constructor required for calls to DataSnapshot.getValue(GalleryImage.class)
        }

        public GalleryImage(String imageUrl, String title) {
            this.imageUrl = imageUrl;
            this.title = title;
        }
    }
}
