package services;

import dao.ReportsDAO;
import models.ReportData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ReportsService - Business logic layer for report generation
 * Handles report data processing and formatting
 */
public class ReportsService {
    
    private ReportsDAO reportsDAO;
    
    public ReportsService() {
        this.reportsDAO = new ReportsDAO();
    }
    
    /**
     * Generate complete monthly report
     * @param month Month number (1-12)
     * @param year Year
     * @return ReportData with all monthly statistics
     */
    public ReportData generateMonthlyReport(int month, int year) {
        ReportData report = reportsDAO.getMonthlyRevenueReport(month, year);
        
        // Add system statistics
        ReportData systemStats = reportsDAO.getSystemStatistics();
        report.setTotalCars(systemStats.getTotalCars());
        report.setTotalCustomers(systemStats.getTotalCustomers());
        report.setAvailableCars(systemStats.getAvailableCars());
        report.setTotalEmployees(systemStats.getTotalEmployees());
        
        return report;
    }
    
    /**
     * Get comprehensive rental statistics
     * @param month Month number (1-12)
     * @param year Year
     * @return ReportData with detailed rental and revenue statistics
     */
    public ReportData getRentalStatistics(int month, int year) {
        return reportsDAO.getRentalStatistics(month, year);
    }
    
    /**
     * Get car utilization data for the month
     * @param month Month number (1-12)
     * @param year Year
     * @return List of car utilization reports
     */
    public List<ReportData> getCarUtilizationReport(int month, int year) {
        return reportsDAO.getCarUtilizationReport(month, year);
    }
    
    /**
     * Get customer activity data for the month
     * @param month Month number (1-12)
     * @param year Year
     * @return List of customer activity reports
     */
    public List<ReportData> getCustomerActivityReport(int month, int year) {
        return reportsDAO.getCustomerActivityReport(month, year);
    }
    
    /**
     * Get all reports for a specific month
     * @param month Month number (1-12)
     * @param year Year
     * @return Combined report data
     */
    public CombinedReportData getAllReports(int month, int year) {
        CombinedReportData combined = new CombinedReportData();
        
        combined.setMonthlyReport(generateMonthlyReport(month, year));
        combined.setCarReports(getCarUtilizationReport(month, year));
        combined.setCustomerReports(getCustomerActivityReport(month, year));
        
        return combined;
    }
    
    /**
     * Export report to CSV file
     * @param reportType Type of report to export
     * @param month Month for the report
     * @param year Year for the report
     * @return Path to the exported CSV file
     * @throws IOException If there's an error writing the file
     */
    public String exportToCSV(String reportType, int month, int year) throws IOException {
        String csvData = reportsDAO.exportToCSV(reportType, month, year);
        
        // Create filename with timestamp (using LocalDateTime instead of LocalDate)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String filename = String.format("%s_report_%s.csv", reportType.toLowerCase(), timestamp);
        
        // Use absolute path to ensure file is created in a known location
        String userHome = System.getProperty("user.home");
        String reportsDirPath = userHome + File.separator + "Documents" + File.separator + "CarRentalReports";
        
        // Create reports directory if it doesn't exist
        File reportsDir = new File(reportsDirPath);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
        
        // Write CSV file
        String filepath = reportsDirPath + File.separator + filename;
        try (FileWriter writer = new FileWriter(filepath, java.nio.charset.StandardCharsets.UTF_8)) {
            writer.write(csvData);
        }
        
        return filepath;
    }
    
    /**
     * Print report (placeholder for future implementation)
     * @param reportType Type of report to print
     * @param month Month for the report
     * @param year Year for the report
     */
    public void printReport(String reportType, int month, int year) {
        // Placeholder for print functionality
        // In a real implementation, this would generate a printable format
        System.out.println("Printing report: " + reportType + " for " + month + "/" + year);
        
        try {
            // For demonstration purposes, also save a print-friendly version
            String printData = reportsDAO.exportToCSV(reportType, month, year);
            
            // Create filename with timestamp (using LocalDateTime instead of LocalDate)
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String timestamp = LocalDateTime.now().format(formatter);
            String filename = String.format("%s_report_%s_print.txt", reportType.toLowerCase(), timestamp);
            
            // Use absolute path to ensure file is created in a known location
            String userHome = System.getProperty("user.home");
            String reportsDirPath = userHome + File.separator + "Documents" + File.separator + "CarRentalReports";
            
            // Create reports directory if it doesn't exist
            File reportsDir = new File(reportsDirPath);
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
            
            // Write print file
            String filepath = reportsDirPath + File.separator + filename;
            try (FileWriter writer = new FileWriter(filepath, java.nio.charset.StandardCharsets.UTF_8)) {
                writer.write("===== CAR RENTAL SYSTEM REPORT =====\n");
                writer.write("Report Type: " + reportType.replace("_", " ") + "\n");
                writer.write("Generated: " + LocalDateTime.now().toString() + "\n");
                writer.write("====================================\n\n");
                writer.write(printData);
                writer.write("\n\n===== END OF REPORT =====\n");
            }
            
            System.out.println("Print-friendly report saved to: " + filepath);
        } catch (IOException e) {
            System.err.println("Failed to save print-friendly report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper class to combine all report types
     */
    public static class CombinedReportData {
        private ReportData monthlyReport;
        private List<ReportData> carReports;
        private List<ReportData> customerReports;
        
        public ReportData getMonthlyReport() {
            return monthlyReport;
        }
        
        public void setMonthlyReport(ReportData monthlyReport) {
            this.monthlyReport = monthlyReport;
        }
        
        public List<ReportData> getCarReports() {
            return carReports;
        }
        
        public void setCarReports(List<ReportData> carReports) {
            this.carReports = carReports;
        }
        
        public List<ReportData> getCustomerReports() {
            return customerReports;
        }
        
        public void setCustomerReports(List<ReportData> customerReports) {
            this.customerReports = customerReports;
        }
    }
}