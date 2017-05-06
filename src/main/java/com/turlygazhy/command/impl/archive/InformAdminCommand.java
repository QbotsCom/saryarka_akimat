package com.turlygazhy.command.impl.archive;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.command.impl.work_around.entity.Category;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.entity.User;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CannotHandleUpdateException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
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
 * Created by user on 1/5/17.
 */
public class InformAdminCommand extends Command {
    private List<Category> categories;
    private List<Category> children;
    private int shownCategoriesList = 0;
    private Ticket ticket = new Ticket();
    private Integer categoriesMessageId;

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
        String chooseCategory = messageDao.getMessageText(2);
        String prev = messageDao.getMessageText(3);
        String next = messageDao.getMessageText(4);
        if (wt == null) {
            chatId = updateMessage.getChatId();
            categories = categoriesDao.selectAll();
            shownCategoriesList++;
            showCategories(bot, chooseCategory);
            return false;
        }
        if (updateMessageText != null && updateMessageText.equals(buttonDao.getButtonText(19))) {//back
            String backWasClicked = messageDao.getMessageText(185);
            deleteCategories(bot, backWasClicked);
            showCategories(bot, chooseCategory);
            return false;
        }
        switch (wt) {
            case CATEGORY:
                if (updateMessageText.equals(prev)) {
                    shownCategoriesList--;
                    bot.editMessageText(new EditMessageText()
                            .setText(chooseCategory)
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) getCategoriesKeyboard())
                            .setMessageId(updateMessage.getMessageId())
                    );
                    return false;
                }
                if (updateMessageText.equals(next)) {
                    shownCategoriesList++;
                    bot.editMessageText(new EditMessageText()
                            .setText(chooseCategory)
                            .setChatId(chatId)
                            .setReplyMarkup((InlineKeyboardMarkup) getCategoriesKeyboard())
                            .setMessageId(updateMessage.getMessageId())
                    );
                    return false;
                }
                Category category = findCategory(updateMessageText);
                deleteCategories(bot, category.getName());

                ticket.setCategory(category);
                sendMessage(messageDao.getMessageText(5), chatId, bot);//send text
                wt = WaitingType.TEXT;
                return false;
            case TEXT:
                ticket.setText(updateMessageText);
                sendMessage(6, chatId, bot);//send photo
                wt = WaitingType.PHOTO;
                return false;
            case PHOTO:
                if (updateMessageText != null && updateMessageText.equals(buttonDao.getButtonText(7))) {//no photo
                    sendTicket(bot);
                    answerToUser(bot);
                    return false;
                }
                try {
                    ticket.setPhoto(updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId());
                } catch (Exception e) {
                    throw new CannotHandleUpdateException();
                }
                sendTicket(bot);
                answerToUser(bot);
                return false;
        }
        return false;
    }

    private void answerToUser(Bot bot) throws SQLException, TelegramApiException {
        String weGetYourOffer = messageDao.getMessageText(187);
        sendMessage(weGetYourOffer, chatId, bot);
    }

    private void deleteCategories(Bot bot, String categoryName) {
        try {
            bot.editMessageText(new EditMessageText()
                    .setText(categoryName)
                    .setMessageId(categoriesMessageId)
                    .setChatId(chatId)
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void showCategories(Bot bot, String chooseCategory) throws TelegramApiException {
        Message message = bot.sendMessage(new SendMessage()
                .setChatId(chatId)
                .setText(chooseCategory)
                .setReplyMarkup(getCategoriesKeyboard())
        );
        categoriesMessageId = message.getMessageId();
        wt = WaitingType.CATEGORY;
    }

    private void sendTicket(Bot bot) throws TelegramApiException, SQLException {
        User kontrolOtdel = userDao.select(1);
        String newOfferForImproving = messageDao.getMessageText(186);
        bot.sendMessage(new SendMessage()
                .setChatId(kontrolOtdel.getChatId())
                .setText(newOfferForImproving + "'" + ticket.getCategory().getName() + "'\n" + ticket.getText())//new ticket
        );
        if (ticket.getPhoto() != null) {
            bot.sendPhoto(new SendPhoto()
                    .setPhoto(ticket.getPhoto())
                    .setChatId(chatId)
            );
        }
    }

    private Category findCategory(String updateMessageText) {
        for (Category category : categories) {
            if (category.getName().equals(updateMessageText)) {
                return category;
            }
        }
        throw new RuntimeException("Cannot find category with name: " + updateMessageText);
    }

    private ReplyKeyboard getCategoriesKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> lastRow = new ArrayList<>();
        boolean last = false;

        int startCount = (shownCategoriesList - 1) * 4;
        if (startCount < 0) {
            startCount = 0;
        }
        for (int i = startCount; i < categories.size(); i++) {
            if (i >= (shownCategoriesList * 4)) {
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

        if (shownCategoriesList > 1) {
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
