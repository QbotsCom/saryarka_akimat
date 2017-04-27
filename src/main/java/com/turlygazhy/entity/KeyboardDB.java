package com.turlygazhy.entity;

/**
 * Created by user on 3/22/17.
 */
public class KeyboardDB {
    private String buttonIds;
    private boolean inline;
    private String comment;
    private int id;

    public void setButtonIds(String buttonIds) {
        this.buttonIds = buttonIds;
    }

    public String getButtonIds() {
        return buttonIds;
    }

    public void setInline(boolean inline) {
        this.inline = inline;
    }

    public boolean isInline() {
        return inline;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
