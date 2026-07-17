package com.hotel.service;

import com.hotel.database.FileDatabase;
import com.hotel.model.Admin;
import com.hotel.model.Customer;
import com.hotel.model.User;
import com.hotel.util.ValidationUtil;
import java.util.List;

public class CustomerService {
    private final FileDatabase db;

    public CustomerService(FileDatabase db) {
        this.db = db;
    }

    public List<Customer> getAllCustomers() {
        return db.getCustomers();
    }

    public Customer getCustomerByUsername(String username) {
        for (Customer c : db.getCustomers()) {
            if (c.getUsername().equalsIgnoreCase(username)) {
                return c;
            }
        }
        return null;
    }

    public Admin getAdminByUsername(String username) {
        for (Admin a : db.getAdmins()) {
            if (a.getUsername().equalsIgnoreCase(username)) {
                return a;
            }
        }
        return null;
    }

    public User authenticateUser(String username, String password, boolean isAdmin) {
        if (isAdmin) {
            Admin a = getAdminByUsername(username);
            if (a != null && a.getPassword().equals(password)) {
                return a;
            }
        } else {
            Customer c = getCustomerByUsername(username);
            if (c != null && c.getPassword().equals(password)) {
                return c;
            }
        }
        return null;
    }

    public String registerCustomer(String username, String password, String name, String email, String phone, String idProof) {
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || idProof.isEmpty()) {
            return "All fields are required!";
        }
        if (getCustomerByUsername(username) != null || getAdminByUsername(username) != null) {
            return "Username is already taken!";
        }
        if (!ValidationUtil.isValidPassword(password)) {
            return "Password must be at least 6 characters!";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Invalid email format!";
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            return "Invalid phone number format! (Must be 10-15 digits)";
        }

        Customer newCustomer = new Customer(username, password, name, email, phone, idProof, 50); // 50 sign-up bonus points
        db.getCustomers().add(newCustomer);
        db.saveCustomers();
        db.log("Customer registered: " + username);
        return "SUCCESS";
    }

    public String updateProfile(String username, String name, String email, String phone, String idProof, String newPassword) {
        Customer c = getCustomerByUsername(username);
        if (c == null) return "User not found!";

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || idProof.isEmpty()) {
            return "Profile fields cannot be empty!";
        }
        if (!ValidationUtil.isValidEmail(email)) {
            return "Invalid email format!";
        }
        if (!ValidationUtil.isValidPhone(phone)) {
            return "Invalid phone number format! (Must be 10-15 digits)";
        }
        if (!newPassword.isEmpty() && !ValidationUtil.isValidPassword(newPassword)) {
            return "New password must be at least 6 characters!";
        }

        c.setFullName(name);
        c.setEmail(email);
        c.setPhone(phone);
        c.setIdProof(idProof);
        if (!newPassword.isEmpty()) {
            c.setPassword(newPassword);
        }

        db.saveCustomers();
        db.log("Customer profile updated: " + username);
        return "SUCCESS";
    }

    public void addLoyaltyPoints(String username, int points) {
        Customer c = getCustomerByUsername(username);
        if (c != null) {
            c.addLoyaltyPoints(points);
            db.saveCustomers();
            db.log("Added " + points + " loyalty points to customer " + username);
        }
    }

    public boolean toggleWishlist(String username, int roomNumber) {
        Customer c = getCustomerByUsername(username);
        if (c != null) {
            if (c.isInWishlist(roomNumber)) {
                c.removeFromWishlist(roomNumber);
                db.saveCustomers();
                db.log("Removed room " + roomNumber + " from wishlist for " + username);
                return false; // Not in wishlist anymore
            } else {
                c.addToWishlist(roomNumber);
                db.saveCustomers();
                db.log("Added room " + roomNumber + " to wishlist for " + username);
                return true; // Added to wishlist
            }
        }
        return false;
    }

    public void deleteCustomer(String username) {
        Customer c = getCustomerByUsername(username);
        if (c != null) {
            db.getCustomers().remove(c);
            db.saveCustomers();
            db.log("Deleted customer: " + username);
        }
    }
}
