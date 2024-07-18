package com.example.haircut_admin;

public class ChatMessage {
    private String name;
    private String message;
    private String mediaUrl;
    private String mediaType;
    private String messageType; // "text", "image", or "video"

    public ChatMessage() {
        // Default constructor required for calls to DataSnapshot.getValue(ChatMessage.class)
    }

    public ChatMessage(String name, String message, String mediaUrl, String mediaType, String messageType) {
        this.name = name;
        this.message = message;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.messageType = messageType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
