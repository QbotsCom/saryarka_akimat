package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Yerassyl_Turlygazhy on 11-May-17.
 */
public class TicketExecutedCommand extends Command {
    private final String ticketIdAsString;
    private int ticketId;

    public TicketExecutedCommand(String ticketIdAsString) {
        this.ticketIdAsString = ticketIdAsString;
    }

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        String updateMessageText;
        if (updateMessage == null) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage = callbackQuery.getMessage();
            updateMessageText = callbackQuery.getData();
        } else {
            updateMessageText = updateMessage.getText();
        }
        if (wt == null) {
            try {
                ticketId = Integer.parseInt(ticketIdAsString);
            } catch (Exception e) {
                throw new CannotHandleUpdateException();
            }
            // TODO: 11-May-17 отправьте пожалуйста текст с возможностью пропустить
            wt = WaitingType.TEXT;
            return false;
        }
        switch (wt) {
            case TEXT:
                // TODO: 11-May-17 сохраняем текст просим фото с возможностью пропустить
                wt = WaitingType.PHOTO;
                return false;
            case PHOTO:
                // TODO: 11-May-17 тут уже отправляем заявителю
                return true;
        }
        return false;
    }
}
