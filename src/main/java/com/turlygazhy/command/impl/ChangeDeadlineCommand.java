package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.exception.CannotHandleUpdateException;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Yerassyl_Turlygazhy on 18-May-17.
 */
public class ChangeDeadlineCommand extends Command {
    private Ticket ticket;

    public ChangeDeadlineCommand(String ticketId) {
        try {
            ticket = ticketDao.select(Integer.parseInt(ticketId));
        } catch (Exception e) {
            throw new CannotHandleUpdateException();
        }
    }


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update);
        if (wt == null) {
            initChatId();

        }
        switch (wt) {

        }
        return false;
    }


}
