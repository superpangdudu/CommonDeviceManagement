package com.krly.api;

import java.io.Serializable;


public class Message implements Serializable {
    private int token;
    private byte[] data;

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
