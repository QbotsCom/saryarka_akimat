package com.turlygazhy.dao.impl;

import com.turlygazhy.command.impl.work_around.entity.Category;
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
    public static final int CATEGORY_NAME_COLUMN_INDEX = 2;
    public static final int TEXT_COLUMN_INDEX = 3;
    public static final int PHOTO_COLUMN_INDEX = 4;
    public static final int CREATOR_CHAT_ID_COLUMN_INDEX = 8;
    private final Connection connection;

    public TicketDao(Connection connection) {
        this.connection = connection;
    }

    public Ticket insert(Ticket ticket, VariablesDao variablesDao, long chatId) throws SQLException {// TODO: 11-May-17 implement chatId
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
                executorsIds = executorsIds + "," + user.getId();// TODO: 11-May-17 test it
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

    public Ticket select(int ticketId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select * from ticket where id=?");
        ps.setInt(1, ticketId);
        ps.execute();
        ResultSet rs = ps.getResultSet();
        rs.next();
        Ticket ticket = new Ticket();
        ticket.setId(ticketId);
        Category category = new Category();
        category.setName(rs.getString(CATEGORY_NAME_COLUMN_INDEX));
        ticket.setCategory(category);
        ticket.setText(rs.getString(TEXT_COLUMN_INDEX));
        ticket.setPhoto(rs.getString(PHOTO_COLUMN_INDEX));
        ticket.setCreatorChatId(rs.getLong(CREATOR_CHAT_ID_COLUMN_INDEX));
        return ticket;
    }
}
