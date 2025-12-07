package controllers.rent;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.Car;
import models.Customer;
import models.RentalRecord;
import services.CarService;
import services.CustomerService;
import services.RentService;
import controllers.utils.Alerts;
import controllers.utils.DateUtil;
import controllers.utils.Validator;
import java.time.LocalDate;

public class RentCarController {
    @FXML private TextField customerIdField;
    @FXML private Label customerNameLabel;
    @FXML private TextField carIdField;
    @FXML private Label carDetailsLabel;
    @FXML private DatePicker rentalDateField;
    @FXML private DatePicker returnDateField;
    @FXML private Label dailyRateLabel;
    @FXML private Label totalCostLabel;
    
    private CarService carService;
    private CustomerService customerService;
    private RentService rentService;
    private Car selectedCar;
    private Customer selectedCustomer;
    private int currentEmployeeId = 1; // Default employee ID - in real app, get from session
    
    @FXML
    public void initialize() {
        carService = new CarService();
        customerService = new CustomerService();
        rentService = new RentService();
        
        rentalDateField.setValue(LocalDate.now());
        returnDateField.setValue(LocalDate.now().plusDays(1));
        
        // Calculate total when dates change
        rentalDateField.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
        returnDateField.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
    }
    
    @FXML
    private void searchCustomer() {
        if (Validator.isEmpty(customerIdField.getText())) {
            Alerts.showError("Error", "Please enter a customer ID");
            return;
        }
        
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            selectedCustomer = customerService.getCustomerById(customerId);
            
            if (selectedCustomer != null) {
                customerNameLabel.setText(selectedCustomer.getFullName() + " - " + selectedCustomer.getPhone());
            } else {
                customerNameLabel.setText("Customer not found");
                selectedCustomer = null;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter a valid customer ID");
        }
    }
    
    @FXML
    private void searchCar() {
        if (Validator.isEmpty(carIdField.getText())) {
            Alerts.showError("Error", "Please enter a car ID");
            return;
        }
        
        try {
            int carId = Integer.parseInt(carIdField.getText().trim());
            selectedCar = carService.getCarById(carId);
            
            if (selectedCar != null) {
                if (!"Available".equals(selectedCar.getAvailability())) {
                    Alerts.showWarning("Warning", "This car is not available. Status: " + selectedCar.getAvailability());
                    carDetailsLabel.setText(selectedCar.getBrand() + " " + selectedCar.getModel() + 
                                          " - NOT AVAILABLE (" + selectedCar.getAvailability() + ")");
                    selectedCar = null;
                } else {
                    carDetailsLabel.setText(selectedCar.getBrand() + " " + selectedCar.getModel() + 
                                          " (" + selectedCar.getYear() + ") - $" + selectedCar.getPricePerDay() + "/day");
                    dailyRateLabel.setText("$" + String.format("%.2f", selectedCar.getPricePerDay()));
                    calculateTotal();
                }
            } else {
                carDetailsLabel.setText("Car not found");
                selectedCar = null;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter a valid car ID");
        }
    }
    
    @FXML
    private void calculateTotal() {
        if (selectedCar == null || rentalDateField.getValue() == null || returnDateField.getValue() == null) {
            totalCostLabel.setText("$0.00");
            return;
        }
        
        LocalDate rentalDate = rentalDateField.getValue();
        LocalDate returnDate = returnDateField.getValue();
        
        if (!Validator.isValidDateRange(rentalDate, returnDate)) {
            totalCostLabel.setText("Invalid date range");
            return;
        }
        
        long days = DateUtil.daysBetween(rentalDate, returnDate);
        if (days <= 0) {
            days = 1; // Minimum 1 day
        }
        
        double total = selectedCar.getPricePerDay() * days;
        totalCostLabel.setText("$" + String.format("%.2f", total));
    }
    
    @FXML
    private void handleRent() {
        if (selectedCustomer == null) {
            Alerts.showError("Error", "Please search and select a customer");
            return;
        }
        
        if (selectedCar == null) {
            Alerts.showError("Error", "Please search and select an available car");
            return;
        }
        
        if (rentalDateField.getValue() == null || returnDateField.getValue() == null) {
            Alerts.showError("Error", "Please select rental and return dates");
            return;
        }
        
        LocalDate rentalDate = rentalDateField.getValue();
        LocalDate returnDate = returnDateField.getValue();
        
        if (!Validator.isValidDateRange(rentalDate, returnDate)) {
            Alerts.showError("Error", "Return date must be after rental date");
            return;
        }
        
        long days = DateUtil.daysBetween(rentalDate, returnDate);
        if (days <= 0) {
            days = 1;
        }
        
        double totalAmount = selectedCar.getPricePerDay() * days;
        
        if (Alerts.showConfirmation("Confirm Rental", 
                "Rent " + selectedCar.getBrand() + " " + selectedCar.getModel() + 
                " to " + selectedCustomer.getFullName() + 
                " for " + days + " day(s)?\nTotal: $" + String.format("%.2f", totalAmount))) {
            
            if (rentService.rentCar(selectedCar.getCarId(), selectedCustomer.getCustomerId(), 
                                  currentEmployeeId, rentalDate, returnDate, totalAmount)) {
                Alerts.showSuccess("Success", "Car rented successfully!");
                clearFields();
            } else {
                Alerts.showError("Error", "Failed to process rental. Car may no longer be available.");
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        clearFields();
    }
    
    private void clearFields() {
        customerIdField.clear();
        customerNameLabel.setText("");
        carIdField.clear();
        carDetailsLabel.setText("");
        dailyRateLabel.setText("");
        totalCostLabel.setText("");
        rentalDateField.setValue(LocalDate.now());
        returnDateField.setValue(LocalDate.now().plusDays(1));
        selectedCar = null;
        selectedCustomer = null;
    }
}



