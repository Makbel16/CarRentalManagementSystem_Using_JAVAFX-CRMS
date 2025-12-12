package controllers.customers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import models.Customer;
import models.Car;
import models.RentalRecord;
import services.CustomerService;
import services.RentService;
import services.CarService;
import controllers.utils.SessionManager;
import controllers.utils.Alerts;
import controllers.utils.Validator;
import java.util.List;

public class DeleteCustomerController {
    @FXML private TextField customerIdField;
    @FXML private Button deleteButton;
    @FXML private Label customerNameLabel;
    @FXML private Label customerContactLabel;
    @FXML private VBox customerDetailsBox;
    
    private CustomerService customerService;
    private RentService rentService;
    private CarService carService;
    private Customer selectedCustomer;
    
    @FXML
    public void initialize() {
        customerService = new CustomerService();
        rentService = new RentService();
        carService = new CarService();
        deleteButton.setDisable(true);
    }
    
    @FXML
    private void handleSearch() {
        if (Validator.isEmpty(customerIdField.getText())) {
            Alerts.showError("Error", "Please enter a customer ID");
            return;
        }
        
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            selectedCustomer = customerService.getCustomerById(customerId);
            
            if (selectedCustomer != null) {
                customerNameLabel.setText("Name: " + selectedCustomer.getFullName());
                customerContactLabel.setText("Contact: " + selectedCustomer.getPhone() + " | " + 
                    (selectedCustomer.getEmail() != null ? selectedCustomer.getEmail() : ""));
                customerDetailsBox.setVisible(true);
                deleteButton.setDisable(false);
            } else {
                customerNameLabel.setText("");
                customerContactLabel.setText("");
                customerDetailsBox.setVisible(false);
                Alerts.showError("Not Found", "Customer not found with ID: " + customerId);
                deleteButton.setDisable(true);
                selectedCustomer = null;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter a valid customer ID number");
        }
    }
    
    @FXML
    private void handleDelete() {
        if (selectedCustomer == null) {
            Alerts.showWarning("Warning", "Please search for a customer first");
            return;
        }
        
        // Check user permission
        if (!SessionManager.getInstance().canDelete()) {
            Alerts.showError(
                "Access Denied",
                "You do not have permission to delete customers.\n\n" +
                "Only Admins and Managers can delete records.\n" +
                "Your current role: " + SessionManager.getInstance().getCurrentUserRole().getDisplayName()
            );
            return;
        }
        
        // Check if the customer has active rentals
        List<RentalRecord> activeRentals = rentService.getActiveRentalRecords();
        RentalRecord activeRental = null;
        for (RentalRecord rental : activeRentals) {
            if (rental.getCustomerId() == selectedCustomer.getCustomerId()) {
                activeRental = rental;
                break;
            }
        }
        
        if (activeRental != null) {
            // Get car details
            Car car = carService.getCarById(activeRental.getCarId());
            String carDetails = car != null ? 
                car.getBrand() + " " + car.getModel() + " (" + car.getRegistrationNumber() + ")" : 
                "Car ID: " + activeRental.getCarId();
            
            // Show warning with rental details
            if (Alerts.showConfirmation(
                "Customer Has Active Rental",
                "WARNING: This customer currently has an active rental!\n\n" +
                "Customer: " + selectedCustomer.getFullName() + "\n" +
                "Rented Car: " + carDetails + "\n" +
                "Rental ID: " + activeRental.getRentalId() + "\n" +
                "Rental Date: " + activeRental.getRentalDate() + "\n" +
                "Expected Return: " + activeRental.getReturnDate() + "\n\n" +
                "Are you sure you want to delete this customer?\n" +
                "Note: This will NOT cancel the rental.")) {
                
                performDelete();
            }
        } else {
            // No active rental, normal confirmation
            if (Alerts.showConfirmation("Confirm Delete", 
                    "Are you sure you want to permanently delete this customer?\n\n" +
                    selectedCustomer.getFullName() + " (ID: " + selectedCustomer.getCustomerId() + ")")) {
                
                performDelete();
            }
        }
    }
    
    private void performDelete() {
        if (customerService.deleteCustomer(selectedCustomer.getCustomerId())) {
            Alerts.showSuccess("Success", "Customer deleted successfully");
            clearFields();
        } else {
            Alerts.showError("Error", "Failed to delete customer");
        }
    }
    
    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customers/CustomerMain.fxml"));
            Parent content = loader.load();
            
            StackPane parent = (StackPane) customerIdField.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void clearFields() {
        customerIdField.clear();
        customerNameLabel.setText("");
        customerContactLabel.setText("");
        customerDetailsBox.setVisible(false);
        deleteButton.setDisable(true);
        selectedCustomer = null;
    }
}
