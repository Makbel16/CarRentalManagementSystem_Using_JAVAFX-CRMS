package controllers.employees;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Employee;
import services.EmployeeService;
import controllers.utils.Alerts;
import controllers.utils.Validator;
import java.time.LocalDate;

public class AddEmployeeController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private ComboBox<String> positionCombo;
    @FXML private TextField salaryField;
    @FXML private DatePicker hireDatePicker;
    @FXML private Label messageLabel;
    
    private final EmployeeService employeeService = new EmployeeService();
    
    @FXML
    public void initialize() {
        // Initialize position options
        positionCombo.getItems().addAll("Manager", "Staff", "Admin", "Accountant");
        
        // Set default hire date to today
        hireDatePicker.setValue(LocalDate.now());
        
        // Add input validation listeners
        addValidationListeners();
    }
    
    private void addValidationListeners() {
        // Real-time salary validation (numbers and decimal only)
        salaryField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                salaryField.setText(oldValue);
            }
        });
    }
    
    @FXML
    private void handleAddEmployee() {
        System.out.println("Add Employee button clicked!");
        
        // Validate all inputs
        if (!validateInput()) {
            return;
        }
        
        try {
            // Create employee object
            Employee employee = createEmployeeFromFields();
            
            // Check if email already exists
            if (employeeService.emailExists(employee.getEmail())) {
                Alerts.showError("Duplicate Email", 
                    "Email '" + employee.getEmail() + "' already exists. Please use a different email.");
                emailField.requestFocus();
                return;
            }
            
            // Add employee to database
            boolean success = employeeService.addEmployee(employee);
            
            if (success) {
                System.out.println("Employee added successfully! ID: " + employee.getEmployeeId());
                
                // Show success message
                Alerts.showSuccess("Success", "Employee added successfully!");
                
                // Clear fields for next entry
                clearFields();
            } else {
                Alerts.showError("Error", "Failed to add employee. Please try again.");
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Invalid Salary", "Please enter a valid salary amount (e.g., 5000.00)");
            salaryField.requestFocus();
        } catch (Exception e) {
            System.err.println("Error adding employee: " + e.getMessage());
            e.printStackTrace();
            Alerts.showError("Unexpected Error", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private Employee createEmployeeFromFields() {
        Employee employee = new Employee();
        employee.setFirstName(firstNameField.getText().trim());
        employee.setLastName(lastNameField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        employee.setPhone(phoneField.getText().trim());
        employee.setAddress(addressField.getText().trim());
        employee.setPosition(positionCombo.getValue());
        employee.setSalary(Double.parseDouble(salaryField.getText().trim()));
        employee.setHireDate(hireDatePicker.getValue());
        return employee;
    }
    
    private boolean validateInput() {
        StringBuilder errorMessage = new StringBuilder();
        
        // First Name validation
        if (Validator.isEmpty(firstNameField.getText())) {
            errorMessage.append("• First name is required\n");
        } else if (firstNameField.getText().trim().length() < 2) {
            errorMessage.append("• First name must be at least 2 characters\n");
        }
        
        // Last Name validation
        if (Validator.isEmpty(lastNameField.getText())) {
            errorMessage.append("• Last name is required\n");
        } else if (lastNameField.getText().trim().length() < 2) {
            errorMessage.append("• Last name must be at least 2 characters\n");
        }
        
        // Email validation
        if (Validator.isEmpty(emailField.getText())) {
            errorMessage.append("• Email is required\n");
        } else if (!Validator.isValidEmail(emailField.getText())) {
            errorMessage.append("• Invalid email format (e.g., user@example.com)\n");
        }
        
        // Phone validation (optional)
        String phone = phoneField.getText().trim();
        if (!phone.isEmpty() && !Validator.isValidPhone(phone)) {
            errorMessage.append("• Invalid phone number format\n");
        }
        
        // Position validation
        if (positionCombo.getValue() == null) {
            errorMessage.append("• Please select a position\n");
        }
        
        // Salary validation
        if (Validator.isEmpty(salaryField.getText())) {
            errorMessage.append("• Salary is required\n");
        } else {
            try {
                double salary = Double.parseDouble(salaryField.getText().trim());
                if (!Validator.isValidPrice(salary)) {
                    errorMessage.append("• Salary must be greater than 0\n");
                } else if (salary > 999999.99) {
                    errorMessage.append("• Salary is too high (max: 999,999.99)\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("• Please enter a valid salary (e.g., 5000.00)\n");
            }
        }
        
        // Hire Date validation
        if (hireDatePicker.getValue() == null) {
            errorMessage.append("• Hire date is required\n");
        } else if (hireDatePicker.getValue().isAfter(LocalDate.now())) {
            errorMessage.append("• Hire date cannot be in the future\n");
        }
        
        // If there are errors, show them
        if (errorMessage.length() > 0) {
            Alerts.showError("Validation Error", errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    // Renamed to match FXML (was handleClearFields)
    @FXML
    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        salaryField.clear();
        positionCombo.getSelectionModel().clearSelection();
        hireDatePicker.setValue(LocalDate.now());
        messageLabel.setText("");
        firstNameField.requestFocus();
    }
    
    @FXML
    private void handleBack() {
        try {
            // Load the main employee management screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employees/EmployeeMain.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Employee Management");
            
        } catch (Exception e) {
            System.err.println("Error navigating back: " + e.getMessage());
            e.printStackTrace();
            Alerts.showError("Navigation Error", "Failed to load employee management screen: " + e.getMessage());
        }
    }
}