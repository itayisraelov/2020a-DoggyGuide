package com.technion.doggyguide.dataElements;

public class OrganizationElement {
    private String name;
    private String email;
    private String location;

    public OrganizationElement(String name, String email, String location) {
        this.name = name;
        this.email = email;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }
}
