package com.example.va;

public class GalleryImageItem {
    private String imageUrl;
    private String title;

    public GalleryImageItem() {
        // Default constructor required for calls to DataSnapshot.getValue(GalleryImageItem.class)
    }

    public GalleryImageItem(String imageUrl, String title) {
        this.imageUrl = imageUrl;
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
