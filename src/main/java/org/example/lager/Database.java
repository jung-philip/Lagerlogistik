package org.example.lager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    // Created in the project root directory if it does not exist yet
    private static final String DB_URL = "jdbc:sqlite:database.db";

    /**
     * Opens a connection to the SQLite database.
     * Automatically creates database.db if it does not exist.
     */
    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    /**
     * Initializes the database and creates the tables if they do not exist.
     */
    public static void initialize() {
        String createStorageTable = """
            CREATE TABLE IF NOT EXISTS Storage (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Location TEXT NOT NULL,
                Name TEXT NOT NULL
            );
            """;

        String createItemTable = """
            CREATE TABLE IF NOT EXISTS Item (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                Name TEXT NOT NULL,
                Quantity INTEGER NOT NULL DEFAULT 0,
                StorageID INTEGER,
                MinInventory INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY (StorageID) REFERENCES Storage(ID) ON DELETE SET NULL
            );
            """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createStorageTable);
            stmt.execute(createItemTable);
            System.out.println("Database and tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}