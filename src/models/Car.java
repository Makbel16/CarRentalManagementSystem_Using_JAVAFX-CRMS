package models;

public class Car {
    private int carId;
    private String brand;
    private String model;
    private int year;
    private String color;
    private String registrationNumber;
    private double pricePerDay;
    private String availability; // "Available", "Rented", "Maintenance"
    private String fuelType;
    private int mileage;
    private String status;
    
    public Car() {
    }
    
    public Car(String brand, String model, int year, String color, String registrationNumber, 
               double pricePerDay, String availability, String fuelType, int mileage) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.color = color;
        this.registrationNumber = registrationNumber;
        this.pricePerDay = pricePerDay;
        this.availability = availability;
        this.fuelType = fuelType;
        this.mileage = mileage;
        this.status = "Active";
    }
    
    // Getters and Setters
    public int getCarId() {
        return carId;
    }
    
    public void setCarId(int carId) {
        this.carId = carId;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    public double getPricePerDay() {
        return pricePerDay;
    }
    
    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
    
    public String getAvailability() {
        return availability;
    }
    
    public void setAvailability(String availability) {
        this.availability = availability;
    }
    
    public String getFuelType() {
        return fuelType;
    }
    
    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
    
    public int getMileage() {
        return mileage;
    }
    
    public void setMileage(int mileage) {
        this.mileage = mileage;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return brand + " " + model + " (" + year + ") - " + registrationNumber;
    }

	public String getLicensePlate() {
		// TODO Auto-generated method stub
		return null;
	}
}



