package com.example.va;

import java.io.Serializable;

public class ProductItem implements Serializable {
    private String id;
    private String productTitle;
    private String imageUrl;
    private int productPrice;
    private String trailerUrl;
    private String sampleImage1Url;
    private String sampleImage2Url;
    private String sampleImage3Url;
    private int quantity;
    private boolean isSelected;

    public ProductItem() {
        // Default constructor required for calls to DataSnapshot.getValue(ProductItem.class)
    }

    public ProductItem(String id, String productTitle, String imageUrl, int productPrice, String trailerUrl, String sampleImage1Url, String sampleImage2Url, String sampleImage3Url) {
        this.id = id;
        this.productTitle = productTitle;
        this.imageUrl = imageUrl;
        this.productPrice = productPrice;
        this.trailerUrl = trailerUrl;
        this.sampleImage1Url = sampleImage1Url;
        this.sampleImage2Url = sampleImage2Url;
        this.sampleImage3Url = sampleImage3Url;
        this.quantity = 1; // Default quantity to 1
        this.isSelected = false; // Default selected state to false
    }

    // Getters and setters...
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getSampleImage1Url() {
        return sampleImage1Url;
    }

    public void setSampleImage1Url(String sampleImage1Url) {
        this.sampleImage1Url = sampleImage1Url;
    }

    public String getSampleImage2Url() {
        return sampleImage2Url;
    }

    public void setSampleImage2Url(String sampleImage2Url) {
        this.sampleImage2Url = sampleImage2Url;
    }

    public String getSampleImage3Url() {
        return sampleImage3Url;
    }

    public void setSampleImage3Url(String sampleImage3Url) {
        this.sampleImage3Url = sampleImage3Url;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
