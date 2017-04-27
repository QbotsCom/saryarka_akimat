package com.turlygazhy.dao.impl;

import com.turlygazhy.command.Command;
import com.turlygazhy.command.CommandFactory;
import com.turlygazhy.dao.AbstractDao;
import com.turlygazhy.exception.CommandNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by user on 12/14/16.
 */
public class CommandDao extends AbstractDao {
    private Connection connection;

    public CommandDao(Connection connection) {
        this.connection = connection;
    }

    public Command getCommand(long commandId) throws CommandNotFoundException, SQLException {
        try {
            String selectCommandById = "SELECT * FROM PUBLIC.COMMAND WHERE ID=?";
            int idParameterIndex = 1;
            int commandTypeIdColumnIndex = 2;
            int messageToUserColumnIndex = 3;

            PreparedStatement ps = connection.prepareStatement(selectCommandById);
            ps.setLong(idParameterIndex, commandId);
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();

            Command command = CommandFactory.getCommand(rs.getLong(commandTypeIdColumnIndex));
            command.setId(rs.getLong(ID_INDEX));
            command.setMessageId(rs.getLong(messageToUserColumnIndex));
            return command;
        } catch (SQLException e) {
            if (e.getMessage().contains("No data is available")) {
                throw new CommandNotFoundException(e);
            }
            throw new SQLException(e);
        }
    }

    public Command insert(int commandTypeId, int messageId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO COMMAND VALUES(default, ?, ?)");
        ps.setInt(1, commandTypeId);
        ps.setInt(2, messageId);
        ps.execute();
        ResultSet rs = ps.getGeneratedKeys();
        rs.next();
        try {
            return getCommand(rs.getInt(1));
        } catch (CommandNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
