package com.turlygazhy.command.impl.work_around;

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
 * Created by user on 4/25/17.
 */
public class FeedbackAkimatCommand extends Command {
    private List<Category> categories;
    private List<Category> childs;
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
            deleteCategories(bot);
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
                Category category;
                try {
                    category = findCategory(updateMessageText);
                } catch (Exception e) {
                    try {
                        category = findChild(updateMessageText);
                    } catch (NullPointerException e1) {
                        throw new CannotHandleUpdateException();
                    }
                } finally {
                    deleteCategories(bot);
                }
                if (category.getAfterText() != null) {
                    sendMessage(category.getAfterText(), chatId, bot);
                }
                if (category.hasChild()) {
                    Message message = bot.sendMessage(new SendMessage()
                            .setChatId(chatId)
                            .setText(chooseCategory)
                            .setReplyMarkup(getCategoryKeyboard(category))
                    );
                    categoriesMessageId = message.getMessageId();
                    childs = category.getChilds();
                    return false;
                }
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
                    sendMessage(messageDao.getMessageText(8) + ticket.getExecutorFullName() + " " + ticket.getExecutorNumber(), chatId, bot);//thank you, ticket created
                    return false;
                }
                try {
                    ticket.setPhoto(updateMessage.getPhoto().get(updateMessage.getPhoto().size() - 1).getFileId());
                } catch (Exception e) {
                    throw new CannotHandleUpdateException();
                }
                sendTicket(bot);
                sendMessage(messageDao.getMessageText(8) + ticket.getExecutorFullName() + " " + ticket.getExecutorNumber(), chatId, bot);//thank you, ticket created
                return false;
        }
        return false;
    }

    private void deleteCategories(Bot bot) {//todo еще принимать имя выбранной категории
        try {
            bot.editMessageText(new EditMessageText()
                            .setText("Category chosen")
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
        List<Long> chats = new ArrayList<>();
        List<String> numbersWithoutChat = new ArrayList<>();
        String executorsIds = ticket.getCategory().getExecutorsIds();
        String[] executors = executorsIds.split(",");
        if (executors.length >= 3) {
            if (executors[2].contains(":")) {
                executors[2] = executors[2].split(":")[0];
            }
            User user = userDao.select(Integer.parseInt(executors[2]));
            ticket.setExecutorNumber(user.getPhoneNumber());
            ticket.setExecutorFullName(user.getUserName());
        }
        for (String executorId : executors) {
            if (executorId.contains(":")) {
                String[] severalExecutorsIds = executorId.split(":");
                executorId = severalExecutorsIds[0];//todo hardcode
                //todo implement it
            }
            User user = userDao.select(Integer.parseInt(executorId));
            long chatId = user.getChatId();
            if (chatId == 0) {
                numbersWithoutChat.add(user.getPhoneNumber());
                continue;
            }
            chats.add(chatId);
        }
        if (chats.size() == 0) {
            long chatId = 271036459L;
            sendTicket(bot, chatId, numbersWithoutChat);
            return;
        }
        for (Long chat : chats) {
            sendTicket(bot, chat, numbersWithoutChat);
        }
    }

    private void sendTicket(Bot bot, long chatId, List<String> numbersWithoutChat) throws TelegramApiException, SQLException {
        bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(messageDao.getMessageText(9) + "\n" + ticket.getText())//new ticket
//                .setReplyMarkup(getInprogKeyboard())
        );
        if (ticket.getPhoto() != null) {
            bot.sendPhoto(new SendPhoto()
                    .setPhoto(ticket.getPhoto())
                    .setChatId(chatId)
            );
        }
        if (numbersWithoutChat.size() > 0) {
            String warning = messageDao.getMessageText(10);//this person does not have bot
            for (String number : numbersWithoutChat) {
                warning = warning + "\n" + number;
            }
            bot.sendMessage(new SendMessage()
                    .setChatId(chatId)
                    .setText(warning)
            );
        }
    }

    private ReplyKeyboard getInprogKeyboard() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Начать работу");
        button.setCallbackData("Начать работу");
        row.add(button);
        rows.add(row);

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private Category findChild(String updateMessageText) {
        for (Category child : childs) {
            if (child.getName().equals(updateMessageText)) {
                return child;
            }
        }
        throw new RuntimeException("Cannot find category: " + updateMessageText);
    }

    private ReplyKeyboard getCategoryKeyboard(Category category) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> lastRow = new ArrayList<>();

        for (Category child : category.getChilds()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton button = new InlineKeyboardButton();
            String name = child.getName();
            button.setText(name);
            button.setCallbackData(name);
            row.add(button);
            rows.add(row);
        }

        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText(buttonDao.getButtonText(19));
        back.setCallbackData(buttonDao.getButtonText(19));
        lastRow.add(back);

        rows.add(lastRow);
        keyboard.setKeyboard(rows);
        return keyboard;
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
