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
    private String executorsIds;
    private String afterText;
    private String deadline;
    private int groupId;

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

    public void setExecutorsIds(String executorsIds) {
        this.executorsIds = executorsIds;
    }

    public String getExecutorsIds() {
        return executorsIds;
    }


    public void setAfterText(String afterText) {
        this.afterText = afterText;
    }

    public String getAfterText() {
        return afterText;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
