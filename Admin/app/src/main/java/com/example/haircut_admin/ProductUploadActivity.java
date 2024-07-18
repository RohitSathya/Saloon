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

public class ProductUploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private static final int PICK_SAMPLE_IMAGE1_REQUEST = 3;
    private static final int PICK_SAMPLE_IMAGE2_REQUEST = 4;
    private static final int PICK_SAMPLE_IMAGE3_REQUEST = 5;
    private static final String TAG = "ProductUploadActivity";

    private EditText editTextProductTitle, editTextProductPrice;
    private Button buttonChooseImage, buttonChooseSampleImage1, buttonChooseSampleImage2, buttonChooseSampleImage3, buttonChooseTrailer, buttonUpload;

    private Uri imageUri, trailerUri, sampleImage1Uri, sampleImage2Uri, sampleImage3Uri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_upload);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInAnonymously:success");
                FirebaseUser user = mAuth.getCurrentUser();
            } else {
                Log.w(TAG, "signInAnonymously:failure", task.getException());
                Toast.makeText(ProductUploadActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });

        initializeViews();

        storageReference = FirebaseStorage.getInstance().getReference("products");

        buttonChooseImage.setOnClickListener(v -> openFileChooser(PICK_IMAGE_REQUEST));
        buttonChooseSampleImage1.setOnClickListener(v -> openFileChooser(PICK_SAMPLE_IMAGE1_REQUEST));
        buttonChooseSampleImage2.setOnClickListener(v -> openFileChooser(PICK_SAMPLE_IMAGE2_REQUEST));
        buttonChooseSampleImage3.setOnClickListener(v -> openFileChooser(PICK_SAMPLE_IMAGE3_REQUEST));
        buttonChooseTrailer.setOnClickListener(v -> openFileChooser(PICK_VIDEO_REQUEST));
        buttonUpload.setOnClickListener(v -> uploadFile());
    }

    private void initializeViews() {
        editTextProductTitle = findViewById(R.id.edit_text_product_title);
        editTextProductPrice = findViewById(R.id.edit_text_product_price);
        buttonChooseImage = findViewById(R.id.button_choose_image);
        buttonChooseSampleImage1 = findViewById(R.id.button_choose_sample_image1);
        buttonChooseSampleImage2 = findViewById(R.id.button_choose_sample_image2);
        buttonChooseSampleImage3 = findViewById(R.id.button_choose_sample_image3);
        buttonChooseTrailer = findViewById(R.id.button_choose_trailer);
        buttonUpload = findViewById(R.id.button_upload);
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType(requestCode == PICK_VIDEO_REQUEST ? "video/*" : "image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            switch (requestCode) {
                case PICK_IMAGE_REQUEST:
                    imageUri = data.getData();
                    Log.d(TAG, "Image URI: " + imageUri.toString());
                    break;
                case PICK_VIDEO_REQUEST:
                    trailerUri = data.getData();
                    Log.d(TAG, "Trailer URI: " + trailerUri.toString());
                    break;
                case PICK_SAMPLE_IMAGE1_REQUEST:
                    sampleImage1Uri = data.getData();
                    Log.d(TAG, "Sample Image 1 URI: " + sampleImage1Uri.toString());
                    break;
                case PICK_SAMPLE_IMAGE2_REQUEST:
                    sampleImage2Uri = data.getData();
                    Log.d(TAG, "Sample Image 2 URI: " + sampleImage2Uri.toString());
                    break;
                case PICK_SAMPLE_IMAGE3_REQUEST:
                    sampleImage3Uri = data.getData();
                    Log.d(TAG, "Sample Image 3 URI: " + sampleImage3Uri.toString());
                    break;
            }
        }
    }

    private void uploadFile() {
        String productTitle = editTextProductTitle.getText().toString().trim();
        String productPrice = editTextProductPrice.getText().toString().trim();

        if (productTitle.isEmpty() || productPrice.isEmpty() || imageUri == null || trailerUri == null || sampleImage1Uri == null || sampleImage2Uri == null || sampleImage3Uri == null) {
            showWarningDialog("All fields are required");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("products");

        String productId = databaseReference.push().getKey();

        StorageReference imageReference = storageReference.child(productId + "/image");
        StorageReference trailerReference = storageReference.child(productId + "/trailer");
        StorageReference sampleImage1Reference = storageReference.child(productId + "/sample_image1");
        StorageReference sampleImage2Reference = storageReference.child(productId + "/sample_image2");
        StorageReference sampleImage3Reference = storageReference.child(productId + "/sample_image3");

        imageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    trailerReference.putFile(trailerUri)
                            .addOnSuccessListener(taskSnapshot1 -> trailerReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                String trailerUrl = uri1.toString();
                                sampleImage1Reference.putFile(sampleImage1Uri)
                                        .addOnSuccessListener(taskSnapshot2 -> sampleImage1Reference.getDownloadUrl().addOnSuccessListener(uri2 -> {
                                            String sampleImage1Url = uri2.toString();
                                            sampleImage2Reference.putFile(sampleImage2Uri)
                                                    .addOnSuccessListener(taskSnapshot3 -> sampleImage2Reference.getDownloadUrl().addOnSuccessListener(uri3 -> {
                                                        String sampleImage2Url = uri3.toString();
                                                        sampleImage3Reference.putFile(sampleImage3Uri)
                                                                .addOnSuccessListener(taskSnapshot4 -> sampleImage3Reference.getDownloadUrl().addOnSuccessListener(uri4 -> {
                                                                    String sampleImage3Url = uri4.toString();
                                                                    saveToDatabase(productId, productTitle, productPrice, imageUrl, trailerUrl, sampleImage1Url, sampleImage2Url, sampleImage3Url, progressDialog);
                                                                })).addOnFailureListener(e -> {
                                                                    Log.e(TAG, "Sample Image 3 upload failed: " + e.getMessage());
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(ProductUploadActivity.this, "Sample Image 3 upload failed", Toast.LENGTH_SHORT).show();
                                                                });
                                                    })).addOnFailureListener(e -> {
                                                        Log.e(TAG, "Sample Image 2 upload failed: " + e.getMessage());
                                                        progressDialog.dismiss();
                                                        Toast.makeText(ProductUploadActivity.this, "Sample Image 2 upload failed", Toast.LENGTH_SHORT).show();
                                                    });
                                        })).addOnFailureListener(e -> {
                                            Log.e(TAG, "Sample Image 1 upload failed: " + e.getMessage());
                                            progressDialog.dismiss();
                                            Toast.makeText(ProductUploadActivity.this, "Sample Image 1 upload failed", Toast.LENGTH_SHORT).show();
                                        });
                            })).addOnFailureListener(e -> {
                                Log.e(TAG, "Trailer upload failed: " + e.getMessage());
                                progressDialog.dismiss();
                                Toast.makeText(ProductUploadActivity.this, "Trailer upload failed", Toast.LENGTH_SHORT).show();
                            });
                })).addOnFailureListener(e -> {
                    Log.e(TAG, "Image upload failed: " + e.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(ProductUploadActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToDatabase(String productId, String productTitle, String productPrice, String imageUrl, String trailerUrl, String sampleImage1Url, String sampleImage2Url, String sampleImage3Url, ProgressDialog progressDialog) {
        Product product = new Product(productId, productTitle, Integer.parseInt(productPrice), imageUrl, trailerUrl, sampleImage1Url, sampleImage2Url, sampleImage3Url);
        databaseReference.child(productId).setValue(product)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Database entry created successfully.");
                    progressDialog.dismiss();
                    Toast.makeText(ProductUploadActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create database entry: " + e.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(ProductUploadActivity.this, "Failed to create database entry", Toast.LENGTH_SHORT).show();
                });
    }

    private void showWarningDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public static class Product {
        public String id;
        public String productTitle;
        public int productPrice;
        public String imageUrl;
        public String trailerUrl;
        public String sampleImage1Url;
        public String sampleImage2Url;
        public String sampleImage3Url;

        public Product() {
            // Default constructor required for calls to DataSnapshot.getValue(Product.class)
        }

        public Product(String id, String productTitle, int productPrice, String imageUrl, String trailerUrl, String sampleImage1Url, String sampleImage2Url, String sampleImage3Url) {
            this.id = id;
            this.productTitle = productTitle;
            this.productPrice = productPrice;
            this.imageUrl = imageUrl;
            this.trailerUrl = trailerUrl;
            this.sampleImage1Url = sampleImage1Url;
            this.sampleImage2Url = sampleImage2Url;
            this.sampleImage3Url = sampleImage3Url;
        }
    }
}
