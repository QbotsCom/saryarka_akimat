package com.turlygazhy;

import com.turlygazhy.dao.DaoFactory;
import com.turlygazhy.dao.impl.KeyboardMarkUpDao;
import com.turlygazhy.exception.NoMainKeyboardException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
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

    private Map<Long, Conversation> conversations = new HashMap<>();

    public Bot(String name, String token, int id, Long ownerChatId) {
        this.name = name;
        this.token = token;
        this.id = id;
        this.ownerChatId = ownerChatId;
    }

    public void onUpdateReceived(Update update) {
        Conversation conversation = getConversation(update);
        try {
            conversation.handleUpdate(update, this);
        } catch (SQLException | TelegramApiException e) {
            try {
                DaoFactory factory = DaoFactory.getFactory();
                com.turlygazhy.entity.Message message = factory.getMessageDao().getMessage(7);
                KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
                sendMessage(new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(message.getSendMessage().getText())
                        .setReplyMarkup(keyboardMarkUpDao.findMain(id))
                );
            } catch (TelegramApiException | SQLException e1) {
                e1.printStackTrace();
            } catch (NoMainKeyboardException e1) {
                try {
                    sendMessage(new SendMessage()
                            .setChatId(update.getMessage().getChatId())
                            .setText(DaoFactory.getFactory().getMessageDao().getMessage(7).getSendMessage().getText())
                    );
                } catch (SQLException | TelegramApiException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }

    private Conversation getConversation(Update update) {
        Message message = update.getMessage();
        if (message == null) {
            message = update.getCallbackQuery().getMessage();
        }
        Long chatId = message.getChatId();
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) {
            logger.info("initMessage new conversation for '{}'", chatId);
            conversation = new Conversation();
            conversations.put(chatId, conversation);
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
