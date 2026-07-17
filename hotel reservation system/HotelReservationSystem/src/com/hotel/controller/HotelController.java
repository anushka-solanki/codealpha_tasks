package com.hotel.controller;

import com.hotel.database.FileDatabase;
import com.hotel.model.*;
import com.hotel.service.*;
import java.util.List;

public class HotelController {
    private final FileDatabase db;
    private final RoomService roomService;
    private final CustomerService customerService;
    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ReportService reportService;
    private final BackupService backupService;

    private User loggedInUser = null;
    private boolean isAdminSession = false;

    public HotelController() {
        this.db = new FileDatabase();
        this.roomService = new RoomService(db);
        this.customerService = new CustomerService(db);
        this.reservationService = new ReservationService(db, roomService);
        this.paymentService = new PaymentService(db, customerService);
        this.reportService = new ReportService(db);
        this.backupService = new BackupService(db);
    }

    public boolean login(String username, String password, boolean isAdmin) {
        User user = customerService.authenticateUser(username, password, isAdmin);
        if (user != null) {
            this.loggedInUser = user;
            this.isAdminSession = isAdmin;
            db.log((isAdmin ? "Admin" : "Customer") + " logged in: " + username);
            return true;
        }
        return false;
    }

    public void logout() {
        if (loggedInUser != null) {
            db.log("User logged out: " + loggedInUser.getUsername());
        }
        this.loggedInUser = null;
        this.isAdminSession = false;
        
        // Auto-backup on logout
        backupService.performAutoBackup();
    }

    public String simulateForgotPassword(String username, boolean isAdmin) {
        if (isAdmin) {
            Admin a = customerService.getAdminByUsername(username);
            if (a != null) {
                db.log("Password reset simulated for Admin: " + username);
                return "SUCCESS: A simulation reset link sent to registered admin logs. [Recovery Code: " + (1000 + new java.util.Random().nextInt(9000)) + "]";
            }
        } else {
            Customer c = customerService.getCustomerByUsername(username);
            if (c != null) {
                db.log("Password reset simulated for Customer: " + username);
                return "SUCCESS: Code sent to " + c.getEmail() + "! [Reset OTP: " + (1000 + new java.util.Random().nextInt(9000)) + "]";
            }
        }
        return "ERROR: Username not found!";
    }

    // Direct access to sub-services
    public RoomService getRoomService() { return roomService; }
    public CustomerService getCustomerService() { return customerService; }
    public ReservationService getReservationService() { return reservationService; }
    public PaymentService getPaymentService() { return paymentService; }
    public ReportService getReportService() { return reportService; }
    public BackupService getBackupService() { return backupService; }
    public FileDatabase getDatabase() { return db; }

    public User getLoggedInUser() { return loggedInUser; }
    public boolean isAdminSession() { return isAdminSession; }

    public void saveAllData() {
        db.saveAllData();
    }
}
