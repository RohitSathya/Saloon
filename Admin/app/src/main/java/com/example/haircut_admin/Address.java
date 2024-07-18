package com.example.haircut_admin;

public class Address {
    private String areaStreet;
    private String flatHouse;
    private String fullName;
    private String landmark;
    private String mobileNumber;
    private String pincode;
    private String state;
    private String townCity;

    public Address() {
        // Default constructor required for calls to DataSnapshot.getValue(Address.class)
    }

    // Getters and setters
    public String getAreaStreet() {
        return areaStreet;
    }

    public void setAreaStreet(String areaStreet) {
        this.areaStreet = areaStreet;
    }

    public String getFlatHouse() {
        return flatHouse;
    }

    public void setFlatHouse(String flatHouse) {
        this.flatHouse = flatHouse;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTownCity() {
        return townCity;
    }

    public void setTownCity(String townCity) {
        this.townCity = townCity;
    }
}
