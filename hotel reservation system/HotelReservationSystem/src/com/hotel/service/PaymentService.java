package com.hotel.service;

import com.hotel.database.FileDatabase;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.util.DateTimeUtil;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.Random;

public class PaymentService {
    private final FileDatabase db;
    private final CustomerService customerService;

    public PaymentService(FileDatabase db, CustomerService customerService) {
        this.db = db;
        this.customerService = customerService;
    }

    public List<Payment> getAllPayments() {
        return db.getPayments();
    }

    public double calculateGst(double amount) {
        double rate = Double.parseDouble(db.getSettings().getOrDefault("gstRate", "18.0"));
        return (amount * rate) / 100.0;
    }

    public double applyCoupon(String couponCode, double subtotal) {
        if (couponCode == null) return 0.0;
        
        switch (couponCode.toUpperCase().trim()) {
            case "WELCOME10":
                return subtotal * 0.10; // 10% off
            case "SUMMER20":
                return subtotal * 0.20; // 20% off
            case "LOYAL50":
                return subtotal > 150 ? 50.0 : 0.0; // $50 off for orders over $150
            case "FESTIVE15":
                return subtotal * 0.15; // 15% off
            default:
                return 0.0;
        }
    }

    public String processPaymentSimulation(String bookingId, double baseAmount, String paymentMethod,
                                           String couponCode, boolean useLoyaltyPoints) {
        
        // 1. Calculate Discount
        double couponDiscount = applyCoupon(couponCode, baseAmount);
        double loyaltyDiscount = 0.0;
        
        Reservation booking = null;
        for (Reservation r : db.getBookings()) {
            if (r.getBookingId().equalsIgnoreCase(bookingId)) {
                booking = r;
                break;
            }
        }
        
        if (booking == null) {
            return "ERROR: Booking not found!";
        }

        // Apply loyalty point discount if requested (1 point = $0.10 discount, up to $20 max discount)
        if (useLoyaltyPoints) {
            var customer = customerService.getCustomerByUsername(booking.getCustomerId());
            if (customer != null) {
                int points = customer.getLoyaltyPoints();
                int pointsToUse = Math.min(points, 200); // Max 200 points ($20)
                loyaltyDiscount = pointsToUse * 0.10;
                customer.setLoyaltyPoints(points - pointsToUse);
            }
        }

        double totalDiscount = couponDiscount + loyaltyDiscount;
        double subtotal = baseAmount - totalDiscount;
        if (subtotal < 0) subtotal = 0;

        double gst = calculateGst(subtotal);
        double finalAmount = subtotal + gst;

        // Generate unique payment ID
        String paymentId = generateUniquePaymentId();

        // Save payment record
        Payment p = new Payment(paymentId, bookingId, finalAmount, paymentMethod, Payment.Status.COMPLETED,
                                gst, totalDiscount, DateTimeUtil.getCurrentDateString());
        
        db.getPayments().add(p);
        db.savePayments();

        // Reward customer with loyalty points (1 point per $10 spent)
        int pointsEarned = (int) (finalAmount / 10);
        customerService.addLoyaltyPoints(booking.getCustomerId(), pointsEarned);

        db.log("Payment processed: " + paymentId + " for booking " + bookingId + " via " + paymentMethod);
        
        // Generate Invoice file
        generateInvoiceFile(booking, p, baseAmount, couponDiscount, loyaltyDiscount, subtotal, gst, finalAmount);

        return "SUCCESS:" + paymentId + ":" + String.format("%.2f", finalAmount);
    }

    private void generateInvoiceFile(Reservation b, Payment p, double baseAmount, double couponDisc,
                                     double loyaltyDisc, double subtotal, double gst, double finalAmt) {
        try {
            Files.createDirectories(Paths.get("reports"));
            File invoiceFile = new File("reports", "invoice_" + b.getBookingId() + ".txt");
            
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(invoiceFile)))) {
                pw.println("==================================================");
                pw.println("          SMART HOTEL RESERVATION SYSTEM          ");
                pw.println("                 INVOICE RECEIPT                  ");
                pw.println("==================================================");
                pw.println("Invoice No:    INV-" + p.getPaymentId().substring(2));
                pw.println("Payment Ref:   " + p.getPaymentId());
                pw.println("Booking Ref:   " + b.getBookingId());
                pw.println("Date:          " + p.getDate());
                pw.println("Customer ID:   " + b.getCustomerId());
                pw.println("Room Number:   " + b.getRoomNumber());
                pw.println("Check-in:      " + b.getCheckInDate());
                pw.println("Check-out:     " + b.getCheckOutDate());
                pw.println("Guests:        " + b.getNumberOfGuests());
                pw.println("--------------------------------------------------");
                pw.printf("Base Room Charge:               $%10.2f\n", baseAmount);
                if (couponDisc > 0) {
                    pw.printf("Coupon Discount:               -$%10.2f\n", couponDisc);
                }
                if (loyaltyDisc > 0) {
                    pw.printf("Loyalty Point Discount:        -$%10.2f\n", loyaltyDisc);
                }
                pw.println("--------------------------------------------------");
                pw.printf("Subtotal:                       $%10.2f\n", subtotal);
                pw.printf("GST (18%%):                      $%10.2f\n", gst);
                pw.println("--------------------------------------------------");
                pw.printf("TOTAL AMOUNT PAID:              $%10.2f\n", finalAmt);
                pw.println("Payment Method:                 " + p.getMethod());
                pw.println("Payment Status:                 " + p.getStatus());
                pw.println("==================================================");
                pw.println("           Thank you for your stay!               ");
                pw.println("==================================================");
            }
            db.log("Invoice file generated: reports/invoice_" + b.getBookingId() + ".txt");
        } catch (IOException e) {
            db.log("Error generating invoice file: " + e.getMessage());
        }
    }

    private String generateUniquePaymentId() {
        Random rand = new Random();
        String id;
        do {
            id = String.format("PM%05d", rand.nextInt(100000));
        } while (paymentExists(id));
        return id;
    }

    private boolean paymentExists(String paymentId) {
        for (Payment p : db.getPayments()) {
            if (p.getPaymentId().equalsIgnoreCase(paymentId)) {
                return true;
            }
        }
        return false;
    }
}
