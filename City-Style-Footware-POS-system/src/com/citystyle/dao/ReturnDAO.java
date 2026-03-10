package com.citystyle.dao;

import com.citystyle.database.DatabaseConnection;
import com.citystyle.model.ReturnRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnDAO {

    public void createReturn(int saleItemId, String reason) throws SQLException {
        // First check if the sale item exists to provide a better error message
        String checkSql = "SELECT 1 FROM sales_items WHERE sale_item_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement psCheck = con.prepareStatement(checkSql)) {
            psCheck.setInt(1, saleItemId);
            ResultSet rs = psCheck.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Sale Item ID " + saleItemId + " does not exist.");
            }
        }

        String sql = "INSERT INTO returns (sale_item_id, reason) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleItemId);
            ps.setString(2, reason);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<ReturnRequest> getAllReturns() {
        List<ReturnRequest> list = new ArrayList<>();
        String sql = "SELECT r.*, u.username as manager_name, s.model_name, si.sale_id " +
                "FROM returns r " +
                "LEFT JOIN users u ON r.approved_by = u.user_id " +
                "JOIN sales_items si ON r.sale_item_id = si.sale_item_id " +
                "JOIN shoes s ON si.shoe_id = s.shoe_id " +
                "ORDER BY r.return_date DESC";

        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ReturnRequest req = new ReturnRequest(
                        rs.getInt("return_id"),
                        rs.getInt("sale_item_id"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getString("manager_name"),
                        rs.getTimestamp("return_date"));
                // We'll use a hacky way to store extra info or just update the model
                req.setModelName(rs.getString("model_name"));
                req.setSaleId(rs.getInt("sale_id"));
                list.add(req);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String[]> getRecentSalesItems() {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT si.sale_item_id, si.sale_id, s.model_name, b.name as brand_name, " +
                "s.color, si.selected_size, " +
                "c.name as customer_name, si.quantity, si.unit_price " +
                "FROM sales_items si " +
                "JOIN shoes s ON si.shoe_id = s.shoe_id " +
                "JOIN brands b ON s.brand_id = b.brand_id " +
                "JOIN sales sa ON si.sale_id = sa.sale_id " +
                "JOIN customers c ON sa.customer_id = c.customer_id " +
                "ORDER BY si.sale_item_id DESC LIMIT 50";
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new String[] {
                        String.valueOf(rs.getInt("sale_item_id")),
                        String.valueOf(rs.getInt("sale_id")),
                        rs.getString("model_name"),
                        rs.getString("brand_name"),
                        rs.getString("color"), // New
                        rs.getString("selected_size"), // New
                        rs.getString("customer_name"),
                        String.valueOf(rs.getInt("quantity")),
                        String.valueOf(rs.getDouble("unit_price"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void approveReturn(int returnId, int managerId) {
        String updateReturnSql = "UPDATE returns SET status='Approved', approved_by=? WHERE return_id=? AND status='Pending'";
        String findSaleItemSql = "SELECT si.shoe_id, si.quantity FROM returns r JOIN sales_items si ON r.sale_item_id = si.sale_item_id WHERE r.return_id = ?";
        String updateStockSql = "UPDATE shoes SET stock_quantity = stock_quantity + ? WHERE shoe_id = ?";

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement psReturn = con.prepareStatement(updateReturnSql);
                    PreparedStatement psFind = con.prepareStatement(findSaleItemSql);
                    PreparedStatement psStock = con.prepareStatement(updateStockSql)) {

                // 1. Update return status
                psReturn.setInt(1, managerId);
                psReturn.setInt(2, returnId);
                int updated = psReturn.executeUpdate();

                if (updated > 0) {
                    // 2. Find shoe and quantity
                    psFind.setInt(1, returnId);
                    try (ResultSet rs = psFind.executeQuery()) {
                        if (rs.next()) {
                            int shoeId = rs.getInt("shoe_id");
                            int quantity = rs.getInt("quantity");

                            // 3. Update stock
                            psStock.setInt(1, quantity);
                            psStock.setInt(2, shoeId);
                            psStock.executeUpdate();
                        }
                    }
                    con.commit();
                } else {
                    con.rollback();
                }
            } catch (Exception e) {
                con.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteReturn(int returnId) {
        String sql = "DELETE FROM returns WHERE return_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, returnId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
