package models;

import java.time.LocalDate;

/**
 * ReportData - Model class for holding various report information
 * This class can represent different types of reports:
 * - Monthly revenue reports
 * - Car utilization reports
 * - Customer activity reports
 * - Rental statistics
 */
public class ReportData {
    
    // Common fields for all reports
    private String reportType;
    private LocalDate reportDate;
    
    // Revenue report fields
    private String period; // e.g., "January 2024"
    private double totalRevenue;
    private int totalRentals;
    private int activeRentals;
    private int completedRentals;
    private int returnedRentals;
    private int cancelledRentals; // New field
    private double lateFees; // New field
    private double damageFees; // New field
    
    // Car report fields
    private String carInfo; // Brand + Model
    private String registrationNumber;
    private int timesRented;
    private double carRevenue;
    private double utilizationRate; // Percentage
    
    // Customer report fields
    private String customerName;
    private String customerPhone;
    private int customerRentals;
    private double customerSpent;
    
    // General statistics
    private int availableCars;
    private int totalCars;
    private int totalCustomers;
    private int totalEmployees; // New field
    
    // Maintenance/Damage report fields
    private String damageDescription;
    private double repairCost;
    
    // Constructors
    public ReportData() {
    }
    
    public ReportData(String reportType) {
        this.reportType = reportType;
        this.reportDate = LocalDate.now();
    }
    
    // Getters and Setters
    public String getReportType() {
        return reportType;
    }
    
    public void setReportType(String reportType) {
        this.reportType = reportType;
    }
    
    public LocalDate getReportDate() {
        return reportDate;
    }
    
    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    
    public void setReportDateFromSQLDate(java.sql.Date sqlDate) {
        if (sqlDate != null) {
            this.reportDate = sqlDate.toLocalDate();
        }
    }
    
    public String getPeriod() {
        return period;
    }
    
    public void setPeriod(String period) {
        this.period = period;
    }
    
    public double getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public int getTotalRentals() {
        return totalRentals;
    }
    
    public void setTotalRentals(int totalRentals) {
        this.totalRentals = totalRentals;
    }
    
    public int getActiveRentals() {
        return activeRentals;
    }
    
    public void setActiveRentals(int activeRentals) {
        this.activeRentals = activeRentals;
    }
    
    public int getCompletedRentals() {
        return completedRentals;
    }
    
    public void setCompletedRentals(int completedRentals) {
        this.completedRentals = completedRentals;
    }
    
    public int getReturnedRentals() {
        return returnedRentals;
    }
    
    public void setReturnedRentals(int returnedRentals) {
        this.returnedRentals = returnedRentals;
    }
    
    public int getCancelledRentals() {
        return cancelledRentals;
    }
    
    public void setCancelledRentals(int cancelledRentals) {
        this.cancelledRentals = cancelledRentals;
    }
    
    public double getLateFees() {
        return lateFees;
    }
    
    public void setLateFees(double lateFees) {
        this.lateFees = lateFees;
    }
    
    public double getDamageFees() {
        return damageFees;
    }
    
    public void setDamageFees(double damageFees) {
        this.damageFees = damageFees;
    }
    
    public String getCarInfo() {
        return carInfo;
    }
    
    public void setCarInfo(String carInfo) {
        this.carInfo = carInfo;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public int getTimesRented() {
        return timesRented;
    }
    
    public void setTimesRented(int timesRented) {
        this.timesRented = timesRented;
    }
    
    public double getCarRevenue() {
        return carRevenue;
    }
    
    public void setCarRevenue(double carRevenue) {
        this.carRevenue = carRevenue;
    }
    
    public double getUtilizationRate() {
        return utilizationRate;
    }
    
    public void setUtilizationRate(double utilizationRate) {
        this.utilizationRate = utilizationRate;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerPhone() {
        return customerPhone;
    }
    
    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
    
    public int getCustomerRentals() {
        return customerRentals;
    }
    
    public void setCustomerRentals(int customerRentals) {
        this.customerRentals = customerRentals;
    }
    
    public double getCustomerSpent() {
        return customerSpent;
    }
    
    public void setCustomerSpent(double customerSpent) {
        this.customerSpent = customerSpent;
    }
    
    public int getAvailableCars() {
        return availableCars;
    }
    
    public void setAvailableCars(int availableCars) {
        this.availableCars = availableCars;
    }
    
    public int getTotalCars() {
        return totalCars;
    }
    
    public void setTotalCars(int totalCars) {
        this.totalCars = totalCars;
    }
    
    public int getTotalCustomers() {
        return totalCustomers;
    }
    
    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
    
    public int getTotalEmployees() {
        return totalEmployees;
    }
    
    public void setTotalEmployees(int totalEmployees) {
        this.totalEmployees = totalEmployees;
    }
    
    // Getters and Setters for damage/repair fields
    public String getDamageDescription() {
        return damageDescription;
    }
    
    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }
    
    public double getRepairCost() {
        return repairCost;
    }
    
    public void setRepairCost(double repairCost) {
        this.repairCost = repairCost;
    }
    
    @Override
    public String toString() {
        return "ReportData{" +
                "reportType='" + reportType + '\'' +
                ", period='" + period + '\'' +
                ", totalRevenue=" + totalRevenue +
                ", totalRentals=" + totalRentals +
                '}';
    }
}