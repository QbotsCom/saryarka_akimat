package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.entity.Button;
import com.turlygazhy.entity.KeyboardDB;
import com.turlygazhy.exception.NoMainKeyboardException;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 12/22/16.
 */
public class KeyboardMarkUpDao extends AbstractDao {
    private static final String SELECT_KEYBOARD_BY_ID = "SELECT * FROM PUBLIC.KEYBOARD WHERE ID=?";
    private static final String SELECT_ROW_BY_ID = "SELECT * FROM PUBLIC.ROW WHERE ID=?";
    private static final String SELECT_BUTTON_BY_ID = "SELECT * FROM PUBLIC.BUTTON WHERE ID=?";
    private static final int PARAMETER_ID = 1;
    private static final int ROW_IDS_COLUMN_INDEX = 2;
    private static final int BUTTON_IDS_COLUMN_INDEX = 2;
    private static final int TEXT_COLUMN_INDEX = 2;
    public static final int INLINE_COLUMN_INDEX = 3;
    public static final String MAIN = "main";
    public static final String ADMIN = "admin";
    private final Connection connection;
    ButtonDao buttonDao = factory.getButtonDao();

    public KeyboardMarkUpDao(Connection connection) {
        this.connection = connection;
    }

    public ReplyKeyboard select(long keyboardMarkUpId, int botId) throws SQLException {
        if (keyboardMarkUpId == 0) {
            return null;
        }
        PreparedStatement ps = connection.prepareStatement(SELECT_KEYBOARD_BY_ID);
        ps.setLong(PARAMETER_ID, keyboardMarkUpId);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        resultSet.next();

        return getKeyboard(resultSet, botId);
    }

    private ReplyKeyboard getKeyboard(ResultSet rs, int botId) throws SQLException {
        String buttonIds = rs.getString(2);

        if (buttonIds == null || buttonIds.equals("0")) {
            return null;
        }

        boolean inline = rs.getBoolean(3);
        String[] rows = buttonIds.split(";");
        if (inline) {
            return getInlineKeyboard(rows, botId);
        } else {
            return getReplyKeyboard(rows, botId);
        }
    }

    private ReplyKeyboard getReplyKeyboard(String[] rows, int botId) throws SQLException {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> rowsList = new ArrayList<>();

        for (String buttonIdsString : rows) {
            KeyboardRow keyboardRow = new KeyboardRow();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId), botId);
                KeyboardButton button = new KeyboardButton();
                String buttonText = buttonFromDb.getText();
                button.setText(buttonText);
                button.setRequestContact(buttonFromDb.isRequestContact());
                keyboardRow.add(button);
            }
            rowsList.add(keyboardRow);
        }

        replyKeyboardMarkup.setKeyboard(rowsList);
        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineKeyboard(String[] rowIds, int botId) throws SQLException {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        for (String buttonIdsString : rowIds) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId), botId);
                InlineKeyboardButton button = new InlineKeyboardButton();
                String buttonText = buttonFromDb.getText();
                button.setText(buttonText);
                String url = buttonFromDb.getUrl();
                if (url != null) {
                    button.setUrl(url);
                } else {
                    button.setCallbackData(buttonText);
                }
                row.add(button);
            }
            rows.add(row);
        }

        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public ReplyKeyboard findMain(int botId) throws SQLException, NoMainKeyboardException {
        PreparedStatement ps = connection.prepareStatement("select id from KEYBOARD where comment=?");
        ps.setString(1, MAIN + botId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        if (rs.next()) {
            int id = rs.getInt(1);
            return select(id, botId);
        }
        throw new NoMainKeyboardException();
    }

    public KeyboardDB insert(String buttonIds, boolean inline, String comment) throws SQLException {
        /*ID	BUTTON_IDS	INLINE	COMMENT*/
        KeyboardDB keyboardDB = new KeyboardDB();
        keyboardDB.setButtonIds(buttonIds);
        keyboardDB.setInline(inline);
        keyboardDB.setComment(comment);
        PreparedStatement ps = connection.prepareStatement("INSERT INTO KEYBOARD VALUES(default, ?, ?, ?)");
        ps.setString(1, buttonIds);
        ps.setBoolean(2, inline);
        ps.setString(3, comment);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        keyboardDB.setId(rs.getInt(1));
        return keyboardDB;
    }

    public KeyboardDB insertMain(String buttonIds, boolean inline, String botId) throws SQLException {
        return insert(buttonIds, inline, MAIN + botId);
    }

    public void addButton(int keyboardId, int buttonId, boolean newRow) throws SQLException {
        KeyboardDB keyboardDB = selectKeyboardDB(keyboardId);
        String buttonIds = keyboardDB.getButtonIds();
        if (buttonIds.equals("0")) {
            buttonIds = String.valueOf(buttonId);
        } else {
            if (newRow) {
                buttonIds = buttonIds + ";" + buttonId;
            } else {
                buttonIds = buttonIds + "," + buttonId;
            }
        }
        keyboardDB.setButtonIds(buttonIds);
        update(keyboardDB);
    }

    public void update(KeyboardDB keyboardDB) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update KEYBOARD set BUTTON_IDS=?, INLINE=?, COMMENT=? where id=?");
        ps.setString(1, keyboardDB.getButtonIds());
        ps.setBoolean(2, keyboardDB.isInline());
        ps.setString(3, keyboardDB.getComment());
        ps.setInt(4, keyboardDB.getId());
        ps.execute();
    }

    public KeyboardDB selectKeyboardDB(int keyboardId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SELECT_KEYBOARD_BY_ID);
        ps.setInt(1, keyboardId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        KeyboardDB keyboardDB = new KeyboardDB();
        keyboardDB.setId(keyboardId);
        keyboardDB.setButtonIds(rs.getString(2));
        keyboardDB.setInline(rs.getBoolean(3));
        keyboardDB.setComment(rs.getString(4));
        return keyboardDB;
    }

    public int getMainId(int botId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select id from KEYBOARD where comment=?");
        ps.setString(1, MAIN + botId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        return rs.getInt(1);
    }

    public int getAdminId(int botId) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("select id from KEYBOARD where comment=?");
            ps.setString(1, ADMIN + botId);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            return insert("0", false, ADMIN + botId).getId();
        }
    }
}
