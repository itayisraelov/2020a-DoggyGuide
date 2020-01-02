package com.technion.doggyguide.dataElements;

public class DogOwnerElement {
    private String name;
    private String email;
    private final String org_ID = "bayet.ovid@igdcb.org";
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

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrg_ID() {
        return org_ID;
    }

    public String getDog_name() {
        return dog_name;
    }

    public void setDog_name(String dog_name) {
        this.dog_name = dog_name;
    }

    public String getDog_breed() {
        return dog_breed;
    }

    public void setDog_breed(String dog_breed) {
        this.dog_breed = dog_breed;
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
