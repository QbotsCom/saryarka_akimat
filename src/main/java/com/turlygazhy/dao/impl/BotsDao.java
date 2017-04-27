package com.turlygazhy.dao.impl;

import com.turlygazhy.Bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 3/21/17.
 */
public class BotsDao {
    private final Connection connection;

    public BotsDao(Connection connection) {
        this.connection = connection;
    }

    public List<Bot> getFor(Long ownerChatId) throws SQLException {
        List<Bot> bots = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("select * from bots where OWNER_CHAT_ID=?");
        ps.setLong(1, ownerChatId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String token = rs.getString(3);
            bots.add(new Bot(name, token, id, ownerChatId));
        }
        return bots;
    }

    public List<Bot> selectAll() throws SQLException {
        List<Bot> bots = new ArrayList<>();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM BOTS");
        ps.execute();
        ResultSet rs = ps.getResultSet();
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String token = rs.getString(3);
            Long ownerChatId = rs.getLong(4);
            Bot bot = new Bot(name, token, id, ownerChatId);
            bots.add(bot);
        }
        return bots;
    }

    public Bot insert(String name, String token, Long chatId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO BOTS VALUES(default, ?,?,?)");
        ps.setString(1, name);
        ps.setString(2, token);
        ps.setLong(3, chatId);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        return select(rs.getInt(1));
    }

    public Bot select(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from Bots where id=?");
        ps.setInt(1, id);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        String name = rs.getString(2);
        String token = rs.getString(3);
        long chatId = rs.getLong(4);
        return new Bot(name, token, id, chatId);
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("DELETE FROM BOTS WHERE ID=?");
        ps.setInt(1, id);
        ps.execute();
    }
}
