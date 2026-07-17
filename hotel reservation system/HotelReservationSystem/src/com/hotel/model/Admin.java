package com.hotel.model;

public class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin(String username, String password, String fullName) {
        super(username, password, fullName);
    }
}
