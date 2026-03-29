package com.lyra.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private final String url;
    private final String user;
    private final String password;

    private DatabaseConnection() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (is == null) {
                throw new RuntimeException("No se encontró database.properties en el classpath");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar database.properties", e);
        }
        this.url = props.getProperty("db.url");
        this.user = props.getProperty("db.user");
        this.password = props.getProperty("db.password");
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }
}
