package com.turlygazhy.command.impl.collect_info;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.command.impl.collect_info.entity.Info;
import com.turlygazhy.command.impl.collect_info.entity.InfoType;
import com.turlygazhy.command.impl.collect_info.exception.TypeNotFoundException;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 4/23/17.
 */
public class InitCollectInfoCommand extends Command {
    private static final String YES = "/yes";
    private static final String NO = "/no";
    private static final String ENOUGH = "/enough";
    private List<Info> infos = new ArrayList<>();
    private Info info;

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
            askInfoName(bot);
            return false;
        }
        switch (wt) {
            case INFO_NAME:
                if (updateMessageText.equals(ENOUGH)) {
                    return true;
                }
                info.setName(updateMessageText);
                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(InfoType.getTextForProvidingTypes())
                );
                wt = WaitingType.INFO_TYPE;
                return false;
            case INFO_TYPE:
                try {
                    InfoType type = InfoType.getType(updateMessageText);
                    info.setType(type);
                    sendMessage("Need options\n" + YES + "\n" + NO, chatId, bot);
                    wt = WaitingType.NEED_OPTIONS;
                    return false;
                } catch (TypeNotFoundException e) {
                    throw new CannotHandleUpdateException();
                }
            case NEED_OPTIONS:
                if (updateMessageText.equals(YES)) {
                    wt = WaitingType.OPTION;
                    sendMessage("Insert option", chatId, bot);
                    return false;
                }
                if (updateMessageText.equals(NO)) {
                    infos.add(info);
                    askInfoName(bot);
                    return false;
                }
                throw new CannotHandleUpdateException();
            case OPTION:
                if (updateMessageText.equals(ENOUGH)) {
                    infos.add(info);
                    askInfoName(bot);
                    return false;
                }
                info.addOption(updateMessageText);
                sendMessage("Option was saved\nInsert one more or click " + ENOUGH, chatId, bot);
                return false;
        }
        return false;
    }

    private void askInfoName(Bot bot) throws SQLException, TelegramApiException {
        info = new Info();
        String sendInfoText = "Send info name";
        if (infos.size() > 0) {
            sendInfoText = sendInfoText + " or click " + ENOUGH;
        }
        sendMessage(sendInfoText, chatId, bot);//это вопрос который выйдет при сборе инфо
        wt = WaitingType.INFO_NAME;
    }

    public List<Info> getInfos() {
        return infos;
    }
}
