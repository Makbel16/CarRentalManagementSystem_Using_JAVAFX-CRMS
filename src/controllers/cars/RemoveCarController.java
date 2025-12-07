package controllers.cars;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import models.Car;
import services.CarService;
import controllers.utils.Alerts;
import controllers.utils.Validator;

public class RemoveCarController {
    @FXML private TextField carIdField;
    @FXML private Button removeButton;
    @FXML private Label carDetailsText;
    @FXML private Label confirmationText;
    
    private CarService carService;
    private Car selectedCar;
    
    @FXML
    public void initialize() {
        try {
            carService = new CarService();
            removeButton.setDisable(true);
            // Set initial empty text to prevent NPE
            carDetailsText.setText("");
            confirmationText.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showError("Error", "Failed to initialize RemoveCarController: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearchCar() {
        try {
            if (Validator.isEmpty(carIdField.getText())) {
                Alerts.showError("Error", "Please enter a car ID");
                return;
            }
            
            int carId = Integer.parseInt(carIdField.getText().trim());
            selectedCar = carService.getCarById(carId);
            
            if (selectedCar != null) {
                carDetailsText.setText(
                    "Car ID: " + selectedCar.getCarId() + "\n" +
                    "Brand: " + selectedCar.getBrand() + "\n" +
                    "Model: " + selectedCar.getModel() + "\n" +
                    "Year: " + selectedCar.getYear() + "\n" +
                    "Registration: " + selectedCar.getRegistrationNumber() + "\n" +
                    "Price/Day: $" + selectedCar.getPricePerDay() + "\n" +
                    "Availability: " + selectedCar.getAvailability()
                );
                confirmationText.setText("Car found. Click Remove to delete.");
                removeButton.setDisable(false);
            } else {
                carDetailsText.setText("");
                confirmationText.setText("Car not found with ID: " + carId);
                removeButton.setDisable(true);
                selectedCar = null;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter a valid car ID number");
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showError("Error", "Failed to search for car: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRemoveCar() {
        try {
            if (selectedCar == null) {
                Alerts.showWarning("Warning", "Please search for a car first");
                return;
            }
            
            if (Alerts.showConfirmation(
                    "Confirm Delete", 
                    "Are you sure you want to permanently delete this car?\n\n" +
                    selectedCar.getBrand() + " " + selectedCar.getModel() + 
                    " (" + selectedCar.getRegistrationNumber() + ")")) {
                
                if (carService.deleteCar(selectedCar.getCarId())) {
                    Alerts.showSuccess("Success", "Car deleted successfully");
                    clearFields();
                } else {
                    Alerts.showError("Error", "Failed to delete car. It may be currently rented.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showError("Error", "Failed to delete car: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            // Use getResource with leading slash to ensure correct path resolution
            Parent content = FXMLLoader.load(getClass().getResource("/fxml/cars/CarMain.fxml"));
            StackPane parent = (StackPane) carIdField.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().setAll(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Alerts.showError("Error", "Failed to load main view: " + e.getMessage());
        }
    }
    
    private void clearFields() {
        carIdField.clear();
        carDetailsText.setText("");
        confirmationText.setText("");
        removeButton.setDisable(true);
        selectedCar = null;
    }
}