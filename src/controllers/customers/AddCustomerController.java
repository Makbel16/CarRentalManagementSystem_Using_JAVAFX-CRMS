package controllers.customers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import models.Customer;
import services.CustomerService;
import dao.CustomerDAO;
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
    private CustomerDAO customerDAO;
    
    @FXML
    public void initialize() {
        customerService = new CustomerService();
        customerDAO = new CustomerDAO();
        
        // Set default phone prefix
        phoneField.setText("+251");
        
        // Position cursor after the prefix
        phoneField.positionCaret(4);
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
            customer.setLicenseNumber(licenseNumberField.getText().trim().toUpperCase());
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
        // First name validation
        if (Validator.isEmpty(firstNameField.getText())) {
            Alerts.showError("Validation Error", "First name is required");
            firstNameField.requestFocus();
            return false;
        }
        if (!Validator.isValidName(firstNameField.getText())) {
            Alerts.showError("Validation Error", "First name must contain only letters and spaces");
            firstNameField.requestFocus();
            return false;
        }
        
        // Last name validation
        if (Validator.isEmpty(lastNameField.getText())) {
            Alerts.showError("Validation Error", "Last name is required");
            lastNameField.requestFocus();
            return false;
        }
        if (!Validator.isValidName(lastNameField.getText())) {
            Alerts.showError("Validation Error", "Last name must contain only letters and spaces");
            lastNameField.requestFocus();
            return false;
        }
        
        // Email validation
        if (Validator.isEmpty(emailField.getText())) {
            Alerts.showError("Validation Error", "Email is required");
            emailField.requestFocus();
            return false;
        }
        if (!Validator.isValidEmail(emailField.getText())) {
            Alerts.showError("Validation Error", "Please enter a valid email address (e.g., name@example.com)");
            emailField.requestFocus();
            return false;
        }
        
        // Phone number validation (Ethiopian format)
        if (Validator.isEmpty(phoneField.getText())) {
            Alerts.showError("Validation Error", "Phone number is required");
            phoneField.requestFocus();
            return false;
        }
        if (!Validator.isValidEthiopianPhone(phoneField.getText())) {
            Alerts.showError("Validation Error", 
                "Please enter a valid Ethiopian phone number\n" +
                "Format: +251 followed by 9 digits\n" +
                "Examples: +251912345678 or +251 912345678");
            phoneField.requestFocus();
            return false;
        }
        
        // License number validation (Ethiopian format)
        if (Validator.isEmpty(licenseNumberField.getText())) {
            Alerts.showError("Validation Error", "License number is required");
            licenseNumberField.requestFocus();
            return false;
        }
        if (!Validator.isValidEthiopianLicense(licenseNumberField.getText())) {
            Alerts.showError("Validation Error", 
                "Please enter a valid Ethiopian driver's license\n" +
                "Format: 2 letters followed by 7 digits\n" +
                "Example: ET1234567");
            licenseNumberField.requestFocus();
            return false;
        }
        
        // Check for duplicate license number
        String licenseNumber = licenseNumberField.getText().trim().toUpperCase();
        if (customerDAO.licenseExists(licenseNumber)) {
            Alerts.showError("Validation Error", 
                "This license number is already registered to another customer.\n" +
                "Each customer must have a unique license number.");
            licenseNumberField.requestFocus();
            return false;
        }
        
        // Date of birth validation
        if (dateOfBirthPicker.getValue() == null) {
            Alerts.showError("Validation Error", "Date of birth is required");
            dateOfBirthPicker.requestFocus();
            return false;
        }
        
        // Address validation (optional but check if provided)
        if (!Validator.isEmpty(addressField.getText()) && addressField.getText().trim().length() < 5) {
            Alerts.showError("Validation Error", "Address must be at least 5 characters if provided");
            addressField.requestFocus();
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