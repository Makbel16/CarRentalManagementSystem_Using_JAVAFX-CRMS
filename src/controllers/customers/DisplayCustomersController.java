package controllers.customers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import models.Customer;
import services.CustomerService;
import controllers.utils.Alerts;
import controllers.utils.DateUtil;

public class DisplayCustomersController {
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> licenseColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    
    private CustomerService customerService;
    private ObservableList<Customer> customersList;
    
    @FXML
    public void initialize() {
        customerService = new CustomerService();
        customersList = FXCollections.observableArrayList();
        
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        licenseColumn.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        
        customersTable.setItems(customersList);
        
        // Enable/disable buttons based on selection
        customersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
        
        loadCustomers();
    }
    
    @FXML
    private void handleRefresh() {
        loadCustomers();
        Alerts.showSuccess("Success", "Customers list refreshed");
    }
    
    @FXML
    private void handleAddCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customers/AddCustomer.fxml"));
            Parent content = loader.load();
            
            StackPane parent = (StackPane) customersTable.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(content);
            }
        } catch (Exception e) {
            Alerts.showError("Error", "Failed to load Add Customer page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEditCustomer() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alerts.showWarning("Warning", "Please select a customer to edit");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customers/EditCustomer.fxml"));
            Parent content = loader.load();
            
            StackPane parent = (StackPane) customersTable.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(content);
            }
        } catch (Exception e) {
            Alerts.showError("Error", "Failed to load Edit Customer page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDeleteCustomer() {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            Alerts.showWarning("Warning", "Please select a customer to delete");
            return;
        }
        
        if (Alerts.showConfirmation("Confirm Delete", 
                "Are you sure you want to delete " + selectedCustomer.getFullName() + "?")) {
            if (customerService.deleteCustomer(selectedCustomer.getCustomerId())) {
                Alerts.showSuccess("Success", "Customer deleted successfully");
                loadCustomers();
            } else {
                Alerts.showError("Error", "Failed to delete customer");
            }
        }
    }

    @FXML
    private void handleExport() {
        // Implement export functionality here
        Alerts.showInformation("Export", "Export to CSV functionality will be implemented here");
    }
    
    private void loadCustomers() {
        customersList.clear();
        customersList.addAll(customerService.getAllCustomers());
    }
}