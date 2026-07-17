package com.hotel.service;

import com.hotel.database.FileDatabase;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.util.DateTimeUtil;
import com.hotel.util.ValidationUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ReservationService {
    private final FileDatabase db;
    private final RoomService roomService;

    public ReservationService(FileDatabase db, RoomService roomService) {
        this.db = db;
        this.roomService = roomService;
    }

    public List<Reservation> getAllBookings() {
        return db.getBookings();
    }

    public List<Reservation> getBookingsByCustomer(String customerId) {
        return db.getBookings().stream()
                .filter(b -> b.getCustomerId().equalsIgnoreCase(customerId))
                .collect(Collectors.toList());
    }

    public Reservation getBookingById(String bookingId) {
        for (Reservation r : db.getBookings()) {
            if (r.getBookingId().equalsIgnoreCase(bookingId)) {
                return r;
            }
        }
        return null;
    }

    // Room availability checker over a date range
    public boolean isRoomAvailable(int roomNumber, String checkIn, String checkOut) {
        Room room = roomService.getRoomByNumber(roomNumber);
        if (room == null || room.getStatus() == Room.Status.MAINTENANCE) {
            return false;
        }

        // Check if there are any overlapping bookings that are confirmed
        for (Reservation res : db.getBookings()) {
            if (res.getRoomNumber() == roomNumber && res.getStatus() == Reservation.Status.CONFIRMED) {
                if (DateTimeUtil.isDateOverlap(checkIn, checkOut, res.getCheckInDate(), res.getCheckOutDate())) {
                    return false;
                }
            }
        }
        return true;
    }

    // Create booking
    public String createBooking(String customerId, int roomNumber, String checkIn, String checkOut,
                                int guests, String specialRequests, double totalCost) {
        
        if (!ValidationUtil.isValidCheckInCheckOut(checkIn, checkOut)) {
            return "Invalid check-in/check-out dates! Dates must be today or in the future and check-out must be after check-in.";
        }

        Room room = roomService.getRoomByNumber(roomNumber);
        if (room == null) {
            return "Selected room does not exist.";
        }

        if (guests <= 0 || guests > room.getCapacity()) {
            return "Invalid number of guests! Maximum capacity for this room is " + room.getCapacity() + ".";
        }

        // Prevent booking unavailable rooms
        if (!isRoomAvailable(roomNumber, checkIn, checkOut)) {
            return "Room is not available for the selected dates.";
        }

        // Check for duplicate booking (same customer booking same room on same dates)
        for (Reservation res : db.getBookings()) {
            if (res.getCustomerId().equalsIgnoreCase(customerId) &&
                res.getRoomNumber() == roomNumber &&
                res.getStatus() == Reservation.Status.CONFIRMED &&
                res.getCheckInDate().equals(checkIn) &&
                res.getCheckOutDate().equals(checkOut)) {
                return "You already have a confirmed booking for this room on the same dates!";
            }
        }

        // Generate unique Booking ID
        String bookingId = generateUniqueBookingId();
        
        Reservation booking = new Reservation(bookingId, customerId, roomNumber, checkIn, checkOut,
                                              guests, specialRequests, totalCost, Reservation.Status.CONFIRMED);
        
        db.getBookings().add(booking);
        
        // Auto-update room status if check-in is today
        if (checkIn.equals(DateTimeUtil.getCurrentDateString())) {
            room.setStatus(Room.Status.BOOKED);
            db.saveRooms();
        }
        
        db.saveBookings();
        db.log("Booking created: " + bookingId + " by " + customerId + " for room " + roomNumber);
        
        return "SUCCESS:" + bookingId;
    }

    // Cancel Booking
    public boolean cancelBooking(String bookingId) {
        Reservation res = getBookingById(bookingId);
        if (res != null && res.getStatus() == Reservation.Status.CONFIRMED) {
            res.setStatus(Reservation.Status.CANCELLED);
            
            // If the room was marked booked, set it back to available
            Room r = roomService.getRoomByNumber(res.getRoomNumber());
            if (r != null && r.getStatus() == Room.Status.BOOKED) {
                // Check if there is any other current booking today
                String today = DateTimeUtil.getCurrentDateString();
                boolean bookedTodayByOther = false;
                for (Reservation other : db.getBookings()) {
                    if (other.getRoomNumber() == r.getRoomNumber() &&
                        other.getStatus() == Reservation.Status.CONFIRMED &&
                        !other.getBookingId().equals(bookingId) &&
                        today.compareTo(other.getCheckInDate()) >= 0 &&
                        today.compareTo(other.getCheckOutDate()) < 0) {
                        bookedTodayByOther = true;
                        break;
                    }
                }
                if (!bookedTodayByOther) {
                    r.setStatus(Room.Status.AVAILABLE);
                    db.saveRooms();
                }
            }
            db.saveBookings();
            db.log("Booking cancelled: " + bookingId);
            return true;
        }
        return false;
    }

    // Modify Booking dates
    public String modifyBooking(String bookingId, String newCheckIn, String newCheckOut, int newGuests) {
        Reservation res = getBookingById(bookingId);
        if (res == null) return "Booking not found!";
        if (res.getStatus() != Reservation.Status.CONFIRMED) return "Only active, confirmed bookings can be modified!";

        if (!ValidationUtil.isValidCheckInCheckOut(newCheckIn, newCheckOut)) {
            return "Invalid check-in/check-out dates.";
        }

        Room room = roomService.getRoomByNumber(res.getRoomNumber());
        if (room == null) return "Room does not exist.";
        if (newGuests <= 0 || newGuests > room.getCapacity()) {
            return "Invalid number of guests! Maximum capacity for this room is " + room.getCapacity() + ".";
        }

        // Check availability, excluding THIS booking's current reservation
        boolean available = true;
        for (Reservation other : db.getBookings()) {
            if (other.getRoomNumber() == res.getRoomNumber() &&
                other.getStatus() == Reservation.Status.CONFIRMED &&
                !other.getBookingId().equals(bookingId)) {
                if (DateTimeUtil.isDateOverlap(newCheckIn, newCheckOut, other.getCheckInDate(), other.getCheckOutDate())) {
                    available = false;
                    break;
                }
            }
        }

        if (!available) {
            return "The room is not available for the newly requested dates.";
        }

        // Update dates & cost
        res.setCheckInDate(newCheckIn);
        res.setCheckOutDate(newCheckOut);
        res.setNumberOfGuests(newGuests);
        
        long days = DateTimeUtil.calculateDaysBetween(newCheckIn, newCheckOut);
        res.setTotalCost(days * room.getPrice());

        db.saveBookings();
        db.log("Booking modified: " + bookingId + " (New Dates: " + newCheckIn + " to " + newCheckOut + ")");
        return "SUCCESS";
    }

    private String generateUniqueBookingId() {
        Random rand = new Random();
        String id;
        do {
            id = String.format("BK%05d", rand.nextInt(100000));
        } while (getBookingById(id) != null);
        return id;
    }
}
