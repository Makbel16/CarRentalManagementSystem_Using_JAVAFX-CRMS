package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import controllers.utils.Alerts;

public class MainLayoutController {
    @FXML
    private StackPane contentPane;  // Make sure this matches your FXML
    
    @FXML
    public void initialize() {
        System.out.println("MainLayoutController initialized - Loading Dashboard...");
        loadDashboard();  // Automatically load dashboard on startup
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
    private void handleLogout() {
        try {
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
