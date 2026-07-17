package com.hotel.model;

import java.io.Serializable;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        COMPLETED, FAILED, REFUNDED
    }

    private String paymentId;
    private String bookingId;
    private double amount;
    private String method; // Credit Card, Debit Card, UPI, Cash, Net Banking
    private Status status;
    private double gstAmount;
    private double discountAmount;
    private String date; // yyyy-MM-dd

    public Payment(String paymentId, String bookingId, double amount, String method, Status status,
                   double gstAmount, double discountAmount, String date) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.gstAmount = gstAmount;
        this.discountAmount = discountAmount;
        this.date = date;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public double getGstAmount() { return gstAmount; }
    public void setGstAmount(double gstAmount) { this.gstAmount = gstAmount; }

    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
