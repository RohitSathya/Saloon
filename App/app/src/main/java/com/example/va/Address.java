package com.example.va;

import java.io.Serializable;

public class Address implements Serializable {
    private String fullName;
    private String mobileNumber;
    private String flatHouse;
    private String areaStreet;
    private String landmark;
    private String pincode;
    private String townCity;
    private String state;

    public Address() {
        // Default constructor required for calls to DataSnapshot.getValue(Address.class)
    }

    public Address(String fullName, String mobileNumber, String flatHouse, String areaStreet, String landmark, String pincode, String townCity, String state) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.flatHouse = flatHouse;
        this.areaStreet = areaStreet;
        this.landmark = landmark;
        this.pincode = pincode;
        this.townCity = townCity;
        this.state = state;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getFlatHouse() {
        return flatHouse;
    }

    public void setFlatHouse(String flatHouse) {
        this.flatHouse = flatHouse;
    }

    public String getAreaStreet() {
        return areaStreet;
    }

    public void setAreaStreet(String areaStreet) {
        this.areaStreet = areaStreet;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getTownCity() {
        return townCity;
    }

    public void setTownCity(String townCity) {
        this.townCity = townCity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
