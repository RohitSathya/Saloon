package com.example.va;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "videos")
public class ReelVideoItem {
    @PrimaryKey
    @NonNull
    private String videoId;
    private String videoLink;
    private String videoName;
    private String thumbnailLink;

    public ReelVideoItem(@NonNull String videoId, String videoLink, String videoName, String thumbnailLink) {
        this.videoId = videoId;
        this.videoLink = videoLink;
        this.videoName = videoName;
        this.thumbnailLink = thumbnailLink;
    }

    // Getters and Setters
    @NonNull
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(@NonNull String videoId) {
        this.videoId = videoId;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public void setThumbnailLink(String thumbnailLink) {
        this.thumbnailLink = thumbnailLink;
    }
}
