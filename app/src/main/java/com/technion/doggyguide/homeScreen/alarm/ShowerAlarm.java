package com.technion.doggyguide.homeScreen.alarm;

public class ShowerAlarm {
    private String shower_time_1;
    private String shower_time_2;
    private String shower_time_3;

    public ShowerAlarm(String shower_time_1, String shower_time_2, String shower_time_3) {
        this.shower_time_1 = shower_time_1;
        this.shower_time_2 = shower_time_2;
        this.shower_time_3 = shower_time_3;
    }

    public ShowerAlarm() {
    }

    public String getShower_time_1() {
        return shower_time_1;
    }

    public void setShower_time_1(String shower_time_1) {
        this.shower_time_1 = shower_time_1;
    }

    public String getShower_time_2() {
        return shower_time_2;
    }

    public void setShower_time_2(String shower_time_2) {
        this.shower_time_2 = shower_time_2;
    }

    public String getShower_time_3() {
        return shower_time_3;
    }

    public void setShower_time_3(String shower_time_3) {
        this.shower_time_3 = shower_time_3;
    }
}
