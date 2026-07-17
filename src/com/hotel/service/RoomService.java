package com.hotel.service;

import com.hotel.database.FileDatabase;
import com.hotel.model.Room;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoomService {
    private final FileDatabase db;

    public RoomService(FileDatabase db) {
        this.db = db;
    }

    public List<Room> getAllRooms() {
        return db.getRooms();
    }

    public Room getRoomByNumber(int roomNumber) {
        for (Room r : db.getRooms()) {
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }

    public void updateRoomStatus(int roomNumber, Room.Status status) {
        Room r = getRoomByNumber(roomNumber);
        if (r != null) {
            r.setStatus(status);
            db.saveRooms();
            db.log("Room " + roomNumber + " status updated to " + status);
        }
    }

    public void addRoom(Room r) {
        if (getRoomByNumber(r.getRoomNumber()) == null) {
            db.getRooms().add(r);
            db.saveRooms();
            db.log("Added new room: " + r.getRoomNumber());
        }
    }

    public void deleteRoom(int roomNumber) {
        Room r = getRoomByNumber(roomNumber);
        if (r != null) {
            db.getRooms().remove(r);
            db.saveRooms();
            db.log("Deleted room: " + roomNumber);
        }
    }

    // Advanced search & filtering
    public List<Room> searchAndFilterRooms(String searchQuery, String typeFilter, String statusFilter,
                                          Boolean acFilter, Boolean wifiFilter, Boolean tvFilter,
                                          Boolean breakfastFilter, Boolean poolFilter, Boolean parkingFilter,
                                          String sortBy) {
        
        List<Room> result = new ArrayList<>(db.getRooms());

        // 1. Search Query (matches Room Number or Type)
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String query = searchQuery.trim().toLowerCase();
            result = result.stream().filter(r -> 
                String.valueOf(r.getRoomNumber()).contains(query) || 
                r.getType().toLowerCase().contains(query)
            ).collect(Collectors.toList());
        }

        // 2. Type Filter
        if (typeFilter != null && !typeFilter.equals("All Types") && !typeFilter.isEmpty()) {
            result = result.stream().filter(r -> r.getType().equalsIgnoreCase(typeFilter)).collect(Collectors.toList());
        }

        // 3. Status Filter
        if (statusFilter != null && !statusFilter.equals("All Statuses") && !statusFilter.isEmpty()) {
            result = result.stream().filter(r -> r.getStatus().name().equalsIgnoreCase(statusFilter)).collect(Collectors.toList());
        }

        // 4. Amenities Filters
        if (acFilter != null && acFilter) {
            result = result.stream().filter(Room::isAc).collect(Collectors.toList());
        }
        if (wifiFilter != null && wifiFilter) {
            result = result.stream().filter(Room::hasWifi).collect(Collectors.toList());
        }
        if (tvFilter != null && tvFilter) {
            result = result.stream().filter(Room::hasTv).collect(Collectors.toList());
        }
        if (breakfastFilter != null && breakfastFilter) {
            result = result.stream().filter(Room::hasBreakfast).collect(Collectors.toList());
        }
        if (poolFilter != null && poolFilter) {
            result = result.stream().filter(Room::hasPool).collect(Collectors.toList());
        }
        if (parkingFilter != null && parkingFilter) {
            result = result.stream().filter(Room::hasParking).collect(Collectors.toList());
        }

        // 5. Sorting
        if (sortBy != null) {
            switch (sortBy) {
                case "Price: Low to High":
                    Collections.sort(result); // Uses Comparable (price)
                    break;
                case "Price: High to Low":
                    Collections.sort(result);
                    Collections.reverse(result);
                    break;
                case "Room Number":
                    result.sort((r1, r2) -> Integer.compare(r1.getRoomNumber(), r2.getRoomNumber()));
                    break;
                case "Rating":
                    result.sort((r1, r2) -> Double.compare(r2.getRating(), r1.getRating())); // Descending
                    break;
            }
        }

        return result;
    }

    public List<String> getSearchSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) return Collections.emptyList();
        String q = query.trim().toLowerCase();
        List<String> suggestions = new ArrayList<>();
        
        for (Room r : db.getRooms()) {
            String roomNumStr = String.valueOf(r.getRoomNumber());
            if (roomNumStr.startsWith(q)) {
                suggestions.add(roomNumStr + " - " + r.getType() + " ($" + r.getPrice() + ")");
            }
        }
        return suggestions;
    }
}
