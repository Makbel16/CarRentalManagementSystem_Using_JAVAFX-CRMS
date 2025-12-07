package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/car_rental_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "Ybe@16";
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null || isConnectionClosed()) {
            try {
                // Try to load MySQL driver
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("ERROR: MySQL JDBC Driver not found!");
                    System.err.println("Please download MySQL Connector/J from:");
                    System.err.println("https://dev.mysql.com/downloads/connector/j/");
                    System.err.println("And place the JAR file in the lib/ directory");
                    throw new RuntimeException("MySQL JDBC Driver not found in classpath", e);
                }
                
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                if (connection != null && !connection.isClosed()) {
                    System.out.println("✓ Database connection established successfully!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("\n✗ Database connection failed!");
                System.err.println("Error: " + e.getMessage());
                System.err.println("\nTroubleshooting:");
                System.err.println("1. Check if MySQL server is running");
                System.err.println("2. Verify database name: car_rental_db");
                System.err.println("3. Check username and password in DatabaseConnection.java");
                System.err.println("4. Run schema.sql to create database and tables");
                throw new RuntimeException("Failed to connect to database", e);
            }
        }
        return connection;
    }
    
    private static boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}