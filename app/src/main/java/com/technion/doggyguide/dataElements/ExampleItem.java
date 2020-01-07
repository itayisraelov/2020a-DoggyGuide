package com.technion.doggyguide.dataElements;

public class ExampleItem {
    private int mImageResource;
    private String mText1;
    private String mDescription;


    public ExampleItem(int imageResource, String text1, String description) {
        mImageResource = imageResource;
        mText1 = text1;
        mDescription = description;
    }

    public void changeText1(String text) {
        mText1 = text;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getText1() {
        return mText1;
    }

    public String getmDescription() { return mDescription; }
}
