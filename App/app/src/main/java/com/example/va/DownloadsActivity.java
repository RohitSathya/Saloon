//package com.example.va;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//public class DownloadsActivity extends AppCompatActivity {
//
//    private ListView listViewDownloads;
//    private ArrayAdapter<String> downloadAdapter;
//    private List<String> downloadList;
//    private FirebaseUser currentUser;
//    private DatabaseReference downloadRef;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_download);
//
//        listViewDownloads = findViewById(R.id.listViewDownloads);
//        downloadList = new ArrayList<>();
//        downloadAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, downloadList);
//        listViewDownloads.setAdapter(downloadAdapter);
//
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        downloadRef = FirebaseDatabase.getInstance().getReference("downloads").child(currentUser.getUid());
//
//        fetchDownloads();
//
//        listViewDownloads.setOnItemClickListener((parent, view, position, id) -> {
//            String videoName = downloadList.get(position);
//            playOfflineVideo(videoName);
//        });
//    }
//
//    private void fetchDownloads() {
//        downloadRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                downloadList.clear();
//                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                    RentVideoPlayerActivity.VideoItem videoItem = postSnapshot.getValue(RentVideoPlayerActivity.VideoItem.class);
//                    if (videoItem != null) {
//                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), videoItem.getVideoName() + ".mp4");
//                        if (file.exists()) {
//                            downloadList.add(videoItem.getVideoName());
//                        }
//                    }
//                }
//                downloadAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(DownloadsActivity.this, "Failed to load downloads", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void playOfflineVideo(String videoName) {
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), videoName + ".mp4");
//        if (file.exists()) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.fromFile(file), "video/*");
//            startActivity(intent);
//        } else {
//            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
