package com.turlygazhy.command.impl.bot;

import com.turlygazhy.Bot;
import com.turlygazhy.command.Command;
import com.turlygazhy.command.CommandType;
import com.turlygazhy.command.impl.button.AddButtonCommand;
import com.turlygazhy.entity.AddButton;
import com.turlygazhy.entity.Button;
import com.turlygazhy.entity.KeyboardDB;
import com.turlygazhy.entity.WaitingType;
import com.turlygazhy.exception.CommandNotFoundException;
import com.turlygazhy.exception.CannotHandleUpdateException;
import com.turlygazhy.exception.DataDoesNotExistException;
import com.turlygazhy.exception.NoMainKeyboardException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.CallbackQuery;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 3/21/17.
 */
public class MyBotsCommand extends Command {
    private static final String ID_PREFIX = "/id";
    private Bot changeBot;
    private AddButton addButton = new AddButton();
    private String change;
    private AddButtonCommand addButtonCommand;


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
            chatId = updateMessage.getChatId();
            change = buttonDao.getButtonText(6);
            List<Bot> bots = botsDao.getFor(chatId);
            if (bots.size() == 0) {
                sendMessage(2, chatId, bot);
                return true;
            } else {
                com.turlygazhy.entity.Message chooseBotMessage = messageDao.getMessage(8);
                String text = chooseBotMessage.getSendMessage().getText() + "\n";
                for (Bot bot1 : bots) {
                    text = text + "\n" + ID_PREFIX + bot1.getId() + " " + bot1.getBotUsername();//todo inline buttons will be better
                }
                bot.sendMessage(new SendMessage()
                        .setChatId(chatId)
                        .setText(text)
                        .setReplyMarkup(keyboardMarkUpDao.select(chooseBotMessage.getKeyboardMarkUpId(), bot.getId()))
                );
                wt = WaitingType.BOT_ID;
                return false;
            }
        }
        String chooseActionMessageText = messageDao.getMessageText(9);
        switch (wt) {
            case BOT_ID:
                try {
                    changeBot = botsDao.select(Integer.parseInt(updateMessageText.replaceAll(ID_PREFIX, "")));
                    addButton.setBotId(changeBot.getId());
                    //if this person is not owner
                    if (!changeBot.getOwnerChatId().equals(update.getMessage().getChatId())) {
                        com.turlygazhy.entity.Message message = messageDao.getMessage(7);
                        SendMessage sendMessage = message.getSendMessage();
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
                                return true;
                            }
                        }
                        sendMessage.setReplyMarkup(keyboard);
                        bot.sendMessage(sendMessage);
                        return true;
                    }
                    sendMainForChange(bot);
                    wt = WaitingType.BOT_CHANGE_ACTION;
                    return false;
                } catch (NoMainKeyboardException e) {
                    KeyboardDB keyboardDB = keyboardMarkUpDao.insertMain("0", false, String.valueOf(Integer.parseInt(updateMessageText.replaceAll(ID_PREFIX, ""))));
                    addButton.setKeyboardId(keyboardDB.getId());
                    bot.sendMessage(new SendMessage()
                            .setText(chooseActionMessageText)
                            .setChatId(chatId)
                            .setReplyMarkup(getNewKeyboard())
                    );
                    wt = WaitingType.BOT_CHANGE_ACTION;
                    return false;
                } catch (Exception e) {
                    sendMessage(10, chatId, bot);//todo что за 10
                    return false;
                }
            case BOT_CHANGE_ACTION:
                String addNewButton = buttonDao.getButtonText(4);
                String changePlaces = buttonDao.getButtonText(5);
                if (updateMessageText.equals(addNewButton)) {
                    sendMessage(11, chatId, bot);
                    wt = WaitingType.NEW_BUTTON_NAME;
                    return false;
                }
                if (updateMessageText.equals(changePlaces)) {
                    //todo implement
                }
                if (updateMessageText.startsWith(change)) {
                    String buttonNameForChange = updateMessageText.replace(change, "").trim();
                    try {
                        Button button = buttonDao.getButton(buttonNameForChange, bot.getId());
                    } catch (CommandNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    //todo implement
                    /*todo берем тип команды, берем что можно для этого типа изменить
                    * предоставляем список*/
                    /*todo как обезопасить, чтоб другие не меняли твои кнопки, к кнопке id бота прикрепить надо
                    * если чел может менять бота значить может менять и кнопку*/
                }
                sendMessage(7, chatId, bot);
                return true;
            case NEW_BUTTON_NAME:
                addButton.setName(updateMessageText);
                bot.sendMessage(new SendMessage()
                        .setText(messageDao.getMessageText(12) + CommandType.getCommandList())//todo не хватает урла: что это такое
                        .setChatId(chatId)
                );
                wt = WaitingType.COMMAND_TYPE;
                return false;
            case COMMAND_TYPE:
                if (addButtonCommand == null) {
                    try {
                        int commandTypeId = CommandType.getTypeId(updateMessageText);
                        addButton.setCommandTypeId(commandTypeId);
                        addButtonCommand = new AddButtonCommand(addButton);
                    } catch (DataDoesNotExistException e) {
                        throw new CannotHandleUpdateException();
                    }
                }
                boolean finished = addButtonCommand.execute(update, bot);
                if (!finished) {
                    return false;
                }
                addButtonCommand = null;
                wt = WaitingType.BOT_CHANGE_ACTION;
                try {
                    sendMainForChange(bot);
                } catch (NoMainKeyboardException e) {
                    //todo по идее не должно падать
                }
                return false;
        }
        return true;
    }

    private void sendMainForChange(Bot bot) throws SQLException, TelegramApiException, NoMainKeyboardException {
        String chooseActionMessageText = messageDao.getMessageText(9);
        ReplyKeyboard mainKeyboard = keyboardMarkUpDao.findMain(changeBot.getId());
        addButton.setKeyboardId(keyboardMarkUpDao.getMainId(changeBot.getId()));
        addButton.setBotId(changeBot.getId());
        mainKeyboard = addChangeButtons(mainKeyboard);
        bot.sendMessage(new SendMessage()
                .setText(chooseActionMessageText)
                .setChatId(chatId)
                .setReplyMarkup(mainKeyboard)
        );
    }

    private ReplyKeyboard getNewKeyboard() throws SQLException {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(new KeyboardButton(buttonDao.getButtonText(4)));
        keyboardButtons.add(new KeyboardButton(buttonDao.getButtonText(5)));
        rows.add(keyboardButtons);
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private ReplyKeyboard addChangeButtons(ReplyKeyboard mainKeyboard) throws SQLException {
        if (mainKeyboard == null) {
            return getNewKeyboard();
        }

        ReplyKeyboardMarkup keyboard = (ReplyKeyboardMarkup) mainKeyboard;
        List<KeyboardRow> keyboardRows = keyboard.getKeyboard();

        for (KeyboardRow keyboardRow : keyboardRows) {
            for (KeyboardButton keyboardButton : keyboardRow) {
                keyboardButton.setText(change + " " + keyboardButton.getText());
            }
        }

        KeyboardRow keyboardButtons = new KeyboardRow();
        keyboardButtons.add(new KeyboardButton(buttonDao.getButtonText(4)));
        keyboardButtons.add(new KeyboardButton(buttonDao.getButtonText(5)));

        keyboardRows.add(keyboardButtons);
        return keyboard;
    }
}
