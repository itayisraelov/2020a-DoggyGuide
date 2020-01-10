package com.technion.doggyguide.dataElements;

public class DogOwnerElement {
    private final String org_ID = "bayet.ovid@igdcb.org";
    private String mName;
    private String mEmail;
    private String mDog_name;
    private String mDog_breed;
    private String mImageUrl;
    private String mStatus;
    private String mDeviceToken;


    public DogOwnerElement() {
        //Need an Empty Constructor
    }

    public DogOwnerElement(String mName, String mEmail, String mDog_name,
                           String mDog_breed, String mImageUrl, String mStatus, String mDeviceToken) {
        this.mName = mName;
        this.mEmail = mEmail;
        this.mDog_name = mDog_name;
        this.mDog_breed = mDog_breed;
        this.mImageUrl = mImageUrl;
        this.mStatus = mStatus;
        this.mDeviceToken = mDeviceToken;
    }

    public String getOrg_ID() {
        return org_ID;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmDog_name() {
        return mDog_name;
    }

    public void setmDog_name(String mDog_name) {
        this.mDog_name = mDog_name;
    }

    public String getmDog_breed() {
        return mDog_breed;
    }

    public void setmDog_breed(String mDog_breed) {
        this.mDog_breed = mDog_breed;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getmDeviceToken() {
        return mDeviceToken;
    }

    public void setmDeviceToken(String mDeviceToken) {
        this.mDeviceToken = mDeviceToken;
    }
}
