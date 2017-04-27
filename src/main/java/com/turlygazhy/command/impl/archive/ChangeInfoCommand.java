package com.turlygazhy.command.impl.archive;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.WaitingType;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by user on 1/2/17.
 */
public class ChangeInfoCommand extends Command {
    private String text;

    @Override
    public boolean execute(Update update, Bot bot) throws TelegramApiException, SQLException {
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
            int giveMeTextMessageId = 13;
            sendMessage(giveMeTextMessageId, chatId, bot);
            wt = WaitingType.TEXT;
            return false;
        }
        switch (wt) {
            case TEXT:
                text = updateMessageText;
                int giveMePhotoMessageId = 14;
                sendMessage(giveMePhotoMessageId, chatId, bot);
                wt = WaitingType.PHOTO;
                return false;
            case PHOTO:
                String withoutPhoto = buttonDao.getButtonText(7);
                if (updateMessageText != null && updateMessageText.equals(withoutPhoto)) {
                    messageDao.update(this.messageId, null, text);
                } else {
                    List<PhotoSize> photos = updateMessage.getPhoto();
                    String photo = photos.get(photos.size() - 1).getFileId();
                    messageDao.update(this.messageId, photo, text);
                }
                int changesSavedMessageId = 15;
                sendMessage(changesSavedMessageId, chatId, bot);
                return true;
        }
        return true;
    }
}