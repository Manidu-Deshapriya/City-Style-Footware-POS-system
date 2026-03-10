package com.citystyle.database;

import java.sql.Connection;
import java.sql.Statement;

public class DBUpdater {
    public static void main(String[] args) {
        try (Connection con = DatabaseConnection.getConnection();
                Statement stmt = con.createStatement()) {

            System.out.println("Updating database...");

            // Add columns if they don't exist
            stmt.execute("ALTER TABLE shoes ADD COLUMN IF NOT EXISTS min_size INTEGER DEFAULT 0");
            stmt.execute("ALTER TABLE shoes ADD COLUMN IF NOT EXISTS max_size INTEGER DEFAULT 0");
            stmt.execute("ALTER TABLE shoes ALTER COLUMN size TYPE VARCHAR(30)");

            // Update existing data: split 'size' or use it as both min/max if numeric
            stmt.execute(
                    "UPDATE shoes SET min_size = CAST(size AS INTEGER), max_size = CAST(size AS INTEGER) WHERE size ~ '^[0-9]+$'");

            // For range-like strings already there (if any), handle them if possible
            // But right now we just want them to look like range in UI

            System.out.println("Database updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
