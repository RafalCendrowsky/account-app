package com.rafalcendrowski.AccountApplication.logging;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {
    private static interface Singleton {
        final ConnectionFactory INSTANCE = new ConnectionFactory();
    }

    private final MysqlDataSource dataSource;

    private ConnectionFactory() {
        MysqlDataSource tempSource = new MysqlDataSource();
        tempSource.setUser("rcen");
        tempSource.setPassword("password");
        tempSource.setUrl("jdbc:mysql://localhost:3306/accounts");
        this.dataSource = tempSource;
    }

    public static Connection getConnection() throws SQLException {
        return Singleton.INSTANCE.dataSource.getConnection("rcen", "password");
    }
}
