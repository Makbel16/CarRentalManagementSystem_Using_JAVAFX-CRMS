package services;

import dao.CustomerDAO;
import models.Customer;
import java.util.List;

public class CustomerService {
    private CustomerDAO customerDAO;
    
    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }
    
    public boolean addCustomer(Customer customer) {
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            return false;
        }
        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            return false;
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            return false;
        }
        if (customer.getLicenseNumber() == null || customer.getLicenseNumber().trim().isEmpty()) {
            return false;
        }
        
        return customerDAO.addCustomer(customer);
    }
    
    public boolean updateCustomer(Customer customer) {
        if (customer.getCustomerId() <= 0) {
            return false;
        }
        return customerDAO.updateCustomer(customer);
    }
    
    public boolean deleteCustomer(int customerId) {
        return customerDAO.deleteCustomer(customerId);
    }
    
    public Customer getCustomerById(int customerId) {
        return customerDAO.getCustomerById(customerId);
    }
    
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    public List<Customer> searchCustomers(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerDAO.searchCustomers(searchTerm.trim());
    }
    
    public int getTotalCustomersCount() {
        return getAllCustomers().size();
    }
}



