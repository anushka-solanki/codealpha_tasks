package com.hotel.model;

import java.io.Serializable;

public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;

    private int feedbackId;
    private int roomNumber;
    private String customerName;
    private int rating; // 1-5 stars
    private String comment;
    private String date;

    public Feedback(int feedbackId, int roomNumber, String customerName, int rating, String comment, String date) {
        this.feedbackId = feedbackId;
        this.roomNumber = roomNumber;
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public int getFeedbackId() { return feedbackId; }
    public void setFeedbackId(int feedbackId) { this.feedbackId = feedbackId; }

    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
