package org.example.lager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:database.db";

    private static final String CREATE_STORAGE_TABLE = """
        CREATE TABLE IF NOT EXISTS Storage (
            ID INTEGER PRIMARY KEY AUTOINCREMENT,
            Location TEXT NOT NULL,
            Name TEXT NOT NULL
        );
        """;

    private static final String CREATE_ITEM_TABLE = """
        CREATE TABLE IF NOT EXISTS Item (
            ID INTEGER PRIMARY KEY AUTOINCREMENT,
            Name TEXT NOT NULL,
            Quantity INTEGER NOT NULL DEFAULT 0,
            StorageID INTEGER,
            MinInventory INTEGER NOT NULL DEFAULT 0,
            FOREIGN KEY (StorageID) REFERENCES Storage(ID) ON DELETE SET NULL
        );
        """;

    public static Connection connect() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initialize() {
        try (Connection conn = connect()) {
            createTablesIfNotExists(conn);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void createTablesIfNotExists(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_STORAGE_TABLE);
            stmt.execute(CREATE_ITEM_TABLE);
        }
    }
}