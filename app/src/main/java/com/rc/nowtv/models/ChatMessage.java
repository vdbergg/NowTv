package com.rc.nowtv.models;

import java.util.Date;

/**
 * Created by berg on 26/04/17.
 */

public class ChatMessage {

    public static final int MSG_SEND = 0;
    public static final int MSG_RECEIVED = 1;

    private String messageText;
    private String username;
    private long time;
    private String urlUserPhoto;
    private int type;

    public ChatMessage(String messageText, String username, String urlUserPhoto) {
        this.messageText = messageText;
        this.username = username;
        this.urlUserPhoto = urlUserPhoto;

        time = new Date().getTime();
    }

    public ChatMessage() {

    }

    public ChatMessage(String body, int type) {
        this.messageText = body;
        this.type = type;

        time = new Date().getTime();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
