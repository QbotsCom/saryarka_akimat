package com.turlygazhy.command.impl;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.command.impl.work_around.entity.Category;
import com.turlygazhy.entity.User;
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
    private int shownExecutorslist = 0;
    private Category category;

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
                    bot.editMessageText(new EditMessageText()
                            .setChatId(chatId)
                            .setText("Choose category")
                            .setMessageId(updateMessage.getMessageId())
                            .setReplyMarkup((InlineKeyboardMarkup) getCategoryKeyboard(category))
                    );
                    children = category.getChilds();
                    return false;
                }
                this.category = category;
                showCategoryExecutors(category);
                wt = WaitingType.CHANGE_EXECUTOR;
                return false;
            case CHANGE_EXECUTOR:
                if (updateMessageText.equals(nextText)){
                    shownExecutorslist++;
                    showCategoryExecutors(this.category);
                    return false;
                }
                if (updateMessageText.equals(prevText)){
                    shownExecutorslist--;
                    showCategoryExecutors(this.category);
                    return false;
                    //todo ihere
                }
                //todo проверить это удалить?
                //todo проверить это изменить?
                //todo check for next and prev
        }
        return false;
    }

    private void showCategoryExecutors(Category category) throws SQLException, TelegramApiException {
        boolean prev = true;
        boolean next = true;
        String text = "/add_new";//todo все должно быть на русском и браться из бд
        String[] executorIds = category.getExecutorsIds().split(",");
        int listSize = 4;
        int i = shownExecutorslist * listSize;
        if (i == 0) {
            prev = false;
        }
        int idsLength = executorIds.length;
        for (; i < idsLength; i++) {
            User user = userDao.select(Integer.parseInt(executorIds[i]));
            int userId = user.getId();
            text = text + userId + ". " + user.getUserName() + "\n/change" + userId + "\n/delete" + userId;
        }
        if (idsLength == (i + 1)) {
            next = false;
        }
        sendMessage(text, getNextPrevKeyboard(prev, next));
    }


}
