package com.rc.nowtv.models;

/**
 * Created by berg on 27/05/17.
 */

public class Member {
    private String texto;
    private int iconeRId;

    public Member(String texto, int iconeRId) {
        this.texto = texto;
        this.iconeRId = iconeRId;
    }

    public int getIconeRId() {
        return iconeRId;
    }

    public void setIconeRId(int iconeRId) {
        this.iconeRId = iconeRId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}