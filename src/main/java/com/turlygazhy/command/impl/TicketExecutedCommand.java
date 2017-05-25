package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 11-May-17.
 */
public class TicketExecutedCommand extends Command {
    private final String ticketIdAsString;
    private int ticketId;
    private Ticket ticket;
    private String answerText;
    private String answerPhoto;

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
            chatId = updateMessage.getChatId();
            try {
                ticketId = Integer.parseInt(ticketIdAsString);
                ticket = ticketDao.select(ticketId);
            } catch (Exception e) {
                throw new CannotHandleUpdateException();
            }
            String finishingTicketText = messageDao.getMessageText(191);
            sendMessage(finishingTicketText + ":\n" + ticket.getText(), chatId, bot);
            String photo = ticket.getPhoto();
            if (photo != null) {
                bot.sendPhoto(new SendPhoto()
                        .setPhoto(photo)
                        .setChatId(chatId)
                );
            }
            String sendText = messageDao.getMessageText(5);
            bot.sendMessage(new SendMessage()
                    .setText(sendText)
                    .setChatId(chatId)
                    .setReplyMarkup(getSkipKeyboard())
            );
            wt = WaitingType.TEXT;
            return false;
        }
        String skipText = messageDao.getMessageText(192);
        switch (wt) {
            case TEXT:
                if (!updateMessageText.equals(skipText)) {
                    answerText = updateMessageText;
                }
                String sendPhoto = messageDao.getMessageText(6);
                bot.sendMessage(new SendMessage()
                        .setText(sendPhoto)
                        .setChatId(chatId)
                        .setReplyMarkup(getSkipKeyboard())
                );
                wt = WaitingType.PHOTO;
                return false;
            case PHOTO:
                List<PhotoSize> photos = updateMessage.getPhoto();
                if (photos != null) {
                    answerPhoto = photos.get(photos.size() - 1).getFileId();
                }
                askConfirmAnswer(bot);
                wt = WaitingType.CONFIRM;
                return false;
            case CONFIRM:
                String sendText = buttonDao.getButtonText(194);
                String cancelText = buttonDao.getButtonText(195);
                if (updateMessageText.equals(sendText)) {
                    ticketDao.complete(ticket);
//                    SheetsAdapter.completeTicket("list", 'E', ticket.getGoogleSheetRowId(), messageDao.getMessageText(203));
                    //todo
                    answerToUser(bot);
                    sendMessage(196, chatId, bot);//Ваш ответ передан заявителю
                    return true;
                }
                if (updateMessageText.equals(cancelText)) {//Вы отменили действие
                    sendMessage(197, chatId, bot);
                    return true;
                }
        }
        return false;
    }

    private void answerToUser(Bot bot) throws TelegramApiException, SQLException {
        String yourTicketHasAnswer = messageDao.getMessageText(195);//На ваш запрос получен ответ
        long creatorChatId = ticket.getCreatorChatId();
        sendMessage(yourTicketHasAnswer + ":\n" + messageDao.getMessageText(194), creatorChatId, bot);
        if (answerText == null && answerPhoto == null) {
            sendMessage(194, creatorChatId, bot);//По Вашей заявке проведена работа
        }
        if (answerText != null) {
            sendMessage(answerText, creatorChatId, bot);
        }
        if (answerPhoto != null) {
            bot.sendPhoto(new SendPhoto()
                    .setPhoto(answerPhoto)
                    .setChatId(creatorChatId)
            );
        }
        int groupId = ticket.getCategory().getGroupId();
        if (groupId != 0) {
            long groupChatId = groupDao.select(groupId).getChatId();
            String ticketExecutedText = messageDao.getMessageText(209);/*<b>Заявка ticketId отработана</b>. Текст заявки: <i> ticketText</i>*/
            ticketExecutedText = ticketExecutedText.replace("ticketId", String.valueOf(ticket.getId()));
            ticketExecutedText = ticketExecutedText.replace("ticketText", ticket.getText());
            sendMessage(ticketExecutedText, groupChatId, bot);
            String photo = ticket.getPhoto();
            if (photo != null) {
                bot.sendPhoto(new SendPhoto()
                        .setChatId(groupChatId)
                        .setPhoto(photo)
                );
            }
            if (answerText != null) {
                String answerWithText = messageDao.getMessageText(210);/*Текст ответа: <i>answerText</i>*/
                answerWithText = answerWithText.replace("answerText", answerText);
                sendMessage(answerWithText, groupChatId, bot);
            }
            if (answerPhoto != null) {
                bot.sendPhoto(new SendPhoto()
                        .setPhoto(answerPhoto)
                        .setChatId(groupChatId)
                );
            }
        }

    }

    private void askConfirmAnswer(Bot bot) throws SQLException, TelegramApiException {
        String youAnswerText = messageDao.getMessageText(193);
        if (answerText == null) {
            if (answerPhoto == null) {
                String yourOrderIsCompleted = messageDao.getMessageText(194);
                bot.sendMessage(new SendMessage()
                        .setText(youAnswerText + "\n" + yourOrderIsCompleted)
                        .setChatId(chatId)
                        .setReplyMarkup(keyboardMarkUpDao.select(159, 109))
                );
            } else {
                bot.sendPhoto(new SendPhoto()
                        .setPhoto(answerPhoto)
                        .setChatId(chatId)
                );
                bot.sendMessage(new SendMessage()
                        .setText(youAnswerText)
                        .setChatId(chatId)
                        .setReplyMarkup(keyboardMarkUpDao.select(159, 109))
                );
            }
        } else {
            if (answerPhoto != null) {
                bot.sendPhoto(new SendPhoto()
                        .setPhoto(answerPhoto)
                        .setChatId(chatId)
                );
            }
            bot.sendMessage(new SendMessage()
                    .setText(youAnswerText + ":\n" + answerText)
                    .setChatId(chatId)
                    .setReplyMarkup(keyboardMarkUpDao.select(159, 109))
            );
        }

    }

    private ReplyKeyboard getSkipKeyboard() throws SQLException {
        String skipText = messageDao.getMessageText(192);
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(skipText);
        button.setCallbackData(skipText);
        row.add(button);
        rows.add(row);
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
