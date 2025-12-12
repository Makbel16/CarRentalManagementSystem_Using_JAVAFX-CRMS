package services;

import dao.RentDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import models.RentalRecord;

public class RentService {
    private RentDAO rentDAO;
    
    public RentService() {
        this.rentDAO = new RentDAO();
    }
    
    public boolean rentCar(int carId, int customerId, int employeeId, 
            LocalDate rentalDate, LocalDate returnDate, double totalAmount) {
try {
// First, update the car status to mark it as rented
CarService carService = new CarService();
if (!carService.updateCarStatus(carId, "Rented")) {
  System.err.println("Failed to update car status to Rented");
  return false;
}

// Then create the rental record
RentalRecord rental = new RentalRecord();
rental.setCarId(carId);
rental.setCustomerId(customerId);
rental.setEmployeeId(employeeId);
rental.setRentalDate(rentalDate);
rental.setReturnDate(returnDate);
rental.setTotalAmount(totalAmount);
rental.setStatus("Active");

boolean rentalAdded = rentDAO.addRental(rental);

// If rental creation fails, revert the car status
if (!rentalAdded) {
  carService.updateCarStatus(carId, "Available");
  return false;
}

return true;
} catch (Exception e) {
System.err.println("Error renting car: " + e.getMessage());
e.printStackTrace();
// If there's an error, make sure to set the car back to Available
CarService carService = new CarService();
carService.updateCarStatus(carId, "Available");
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

    public List<Map<String, Object>> getRecentRentals(int limit) {
        return rentDAO.getRecentRentals(limit);
    }

    public List<Map<String, Object>> getRecentReturns(int limit) {
        return rentDAO.getRecentReturns(limit);
    }
    
    public boolean returnCar(int rentalId, int employeeId, String notes) {
        try {
            // Get the rental record
            RentalRecord rental = rentDAO.getRentalById(rentalId);
            if (rental == null) {
                System.err.println("Rental not found with ID: " + rentalId);
                return false;
            }
            
            // First update the car status to Available
            CarService carService = new CarService();
            if (!carService.updateCarStatus(rental.getCarId(), "Available")) {
                System.err.println("Failed to update car status to Available");
                return false;
            }
            
            // Then update the rental record
            rental.setStatus("Returned");
            rental.setActualReturnDate(LocalDate.now());
            rental.setNotes(notes);
            
            boolean rentalUpdated = rentDAO.updateRental(rental);
            
            // If rental update fails, log the error but don't revert the car status
            // since the car is physically returned
            if (!rentalUpdated) {
                System.err.println("Warning: Car status updated but failed to update rental record for ID: " + rentalId);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error returning car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // New method to handle return with late fees and damage fees
    public boolean returnCar(int rentalId, int employeeId, double lateFee, double damageFee, String notes) {
        try {
            // Get the rental record
            RentalRecord rental = rentDAO.getRentalById(rentalId);
            if (rental == null) {
                System.err.println("Rental not found with ID: " + rentalId);
                return false;
            }
            
            // Update the rental record with fees
            rental.setLateFee(lateFee);
            rental.setDamageFee(damageFee);
            double totalAmount = rental.getTotalAmount() + lateFee + damageFee;
            rental.setTotalAmount(totalAmount);
            
            // First update the car status to Available
            CarService carService = new CarService();
            if (!carService.updateCarStatus(rental.getCarId(), "Available")) {
                System.err.println("Failed to update car status to Available");
                return false;
            }
            
            // Then update the rental record
            rental.setStatus("Returned");
            rental.setActualReturnDate(LocalDate.now());
            rental.setNotes(notes);
            
            boolean rentalUpdated = rentDAO.updateRental(rental);
            
            // If rental update fails, log the error but don't revert the car status
            // since the car is physically returned
            if (!rentalUpdated) {
                System.err.println("Warning: Car status updated but failed to update rental record for ID: " + rentalId);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error returning car: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}