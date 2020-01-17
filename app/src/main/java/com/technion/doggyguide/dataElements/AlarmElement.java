package com.technion.doggyguide.dataElements;

import com.google.firebase.Timestamp;

public class AlarmElement {
    private String time;
    private boolean set;
    private String alarmId;
    private int hourOfDay;
    private int minute;
    private String type;

    public AlarmElement(String time, boolean set, String alarmId, int hourOfDay, int minute, String type) {
        this.time = time;
        this.set = set;
        this.alarmId = alarmId;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.type = type;
    }


    public AlarmElement() {
    }

    public String getTime() {
        return time;
    }

    public boolean isSet() {
        return set;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public String getType() {
        return type;
    }
}
