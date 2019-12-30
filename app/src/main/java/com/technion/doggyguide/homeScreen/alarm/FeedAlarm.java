package com.technion.doggyguide.homeScreen.alarm;

public class FeedAlarm {
    private String feed_time_1;
    private String feed_time_2;
    private String feed_time_3;

    public FeedAlarm(String feed_time_1, String feed_time_2, String feed_time_3) {
        this.feed_time_1 = feed_time_1;
        this.feed_time_2 = feed_time_2;
        this.feed_time_3 = feed_time_3;
    }

    public FeedAlarm() {
    }

    public String getFeed_time_1() {
        return feed_time_1;
    }

    public void setFeed_time_1(String feed_time_1) {
        this.feed_time_1 = feed_time_1;
    }

    public String getFeed_time_2() {
        return feed_time_2;
    }

    public void setFeed_time_2(String feed_time_2) {
        this.feed_time_2 = feed_time_2;
    }

    public String getFeed_time_3() {
        return feed_time_3;
    }

    public void setFeed_time_3(String feed_time_3) {
        this.feed_time_3 = feed_time_3;
    }
}
