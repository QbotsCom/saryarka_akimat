package com.turlygazhy.command.impl.archive;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 1/21/17.
 */
public class PutTextInsteadButtonCommand extends Command {

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        return false;
    }
}
