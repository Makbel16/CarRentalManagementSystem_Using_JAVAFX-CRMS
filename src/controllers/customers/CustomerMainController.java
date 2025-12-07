package controllers.customers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class CustomerMainController {
    
    @FXML
    private StackPane contentPane;
    
    @FXML
    private void initialize() {
        // Initialization code if needed
        // No stats labels to initialize
    }
    
    @FXML
    private void loadAddCustomer() {
        loadFXML("/fxml/customers/AddCustomer.fxml");
    }
    
    @FXML
    private void loadDeleteCustomer() {
        loadFXML("/fxml/customers/DeleteCustomer.fxml");
    }
    
    @FXML
    private void loadEditCustomer() {
        loadFXML("/fxml/customers/EditCustomer.fxml");
    }
    
    @FXML
    private void loadSearchCustomer() {
        loadFXML("/fxml/customers/SearchCustomer.fxml");
    }
    
    @FXML
    private void loadDisplayCustomers() {
        loadFXML("/fxml/customers/DisplayCustomers.fxml");
    }
    
    private void loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + fxmlPath);
        }
    }
    
    // Remove or comment out the refreshCustomerStats method if it exists
    // @FXML
    // private void refreshCustomerStats() {
    //     // This method is no longer needed
    // }
    
    // Remove or comment out the handleExportData method if it exists
    // @FXML
    // private void handleExportData() {
    //     // This method is no longer needed
    // }
}