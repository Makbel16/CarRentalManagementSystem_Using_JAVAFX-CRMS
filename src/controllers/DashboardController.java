package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import services.DashboardService;
import services.CarService;
import services.CustomerService;
import services.RentService;
import models.Car;
import controllers.utils.Alerts;
import controllers.utils.DateUtil;
import java.util.List;
import java.util.Map;

public class DashboardController {
    @FXML private Label rentingCarsLabel;
    @FXML private Label availableCarsLabel;
    @FXML private Label totalEarningsLabel;
    @FXML private Label totalCustomersLabel;
    @FXML private VBox detailsPane;
    
    // Add button references
    @FXML private Button showRentingBtn;
    @FXML private Button showAvailableBtn;
    @FXML private Button showEarningsBtn;
    @FXML private Button showCustomersBtn;
    @FXML private Button refreshBtn;
    
    private DashboardService dashboardService;
    private CarService carService;
    private CustomerService customerService;
    private RentService rentService;
    
    @FXML
    public void initialize() {
        System.out.println("=== DashboardController.initialize() START ===");
        
        try {
            // DEBUG: Check what was injected
            System.out.println("FXML Injection Status:");
            System.out.println("  rentingCarsLabel: " + (rentingCarsLabel != null ? "OK" : "NULL"));
            System.out.println("  availableCarsLabel: " + (availableCarsLabel != null ? "OK" : "NULL"));
            System.out.println("  totalEarningsLabel: " + (totalEarningsLabel != null ? "OK" : "NULL"));
            System.out.println("  totalCustomersLabel: " + (totalCustomersLabel != null ? "OK" : "NULL"));
            System.out.println("  detailsPane: " + (detailsPane != null ? "OK" : "NULL"));
            
            // Initialize services
            initializeServices();
            
            // Set button actions
            setupButtonActions();
            
            // Load initial statistics
            loadStatistics();
            
            System.out.println("=== DashboardController.initialize() END - SUCCESS ===");
            
        } catch (Exception e) {
            System.err.println("=== ERROR in initialize(): " + e.getMessage() + " ===");
            e.printStackTrace();
            setSafeDefaults();
        }
    }
    
    private void initializeServices() {
        try {
            System.out.println("Initializing services...");
            dashboardService = new DashboardService();
            carService = new CarService();
            customerService = new CustomerService();
            rentService = new RentService();
            System.out.println("Services initialized successfully!");
        } catch (Exception e) {
            System.err.println("ERROR initializing services: " + e.getMessage());
            e.printStackTrace();
            // Don't throw - just log and continue with null services
        }
    }
    
    private void setupButtonActions() {
        System.out.println("Setting up button actions...");
        
        if (showRentingBtn != null) {
            showRentingBtn.setOnAction(e -> showRentingCars());
            System.out.println("  showRentingBtn action set");
        }
        
        if (showAvailableBtn != null) {
            showAvailableBtn.setOnAction(e -> showAvailableCars());
            System.out.println("  showAvailableBtn action set");
        }
        
        if (showEarningsBtn != null) {
            showEarningsBtn.setOnAction(e -> showTotalEarnings());
            System.out.println("  showEarningsBtn action set");
        }
        
        if (showCustomersBtn != null) {
            showCustomersBtn.setOnAction(e -> showCustomers());
            System.out.println("  showCustomersBtn action set");
        }
        
        if (refreshBtn != null) {
            refreshBtn.setOnAction(e -> handleRefresh());
            System.out.println("  refreshBtn action set");
        }
    }
    
    private void setSafeDefaults() {
        if (rentingCarsLabel != null) rentingCarsLabel.setText("0");
        if (availableCarsLabel != null) availableCarsLabel.setText("0");
        if (totalEarningsLabel != null) totalEarningsLabel.setText("$0.00");
        if (totalCustomersLabel != null) totalCustomersLabel.setText("0");
    }
    
    private void loadStatistics() {
        try {
            System.out.println("Loading dashboard statistics...");
            
            if (dashboardService == null) {
                System.err.println("DashboardService is null - using defaults");
                setSafeDefaults();
                return;
            }
            
            var stats = dashboardService.getDashboardStats();
            System.out.println("Stats received: " + stats);
            
            // Update labels
            rentingCarsLabel.setText(String.valueOf(stats.get("activeRentals")));
            availableCarsLabel.setText(String.valueOf(stats.get("availableCars")));
            
            Object revenueObj = stats.get("monthlyRevenue");
            if (revenueObj instanceof Integer) {
                totalEarningsLabel.setText("$" + revenueObj);
            } else if (revenueObj instanceof Double) {
                double revenue = (Double) revenueObj;
                totalEarningsLabel.setText(String.format("$%.2f", revenue));
            } else {
                totalEarningsLabel.setText("$0.00");
            }
            
            totalCustomersLabel.setText(String.valueOf(stats.get("totalCustomers")));
            
            System.out.println("Dashboard labels updated successfully");
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard statistics: " + e.getMessage());
            e.printStackTrace();
            setSafeDefaults();
            showErrorInPane("Failed to load statistics: " + e.getMessage());
        }
    }
    
    @FXML
    private void showRentingCars() {
        try {
            System.out.println("Showing renting cars...");
            
            if (detailsPane == null) {
                System.err.println("ERROR: detailsPane is null!");
                return;
            }
            
            detailsPane.getChildren().clear();
            
            Label title = new Label("Currently Rented Cars");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px;");
            
            if (rentService == null) {
                System.err.println("RentService is null - showing sample data");
                showSampleRentingCars();
                return;
            }
            
            TableView<Map<String, Object>> table = new TableView<>();
            
            // Create columns
            TableColumn<Map<String, Object>, Integer> idCol = new TableColumn<>("Rental ID");
            idCol.setCellValueFactory(cellData -> {
                Map<String, Object> row = cellData.getValue();
                return new javafx.beans.property.SimpleIntegerProperty((Integer) row.get("rental_id")).asObject();
            });
            
            TableColumn<Map<String, Object>, String> carCol = new TableColumn<>("Car");
            carCol.setCellValueFactory(cellData -> {
                Map<String, Object> row = cellData.getValue();
                String brand = (String) row.get("brand");
                String model = (String) row.get("model");
                return new javafx.beans.property.SimpleStringProperty(brand + " " + model);
            });
            
            TableColumn<Map<String, Object>, String> customerCol = new TableColumn<>("Customer");
            customerCol.setCellValueFactory(cellData -> {
                Map<String, Object> row = cellData.getValue();
                return new javafx.beans.property.SimpleStringProperty((String) row.get("customer_name"));
            });
            
            TableColumn<Map<String, Object>, String> rentalDateCol = new TableColumn<>("Rental Date");
            rentalDateCol.setCellValueFactory(cellData -> {
                Map<String, Object> row = cellData.getValue();
                java.sql.Date rentalDate = (java.sql.Date) row.get("rental_date");
                if (rentalDate != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                        DateUtil.formatDate(rentalDate.toLocalDate())
                    );
                } else {
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                }
            });
            
            TableColumn<Map<String, Object>, String> returnDateCol = new TableColumn<>("Return Date");
            returnDateCol.setCellValueFactory(cellData -> {
                Map<String, Object> row = cellData.getValue();
                java.sql.Date returnDate = (java.sql.Date) row.get("return_date");
                if (returnDate != null) {
                    return new javafx.beans.property.SimpleStringProperty(
                        DateUtil.formatDate(returnDate.toLocalDate())
                    );
                } else {
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                }
            });
            
            TableColumn<Map<String, Object>, Double> amountCol = new TableColumn<>("Amount");
            amountCol.setCellValueFactory(cellData -> {
                Map<String, Object> row = cellData.getValue();
                Double amount = (Double) row.get("total_amount");
                return new javafx.beans.property.SimpleDoubleProperty(amount != null ? amount : 0.0).asObject();
            });
            
            amountCol.setCellFactory(col -> new TableCell<Map<String, Object>, Double>() {
                @Override
                protected void updateItem(Double amount, boolean empty) {
                    super.updateItem(amount, empty);
                    if (empty || amount == null) {
                        setText(null);
                    } else {
                        setText(String.format("$%.2f", amount));
                    }
                }
            });
            
            table.getColumns().addAll(idCol, carCol, customerCol, rentalDateCol, returnDateCol, amountCol);
            
            List<Map<String, Object>> activeRentals = rentService.getActiveRentals();
            ObservableList<Map<String, Object>> data = FXCollections.observableArrayList(activeRentals);
            table.setItems(data);
            
            VBox container = new VBox(10, title, table);
            detailsPane.getChildren().add(container);
            
        } catch (Exception e) {
            System.err.println("Error showing renting cars: " + e.getMessage());
            e.printStackTrace();
            showErrorInPane("Failed to load renting cars: " + e.getMessage());
        }
    }
    
    private void showSampleRentingCars() {
        if (detailsPane == null) return;
        
        Label title = new Label("Currently Rented Cars (Sample Data)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px;");
        
        VBox sampleData = new VBox(10,
            new Label("• Toyota Camry - John Doe (Rented: 2024-01-15)"),
            new Label("• Honda Civic - Jane Smith (Rented: 2024-01-10)"),
            new Label("• Ford Focus - Bob Johnson (Rented: 2024-01-05)")
        );
        
        detailsPane.getChildren().addAll(title, sampleData);
    }
    
    @FXML
    private void showAvailableCars() {
        try {
            System.out.println("Showing available cars...");
            
            if (detailsPane == null) {
                System.err.println("ERROR: detailsPane is null!");
                return;
            }
            
            detailsPane.getChildren().clear();
            
            Label title = new Label("Available Cars");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px;");
            
            if (carService == null) {
                System.err.println("CarService is null - showing sample data");
                showSampleAvailableCars();
                return;
            }
            
            TableView<Car> table = new TableView<>();
            
            TableColumn<Car, String> brandCol = new TableColumn<>("Brand");
            brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
            
            TableColumn<Car, String> modelCol = new TableColumn<>("Model");
            modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
            
            TableColumn<Car, Integer> yearCol = new TableColumn<>("Year");
            yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
            
            TableColumn<Car, String> regCol = new TableColumn<>("Registration");
            regCol.setCellValueFactory(new PropertyValueFactory<>("registrationNumber"));
            
            TableColumn<Car, Double> priceCol = new TableColumn<>("Price/Day");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
            priceCol.setCellFactory(col -> new TableCell<Car, Double>() {
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
            
            table.getColumns().addAll(brandCol, modelCol, yearCol, regCol, priceCol);
            
            List<Car> availableCars = carService.getAvailableCars();
            ObservableList<Car> data = FXCollections.observableArrayList(availableCars);
            table.setItems(data);
            
            VBox container = new VBox(10, title, table);
            detailsPane.getChildren().add(container);
            
        } catch (Exception e) {
            System.err.println("Error showing available cars: " + e.getMessage());
            e.printStackTrace();
            showErrorInPane("Failed to load available cars: " + e.getMessage());
        }
    }
    
    private void showSampleAvailableCars() {
        if (detailsPane == null) return;
        
        Label title = new Label("Available Cars (Sample Data)");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px;");
        
        VBox sampleData = new VBox(10,
            new Label("• Toyota Camry - 2022 - $50/day"),
            new Label("• Honda Civic - 2021 - $45/day"),
            new Label("• Ford Focus - 2020 - $40/day"),
            new Label("• BMW X5 - 2023 - $120/day")
        );
        
        detailsPane.getChildren().addAll(title, sampleData);
    }
    
    @FXML
    private void showTotalEarnings() {
        try {
            System.out.println("Showing total earnings...");
            
            if (detailsPane == null) {
                System.err.println("ERROR: detailsPane is null!");
                return;
            }
            
            detailsPane.getChildren().clear();
            
            double earnings = 0.0;
            
            if (dashboardService != null) {
                var stats = dashboardService.getDashboardStats();
                Object revenueObj = stats.get("monthlyRevenue");
                
                if (revenueObj instanceof Integer) {
                    earnings = ((Integer) revenueObj).doubleValue();
                } else if (revenueObj instanceof Double) {
                    earnings = (Double) revenueObj;
                }
            } else {
                earnings = 1250.00; // Sample data
            }
            
            Label title = new Label("Total Earnings (This Month)");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px;");
            
            Label earningsLabel = new Label(String.format("$%.2f", earnings));
            earningsLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #2ecc71; -fx-padding: 20px;");
            
            Label statsLabel = new Label("Revenue Statistics");
            statsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            Label detailLabel = new Label(String.format(
                "• Monthly Revenue: $%.2f\n" +
                "• From active rentals\n" +
                "• Updated in real-time",
                earnings
            ));
            detailLabel.setStyle("-fx-font-size: 12px;");
            
            VBox statsBox = new VBox(5, statsLabel, detailLabel);
            statsBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15px;");
            
            VBox container = new VBox(20, title, earningsLabel, statsBox);
            container.setAlignment(Pos.CENTER);
            detailsPane.getChildren().add(container);
            
        } catch (Exception e) {
            System.err.println("Error showing total earnings: " + e.getMessage());
            e.printStackTrace();
            showErrorInPane("Failed to load earnings data: " + e.getMessage());
        }
    }
    
    @FXML
    private void showCustomers() {
        try {
            System.out.println("Showing customers...");
            
            if (detailsPane == null) {
                System.err.println("ERROR: detailsPane is null!");
                return;
            }
            
            detailsPane.getChildren().clear();
            
            int count = 0;
            
            if (dashboardService != null) {
                var stats = dashboardService.getDashboardStats();
                count = (int) stats.get("totalCustomers");
            } else {
                count = 24; // Sample data
            }
            
            Label title = new Label("Total Customers");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 10px;");
            
            Label countLabel = new Label(String.valueOf(count));
            countLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #3498db; -fx-padding: 20px;");
            
            Label statsLabel = new Label("Customer Information");
            statsLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
            Label detailLabel = new Label(String.format(
                "• Total Registered: %d\n" +
                "• Can rent multiple cars\n" +
                "• Valid license required",
                count
            ));
            detailLabel.setStyle("-fx-font-size: 12px;");
            
            VBox statsBox = new VBox(5, statsLabel, detailLabel);
            statsBox.setStyle("-fx-background-color: #f5f5f5; -fx-padding: 15px;");
            
            VBox container = new VBox(20, title, countLabel, statsBox);
            container.setAlignment(Pos.CENTER);
            detailsPane.getChildren().add(container);
            
        } catch (Exception e) {
            System.err.println("Error showing customers: " + e.getMessage());
            e.printStackTrace();
            showErrorInPane("Failed to load customer data: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRefresh() {
        System.out.println("Refreshing dashboard...");
        loadStatistics();
        
        if (detailsPane != null) {
            detailsPane.getChildren().clear();
            Label refreshMessage = new Label("Dashboard refreshed successfully!");
            refreshMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
            detailsPane.getChildren().add(refreshMessage);
        }
    }
    
    private void showErrorInPane(String message) {
        if (detailsPane != null) {
            detailsPane.getChildren().clear();
            Label errorLabel = new Label("Error: " + message);
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            detailsPane.getChildren().add(errorLabel);
        }
    }
}