package com.technion.doggyguide.dataElements;

public class EventElement {
    private String date;
    private String start_time;
    private String end_time;
    private String description;

    public EventElement(String date, String start_time, String end_time, String description) {
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getDescription() {
        return description;
    }
}
