package com.example.va;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final int PICK_MEDIA_REQUEST = 1;

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private EditText editTextMessage;
    private Button buttonSend;
    private ImageButton buttonAttach;
    private Uri mediaUri;
    private String mediaType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recycler_view_chat);
        editTextMessage = findViewById(R.id.edit_text_message);
        buttonSend = findViewById(R.id.button_send);
        buttonAttach = findViewById(R.id.button_attach);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatList);
        recyclerView.setAdapter(chatAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        loadMessages();

        buttonSend.setOnClickListener(v -> sendMessage());

        buttonAttach.setOnClickListener(v -> openFileChooser());

        // Get FCM registration token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();

                    // Log and toast
                    Log.d(TAG, "FCM Token: " + token);

                });

        // Request notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadMessages() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference chatRef = databaseReference.child("chat").child(userId);

            chatRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isAtBottom = isRecyclerViewAtBottom();

                    chatList.clear();
                    for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                        ChatMessage message = chatSnapshot.getValue(ChatMessage.class);
                        if (message != null) {
                            chatList.add(message);
                        }
                    }
                    chatAdapter.notifyDataSetChanged();

                    if (isAtBottom) {
                        recyclerView.post(() -> recyclerView.scrollToPosition(chatList.size() - 1));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChatActivity.this, "Failed to load messages", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String userName = user.getDisplayName();
            String message = editTextMessage.getText().toString().trim();

            if (!TextUtils.isEmpty(message) || mediaUri != null) {
                if (mediaUri != null) {
                    uploadMedia(userId, userName, message);
                } else {
                    saveMessage(userId, userName, message, null, "text");
                }
            } else {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            }
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
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                String userName = user.getDisplayName();
                uploadMedia(userId, userName, "");
            }
        }
    }

    private void uploadMedia(String userId, String userName, String message) {
        if (mediaUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("chat_media");
            final StorageReference mediaRef = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(mediaUri));

            mediaRef.putFile(mediaUri).addOnSuccessListener(taskSnapshot -> mediaRef.getDownloadUrl().addOnSuccessListener(uri -> {
                saveMessage(userId, userName, message, uri.toString(), mediaType);
            }).addOnFailureListener(e -> Toast.makeText(ChatActivity.this, "Failed to upload media", Toast.LENGTH_SHORT).show()));
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void saveMessage(String userId, String userName, String message, String mediaUrl, String messageType) {
        DatabaseReference chatRef = databaseReference.child("chat").child(userId).push();
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("name", userName);
        messageData.put("message", message);
        messageData.put("mediaUrl", mediaUrl);
        messageData.put("mediaType", messageType);

        chatRef.setValue(messageData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sendNotification(userId, "New Message", message);
            } else {
                Toast.makeText(ChatActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });

        editTextMessage.setText("");
        mediaUri = null;
        mediaType = null;
    }

    private void sendNotification(String userId, String title, String message) {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId).push();
        Map<String, String> notificationData = new HashMap<>();
        notificationData.put("title", title);
        notificationData.put("message", message);

        notificationRef.setValue(notificationData);
    }

    private void listenForNotifications() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference("notifications").child(userId);

            notificationRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Map<String, String> notificationData = (Map<String, String>) snapshot.getValue();
                    if (notificationData != null) {
//                        sendNotification(notificationData.get("title"), notificationData.get("message"));
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {}

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        listenForNotifications();
    }

    private void sendNotification(String title, String messageBody) {
        String channelId = "default_channel_id";
        String channelName = "Default Channel";

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Check for notification permission before notifying
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }
}
