package org.example.lager.repository;

import org.example.lager.Database;
import org.example.lager.model.Item;
import org.example.lager.model.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ItemRepository {

    private static final String SELECT_WITH_STORAGE = """
        SELECT
            i.ID,
            i.Name,
            i.Quantity,
            i.StorageID,
            i.MinInventory,
            s.Location AS StorageLocation,
            s.Name AS StorageName
        FROM Item i
        LEFT JOIN Storage s ON i.StorageID = s.ID
        """;

    public boolean insert(Item item) {
        String sql = "INSERT INTO Item (Name, Quantity, StorageID, MinInventory) VALUES (?, ?, ?, ?);";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getQuantity());
            bindStorageId(pstmt, 3, item.getStorage());
            pstmt.setInt(4, item.getMinInventory());

            if (pstmt.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting item: " + e.getMessage());
            return false;
        }
    }

    public Item findById(int id) {
        String sql = SELECT_WITH_STORAGE + " WHERE i.ID = ?;";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding item by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        String sql = SELECT_WITH_STORAGE + " ORDER BY i.ID;";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading items: " + e.getMessage());
        }
        return items;
    }

    public boolean update(Item item) {
        String sql = "UPDATE Item SET Name = ?, Quantity = ?, StorageID = ?, MinInventory = ? WHERE ID = ?;";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, item.getName());
            pstmt.setInt(2, item.getQuantity());
            bindStorageId(pstmt, 3, item.getStorage());
            pstmt.setInt(4, item.getMinInventory());
            pstmt.setInt(5, item.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating item: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Item WHERE ID = ?;";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting item: " + e.getMessage());
            return false;
        }
    }

    private void bindStorageId(PreparedStatement pstmt, int parameterIndex, Storage storage) throws SQLException {
        if (storage != null && storage.getId() > 0) {
            pstmt.setInt(parameterIndex, storage.getId());
        } else {
            pstmt.setNull(parameterIndex, Types.INTEGER);
        }
    }

    private Item mapRow(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setId(rs.getInt("ID"));
        item.setName(rs.getString("Name"));
        item.setQuantity(rs.getInt("Quantity"));
        item.setMinInventory(rs.getInt("MinInventory"));

        int storageId = rs.getInt("StorageID");
        if (!rs.wasNull()) {
            Storage storage = new Storage();
            storage.setId(storageId);
            storage.setLocation(rs.getString("StorageLocation"));
            storage.setName(rs.getString("StorageName"));
            item.setStorage(storage);
        } else {
            item.setStorage(null);
        }

        return item;
    }
}