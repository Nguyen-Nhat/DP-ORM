package org.group05.com.entityManager;

import org.group05.com.ConfigurationManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EntityManagerFactory {
    private Connection connection = null;

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            ConfigurationManager configuration = ConfigurationManager.getInstance();
            String url = configuration.getProperty("database.url");
            String username = configuration.getProperty("database.username");
            String password = configuration.getProperty("database.password");

            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to database successfully!");
        }
        return connection;
    }

    public EntityManager createEntityManager() {
        try {
            String url = ConfigurationManager.getInstance().getProperty("database.url");
            String typeDatabase = url.split(":")[1];

            return switch (typeDatabase) {
                case "mysql" -> new MySQLEntityManager(getConnection());
                case "postgresql" -> new PostgresSQLEntityManager(getConnection());
                default -> throw new IllegalArgumentException("Database not supported");
            };

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
