package com.turlygazhy.entity;

import com.turlygazhy.command.impl.collect_info.entity.Info;

import java.util.List;

/**
 * Created by user on 3/21/17.
 */
public class AddButton {
    private String name;
    private int commandTypeId;
    private String messageText;
    private String photo;
    private int keyboardId;
    private boolean newRow;
    private int botId;
    private int changeMessageId;
    private String linkForInline;
    private String linkForInlineName;
    private List<Info> infos;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCommandTypeId(int commandTypeId) {
        this.commandTypeId = commandTypeId;
    }

    public int getCommandTypeId() {
        return commandTypeId;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public int getKeyboardId() {
        return keyboardId;
    }

    public void setKeyboardId(int keyboardId) {
        this.keyboardId = keyboardId;
    }

    public boolean isNewRow() {
        return newRow;
    }

    public void setNewRow(boolean newRow) {
        this.newRow = newRow;
    }

    public int getBotId() {
        return botId;
    }

    public void setBotId(int botId) {
        this.botId = botId;
    }

    public int getChangeMessageId() {
        return changeMessageId;
    }

    public void setChangeMessageId(int changeMessageId) {
        this.changeMessageId = changeMessageId;
    }

    public void setLinkForInline(String linkForInline) {
        this.linkForInline = linkForInline;
    }

    public String getLinkForInline() {
        return linkForInline;
    }

    public String getLinkForInlineName() {
        return linkForInlineName;
    }

    public void setLinkForInlineName(String linkForInlineName) {
        this.linkForInlineName = linkForInlineName;
    }

    public void setInfos(List<Info> infos) {
        this.infos = infos;
    }

    public List<Info> getInfos() {
        return infos;
    }
}
