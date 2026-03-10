package com.citystyle.dao;

import com.citystyle.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountRequestDAO {

    public DiscountRequestDAO() {
        ensureTableExists();
    }

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS discount_requests (" +
                "request_id SERIAL PRIMARY KEY, " +
                "cashier_id INTEGER REFERENCES users(user_id), " +
                "discount_percentage DECIMAL(5,2) NOT NULL, " +
                "items_summary TEXT, " +
                "status VARCHAR(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Approved', 'Rejected')), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement()) {
            st.execute(sql);
            // Ensure column exists for older databases
            try {
                st.execute("ALTER TABLE discount_requests ADD COLUMN items_summary TEXT");
            } catch (Exception e) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int requestDiscount(int cashierId, double percentage, String itemsSummary) {
        String sql = "INSERT INTO discount_requests (cashier_id, discount_percentage, items_summary, status) VALUES (?, ?, ?, 'Pending') RETURNING request_id";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, cashierId);
            pst.setDouble(2, percentage);
            pst.setString(3, itemsSummary);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String checkStatus(int requestId) {
        String sql = "SELECT status FROM discount_requests WHERE request_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, requestId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next())
                    return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    // For Admin to see pending requests
    public List<RequestData> getPendingRequests() {
        List<RequestData> list = new ArrayList<>();
        String sql = "SELECT r.request_id, u.username, r.discount_percentage, r.items_summary, r.created_at " +
                "FROM discount_requests r " +
                "JOIN users u ON r.cashier_id = u.user_id " +
                "WHERE r.status = 'Pending' " +
                "ORDER BY r.created_at ASC";
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new RequestData(
                        rs.getInt("request_id"),
                        rs.getString("username"),
                        rs.getDouble("discount_percentage"),
                        rs.getString("items_summary"),
                        rs.getTimestamp("created_at")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateStatus(int requestId, String status) {
        String sql = "UPDATE discount_requests SET status = ? WHERE request_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, status);
            pst.setInt(2, requestId);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class RequestData {
        public int id;
        public String cashierName;
        public double percentage;
        public String itemsSummary;
        public Timestamp time;

        public RequestData(int id, String cashierName, double percentage, String itemsSummary, Timestamp time) {
            this.id = id;
            this.cashierName = cashierName;
            this.percentage = percentage;
            this.itemsSummary = itemsSummary;
            this.time = time;
        }
    }
}
