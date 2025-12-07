package controllers.customers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Customer;
import services.CustomerService;
import controllers.utils.Alerts;

public class SearchCustomerController {
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private TextField searchField;
    @FXML private TableView<Customer> resultsTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> licenseColumn;
    
    private CustomerService customerService;
    private ObservableList<Customer> searchResults;
    
    @FXML
    public void initialize() {
        customerService = new CustomerService();
        searchResults = FXCollections.observableArrayList();
        
        // Set up search type combo
        searchTypeCombo.getItems().addAll("All Fields", "Name", "Email", "Phone", "License Number");
        searchTypeCombo.setValue("All Fields");
        
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(cellData -> {
            Customer c = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(c.getFullName());
        });
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        licenseColumn.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        
        resultsTable.setItems(searchResults);
        
        // Load all customers initially
        loadAllCustomers();
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadAllCustomers();
            return;
        }
        
        String searchType = searchTypeCombo.getValue();
        searchResults.clear();
        
        if ("All Fields".equals(searchType)) {
            searchResults.addAll(customerService.searchCustomers(searchTerm));
        } else {
            customerService.getAllCustomers().stream()
                .filter(customer -> matchesSearch(customer, searchTerm, searchType))
                .forEach(searchResults::add);
        }
        
        if (searchResults.isEmpty()) {
            Alerts.showWarning("No Results", "No customers found matching: " + searchTerm);
        } else {
            Alerts.showSuccess("Search Complete", "Found " + searchResults.size() + " customer(s)");
        }
    }
    
    private boolean matchesSearch(Customer customer, String searchTerm, String searchType) {
        String term = searchTerm.toLowerCase();
        
        switch (searchType) {
            case "Name":
                return customer.getFullName().toLowerCase().contains(term);
            case "Email":
                return customer.getEmail() != null && customer.getEmail().toLowerCase().contains(term);
            case "Phone":
                return customer.getPhone() != null && customer.getPhone().contains(term);
            case "License Number":
                return customer.getLicenseNumber() != null && 
                       customer.getLicenseNumber().toLowerCase().contains(term);
            default:
                return false;
        }
    }
    
    private void loadAllCustomers() {
        searchResults.clear();
        searchResults.addAll(customerService.getAllCustomers());
    }
    
    @FXML
    private void handleClear() {
        searchField.clear();
        searchTypeCombo.setValue("All Fields");
        loadAllCustomers();
    }
}