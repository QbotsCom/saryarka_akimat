package com.turlygazhy;

import com.turlygazhy.command.Command;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.ButtonDao;
import com.turlygazhy.dao.impl.KeyboardMarkUpDao;
import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CommandNotFoundException;
import com.turlygazhy.exception.CannotHandleUpdateException;
import com.turlygazhy.exception.NoMainKeyboardException;
import com.turlygazhy.service.CommandService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
public class Conversation {
    private CommandService commandService = new CommandService();
    private Command command;
    private DaoFactory factory = DaoFactory.getFactory();
    private MessageDao messageDao = factory.getMessageDao();
    private ButtonDao buttonDao = factory.getButtonDao();
    private KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();

    private WaitingType waitingType;
    private String nisha;
    private String naviki;

    public void handleUpdate(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        String inputtedText;
        if (updateMessage == null) {
            inputtedText = update.getCallbackQuery().getData();
            updateMessage = update.getCallbackQuery().getMessage();
        } else {
            inputtedText = updateMessage.getText();
        }

        try {
            command = commandService.getCommand(inputtedText, bot.getId());
        } catch (CommandNotFoundException e) {
            if (updateMessage.isGroupMessage()) {
                return;
            }
            if (command == null) {
                showMain(update, bot);
                return;
            }
        }
        boolean commandFinished = true;
        try {
            commandFinished = command.execute(update, bot);
        } catch (CannotHandleUpdateException e) {
            e.printStackTrace();
            showMain(update, bot);
        }
        if (commandFinished) {
            command = null;
        }
    }

    private void showMain(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(7);
        SendMessage sendMessage = message.getSendMessage();
        Long chatId = update.getMessage().getChatId();
        sendMessage.setChatId(chatId);
        ReplyKeyboard keyboard;
        int botId = bot.getId();
        if (botId == 0) {
            keyboard = keyboardMarkUpDao.select(message.getKeyboardMarkUpId(), botId);
        } else {
            try {
                keyboard = keyboardMarkUpDao.findMain(botId);
            } catch (NoMainKeyboardException e1) {
                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(21))
                        .setReplyMarkup(new ReplyKeyboardRemove())
                );
                return;
            }
        }
        sendMessage.setReplyMarkup(keyboard);
        bot.sendMessage(sendMessage);
    }
}
