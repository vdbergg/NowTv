package com.rc.nowtv.models;

/**
 * Created by berg on 26/04/17.
 */

public class User {

    private String name;
    private String email;
    private String idUser;
    private String urlPhoto;
    private String username;

    public User(String name, String email, String idUser, String urlPhoto) {
        this.name = name;
        this.email = email;
        this.idUser = idUser;
        this.urlPhoto = urlPhoto;
        if (email != null && !email.equals("")) this.username = email.substring(0, email.lastIndexOf("@"));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }
}
