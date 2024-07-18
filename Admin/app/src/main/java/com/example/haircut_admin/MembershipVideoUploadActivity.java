package com.example.haircut_admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MembershipVideoUploadActivity extends AppCompatActivity {

    private static final int PICK_THUMBNAIL_REQUEST = 1;
    private static final int PICK_CHANNEL_LOGO_REQUEST = 2;
    private static final int PICK_SERIES_THUMBNAIL_REQUEST = 4;
    private static final int PICK_TRAILER_REQUEST = 5;
    private static final int PICK_SAMPLE_IMAGE1_REQUEST = 6;
    private static final int PICK_SAMPLE_IMAGE2_REQUEST = 7;
    private static final int PICK_SAMPLE_IMAGE3_REQUEST = 8;
    private static final String TAG = "MembershipVideoUpload";

    private Spinner spinnerVideoType, spinnerContentType, spinnerGenre, spinnerSource, spinnerChannelName, spinnerCategories, spinnerLanguageType;
    private EditText editTextVideoId, editTextVideoLink, editTextVideoName, editTextThumbnailLink, editTextEpisodeNumber, editTextVideoPrice;
    private Button buttonChooseThumbnail, buttonChooseChannelLogo, buttonChooseSeriesThumbnail, buttonUpload, buttonUploadTrailer;
    private Button buttonUploadSampleImage1, buttonUploadSampleImage2, buttonUploadSampleImage3;

    private Uri thumbnailUri, channelLogoUri, seriesThumbnailUri, trailerUri;
    private Uri sampleImage1Uri, sampleImage2Uri, sampleImage3Uri;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_video_upload);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInAnonymously:success");
                FirebaseUser user = mAuth.getCurrentUser();
            } else {
                Log.w(TAG, "signInAnonymously:failure", task.getException());
                Toast.makeText(MembershipVideoUploadActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
            }
        });

        initializeViews();
        setupSpinners();

        storageReference = FirebaseStorage.getInstance().getReference();

        buttonChooseThumbnail.setOnClickListener(v -> openFileChooser(PICK_THUMBNAIL_REQUEST));
        buttonChooseChannelLogo.setOnClickListener(v -> openFileChooser(PICK_CHANNEL_LOGO_REQUEST));
        buttonChooseSeriesThumbnail.setOnClickListener(v -> openFileChooser(PICK_SERIES_THUMBNAIL_REQUEST));
        buttonUploadTrailer.setOnClickListener(v -> openVideoChooser(PICK_TRAILER_REQUEST));
        buttonUpload.setOnClickListener(v -> uploadFile());

        buttonUploadSampleImage1.setOnClickListener(v -> openFileChooser(PICK_SAMPLE_IMAGE1_REQUEST));
        buttonUploadSampleImage2.setOnClickListener(v -> openFileChooser(PICK_SAMPLE_IMAGE2_REQUEST));
        buttonUploadSampleImage3.setOnClickListener(v -> openFileChooser(PICK_SAMPLE_IMAGE3_REQUEST));

        spinnerContentType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if ("Series".equals(selectedItem)) {
                    editTextEpisodeNumber.setVisibility(View.VISIBLE);
                    buttonChooseSeriesThumbnail.setVisibility(View.VISIBLE);
                } else {
                    editTextEpisodeNumber.setVisibility(View.GONE);
                    buttonChooseSeriesThumbnail.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerVideoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if ("Rent".equals(selectedItem)) {
                    editTextVideoPrice.setVisibility(View.VISIBLE);
                    spinnerLanguageType.setVisibility(View.VISIBLE);
                    buttonUploadTrailer.setVisibility(View.VISIBLE);
                    buttonUploadSampleImage1.setVisibility(View.VISIBLE);
                    buttonUploadSampleImage2.setVisibility(View.VISIBLE);
                    buttonUploadSampleImage3.setVisibility(View.VISIBLE);
                } else {
                    editTextVideoPrice.setVisibility(View.GONE);
                    spinnerLanguageType.setVisibility(View.GONE);
                    buttonUploadTrailer.setVisibility(View.GONE);
                    buttonUploadSampleImage1.setVisibility(View.GONE);
                    buttonUploadSampleImage2.setVisibility(View.GONE);
                    buttonUploadSampleImage3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initializeViews() {
        spinnerVideoType = findViewById(R.id.spinner_video_type);
        spinnerContentType = findViewById(R.id.spinner_content_type);
        spinnerGenre = findViewById(R.id.spinner_genre);
        spinnerSource = findViewById(R.id.spinner_source);
        spinnerChannelName = findViewById(R.id.spinner_channel_name);
        spinnerCategories = findViewById(R.id.spinner_categories);
        spinnerLanguageType = findViewById(R.id.spinner_language_type);
        editTextVideoId = findViewById(R.id.edit_text_video_id);
        editTextVideoLink = findViewById(R.id.edit_text_video_link);
        editTextVideoName = findViewById(R.id.edit_text_video_name);
        editTextEpisodeNumber = findViewById(R.id.edit_text_episode_number);
        editTextThumbnailLink = findViewById(R.id.edit_text_thumbnail_link);
        editTextVideoPrice = findViewById(R.id.edit_text_video_price);
        buttonChooseThumbnail = findViewById(R.id.button_choose_thumbnail);
        buttonChooseChannelLogo = findViewById(R.id.button_choose_channel_logo);
        buttonChooseSeriesThumbnail = findViewById(R.id.button_choose_series_thumbnail);
        buttonUploadTrailer = findViewById(R.id.button_upload_trailer);
        buttonUpload = findViewById(R.id.button_upload);
        buttonUploadSampleImage1 = findViewById(R.id.button_upload_sample_image1);
        buttonUploadSampleImage2 = findViewById(R.id.button_upload_sample_image2);
        buttonUploadSampleImage3 = findViewById(R.id.button_upload_sample_image3);
    }

    private void setupSpinners() {
        setupSpinner(spinnerVideoType, R.array.video_types);
        setupSpinner(spinnerContentType, R.array.content_types);
        setupSpinner(spinnerGenre, R.array.genres);
        setupSpinner(spinnerSource, R.array.sources);
        setupSpinner(spinnerChannelName, R.array.channel_names);
        setupSpinner(spinnerCategories, R.array.categories);
        setupSpinner(spinnerLanguageType, R.array.languages);
    }

    private void setupSpinner(Spinner spinner, int arrayResource) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResource, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void openFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    private void openVideoChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_THUMBNAIL_REQUEST) {
                thumbnailUri = data.getData();
                Log.d(TAG, "Thumbnail URI: " + thumbnailUri.toString());
            } else if (requestCode == PICK_CHANNEL_LOGO_REQUEST) {
                channelLogoUri = data.getData();
                Log.d(TAG, "Channel Logo URI: " + channelLogoUri.toString());
            } else if (requestCode == PICK_SERIES_THUMBNAIL_REQUEST) {
                seriesThumbnailUri = data.getData();
                Log.d(TAG, "Series Thumbnail URI: " + seriesThumbnailUri.toString());
            } else if (requestCode == PICK_TRAILER_REQUEST) {
                trailerUri = data.getData();
                Log.d(TAG, "Trailer URI: " + trailerUri.toString());
            } else if (requestCode == PICK_SAMPLE_IMAGE1_REQUEST) {
                sampleImage1Uri = data.getData();
                Log.d(TAG, "Sample Image 1 URI: " + sampleImage1Uri.toString());
            } else if (requestCode == PICK_SAMPLE_IMAGE2_REQUEST) {
                sampleImage2Uri = data.getData();
                Log.d(TAG, "Sample Image 2 URI: " + sampleImage2Uri.toString());
            } else if (requestCode == PICK_SAMPLE_IMAGE3_REQUEST) {
                sampleImage3Uri = data.getData();
                Log.d(TAG, "Sample Image 3 URI: " + sampleImage3Uri.toString());
            }
        }
    }

    private void uploadFile() {
        String videoType = spinnerVideoType.getSelectedItem().toString();
        String contentType = spinnerContentType.getSelectedItem().toString();
        String genre = spinnerGenre.getSelectedItem().toString();
        String source = spinnerSource.getSelectedItem().toString();
        String channelName = spinnerChannelName.getSelectedItem().toString();
        String categories = spinnerCategories.getSelectedItem().toString();
        String videoId = editTextVideoId.getText().toString().trim();
        String videoLink = editTextVideoLink.getText().toString().trim();
        String videoName = editTextVideoName.getText().toString().trim();
        String episodeNumber = editTextEpisodeNumber.getText().toString().trim();
        String thumbnailLink = editTextThumbnailLink.getText().toString().trim();
        String videoPrice = editTextVideoPrice.getText().toString().trim();
        String languageType = spinnerLanguageType.getSelectedItem().toString();

        if (videoId.isEmpty() || videoLink.isEmpty() || videoName.isEmpty() || thumbnailUri == null || channelLogoUri == null || (contentType.equals("Series") && seriesThumbnailUri == null) || (videoType.equals("Rent") && trailerUri == null)) {
            showWarningDialog("All fields are required");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        databaseReference = FirebaseDatabase.getInstance().getReference("membership");

        databaseReference.child(videoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressDialog.dismiss();
                    showWarningDialog("Video ID already exists. Please use a unique ID.");
                } else {
                    uploadImage(thumbnailUri, "thumbnails", new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String thumbnailLink) {
                            uploadImage(channelLogoUri, "channel_logos", new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String channelLogoLink) {
                                    if (contentType.equals("Series")) {
                                        uploadImage(seriesThumbnailUri, "series_thumbnails", new OnSuccessListener<String>() {
                                            @Override
                                            public void onSuccess(String seriesThumbnailLink) {
                                                if (videoType.equals("Rent")) {
                                                    uploadVideo(trailerUri, "trailers", new OnSuccessListener<String>() {
                                                        @Override
                                                        public void onSuccess(String trailerLink) {
                                                            uploadImage(sampleImage1Uri, "sample_images", new OnSuccessListener<String>() {
                                                                @Override
                                                                public void onSuccess(String sampleImage1Link) {
                                                                    uploadImage(sampleImage2Uri, "sample_images", new OnSuccessListener<String>() {
                                                                        @Override
                                                                        public void onSuccess(String sampleImage2Link) {
                                                                            uploadImage(sampleImage3Uri, "sample_images", new OnSuccessListener<String>() {
                                                                                @Override
                                                                                public void onSuccess(String sampleImage3Link) {
                                                                                    saveToDatabase(videoType, contentType, genre, source, channelName, categories, videoId, videoLink, videoName, episodeNumber, thumbnailLink, channelLogoLink, seriesThumbnailLink, trailerLink, videoPrice, languageType, sampleImage1Link, sampleImage2Link, sampleImage3Link, progressDialog);
                                                                                }
                                                                            });
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                } else {
                                                    saveToDatabase(videoType, contentType, genre, source, channelName, categories, videoId, videoLink, videoName, episodeNumber, thumbnailLink, channelLogoLink, seriesThumbnailLink, "", videoPrice, languageType, "", "", "", progressDialog);
                                                }
                                            }
                                        });
                                    } else {
                                        if (videoType.equals("Rent")) {
                                            uploadVideo(trailerUri, "trailers", new OnSuccessListener<String>() {
                                                @Override
                                                public void onSuccess(String trailerLink) {
                                                    uploadImage(sampleImage1Uri, "sample_images", new OnSuccessListener<String>() {
                                                        @Override
                                                        public void onSuccess(String sampleImage1Link) {
                                                            uploadImage(sampleImage2Uri, "sample_images", new OnSuccessListener<String>() {
                                                                @Override
                                                                public void onSuccess(String sampleImage2Link) {
                                                                    uploadImage(sampleImage3Uri, "sample_images", new OnSuccessListener<String>() {
                                                                        @Override
                                                                        public void onSuccess(String sampleImage3Link) {
                                                                            saveToDatabase(videoType, contentType, genre, source, channelName, categories, videoId, videoLink, videoName, episodeNumber, thumbnailLink, channelLogoLink, "", trailerLink, videoPrice, languageType, sampleImage1Link, sampleImage2Link, sampleImage3Link, progressDialog);
                                                                        }
                                                                    });
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            saveToDatabase(videoType, contentType, genre, source, channelName, categories, videoId, videoLink, videoName, episodeNumber, thumbnailLink, channelLogoLink, "", "", videoPrice, languageType, "", "", "", progressDialog);
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(MembershipVideoUploadActivity.this, "Error checking Video ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(Uri uri, String folder, OnSuccessListener<String> onSuccessListener) {
        if (uri == null) {
            onSuccessListener.onSuccess("");
            return;
        }
        String path = folder + "/" + System.currentTimeMillis() + getFileExtension(uri);
        StorageReference reference = storageReference.child(path);

        reference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri1 -> onSuccessListener.onSuccess(uri1.toString()))
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                            Toast.makeText(MembershipVideoUploadActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Upload failed: " + e.getMessage());
                    Toast.makeText(MembershipVideoUploadActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadVideo(Uri uri, String folder, OnSuccessListener<String> onSuccessListener) {
        if (uri == null) {
            onSuccessListener.onSuccess("");
            return;
        }
        String path = folder + "/" + System.currentTimeMillis() + getFileExtension(uri);
        StorageReference reference = storageReference.child(path);

        reference.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri1 -> onSuccessListener.onSuccess(uri1.toString()))
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                            Toast.makeText(MembershipVideoUploadActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                        }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Upload failed: " + e.getMessage());
                    Toast.makeText(MembershipVideoUploadActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveToDatabase(String videoType, String contentType, String genre, String source, String channelName, String categories, String videoId, String videoLink, String videoName, String episodeNumber, String thumbnailLink, String channelLogoLink, String seriesThumbnailLink, String trailerLink, String videoPrice, String languageType, String sampleImage1Link, String sampleImage2Link, String sampleImage3Link, ProgressDialog progressDialog) {
        MembershipVideo membershipVideo = new MembershipVideo(videoType, contentType, genre, source, channelName, categories, videoId, videoLink, videoName, episodeNumber, thumbnailLink, channelLogoLink, seriesThumbnailLink, trailerLink, videoPrice, languageType, sampleImage1Link, sampleImage2Link, sampleImage3Link);
        databaseReference.child(videoId).setValue(membershipVideo)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Database entry created successfully.");
                    progressDialog.dismiss();
                    Toast.makeText(MembershipVideoUploadActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to create database entry: " + e.getMessage());
                    progressDialog.dismiss();
                    Toast.makeText(MembershipVideoUploadActivity.this, "Failed to create database entry", Toast.LENGTH_SHORT).show();
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

    public static class MembershipVideo {
        public String videoType;
        public String contentType;
        public String genre;
        public String source;
        public String channelName;
        public String categories;
        public String videoId;
        public String videoLink;
        public String videoName;
        public String episodeNumber;
        public String thumbnailLink;
        public String seriesThumbnailLink;
        public String channelLogoLink;
        public String trailerLink;
        public String videoPrice;
        public String languageType;
        public String sampleImage1Link;
        public String sampleImage2Link;
        public String sampleImage3Link;

        public MembershipVideo() {
            // Default constructor required for calls to DataSnapshot.getValue(MembershipVideo.class)
        }

        public MembershipVideo(String videoType, String contentType, String genre, String source, String channelName, String categories, String videoId, String videoLink, String videoName, String episodeNumber, String thumbnailLink, String channelLogoLink, String seriesThumbnailLink, String trailerLink, String videoPrice, String languageType, String sampleImage1Link, String sampleImage2Link, String sampleImage3Link) {
            this.videoType = videoType;
            this.contentType = contentType;
            this.genre = genre;
            this.source = source;
            this.channelName = channelName;
            this.categories = categories;
            this.videoId = videoId;
            this.videoLink = videoLink;
            this.videoName = videoName;
            this.episodeNumber = episodeNumber;
            this.thumbnailLink = thumbnailLink;
            this.channelLogoLink = channelLogoLink;
            this.seriesThumbnailLink = seriesThumbnailLink;
            this.trailerLink = trailerLink;
            this.videoPrice = videoPrice;
            this.languageType = languageType;
            this.sampleImage1Link = sampleImage1Link;
            this.sampleImage2Link = sampleImage2Link;
            this.sampleImage3Link = sampleImage3Link;
        }
    }
}
