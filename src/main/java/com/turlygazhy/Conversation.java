package com.turlygazhy;

import com.turlygazhy.command.Command;
import com.turlygazhy.command.impl.work_around.entity.Category;
import com.turlygazhy.dao.CategoriesDao;
import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.KeyboardMarkUpDao;
import com.turlygazhy.dao.impl.MessageDao;
import com.turlygazhy.dao.impl.UserDao;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.User;
import com.turlygazhy.exception.CannotHandleUpdateException;
import com.turlygazhy.exception.CommandNotFoundException;
import com.turlygazhy.exception.NoMainKeyboardException;
import com.turlygazhy.service.CommandService;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
public class Conversation {
    private CommandService commandService = new CommandService();
    private Command command;
    private DaoFactory factory = DaoFactory.getFactory();
    private MessageDao messageDao = factory.getMessageDao();
    private KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    private UserDao userDao = factory.getUserDao();
    private CategoriesDao categoriesDao = factory.getCategoriesDao();
    private Long chatId;

    public void handleUpdate(Update update, Bot bot) throws SQLException, TelegramApiException {
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        String inputtedText;
        if (updateMessage == null) {
            inputtedText = update.getCallbackQuery().getData();
            updateMessage = update.getCallbackQuery().getMessage();
        } else {
            inputtedText = updateMessage.getText();
        }

        if (inputtedText == null) {
            Contact contact = updateMessage.getContact();
            if (contact != null) {
                Integer contactUserID = contact.getUserID();
                Integer fromId = update.getMessage().getFrom().getId();
                if (contactUserID != null && contactUserID.equals(fromId)) {
                    String phoneNumber = contact.getPhoneNumber();
                    phoneNumber = phoneNumber.replace("+", "");
                    boolean exist = userDao.checkPhoneNumber(phoneNumber);
                    if (exist) {
                        User user = userDao.setChatId(phoneNumber, updateMessage.getChatId());
                        String messageText = messageDao.getMessageText(1);
                        List<Category> categories = categoriesDao.selectAll();
                        for (Category category : categories) {
                            messageText = checkExecutor(user, messageText, category);
                            List<Category> childs = category.getChilds();
                            if (childs != null && childs.size() > 0) {
                                for (Category child : childs) {
                                    messageText = checkExecutor(user, messageText, child);
                                }
                            }
                        }
                        try {
                            bot.sendMessage(new SendMessage()
                                    .setChatId(updateMessage.getChatId())
                                    .setText(messageText)
                                    .setReplyMarkup(keyboardMarkUpDao.findMain(109))
                            );
                        } catch (NoMainKeyboardException ignored) {
                        }
                        return;
                    }
                }
            }
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

    private String checkExecutor(User user, String messageText, Category category) {
        String executorsIds = category.getExecutorsIds();
        if (executorsIds != null) {
            String[] executorIds = executorsIds.split(",");
            for (String executorId : executorIds) {
                if (executorId.contains(":")) {
                    String[] splitId = executorId.split(":");
                    for (String id : splitId) {
                        if (id.equals(String.valueOf(user.getId()))) {
                            messageText = messageText + "\n" + category.getName();
                            break;
                        }
                    }
                }
                if (executorId.equals(String.valueOf(user.getId()))) {
                    messageText = messageText + "\n" + category.getName();
                    break;
                }
            }
        }
        return messageText;
    }

    private void showMain(Update update, Bot bot) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(7);
        SendMessage sendMessage = message.getSendMessage();
        org.telegram.telegrambots.api.objects.Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        Long chatId = updateMessage.getChatId();
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

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }

    public boolean isPersonal() {
        return chatId > 0;
    }
}
