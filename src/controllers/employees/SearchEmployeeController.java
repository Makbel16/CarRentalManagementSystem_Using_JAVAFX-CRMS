package controllers.employees;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Employee;
import services.EmployeeService;
import controllers.utils.Alerts;

public class SearchEmployeeController {
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private TextField searchField;
    @FXML private TableView<Employee> resultsTable;
    @FXML private TableColumn<Employee, Integer> idColumn;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> emailColumn;
    @FXML private TableColumn<Employee, String> positionColumn;
    @FXML private TableColumn<Employee, String> statusColumn;
    
    private EmployeeService employeeService;
    private ObservableList<Employee> searchResults;
    
    @FXML
    public void initialize() {
        employeeService = new EmployeeService();
        searchResults = FXCollections.observableArrayList();
        
        // Set up search type combo
        searchTypeCombo.getItems().addAll("All Fields", "Name", "Email", "Position");
        searchTypeCombo.setValue("All Fields");
        
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        nameColumn.setCellValueFactory(cellData -> {
            Employee e = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(e.getFullName());
        });
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        resultsTable.setItems(searchResults);
        
        // Load all employees initially
        loadAllEmployees();
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadAllEmployees();
            return;
        }
        
        String searchType = searchTypeCombo.getValue();
        searchResults.clear();
        
        if ("All Fields".equals(searchType)) {
            // Search in all fields
            searchResults.addAll(employeeService.searchEmployees(searchTerm));
        } else {
            // Search in specific field
            employeeService.getAllEmployees().stream()
                .filter(employee -> matchesSearch(employee, searchTerm, searchType))
                .forEach(searchResults::add);
        }
        
        if (searchResults.isEmpty()) {
            Alerts.showWarning("No Results", "No employees found matching: " + searchTerm);
        } else {
            Alerts.showSuccess("Search Complete", "Found " + searchResults.size() + " employee(s)");
        }
    }
    
    private boolean matchesSearch(Employee employee, String searchTerm, String searchType) {
        String term = searchTerm.toLowerCase();
        
        switch (searchType) {
            case "Name":
                return employee.getFullName().toLowerCase().contains(term);
            case "Email":
                return employee.getEmail() != null && employee.getEmail().toLowerCase().contains(term);
            case "Position":
                return employee.getPosition() != null && employee.getPosition().toLowerCase().contains(term);
            default:
                return false;
        }
    }
    
    private void loadAllEmployees() {
        searchResults.clear();
        searchResults.addAll(employeeService.getAllEmployees());
    }
}
