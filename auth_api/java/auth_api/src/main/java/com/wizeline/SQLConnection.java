package com.wizeline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection implements DBConnection {

    private static SQLConnection INSTANCE;
    private static Connection connection;

    SQLConnection() throws SQLException {
        connection = buildConnection();
    }

    private static Connection buildConnection() throws SQLException {
        String dbName = System.getenv("DB_NAME");
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");
        String hostName = System.getenv("DB_HOSTNAME");
        String port = System.getenv("DB_PORT");
        String url = String.format(
                "jdbc:mysql://%s:%s/%s?user=%s&password=%s?characterEncoding=utf8",
                hostName, port, dbName, dbUsername, dbPassword);
        return DriverManager.getConnection(url);
    }

    public static SQLConnection getInstance() throws SQLException {
        if(INSTANCE == null) {
            INSTANCE = new SQLConnection();
        }

        return INSTANCE;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
