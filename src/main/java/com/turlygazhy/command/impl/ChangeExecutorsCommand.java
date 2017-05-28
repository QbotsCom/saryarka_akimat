package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.command.impl.work_around.entity.Category;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by user on 5/28/17.
 */
public class ChangeExecutorsCommand extends Command {
    private List<Category> categories;
    private List<Category> children;


    @Override
    public boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException {
        initMessage(update, bot);
        String chooseCategory = messageDao.getMessageText(2);
        String prev = messageDao.getMessageText(3);
        String next = messageDao.getMessageText(4);
        if (wt == null) {
            categories = categoriesDao.selectAll();
            shownCategoriesList++;
            sendMessage("Choose category", getCategoriesKeyboard(categories));//choose category
            wt = WaitingType.CATEGORY;
            return false;
        }
        switch (wt) {
            case CATEGORY:
                //todo копипаста проверить весь case
                if (updateMessageText.equals(prev)) {
                    shownCategoriesList--;
                    bot.editMessageText(new EditMessageText()
                            .setText(chooseCategory)
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) getCategoriesKeyboard(categories))
                            .setMessageId(updateMessage.getMessageId())
                    );
                    return false;
                }
                if (updateMessageText.equals(next)) {
                    shownCategoriesList++;
                    bot.editMessageText(new EditMessageText()
                            .setText(chooseCategory)
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) getCategoriesKeyboard(categories))
                            .setMessageId(updateMessage.getMessageId())
                    );
                    return false;
                }
                Category category = null;
                try {
                    category = findCategory(updateMessageText, categories);
                } catch (Exception e) {
                    try {
                        category = findChild(updateMessageText, children);
                    } catch (NullPointerException e1) {
                        throw new CannotHandleUpdateException();
                    }
                }
                if (category.getAfterText() != null) {
                    sendMessage(category.getAfterText(), chatId, bot);
                }
                if (category.hasChild()) {
                    children = category.getChilds();
                    return false;
                }
        }
        return false;
    }
}
