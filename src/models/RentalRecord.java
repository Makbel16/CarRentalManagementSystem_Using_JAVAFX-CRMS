package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalRecord {
    private int rentalId;
    private int carId;
    private int customerId;
    private int employeeId;
    private LocalDate rentalDate;
    private LocalDate returnDate;
    private LocalDate actualReturnDate;  // Added
    private double totalAmount;
    private double lateFee;  // Added
    private double damageFee;  // Added
    private String status; // "Active", "Completed", "Cancelled", "Returned"
    private LocalDateTime createdAt;
    private String notes;
    
    public RentalRecord() {
        this.createdAt = LocalDateTime.now();
    }
    
    public RentalRecord(int carId, int customerId, int employeeId, LocalDate rentalDate, 
                       LocalDate returnDate, double totalAmount) {
        this.carId = carId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.rentalDate = rentalDate;
        this.returnDate = returnDate;
        this.totalAmount = totalAmount;
        this.status = "Active";
        this.createdAt = LocalDateTime.now();
        this.lateFee = 0.0;
        this.damageFee = 0.0;
    }
    
    // Getters and Setters
    public int getRentalId() {
        return rentalId;
    }
    
    public void setRentalId(int rentalId) {
        this.rentalId = rentalId;
    }
    
    public int getCarId() {
        return carId;
    }
    
    public void setCarId(int carId) {
        this.carId = carId;
    }
    
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public int getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
    
    public LocalDate getRentalDate() {
        return rentalDate;
    }
    
    public void setRentalDate(LocalDate rentalDate) {
        this.rentalDate = rentalDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }
    
    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public double getLateFee() {
        return lateFee;
    }
    
    public void setLateFee(double lateFee) {
        this.lateFee = lateFee;
    }
    
    public double getDamageFee() {
        return damageFee;
    }
    
    public void setDamageFee(double damageFee) {
        this.damageFee = damageFee;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Helper method to calculate total with fees
    public double getTotalWithFees() {
        return totalAmount + lateFee + damageFee;
    }
    
    @Override
    public String toString() {
        return "RentalRecord{" +
                "rentalId=" + rentalId +
                ", carId=" + carId +
                ", customerId=" + customerId +
                ", employeeId=" + employeeId +
                ", rentalDate=" + rentalDate +
                ", returnDate=" + returnDate +
                ", actualReturnDate=" + actualReturnDate +
                ", totalAmount=" + totalAmount +
                ", lateFee=" + lateFee +
                ", damageFee=" + damageFee +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}