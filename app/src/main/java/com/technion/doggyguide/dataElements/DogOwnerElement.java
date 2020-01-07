package com.technion.doggyguide.dataElements;

public class DogOwnerElement {
    private final String org_ID = "bayet.ovid@igdcb.org";
    private String name;
    private String email;
    private String dog_name;
    private String dog_breed;
    private String mImageUrl;
    private String mStatus;


    public DogOwnerElement() {
        //Need an Empty Constructor
    }

    public DogOwnerElement(String name, String email, String dog_name,
                           String dog_breed, String mImageUrl, String mStatus) {
        this.name = name;
        this.email = email;
        this.dog_name = dog_name;
        this.dog_breed = dog_breed;
        this.mImageUrl = mImageUrl;
        this.mStatus = mStatus;
    }

    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }

    public String getOrg_ID() {
        return org_ID;
    }


    public String getDog_name() {
        return dog_name;
    }


    public String getDog_breed() {
        return dog_breed;
    }


    public String getmImageUrl() {
        return mImageUrl;
    }


    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) { this.mStatus = mStatus; }
}
