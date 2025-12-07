package services;

import dao.DashboardDAO;
import java.util.Map;

public class DashboardService {
    private final CarService carService;
    private final CustomerService customerService;
    private final EmployeeService employeeService;
    private final RentService rentService;
    private final DashboardDAO dashboardDAO;

    public DashboardService() {
        this.carService = new CarService();
        this.customerService = new CustomerService();
        this.employeeService = new EmployeeService();
        this.rentService = new RentService();
        this.dashboardDAO = new DashboardDAO();
    }

    public Map<String, Object> getDashboardStats() {
        try {
            int totalCars = carService.getTotalCarsCount();
            int availableCars = carService.getAvailableCarsCount();
            int totalCustomers = customerService.getTotalCustomersCount();
            int activeRentals = rentService.getActiveRentalsCount();
            int totalEmployees = employeeService.getTotalEmployeesCount();
            double monthlyRevenue = rentService.getMonthlyRevenue();

            // Debug output
            System.out.println("Dashboard Stats:");
            System.out.println("Total Cars: " + totalCars);
            System.out.println("Available Cars: " + availableCars);
            System.out.println("Total Customers: " + totalCustomers);
            System.out.println("Active Rentals: " + activeRentals);
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
}