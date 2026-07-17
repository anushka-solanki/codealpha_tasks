package com.hotel.database;

import com.hotel.model.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileDatabase {
    private static final String DATA_DIR = "data";
    private static final String BACKUP_DIR = "data/backup";

    private final File adminsFile = new File(DATA_DIR, "admins.txt");
    private final File roomsFile = new File(DATA_DIR, "rooms.txt");
    private final File customersFile = new File(DATA_DIR, "customers.txt");
    private final File bookingsFile = new File(DATA_DIR, "bookings.txt");
    private final File paymentsFile = new File(DATA_DIR, "payments.txt");
    private final File feedbackFile = new File(DATA_DIR, "feedback.txt");
    private final File settingsFile = new File(DATA_DIR, "settings.txt");
    private final File logsFile = new File(DATA_DIR, "logs.txt");

    private List<Admin> admins = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Reservation> bookings = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();
    private List<Feedback> feedbacks = new ArrayList<>();
    private Map<String, String> settings = new HashMap<>();

    public FileDatabase() {
        createDirectoryStructure();
        loadAllData();
    }

    private void createDirectoryStructure() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(BACKUP_DIR));
        } catch (IOException e) {
            System.err.println("Could not create directories: " + e.getMessage());
        }
    }

    public void loadAllData() {
        admins = loadAdmins();
        rooms = loadRooms();
        customers = loadCustomers();
        bookings = loadBookings();
        payments = loadPayments();
        feedbacks = loadFeedback();
        settings = loadSettings();
        log("System preloaded data successfully.");
    }

    public void saveAllData() {
        saveAdmins();
        saveRooms();
        saveCustomers();
        saveBookings();
        savePayments();
        saveFeedback();
        saveSettings();
    }

    // --- LOGS ---
    public void log(String message) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logsFile, true)))) {
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            out.println(timestamp + " [INFO] " + message);
        } catch (IOException e) {
            System.err.println("Log error: " + e.getMessage());
        }
    }

    public List<String> readLogs() {
        List<String> logs = new ArrayList<>();
        if (!logsFile.exists()) return logs;
        try (BufferedReader br = new BufferedReader(new FileReader(logsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                logs.add(line);
            }
        } catch (IOException e) {
            System.err.println("Read logs error: " + e.getMessage());
        }
        return logs;
    }

    // --- ADMINS ---
    private List<Admin> loadAdmins() {
        List<Admin> list = new ArrayList<>();
        if (!adminsFile.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(adminsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    list.add(new Admin(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            log("Error loading admins: " + e.getMessage());
        }
        return list;
    }

    public void saveAdmins() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(adminsFile)))) {
            for (Admin a : admins) {
                pw.println(a.getUsername() + "," + a.getPassword() + "," + a.getFullName());
            }
        } catch (IOException e) {
            log("Error saving admins: " + e.getMessage());
        }
    }

    // --- ROOMS ---
    private List<Room> loadRooms() {
        List<Room> list = new ArrayList<>();
        if (!roomsFile.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(roomsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 13) {
                    int num = Integer.parseInt(parts[0]);
                    String type = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    int cap = Integer.parseInt(parts[3]);
                    Room.Status status = Room.Status.valueOf(parts[4]);
                    boolean ac = Boolean.parseBoolean(parts[5]);
                    boolean wifi = Boolean.parseBoolean(parts[6]);
                    boolean tv = Boolean.parseBoolean(parts[7]);
                    boolean breakfast = Boolean.parseBoolean(parts[8]);
                    boolean pool = Boolean.parseBoolean(parts[9]);
                    boolean parking = Boolean.parseBoolean(parts[10]);
                    double rating = Double.parseDouble(parts[11]);
                    int reviews = Integer.parseInt(parts[12]);
                    
                    list.add(new Room(num, type, price, cap, status, ac, wifi, tv, breakfast, pool, parking, rating, reviews));
                }
            }
        } catch (Exception e) {
            log("Error loading rooms: " + e.getMessage());
        }
        return list;
    }

    public void saveRooms() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(roomsFile)))) {
            for (Room r : rooms) {
                pw.println(r.getRoomNumber() + "," +
                           r.getType() + "," +
                           r.getPrice() + "," +
                           r.getCapacity() + "," +
                           r.getStatus().name() + "," +
                           r.isAc() + "," +
                           r.hasWifi() + "," +
                           r.hasTv() + "," +
                           r.hasBreakfast() + "," +
                           r.hasPool() + "," +
                           r.hasParking() + "," +
                           r.getRating() + "," +
                           r.getReviewsCount());
            }
        } catch (IOException e) {
            log("Error saving rooms: " + e.getMessage());
        }
    }

    // --- CUSTOMERS ---
    private List<Customer> loadCustomers() {
        List<Customer> list = new ArrayList<>();
        if (!customersFile.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(customersFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 7) {
                    String username = parts[0];
                    String password = parts[1];
                    String name = parts[2];
                    String email = parts[3];
                    String phone = parts[4];
                    String idProof = parts[5];
                    int points = Integer.parseInt(parts[6]);
                    
                    Customer c = new Customer(username, password, name, email, phone, idProof, points);
                    
                    // Wishlist parsing (semi-colon separated integers)
                    if (parts.length >= 8 && !parts[7].trim().isEmpty()) {
                        String[] wish = parts[7].split(";");
                        for (String w : wish) {
                            try {
                                c.addToWishlist(Integer.parseInt(w));
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                    list.add(c);
                }
            }
        } catch (Exception e) {
            log("Error loading customers: " + e.getMessage());
        }
        return list;
    }

    public void saveCustomers() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(customersFile)))) {
            for (Customer c : customers) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < c.getWishlistRooms().size(); i++) {
                    sb.append(c.getWishlistRooms().get(i));
                    if (i < c.getWishlistRooms().size() - 1) {
                        sb.append(";");
                    }
                }
                pw.println(c.getUsername() + "," +
                           c.getPassword() + "," +
                           c.getFullName() + "," +
                           c.getEmail() + "," +
                           c.getPhone() + "," +
                           c.getIdProof() + "," +
                           c.getLoyaltyPoints() + "," +
                           sb.toString());
            }
        } catch (IOException e) {
            log("Error saving customers: " + e.getMessage());
        }
    }

    // --- BOOKINGS ---
    private List<Reservation> loadBookings() {
        List<Reservation> list = new ArrayList<>();
        if (!bookingsFile.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(bookingsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 9) {
                    String bid = parts[0];
                    String cid = parts[1];
                    int roomNum = Integer.parseInt(parts[2]);
                    String cin = parts[3];
                    String cout = parts[4];
                    int guests = Integer.parseInt(parts[5]);
                    String reqs = parts[6].replace("%%", ","); // un-escape commas
                    double cost = Double.parseDouble(parts[7]);
                    Reservation.Status status = Reservation.Status.valueOf(parts[8]);
                    
                    list.add(new Reservation(bid, cid, roomNum, cin, cout, guests, reqs, cost, status));
                }
            }
        } catch (Exception e) {
            log("Error loading bookings: " + e.getMessage());
        }
        return list;
    }

    public void saveBookings() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(bookingsFile)))) {
            for (Reservation b : bookings) {
                String safeReqs = b.getSpecialRequests().replace(",", "%%"); // escape commas
                pw.println(b.getBookingId() + "," +
                           b.getCustomerId() + "," +
                           b.getRoomNumber() + "," +
                           b.getCheckInDate() + "," +
                           b.getCheckOutDate() + "," +
                           b.getNumberOfGuests() + "," +
                           safeReqs + "," +
                           b.getTotalCost() + "," +
                           b.getStatus().name());
            }
        } catch (IOException e) {
            log("Error saving bookings: " + e.getMessage());
        }
    }

    // --- PAYMENTS ---
    private List<Payment> loadPayments() {
        List<Payment> list = new ArrayList<>();
        if (!paymentsFile.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(paymentsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 8) {
                    String pid = parts[0];
                    String bid = parts[1];
                    double amt = Double.parseDouble(parts[2]);
                    String method = parts[3];
                    Payment.Status status = Payment.Status.valueOf(parts[4]);
                    double gst = Double.parseDouble(parts[5]);
                    double disc = Double.parseDouble(parts[6]);
                    String date = parts[7];
                    
                    list.add(new Payment(pid, bid, amt, method, status, gst, disc, date));
                }
            }
        } catch (Exception e) {
            log("Error loading payments: " + e.getMessage());
        }
        return list;
    }

    public void savePayments() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(paymentsFile)))) {
            for (Payment p : payments) {
                pw.println(p.getPaymentId() + "," +
                           p.getBookingId() + "," +
                           p.getAmount() + "," +
                           p.getMethod() + "," +
                           p.getStatus().name() + "," +
                           p.getGstAmount() + "," +
                           p.getDiscountAmount() + "," +
                           p.getDate());
            }
        } catch (IOException e) {
            log("Error saving payments: " + e.getMessage());
        }
    }

    // --- FEEDBACK ---
    private List<Feedback> loadFeedback() {
        List<Feedback> list = new ArrayList<>();
        if (!feedbackFile.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(feedbackFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 6) {
                    int fid = Integer.parseInt(parts[0]);
                    int rnum = Integer.parseInt(parts[1]);
                    String name = parts[2];
                    int rating = Integer.parseInt(parts[3]);
                    String comment = parts[4].replace("%%", ","); // un-escape
                    String date = parts[5];
                    
                    list.add(new Feedback(fid, rnum, name, rating, comment, date));
                }
            }
        } catch (Exception e) {
            log("Error loading feedback: " + e.getMessage());
        }
        return list;
    }

    public void saveFeedback() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(feedbackFile)))) {
            for (Feedback f : feedbacks) {
                String safeComment = f.getComment().replace(",", "%%");
                pw.println(f.getFeedbackId() + "," +
                           f.getRoomNumber() + "," +
                           f.getCustomerName() + "," +
                           f.getRating() + "," +
                           safeComment + "," +
                           f.getDate());
            }
        } catch (IOException e) {
            log("Error saving feedback: " + e.getMessage());
        }
    }

    // --- SETTINGS ---
    private Map<String, String> loadSettings() {
        Map<String, String> map = new HashMap<>();
        if (!settingsFile.exists()) {
            // Write defaults
            map.put("theme", "dark");
            map.put("autoBackup", "true");
            map.put("gstRate", "18.0");
            map.put("loyaltyPointsRate", "10");
            return map;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(settingsFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains("=")) continue;
                String[] parts = line.split("=", 2);
                map.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            log("Error loading settings: " + e.getMessage());
        }
        return map;
    }

    public void saveSettings() {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(settingsFile)))) {
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                pw.println(entry.getKey() + "=" + entry.getValue());
            }
        } catch (IOException e) {
            log("Error saving settings: " + e.getMessage());
        }
    }

    // --- BACKUP & RESTORE ---
    public boolean backupData(String filenameSuffix) {
        try {
            copyFile(adminsFile, new File(BACKUP_DIR, "admins_" + filenameSuffix + ".txt"));
            copyFile(roomsFile, new File(BACKUP_DIR, "rooms_" + filenameSuffix + ".txt"));
            copyFile(customersFile, new File(BACKUP_DIR, "customers_" + filenameSuffix + ".txt"));
            copyFile(bookingsFile, new File(BACKUP_DIR, "bookings_" + filenameSuffix + ".txt"));
            copyFile(paymentsFile, new File(BACKUP_DIR, "payments_" + filenameSuffix + ".txt"));
            copyFile(feedbackFile, new File(BACKUP_DIR, "feedback_" + filenameSuffix + ".txt"));
            copyFile(settingsFile, new File(BACKUP_DIR, "settings_" + filenameSuffix + ".txt"));
            log("Backup created successfully with suffix: " + filenameSuffix);
            return true;
        } catch (IOException e) {
            log("Backup failed: " + e.getMessage());
            return false;
        }
    }

    public boolean restoreData(String filenameSuffix) {
        try {
            copyFile(new File(BACKUP_DIR, "admins_" + filenameSuffix + ".txt"), adminsFile);
            copyFile(new File(BACKUP_DIR, "rooms_" + filenameSuffix + ".txt"), roomsFile);
            copyFile(new File(BACKUP_DIR, "customers_" + filenameSuffix + ".txt"), customersFile);
            copyFile(new File(BACKUP_DIR, "bookings_" + filenameSuffix + ".txt"), bookingsFile);
            copyFile(new File(BACKUP_DIR, "payments_" + filenameSuffix + ".txt"), paymentsFile);
            copyFile(new File(BACKUP_DIR, "feedback_" + filenameSuffix + ".txt"), feedbackFile);
            copyFile(new File(BACKUP_DIR, "settings_" + filenameSuffix + ".txt"), settingsFile);
            
            // Reload into memory
            loadAllData();
            log("Backup restored successfully from suffix: " + filenameSuffix);
            return true;
        } catch (IOException e) {
            log("Restore failed: " + e.getMessage());
            return false;
        }
    }

    private void copyFile(File src, File dest) throws IOException {
        if (!src.exists()) return;
        Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // Getters for Collections
    public List<Admin> getAdmins() { return admins; }
    public List<Room> getRooms() { return rooms; }
    public List<Customer> getCustomers() { return customers; }
    public List<Reservation> getBookings() { return bookings; }
    public List<Payment> getPayments() { return payments; }
    public List<Feedback> getFeedbacks() { return feedbacks; }
    public Map<String, String> getSettings() { return settings; }
}
