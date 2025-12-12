package controllers.customers;

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
    private CustomerDAO customerDAO;
    private Customer currentCustomer;
    
    @FXML
    public void initialize() {
        customerService = new CustomerService();
        customerDAO = new CustomerDAO();
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
            currentCustomer.setLicenseNumber(licenseNumberField.getText().trim().toUpperCase());
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
        
        // Check for duplicate license number (excluding current customer)
        String licenseNumber = licenseNumberField.getText().trim().toUpperCase();
        if (currentCustomer != null && customerDAO.licenseExists(licenseNumber, currentCustomer.getCustomerId())) {
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
        customerIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.setText("+251"); // Reset to default prefix
        phoneField.positionCaret(4);
        addressField.clear();
        licenseNumberField.clear();
        dateOfBirthPicker.setValue(null);
        currentCustomer = null;
    }
}
