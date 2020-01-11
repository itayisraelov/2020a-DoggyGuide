package com.technion.doggyguide.dataElements;

public class PostNotificationElement {
    private String mTitle;
    private String mDescription;
    private String mSender;
    private String mReciever;

    public PostNotificationElement() {

    }

    public PostNotificationElement(String mTitle, String mDescription, String mSender, String mReciever) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mSender = mSender;
        this.mReciever = mReciever;
    }

    public String getmTitle() {
        return mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmSender() {
        return mSender;
    }

    public String getmReciever() {
        return mReciever;
    }
}
