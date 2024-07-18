package com.example.va;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VideoDao {
    @Insert
    void insert(ReelVideoItem video);

    @Query("SELECT * FROM videos")
    List<ReelVideoItem> getAllVideos();

    @Query("DELETE FROM videos WHERE videoId = :videoId")
    void deleteVideo(String videoId);

    @Query("SELECT * FROM videos WHERE videoId = :videoId LIMIT 1")
    ReelVideoItem getVideoById(String videoId);
}
