package org.example.lager.repository;

import org.example.lager.Database;
import org.example.lager.model.Storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StorageRepository {

    public boolean insert(Storage storage) {
        String sql = "INSERT INTO Storage (Location, Name) VALUES (?, ?);";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, storage.getLocation());
            pstmt.setString(2, storage.getName());

            if (pstmt.executeUpdate() == 0) {
                return false;
            }

            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    storage.setId(keys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting storage: " + e.getMessage());
            return false;
        }
    }

    public Storage findById(int id) {
        String sql = "SELECT ID, Location, Name FROM Storage WHERE ID = ?;";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding storage by ID: " + e.getMessage());
        }
        return null;
    }

    public List<Storage> findAll() {
        List<Storage> storages = new ArrayList<>();
        String sql = "SELECT ID, Location, Name FROM Storage ORDER BY ID;";

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                storages.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading storages: " + e.getMessage());
        }
        return storages;
    }

    public boolean update(Storage storage) {
        String sql = "UPDATE Storage SET Location = ?, Name = ? WHERE ID = ?;";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, storage.getLocation());
            pstmt.setString(2, storage.getName());
            pstmt.setInt(3, storage.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating storage: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Storage WHERE ID = ?;";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting storage: " + e.getMessage());
            return false;
        }
    }

    private Storage mapRow(ResultSet rs) throws SQLException {
        Storage storage = new Storage();
        storage.setId(rs.getInt("ID"));
        storage.setLocation(rs.getString("Location"));
        storage.setName(rs.getString("Name"));
        return storage;
    }
}