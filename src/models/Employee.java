// src/models/Employee.java
package models;

import java.time.LocalDate;

public class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String position;
    private double salary;
    private LocalDate hireDate;
    private String status;

    // Constructors
    public Employee() {
        this.status = "Active";
    }
    
    public Employee(String firstName, String lastName, String email, String position) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.position = position;
        this.hireDate = LocalDate.now();
    }

    // Getters and Setters
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    // For backward compatibility
    public void setHireDate(java.sql.Date hireDate) {
        this.hireDate = hireDate != null ? hireDate.toLocalDate() : null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Helper methods
    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }
    
    public String getFormattedSalary() {
        return String.format("$%.2f", salary);
    }
    
    public String getFormattedHireDate() {
        return hireDate != null ? hireDate.toString() : "N/A";
    }

    @Override
    public String toString() {
        return getFullName() + " - " + position + " (" + email + ")";
    }
}