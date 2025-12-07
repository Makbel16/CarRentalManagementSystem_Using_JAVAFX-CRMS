package controllers.cars;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Car;
import services.CarService;
import controllers.utils.Alerts;

public class SearchCarController {
    @FXML private ComboBox<String> searchTypeCombo;
    @FXML private TextField searchField;
    @FXML private TableView<Car> resultsTable;
    @FXML private TableColumn<Car, Integer> idColumn;
    @FXML private TableColumn<Car, String> brandColumn;
    @FXML private TableColumn<Car, String> modelColumn;
    @FXML private TableColumn<Car, Integer> yearColumn;
    @FXML private TableColumn<Car, String> availabilityColumn;
    
    private CarService carService;
    private ObservableList<Car> searchResults;
    
    @FXML
    public void initialize() {
        carService = new CarService();
        searchResults = FXCollections.observableArrayList();
        
        // Set up search type combo
        searchTypeCombo.getItems().addAll("All Fields", "Brand", "Model", "Registration Number", "Color");
        searchTypeCombo.setValue("All Fields");
        
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("carId"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
        
        resultsTable.setItems(searchResults);
        
        // Load all cars initially
        loadAllCars();
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadAllCars();
            return;
        }
        
        String searchType = searchTypeCombo.getValue();
        searchResults.clear();
        
        if ("All Fields".equals(searchType)) {
            // Search in all fields
            searchResults.addAll(carService.searchCars(searchTerm));
        } else {
            // Search in specific field
            carService.getAllCars().stream()
                .filter(car -> matchesSearch(car, searchTerm, searchType))
                .forEach(searchResults::add);
        }
        
        if (searchResults.isEmpty()) {
            Alerts.showWarning("No Results", "No cars found matching: " + searchTerm);
        } else {
            Alerts.showSuccess("Search Complete", "Found " + searchResults.size() + " car(s)");
        }
    }
    
    private boolean matchesSearch(Car car, String searchTerm, String searchType) {
        String term = searchTerm.toLowerCase();
        
        switch (searchType) {
            case "Brand":
                return car.getBrand() != null && car.getBrand().toLowerCase().contains(term);
            case "Model":
                return car.getModel() != null && car.getModel().toLowerCase().contains(term);
            case "Registration Number":
                return car.getRegistrationNumber() != null && 
                       car.getRegistrationNumber().toLowerCase().contains(term);
            case "Color":
                return car.getColor() != null && car.getColor().toLowerCase().contains(term);
            default:
                return false;
        }
    }
    
    private void loadAllCars() {
        searchResults.clear();
        searchResults.addAll(carService.getAllCars());
    }
}

