package com.rc.nowtv.models;

/**
 * Created by berg on 24/05/17.
 */

public class Video {
    private String title;
    private String duration;
    private String published;
    private int picture;

    public Video(String title, String duration, String published, int picture) {
        this.title = title;
        this.duration = duration;
        this.published = published;
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }
}
