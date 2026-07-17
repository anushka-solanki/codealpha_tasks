package com.hotel.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User {
    private static final long serialVersionUID = 1L;
    
    private String email;
    private String phone;
    private String idProof;
    private int loyaltyPoints;
    private List<Integer> wishlistRooms;

    public Customer(String username, String password, String fullName, String email, String phone, String idProof, int loyaltyPoints) {
        super(username, password, fullName);
        this.email = email;
        this.phone = phone;
        this.idProof = idProof;
        this.loyaltyPoints = loyaltyPoints;
        this.wishlistRooms = new ArrayList<>();
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdProof() { return idProof; }
    public void setIdProof(String idProof) { this.idProof = idProof; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    public void addLoyaltyPoints(int points) { this.loyaltyPoints += points; }

    public List<Integer> getWishlistRooms() { return wishlistRooms; }
    public void setWishlistRooms(List<Integer> wishlistRooms) { this.wishlistRooms = wishlistRooms; }

    public void addToWishlist(int roomNumber) {
        if (!wishlistRooms.contains(roomNumber)) {
            wishlistRooms.add(roomNumber);
        }
    }

    public void removeFromWishlist(int roomNumber) {
        wishlistRooms.remove(Integer.valueOf(roomNumber));
    }
    
    public boolean isInWishlist(int roomNumber) {
        return wishlistRooms.contains(roomNumber);
    }
}
