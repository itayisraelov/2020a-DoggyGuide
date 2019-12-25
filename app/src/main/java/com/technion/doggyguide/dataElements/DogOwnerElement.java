package com.technion.doggyguide.dataElements;

public class DogOwnerElement {
    private String name;
    private String email;
    private final String org_ID = "bayet.ovid@igdcb.org";
    private String dog_name;
    private String dog_breed;

    public DogOwnerElement(String name, String email, String dog_name, String dog_breed) {
        this.name = name;
        this.email = email;
        this.dog_name = dog_name;
        this.dog_breed = dog_breed;
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
}
