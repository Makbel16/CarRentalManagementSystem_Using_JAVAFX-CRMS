package services;

import dao.RentDAO;
import models.RentalRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RentService {
    private RentDAO rentDAO;
    
    public RentService() {
        this.rentDAO = new RentDAO();
    }
    
    public boolean rentCar(int carId, int customerId, int employeeId, 
                          LocalDate rentalDate, LocalDate returnDate, double totalAmount) {
        try {
            RentalRecord rental = new RentalRecord();
            rental.setCarId(carId);
            rental.setCustomerId(customerId);
            rental.setEmployeeId(employeeId);
            rental.setRentalDate(rentalDate);
            rental.setReturnDate(returnDate);
            rental.setTotalAmount(totalAmount);
            rental.setStatus("Active");
            
            return rentDAO.addRental(rental);
        } catch (Exception e) {
            System.err.println("Error renting car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // OPTION 1: Returns Map (for DashboardController)
    public List<Map<String, Object>> getActiveRentals() {
        return rentDAO.getActiveRentalsWithDetails();
    }
    
    // OPTION 2: Returns RentalRecord objects (for other parts)
    public List<RentalRecord> getActiveRentalRecords() {
        return rentDAO.getActiveRentals();
    }
    
    public int getActiveRentalsCount() {
        return rentDAO.getActiveRentalsCount();
    }
    
    public double getMonthlyRevenue() {
        return rentDAO.getMonthlyRevenue();
    }
    
    // Keep all other methods you had
    public boolean updateRental(RentalRecord rental) {
        return rentDAO.updateRental(rental);
    }
    
    public RentalRecord getRentalById(int rentalId) {
        return rentDAO.getRentalById(rentalId);
    }
    
    public List<RentalRecord> getAllRentals() {
        return rentDAO.getAllRentals();
    }
    
    public List<RentalRecord> getRentalsByCustomerId(int customerId) {
        return rentDAO.getRentalsByCustomerId(customerId);
    }
    
    public List<RentalRecord> getRentalsByCarId(int carId) {
        return rentDAO.getRentalsByCarId(carId);
    }
    
    public double getMonthlyRevenue(int year, int month) {
        return rentDAO.getMonthlyRevenue(year, month);
    }
 // Add these methods to the RentService class

    public List<Map<String, Object>> getRecentRentals(int limit) {
        return rentDAO.getRecentRentals(limit);
    }

    public List<Map<String, Object>> getRecentReturns(int limit) {
        return rentDAO.getRecentReturns(limit);
    }
 // Add this method to RentService class
    public boolean returnCar(int rentalId, int employeeId, double lateFee, 
                             double damageFee, String notes) {
        try {
            // Get the rental record
            RentalRecord rental = rentDAO.getRentalById(rentalId);
            if (rental == null) {
                return false;
            }
            
            // Update rental record
            rental.setStatus("Returned");
            rental.setActualReturnDate(LocalDate.now());
            rental.setLateFee(lateFee);
            rental.setDamageFee(damageFee);
            rental.setNotes(notes);
            rental.setEmployeeId(employeeId); // Employee who processed the return
            
            // Update total amount to include fees
            rental.setTotalAmount(rental.getTotalAmount() + lateFee + damageFee);
            
            // Update the rental in database
            boolean rentalUpdated = rentDAO.updateRental(rental);
            
            if (rentalUpdated) {
                // Update car status to available
                CarService carService = new CarService();
                return carService.updateCarStatus(rental.getCarId(), "Available");
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Error returning car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }  
}