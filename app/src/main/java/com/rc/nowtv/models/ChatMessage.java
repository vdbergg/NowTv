package com.rc.nowtv.models;

import java.util.Date;

/**
 * Created by berg on 26/04/17.
 */

public class ChatMessage {

    private String messageText;
    private String nameuser;
    private long time;
    private String urlUserPhoto;

    public ChatMessage(String messageText, String nameUser, String urlUserPhoto) {
        this.messageText = messageText;
        this.nameuser = nameUser;
        this.urlUserPhoto = urlUserPhoto;

        time = new Date().getTime();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getUsername() {
        return nameuser;
    }

    public void setNameuser(String nameuser) {
        this.nameuser = nameuser;
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
