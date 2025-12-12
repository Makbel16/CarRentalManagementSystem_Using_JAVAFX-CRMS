package dao;

import models.ReportData;
import application.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ReportsDAO - Data Access Object for generating various reports
 * Handles all database queries for reporting functionality
 */
public class ReportsDAO {
    
    /**
     * Get monthly revenue report data
     * @param month Month number (1-12)
     * @param year Year
     * @return ReportData with monthly statistics
     */
    public ReportData getMonthlyRevenueReport(int month, int year) {
        ReportData report = new ReportData("MONTHLY_REVENUE");
        report.setPeriod(getMonthName(month) + " " + year);
        
        String sql = "SELECT " +
                     "COUNT(*) as total_rentals, " +
                     "SUM(CASE WHEN status = 'Active' THEN 1 ELSE 0 END) as active_rentals, " +
                     "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) as completed_rentals, " +
                     "SUM(CASE WHEN status = 'Returned' THEN 1 ELSE 0 END) as returned_rentals, " +
                     "COALESCE(SUM(total_amount), 0) as total_revenue " +
                     "FROM rental_records " +
                     "WHERE MONTH(rental_date) = ? AND YEAR(rental_date) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                report.setTotalRentals(rs.getInt("total_rentals"));
                report.setActiveRentals(rs.getInt("active_rentals"));
                report.setCompletedRentals(rs.getInt("completed_rentals"));
                report.setReturnedRentals(rs.getInt("returned_rentals"));
                report.setTotalRevenue(rs.getDouble("total_revenue"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return report;
    }
    
    /**
     * Get comprehensive rental statistics
     * @param month Month number (1-12)
     * @param year Year
     * @return ReportData with detailed rental statistics
     */
    public ReportData getRentalStatistics(int month, int year) {
        ReportData report = new ReportData("RENTAL_STATISTICS");
        
        // Get rental counts by status
        String rentalStatsSql = "SELECT " +
                               "COUNT(*) as total_rentals, " +
                               "SUM(CASE WHEN status = 'Active' THEN 1 ELSE 0 END) as active_rentals, " +
                               "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) as completed_rentals, " +
                               "SUM(CASE WHEN status = 'Returned' THEN 1 ELSE 0 END) as returned_rentals, " +
                               "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled_rentals " +
                               "FROM rental_records " +
                               "WHERE MONTH(rental_date) = ? AND YEAR(rental_date) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(rentalStatsSql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                report.setTotalRentals(rs.getInt("total_rentals"));
                report.setActiveRentals(rs.getInt("active_rentals"));
                report.setCompletedRentals(rs.getInt("completed_rentals"));
                report.setReturnedRentals(rs.getInt("returned_rentals"));
                report.setCancelledRentals(rs.getInt("cancelled_rentals"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Get revenue statistics
        String revenueStatsSql = "SELECT " +
                                "COALESCE(SUM(total_amount), 0) as total_revenue, " +
                                "COALESCE(SUM(late_fee), 0) as total_late_fees, " +
                                "COALESCE(SUM(damage_fee), 0) as total_damage_fees " +
                                "FROM rental_records " +
                                "WHERE MONTH(rental_date) = ? AND YEAR(rental_date) = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(revenueStatsSql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                report.setTotalRevenue(rs.getDouble("total_revenue"));
                report.setLateFees(rs.getDouble("total_late_fees"));
                report.setDamageFees(rs.getDouble("total_damage_fees"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return report;
    }
    
    /**
     * Get car utilization report
     * @param month Month number (1-12)
     * @param year Year
     * @return List of ReportData for each car
     */
    public List<ReportData> getCarUtilizationReport(int month, int year) {
        List<ReportData> reports = new ArrayList<>();
        
        String sql = "SELECT " +
                     "c.brand, c.model, c.registration_number, " +
                     "COUNT(r.rental_id) as times_rented, " +
                     "COALESCE(SUM(r.total_amount), 0) as car_revenue " +
                     "FROM cars c " +
                     "LEFT JOIN rental_records r ON c.car_id = r.car_id " +
                     "AND MONTH(r.rental_date) = ? AND YEAR(r.rental_date) = ? " +
                     "GROUP BY c.car_id " +
                     "ORDER BY times_rented DESC, car_revenue DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ReportData report = new ReportData("CAR_UTILIZATION");
                report.setCarInfo(rs.getString("brand") + " " + rs.getString("model"));
                report.setRegistrationNumber(rs.getString("registration_number"));
                report.setTimesRented(rs.getInt("times_rented"));
                report.setCarRevenue(rs.getDouble("car_revenue"));
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reports;
    }
    
    /**
     * Get customer activity report
     * @param month Month number (1-12)
     * @param year Year
     * @return List of ReportData for each customer
     */
    public List<ReportData> getCustomerActivityReport(int month, int year) {
        List<ReportData> reports = new ArrayList<>();
        
        String sql = "SELECT " +
                     "cust.first_name, cust.last_name, cust.phone, " +
                     "COUNT(r.rental_id) as customer_rentals, " +
                     "COALESCE(SUM(r.total_amount), 0) as customer_spent " +
                     "FROM customers cust " +
                     "LEFT JOIN rental_records r ON cust.customer_id = r.customer_id " +
                     "AND MONTH(r.rental_date) = ? AND YEAR(rental_date) = ? " +
                     "GROUP BY cust.customer_id " +
                     "ORDER BY customer_spent DESC, customer_rentals DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, month);
            pstmt.setInt(2, year);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ReportData report = new ReportData("CUSTOMER_ACTIVITY");
                report.setCustomerName(rs.getString("first_name") + " " + rs.getString("last_name"));
                report.setCustomerPhone(rs.getString("phone"));
                report.setCustomerRentals(rs.getInt("customer_rentals"));
                report.setCustomerSpent(rs.getDouble("customer_spent"));
                reports.add(report);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reports;
    }
    
    /**
     * Get system statistics for dashboard/report header
     * @return ReportData with system statistics
     */
    public ReportData getSystemStatistics() {
        ReportData report = new ReportData("SYSTEM_STATS");
        
        // Get total cars
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total_cars FROM cars");
            if (rs.next()) {
                report.setTotalCars(rs.getInt("total_cars"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as total_customers FROM customers");
            if (rs.next()) {
                report.setTotalCustomers(rs.getInt("total_customers"));
            }
            
            rs = stmt.executeQuery("SELECT COUNT(*) as available_cars FROM cars WHERE availability = 'Available'");
            if (rs.next()) {
                report.setAvailableCars(rs.getInt("available_cars"));
            }
            
            // Get total employees
            rs = stmt.executeQuery("SELECT COUNT(*) as total_employees FROM employees");
            if (rs.next()) {
                report.setTotalEmployees(rs.getInt("total_employees"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return report;
    }
    
    /**
     * Export report data to CSV format
     * @param reportType Type of report to export
     * @param month Month for the report
     * @param year Year for the report
     * @return CSV formatted string
     */
    public String exportToCSV(String reportType, int month, int year) {
        StringBuilder csv = new StringBuilder();
        
        switch (reportType) {
            case "MONTHLY_REVENUE":
                csv.append("Monthly Revenue Report\n");
                csv.append("Period,Total Rentals,Active Rentals,Completed Rentals,Returned Rentals,Total Revenue,Late Fees,Damage Fees\n");
                
                ReportData revenueReport = getRentalStatistics(month, year);
                csv.append(String.format("%s,%d,%d,%d,%d,%.2f,%.2f,%.2f\n",
                    getMonthName(month) + " " + year,
                    revenueReport.getTotalRentals(),
                    revenueReport.getActiveRentals(),
                    revenueReport.getCompletedRentals(),
                    revenueReport.getReturnedRentals(),
                    revenueReport.getTotalRevenue(),
                    revenueReport.getLateFees(),
                    revenueReport.getDamageFees()));
                break;
                
            case "CAR_UTILIZATION":
                csv.append("Car Utilization Report\n");
                csv.append("Car,Registration Number,Rentals,Revenue\n");
                
                List<ReportData> carReports = getCarUtilizationReport(month, year);
                for (ReportData carReport : carReports) {
                    csv.append(String.format("%s,%s,%d,%.2f\n",
                        carReport.getCarInfo(),
                        carReport.getRegistrationNumber(),
                        carReport.getTimesRented(),
                        carReport.getCarRevenue()));
                }
                break;
                
            case "CUSTOMER_ACTIVITY":
                csv.append("Customer Activity Report\n");
                csv.append("Customer Name,Phone,Rentals,Total Spent\n");
                
                List<ReportData> customerReports = getCustomerActivityReport(month, year);
                for (ReportData customerReport : customerReports) {
                    csv.append(String.format("%s,%s,%d,%.2f\n",
                        customerReport.getCustomerName(),
                        customerReport.getCustomerPhone(),
                        customerReport.getCustomerRentals(),
                        customerReport.getCustomerSpent()));
                }
                break;
                
            default:
                csv.append("Invalid report type\n");
        }
        
        return csv.toString();
    }
    
    /**
     * Helper method to get month name from number
     */
    private String getMonthName(int month) {
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        return (month >= 1 && month <= 12) ? months[month - 1] : "Unknown";
    }
}