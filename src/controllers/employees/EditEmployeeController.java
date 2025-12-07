package controllers.employees;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import models.Employee;
import services.EmployeeService;
import controllers.utils.Alerts;
import controllers.utils.Validator;

public class EditEmployeeController {
    @FXML private TextField searchField;
    @FXML private TextField employeeIdField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextArea addressField;
    @FXML private ComboBox<String> positionCombo;
    @FXML private TextField salaryField;
    @FXML private DatePicker hireDatePicker;
    @FXML private ComboBox<String> statusCombo;
    
    private EmployeeService employeeService;
    private Employee currentEmployee;
    
    @FXML
    public void initialize() {
        employeeService = new EmployeeService();
        employeeIdField.setEditable(false);
        positionCombo.getItems().addAll("Manager", "Staff", "Admin", "Supervisor");
        statusCombo.getItems().addAll("Active", "Inactive");
    }
    
    @FXML
    private void handleSearch() {
        if (Validator.isEmpty(searchField.getText())) {
            Alerts.showError("Error", "Please enter an employee ID or name");
            return;
        }
        
        String searchTerm = searchField.getText().trim();
        
        try {
            int employeeId = Integer.parseInt(searchTerm);
            currentEmployee = employeeService.getEmployeeById(employeeId);
        } catch (NumberFormatException e) {
            currentEmployee = employeeService.searchEmployees(searchTerm).stream()
                .findFirst()
                .orElse(null);
        }
        
        if (currentEmployee != null) {
            loadEmployeeData(currentEmployee);
        } else {
            Alerts.showError("Error", "Employee not found");
            clearFields();
        }
    }
    
    @FXML
    private void handleUpdate() {
        if (currentEmployee == null) {
            Alerts.showWarning("Warning", "Please search for an employee first");
            return;
        }
        
        if (!validateInput()) {
            return;
        }
        
        try {
            currentEmployee.setFirstName(firstNameField.getText().trim());
            currentEmployee.setLastName(lastNameField.getText().trim());
            currentEmployee.setEmail(emailField.getText().trim());
            currentEmployee.setPhone(phoneField.getText().trim());
            currentEmployee.setAddress(addressField.getText().trim());
            currentEmployee.setPosition(positionCombo.getValue());
            currentEmployee.setSalary(Double.parseDouble(salaryField.getText().trim()));
            currentEmployee.setHireDate(hireDatePicker.getValue());
            currentEmployee.setStatus(statusCombo.getValue());
            
            if (employeeService.updateEmployee(currentEmployee)) {
                Alerts.showSuccess("Success", "Employee updated successfully");
                loadEmployeeData(currentEmployee);
            } else {
                Alerts.showError("Error", "Failed to update employee");
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter a valid salary amount");
        } catch (Exception e) {
            Alerts.showError("Error", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/employees/EmployeeMain.fxml"));
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
    
    private void loadEmployeeData(Employee employee) {
        employeeIdField.setText(String.valueOf(employee.getEmployeeId()));
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        phoneField.setText(employee.getPhone());
        addressField.setText(employee.getAddress());
        positionCombo.setValue(employee.getPosition());
        salaryField.setText(String.valueOf(employee.getSalary()));
        hireDatePicker.setValue(employee.getHireDate());
        statusCombo.setValue(employee.getStatus());
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
        if (positionCombo.getValue() == null) {
            Alerts.showError("Validation Error", "Position is required");
            return false;
        }
        if (Validator.isEmpty(salaryField.getText())) {
            Alerts.showError("Validation Error", "Salary is required");
            return false;
        }
        try {
            double salary = Double.parseDouble(salaryField.getText().trim());
            if (salary < 0) {
                Alerts.showError("Validation Error", "Salary must be positive");
                return false;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Validation Error", "Salary must be a number");
            return false;
        }
        return true;
    }
    
    private void clearFields() {
        employeeIdField.clear();
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        positionCombo.setValue(null);
        salaryField.clear();
        hireDatePicker.setValue(null);
        statusCombo.setValue(null);
        currentEmployee = null;
    }
}

