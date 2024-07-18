package com.example.va;

import java.io.Serializable;

public class VideoItem implements Serializable {
    private String id;
    private String title;
    private String thumbnailUrl;
    private String videoUrl;
    private boolean isLocked;

    public VideoItem() {
        // Default constructor required for calls to DataSnapshot.getValue(VideoItem.class)
    }

    public VideoItem(String id, String title, String thumbnailUrl, String videoUrl) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.isLocked = false;
    }

    public VideoItem(String id, String title, String thumbnailUrl, String videoUrl, boolean isLocked) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUrl = videoUrl;
        this.isLocked = isLocked;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }
}
