package com.hotel.util;

import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        // Strip out spaces, dashes, parentheses
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        return PHONE_PATTERN.matcher(cleanPhone).matches();
    }

    public static boolean isValidPassword(String password) {
        if (password == null) return false;
        // Password must be at least 6 characters
        return password.length() >= 6;
    }

    public static boolean isValidDate(String dateStr) {
        if (dateStr == null) return false;
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isValidCheckInCheckOut(String checkIn, String checkOut) {
        if (!isValidDate(checkIn) || !isValidDate(checkOut)) return false;
        try {
            LocalDate in = LocalDate.parse(checkIn);
            LocalDate out = LocalDate.parse(checkOut);
            LocalDate today = LocalDate.now();

            // Check-in must be today or in the future
            if (in.isBefore(today)) {
                return false;
            }
            // Check-out must be after check-in
            return out.isAfter(in);
        } catch (Exception e) {
            return false;
        }
    }
}
