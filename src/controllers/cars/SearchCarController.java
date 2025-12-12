package controllers.cars;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Car;
import models.RentalRecord;
import services.CarService;
import services.RentService;
import controllers.utils.Alerts;
import javafx.beans.property.SimpleStringProperty;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

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
    private RentService rentService;
    private ObservableList<Car> searchResults;
    private Set<Integer> activeRentedCarIds;
    
    @FXML
    public void initialize() {
        carService = new CarService();
        rentService = new RentService();
        searchResults = FXCollections.observableArrayList();
        activeRentedCarIds = new HashSet<>();
        
        // Set up search type combo
        searchTypeCombo.getItems().addAll("All Fields", "Brand", "Model", "Registration Number", "Color");
        searchTypeCombo.setValue("All Fields");
        
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("carId"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        availabilityColumn.setCellValueFactory(cellData -> {
            Car car = cellData.getValue();
            if (car == null) {
                return new SimpleStringProperty("");
            }
            String value;
            if (activeRentedCarIds != null && activeRentedCarIds.contains(car.getCarId())) {
                value = "Rented";
            } else {
                value = car.getAvailability() != null ? car.getAvailability() : "Unknown";
            }
            return new SimpleStringProperty(value);
        });
        
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
        refreshActiveRentedCarIds();
        searchResults.clear();
        searchResults.addAll(carService.getAllCars());
    }
    
    private void refreshActiveRentedCarIds() {
        if (activeRentedCarIds == null) {
            activeRentedCarIds = new HashSet<>();
        }
        activeRentedCarIds.clear();
        try {
            List<RentalRecord> activeRentals = rentService.getActiveRentalRecords();
            for (RentalRecord rental : activeRentals) {
                activeRentedCarIds.add(rental.getCarId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

