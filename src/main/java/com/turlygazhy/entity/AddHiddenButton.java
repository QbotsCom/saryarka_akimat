package com.turlygazhy.entity;

/**
 * Created by user on 4/16/17.
 */
public class AddHiddenButton {
    private int commandTypeId;
    private String name;
    private int botId;
    private int keyboardId;

    public int getCommandTypeId() {
        return commandTypeId;
    }

    public void setCommandTypeId(int commandTypeId) {
        this.commandTypeId = commandTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBotId() {
        return botId;
    }

    public void setBotId(int botId) {
        this.botId = botId;
    }

    public int getKeyboardId() {
        return keyboardId;
    }

    public void setKeyboardId(int keyboardId) {
        this.keyboardId = keyboardId;
    }
}
