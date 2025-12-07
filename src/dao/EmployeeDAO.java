// src/dao/EmployeeDAO.java
package dao;

import models.Employee;
import application.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private static final String TABLE_NAME = "employees";
    
    // Check if email already exists (FIXED VERSION)
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE LOWER(email) = LOWER(?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking email: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Add new employee (FIXED VERSION)
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO " + TABLE_NAME + " (" +
                     "first_name, last_name, email, phone, address, " +
                     "position, salary, hire_date, status" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            setEmployeeStatementParameters(pstmt, employee);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setEmployeeId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            
            // Check if it's a duplicate email error
            if (e.getMessage().contains("Duplicate") || e.getMessage().contains("unique")) {
                System.err.println("Duplicate email detected: " + employee.getEmail());
            }
            e.printStackTrace();
        }
        return false;
    }
    
    // Update employee
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                     "first_name = ?, last_name = ?, email = ?, phone = ?, address = ?, " +
                     "position = ?, salary = ?, hire_date = ?, status = ? " +
                     "WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            setEmployeeStatementParameters(pstmt, employee);
            pstmt.setInt(9, employee.getEmployeeId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete employee (soft delete)
    public boolean deleteEmployee(int employeeId) {
        String sql = "UPDATE " + TABLE_NAME + " SET status = 'Inactive' WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting employee: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // Get employee by ID
    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error getting employee by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Get all employees
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " ORDER BY first_name, last_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving employees: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    // Search employees
    public List<Employee> searchEmployees(String searchTerm) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + 
                     " WHERE first_name LIKE ? OR last_name LIKE ? OR email LIKE ? OR phone LIKE ? " +
                     "ORDER BY first_name, last_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching employees: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }
    
    // ============================================
    // ADD THIS METHOD FOR DASHBOARD
    // ============================================
    public int getTotalEmployeesCount() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE status = 'Active'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("Total active employees count: " + count);
                return count;
            }
        } catch (SQLException e) {
            System.err.println("Error getting total employees count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Helper method to set parameters
    private void setEmployeeStatementParameters(PreparedStatement pstmt, Employee employee) 
            throws SQLException {
        pstmt.setString(1, employee.getFirstName());
        pstmt.setString(2, employee.getLastName());
        pstmt.setString(3, employee.getEmail());
        pstmt.setString(4, employee.getPhone());
        pstmt.setString(5, employee.getAddress());
        pstmt.setString(6, employee.getPosition());
        pstmt.setDouble(7, employee.getSalary());
        
        if (employee.getHireDate() != null) {
            pstmt.setDate(8, Date.valueOf(employee.getHireDate()));
        } else {
            pstmt.setNull(8, Types.DATE);
        }
        
        pstmt.setString(9, employee.getStatus() != null ? employee.getStatus() : "Active");
    }
    
    // Helper method to map ResultSet to Employee
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("employee_id"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastName(rs.getString("last_name"));
        employee.setEmail(rs.getString("email"));
        employee.setPhone(rs.getString("phone"));
        employee.setAddress(rs.getString("address"));
        employee.setPosition(rs.getString("position"));
        employee.setSalary(rs.getDouble("salary"));
        
        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) {
            employee.setHireDate(hireDate.toLocalDate());
        }
        
        employee.setStatus(rs.getString("status"));
        return employee;
    }
}