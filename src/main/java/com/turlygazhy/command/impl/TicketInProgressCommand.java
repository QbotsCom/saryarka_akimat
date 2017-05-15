package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 15-May-17.
 */
public class TicketInProgressCommand extends Command {
    private final String ticketIdAsString;
    private int ticketId;
    private Ticket ticket;

    public TicketInProgressCommand(String ticketIdAsString) {
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
            chatId = updateMessage.getChatId();
            try {
                ticketId = Integer.parseInt(ticketIdAsString);
                ticket = ticketDao.select(ticketId);
            } catch (Exception e) {
                throw new CannotHandleUpdateException();
            }
            int addCommentMessageId = 199;
            sendMessage(addCommentMessageId, chatId, bot);
            wt = WaitingType.TEXT;
            return false;
        }
        switch (wt) {
            case TEXT:
                informAboutInProgress(updateMessageText, bot, chatId);
                return true;
        }
        return false;

    }

    private void informAboutInProgress(String comment, Bot bot, Long executorChatId) throws SQLException, TelegramApiException {
        long creatorChatId = ticket.getCreatorChatId();
        String yourTicketInProgressText = messageDao.getMessageText(200);
        sendMessage(yourTicketInProgressText + ": " + ticket.getText() + "\n" + comment, creatorChatId, bot);
        String executorsIds = ticket.getCategory().getExecutorsIds();
        String answeredToTicketText = messageDao.getMessageText(201);
        List<User> anotherExecutors = new ArrayList<>();
        User answeredExecutor = null;
        for (String executor : executorsIds.split(",")) {
            if (executor.contains(":")) {
                executor = executor.split(":")[0];// TODO: 15-May-17 hardcode
            }
            User user = userDao.select(Integer.parseInt(executor));
            if (user.getChatId() != executorChatId) {
                anotherExecutors.add(user);
            } else {
                answeredExecutor = user;
            }
        }
        String informText = answeredExecutor.getUserName() + " " + answeredToTicketText + " " + ticket.getText() + ": " + comment;
        for (User anotherExecutor : anotherExecutors) {
            sendMessage(informText, anotherExecutor.getChatId(), bot);
        }
    }
}
