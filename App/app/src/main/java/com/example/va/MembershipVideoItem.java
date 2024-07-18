package com.example.va;

public class MembershipVideoItem {
    private String id;
    private String videoName;
    private String thumbnailLink;
    private String videoLink;
    private boolean isLocked;
    private String channelOwnerName;
    private String categories;

    // Default constructor required for calls to DataSnapshot.getValue(MembershipVideoItem.class)
    public MembershipVideoItem() {}

    public MembershipVideoItem(String id, String videoName, String thumbnailLink, String videoLink, boolean isLocked, String channelOwnerName, String categories) {
        this.id = id;
        this.videoName = videoName;
        this.thumbnailLink = thumbnailLink;
        this.videoLink = videoLink;
        this.isLocked = isLocked;
        this.channelOwnerName = channelOwnerName;
        this.categories = categories;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getVideoName() { return videoName; }
    public void setVideoName(String videoName) { this.videoName = videoName; }
    public String getThumbnailLink() { return thumbnailLink; }
    public void setThumbnailLink(String thumbnailLink) { this.thumbnailLink = thumbnailLink; }
    public String getVideoLink() { return videoLink; }
    public void setVideoLink(String videoLink) { this.videoLink = videoLink; }
    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }
    public String getChannelOwnerName() { return channelOwnerName; }
    public void setChannelOwnerName(String channelOwnerName) { this.channelOwnerName = channelOwnerName; }
    public String getCategories() { return categories; }
    public void setCategories(String categories) { this.categories = categories; }
}
