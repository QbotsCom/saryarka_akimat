package com.turlygazhy.entity;

/**
 * Created by user on 4/27/17.
 */
public class User {
    private int id;
    private long chatId;
    private String userName;
    private String phoneNumber;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
