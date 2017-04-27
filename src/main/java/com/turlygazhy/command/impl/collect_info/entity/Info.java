package com.turlygazhy.command.impl.collect_info.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 4/23/17.
 */
public class Info {
    private String name;
    private InfoType type;
    private List<String> options;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(InfoType type) {
        this.type = type;
    }

    public InfoType getType() {
        return type;
    }

    public void addOption(String option) {
        if (options == null) {
            options = new ArrayList<>();
        }
        options.add(option);
    }
}
