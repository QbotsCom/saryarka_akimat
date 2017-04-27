package com.turlygazhy.service;

import com.turlygazhy.command.Command;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.ButtonDao;
import com.turlygazhy.dao.impl.CommandDao;
import com.turlygazhy.dao.impl.KeyboardMarkUpDao;
import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.*;
import com.turlygazhy.exception.CommandNotFoundException;
import org.h2.jdbc.JdbcSQLException;

import java.sql.SQLException;

/**
 * Created by user on 1/2/17.
 */
public class BotService {
    public static final int SHOW_INFO_COMMAND_TYPE_ID = 1;
    public static final String START = "/start";
    public static final int CHANGE_INFO_COMMAND_TYPE_ID = 2;
    public static final String CHANGE_START = "Change start";
    public static final String ADMIN_MODE = "Admin mode";
    public static final String ADMIN = "Admin";
    public static final String CHANGE = "change ";
    DaoFactory factory = DaoFactory.getFactory();
    MessageDao messageDao = factory.getMessageDao();
    KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    ButtonDao buttonDao = factory.getButtonDao();
    CommandDao commandDao = factory.getCommandDao();

    public void addShowInfoButton(AddButton addButton) throws SQLException {
        int keyboardId = addButton.getKeyboardId();
        String linkForInline = addButton.getLinkForInline();
        int linkKeyboardId = 0;
        int botId = addButton.getBotId();
        if (linkForInline != null) {
            Button inline = buttonDao.insert(addButton.getLinkForInlineName(), 0, linkForInline, false, botId);
            KeyboardDB link = keyboardMarkUpDao.insert(String.valueOf(inline.getId()), true, "link");
            linkKeyboardId = link.getId();
        }
        Message message = messageDao.insert(addButton.getMessageText(), addButton.getPhoto(), linkKeyboardId);
        Command command = commandDao.insert(addButton.getCommandTypeId(), (int) message.getId());
        Button button = buttonDao.insert(addButton.getName(), (int) command.getId(), null, false, botId);
        keyboardMarkUpDao.addButton(keyboardId, button.getId(), addButton.isNewRow());

        //adding to admin change this button
        AddButton changeButton = new AddButton();
        int adminKeyboardId = keyboardMarkUpDao.getAdminId(botId);
        changeButton.setKeyboardId(adminKeyboardId);
        Command command1;
        try {
            command1 = commandDao.getCommand(button.getCommandId());
        } catch (CommandNotFoundException e) {
            //this is impossible
            throw new RuntimeException(e);
        }
        changeButton.setChangeMessageId((int) command1.getMessageId());
        changeButton.setName(CHANGE + addButton.getName());
        changeButton.setNewRow(true);
        changeButton.setBotId(botId);
        addChangeInfoButton(changeButton);
    }

    public Button addHiddenStartButton(AddHiddenButton hiddenButton) throws SQLException {
        int botId = hiddenButton.getBotId();
        String changeThisInAdmin = messageDao.getMessageText(Const.CHANGE_THIS_IN_ADMIN_MESSAGE_ID);
        int mainKeyboardId;
        try {
            mainKeyboardId = keyboardMarkUpDao.getMainId(botId);
        } catch (JdbcSQLException e) {
            mainKeyboardId = keyboardMarkUpDao.insertMain("0", false, String.valueOf(botId)).getId();
        }
        Message message = messageDao.insert(changeThisInAdmin, null, mainKeyboardId);
        Command command = commandDao.insert(hiddenButton.getCommandTypeId(), (int) message.getId());
        return buttonDao.insert(hiddenButton.getName(), (int) command.getId(), null, false, botId);
    }

    public void addChangeInfoButton(AddButton addButton) throws SQLException {
        int keyboardId = addButton.getKeyboardId();
        Command command = commandDao.insert(CHANGE_INFO_COMMAND_TYPE_ID, addButton.getChangeMessageId());
        Button button = buttonDao.insert(addButton.getName(), (int) command.getId(), null, false, addButton.getBotId());
        keyboardMarkUpDao.addButton(keyboardId, button.getId(), addButton.isNewRow());
    }

    public void addAdminCommand(int botId) throws SQLException {
        //adding "/start"
        AddHiddenButton addHiddenButton = new AddHiddenButton();
        addHiddenButton.setCommandTypeId(SHOW_INFO_COMMAND_TYPE_ID);
        addHiddenButton.setName(START);
        addHiddenButton.setBotId(botId);
        Button button = addHiddenStartButton(addHiddenButton);

        //adding "change start"
        AddButton addButton = new AddButton();
        int adminKeyboardId = keyboardMarkUpDao.getAdminId(botId);
        addButton.setKeyboardId(adminKeyboardId);
        Command command;
        try {
            command = commandDao.getCommand(button.getCommandId());
        } catch (CommandNotFoundException e) {
            //this is impossible
            throw new RuntimeException(e);
        }
        addButton.setChangeMessageId((int) command.getMessageId());
        addButton.setName(CHANGE_START);
        addButton.setNewRow(true);
        addButton.setBotId(botId);
        addChangeInfoButton(addButton);

        //adding "Admin"
        AddHiddenButton adminButton = new AddHiddenButton();
        adminButton.setCommandTypeId(SHOW_INFO_COMMAND_TYPE_ID);
        adminButton.setKeyboardId(adminKeyboardId);
        adminButton.setName(ADMIN);
        adminButton.setBotId(botId);
        addHiddenAdminButton(adminButton);
    }

    public Button addHiddenAdminButton(AddHiddenButton hiddenButton) throws SQLException {
        Message message = messageDao.insert(ADMIN_MODE, null, hiddenButton.getKeyboardId());
        Command command = commandDao.insert(hiddenButton.getCommandTypeId(), (int) message.getId());
        return buttonDao.insert(hiddenButton.getName(), (int) command.getId(), null, false, hiddenButton.getBotId());
    }

    public void addFeedbackButton(AddButton addButton) {
        //todo implement it
    }
}
