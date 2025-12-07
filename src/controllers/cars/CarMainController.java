package controllers.cars;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class CarMainController {
    @FXML 
    private StackPane contentPane;
    
    @FXML
    public void initialize() {
        // Load default view
        handleViewAllCars();
    }
    
    @FXML
    private void handleAddCar() {
        loadContent("/fxml/cars/AddCar.fxml");
    }
    
    @FXML
    private void handleDeleteCar() {
        loadContent("/fxml/cars/RemoveCar.fxml");
    }
    
    @FXML
    private void handleEditCar() {
        loadContent("/fxml/cars/EditCar.fxml");
    }
    
    @FXML
    private void handleSearchCar() {
        loadContent("/fxml/cars/SearchCar.fxml");
    }
    
    @FXML
    private void handleViewAllCars() {
        loadContent("/fxml/cars/DisplayCars.fxml");
    }
    
    private void loadContent(String fxmlPath) {
        try {
            // Clear existing content
            contentPane.getChildren().clear();
            
            // Load the new content
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            
            // Add the new content
            contentPane.getChildren().add(content);
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading " + fxmlPath + ": " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}