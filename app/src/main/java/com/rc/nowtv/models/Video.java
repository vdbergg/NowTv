package com.rc.nowtv.models;

/**
 * Created by berg on 24/05/17.
 */

public class Video {
    private String title;
    private String duration;
    private String published;
    private String url;
    private int picture;
    private String room;

    public Video(String title, String url, int picture, String room) {
        this.title = title;
        this.url = url;
        this.picture = picture;
        this.room = room;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPicture() {
        return picture;
    }

    public void setPicture(int picture) {
        this.picture = picture;
    }

    public String getRoom() {
        return this.room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
