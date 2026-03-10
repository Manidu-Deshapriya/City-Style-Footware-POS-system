package com.citystyle;

import com.citystyle.database.DatabaseConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

public class DBSetup {
    public static void main(String[] args) {
        System.out.println("Starting Database Setup...");
        String setupFile = "setup.sql";

        try (Connection con = DatabaseConnection.getConnection();
                Statement stmt = con.createStatement();
                BufferedReader br = new BufferedReader(new FileReader(setupFile))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                // Ignore comments and empty lines
                if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                    continue;
                }
                sb.append(line).append("\n");

                // Execute when a semicolon is found (simple SQL parser)
                if (line.trim().endsWith(";")) {
                    try {
                        // System.out.println("Executing: " + sb.toString());
                        stmt.execute(sb.toString());
                        sb.setLength(0); // clear buffer
                    } catch (Exception e) {
                        System.err.println("Error executing statement: " + e.getMessage());
                        // e.printStackTrace();
                        // Continue even if error (e.g. drop table if exists)
                        sb.setLength(0);
                    }
                }
            }

            // Execute any remaining part
            if (sb.length() > 0 && !sb.toString().trim().isEmpty()) {
                stmt.execute(sb.toString());
            }

            System.out.println("Database Setup Completed Successfully (with New Data)!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
