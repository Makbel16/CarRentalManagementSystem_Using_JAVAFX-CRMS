package dao;

import application.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {
    
    // Get total cars count
    public int getTotalCarsCount() {
        String sql = "SELECT COUNT(*) FROM cars WHERE status = 'Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total cars count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get available cars count
    public int getAvailableCarsCount() {
        String sql = "SELECT COUNT(*) FROM cars WHERE availability = 'Available' AND status = 'Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting available cars count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get total customers count
    public int getTotalCustomersCount() {
        String sql = "SELECT COUNT(*) FROM customers";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total customers count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get active rentals count
    public int getActiveRentalsCount() {
        String sql = "SELECT COUNT(*) FROM rental_records WHERE status = 'Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active rentals count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get total employees count
    public int getTotalEmployeesCount() {
        String sql = "SELECT COUNT(*) FROM employees WHERE status = 'Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting total employees count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get monthly revenue (returns double)
    public double getMonthlyRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM rental_records " +
                    "WHERE MONTH(rental_date) = MONTH(CURRENT_DATE()) " +
                    "AND YEAR(rental_date) = YEAR(CURRENT_DATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting monthly revenue: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Get active rentals for dashboard
    public List<Map<String, Object>> getActiveRentals() {
        List<Map<String, Object>> rentals = new ArrayList<>();
        String sql = "SELECT r.rental_id, r.car_id, c.brand, c.model, " +
                    "r.customer_id, cust.first_name, cust.last_name, " +
                    "r.rental_date, r.return_date, r.total_amount " +
                    "FROM rental_records r " +
                    "JOIN cars c ON r.car_id = c.car_id " +
                    "JOIN customers cust ON r.customer_id = cust.customer_id " +
                    "WHERE r.status = 'Active' " +
                    "ORDER BY r.rental_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> rental = new HashMap<>();
                rental.put("rental_id", rs.getInt("rental_id"));
                rental.put("car_id", rs.getInt("car_id"));
                rental.put("brand", rs.getString("brand"));
                rental.put("model", rs.getString("model"));
                rental.put("customer_id", rs.getInt("customer_id"));
                rental.put("customer_name", rs.getString("first_name") + " " + rs.getString("last_name"));
                rental.put("rental_date", rs.getDate("rental_date"));
                rental.put("return_date", rs.getDate("return_date"));
                rental.put("total_amount", rs.getDouble("total_amount"));
                rentals.add(rental);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active rentals: " + e.getMessage());
            e.printStackTrace();
        }
        return rentals;
    }
}