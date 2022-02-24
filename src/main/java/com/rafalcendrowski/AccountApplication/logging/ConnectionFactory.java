package com.rafalcendrowski.AccountApplication.logging;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    private final MysqlDataSource dataSource;

    private final static ConnectionFactory INSTANCE = new ConnectionFactory();

    private ConnectionFactory() {
        MysqlDataSource tempSource = new MysqlDataSource();
        tempSource.setUser("rcen");
        tempSource.setPassword("password");
        tempSource.setUrl("jdbc:mysql://localhost:3306/accounts");
        this.dataSource = tempSource;
    }

    public static Connection getConnection() throws SQLException {
        return INSTANCE.dataSource.getConnection("rcen", "password");
    }
}
