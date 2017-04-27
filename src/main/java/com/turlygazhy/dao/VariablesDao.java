package com.turlygazhy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by user on 4/25/17.
 */
public class VariablesDao {
    public static final int VALUE_COLUMN_INDEX = 3;
    private final Connection connection;

    public VariablesDao(Connection connection) {
        this.connection = connection;
    }

    public String select(String key) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM VAR where key=?");
        ps.setString(1, key);
        ps.execute();
        ResultSet resultSet = ps.getResultSet();
        resultSet.next();
        return resultSet.getString(VALUE_COLUMN_INDEX);
    }
}
