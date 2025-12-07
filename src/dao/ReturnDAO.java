package dao;

import models.ReturnRecord;
import application.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReturnDAO {
    
    public boolean addReturn(ReturnRecord returnRecord) {
        String sql = "INSERT INTO return_records (rental_id, return_date, late_fee, damage_fee, " +
                     "total_amount, notes, employee_id, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, returnRecord.getRentalId());
            pstmt.setTimestamp(2, Timestamp.valueOf(returnRecord.getReturnDate()));
            pstmt.setDouble(3, returnRecord.getLateFee());
            pstmt.setDouble(4, returnRecord.getDamageFee());
            pstmt.setDouble(5, returnRecord.getTotalAmount());
            pstmt.setString(6, returnRecord.getNotes());
            pstmt.setInt(7, returnRecord.getEmployeeId());
            pstmt.setTimestamp(8, Timestamp.valueOf(returnRecord.getCreatedAt() != null ? 
                           returnRecord.getCreatedAt() : LocalDateTime.now()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        returnRecord.setReturnId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public ReturnRecord getReturnById(int returnId) {
        String sql = "SELECT * FROM return_records WHERE return_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, returnId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToReturn(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<ReturnRecord> getReturnsByRentalId(int rentalId) {
        List<ReturnRecord> returns = new ArrayList<>();
        String sql = "SELECT * FROM return_records WHERE rental_id=? ORDER BY return_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, rentalId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                returns.add(mapResultSetToReturn(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returns;
    }
    
    public List<ReturnRecord> getAllReturns() {
        List<ReturnRecord> returns = new ArrayList<>();
        String sql = "SELECT * FROM return_records ORDER BY return_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                returns.add(mapResultSetToReturn(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returns;
    }
    
    private ReturnRecord mapResultSetToReturn(ResultSet rs) throws SQLException {
        ReturnRecord returnRecord = new ReturnRecord();
        returnRecord.setReturnId(rs.getInt("return_id"));
        returnRecord.setRentalId(rs.getInt("rental_id"));
        
        Timestamp returnDate = rs.getTimestamp("return_date");
        if (returnDate != null) {
            returnRecord.setReturnDate(returnDate.toLocalDateTime());
        }
        
        returnRecord.setLateFee(rs.getDouble("late_fee"));
        returnRecord.setDamageFee(rs.getDouble("damage_fee"));
        returnRecord.setTotalAmount(rs.getDouble("total_amount"));
        returnRecord.setNotes(rs.getString("notes"));
        returnRecord.setEmployeeId(rs.getInt("employee_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            returnRecord.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return returnRecord;
    }
}
