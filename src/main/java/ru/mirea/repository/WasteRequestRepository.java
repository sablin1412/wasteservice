package ru.mirea.repository;

import ru.mirea.model.RequestStatus;
import ru.mirea.model.WasteRequest;
import ru.mirea.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WasteRequestRepository {

    public void create(WasteRequest request) {
        String sql = "INSERT INTO waste_requests (client_id, address, waste_type, volume_m3, status, scheduled_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, request.getClientId());
            stmt.setString(2, request.getAddress());
            stmt.setString(3, request.getWasteType());
            stmt.setDouble(4, request.getVolumeM3());
            stmt.setString(5, request.getStatus().name());
            stmt.setDate(6, Date.valueOf(request.getScheduledDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<WasteRequest> findAll() {
        List<WasteRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM waste_requests";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                WasteRequest req = new WasteRequest();
                req.setId(rs.getInt("id"));
                req.setClientId(rs.getInt("client_id"));
                req.setAddress(rs.getString("address"));
                req.setWasteType(rs.getString("waste_type"));
                req.setVolumeM3(rs.getDouble("volume_m3"));
                req.setStatus(RequestStatus.valueOf(rs.getString("status")));
                req.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                req.setScheduledDate(rs.getDate("scheduled_date").toLocalDate());
                requests.add(req);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return requests;
    }

    public void update(WasteRequest request) {
        String sql = "UPDATE waste_requests SET client_id=?, address=?, waste_type=?, volume_m3=?, status=?, scheduled_date=? WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, request.getClientId());
            stmt.setString(2, request.getAddress());
            stmt.setString(3, request.getWasteType());
            stmt.setDouble(4, request.getVolumeM3());
            stmt.setString(5, request.getStatus().name());
            stmt.setDate(6, Date.valueOf(request.getScheduledDate()));
            stmt.setInt(7, request.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM waste_requests WHERE id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}