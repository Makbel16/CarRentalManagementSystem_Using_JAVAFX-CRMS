package services;

import dao.CarDAO;
import models.Car;
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
        try {
            // Validate status
            List<String> validStatuses = List.of("Available", "Rented", "Maintenance", "Unavailable");
            if (!validStatuses.contains(status)) {
                System.err.println("Invalid car status: " + status);
                return false;
            }
            
            // Get the car first to ensure it exists
            Car car = carDAO.getCarById(carId);
            if (car == null) {
                System.err.println("Car not found with ID: " + carId);
                return false;
            }
            
            // Update the status in database
            return carDAO.updateCarStatus(carId, status);
            
        } catch (Exception e) {
            System.err.println("Error updating car status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}



