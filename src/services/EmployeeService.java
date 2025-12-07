// src/services/EmployeeService.java
package services;

import dao.EmployeeDAO;
import models.Employee;
import java.util.List;

public class EmployeeService {
    private final EmployeeDAO employeeDAO;
    
    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }
    
    public boolean addEmployee(Employee employee) {
        // Additional business logic can be added here
        if (employee == null) {
            System.err.println("Employee is null");
            return false;
        }
        
        // Validate required fields before adding
        if (employee.getFirstName() == null || employee.getFirstName().trim().isEmpty() ||
            employee.getLastName() == null || employee.getLastName().trim().isEmpty() ||
            employee.getEmail() == null || employee.getEmail().trim().isEmpty()) {
            System.err.println("Employee missing required fields");
            return false;
        }
        
        return employeeDAO.addEmployee(employee);
    }
    
    public boolean updateEmployee(Employee employee) {
        return employeeDAO.updateEmployee(employee);
    }
    
    public boolean deleteEmployee(int employeeId) {
        return employeeDAO.deleteEmployee(employeeId);
    }
    
    public Employee getEmployeeById(int employeeId) {
        return employeeDAO.getEmployeeById(employeeId);
    }
    
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }
    
    public List<Employee> searchEmployees(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllEmployees();
        }
        return employeeDAO.searchEmployees(searchTerm.trim());
    }
    
    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return employeeDAO.emailExists(email.trim());
    }
    public int getTotalEmployeesCount() {
        try {
            return employeeDAO.getTotalEmployeesCount();
        } catch (Exception e) {
            System.err.println("Error getting total employees count in service: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}