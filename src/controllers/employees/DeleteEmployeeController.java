package controllers.employees;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import models.Employee;
import services.EmployeeService;
import controllers.utils.Alerts;
import controllers.utils.Validator;

public class DeleteEmployeeController {
    @FXML private TextField employeeIdField;
    @FXML private Label employeeDetailsLabel;
    @FXML private Label confirmationLabel;
    @FXML private HBox actionButtons;
    @FXML private Button deleteButton;  // This now matches the FXML
    
    private EmployeeService employeeService;
    private Employee selectedEmployee;
    
    @FXML
    public void initialize() {
        employeeService = new EmployeeService();
        if (deleteButton != null) {
            deleteButton.setDisable(true);
        }
    }
    
    @FXML
    private void handleSearchEmployee() {
        if (Validator.isEmpty(employeeIdField.getText())) {
            Alerts.showError("Error", "Please enter an employee ID");
            return;
        }
        
        try {
            int employeeId = Integer.parseInt(employeeIdField.getText().trim());
            selectedEmployee = employeeService.getEmployeeById(employeeId);
            
            if (selectedEmployee != null) {
                employeeDetailsLabel.setText(
                    "Employee ID: " + selectedEmployee.getEmployeeId() + "\n" +
                    "Name: " + selectedEmployee.getFullName() + "\n" +
                    "Email: " + (selectedEmployee.getEmail() != null ? selectedEmployee.getEmail() : "N/A") + "\n" +
                    "Phone: " + (selectedEmployee.getPhone() != null ? selectedEmployee.getPhone() : "N/A") + "\n" +
                    "Position: " + selectedEmployee.getPosition() + "\n" +
                    "Salary: $" + selectedEmployee.getSalary()
                );
                confirmationLabel.setText("Employee found. Click Delete to remove.");
                actionButtons.setVisible(true);
                if (deleteButton != null) {
                    deleteButton.setDisable(false);
                }
            } else {
                employeeDetailsLabel.setText("");
                confirmationLabel.setText("Employee not found with ID: " + employeeId);
                actionButtons.setVisible(false);
                selectedEmployee = null;
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter a valid employee ID number");
        }
    }
    
    @FXML
    private void handleDeleteEmployee() {
        if (selectedEmployee == null) {
            Alerts.showWarning("Warning", "Please search for an employee first");
            return;
        }
        
        if (Alerts.showConfirmation("Confirm Delete", 
                "Are you sure you want to permanently delete this employee?\n\n" +
                selectedEmployee.getFullName() + " (ID: " + selectedEmployee.getEmployeeId() + ")")) {
            
            if (employeeService.deleteEmployee(selectedEmployee.getEmployeeId())) {
                Alerts.showSuccess("Success", "Employee deleted successfully");
                clearFields();
            } else {
                Alerts.showError("Error", "Failed to delete employee");
            }
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            Parent content = FXMLLoader.load(getClass().getResource("/fxml/employees/EmployeeMain.fxml"));
            StackPane parent = (StackPane) employeeIdField.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().setAll(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void clearFields() {
        employeeIdField.clear();
        employeeDetailsLabel.setText("");
        confirmationLabel.setText("");
        actionButtons.setVisible(false);
        if (deleteButton != null) {
            deleteButton.setDisable(true);
        }
        selectedEmployee = null;
    }
}