package com.turlygazhy.service;

import com.turlygazhy.command.Command;
import com.turlygazhy.command.impl.ChangeDeadlineCommand;
import com.turlygazhy.command.impl.ExecuteScriptCommand;
import com.turlygazhy.command.impl.TicketExecutedCommand;
import com.turlygazhy.command.impl.TicketInProgressCommand;
import com.turlygazhy.entity.Button;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.exception.CommandNotFoundException;

import java.sql.SQLException;

/**
 * Created by user on 1/2/17.
 */
public class CommandService extends BotService {

    public Command getCommand(String text, int botId) throws SQLException, CommandNotFoundException {
        if (text != null) {
            if (text.equals("script")) {
                return new ExecuteScriptCommand();
            }

            String executedText = messageDao.getMessageText(190);
            if (text.contains(executedText + ":")) {
                return new TicketExecutedCommand(text.replace(executedText + ":", ""));
            }

            String inProgressText = messageDao.getMessageText(198);
            if (text.contains(inProgressText + ":")) {
                return new TicketInProgressCommand(text.replace(inProgressText + ":", ""));
            }

            String changeDeadlineText = messageDao.getMessageText(205);
            if (text.contains(changeDeadlineText + ":")) {
                return new ChangeDeadlineCommand(text.replace(changeDeadlineText + ":", ""));
            }
        }
        Button button = buttonDao.getButton(text, botId);
        return commandDao.getCommand(button.getCommandId());
    }
}
