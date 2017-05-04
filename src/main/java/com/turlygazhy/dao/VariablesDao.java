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
    public static final String LAST_ROW_ID = "last_row_id";
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

    public synchronized int takeLastRowId() throws SQLException {
        int lastRowId = Integer.parseInt(select(LAST_ROW_ID));
        update(LAST_ROW_ID, String.valueOf(lastRowId + 1));
        return lastRowId;
    }

    private void update(String key, String value) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("update VAR set value=? where key=?");
        ps.setString(1, value);
        ps.setString(2, key);
        ps.execute();
    }
}
