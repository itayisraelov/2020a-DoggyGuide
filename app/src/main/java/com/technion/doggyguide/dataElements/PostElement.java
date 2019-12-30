package com.technion.doggyguide.dataElements;



public class PostElement {
    private String name;
    private String userId;
    private String start_time;
    private String end_time;
    private String post_date;
    private String posting_date;
    private String description;

    public PostElement() {
        //must have an empty constructor
    }

    public PostElement(String name, String userId, String start_time, String end_time,
                       String post_date, String posting_date, String description) {
        this.name = name;
        this.userId = userId;
        this.start_time = start_time;
        this.end_time = end_time;
        this.post_date = post_date;
        this.posting_date = posting_date;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getUserId() { return userId; }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getPost_date() {
        return post_date;
    }

    public String getPosting_date() { return posting_date; }

    public String getDescription() { return description; }
}
