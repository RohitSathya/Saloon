// Reel.java
package com.example.haircut_admin;

public class Reel {
    public String videoUrl;
    public String imageUrl;

    public Reel() {
        // Default constructor required for calls to DataSnapshot.getValue(Reel.class)
    }

    public Reel(String videoUrl, String imageUrl) {
        this.videoUrl = videoUrl;
        this.imageUrl = imageUrl;
    }
}
