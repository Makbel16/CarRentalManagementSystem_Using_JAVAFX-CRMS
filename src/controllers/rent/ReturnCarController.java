package controllers.rent;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
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
import java.time.temporal.ChronoUnit;
public class ReturnCarController {
    @FXML private TextField rentalIdField;
    @FXML private Label customerNameLabel;
    @FXML private Label carDetailsLabel;
    @FXML private Label rentalDateLabel;
    @FXML private Label scheduledReturnDateLabel;
    @FXML private DatePicker actualReturnDateField;
    @FXML private Label dailyRateLabel;
    @FXML private Label daysRentedLabel;
    @FXML private Label lateFeeLabel;
    @FXML private Label totalAmountLabel;
    @FXML private TextArea notesField;
    @FXML private TextField damageFeeField;
    
    @FXML private CheckBox damageCheckbox;
    @FXML private VBox damageReportSection;
    @FXML private VBox damageDetails;
    @FXML private TextArea damageDescriptionField;
    @FXML private TextField repairCostField;
    @FXML private Button addPhotoButton;
    @FXML private Label photoCountLabel;
    
    private RentService rentService;
    private CarService carService;
    private CustomerService customerService;
    private RentalRecord currentRental;
    private Car rentalCar;
    private Customer rentalCustomer;
    private int currentEmployeeId = 1; // Default employee ID - in real app, get from session
    private int damagePhotosCount = 0;
    
    @FXML
    public void initialize() {
        rentService = new RentService();
        carService = new CarService();
        customerService = new CustomerService();
        
        actualReturnDateField.setValue(LocalDate.now());
        
        // Set up damage report section
        damageReportSection.setVisible(false);
        damageReportSection.setManaged(false);
        
        // Add listener to actual return date for auto-calculation
        actualReturnDateField.valueProperty().addListener((observable, oldValue, newValue) -> {
            calculateCharges();
        });
        
        // Add listener to damage fee field for auto-calculation
        damageFeeField.textProperty().addListener((observable, oldValue, newValue) -> {
            calculateCharges();
        });
        
        // Add listener to repair cost field
        repairCostField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateDamageFee();
        });
    }
    
    @FXML
    private void toggleDamageReport() {
        if (damageCheckbox.isSelected()) {
            damageReportSection.setVisible(true);
            damageReportSection.setManaged(true);
            damageDetails.setDisable(false);
        } else {
            damageReportSection.setVisible(false);
            damageReportSection.setManaged(false);
            damageDetails.setDisable(true);
            // Clear damage fields when unchecked
            damageDescriptionField.clear();
            repairCostField.clear();
            damageFeeField.clear();
            damagePhotosCount = 0;
            photoCountLabel.setText("No photos added");
            calculateCharges(); // Recalculate without damage fee
        }
    }
    
    @FXML
    private void addDamagePhoto() {
        damagePhotosCount++;
        if (damagePhotosCount == 1) {
            photoCountLabel.setText("1 photo added");
        } else {
            photoCountLabel.setText(damagePhotosCount + " photos added");
        }
        // Note: In a real app, you would implement file picker and image handling here
        Alerts.showInformation("Info", "Photo added (simulated). In real app, this would open file picker.");
    }
    
    private void updateDamageFee() {
        try {
            if (!repairCostField.getText().isEmpty()) {
                double repairCost = Double.parseDouble(repairCostField.getText().trim());
                damageFeeField.setText(String.format("%.2f", repairCost));
            } else {
                damageFeeField.clear();
            }
        } catch (NumberFormatException e) {
            // Invalid input, don't update
        }
    }
    
    @FXML
    private void searchRental() {
        if (Validator.isEmpty(rentalIdField.getText())) {
            Alerts.showError("Error", "Please enter a rental ID");
            return;
        }
        
        try {
            int rentalId = Integer.parseInt(rentalIdField.getText().trim());
            currentRental = rentService.getRentalById(rentalId);
            
            if (currentRental == null) {
                Alerts.showError("Error", "Rental not found");
                clearFields();
                return;
            }
            
            if (!"Active".equals(currentRental.getStatus())) {
                Alerts.showWarning("Warning", "This rental is already completed or cancelled");
                clearFields();
                return;
            }
            
            // Load related data
            rentalCar = carService.getCarById(currentRental.getCarId());
            rentalCustomer = customerService.getCustomerById(currentRental.getCustomerId());
            
            if (rentalCar != null && rentalCustomer != null) {
                displayRentalInfo();
            } else {
                Alerts.showError("Error", "Could not load rental details");
                clearFields();
            }
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Please enter a valid rental ID");
        }
    }
    
    private void displayRentalInfo() {
        customerNameLabel.setText(rentalCustomer.getFullName());
        carDetailsLabel.setText(rentalCar.getBrand() + " " + rentalCar.getModel() + 
                               " (" + rentalCar.getYear() + ")");
        rentalDateLabel.setText(DateUtil.formatDate(currentRental.getRentalDate()));
        scheduledReturnDateLabel.setText(DateUtil.formatDate(currentRental.getReturnDate()));
        dailyRateLabel.setText("$" + String.format("%.2f", rentalCar.getPricePerDay()));
        
        long days = ChronoUnit.DAYS.between(currentRental.getRentalDate(), currentRental.getReturnDate());
        daysRentedLabel.setText(String.valueOf(days));
        
        calculateCharges();
    }
    
    @FXML
    private void calculateCharges() {
        if (currentRental == null || actualReturnDateField.getValue() == null) {
            return;
        }
        
        LocalDate actualReturn = actualReturnDateField.getValue();
        LocalDate scheduledReturn = currentRental.getReturnDate();
        
        // Calculate late fee
        double lateFee = 0.0;
        if (actualReturn.isAfter(scheduledReturn)) {
            long lateDays = ChronoUnit.DAYS.between(scheduledReturn, actualReturn);
            lateFee = lateDays * rentalCar.getPricePerDay() * 0.5; // 50% of daily rate per late day
        }
        lateFeeLabel.setText("$" + String.format("%.2f", lateFee));
        
        // Get damage fee
        double damageFee = 0.0;
        try {
            if (!Validator.isEmpty(damageFeeField.getText())) {
                damageFee = Double.parseDouble(damageFeeField.getText().trim());
            }
        } catch (NumberFormatException e) {
            // Invalid damage fee, will be 0
        }
        
        // Calculate total
        double total = currentRental.getTotalAmount() + lateFee + damageFee;
        totalAmountLabel.setText("$" + String.format("%.2f", total));
    }
    
    @FXML
    private void processReturn() {
        if (currentRental == null) {
            Alerts.showError("Error", "Please search for a rental first");
            return;
        }
        
        if (actualReturnDateField.getValue() == null) {
            Alerts.showError("Error", "Please select actual return date");
            return;
        }
        
        // Validate damage fee if damage is reported
        if (damageCheckbox.isSelected() && repairCostField.getText().isEmpty()) {
            Alerts.showError("Error", "Please enter repair cost when reporting damage");
            return;
        }
        
        // Calculate fees
        double lateFee = 0.0;
        LocalDate actualReturn = actualReturnDateField.getValue();
        LocalDate scheduledReturn = currentRental.getReturnDate();
        
        if (actualReturn.isAfter(scheduledReturn)) {
            long lateDays = ChronoUnit.DAYS.between(scheduledReturn, actualReturn);
            lateFee = lateDays * rentalCar.getPricePerDay() * 0.5;
        }
        
        double damageFee = 0.0;
        String damageDescription = "";
        
        if (damageCheckbox.isSelected()) {
            try {
                if (!Validator.isEmpty(repairCostField.getText())) {
                    damageFee = Double.parseDouble(repairCostField.getText().trim());
                }
            } catch (NumberFormatException e) {
                Alerts.showError("Error", "Invalid repair cost amount");
                return;
            }
            
            damageDescription = damageDescriptionField.getText().trim();
            if (damageDescription.isEmpty()) {
                Alerts.showError("Error", "Please describe the damage");
                return;
            }
            
            // Add photo info to notes
            if (damagePhotosCount > 0) {
                damageDescription += "\n\nPhotos taken: " + damagePhotosCount;
            }
        }
        
        String notes = notesField.getText().trim();
        if (!damageDescription.isEmpty()) {
            if (!notes.isEmpty()) {
                notes += "\n\nDAMAGE REPORT:\n" + damageDescription;
            } else {
                notes = "DAMAGE REPORT:\n" + damageDescription;
            }
        }
        
        if (Alerts.showConfirmation("Confirm Return", 
                "Process return for rental #" + currentRental.getRentalId() + "?\n" +
                "Customer: " + rentalCustomer.getFullName() + "\n" +
                "Car: " + rentalCar.getBrand() + " " + rentalCar.getModel() + "\n" +
                "Late Fee: $" + String.format("%.2f", lateFee) + "\n" +
                "Damage Fee: $" + String.format("%.2f", damageFee) + "\n" +
                "Total: $" + String.format("%.2f", currentRental.getTotalAmount() + lateFee + damageFee))) {
            
            if (rentService.returnCar(currentRental.getRentalId(), currentEmployeeId, 
                                     lateFee, damageFee, notes)) {
                Alerts.showSuccess("Success", "Car returned successfully!");
                clearFields();
            } else {
                Alerts.showError("Error", "Failed to process return");
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        clearFields();
    }
    
    private void clearFields() {
        rentalIdField.clear();
        customerNameLabel.setText("");
        carDetailsLabel.setText("");
        rentalDateLabel.setText("");
        scheduledReturnDateLabel.setText("");
        dailyRateLabel.setText("");
        daysRentedLabel.setText("");
        lateFeeLabel.setText("$0.00");
        totalAmountLabel.setText("$0.00");
        notesField.clear();
        damageFeeField.clear();
        actualReturnDateField.setValue(LocalDate.now());
        
        // Reset damage report section
        damageCheckbox.setSelected(false);
        damageReportSection.setVisible(false);
        damageReportSection.setManaged(false);
        damageDescriptionField.clear();
        repairCostField.clear();
        damagePhotosCount = 0;
        photoCountLabel.setText("No photos added");
        
        currentRental = null;
        rentalCar = null;
        rentalCustomer = null;
    }
}