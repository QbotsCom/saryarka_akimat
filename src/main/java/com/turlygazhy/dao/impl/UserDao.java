package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.User;
import org.telegram.telegrambots.api.objects.Contact;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by user on 12/18/16.
 */
public class UserDao {
    private static final String SELECT_ADMIN_CHAT_ID = "SELECT * FROM PUBLIC.USER WHERE ID=?";
    private static final int PARAMETER_USER_ID = 1;
    private static final int CHAT_ID_COLUMN_INDEX = 2;
    public static final int ADMIN_ID = 1;
    public static final int ID_COLUMN_INDEX = 1;
    public static final int USERNAME_COLUMN_INDEX = 3;
    public static final int PHONE_COLUMN_INDEX = 4;
    public static final int IS_EXECUTOR_COLUMN_INDEX = 5;
    public static final int IS_AKIMAT_WORKER_COLUMN_INDEX = 6;
    private Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public Long getAdminChatId() {
        try {
            PreparedStatement ps = connection.prepareStatement(SELECT_ADMIN_CHAT_ID);
            ps.setLong(PARAMETER_USER_ID, ADMIN_ID);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            return rs.getLong(CHAT_ID_COLUMN_INDEX);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAdmin(Long chatId) {
        return Objects.equals(chatId, getAdminChatId());
    }

    public boolean checkPhoneNumber(String phoneNumber) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM USER where PHONENUMBER=?");
        ps.setString(1, phoneNumber);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        return rs.next();
    }

    public User setChatId(String phoneNumber, Long chatId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update USER set chat_id=? where PHONENUMBER=?");
        ps.setLong(1, chatId);
        ps.setString(2, phoneNumber);
        ps.execute();
        return select(phoneNumber);
    }

    public User select(String phoneNumber) throws SQLException {
        User user = new User();
        PreparedStatement ps = connection.prepareStatement("select * from User where PHONENUMBER=?");
        ps.setString(1, phoneNumber);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        user.setId(rs.getInt(ID_COLUMN_INDEX));
        user.setChatId(rs.getLong(CHAT_ID_COLUMN_INDEX));
        user.setUserName(rs.getString(USERNAME_COLUMN_INDEX));
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    public User select(int id) throws SQLException {
        User user = new User();
        PreparedStatement ps = connection.prepareStatement("select * from User where id=?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        user.setId(rs.getInt(ID_COLUMN_INDEX));
        user.setChatId(rs.getLong(CHAT_ID_COLUMN_INDEX));
        user.setUserName(rs.getString(USERNAME_COLUMN_INDEX));
        user.setPhoneNumber(rs.getString(PHONE_COLUMN_INDEX));
        user.setExecutor(rs.getBoolean(IS_EXECUTOR_COLUMN_INDEX));
        user.setAkimatWorker(rs.getBoolean(IS_AKIMAT_WORKER_COLUMN_INDEX));
        return user;
    }

    public void put(Long chatId, org.telegram.telegrambots.api.objects.User user) {
        try {
            PreparedStatement select = connection.prepareStatement("select * from user0 where USER_ID=?");
            select.setInt(1, user.getId());
            select.execute();
            ResultSet rs = select.getResultSet();
            rs.next();
            rs.getInt(1);

            PreparedStatement ps = connection.prepareStatement("UPDATE USER0 SET FIRSTNAME=?, LASTNAME=?, USERNAME=? WHERE CHAT_ID=?");
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getUserName());
            ps.setLong(4, chatId);
            ps.execute();
        } catch (Exception ignored) {
            try {
                /*ID USER_ID FIRSTNAME LASTNAME USERNAME PHONENUMBER CHAT_ID*/
                PreparedStatement ps = connection.prepareStatement("INSERT INTO USER0 VALUES(default, ?, ?, ?, ?, ?, ?)");
                ps.setInt(1, user.getId());
                ps.setString(2, user.getFirstName());
                ps.setString(3, user.getLastName());
                ps.setString(4, user.getUserName());
                ps.setString(5, null);
                ps.setLong(6, chatId);
                ps.execute();
            } catch (SQLException ignored2) {
            }
        }
    }

    public void put(Contact contact) {
        try {
            Integer userID = contact.getUserID();
            if (userID == null) {
                return;
            }
            PreparedStatement ps = connection.prepareStatement("update user0 set FIRSTNAME=?, LASTNAME=?, PHONENUMBER=? where user_id=?");
            ps.setString(1, contact.getFirstName());
            ps.setString(2, contact.getLastName());
            ps.setString(3, contact.getPhoneNumber());
            ps.setLong(4, userID);
            ps.execute();
        } catch (Exception ignored) {
        }
    }

    public void putGroup(Long chatId, String title) {
        try {
            PreparedStatement select = connection.prepareStatement("select * from GROUP0 where CHAT_ID=?");
            select.setLong(1, chatId);
            select.execute();
            ResultSet rs = select.getResultSet();
            rs.next();
            rs.getInt(1);

            PreparedStatement ps = connection.prepareStatement("UPDATE GROUP0 SET TITLE=? WHERE CHAT_ID=?");
            ps.setString(1, title);
            ps.setLong(2, chatId);
            ps.execute();
        } catch (Exception ignored) {
            try {
                /*ID  	TITLE  	CHAT_ID  */
                PreparedStatement ps = connection.prepareStatement("INSERT INTO GROUP0 VALUES(default, ?, ?)");
                ps.setString(1, title);
                ps.setLong(2, chatId);
                ps.execute();
            } catch (SQLException ignored2) {
            }
        }
    }
}
