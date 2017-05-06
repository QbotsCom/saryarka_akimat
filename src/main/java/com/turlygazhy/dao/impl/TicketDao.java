package com.turlygazhy.dao.impl;

import com.turlygazhy.dao.VariablesDao;
import com.turlygazhy.entity.Ticket;
import com.turlygazhy.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by user on 4/30/17.
 */
public class TicketDao {
    public static final String NOT_FINISHED = "not_finished";
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
        List<User> executors = ticket.getExecutors();
        if (executors != null) {
            String executorsIds = "";
            for (User user : executors) {
                executorsIds = executorsIds + "\n" + user.getId();
            }
            ps.setString(4, executorsIds.trim());
        } else {
            ps.setString(4, null);
        }
        ps.setInt(5, ticket.getGoogleSheetRowId());
        ps.setBoolean(6, false);
        ps.execute();

        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        ticket.setId(id);
        ticket.setGoogleSheetRowId(lastRowId);
        ticket.setState(variablesDao.select(NOT_FINISHED));
        return ticket;
    }
}
