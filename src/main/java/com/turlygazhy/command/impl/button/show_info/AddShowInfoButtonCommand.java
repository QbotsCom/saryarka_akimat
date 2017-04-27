package com.turlygazhy.command.impl.button.show_info;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.entity.AddButton;
import com.turlygazhy.entity.Const;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;

/**
 * Created by user on 4/16/17.
 */
public class AddShowInfoButtonCommand extends Command {
    private final AddButton addButton;

    public AddShowInfoButtonCommand(AddButton addButton) {
        this.addButton = addButton;
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
            chatId = updateMessage.getChatId();// здесь как сообщ вставим „этот текст можно изменить в админке„
            sendMessage(17, chatId, bot);//если это первая кнопка в мэйне то не спрашивать роу
            addButton.setMessageText(messageDao.getMessageText(Const.CHANGE_THIS_IN_ADMIN_MESSAGE_ID));
            wt = WaitingType.IS_NEW_ROW;
            return false;
        }
        switch (wt) {
            case IS_NEW_ROW:// is new row нужно вынести на ступень выше
                boolean correctAnswer = false;
                if (updateMessageText.equals(buttonDao.getButtonText(10))) {
                    addButton.setNewRow(true);
                    correctAnswer = true;
                }
                if (updateMessageText.equals(buttonDao.getButtonText(11))) {
                    addButton.setNewRow(false);
                    correctAnswer = true;
                }
                if (!correctAnswer) {
                    throw new CannotHandleUpdateException();
                }
                sendMessage(168, chatId, bot);//send link
                wt = WaitingType.INLINE_LINK;
                return false;
            case INLINE_LINK:
                boolean noLink = false;
                String noLinkText = buttonDao.getButtonText(34);
                if (updateMessageText.equals(noLinkText)) {
                    noLink = true;
                }
                if (!noLink) {
                    addButton.setLinkForInline(updateMessageText);
                }
                sendMessage(26, chatId, bot);
                wt = WaitingType.LINK_NAME;
                return false;
            case LINK_NAME:
                addButton.setLinkForInlineName(updateMessageText);
                botService.addShowInfoButton(addButton);
                sendMessage(25, chatId, bot);//button was added
                return true;
        }
        return false;
    }
}
