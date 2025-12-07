package application;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseTest {
    
    public static void main(String[] args) {
        System.out.println("=== Database Connection Test ===\n");
        
        try {
            // Test connection
            System.out.println("1. Testing database connection...");
            Connection conn = DatabaseConnection.getConnection();
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Connection successful!");
                
                // Get database metadata
                System.out.println("\n2. Database Information:");
                DatabaseMetaData metaData = conn.getMetaData();
                System.out.println("   Database: " + metaData.getDatabaseProductName());
                System.out.println("   Version: " + metaData.getDatabaseProductVersion());
                System.out.println("   Driver: " + metaData.getDriverName());
                System.out.println("   URL: " + metaData.getURL());
                
                // Check if tables exist
                System.out.println("\n3. Checking tables...");
                String[] tableTypes = {"TABLE"};
                ResultSet tables = metaData.getTables(null, null, "%", tableTypes);
                
                int tableCount = 0;
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    System.out.println("   ✓ Table found: " + tableName);
                    tableCount++;
                }
                
                if (tableCount == 0) {
                    System.out.println("   ⚠ No tables found. Please run schema.sql to create tables.");
                } else {
                    System.out.println("   Total tables: " + tableCount);
                }
                
                // Test query
                System.out.println("\n4. Testing queries...");
                try (Statement stmt = conn.createStatement()) {
                    // Test users table
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM users");
                    if (rs.next()) {
                        System.out.println("   ✓ Users table: " + rs.getInt("count") + " records");
                    }
                    
                    // Test cars table
                    rs = stmt.executeQuery("SELECT COUNT(*) as count FROM cars");
                    if (rs.next()) {
                        System.out.println("   ✓ Cars table: " + rs.getInt("count") + " records");
                    }
                    
                    // Test customers table
                    rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers");
                    if (rs.next()) {
                        System.out.println("   ✓ Customers table: " + rs.getInt("count") + " records");
                    }
                    
                    // Test employees table
                    rs = stmt.executeQuery("SELECT COUNT(*) as count FROM employees");
                    if (rs.next()) {
                        System.out.println("   ✓ Employees table: " + rs.getInt("count") + " records");
                    }
                }
                
                System.out.println("\n=== All tests passed! ===");
                
            } else {
                System.out.println("✗ Connection failed!");
            }
            
        } catch (Exception e) {
            System.err.println("\n✗ Error: " + e.getMessage());
            e.printStackTrace();
            System.out.println("\nTroubleshooting:");
            System.out.println("1. Make sure MySQL server is running");
            System.out.println("2. Check database name in DatabaseConnection.java (should be 'car_rental_db')");
            System.out.println("3. Verify username and password in DatabaseConnection.java");
            System.out.println("4. Run schema.sql to create database and tables");
            System.out.println("5. Make sure MySQL JDBC driver is in classpath");
        } finally {
            DatabaseConnection.closeConnection();
        }
    }
}



