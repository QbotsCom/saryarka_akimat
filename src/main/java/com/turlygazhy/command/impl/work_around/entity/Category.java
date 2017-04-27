package com.turlygazhy.command.impl.work_around.entity;

import java.util.List;

/**
 * Created by user on 4/26/17.
 */
public class Category {

    private int id;
    private String name;
    private boolean main;
    private List<Category> childs;
    private long chatId;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setMain(boolean main) {
        this.main = main;
    }

    public boolean isMain() {
        return main;
    }


    public void setChilds(List<Category> childs) {
        this.childs = childs;
    }

    public List<Category> getChilds() {
        return childs;
    }

    public boolean hasChild() {
        return childs != null;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
