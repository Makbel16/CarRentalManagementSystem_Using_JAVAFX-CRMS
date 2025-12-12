package controllers.cars;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import models.Car;
import models.RentalRecord;
import services.CarService;
import services.RentService;
import controllers.utils.Alerts;
import javafx.beans.property.SimpleStringProperty;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class DisplayCarsController {
    @FXML private TableView<Car> carsTable;
    @FXML private TableColumn<Car, Integer> idColumn;
    @FXML private TableColumn<Car, String> makeColumn;
    @FXML private TableColumn<Car, String> modelColumn;
    @FXML private TableColumn<Car, Integer> yearColumn;
    @FXML private TableColumn<Car, String> colorColumn;
    @FXML private TableColumn<Car, Double> pricePerDayColumn;
    @FXML private TableColumn<Car, String> statusColumn;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    
    private CarService carService;
    private RentService rentService;
    private ObservableList<Car> carsList;
    private Set<Integer> activeRentedCarIds;
    
    @FXML
    public void initialize() {
        carService = new CarService();
        rentService = new RentService();
        carsList = FXCollections.observableArrayList();
        activeRentedCarIds = new HashSet<>();
        
        // Set up table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("carId"));
        makeColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        pricePerDayColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        statusColumn.setCellValueFactory(cellData -> {
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
        
        // Format price column
        pricePerDayColumn.setCellFactory(column -> new TableCell<Car, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });
        
        carsTable.setItems(carsList);
        
        // Enable/disable buttons based on selection
        carsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
        
        loadCars();
    }
    
    @FXML
    private void handleRefresh() {
        loadCars();
        Alerts.showSuccess("Success", "Cars list refreshed");
    }
    
    @FXML
    private void handleAddCar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cars/AddCar.fxml"));
            Parent content = loader.load();
            
            StackPane parent = (StackPane) carsTable.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(content);
            }
        } catch (Exception e) {
            Alerts.showError("Error", "Failed to load Add Car page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEditCar() {
        Car selectedCar = carsTable.getSelectionModel().getSelectedItem();
        if (selectedCar == null) {
            Alerts.showWarning("Warning", "Please select a car to edit");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cars/EditCar.fxml"));
            Parent content = loader.load();
            
            // Pass selected car to edit controller if needed
            StackPane parent = (StackPane) carsTable.getScene().lookup("#contentPane");
            if (parent != null) {
                parent.getChildren().clear();
                parent.getChildren().add(content);
            }
        } catch (Exception e) {
            Alerts.showError("Error", "Failed to load Edit Car page: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleDeleteCar() {
        Car selectedCar = carsTable.getSelectionModel().getSelectedItem();
        if (selectedCar == null) {
            Alerts.showWarning("Warning", "Please select a car to delete");
            return;
        }
        
        if (Alerts.showConfirmation("Confirm Delete", 
                "Are you sure you want to delete " + selectedCar.getBrand() + " " + 
                selectedCar.getModel() + "?")) {
            if (carService.deleteCar(selectedCar.getCarId())) {
                Alerts.showSuccess("Success", "Car deleted successfully");
                loadCars();
            } else {
                Alerts.showError("Error", "Failed to delete car");
            }
        }
    }
    
    private void loadCars() {
        refreshActiveRentedCarIds();
        carsList.clear();
        carsList.addAll(carService.getAllCars());
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



