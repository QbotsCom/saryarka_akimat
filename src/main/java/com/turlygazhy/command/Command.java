package com.turlygazhy.command;

import com.turlygazhy.Bot;
import com.turlygazhy.command.impl.work_around.entity.Category;
import com.turlygazhy.dao.*;
import com.turlygazhy.dao.impl.*;
import com.turlygazhy.entity.Message;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.service.BotService;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendContact;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Contact;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yerassyl_Turlygazhy on 11/27/2016.
 */
public abstract class Command {
    protected long id;
    protected long messageId;
    protected int shownCategoriesList = 0;

    protected DaoFactory factory = DaoFactory.getFactory();
    protected UserDao userDao = factory.getUserDao();
    protected MessageDao messageDao = factory.getMessageDao();
    protected KeyboardMarkUpDao keyboardMarkUpDao = factory.getKeyboardMarkUpDao();
    protected ButtonDao buttonDao = factory.getButtonDao();
    protected CommandDao commandDao = factory.getCommandDao();
    protected ConstDao constDao = factory.getConstDao();
    protected MemberDao memberDao = factory.getMemberDao();
    protected KeyWordDao keyWordDao = factory.getKeyWordDao();
    protected ReservationDao reservationDao = factory.getReservationDao();
    protected GroupDao groupDao = factory.getGroupDao();
    protected GoalDao goalDao = factory.getGoalDao();
    protected ThesisDao thesisDao = factory.getThesisDao();
    protected SavedResultsDao savedResultsDao = factory.getSavedResultsDao();
    protected BotsDao botsDao = factory.getBotsDao();
    protected WaitingType wt;
    protected BotService botService = new BotService();
    protected VariablesDao variablesDao = factory.getVariablesDao();
    protected CategoriesDao categoriesDao = factory.getCategoriesDao();
    protected TicketDao ticketDao = factory.getTicketDao();
    protected ScriptExecutor scriptExecutor = factory.getScriptExecutor();

    protected org.telegram.telegrambots.api.objects.Message updateMessage;
    protected String updateMessageText;
    protected Long chatId;
    private Bot bot;

    public void initMessage(Update update, Bot bot) throws TelegramApiException, SQLException {
        this.bot = bot;
        updateMessage = update.getMessage();
        if (updateMessage == null) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            updateMessage = callbackQuery.getMessage();
            updateMessageText = callbackQuery.getData();
            String waitText = messageDao.getMessageText(208);
            if (chatId == null) {
                chatId = updateMessage.getChatId();
            }
            bot.editMessageText(new EditMessageText()
                    .setText(waitText)
                    .setChatId(chatId)
                    .setMessageId(updateMessage.getMessageId())
            );
        } else {
            updateMessageText = updateMessage.getText();
            if (chatId == null) {
                chatId = updateMessage.getChatId();
            }
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return is command finished
     */
    public abstract boolean execute(Update update, Bot bot) throws SQLException, TelegramApiException;

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void sendMessage(long messageId, long chatId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        sendMessage(messageId, chatId, bot, null);
    }

    public void sendMessage(int messageId) throws SQLException, TelegramApiException {
        String messageText = messageDao.getMessageText(messageId);
        bot.sendMessage(new SendMessage()
                .setChatId(chatId)
                .setText(messageText)
                .setParseMode(ParseMode.HTML)
        );
    }

    public void sendMessage(String text, long chatId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        sendMessage(text, chatId, bot, null);
    }

    public void sendMessage(long messageId, long chatId, TelegramLongPollingBot bot, Contact contact) throws SQLException, TelegramApiException {
        Message message = messageDao.getMessage(messageId);
        SendMessage sendMessage = message.getSendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId(), ((Bot) bot).getId()));
        bot.sendMessage(sendMessage);
        if (contact != null) {
            bot.sendContact(new SendContact()
                    .setChatId(chatId)
                    .setFirstName(contact.getFirstName())
                    .setLastName(contact.getLastName())
                    .setPhoneNumber(contact.getPhoneNumber())
            );
        }
    }

    public void sendMessage(String text, long chatId) throws TelegramApiException {
        bot.sendMessage(new SendMessage()
                .setChatId(chatId)
                .setText(text)
                .setParseMode(ParseMode.HTML)
        );
    }

    public void sendMessage(String text, ReplyKeyboard keyboard) throws TelegramApiException {
        bot.sendMessage(new SendMessage()
                .setChatId(chatId)
                .setText(text)
                .setParseMode(ParseMode.HTML)
                .setReplyMarkup(keyboard)
        );
    }

    public void sendMessage(int messageId, ReplyKeyboard keyboard) throws TelegramApiException, SQLException {
        String text = messageDao.getMessageText(messageId);
        bot.sendMessage(new SendMessage()
                .setChatId(chatId)
                .setText(text)
                .setParseMode(ParseMode.HTML)
                .setReplyMarkup(keyboard)
        );
    }

    public Category findCategory(String updateMessageText, List<Category> categories) {
        for (Category category : categories) {
            if (category.getName().equals(updateMessageText)) {
                return category;
            }
        }
        throw new RuntimeException("Cannot find category with name: " + updateMessageText);
    }

    public Category findChild(String updateMessageText, List<Category> children) {
        for (Category child : children) {
            if (child.getName().equals(updateMessageText)) {
                return child;
            }
        }
        throw new RuntimeException("Cannot find category: " + updateMessageText);
    }

    public void sendMessage(String text, long chatId, TelegramLongPollingBot bot, Contact contact) throws SQLException, TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setParseMode("HTML");
        bot.sendMessage(sendMessage);
        if (contact != null) {
            bot.sendContact(new SendContact()
                    .setChatId(chatId)
                    .setFirstName(contact.getFirstName())
                    .setLastName(contact.getLastName())
                    .setPhoneNumber(contact.getPhoneNumber())
            );
        }
    }

    public void sendMessageToAdmin(long messageId, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(messageId, adminChatId, bot);
    }

    public long getAdminChatId() {
        return userDao.getAdminChatId();
    }

    public void sendMessageToAdmin(long messageId, Bot bot, Contact contact) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(messageId, adminChatId, bot, contact);
    }

    public void sendMessageToAdmin(String text, TelegramLongPollingBot bot) throws SQLException, TelegramApiException {
        long adminChatId = getAdminChatId();
        sendMessage(text, adminChatId, bot);
    }

    public void sendPhotoToAdmin(String photo, Bot bot) throws TelegramApiException {
        long adminChatId = getAdminChatId();
        bot.sendPhoto(new SendPhoto()
                .setChatId(adminChatId)
                .setPhoto(photo)
        );
    }

    public boolean validateTime(String theTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm"); //HH = 24h format
        dateFormat.setLenient(false); //this will not enable 25:67 for example
        try {
            dateFormat.parse(theTime);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public ReplyKeyboard getCategoriesKeyboard(List<Category> categories) {
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
