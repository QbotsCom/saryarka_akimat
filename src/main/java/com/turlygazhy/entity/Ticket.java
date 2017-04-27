package com.turlygazhy.entity;

import com.turlygazhy.command.impl.work_around.entity.Category;

/**
 * Created by user on 4/26/17.
 */
public class Ticket {
    private Category category;
    private String text;
    private String photo;

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

    public long getChatId() {
        return category.getChatId();
    }


    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }
}
