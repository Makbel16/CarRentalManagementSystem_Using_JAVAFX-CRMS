package models;

/**
 * Enum representing different user roles in the system
 * Each role has specific permissions and access levels
 */
public enum UserRole {
    /**
     * ADMIN: Full system access
     * - Can manage employees
     * - Can view salary information
     * - Can delete any records
     * - Full access to all features
     */
    ADMIN("Admin"),
    
    /**
     * MANAGER: Limited administrative access
     * - Can manage cars and customers
     * - Can process rentals and returns
     * - Can view reports (but not salary details)
     * - Cannot manage employees
     */
    MANAGER("Manager"),
    
    /**
     * EMPLOYEE: Basic operational access
     * - Can view cars and customers
     * - Can process rentals and returns
     * - Cannot delete records
     * - Cannot view reports
     * - Cannot manage other employees
     */
    EMPLOYEE("Employee");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Convert string to UserRole enum
     */
    public static UserRole fromString(String role) {
        if (role == null) return EMPLOYEE; // Default role
        
        switch (role.toUpperCase()) {
            case "ADMIN":
                return ADMIN;
            case "MANAGER":
                return MANAGER;
            case "EMPLOYEE":
            default:
                return EMPLOYEE;
        }
    }
    
    /**
     * Check if this role has administrative privileges
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
    
    /**
     * Check if this role has manager or higher privileges
     */
    public boolean isManagerOrAbove() {
        return this == ADMIN || this == MANAGER;
    }
    
    /**
     * Check if this role can delete records
     */
    public boolean canDelete() {
        return this == ADMIN || this == MANAGER;
    }
    
    /**
     * Check if this role can view reports
     */
  
    
    
    /**
     * Check if this role can manage employees
     */
    public boolean canManageEmployees() {
        return this == ADMIN;
    }
    
    /**
     * Check if this role can view salary information
     */
    public boolean canViewSalary() {
        return this == ADMIN;
    }
    
    /**
     * Check if this role can view reports
     */
    public boolean canViewReports() {
        return this == ADMIN || this == MANAGER;
    }
}
