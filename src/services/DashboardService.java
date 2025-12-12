package services;

import dao.DashboardDAO;
import dao.ReportsDAO; // Add this import
import models.ReportData; // Add this import
import java.util.Map;

public class DashboardService {
    private final CarService carService;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final RentService rentService;
    private final DashboardDAO dashboardDAO;
    private final ReportsDAO reportsDAO; // Add this field

    public DashboardService() {
        this.carService = new CarService();
        this.customerService = new CustomerService();
        this.employeeService = new EmployeeService();
        this.rentService = new RentService();
        this.dashboardDAO = new DashboardDAO();
        this.reportsDAO = new ReportsDAO(); // Initialize ReportsDAO
    }

    public Map<String, Object> getDashboardStats() {
        try {
            int totalCars = carService.getTotalCarsCount();
            int activeRentals = rentService.getActiveRentalsCount();
            int rawAvailableCars = carService.getAvailableCarsCount();
            int availableCars = Math.min(rawAvailableCars, Math.max(0, totalCars - activeRentals));
            int totalCustomers = customerService.getTotalCustomersCount();
            int totalEmployees = employeeService.getTotalEmployeesCount();
            double monthlyRevenue = rentService.getMonthlyRevenue();

            // Debug output
            System.out.println("Dashboard Stats:");
            System.out.println("Total Cars: " + totalCars);
            System.out.println("Available Cars (raw): " + rawAvailableCars);
            System.out.println("Active Rentals: " + activeRentals);
            System.out.println("Available Cars (displayed): " + availableCars);
            System.out.println("Total Customers: " + totalCustomers);
            System.out.println("Total Employees: " + totalEmployees);
            System.out.println("Monthly Revenue: " + monthlyRevenue);

            return Map.of(
                "totalCars", totalCars,
                "availableCars", availableCars,
                "totalCustomers", totalCustomers,
                "activeRentals", activeRentals,
                "totalEmployees", totalEmployees,
                "monthlyRevenue", monthlyRevenue  // Keep as double
            );
        } catch (Exception e) {
            System.err.println("Error getting dashboard stats: " + e.getMessage());
            e.printStackTrace();
            return Map.of(
                "totalCars", 0,
                "availableCars", 0,
                "totalCustomers", 0,
                "activeRentals", 0,
                "totalEmployees", 0,
                "monthlyRevenue", 0.0  // Changed to double
            );
        }
    }

    public Map<String, Object> getRecentActivity() {
        return Map.of(
            "recentRentals", rentService.getRecentRentals(5),
            "recentReturns", rentService.getRecentReturns(5)
        );
    }
    
    // New method to get revenue as double
    public double getMonthlyRevenue() {
        try {
            return rentService.getMonthlyRevenue();
        } catch (Exception e) {
            System.err.println("Error getting monthly revenue: " + e.getMessage());
            return 0.0;
        }
    }
    
    // New method to get comprehensive rental statistics for dashboard
    public ReportData getRentalStatistics() {
        try {
            // Get current month and year for statistics
            java.time.LocalDate now = java.time.LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();
            
            // Get comprehensive rental statistics from ReportsDAO
            ReportData rentalStats = reportsDAO.getRentalStatistics(currentMonth, currentYear);
            
            // Add system statistics to the report
            ReportData systemStats = reportsDAO.getSystemStatistics();
            rentalStats.setTotalCars(systemStats.getTotalCars());
            rentalStats.setAvailableCars(systemStats.getAvailableCars());
            rentalStats.setTotalCustomers(systemStats.getTotalCustomers());
            rentalStats.setTotalEmployees(systemStats.getTotalEmployees());
            
            return rentalStats;
        } catch (Exception e) {
            System.err.println("Error getting rental statistics: " + e.getMessage());
            e.printStackTrace();
            
            // Return a default ReportData object in case of error
            ReportData defaultStats = new ReportData("DASHBOARD_STATS");
            defaultStats.setTotalRentals(0);
            defaultStats.setActiveRentals(0);
            defaultStats.setCompletedRentals(0);
            defaultStats.setReturnedRentals(0);
            defaultStats.setCancelledRentals(0);
            defaultStats.setTotalRevenue(0.0);
            defaultStats.setLateFees(0.0);
            defaultStats.setDamageFees(0.0);
            defaultStats.setTotalCars(0);
            defaultStats.setAvailableCars(0);
            defaultStats.setTotalCustomers(0);
            defaultStats.setTotalEmployees(0);
            
            return defaultStats;
        }
    }
    
    // Method to get monthly comparison data
    public Map<String, Object> getMonthlyComparison() {
        try {
            java.time.LocalDate now = java.time.LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();
            
            int previousMonth = currentMonth == 1 ? 12 : currentMonth - 1;
            int previousYear = currentMonth == 1 ? currentYear - 1 : currentYear;
            
            // Get current month statistics
            ReportData currentStats = reportsDAO.getRentalStatistics(currentMonth, currentYear);
            // Get previous month statistics
            ReportData previousStats = reportsDAO.getRentalStatistics(previousMonth, previousYear);
            
            // Calculate percentage changes
            double revenueChange = calculatePercentageChange(
                previousStats.getTotalRevenue(), currentStats.getTotalRevenue());
            int rentalChange = calculatePercentageChange(
                previousStats.getTotalRentals(), currentStats.getTotalRentals());
                
            return Map.of(
                "currentRevenue", currentStats.getTotalRevenue(),
                "previousRevenue", previousStats.getTotalRevenue(),
                "revenueChange", revenueChange,
                "currentRentals", currentStats.getTotalRentals(),
                "previousRentals", previousStats.getTotalRentals(),
                "rentalChange", rentalChange
            );
        } catch (Exception e) {
            System.err.println("Error getting monthly comparison: " + e.getMessage());
            e.printStackTrace();
            
            return Map.of(
                "currentRevenue", 0.0,
                "previousRevenue", 0.0,
                "revenueChange", 0.0,
                "currentRentals", 0,
                "previousRentals", 0,
                "rentalChange", 0
            );
        }
    }
    
    // Helper method to calculate percentage change
    private double calculatePercentageChange(double oldValue, double newValue) {
        if (oldValue == 0) {
            return newValue > 0 ? 100.0 : 0.0;
        }
        return ((newValue - oldValue) / oldValue) * 100;
    }
    
    // Helper method to calculate percentage change for integers
    private int calculatePercentageChange(int oldValue, int newValue) {
        if (oldValue == 0) {
            return newValue > 0 ? 100 : 0;
        }
        return (int) (((double)(newValue - oldValue) / oldValue) * 100);
    }
}