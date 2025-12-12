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

public class AddCarController {
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField colorField;
    @FXML private TextField registrationField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> fuelTypeCombo;
    @FXML private TextField mileageField;
    @FXML private ComboBox<String> availabilityCombo;
    @FXML private Label messageLabel;
    
    private CarService carService;
    
    @FXML
    public void initialize() {
        carService = new CarService();
        
        fuelTypeCombo.getItems().addAll("Petrol", "Diesel", "Electric", "Hybrid");
        availabilityCombo.getItems().addAll("Available", "Rented", "Maintenance");
        availabilityCombo.setValue("Available");
    }
    
    @FXML
    private void handleAddCar() {
        if (!validateInput()) {
            return;
        }
        
        try {
            Car car = new Car();
            car.setBrand(brandField.getText().trim());
            car.setModel(modelField.getText().trim());
            car.setYear(Integer.parseInt(yearField.getText().trim()));
            car.setColor(colorField.getText().trim());
            car.setRegistrationNumber(registrationField.getText().trim());
            car.setPricePerDay(Double.parseDouble(priceField.getText().trim()));
            car.setFuelType(fuelTypeCombo.getValue());
            car.setMileage(Integer.parseInt(mileageField.getText().trim()));
            car.setAvailability(availabilityCombo.getValue());
            car.setStatus("Active");
            
            if (carService.addCar(car)) {
                Alerts.showSuccess("Success", "Car added successfully!");
                clearFields();
            } else {
                Alerts.showError("Error", "Failed to add car. Registration number may already exist.");
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter valid numbers for year, price, and mileage.");
        } catch (Exception e) {
            Alerts.showError("Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cars/CarMain.fxml"));
            Parent content = loader.load();
            
            StackPane parent = (StackPane) brandField.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean validateInput() {
        // Brand validation
        if (Validator.isEmpty(brandField.getText())) {
            Alerts.showError("Validation Error", "Brand is required");
            brandField.requestFocus();
            return false;
        }
        
        // Model validation
        if (Validator.isEmpty(modelField.getText())) {
            Alerts.showError("Validation Error", "Model is required");
            modelField.requestFocus();
            return false;
        }
        
        // Year validation
        if (Validator.isEmpty(yearField.getText())) {
            Alerts.showError("Validation Error", "Year is required");
            yearField.requestFocus();
            return false;
        }
        try {
            int year = Integer.parseInt(yearField.getText().trim());
            if (!Validator.isValidYear(year)) {
                Alerts.showError("Validation Error", "Year must be between 1900 and current year + 1");
                yearField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Validation Error", "Year must be a valid number");
            yearField.requestFocus();
            return false;
        }
        
        // Color validation
        if (Validator.isEmpty(colorField.getText())) {
            Alerts.showError("Validation Error", "Color is required");
            colorField.requestFocus();
            return false;
        }
        
        // Registration number validation
        if (Validator.isEmpty(registrationField.getText())) {
            Alerts.showError("Validation Error", "Registration number is required");
            registrationField.requestFocus();
            return false;
        }
        if (!Validator.isValidRegistrationNumber(registrationField.getText())) {
            Alerts.showError("Validation Error", "Registration number must be at least 3 characters");
            registrationField.requestFocus();
            return false;
        }
        
        // Price validation
        if (Validator.isEmpty(priceField.getText())) {
            Alerts.showError("Validation Error", "Price per day is required");
            priceField.requestFocus();
            return false;
        }
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (!Validator.isValidPrice(price)) {
                Alerts.showError("Validation Error", "Price must be greater than 0");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Validation Error", "Price must be a valid number");
            priceField.requestFocus();
            return false;
        }
        
        // Fuel type validation
        if (fuelTypeCombo.getValue() == null || fuelTypeCombo.getValue().isEmpty()) {
            Alerts.showError("Validation Error", "Fuel type is required");
            fuelTypeCombo.requestFocus();
            return false;
        }
        
        // Mileage validation
        if (Validator.isEmpty(mileageField.getText())) {
            Alerts.showError("Validation Error", "Mileage is required");
            mileageField.requestFocus();
            return false;
        }
        try {
            int mileage = Integer.parseInt(mileageField.getText().trim());
            if (mileage < 0) {
                Alerts.showError("Validation Error", "Mileage cannot be negative");
                mileageField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Validation Error", "Mileage must be a valid number");
            mileageField.requestFocus();
            return false;
        }
        
        // Availability validation
        if (availabilityCombo.getValue() == null || availabilityCombo.getValue().isEmpty()) {
            Alerts.showError("Validation Error", "Availability status is required");
            availabilityCombo.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        brandField.clear();
        modelField.clear();
        yearField.clear();
        colorField.clear();
        registrationField.clear();
        priceField.clear();
        mileageField.clear();
        fuelTypeCombo.setValue(null);
        availabilityCombo.setValue("Available");
    }
}



