package controllers.rent;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Car;
import models.Customer;
import models.RentalRecord;
import services.CarService;
import services.CustomerService;
import services.RentService;
import controllers.utils.Alerts;
import controllers.utils.DateUtil;
import controllers.utils.Validator;
import java.time.LocalDate;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class RentCarController {
    @FXML private TextField customerIdField;
    @FXML private Label customerNameLabel;
    @FXML private TextField carIdField;
    @FXML private Label carDetailsLabel;
    @FXML private DatePicker rentalDateField;
    @FXML private DatePicker returnDateField;
    @FXML private Label dailyRateLabel;
    @FXML private Label totalCostLabel;
    @FXML private ImageView animatedCarView;
    @FXML private Label availableCarsLabel;
    
    private CarService carService;
    private CustomerService customerService;
    private RentService rentService;
    private Car selectedCar;
    private Customer selectedCustomer;
    private int currentEmployeeId = 1; // Default employee ID
    
    private Timeline carAnimation;
    private Circle statusIndicator;
    
    @FXML
    public void initialize() {
        carService = new CarService();
        customerService = new CustomerService();
        rentService = new RentService();
        
        rentalDateField.setValue(LocalDate.now());
        returnDateField.setValue(LocalDate.now().plusDays(1));
        
        // Calculate total when dates change
        rentalDateField.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
        returnDateField.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotal());
        
        // Load and setup animation
        setupCarAnimation();
        updateAvailableCarsCount();
    }
    
    private void setupCarAnimation() {
        try {
            // Try to load animated GIF (replace with your actual GIF path)
            Image animatedGif = new Image(getClass().getResource("/images/car-animation.gif").toExternalForm());
            animatedCarView.setImage(animatedGif);
            
            // Add pulsing effect to the image
            ScaleTransition pulse = new ScaleTransition(Duration.seconds(2), animatedCarView);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.05);
            pulse.setToY(1.05);
            pulse.setAutoReverse(true);
            pulse.setCycleCount(Timeline.INDEFINITE);
            pulse.play();
            
        } catch (Exception e) {
            // Fallback to static image with CSS animation
            try {
                Image staticImage = new Image(getClass().getResource("/images/car-static.png").toExternalForm());
                animatedCarView.setImage(staticImage);
                
                // Create manual animation for static image
                TranslateTransition translate = new TranslateTransition(Duration.seconds(3), animatedCarView);
                translate.setFromX(-30);
                translate.setToX(30);
                translate.setAutoReverse(true);
                translate.setCycleCount(Timeline.INDEFINITE);
                translate.play();
                
            } catch (Exception ex) {
                System.err.println("Could not load car image: " + ex.getMessage());
            }
        }
    }
    
    private void updateAvailableCarsCount() {
        try {
            int availableCount = carService.getAvailableCarsCount();
            availableCarsLabel.setText(availableCount + " cars available for rent");
        } catch (Exception e) {
            availableCarsLabel.setText("Check availability");
        }
    }
    
    @FXML
    private void searchCustomer() {
        if (Validator.isEmpty(customerIdField.getText())) {
            Alerts.showError("Error", "Please enter a customer ID");
            return;
        }
        
        try {
            int customerId = Integer.parseInt(customerIdField.getText().trim());
            selectedCustomer = customerService.getCustomerById(customerId);
            
            if (selectedCustomer != null) {
                customerNameLabel.setText(selectedCustomer.getFullName() + " - " + selectedCustomer.getPhone());
                // Add success animation
                animateSuccess();
            } else {
                customerNameLabel.setText("Customer not found");
                selectedCustomer = null;
                animateError();
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter a valid customer ID");
            animateError();
        }
    }
    
    @FXML
    private void searchCar() {
        if (Validator.isEmpty(carIdField.getText())) {
            Alerts.showError("Error", "Please enter a car ID");
            return;
        }
        
        try {
            int carId = Integer.parseInt(carIdField.getText().trim());
            selectedCar = carService.getCarById(carId);
            
            if (selectedCar != null) {
                if (!"Available".equals(selectedCar.getAvailability())) {
                    Alerts.showWarning("Warning", "This car is not available. Status: " + selectedCar.getAvailability());
                    carDetailsLabel.setText(selectedCar.getBrand() + " " + selectedCar.getModel() + 
                                          " - NOT AVAILABLE (" + selectedCar.getAvailability() + ")");
                    selectedCar = null;
                    animateWarning();
                } else {
                    carDetailsLabel.setText(selectedCar.getBrand() + " " + selectedCar.getModel() + 
                                          " (" + selectedCar.getYear() + ") - $" + selectedCar.getPricePerDay() + "/day");
                    dailyRateLabel.setText("$" + String.format("%.2f", selectedCar.getPricePerDay()));
                    calculateTotal();
                    animateSuccess();
                }
            } else {
                carDetailsLabel.setText("Car not found");
                selectedCar = null;
                animateError();
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter a valid car ID");
            animateError();
        }
    }
    
    @FXML
    private void calculateTotal() {
        if (selectedCar == null || rentalDateField.getValue() == null || returnDateField.getValue() == null) {
            totalCostLabel.setText("$0.00");
            return;
        }
        
        LocalDate rentalDate = rentalDateField.getValue();
        LocalDate returnDate = returnDateField.getValue();
        
        if (!Validator.isValidDateRange(rentalDate, returnDate)) {
            totalCostLabel.setText("Invalid date range");
            return;
        }
        
        long days = DateUtil.daysBetween(rentalDate, returnDate);
        if (days <= 0) {
            days = 1; // Minimum 1 day
        }
        
        double total = selectedCar.getPricePerDay() * days;
        totalCostLabel.setText("$" + String.format("%.2f", total));
        
        // Animate total cost change
        FadeTransition fade = new FadeTransition(Duration.millis(300), totalCostLabel);
        fade.setFromValue(0.5);
        fade.setToValue(1.0);
        fade.play();
    }
    
    // Animation helper methods
    private void animateSuccess() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), animatedCarView);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }
    
    private void animateError() {
        RotateTransition shake = new RotateTransition(Duration.millis(100), animatedCarView);
        shake.setFromAngle(-5);
        shake.setToAngle(5);
        shake.setAutoReverse(true);
        shake.setCycleCount(4);
        shake.play();
    }
    
    private void animateWarning() {
        FadeTransition flash = new FadeTransition(Duration.millis(200), animatedCarView);
        flash.setFromValue(1.0);
        flash.setToValue(0.5);
        flash.setAutoReverse(true);
        flash.setCycleCount(3);
        flash.play();
    }
    
    @FXML
    private void handleRent() {
        if (selectedCustomer == null) {
            Alerts.showError("Error", "Please search and select a customer");
            return;
        }
        
        if (selectedCar == null) {
            Alerts.showError("Error", "Please search and select an available car");
            return;
        }
        
        if (rentalDateField.getValue() == null || returnDateField.getValue() == null) {
            Alerts.showError("Error", "Please select rental and return dates");
            return;
        }
        
        LocalDate rentalDate = rentalDateField.getValue();
        LocalDate returnDate = returnDateField.getValue();
        
        if (!Validator.isValidDateRange(rentalDate, returnDate)) {
            Alerts.showError("Error", "Return date must be after rental date");
            return;
        }
        
        long days = DateUtil.daysBetween(rentalDate, returnDate);
        if (days <= 0) {
            days = 1;
        }
        
        double totalAmount = selectedCar.getPricePerDay() * days;
        
        if (Alerts.showConfirmation("Confirm Rental", 
                "Rent " + selectedCar.getBrand() + " " + selectedCar.getModel() + 
                " to " + selectedCustomer.getFullName() + 
                " for " + days + " day(s)?\nTotal: $" + String.format("%.2f", totalAmount))) {
            
            if (rentService.rentCar(selectedCar.getCarId(), selectedCustomer.getCustomerId(), 
                                  currentEmployeeId, rentalDate, returnDate, totalAmount)) {
                Alerts.showSuccess("Success", "Car rented successfully!");
                
                // Celebrate animation
                celebrateAnimation();
                clearFields();
                updateAvailableCarsCount();
            } else {
                Alerts.showError("Error", "Failed to process rental. Car may no longer be available.");
            }
        }
    }
    
    private void celebrateAnimation() {
        // Create a celebration animation
        ParallelTransition celebrate = new ParallelTransition();
        
        // Scale up
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(500), animatedCarView);
        scaleUp.setToX(1.3);
        scaleUp.setToY(1.3);
        
        // Bounce
        TranslateTransition bounce = new TranslateTransition(Duration.millis(300), animatedCarView);
        bounce.setFromY(0);
        bounce.setToY(-30);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(3);
        
        celebrate.getChildren().addAll(scaleUp, bounce);
        celebrate.setOnFinished(e -> {
            // Return to normal
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(300), animatedCarView);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
        });
        
        celebrate.play();
    }
    
    @FXML
    private void handleCancel() {
        clearFields();
    }
    
    private void clearFields() {
        customerIdField.clear();
        customerNameLabel.setText("");
        carIdField.clear();
        carDetailsLabel.setText("");
        dailyRateLabel.setText("");
        totalCostLabel.setText("");
        rentalDateField.setValue(LocalDate.now());
        returnDateField.setValue(LocalDate.now().plusDays(1));
        selectedCar = null;
        selectedCustomer = null;
    }
}