package controllers.cars;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import models.Car;
import models.Customer;
import models.RentalRecord;
import services.CarService;
import services.RentService;
import services.CustomerService;
import controllers.utils.SessionManager;
import controllers.utils.Alerts;
import controllers.utils.Validator;
import java.util.List;

public class RemoveCarController {
    @FXML private TextField carIdField;
    @FXML private Button removeButton;
    @FXML private Label carDetailsText;
    @FXML private Label confirmationText;
    
    private CarService carService;
    private RentService rentService;
    private CustomerService customerService;
    private Car selectedCar;
    
    @FXML
    public void initialize() {
        try {
            carService = new CarService();
            rentService = new RentService();
            customerService = new CustomerService();
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
            
            // Check user permission
            if (!SessionManager.getInstance().canDelete()) {
                Alerts.showError(
                    "Access Denied",
                    "You do not have permission to delete cars.\n\n" +
                    "Only Admins and Managers can delete records.\n" +
                    "Your current role: " + SessionManager.getInstance().getCurrentUserRole().getDisplayName()
                );
                return;
            }
            
            // Check if the car is currently rented
            List<RentalRecord> activeRentals = rentService.getActiveRentalRecords();
            RentalRecord activeRental = null;
            for (RentalRecord rental : activeRentals) {
                if (rental.getCarId() == selectedCar.getCarId()) {
                    activeRental = rental;
                    break;
                }
            }
            
            if (activeRental != null) {
                // Get customer name
                Customer customer = customerService.getCustomerById(activeRental.getCustomerId());
                String customerName = customer != null ? customer.getFullName() : "Unknown Customer";
                
                Alerts.showError(
                    "Cannot Delete Car",
                    "This car is currently rented to \"" + customerName + "\".\n\n" +
                    "Please process the return before deleting this car.\n\n" +
                    "Rental ID: " + activeRental.getRentalId() + "\n" +
                    "Rental Date: " + activeRental.getRentalDate() + "\n" +
                    "Expected Return: " + activeRental.getReturnDate()
                );
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
                    Alerts.showError("Error", "Failed to delete car. Please try again.");
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