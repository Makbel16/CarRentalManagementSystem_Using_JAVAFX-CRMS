package controllers.utils;

import models.UserRole;

/**
 * SessionManager - Singleton class to manage the current logged-in user session
 * Tracks the username, user ID, and role throughout the application
 */
public class SessionManager {
    
    private static SessionManager instance;
    
    private String currentUsername;
    private int currentUserId;
    private UserRole currentUserRole;
    private String currentUserFullName;
    
    // Private constructor for Singleton pattern
    private SessionManager() {
        this.currentUserRole = UserRole.EMPLOYEE; // Default role
    }
    
    /**
     * Get the singleton instance of SessionManager
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Initialize session after successful login
     */
    public void login(String username, int userId, UserRole role, String fullName) {
        this.currentUsername = username;
        this.currentUserId = userId;
        this.currentUserRole = role;
        this.currentUserFullName = fullName;
    }
    
    /**
     * Clear session on logout
     */
    public void logout() {
        this.currentUsername = null;
        this.currentUserId = 0;
        this.currentUserRole = UserRole.EMPLOYEE;
        this.currentUserFullName = null;
    }
    
    /**
     * Check if user is currently logged in
     */
    public boolean isLoggedIn() {
        return currentUsername != null && currentUserId > 0;
    }
    
    // Getters
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    public int getCurrentUserId() {
        return currentUserId;
    }
    
    public UserRole getCurrentUserRole() {
        return currentUserRole;
    }
    
    public String getCurrentUserFullName() {
        return currentUserFullName != null ? currentUserFullName : currentUsername;
    }
    
    // Permission check methods
    public boolean canDelete() {
        return currentUserRole != null && currentUserRole.canDelete();
    }
    
    public boolean canViewReports() {
        return currentUserRole != null && currentUserRole.canViewReports();
    }
    
    public boolean canManageEmployees() {
        return currentUserRole != null && currentUserRole.canManageEmployees();
    }
    
    public boolean canViewSalary() {
        return currentUserRole != null && currentUserRole.canViewSalary();
    }
    
    public boolean isAdmin() {
        return currentUserRole != null && currentUserRole.isAdmin();
    }
    
    public boolean isManagerOrAbove() {
        return currentUserRole != null && currentUserRole.isManagerOrAbove();
    }
    
    @Override
    public String toString() {
        return "Session[User=" + currentUsername + 
               ", Role=" + (currentUserRole != null ? currentUserRole.getDisplayName() : "None") + "]";
    }
}
