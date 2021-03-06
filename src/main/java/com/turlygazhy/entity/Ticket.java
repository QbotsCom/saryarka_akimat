package com.turlygazhy.entity;

import com.turlygazhy.command.impl.work_around.entity.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 4/26/17.
 */
public class Ticket {
    private Category category;
    private String text;
    private String photo;
    private int googleSheetRowId;
    private int id;
    private String state;
    private List<User> executors;
    private long creatorChatId;

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public int getGoogleSheetRowId() {
        return googleSheetRowId;
    }

    public void setGoogleSheetRowId(int googleSheetRowId) {
        this.googleSheetRowId = googleSheetRowId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void addExecutor(User user) {
        if (executors == null) {
            executors = new ArrayList<>();
        }
        executors.add(user);
    }

    public List<User> getExecutors() {
        return executors;
    }

    public void setCreatorChatId(long creatorChatId) {
        this.creatorChatId = creatorChatId;
    }

    public long getCreatorChatId() {
        return creatorChatId;
    }
}
