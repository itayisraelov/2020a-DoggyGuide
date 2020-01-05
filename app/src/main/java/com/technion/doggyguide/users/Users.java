package com.technion.doggyguide.users;

public class Users {
    private String name;
    private String mImageUrl;
    private String mStatus;

    public Users(String name, String mImageUrl, String mStatus) {
        this.name = name;
        this.mImageUrl = mImageUrl;
        this.mStatus = mStatus;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
