package com.turlygazhy;

import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.KeyboardMarkUpDao;
import com.turlygazhy.dao.impl.UserDao;
import com.turlygazhy.exception.NoMainKeyboardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yerassyl_Turlygazhy on 11/24/2016.
 */
public class Bot extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    private final String name;
    private final String token;
    private final int id;//if id=0 this is BotMother
    private final Long ownerChatId;
    private DaoFactory factory = DaoFactory.getFactory();
    private UserDao userDao = factory.getUserDao();

    private Map<Long, Conversation> conversations = new HashMap<>();

    public Bot(String name, String token, int id, Long ownerChatId) {
        this.name = name;
        this.token = token;
        this.id = id;
        this.ownerChatId = ownerChatId;
    }

    public void onUpdateReceived(Update update) {
        Conversation conversation = getConversation(update);
        Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        if (conversation.isPersonal()) {
            try {
                handleContact(updateMessage.getContact());
                conversation.handleUpdate(update, this);
            } catch (SQLException | TelegramApiException e) {
                try {
                    com.turlygazhy.entity.Message message = factory.getMessageDao().getMessage(7);
                    KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
                    sendMessage(new SendMessage()
                            .setChatId(updateMessage.getChatId())
                            .setText(message.getSendMessage().getText())
                            .setReplyMarkup(keyboardMarkUpDao.findMain(id))
                    );
                } catch (TelegramApiException | SQLException e1) {
                    e1.printStackTrace();
                } catch (NoMainKeyboardException e1) {
                    try {
                        sendMessage(new SendMessage()
                                .setChatId(updateMessage.getChatId())
                                .setText(DaoFactory.getFactory().getMessageDao().getMessage(7).getSendMessage().getText())
                        );
                    } catch (SQLException | TelegramApiException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        }
    }

    private void handleContact(Contact contact) {
        if (contact == null) {
            return;
        }
        userDao.put(contact);
    }

    private Conversation getConversation(Update update) {
        Message updateMessage = update.getMessage();
        if (updateMessage == null) {
            updateMessage = update.getCallbackQuery().getMessage();
        }
        Long chatId = updateMessage.getChatId();
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) {
            logger.info("init new conversation for '{}'", chatId);
            conversation = new Conversation();
            conversations.put(chatId, conversation);
        }
        conversation.setChatId(chatId);
        if (chatId > 0) {
            userDao.put(chatId, updateMessage.getFrom());
        } else {
            String title = updateMessage.getChat().getTitle();
            userDao.putGroup(chatId, title);
        }
        return conversation;
    }

    public String getBotUsername() {
        return name;
    }

    public String getBotToken() {
        return token;
    }

    public int getId() {
        return id;
    }

    public Long getOwnerChatId() {
        return ownerChatId;
    }
}
