package com.hotel.model;

import java.io.Serializable;

public class Reservation implements Serializable, Comparable<Reservation> {
    private static final long serialVersionUID = 1L;

    public enum Status {
        CONFIRMED, CANCELLED, COMPLETED
    }

    private String bookingId;
    private String customerId;
    private int roomNumber;
    private String checkInDate; // yyyy-MM-dd
    private String checkOutDate; // yyyy-MM-dd
    private int numberOfGuests;
    private String specialRequests;
    private double totalCost;
    private Status status;

    public Reservation(String bookingId, String customerId, int roomNumber, String checkInDate,
                       String checkOutDate, int numberOfGuests, String specialRequests,
                       double totalCost, Status status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfGuests = numberOfGuests;
        this.specialRequests = specialRequests;
        this.totalCost = totalCost;
        this.status = status;
    }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public int getNumberOfGuests() { return numberOfGuests; }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests = numberOfGuests; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    @Override
    public int compareTo(Reservation other) {
        return this.checkInDate.compareTo(other.checkInDate);
    }
}
