package controllers.reports;

import controllers.utils.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import models.ReportData;
import services.ReportsService;
import controllers.utils.SessionManager;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * ReportsController - UI controller for the reports module
 * Handles user interactions for generating and viewing reports
 */
public class ReportsController implements Initializable {
    
    @FXML private ComboBox<String> monthComboBox;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private Button generateReportButton;
    @FXML private TabPane reportTabPane;
    
    // Monthly Report Tab
    @FXML private Label periodLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalRentalsLabel;
    @FXML private Label activeRentalsLabel;
    @FXML private Label completedRentalsLabel;
    @FXML private Label returnedRentalsLabel;
    @FXML private Label systemStatsLabel;
    
    // Car Utilization Tab
    @FXML private TableView<ReportData> carReportTable;
    @FXML private TableColumn<ReportData, String> carInfoColumn;
    @FXML private TableColumn<ReportData, String> registrationColumn;
    @FXML private TableColumn<ReportData, Integer> timesRentedColumn;
    @FXML private TableColumn<ReportData, Double> carRevenueColumn;
    
    // Customer Activity Tab
    @FXML private TableView<ReportData> customerReportTable;
    @FXML private TableColumn<ReportData, String> customerNameColumn;
    @FXML private TableColumn<ReportData, String> customerPhoneColumn;
    @FXML private TableColumn<ReportData, Integer> customerRentalsColumn;
    @FXML private TableColumn<ReportData, Double> customerSpentColumn;
    
    private ReportsService reportsService;
    private ObservableList<ReportData> carReportData;
    private ObservableList<ReportData> customerReportData;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        reportsService = new ReportsService();
        carReportData = FXCollections.observableArrayList();
        customerReportData = FXCollections.observableArrayList();
        
        setupComboBoxes();
        setupTableColumns();
        setupEventHandlers();
        
        // Load current month/year by default
        loadDefaultValues();
    }
    
    private void setupComboBoxes() {
        // Populate months
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        monthComboBox.setItems(FXCollections.observableArrayList(months));
        
        // Populate years (current year and previous 5 years)
        int currentYear = LocalDate.now().getYear();
        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int i = currentYear - 5; i <= currentYear; i++) {
            years.add(i);
        }
        yearComboBox.setItems(years);
    }
    
    private void setupTableColumns() {
        // Car report columns
        carInfoColumn.setCellValueFactory(new PropertyValueFactory<>("carInfo"));
        registrationColumn.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
        timesRentedColumn.setCellValueFactory(new PropertyValueFactory<>("timesRented"));
        carRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("carRevenue"));
        
        // Customer report columns
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));
        customerRentalsColumn.setCellValueFactory(new PropertyValueFactory<>("customerRentals"));
        customerSpentColumn.setCellValueFactory(new PropertyValueFactory<>("customerSpent"));
        
        // Set data to tables
        carReportTable.setItems(carReportData);
        customerReportTable.setItems(customerReportData);
    }
    
    private void setupEventHandlers() {
        generateReportButton.setOnAction(event -> generateReport());
    }
    
    private void loadDefaultValues() {
        LocalDate now = LocalDate.now();
        monthComboBox.getSelectionModel().select(now.getMonthValue() - 1);
        yearComboBox.getSelectionModel().select(Integer.valueOf(now.getYear()));
    }
    
    @FXML
    private void generateReport() {
        try {
            // Check user permissions
            if (!SessionManager.getInstance().canViewReports()) {
                Alerts.showError(
                    "Access Denied",
                    "You do not have permission to view reports.\n\n" +
                    "Only Admins and Managers can access reports.\n" +
                    "Your current role:"+ SessionManager.getInstance().getCurrentUserRole().getDisplayName()
                );
                return;
            }
            
            String selectedMonth = monthComboBox.getSelectionModel().getSelectedItem();
            Integer selectedYear = yearComboBox.getSelectionModel().getSelectedItem();
            
            if (selectedMonth == null || selectedYear == null) {
                Alerts.showError("Validation Error", "Please select both month and year");
                return;
            }
            
            int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            
            // Generate reports
            ReportData monthlyReport = reportsService.generateMonthlyReport(month, selectedYear);
            var carReports = reportsService.getCarUtilizationReport(month, selectedYear);
            var customerReports = reportsService.getCustomerActivityReport(month, selectedYear);
            
            // Update UI with monthly report data
            updateMonthlyReportDisplay(monthlyReport);
            
            // Update tables
            carReportData.clear();
            carReportData.addAll(carReports);
            
            customerReportData.clear();
            customerReportData.addAll(customerReports);
            
            Alerts.showSuccess("Success", "Report generated successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showError("Error", "Failed to generate report: " + e.getMessage());
        }
    }
    
    private void updateMonthlyReportDisplay(ReportData report) {
        periodLabel.setText(report.getPeriod());
        totalRevenueLabel.setText(String.format("$%.2f", report.getTotalRevenue()));
        totalRentalsLabel.setText(String.valueOf(report.getTotalRentals()));
        activeRentalsLabel.setText(String.valueOf(report.getActiveRentals()));
        completedRentalsLabel.setText(String.valueOf(report.getCompletedRentals()));
        returnedRentalsLabel.setText(String.valueOf(report.getReturnedRentals()));
        
        String systemStats = String.format(
            "Total Cars: %d | Available: %d | Total Customers: %d | Total Employees: %d",
            report.getTotalCars(),
            report.getAvailableCars(),
            report.getTotalCustomers(),
            report.getTotalEmployees()
        );
        systemStatsLabel.setText(systemStats);
    }
    
    @FXML
    private void handleExportToCSV() {
        try {
            // Check user permissions
            if (!SessionManager.getInstance().canViewReports()) {
                Alerts.showError(
                    "Access Denied",
                    "You do not have permission to export reports.\n\n" +
                    "Only Admins and Managers can export reports.\n" +
                    "Your current role: " + SessionManager.getInstance().getCurrentUserRole().getDisplayName()
                );
                return;
            }
            
            String selectedMonth = monthComboBox.getSelectionModel().getSelectedItem();
            Integer selectedYear = yearComboBox.getSelectionModel().getSelectedItem();
            
            if (selectedMonth == null || selectedYear == null) {
                Alerts.showError("Validation Error", "Please select both month and year before exporting");
                return;
            }
            
            int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            
            // Show confirmation dialog to select report type
            ChoiceDialog<String> dialog = new ChoiceDialog<>("MONTHLY_REVENUE", 
                "MONTHLY_REVENUE", "CAR_UTILIZATION", "CUSTOMER_ACTIVITY");
            dialog.setTitle("Export Report");
            dialog.setHeaderText("Select Report Type to Export");
            dialog.setContentText("Choose report type:");
            
            dialog.showAndWait().ifPresent(reportType -> {
                try {
                    String filePath = reportsService.exportToCSV(reportType, month, selectedYear);
                    Alerts.showSuccess("Export Successful", 
                        "Report exported successfully!\nFile saved to: " + filePath +
                        "\n\nClick OK to open the file location.");
                    
                    // Ask user if they want to open the file location
                    if (Alerts.showConfirmation("Open File Location", 
                            "Would you like to open the folder containing the exported CSV file?")) {
                        try {
                            File file = new File(filePath);
                            Desktop.getDesktop().open(file.getParentFile());
                        } catch (Exception e) {
                            System.err.println("Could not open file location: " + e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Alerts.showError("Export Failed", "Failed to export report: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showError("Error", "Failed to export report: " + e.getMessage());
        }
    }
    
    @FXML
    private void handlePrintReport() {
        try {
            // Check user permissions
            if (!SessionManager.getInstance().canViewReports()) {
                Alerts.showError(
                    "Access Denied",
                    "You do not have permission to print reports.\n\n" +
                    "Only Admins and Managers can print reports.\n" +
                    "Your current role: " + SessionManager.getInstance().getCurrentUserRole().getDisplayName()
                );
                return;
            }
            
            String selectedMonth = monthComboBox.getSelectionModel().getSelectedItem();
            Integer selectedYear = yearComboBox.getSelectionModel().getSelectedItem();
            
            if (selectedMonth == null || selectedYear == null) {
                Alerts.showError("Validation Error", "Please select both month and year before printing");
                return;
            }
            
            int month = monthComboBox.getSelectionModel().getSelectedIndex() + 1;
            
            // Show confirmation dialog to select report type
            ChoiceDialog<String> dialog = new ChoiceDialog<>("MONTHLY_REVENUE", 
                "MONTHLY_REVENUE", "CAR_UTILIZATION", "CUSTOMER_ACTIVITY");
            dialog.setTitle("Print Report");
            dialog.setHeaderText("Select Report Type to Print");
            dialog.setContentText("Choose report type:");
            
            dialog.showAndWait().ifPresent(reportType -> {
                reportsService.printReport(reportType, month, selectedYear);
                Alerts.showInformation("Print Initiated", 
                    "Print job initiated for " + reportType.replace("_", " ") + 
                    " report for " + selectedMonth + " " + selectedYear + ".\n\n" +
                    "A print-friendly version has been saved to your Documents/CarRentalReports folder.\n" +
                    "In a production environment, this would send the report to your printer.");
                
                // Ask user if they want to open the print file
                if (Alerts.showConfirmation("Open Print File", 
                        "Would you like to open the print-friendly file that was generated?")) {
                    try {
                        // Try to open the most recent print file
                        String userHome = System.getProperty("user.home");
                        String reportsDirPath = userHome + File.separator + "Documents" + File.separator + "CarRentalReports";
                        File reportsDir = new File(reportsDirPath);
                        
                        if (reportsDir.exists()) {
                            // In a real implementation, you would track the specific file generated
                            // For now, we'll just open the folder
                            Desktop.getDesktop().open(reportsDir);
                        }
                    } catch (Exception e) {
                        System.err.println("Could not open print file: " + e.getMessage());
                    }
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showError("Error", "Failed to print report: " + e.getMessage());
        }
    }
}