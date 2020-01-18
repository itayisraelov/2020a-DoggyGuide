package com.technion.doggyguide.friends;

public class Friends {
    private String mName;
    private String mImageUrl;
    private String mStatus;
//    private String online;

    public Friends() { }

    public Friends(String mName, String mImageUrl, String mStatus) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.mStatus = mStatus;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
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
}