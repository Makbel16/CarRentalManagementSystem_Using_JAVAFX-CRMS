package controllers.employees;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Employee;
import services.EmployeeService;
import java.util.List;

public class DisplayEmployeesController {
    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, Integer> idColumn;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> emailColumn;
    @FXML private TableColumn<Employee, String> phoneColumn;
    @FXML private TableColumn<Employee, String> positionColumn;
    @FXML private TableColumn<Employee, Double> salaryColumn;
    @FXML private TableColumn<Employee, String> hireDateColumn;
    @FXML private TableColumn<Employee, String> statusColumn;
    
    private final EmployeeService employeeService = new EmployeeService();
    
    @FXML
    public void initialize() {
        setupTableColumns();
        loadEmployees();
    }
    
    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));
        hireDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getHireDate() != null ? 
                cellData.getValue().getHireDate().toString() : ""));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    private void loadEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        employeesTable.getItems().setAll(employees);
    }
    
    @FXML
    private void handleRefresh() {
        loadEmployees();
    }
    
    @FXML
    private void handleAddEmployee() {
        try {
            // This will be handled by the parent controller
            // The event will bubble up to the EmployeeMainController
            throw new UnsupportedOperationException("This should be handled by the parent controller");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}