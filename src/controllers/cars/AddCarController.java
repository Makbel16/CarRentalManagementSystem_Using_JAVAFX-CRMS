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
        if (Validator.isEmpty(brandField.getText())) {
            Alerts.showError("Validation Error", "Brand is required");
            return false;
        }
        if (Validator.isEmpty(modelField.getText())) {
            Alerts.showError("Validation Error", "Model is required");
            return false;
        }
        if (Validator.isEmpty(yearField.getText())) {
            Alerts.showError("Validation Error", "Year is required");
            return false;
        }
        try {
            int year = Integer.parseInt(yearField.getText().trim());
            if (!Validator.isValidYear(year)) {
                Alerts.showError("Validation Error", "Invalid year");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Validation Error", "Year must be a number");
            return false;
        }
        if (Validator.isEmpty(registrationField.getText()) || 
            !Validator.isValidRegistrationNumber(registrationField.getText())) {
            Alerts.showError("Validation Error", "Valid registration number is required");
            return false;
        }
        if (Validator.isEmpty(priceField.getText())) {
            Alerts.showError("Validation Error", "Price is required");
            return false;
        }
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            if (!Validator.isValidPrice(price)) {
                Alerts.showError("Validation Error", "Price must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Validation Error", "Price must be a number");
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



