package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 5/9/17.
 */
public class ExecuteScriptCommand extends Command {
    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        if (wt == null) {
            sendMessage("send script", update.getMessage().getChatId(), bot);
            wt = WaitingType.SCRIPT;
            return false;
        }
        switch (wt) {
            case SCRIPT:
                scriptExecutor.execute(update.getMessage().getText());
                sendMessage("done", update.getMessage().getChatId(), bot);
                return true;
        }
        return false;
    }
}
