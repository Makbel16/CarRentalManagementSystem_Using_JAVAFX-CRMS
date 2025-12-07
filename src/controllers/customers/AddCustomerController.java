package controllers.customers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import models.Customer;
import services.CustomerService;
import controllers.utils.Alerts;
import controllers.utils.Validator;

public class AddCustomerController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private TextField licenseNumberField;
    @FXML private DatePicker dateOfBirthPicker;
    @FXML private Label messageLabel;
    
    private CustomerService customerService;
    
    @FXML
    public void initialize() {
        customerService = new CustomerService();
    }
    
    @FXML
    private void handleAddCustomer() {
        if (!validateInput()) {
            return;
        }
        
        try {
            Customer customer = new Customer();
            customer.setFirstName(firstNameField.getText().trim());
            customer.setLastName(lastNameField.getText().trim());
            customer.setEmail(emailField.getText().trim());
            customer.setPhone(phoneField.getText().trim());
            customer.setAddress(addressField.getText().trim());
            customer.setLicenseNumber(licenseNumberField.getText().trim());
            customer.setDateOfBirth(dateOfBirthPicker.getValue());
            
            if (customerService.addCustomer(customer)) {
                Alerts.showSuccess("Success", "Customer added successfully");
                clearFields();
            } else {
                Alerts.showError("Error", "Failed to add customer");
            }
        } catch (Exception e) {
            Alerts.showError("Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/customers/CustomerMain.fxml"));
            StackPane parent = (StackPane) firstNameField.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if (Validator.isEmpty(emailField.getText()) || !Validator.isValidEmail(emailField.getText())) {
            Alerts.showError("Validation Error", "Valid email is required");
            return false;
        }
        if (Validator.isEmpty(phoneField.getText())) {
            Alerts.showError("Validation Error", "Phone number is required");
            return false;
        }
        if (dateOfBirthPicker.getValue() == null) {
            Alerts.showError("Validation Error", "Date of birth is required");
            return false;
        }
        if (Validator.isEmpty(licenseNumberField.getText())) {
            Alerts.showError("Validation Error", "License number is required");
            return false;
        }
        return true;
    }
    
    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        licenseNumberField.clear();
        dateOfBirthPicker.setValue(null);
    }
}