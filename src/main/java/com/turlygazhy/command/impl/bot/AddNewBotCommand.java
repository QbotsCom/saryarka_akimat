package com.turlygazhy.command.impl.bot;

import com.turlygazhy.Bot;
import com.turlygazhy.Main;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 3/21/17.
 */
public class AddNewBotCommand extends Command {
    private String name;

    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message updateMessage = update.getMessage();
        Long chatId = updateMessage.getChatId();
        String updateMessageText = updateMessage.getText();
        if (wt == null) {
            sendMessage(3, chatId, bot);
            wt = WaitingType.BOT_NAME;
            return false;
        }
        switch (wt) {
            case BOT_NAME:
                name = updateMessageText;
                sendMessage(4, chatId, bot); //todo add inline link 'what is token'
                wt = WaitingType.BOT_TOKEN;
                return false;
            case BOT_TOKEN:
                Bot newBot = null;
                try {
                    newBot = botsDao.insert(name, updateMessageText, chatId);
                    Main.register(newBot);
                    sendMessage(5, chatId, bot);
                    int newBotId = newBot.getId();
                    botService.addAdminCommand(newBotId);
                    return true;
                } catch (Exception e) {
                    if (newBot != null) {
                        botsDao.delete(newBot.getId());
                    }
                    sendMessage(6, chatId, bot);
                    wt = WaitingType.BOT_TOKEN;
                    return false;
                }
        }
        return false;
    }
}
