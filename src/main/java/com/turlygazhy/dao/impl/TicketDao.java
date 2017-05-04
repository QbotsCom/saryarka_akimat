package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.VariablesDao;
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

    public Ticket insert(Ticket ticket, VariablesDao variablesDao) throws SQLException {
        int lastRowId = variablesDao.takeLastRowId();

        /*ID  	CATEGORY  	TEXT  	PHOTO  	EXECUTOR_IDS  	GOOGLE_SHEET_ROW_ID  	EXECUTED*/
        PreparedStatement ps = connection.prepareStatement("INSERT INTO TICKET VALUES(default, ?, ?, ?, ?, ?, ?)");
        ps.setString(1, ticket.getCategory().getName());
        ps.setString(2, ticket.getText());
        ps.setString(3, ticket.getPhoto());
        ps.setString(4, ticket.getExecutorNumber());
        ps.setInt(5, ticket.getGoogleSheetRowId());
        ps.setBoolean(6, false);

        ticket.setGoogleSheetRowId(lastRowId);
        return ticket;
    }
}
