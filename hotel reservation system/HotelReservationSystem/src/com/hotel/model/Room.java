package com.hotel.model;

import java.io.Serializable;

public class Room implements Serializable, Comparable<Room> {
    private static final long serialVersionUID = 1L;

    public enum Status {
        AVAILABLE, BOOKED, MAINTENANCE, RESERVED
    }

    private int roomNumber;
    private String type; // Standard, Deluxe, Suite, Executive, Presidential Suite
    private double price;
    private int capacity;
    private Status status;
    
    // Amenities
    private boolean isAc;
    private boolean hasWifi;
    private boolean hasTv;
    private boolean hasBreakfast;
    private boolean hasPool;
    private boolean hasParking;

    // Rating
    private double rating;
    private int reviewsCount;

    public Room(int roomNumber, String type, double price, int capacity, Status status,
                boolean isAc, boolean hasWifi, boolean hasTv, boolean hasBreakfast,
                boolean hasPool, boolean hasParking, double rating, int reviewsCount) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.capacity = capacity;
        this.status = status;
        this.isAc = isAc;
        this.hasWifi = hasWifi;
        this.hasTv = hasTv;
        this.hasBreakfast = hasBreakfast;
        this.hasPool = hasPool;
        this.hasParking = hasParking;
        this.rating = rating;
        this.reviewsCount = reviewsCount;
    }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public boolean isAc() { return isAc; }
    public void setAc(boolean ac) { isAc = ac; }

    public boolean hasWifi() { return hasWifi; }
    public void setWifi(boolean hasWifi) { this.hasWifi = hasWifi; }

    public boolean hasTv() { return hasTv; }
    public void setTv(boolean hasTv) { this.hasTv = hasTv; }

    public boolean hasBreakfast() { return hasBreakfast; }
    public void setBreakfast(boolean hasBreakfast) { this.hasBreakfast = hasBreakfast; }

    public boolean hasPool() { return hasPool; }
    public void setPool(boolean hasPool) { this.hasPool = hasPool; }

    public boolean hasParking() { return hasParking; }
    public void setParking(boolean hasParking) { this.hasParking = hasParking; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReviewsCount() { return reviewsCount; }
    public void setReviewsCount(int reviewsCount) { this.reviewsCount = reviewsCount; }

    public void addReview(int ratingScore) {
        double totalRating = this.rating * this.reviewsCount;
        this.reviewsCount++;
        this.rating = (totalRating + ratingScore) / this.reviewsCount;
    }

    @Override
    public int compareTo(Room other) {
        // Default comparison by price
        return Double.compare(this.price, other.price);
    }
}
