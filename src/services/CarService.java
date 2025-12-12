package services;


import application.DatabaseConnection;
import dao.CarDAO;
import models.Car;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CarService {
    private CarDAO carDAO;
    
    public CarService() {
        this.carDAO = new CarDAO();
    }
    
    public boolean addCar(Car car) {
        if (car.getBrand() == null || car.getBrand().trim().isEmpty()) {
            return false;
        }
        if (car.getModel() == null || car.getModel().trim().isEmpty()) {
            return false;
        }
        if (car.getRegistrationNumber() == null || car.getRegistrationNumber().trim().isEmpty()) {
            return false;
        }
        if (car.getPricePerDay() <= 0) {
            return false;
        }
        
        // Check if registration number already exists
        Car existing = carDAO.getCarByRegistration(car.getRegistrationNumber());
        if (existing != null) {
            return false;
        }
        
        return carDAO.addCar(car);
    }
    
    public boolean updateCar(Car car) {
        if (car.getCarId() <= 0) {
            return false;
        }
        return carDAO.updateCar(car);
    }
    
    public boolean deleteCar(int carId) {
        return carDAO.deleteCar(carId);
    }
    
    public Car getCarById(int carId) {
        return carDAO.getCarById(carId);
    }
    
    public List<Car> getAllCars() {
        return carDAO.getAllCars();
    }
    
    public List<Car> searchCars(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCars();
        }
        return carDAO.searchCars(searchTerm.trim());
    }
    
    public List<Car> getAvailableCars() {
        return carDAO.getAvailableCars();
    }
    
    public List<Car> getRentedCars() {
        return carDAO.getRentedCars();
    }
    
    public int getTotalCarsCount() {
        return getAllCars().size();
    }
    
    public int getAvailableCarsCount() {
        return getAvailableCars().size();
    }

    public boolean updateCarStatus(int carId, String status) {
        String sql = "UPDATE cars SET status = ?, availability = ? WHERE car_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String availability;
            switch (status.toLowerCase()) {
                case "available":
                case "active":
                    status = "Available";
                    availability = "Available";
                    break;
                case "rented":
                    status = "Rented";
                    availability = "Rented";
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
            
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating car status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}



