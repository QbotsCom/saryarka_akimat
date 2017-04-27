package com.turlygazhy.service;

import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Button;
import com.turlygazhy.exception.CommandNotFoundException;

import java.sql.SQLException;

/**
 * Created by user on 1/2/17.
 */
public class CommandService extends BotService {

    public Command getCommand(String text, int botId) throws SQLException, CommandNotFoundException {
        Button button = buttonDao.getButton(text, botId);
        return commandDao.getCommand(button.getCommandId());
    }
}
