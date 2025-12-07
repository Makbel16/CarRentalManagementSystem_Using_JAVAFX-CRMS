package controllers.customers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import models.Customer;
import services.CustomerService;
import controllers.utils.Alerts;
import controllers.utils.Validator;

public class EditCustomerController {
    @FXML private TextField searchField;
    @FXML private TextField customerIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private TextField licenseNumberField;
    @FXML private DatePicker dateOfBirthPicker;
    
    private CustomerService customerService;
    private Customer currentCustomer;
    
    @FXML
    public void initialize() {
        customerService = new CustomerService();
        customerIdField.setEditable(false);
    }
    
    @FXML
    private void handleSearch() {
        if (Validator.isEmpty(searchField.getText())) {
            Alerts.showError("Error", "Please enter a customer ID or name");
            return;
        }
        
        String searchTerm = searchField.getText().trim();
        
        // Try to find by ID first
        try {
            int customerId = Integer.parseInt(searchTerm);
            currentCustomer = customerService.getCustomerById(customerId);
        } catch (NumberFormatException e) {
            // If not a number, search by name
            currentCustomer = customerService.searchCustomers(searchTerm).stream()
                .findFirst()
                .orElse(null);
        }
        
        if (currentCustomer != null) {
            loadCustomerData(currentCustomer);
        } else {
            Alerts.showError("Error", "Customer not found");
            clearFields();
        }
    }
    
    @FXML
    private void handleUpdateCustomer() {
        if (currentCustomer == null) {
            Alerts.showWarning("Warning", "Please search for a customer first");
            return;
        }
        
        if (!validateInput()) {
            return;
        }
        
        try {
            currentCustomer.setFirstName(firstNameField.getText().trim());
            currentCustomer.setLastName(lastNameField.getText().trim());
            currentCustomer.setEmail(emailField.getText().trim());
            currentCustomer.setPhone(phoneField.getText().trim());
            currentCustomer.setAddress(addressField.getText().trim());
            currentCustomer.setLicenseNumber(licenseNumberField.getText().trim());
            currentCustomer.setDateOfBirth(dateOfBirthPicker.getValue());
            
            if (customerService.updateCustomer(currentCustomer)) {
                Alerts.showSuccess("Success", "Customer updated successfully");
                loadCustomerData(currentCustomer); // Refresh display
            } else {
                Alerts.showError("Error", "Failed to update customer");
            }
        } catch (Exception e) {
            Alerts.showError("Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customers/CustomerMain.fxml"));
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
    
    private void loadCustomerData(Customer customer) {
        customerIdField.setText(String.valueOf(customer.getCustomerId()));
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        emailField.setText(customer.getEmail() != null ? customer.getEmail() : "");
        phoneField.setText(customer.getPhone());
        addressField.setText(customer.getAddress() != null ? customer.getAddress() : "");
        licenseNumberField.setText(customer.getLicenseNumber());
        dateOfBirthPicker.setValue(customer.getDateOfBirth());
    }
    
    private boolean validateInput() {
        if (Validator.isEmpty(firstNameField.getText())) {
            Alerts.showError("Validation Error", "First name is required");
            return false;
        }
        if (Validator.isEmpty(lastNameField.getText())) {
            Alerts.showError("Validation Error", "Last name is required");
            return false;
        }
        if (Validator.isEmpty(phoneField.getText()) || !Validator.isValidPhone(phoneField.getText())) {
            Alerts.showError("Validation Error", "Valid phone number is required");
            return false;
        }
        if (!Validator.isEmpty(emailField.getText()) && !Validator.isValidEmail(emailField.getText())) {
            Alerts.showError("Validation Error", "Invalid email format");
            return false;
        }
        if (Validator.isEmpty(licenseNumberField.getText()) || 
            !Validator.isValidLicenseNumber(licenseNumberField.getText())) {
            Alerts.showError("Validation Error", "Valid license number is required");
            return false;
        }
        return true;
    }
    
    private void clearFields() {
        customerIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        licenseNumberField.clear();
        dateOfBirthPicker.setValue(null);
        currentCustomer = null;
    }
}
