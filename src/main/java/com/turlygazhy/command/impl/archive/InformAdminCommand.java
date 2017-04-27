package com.turlygazhy.command.impl.archive;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 1/5/17.
 */
public class InformAdminCommand extends Command {
    private Ticket ticket = new Ticket();

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
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
            sendMessage(messageDao.getMessageText(5), chatId, bot);//send text
            wt = WaitingType.TEXT;
            return false;
        }
        switch (wt) {
            case TEXT:
                ticket.setText(updateMessageText);
                sendMessage(6, chatId, bot);//send photo
                wt = WaitingType.PHOTO;
                return false;
            case PHOTO:
                if (updateMessageText != null && updateMessageText.equals(buttonDao.getButtonText(7))) {//no photo
                    sendMessage(messageDao.getMessageText(8), chatId, bot);//thank you, ticket created
                    sendTicket(ticket, bot);
                    return false;
                }
                try {
                    ticket.setPhoto(updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId());
                } catch (Exception e) {
                    throw new CannotHandleUpdateException();
                }
                sendMessage(messageDao.getMessageText(8), chatId, bot);//thank you, ticket created
                sendTicket(ticket, bot);
                return false;
        }
        return false;
    }

    private void sendTicket(Ticket ticket, Bot bot) throws TelegramApiException, SQLException {
        long chatId = 271036459L;
        sendTicket(bot, chatId);

    }

    private void sendTicket(Bot bot, long chatId) throws TelegramApiException, SQLException {
        bot.sendMessage(new SendMessage()
                .setChatId(chatId)
                .setText(messageDao.getMessageText(9) + "\n" + ticket.getText())//new ticket
        );
        if (ticket.getPhoto() != null) {
            bot.sendPhoto(new SendPhoto()
                    .setPhoto(ticket.getPhoto())
                    .setChatId(chatId)
            );
        }
    }
}
