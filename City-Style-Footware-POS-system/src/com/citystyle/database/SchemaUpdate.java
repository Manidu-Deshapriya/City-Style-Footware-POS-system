package com.citystyle.database;

import java.sql.Connection;
import java.sql.Statement;

public class SchemaUpdate {
    public static void main(String[] args) {
        try (Connection con = DatabaseConnection.getConnection();
                Statement st = con.createStatement()) {
            st.execute("ALTER TABLE sales_items ADD COLUMN IF NOT EXISTS selected_size VARCHAR(20)");

            // Step 2: Add Discount Requests Table
            st.execute("CREATE TABLE IF NOT EXISTS discount_requests (" +
                    "request_id SERIAL PRIMARY KEY, " +
                    "cashier_id INTEGER REFERENCES users(user_id), " +
                    "discount_percentage DECIMAL(5,2) NOT NULL, " +
                    "status VARCHAR(20) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Approved', 'Rejected')), " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            System.out.println("Schema updated successfully!");
        } catch (Exception e) {
            System.err.println("Error updating schema: " + e.getMessage());
        }
    }
}
