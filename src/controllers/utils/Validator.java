package controllers.utils;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validator {
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[0-9\\-\\+\\s\\(\\)]+$"
    );
    
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone).matches() && phone.length() >= 10;
    }
    
    public static boolean isValidPrice(double price) {
        return price > 0;
    }
    
    public static boolean isValidYear(int year) {
        int currentYear = LocalDate.now().getYear();
        return year >= 1900 && year <= currentYear + 1;
    }
    
    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return false;
        return !endDate.isBefore(startDate);
    }
    
    public static boolean isValidLicenseNumber(String license) {
        return !isEmpty(license) && license.length() >= 5;
    }
    
    public static boolean isValidRegistrationNumber(String regNumber) {
        return !isEmpty(regNumber) && regNumber.length() >= 4;
    }
}
