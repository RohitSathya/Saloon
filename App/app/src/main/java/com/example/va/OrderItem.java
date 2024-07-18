package com.example.va;

public class OrderItem {
    private String orderId;
    private String imageUrl;
    private String productTitle;
    private int productPrice;
    private int quantity;
    private boolean isSelected;

    public OrderItem() {
        // Default constructor required for calls to DataSnapshot.getValue(OrderItem.class)
    }

    public OrderItem(String orderId, String imageUrl, String productTitle, int productPrice, int quantity, boolean isSelected) {
        this.orderId = orderId;
        this.imageUrl = imageUrl;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.isSelected = isSelected;
    }

    // Getters and setters...
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
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
