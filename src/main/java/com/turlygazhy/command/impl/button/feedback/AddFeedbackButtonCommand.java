package com.turlygazhy.command.impl.button.feedback;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.command.impl.collect_info.InitCollectInfoCommand;
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
 * Created by user on 4/23/17.
 */
public class AddFeedbackButtonCommand extends Command {
    private final AddButton addButton;
    private boolean newRowSetted = false;
    private InitCollectInfoCommand collectCommand;


    public AddFeedbackButtonCommand(AddButton addButton) {
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
                if (!newRowSetted) {
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
                    newRowSetted = true;
                }
                if (collectCommand == null) {
                    collectCommand = new InitCollectInfoCommand();
                }
                boolean collectInfoInited = collectCommand.execute(update, bot);
                if (collectInfoInited) {
                    addButton.setInfos(collectCommand.getInfos());
                    collectCommand = null;
                    botService.addFeedbackButton(addButton);
                    sendMessage("Button was added", chatId, bot);
                    return true;
                } else {
                    return false;
                }

        }
        return false;
    }
}
