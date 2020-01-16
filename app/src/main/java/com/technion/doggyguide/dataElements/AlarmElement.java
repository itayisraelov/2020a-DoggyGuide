package com.technion.doggyguide.dataElements;

import com.google.firebase.Timestamp;

public class AlarmElement {
    private String time;
    private boolean set;

    public AlarmElement(String time, boolean set) {
        this.time = time;
        this.set = set;
    }

    public AlarmElement() {
    }

    public String getTime() {
        return time;
    }

    public boolean isSet() {
        return set;
    }

}
