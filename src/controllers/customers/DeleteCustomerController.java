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
import services.CustomerService;
import controllers.utils.Alerts;
import controllers.utils.Validator;

public class DeleteCustomerController {
    @FXML private TextField customerIdField;
    @FXML private Button deleteButton;
    @FXML private Label customerNameLabel;
    @FXML private Label customerContactLabel;
    @FXML private VBox customerDetailsBox;
    
    private CustomerService customerService;
    private Customer selectedCustomer;
    
    @FXML
    public void initialize() {
        customerService = new CustomerService();
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
        
        if (Alerts.showConfirmation("Confirm Delete", 
                "Are you sure you want to permanently delete this customer?\n\n" +
                selectedCustomer.getFullName() + " (ID: " + selectedCustomer.getCustomerId() + ")")) {
            
            if (customerService.deleteCustomer(selectedCustomer.getCustomerId())) {
                Alerts.showSuccess("Success", "Customer deleted successfully");
                clearFields();
            } else {
                Alerts.showError("Error", "Failed to delete customer");
            }
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
