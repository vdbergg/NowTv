package com.rc.nowtv.models;

/**
 * Created by berg on 27/05/17.
 */

public class Member {
    private String texto;
    private int iconeRId;
    private String jId;

    public Member(String texto, int iconeRId, String jId) {
        this.texto = texto;
        this.iconeRId = iconeRId;
        this.jId = jId;
    }

    public String getjId() {
        return jId;
    }

    public void setjId(String jId) {
        this.jId = jId;
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