package com.citystyle.dao;

import com.citystyle.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetaDAO {

    public List<String> getCategories() {
        return getList("SELECT name FROM categories ORDER BY name");
    }

    public List<String> getBrands() {
        return getList("SELECT name FROM brands ORDER BY name");
    }

    public List<String> getSuppliers() {
        return getList("SELECT name FROM suppliers ORDER BY name");
    }

    public List<String> getColors() {
        return getList("SELECT DISTINCT color FROM shoes WHERE color IS NOT NULL AND color != '' ORDER BY color");
    }

    public int getCategoryIdByName(String name) {
        return getId("SELECT category_id FROM categories WHERE name = ?", name);
    }

    public int getBrandIdByName(String name) {
        return getId("SELECT brand_id FROM brands WHERE name = ?", name);
    }

    public int getSupplierIdByName(String name) {
        return getId("SELECT supplier_id FROM suppliers WHERE name = ?", name);
    }

    private int getId(String sql, String name) {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private List<String> getList(String sql) {
        List<String> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addCategory(String name) {
        execute("INSERT INTO categories (name) VALUES (?)", name);
    }

    public void addBrand(String name) {
        execute("INSERT INTO brands (name) VALUES (?)", name);
    }

    public void updateCategory(String oldName, String newName) {
        execute2("UPDATE categories SET name = ? WHERE name = ?", newName, oldName);
    }

    public void deleteCategory(String name) {
        execute("DELETE FROM categories WHERE name = ?", name);
    }

    public void updateBrand(String oldName, String newName) {
        execute2("UPDATE brands SET name = ? WHERE name = ?", newName, oldName);
    }

    public void deleteBrand(String name) {
        execute("DELETE FROM brands WHERE name = ?", name);
    }

    public void addSupplier(String name) {
        execute("INSERT INTO suppliers (name) VALUES (?)", name);
    }

    public void updateSupplier(String oldName, String newName) {
        execute2("UPDATE suppliers SET name = ? WHERE name = ?", newName, oldName);
    }

    public void deleteSupplier(String name) {
        execute("DELETE FROM suppliers WHERE name = ?", name);
    }

    private void execute(String sql, String val) {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, val);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void execute2(String sql, String val1, String val2) {
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, val1);
            ps.setString(2, val2);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
