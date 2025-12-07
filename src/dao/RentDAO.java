package dao;

import models.RentalRecord;
import application.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentDAO {
    
    public boolean addRental(RentalRecord rental) {
        String sql = "INSERT INTO rental_records (car_id, customer_id, employee_id, rental_date, " +
                     "return_date, total_amount, status, notes, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rental.getCarId());
            pstmt.setInt(2, rental.getCustomerId());
            pstmt.setInt(3, rental.getEmployeeId());
            pstmt.setDate(4, Date.valueOf(rental.getRentalDate()));
            pstmt.setDate(5, Date.valueOf(rental.getReturnDate()));
            pstmt.setDouble(6, rental.getTotalAmount());
            pstmt.setString(7, rental.getStatus());
            pstmt.setString(8, rental.getNotes());
            pstmt.setTimestamp(9, Timestamp.valueOf(rental.getCreatedAt() != null ? 
                           rental.getCreatedAt() : LocalDateTime.now()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRental(RentalRecord rental) {
        String sql = "UPDATE rental_records SET car_id=?, customer_id=?, employee_id=?, " +
                     "rental_date=?, return_date=?, total_amount=?, status=?, notes=? " +
                     "WHERE rental_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rental.getCarId());
            pstmt.setInt(2, rental.getCustomerId());
            pstmt.setInt(3, rental.getEmployeeId());
            pstmt.setDate(4, Date.valueOf(rental.getRentalDate()));
            pstmt.setDate(5, Date.valueOf(rental.getReturnDate()));
            pstmt.setDouble(6, rental.getTotalAmount());
            pstmt.setString(7, rental.getStatus());
            pstmt.setString(8, rental.getNotes());
            pstmt.setInt(9, rental.getRentalId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public RentalRecord getRentalById(int rentalId) {
        String sql = "SELECT * FROM rental_records WHERE rental_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentalId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRental(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<RentalRecord> getAllRentals() {
        List<RentalRecord> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental_records ORDER BY rental_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentals;
    }
    
    public List<RentalRecord> getActiveRentals() {
        List<RentalRecord> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental_records WHERE status='Active' ORDER BY rental_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentals;
    }
    
    public List<RentalRecord> getRentalsByCustomerId(int customerId) {
        List<RentalRecord> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental_records WHERE customer_id=? ORDER BY rental_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentals;
    }
    
    public List<RentalRecord> getRentalsByCarId(int carId) {
        List<RentalRecord> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rental_records WHERE car_id=? ORDER BY rental_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentals;
    }
    
    public double getMonthlyRevenue(int year, int month) {
        String sql = "SELECT SUM(total_amount) as total FROM rental_records " +
                     "WHERE YEAR(rental_date)=? AND MONTH(rental_date)=? AND status='Completed'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, year);
            pstmt.setInt(2, month);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    private RentalRecord mapResultSetToRental(ResultSet rs) throws SQLException {
        RentalRecord rental = new RentalRecord();
        rental.setRentalId(rs.getInt("rental_id"));
        rental.setCarId(rs.getInt("car_id"));
        rental.setCustomerId(rs.getInt("customer_id"));
        rental.setEmployeeId(rs.getInt("employee_id"));
        
        Date rentalDate = rs.getDate("rental_date");
        if (rentalDate != null) {
            rental.setRentalDate(rentalDate.toLocalDate());
        }
        
        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            rental.setReturnDate(returnDate.toLocalDate());
        }
        
        rental.setTotalAmount(rs.getDouble("total_amount"));
        rental.setStatus(rs.getString("status"));
        rental.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            rental.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return rental;
    }
 // ============================================
 // ADD THESE 3 NEW METHODS TO YOUR EXISTING RentDAO.java
 // ============================================

 public List<Map<String, Object>> getActiveRentalsWithDetails() {
     List<Map<String, Object>> rentals = new ArrayList<>();
     
     String sql = "SELECT r.rental_id, r.car_id, r.customer_id, r.rental_date, " +
                  "r.return_date, r.total_amount, c.brand, c.model, " +
                  "CONCAT(cust.first_name, ' ', cust.last_name) as customer_name " +
                  "FROM rental_records r " +
                  "LEFT JOIN cars c ON r.car_id = c.car_id " +
                  "LEFT JOIN customers cust ON r.customer_id = cust.customer_id " +
                  "WHERE r.status = 'Active' " +
                  "ORDER BY r.rental_date DESC";
     
     try (Connection conn = DatabaseConnection.getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(sql)) {
         
         while (rs.next()) {
             Map<String, Object> rental = new HashMap<>();
             rental.put("rental_id", rs.getInt("rental_id"));
             rental.put("car_id", rs.getInt("car_id"));
             rental.put("customer_id", rs.getInt("customer_id"));
             rental.put("brand", rs.getString("brand"));
             rental.put("model", rs.getString("model"));
             rental.put("customer_name", rs.getString("customer_name"));
             rental.put("rental_date", rs.getDate("rental_date"));
             rental.put("return_date", rs.getDate("return_date"));
             rental.put("total_amount", rs.getDouble("total_amount"));
             rentals.add(rental);
         }
     } catch (SQLException e) {
         System.err.println("Error getting active rentals with details: " + e.getMessage());
         e.printStackTrace();
     }
     return rentals;
 }

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

 public double getMonthlyRevenue() {
     String sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM rental_records " +
                 "WHERE MONTH(rental_date) = MONTH(CURRENT_DATE()) " +
                 "AND YEAR(rental_date) = YEAR(CURRENT_DATE()) " +
                 "AND status IN ('Active', 'Completed')";
     
     try (Connection conn = DatabaseConnection.getConnection();
          PreparedStatement pstmt = conn.prepareStatement(sql);
          ResultSet rs = pstmt.executeQuery()) {
         
         if (rs.next()) {
             return rs.getDouble("total");
         }
     } catch (SQLException e) {
         System.err.println("Error getting monthly revenue: " + e.getMessage());
         e.printStackTrace();
     }
     return 0.0;
 }
//In RentDAO class, add:

public List<Map<String, Object>> getRecentRentals(int limit) {
  String sql = "SELECT r.*, c.make, c.model, c.plate_number, " +
               "cust.first_name || ' ' || cust.last_name as customer_name, " +
               "e.first_name || ' ' || e.last_name as employee_name " +
               "FROM rentals r " +
               "JOIN cars c ON r.car_id = c.car_id " +
               "JOIN customers cust ON r.customer_id = cust.customer_id " +
               "JOIN employees e ON r.employee_id = e.employee_id " +
               "WHERE r.status = 'Active' " +
               "ORDER BY r.rental_date DESC LIMIT ?";
  // Execute query and return result
  return null;
}

public List<Map<String, Object>> getRecentReturns(int limit) {
  String sql = "SELECT r.*, c.make, c.model, c.plate_number, " +
               "cust.first_name || ' ' || cust.last_name as customer_name, " +
               "e.first_name || ' ' || e.last_name as employee_name " +
               "FROM rentals r " +
               "JOIN cars c ON r.car_id = c.car_id " +
               "JOIN customers cust ON r.customer_id = cust.customer_id " +
               "JOIN employees e ON r.employee_id = e.employee_id " +
               "WHERE r.status = 'Returned' " +
               "ORDER BY r.return_date DESC LIMIT ?";
  // Execute query and return result
  return null;
}
}



