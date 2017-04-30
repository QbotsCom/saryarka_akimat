package com.turlygazhy.dao.impl;

import com.turlygazhy.entity.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by user on 4/30/17.
 */
public class TicketDao {
    private final Connection connection;

    public TicketDao(Connection connection) {
        this.connection = connection;
    }

    public void insert(Ticket ticket) throws SQLException {//should return Ticket
        /*ID, 1 CATEGORY, 2 TEXT, 3	PHOTO, 4 EXECUTOR_IDS, 5 GOOGLE_SHEET_ROW_ID, 6 EXECUTED*/
        PreparedStatement ps = connection.prepareStatement("INSERT INTO TICKET VALUES(default, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, ticket.getCategory().getName());//todo тут должно быть вместе с родителем
        ps.setString(2, ticket.getText());
        ps.setString(3, ticket.getPhoto());
        ps.setString(4, ticket.getExecutorNumber());
        ps.setInt(5, -1);//todo пока не реализовано
        ps.setBoolean(6, false);
        ps.execute();
    }
}
