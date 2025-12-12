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

public class EditCarController {
    @FXML private TextField carIdField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField colorField;
    @FXML private TextField registrationField;
    @FXML private TextField priceField;
    @FXML private TextField mileageField;
    @FXML private ComboBox<String> fuelTypeCombo;
    @FXML private ComboBox<String> availabilityCombo;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    
    private CarService carService;
    private Car currentCar;
    
    @FXML
    public void initialize() {
        carService = new CarService();
        
        fuelTypeCombo.getItems().addAll("Petrol", "Diesel", "Electric", "Hybrid");
        availabilityCombo.getItems().addAll("Available", "Rented", "Maintenance");
        
        // Make carIdField non-editable after search
        carIdField.setEditable(false);
    }
    
    @FXML
    private void handleSearch() {
        if (Validator.isEmpty(searchField.getText())) {
            Alerts.showError("Error", "Please enter a car ID or registration number");
            return;
        }
        
        String searchTerm = searchField.getText().trim();
        
        // Try to find by ID first
        try {
            int carId = Integer.parseInt(searchTerm);
            currentCar = carService.getCarById(carId);
        } catch (NumberFormatException e) {
            // If not a number, search by registration
            currentCar = carService.searchCars(searchTerm).stream()
                .filter(c -> c.getRegistrationNumber().equalsIgnoreCase(searchTerm))
                .findFirst()
                .orElse(null);
        }
        
        if (currentCar != null) {
            loadCarData(currentCar);
        } else {
            Alerts.showError("Error", "Car not found");
            clearFields();
        }
    }
    
    @FXML
    private void handleUpdateCar() {
        if (currentCar == null) {
            Alerts.showWarning("Warning", "Please search for a car first");
            return;
        }
        
        if (!validateInput()) {
            return;
        }
        
        try {
            currentCar.setBrand(brandField.getText().trim());
            currentCar.setModel(modelField.getText().trim());
            currentCar.setYear(Integer.parseInt(yearField.getText().trim()));
            currentCar.setColor(colorField.getText().trim());
            currentCar.setRegistrationNumber(registrationField.getText().trim());
            currentCar.setPricePerDay(Double.parseDouble(priceField.getText().trim()));
            currentCar.setFuelType(fuelTypeCombo.getValue());
            currentCar.setMileage(Integer.parseInt(mileageField.getText().trim()));
            currentCar.setAvailability(availabilityCombo.getValue());
            
            if (carService.updateCar(currentCar)) {
                Alerts.showSuccess("Success", "Car updated successfully");
                loadCarData(currentCar); // Refresh display
            } else {
                Alerts.showError("Error", "Failed to update car");
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter valid numbers for year, price, and mileage");
        } catch (Exception e) {
            Alerts.showError("Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cars/CarMain.fxml"));
            Parent content = loader.load();
            
            StackPane parent = (StackPane) searchField.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadCarData(Car car) {
        carIdField.setText(String.valueOf(car.getCarId()));
        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        yearField.setText(String.valueOf(car.getYear()));
        colorField.setText(car.getColor());
        registrationField.setText(car.getRegistrationNumber());
        priceField.setText(String.valueOf(car.getPricePerDay()));
        mileageField.setText(String.valueOf(car.getMileage()));
        fuelTypeCombo.setValue(car.getFuelType());
        availabilityCombo.setValue(car.getAvailability());
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
        carIdField.clear();
        brandField.clear();
        modelField.clear();
        yearField.clear();
        colorField.clear();
        registrationField.clear();
        priceField.clear();
        mileageField.clear();
        fuelTypeCombo.setValue(null);
        availabilityCombo.setValue(null);
        currentCar = null;
    }
}

