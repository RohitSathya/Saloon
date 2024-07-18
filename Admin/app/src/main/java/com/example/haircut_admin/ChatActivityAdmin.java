package com.example.haircut_admin;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivityAdmin extends AppCompatActivity {

    private static final int PICK_MEDIA_REQUEST = 1;
    private RecyclerView recyclerView;
    private ChatAdapterAdmin chatAdapterAdmin;
    private List<ChatMessage> chatList;
    private DatabaseReference databaseReference;
    private EditText editTextMessage;
    private Button buttonSend;
    private ImageButton buttonAttach;
    private String userId;
    private Uri mediaUri;
    private String mediaType;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_admin);

        userId = getIntent().getStringExtra("userId");

        recyclerView = findViewById(R.id.recycler_view_chat_admin);
        editTextMessage = findViewById(R.id.edit_text_message_admin);
        buttonSend = findViewById(R.id.button_send_admin);
        buttonAttach = findViewById(R.id.button_attach_admin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatList = new ArrayList<>();
        chatAdapterAdmin = new ChatAdapterAdmin(this, chatList);
        recyclerView.setAdapter(chatAdapterAdmin);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadMessages();

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        buttonAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void loadMessages() {
        DatabaseReference chatRef = databaseReference.child("chat").child(userId);

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isAtBottom = isRecyclerViewAtBottom();

                chatList.clear(); // Clear the list before adding new messages
                for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                    ChatMessage message = chatSnapshot.getValue(ChatMessage.class);

                    if (message != null) {
                        chatList.add(message);
                    }
                }
                chatAdapterAdmin.notifyDataSetChanged();

                if (isAtBottom) {
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.scrollToPosition(chatList.size() - 1); // Scroll to the latest message
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivityAdmin.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isRecyclerViewAtBottom() {
        if (recyclerView != null && recyclerView.getAdapter() != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (layoutManager != null) {
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                return lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
            }
        }
        return false;
    }

    private void sendMessage() {
        String message = editTextMessage.getText().toString().trim();

        if (!TextUtils.isEmpty(message) || mediaUri != null) {
            if (mediaUri != null) {
                uploadMedia(message);
            } else {
                saveMessage(message, null, "text");
            }
        } else {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Media"), PICK_MEDIA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            mediaUri = data.getData();
            mediaType = getContentResolver().getType(mediaUri);

            if (mediaType != null) {
                if (mediaType.startsWith("image")) {
                    mediaType = "image";
                } else if (mediaType.startsWith("video")) {
                    mediaType = "video";
                }
            }


            uploadMedia("");
        }
    }

    private void uploadMedia(String message) {
        if (mediaUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chat_media");
            final StorageReference mediaRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(mediaUri));

            mediaRef.putFile(mediaUri).addOnSuccessListener(taskSnapshot -> mediaRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveMessage(message, uri.toString(), mediaType);
                    }).addOnFailureListener(e -> Toast.makeText(ChatActivityAdmin.this, "Failed to upload media", Toast.LENGTH_SHORT).show()))
                    .addOnProgressListener(taskSnapshot -> {
                        // Show progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        // You can use a ProgressBar to show the progress
                        // Example: progressBar.setProgress((int) progress);
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveMessage(String message, String mediaUrl, String messageType) {
        DatabaseReference chatRef = databaseReference.child("chat").child(userId).push();
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("name", "Admin");
        messageData.put("message", message);
        messageData.put("mediaUrl", mediaUrl);
        messageData.put("mediaType", messageType);
        messageData.put("messageType", messageType != null ? messageType : "text");
        chatRef.setValue(messageData);

        editTextMessage.setText("");
        mediaUri = null;
        mediaType = null;
    }
}
