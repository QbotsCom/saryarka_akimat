package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 16-May-17.
 */
public class ShowNotExecutedTicketsCommand extends Command {
    private int shownPage = 0;
    private List<Ticket> notExecutedTickets;

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
            notExecutedTickets = ticketDao.selectNotExecuted();
            chatId = updateMessage.getChatId();
            shownPage++;
            showNotExecutedTickets(bot);
            wt = WaitingType.TICKET_ID;
            return false;
        }
//        switch (wt) {
//            case TICKET_ID:
//        }
        return false;
    }

    private void showNotExecutedTickets(Bot bot) throws SQLException, TelegramApiException {
        String notExecutedTicketsText = messageDao.getMessageText(204);
        /**todo
         * берем лист
         * если показываем первую страницу кнопка прев не должна быть
         * если показываем последную страницу кнопка дальше не должна быть
         * при нажатии нехт берем следующие 7*/
        for (Ticket ticket : notExecutedTickets) {
            notExecutedTicketsText = notExecutedTicketsText + "\n/" + ticket.getId() + " " + ticket.getText().substring(0, 12) + "...";
        }
        bot.sendMessage(new SendMessage()
                .setChatId(chatId)
                .setText(notExecutedTicketsText)
                .setReplyMarkup(getNextPrevKeyboard())
        );
    }

    private ReplyKeyboard getNextPrevKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> lastRow = new ArrayList<>();
        boolean last = false;

        int startCount = (shownPage - 1) * 7;
        if (startCount < 0) {
            startCount = 0;
        }
        for (int i = startCount; i < notExecutedTickets.size(); i++) {
            if (i >= (shownPage * 4)) {
                break;
            }
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            String name = categories.get(i).getName();
            button.setText(name);
            button.setCallbackData(name);
            row.add(button);
            rows.add(row);
            if (i == categories.size() - 1) {
                last = true;
            }
        }

        if (shownPage > 1) {
            String prev = null;
            try {
                prev = messageDao.getMessageText(3);
            } catch (SQLException ignored) {
            }
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(prev);
            button.setCallbackData(prev);
            lastRow.add(button);
        }

        if (!last) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            String next = null;
            try {
                next = messageDao.getMessageText(4);
            } catch (SQLException ignored) {
            }
            button.setText(next);
            button.setCallbackData(next);
            lastRow.add(button);
        }

        rows.add(lastRow);
        keyboard.setKeyboard(rows);
        return keyboard;

    }
}
