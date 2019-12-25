package com.technion.doggyguide.homeScreen.alarm;

public class WalkAlarm {
    private String walk_time_1;
    private String walk_time_2;
    private String walk_time_3;

    public WalkAlarm(String walk_time_1, String walk_time_2, String walk_time_3) {
        this.walk_time_1 = walk_time_1;
        this.walk_time_2 = walk_time_2;
        this.walk_time_3 = walk_time_3;
    }

    public WalkAlarm() {
        this.walk_time_1 = "set_daily_alarm";
        this.walk_time_2 = "set_daily_alarm";
        this.walk_time_3 = "set_daily_alarm";
    }

    public String getWalk_time_1() {
        return walk_time_1;
    }

    public void setWalk_time_1(String walk_time_1) {
        this.walk_time_1 = walk_time_1;
    }

    public String getWalk_time_2() {
        return walk_time_2;
    }

    public void setWalk_time_2(String walk_time_2) {
        this.walk_time_2 = walk_time_2;
    }

    public String getWalk_time_3() {
        return walk_time_3;
    }

    public void setWalk_time_3(String walk_time_3) {
        this.walk_time_3 = walk_time_3;
    }
}
