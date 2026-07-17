package com.hotel.service;

import com.hotel.database.FileDatabase;
import com.hotel.model.Payment;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.util.DateTimeUtil;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ReportService {
    private final FileDatabase db;

    public ReportService(FileDatabase db) {
        this.db = db;
    }

    public double calculateTotalRevenue() {
        return db.getPayments().stream()
                .filter(p -> p.getStatus() == Payment.Status.COMPLETED)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public int getBookedRoomsCount() {
        int count = 0;
        for (Room r : db.getRooms()) {
            if (r.getStatus() == Room.Status.BOOKED || r.getStatus() == Room.Status.RESERVED) {
                count++;
            }
        }
        return count;
    }

    public int getAvailableRoomsCount() {
        int count = 0;
        for (Room r : db.getRooms()) {
            if (r.getStatus() == Room.Status.AVAILABLE) {
                count++;
            }
        }
        return count;
    }

    public double calculateOccupancyPercentage() {
        int total = db.getRooms().size();
        if (total == 0) return 0.0;
        int booked = getBookedRoomsCount();
        return ((double) booked / total) * 100.0;
    }

    public String getMostPopularRoomType() {
        Map<String, Integer> typeCounts = new HashMap<>();
        for (Reservation res : db.getBookings()) {
            if (res.getStatus() == Reservation.Status.CONFIRMED || res.getStatus() == Reservation.Status.COMPLETED) {
                Room r = getRoomByNumber(res.getRoomNumber());
                if (r != null) {
                    typeCounts.put(r.getType(), typeCounts.getOrDefault(r.getType(), 0) + 1);
                }
            }
        }
        
        String popularType = "None";
        int max = 0;
        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                popularType = entry.getKey();
            }
        }
        return popularType + " (" + max + " bookings)";
    }

    private Room getRoomByNumber(int roomNumber) {
        for (Room r : db.getRooms()) {
            if (r.getRoomNumber() == roomNumber) return r;
        }
        return null;
    }

    public String getReportSummaryText() {
        StringBuilder sb = new StringBuilder();
        sb.append("================ HOTEL ANALYTICS REPORT ================\n");
        sb.append("Generated on: ").append(DateTimeUtil.getCurrentDateTimeString()).append("\n");
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Total Revenue:           $%10.2f\n", calculateTotalRevenue()));
        sb.append(String.format("Occupancy Rate:          %9.1f%%\n", calculateOccupancyPercentage()));
        sb.append(String.format("Total Rooms:             %10d\n", db.getRooms().size()));
        sb.append(String.format("Available Rooms:         %10d\n", getAvailableRoomsCount()));
        sb.append(String.format("Booked Rooms:            %10d\n", getBookedRoomsCount()));
        sb.append(String.format("Cancelled Bookings:      %10d\n", 
                db.getBookings().stream().filter(b -> b.getStatus() == Reservation.Status.CANCELLED).count()));
        sb.append("Most Popular Room Type:  ").append(getMostPopularRoomType()).append("\n");
        sb.append("====================================================\n");
        return sb.toString();
    }

    public String exportPdfReport() {
        return writeTextReport("report_" + System.currentTimeMillis() + ".pdf", "PDF");
    }

    public String exportExcelReport() {
        return writeTextReport("report_" + System.currentTimeMillis() + ".xlsx", "EXCEL");
    }

    private String writeTextReport(String filename, String formatType) {
        try {
            Files.createDirectories(Paths.get("reports"));
            File reportFile = new File("reports", filename);
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)))) {
                pw.println("Format: " + formatType + " Simulated Export");
                pw.println(getReportSummaryText());
                
                // Detailed bookings breakdown
                pw.println("\n--- DETAILED BOOKINGS LIST ---");
                pw.println("BookingID | CustomerID | RoomNum | CheckIn | CheckOut | Status | Cost");
                for (Reservation b : db.getBookings()) {
                    pw.printf("%s | %s | %d | %s | %s | %s | $%.2f\n",
                            b.getBookingId(), b.getCustomerId(), b.getRoomNumber(),
                            b.getCheckInDate(), b.getCheckOutDate(), b.getStatus(), b.getTotalCost());
                }
            }
            db.log("Exported " + formatType + " report to: reports/" + filename);
            return "SUCCESS:reports/" + filename;
        } catch (IOException e) {
            db.log("Failed to export report: " + e.getMessage());
            return "ERROR:" + e.getMessage();
        }
    }
}
