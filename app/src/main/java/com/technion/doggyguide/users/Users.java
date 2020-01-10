package com.technion.doggyguide.users;

public class Users {
    private String mName;
    private String mImageUrl;
    private String mStatus;

    public Users(String name, String mImageUrl, String mStatus) {
        this.mName = name;
        this.mImageUrl = mImageUrl;
        this.mStatus = mStatus;
    }

    public Users() {
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
