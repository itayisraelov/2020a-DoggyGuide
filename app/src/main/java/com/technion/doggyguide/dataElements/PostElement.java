package com.technion.doggyguide.dataElements;


public class PostElement {
    private String name;
    private String start_time;
    private String end_time;
    private String post_time;
    private String description;

    public PostElement(String name, String start_time, String end_time, String post_time, String description) {
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.post_time = post_time;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getPost_time() {
        return post_time;
    }

    public String getDescription() {
        return description;
    }
}
