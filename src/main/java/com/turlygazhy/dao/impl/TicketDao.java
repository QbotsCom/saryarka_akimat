package com.turlygazhy.dao.impl;

import java.sql.Connection;

/**
 * Created by user on 4/30/17.
 */
public class TicketDao {
    private final Connection connection;

    public TicketDao(Connection connection) {
        this.connection = connection;
    }
}
