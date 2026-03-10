package com.citystyle.dao;

import com.citystyle.database.DatabaseConnection;
import java.sql.*;

public class SaleDAO {
    public int processSale(int userId, String customerName, java.util.List<com.citystyle.model.CartItem> cartItems,
            double totalAmount) {
        String saleSql = "INSERT INTO sales (user_id, customer_id, total_amount) VALUES (?, ?, ?) RETURNING sale_id";
        String itemSql = "INSERT INTO sales_items (sale_id, shoe_id, quantity, unit_price, selected_size) VALUES (?, ?, ?, ?, ?)";
        String stockSql = "UPDATE shoes SET stock_quantity = stock_quantity - ? WHERE shoe_id = ?";

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);

            int customerId = getOrCreateCustomer(con, customerName);

            int saleId = -1;
            try (PreparedStatement ps = con.prepareStatement(saleSql)) {
                ps.setInt(1, userId);
                ps.setInt(2, customerId);
                ps.setDouble(3, totalAmount);
                ResultSet rs = ps.executeQuery();
                if (rs.next())
                    saleId = rs.getInt(1);
            }

            try (PreparedStatement itemPs = con.prepareStatement(itemSql);
                    PreparedStatement stockPs = con.prepareStatement(stockSql)) {

                for (com.citystyle.model.CartItem item : cartItems) {
                    itemPs.setInt(1, saleId);
                    itemPs.setInt(2, item.getShoeId());
                    itemPs.setInt(3, item.getQuantity());
                    itemPs.setDouble(4, item.getPrice());
                    itemPs.setString(5, item.getSize());
                    itemPs.addBatch();

                    stockPs.setInt(1, item.getQuantity());
                    stockPs.setInt(2, item.getShoeId());
                    stockPs.addBatch();
                }
                itemPs.executeBatch();
                stockPs.executeBatch();
            }

            con.commit();
            return saleId;
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            throw new RuntimeException("Checkout failed: " + e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    private int getOrCreateCustomer(Connection con, String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            name = "Walk-in Customer";
        }
        String findSql = "SELECT customer_id FROM customers WHERE name = ?";
        try (PreparedStatement ps = con.prepareStatement(findSql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        }

        String insertSql = "INSERT INTO customers (name) VALUES (?) RETURNING customer_id";
        try (PreparedStatement ps = con.prepareStatement(insertSql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        }
        return 1; // Default
    }

    public void deleteSaleItem(int saleItemId) {
        String sql = "DELETE FROM sales_items WHERE sale_item_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
