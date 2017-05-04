package com.turlygazhy.entity;

import com.turlygazhy.command.impl.work_around.entity.Category;

/**
 * Created by user on 4/26/17.
 */
public class Ticket {
    private Category category;
    private String text;
    private String photo;
    private String executorNumber;
    private String executorFullName;
    private int googleSheetRowId;

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

    public void setExecutorNumber(String executorNumber) {
        this.executorNumber = executorNumber;
    }

    public String getExecutorNumber() {
        return executorNumber;
    }

    public void setExecutorFullName(String executorFullName) {
        this.executorFullName = executorFullName;
    }

    public String getExecutorFullName() {
        return executorFullName;
    }

    public int getGoogleSheetRowId() {
        return googleSheetRowId;
    }

    public void setGoogleSheetRowId(int googleSheetRowId) {
        this.googleSheetRowId = googleSheetRowId;
    }
}
