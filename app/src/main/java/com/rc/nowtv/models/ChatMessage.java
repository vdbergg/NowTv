package com.rc.nowtv.models;

import java.util.Date;

/**
 * Created by berg on 26/04/17.
 */

public class ChatMessage {

    private String messageText;
    private String username;
    private long time;
    private String urlUserPhoto;

    public ChatMessage(String messageText, String username, String urlUserPhoto) {
        this.messageText = messageText;
        this.username = username;
        this.urlUserPhoto = urlUserPhoto;

        time = new Date().getTime();
    }

    public ChatMessage() {

    }

    public ChatMessage(String body) {
        this.messageText = body;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrlUserPhoto() {
        return urlUserPhoto;
    }

    public void setUrlUserPhoto(String urlUserPhoto) {
        this.urlUserPhoto = urlUserPhoto;
    }
}
