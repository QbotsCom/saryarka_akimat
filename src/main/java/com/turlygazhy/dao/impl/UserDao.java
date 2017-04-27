package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.User;

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
        return user;
    }
}
