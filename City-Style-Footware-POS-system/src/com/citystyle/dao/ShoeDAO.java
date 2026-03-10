package com.citystyle.dao;

import com.citystyle.database.DatabaseConnection;
import com.citystyle.model.Shoe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShoeDAO {

    public List<Shoe> getAllShoes() {
        return searchByAll(null, null, null, null, null, -1, -1, -1, -1);
    }

    public List<Shoe> searchByAll(String query, String category, String brand, String color, String size,
            int minSize, int maxSize, double minPrice, double maxPrice) {
        List<Shoe> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT s.*, b.name as brand_name, c.name as category_name, sup.name as supplier_name " +
                        "FROM shoes s " +
                        "LEFT JOIN brands b ON s.brand_id = b.brand_id " +
                        "LEFT JOIN categories c ON s.category_id = c.category_id " +
                        "LEFT JOIN suppliers sup ON s.supplier_id = sup.supplier_id " +
                        "WHERE 1=1");

        if (query != null && !query.isEmpty()) {
            boolean isNumeric = query.matches("\\d+");
            if (isNumeric) {
                sql.append(" AND (s.model_name ILIKE ? OR (s.min_size <= ? AND s.max_size >= ?))");
            } else {
                sql.append(" AND s.model_name ILIKE ?");
            }
        }
        if (category != null && !category.equals("All"))
            sql.append(" AND c.name = ?");
        if (brand != null && !brand.equals("All"))
            sql.append(" AND b.name = ?");
        if (color != null && !color.isEmpty() && !color.equalsIgnoreCase("All"))
            sql.append(" AND s.color ILIKE ?");
        if (size != null && !size.isEmpty() && !size.equalsIgnoreCase("All"))
            sql.append(" AND s.size ILIKE ?");
        if (minSize >= 0)
            sql.append(" AND s.max_size >= ?");
        if (maxSize >= 0)
            sql.append(" AND s.min_size <= ?");
        if (minPrice >= 0)
            sql.append(" AND s.base_price >= ?");
        if (maxPrice >= 0)
            sql.append(" AND s.base_price <= ?");

        sql.append(" ORDER BY c.name, s.model_name");

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int idx = 1;
            if (query != null && !query.isEmpty()) {
                ps.setString(idx++, "%" + query + "%");
                if (query.matches("\\d+")) {
                    int sizeQuery = Integer.parseInt(query);
                    ps.setInt(idx++, sizeQuery);
                    ps.setInt(idx++, sizeQuery);
                }
            }
            if (category != null && !category.equals("All"))
                ps.setString(idx++, category);
            if (brand != null && !brand.equals("All"))
                ps.setString(idx++, brand);
            if (color != null && !color.isEmpty() && !color.equalsIgnoreCase("All"))
                ps.setString(idx++, "%" + color + "%");
            if (size != null && !size.isEmpty() && !size.equalsIgnoreCase("All"))
                ps.setString(idx++, "%" + size + "%");
            if (minSize >= 0)
                ps.setInt(idx++, minSize);
            if (maxSize >= 0)
                ps.setInt(idx++, maxSize);
            if (minPrice >= 0)
                ps.setDouble(idx++, minPrice);
            if (maxPrice >= 0)
                ps.setDouble(idx++, maxPrice);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Shoe(
                        rs.getInt("shoe_id"),
                        rs.getString("model_name"),
                        rs.getString("brand_name"),
                        rs.getString("category_name"),
                        rs.getString("supplier_name"),
                        rs.getString("size"),
                        rs.getInt("min_size"),
                        rs.getInt("max_size"),
                        rs.getString("color"),
                        rs.getDouble("base_price"),
                        rs.getDouble("promotional_price"),
                        rs.getInt("stock_quantity")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ADD NEW SHOE (MANAGER)
    public void addShoe(String modelName, int brandId, int categoryId,
            String size, int minS, int maxS, String color, int stock, double price, int supplierId) {

        String sql = "INSERT INTO shoes (model_name, brand_id, category_id, size, min_size, max_size, color, stock_quantity, base_price, promotional_price, supplier_id) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, modelName);
            ps.setInt(2, brandId);
            ps.setInt(3, categoryId);
            ps.setString(4, size);
            ps.setInt(5, minS);
            ps.setInt(6, maxS);
            ps.setString(7, color);
            ps.setInt(8, stock);
            ps.setDouble(9, price);
            ps.setDouble(10, price); // Default promo price to same as base
            ps.setInt(11, supplierId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding shoe: " + e.getMessage());
        }
    }

    // UPDATE SHOE (MANAGER)
    // UPDATE SHOE (MANAGER)
    public void updateShoe(int shoeId, String modelName, int brandId, int categoryId, String size, int minS, int maxS,
            String color, int stock,
            double price,
            double promoPrice, int supplierId) {
        String sql = "UPDATE shoes SET model_name=?, brand_id=?, category_id=?, size=?, min_size=?, max_size=?, color=?, stock_quantity=?, base_price=?, promotional_price=?, supplier_id=? WHERE shoe_id=?";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, modelName);
            ps.setInt(2, brandId);
            ps.setInt(3, categoryId);
            ps.setString(4, size);
            ps.setInt(5, minS);
            ps.setInt(6, maxS);
            ps.setString(7, color);
            ps.setInt(8, stock);
            ps.setDouble(9, price);
            ps.setDouble(10, promoPrice);
            ps.setInt(11, supplierId);
            ps.setInt(12, shoeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating shoe: " + e.getMessage());
        }
    }

    // UPDATE STOCK ONLY
    public void updateStock(int shoeId, int newStock) {
        String sql = "UPDATE shoes SET stock_quantity=? WHERE shoe_id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, newStock);
            ps.setInt(2, shoeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteShoe(int shoeId) {
        String sql = "DELETE FROM shoes WHERE shoe_id=?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, shoeId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void bulkUpdatePrice(String category, double percentage) throws SQLException {
        String sql = "UPDATE shoes SET promotional_price = base_price * (1 + ? / 100.0) " +
                "WHERE category_id = (SELECT category_id FROM categories WHERE name = ?)";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, percentage);
            ps.setString(2, category);
            ps.executeUpdate();
        }
    }
}
