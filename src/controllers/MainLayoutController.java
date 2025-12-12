package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import controllers.utils.SessionManager;
import controllers.utils.Alerts;

public class MainLayoutController {
    @FXML
    private StackPane contentPane;  // Make sure this matches your FXML
    
    @FXML
    private Button dashboardBtn;
    
    @FXML
    private Button carsBtn;
    
    @FXML
    private Button customersBtn;
    
    @FXML
    private Button employeesBtn;
    
    @FXML
    private Button rentCarBtn;
    
    @FXML
    private Button returnCarBtn;
    
    @FXML
    private Button reportsBtn;
    
    @FXML
    public void initialize() {
        System.out.println("MainLayoutController initialized - Loading Dashboard...");
        
        // Configure menu based on user role
        configureMenuByRole();
        
        loadDashboard();  // Automatically load dashboard on startup
    }
    
    /**
     * Configure menu visibility and access based on logged-in user's role
     */
    private void configureMenuByRole() {
        SessionManager session = SessionManager.getInstance();
        
        if (!session.isLoggedIn()) {
            System.out.println("WARNING: No user session found!");
            return;
        }
        
        System.out.println("Configuring menu for: " + session.toString());
        
        // Dashboard - Everyone can access
        dashboardBtn.setDisable(false);
        
        // Cars - Everyone can access
        carsBtn.setDisable(false);
        
        // Customers - Everyone can access
        customersBtn.setDisable(false);
        
        // Employees - Only ADMIN can access
        if (!session.canManageEmployees()) {
            employeesBtn.setDisable(true);
            employeesBtn.setVisible(false);
            employeesBtn.setManaged(false);
        }
        
        // Rent/Return - Everyone can access
        rentCarBtn.setDisable(false);
        returnCarBtn.setDisable(false);
        
        // Reports - Only managers and admins can access
        if (!session.canViewReports()) {
            reportsBtn.setDisable(true);
            reportsBtn.setVisible(false);
            reportsBtn.setManaged(false);
        }
    }
    @FXML
    private void loadDashboard() {
        loadContent("/fxml/Dashboard.fxml");
    }

    @FXML
    private void loadCars() {
        loadContent("/fxml/cars/CarMain.fxml");
    }

    @FXML
    private void loadCustomers() {
        loadContent("/fxml/customers/CustomerMain.fxml");
    }

    @FXML
    private void loadEmployees() {
        loadContent("/fxml/employees/EmployeeMain.fxml");
    }

    @FXML
    private void loadRentCar() {
        loadContent("/fxml/rent/RentCar.fxml");
    }

    @FXML
    private void loadReturnCar() {
        loadContent("/fxml/rent/ReturnCar.fxml");
    }
    
    @FXML
    private void loadReports() {
        loadContent("/fxml/reports/Reports.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            // Clear session
            SessionManager.getInstance().logout();
            System.out.println("User logged out successfully");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Car Rental System - Login");
            stage.setMaximized(false);
            stage.setWidth(500);
            stage.setHeight(400);
        } catch (IOException e) {
            Alerts.showError("Error", "Failed to logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);
        } catch (IOException e) {
            Alerts.showError("Error", "Failed to load page: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
