package controllers.employees;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;

public class EmployeeMainController {
    @FXML private StackPane contentPane;
    
    @FXML
    public void initialize() {
        // Load default content
        loadContent("/fxml/employees/DisplayEmployees.fxml");
    }
    
    @FXML
    private void loadAddEmployee() {
        loadContent("/fxml/employees/AddEmployee.fxml");
    }
    
    @FXML
    private void loadDeleteEmployee() {
        loadContent("/fxml/employees/DeleteEmployee.fxml");
    }
    
    @FXML
    private void loadEditEmployee() {
        loadContent("/fxml/employees/EditEmployee.fxml");
    }
    
    @FXML
    private void loadSearchEmployee() {
        loadContent("/fxml/employees/SearchEmployee.fxml");
    }
    
    @FXML
    private void loadDisplayEmployees() {
        loadContent("/fxml/employees/DisplayEmployees.fxml");
    }
    
    private void loadContent(String fxmlPath) {
        try {
            System.out.println("Loading FXML from: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            
            // If the controller is DisplayEmployeesController, set up the parent reference
            if (loader.getController() instanceof DisplayEmployeesController) {
                DisplayEmployeesController controller = loader.getController();
                // You can pass a reference to this controller if needed
            }
            
            if (contentPane != null) {
                contentPane.getChildren().setAll(content);
            }
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + fxmlPath);
            e.printStackTrace();
            showError("Loading Error", "Failed to load: " + fxmlPath + "\n" + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}