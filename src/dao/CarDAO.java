package dao;

import models.Car;
import application.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarDAO {
    
    public boolean addCar(Car car) {
        String sql = "INSERT INTO cars (brand, model, year, color, registration_number, " +
                     "price_per_day, availability, fuel_type, mileage, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, car.getBrand());
            pstmt.setString(2, car.getModel());
            pstmt.setInt(3, car.getYear());
            pstmt.setString(4, car.getColor());
            pstmt.setString(5, car.getRegistrationNumber());
            pstmt.setDouble(6, car.getPricePerDay());
            pstmt.setString(7, car.getAvailability());
            pstmt.setString(8, car.getFuelType());
            pstmt.setInt(9, car.getMileage());
            pstmt.setString(10, car.getStatus());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateCar(Car car) {
        String sql = "UPDATE cars SET brand=?, model=?, year=?, color=?, registration_number=?, " +
                     "price_per_day=?, availability=?, fuel_type=?, mileage=?, status=? " +
                     "WHERE car_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, car.getBrand());
            pstmt.setString(2, car.getModel());
            pstmt.setInt(3, car.getYear());
            pstmt.setString(4, car.getColor());
            pstmt.setString(5, car.getRegistrationNumber());
            pstmt.setDouble(6, car.getPricePerDay());
            pstmt.setString(7, car.getAvailability());
            pstmt.setString(8, car.getFuelType());
            pstmt.setInt(9, car.getMileage());
            pstmt.setString(10, car.getStatus());
            pstmt.setInt(11, car.getCarId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteCar(int carId) {
        String sql = "DELETE FROM cars WHERE car_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateCarStatus(int carId, String status) {
        String sql = "UPDATE cars SET status = ?, availability = ? WHERE car_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String availability;
            switch (status.toLowerCase()) {
                case "available":
                    status = "Available";
                    availability = "Available";
                    break;
                case "active":
                case "rented":
                    status = "Active";  // Keep status as Active for active rentals
                    availability = "Rented";  // But set availability to Rented
                    break;
                case "maintenance":
                case "unavailable":
                default:
                    status = "Unavailable";
                    availability = "Unavailable";
            }
            
            pstmt.setString(1, status);
            pstmt.setString(2, availability);
            pstmt.setInt(3, carId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating car status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public Car getCarById(int carId) {
        String sql = "SELECT * FROM cars WHERE car_id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, carId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCar(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Car getCarByRegistration(String registrationNumber) {
        String sql = "SELECT * FROM cars WHERE registration_number=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, registrationNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCar(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY car_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }
    
    public List<Car> searchCars(String searchTerm) {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE brand LIKE ? OR model LIKE ? OR " +
                     "registration_number LIKE ? OR color LIKE ? ORDER BY car_id DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }
    
    public List<Car> getAvailableCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE availability='Available' ORDER BY brand, model";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }
    public List<Car> getRentedCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars WHERE availability='Rented' ORDER BY brand, model";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                cars.add(mapResultSetToCar(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }
    
    private Car mapResultSetToCar(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setCarId(rs.getInt("car_id"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setYear(rs.getInt("year"));
        car.setColor(rs.getString("color"));
        car.setRegistrationNumber(rs.getString("registration_number"));
        car.setPricePerDay(rs.getDouble("price_per_day"));
        car.setAvailability(rs.getString("availability"));
        car.setFuelType(rs.getString("fuel_type"));
        car.setMileage(rs.getInt("mileage"));
        car.setStatus(rs.getString("status"));
        return car;
    }
    public int getAvailableCarsCount() {
        String sql = "SELECT COUNT(*) as count FROM cars WHERE availability='Available'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting available cars count: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}