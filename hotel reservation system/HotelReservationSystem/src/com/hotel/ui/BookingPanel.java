package com.hotel.ui;

import com.hotel.controller.HotelController;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import com.hotel.util.DateTimeUtil;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class BookingPanel extends JPanel {
    private final HotelController controller;
    private final MainFrame parent;

    // Booking Form
    private JTextField txtRoomNum;
    private JTextField txtCheckIn;
    private JTextField txtCheckOut;
    private JTextField txtGuests;
    private JTextField txtSpecialReq;
    private JTextField txtCoupon;
    
    // Bookings Table
    private JTable tblBookings;
    private DefaultTableModel tableModel;

    public BookingPanel(HotelController controller, MainFrame parent) {
        this.controller = controller;
        this.parent = parent;

        setLayout(new BorderLayout(20, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Booking & Reservations Panel");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.getTextPrimary());
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Split Layout: Left Form, Right List
        JPanel splitPanel = new JPanel(new GridBagLayout());
        splitPanel.setOpaque(false);
        add(splitPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Left Card - Booking Form
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        gbc.insets = new Insets(10, 0, 10, 10);
        RoundedPanel formCard = new RoundedPanel(15);
        formCard.setBackground(ThemeManager.getCardBackground());
        formCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        formCard.setLayout(new GridBagLayout());
        setupBookingForm(formCard);
        splitPanel.add(formCard, gbc);

        // Right Card - Reservations List Table
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(10, 10, 10, 0);
        RoundedPanel listCard = new RoundedPanel(15);
        listCard.setBackground(ThemeManager.getCardBackground());
        listCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        listCard.setLayout(new BorderLayout());
        setupBookingsTable(listCard);
        splitPanel.add(listCard, gbc);

        // Load Initial Table Data
        refreshData();
    }

    private void setupBookingForm(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel lblTitle = new JLabel("Reserve a Room");
        lblTitle.setFont(ThemeManager.FONT_HEADER);
        lblTitle.setForeground(ThemeManager.getTextPrimary());
        gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        // Fields Setup
        txtRoomNum = new JTextField();
        txtCheckIn = new JTextField(DateTimeUtil.getCurrentDateString());
        txtCheckOut = new JTextField(DateTimeUtil.getCurrentDateString());
        txtGuests = new JTextField("1");
        txtSpecialReq = new JTextField();
        txtCoupon = new JTextField();

        addFormLabelAndField(panel, "Room Number", txtRoomNum, 1, gbc);
        addFormLabelAndField(panel, "Check-In Date (yyyy-MM-dd)", txtCheckIn, 3, gbc);
        addFormLabelAndField(panel, "Check-Out Date (yyyy-MM-dd)", txtCheckOut, 5, gbc);
        addFormLabelAndField(panel, "Number of Guests", txtGuests, 7, gbc);
        addFormLabelAndField(panel, "Special Requests", txtSpecialReq, 9, gbc);
        addFormLabelAndField(panel, "Coupon Code (e.g. WELCOME10)", txtCoupon, 11, gbc);

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setOpaque(false);

        CustomButton btnCheck = new CustomButton("Check", new Color(59, 130, 246), new Color(29, 78, 216));
        btnCheck.setRadius(6);
        btnCheck.addActionListener(e -> checkAvailabilityAction());
        actionPanel.add(btnCheck);

        CustomButton btnBook = new CustomButton("Book & Pay");
        btnBook.setRadius(6);
        btnBook.addActionListener(e -> executeBookingAction());
        actionPanel.add(btnBook);

        gbc.gridy = 13;
        gbc.insets = new Insets(15, 0, 0, 0);
        panel.add(actionPanel, gbc);
    }

    private void addFormLabelAndField(JPanel panel, String text, JTextField field, int gridy, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(ThemeManager.FONT_SMALL);
        label.setForeground(ThemeManager.getTextSecondary());
        gbc.gridy = gridy;
        gbc.insets = new Insets(0, 0, 2, 0);
        panel.add(label, gbc);

        field.setBackground(ThemeManager.getBackground());
        field.setForeground(ThemeManager.getTextPrimary());
        field.setCaretColor(ThemeManager.getTextPrimary());
        field.setFont(ThemeManager.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        gbc.gridy = gridy + 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(field, gbc);
    }

    private void setupBookingsTable(JPanel panel) {
        JLabel lblTitle = new JLabel("Reservation History / Activity Log");
        lblTitle.setFont(ThemeManager.FONT_HEADER);
        lblTitle.setForeground(ThemeManager.getTextPrimary());
        panel.add(lblTitle, BorderLayout.NORTH);

        String[] cols = {"Booking ID", "Room", "Check In", "Check Out", "Guests", "Total Cost", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tblBookings = new JTable(tableModel);
        tblBookings.setFont(ThemeManager.FONT_BODY);
        tblBookings.setRowHeight(24);
        tblBookings.getTableHeader().setFont(ThemeManager.FONT_HEADER);
        tblBookings.getTableHeader().setReorderingAllowed(false);

        JScrollPane scroll = new JScrollPane(tblBookings);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        panel.add(scroll, BorderLayout.CENTER);

        // Footer Actions
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setOpaque(false);

        CustomButton btnCancel = new CustomButton("Cancel Booking", ThemeManager.STATUS_BOOKED, new Color(220, 38, 38));
        btnCancel.setRadius(6);
        btnCancel.setPreferredSize(new Dimension(130, 30));
        btnCancel.addActionListener(e -> cancelBookingAction());
        footer.add(btnCancel);

        CustomButton btnInvoice = new CustomButton("Simulate Invoice", ThemeManager.COLOR_ACCENT, ThemeManager.COLOR_ACCENT_HOVER);
        btnInvoice.setRadius(6);
        btnInvoice.setPreferredSize(new Dimension(130, 30));
        btnInvoice.addActionListener(e -> printInvoiceAction());
        footer.add(btnInvoice);

        panel.add(footer, BorderLayout.SOUTH);
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Reservation> list;
        if (controller.isAdminSession()) {
            list = controller.getReservationService().getAllBookings();
        } else {
            list = controller.getReservationService().getBookingsByCustomer(controller.getLoggedInUser().getUsername());
        }

        for (Reservation b : list) {
            tableModel.addRow(new Object[]{
                    b.getBookingId(),
                    b.getRoomNumber(),
                    b.getCheckInDate(),
                    b.getCheckOutDate(),
                    b.getNumberOfGuests(),
                    String.format("$%.2f", b.getTotalCost()),
                    b.getStatus().name()
            });
        }
    }

    private void checkAvailabilityAction() {
        try {
            int roomNum = Integer.parseInt(txtRoomNum.getText().trim());
            String cin = txtCheckIn.getText().trim();
            String cout = txtCheckOut.getText().trim();

            if (controller.getReservationService().isRoomAvailable(roomNum, cin, cout)) {
                ToastNotification.show(parent, "Room " + roomNum + " is AVAILABLE for the selected dates!", false);
            } else {
                ToastNotification.show(parent, "Room " + roomNum + " is UNAVAILABLE or under maintenance.", true);
            }
        } catch (NumberFormatException e) {
            ToastNotification.show(parent, "Invalid room number input!", true);
        }
    }

    private void executeBookingAction() {
        try {
            int roomNum = Integer.parseInt(txtRoomNum.getText().trim());
            String cin = txtCheckIn.getText().trim();
            String cout = txtCheckOut.getText().trim();
            int guests = Integer.parseInt(txtGuests.getText().trim());
            String reqs = txtSpecialReq.getText().trim();
            String coupon = txtCoupon.getText().trim();

            Room room = controller.getRoomService().getRoomByNumber(roomNum);
            if (room == null) {
                ToastNotification.show(parent, "Room does not exist!", true);
                return;
            }

            long days = DateTimeUtil.calculateDaysBetween(cin, cout);
            if (days <= 0) {
                ToastNotification.show(parent, "Check-out date must be after check-in date!", true);
                return;
            }

            double baseCost = days * room.getPrice();

            // Open simulated billing confirmation first
            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format("Confirm Reservation details:\nRoom: %d (%s)\nDays: %d\nSubtotal: $%.2f\n\nProceed to payment gateway simulation?",
                            roomNum, room.getType(), days, baseCost),
                    "Payment Simulation Gate", JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            // Prompt for Payment Method selection
            String[] methods = {"Credit Card", "Debit Card", "UPI", "Net Banking", "Cash"};
            String selectedMethod = (String) JOptionPane.showInputDialog(this, "Select Payment Method:",
                    "Payment Gateway Simulation", JOptionPane.QUESTION_MESSAGE, null, methods, methods[0]);
            
            if (selectedMethod == null) return;

            // Execute service calls
            String customerId = controller.getLoggedInUser().getUsername();
            String result = controller.getReservationService().createBooking(customerId, roomNum, cin, cout, guests, reqs, baseCost);
            
            if (result.startsWith("SUCCESS")) {
                String bookingId = result.split(":")[1];
                
                // Process Payment transaction
                String paymentResult = controller.getPaymentService().processPaymentSimulation(
                        bookingId, baseCost, selectedMethod, coupon, false
                );
                
                if (paymentResult.startsWith("SUCCESS")) {
                    String[] parts = paymentResult.split(":");
                    JOptionPane.showMessageDialog(this,
                            String.format("Booking ID: %s\nPayment ID: %s\nFinal Paid (Inc GST): $%s\n\nInvoice printed to reports/ folder!",
                                    bookingId, parts[1], parts[2]),
                            "Booking & Payment Confirmed", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Booking Confirmed but Payment simulation failed: " + paymentResult,
                            "Warning", JOptionPane.WARNING_MESSAGE);
                }
                
                refreshData();
                clearForm();
            } else {
                ToastNotification.show(parent, result, true);
            }
        } catch (NumberFormatException e) {
            ToastNotification.show(parent, "Please fill in numeric Room Number and Guests!", true);
        }
    }

    private void cancelBookingAction() {
        int selectedRow = tblBookings.getSelectedRow();
        if (selectedRow == -1) {
            ToastNotification.show(parent, "Please select a booking from the table to cancel!", true);
            return;
        }

        String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel booking " + bookingId + "?",
                "Cancel Reservation", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (controller.getReservationService().cancelBooking(bookingId)) {
                ToastNotification.show(parent, "Booking cancelled successfully.", false);
                refreshData();
            } else {
                ToastNotification.show(parent, "Only active CONFIRMED bookings can be cancelled.", true);
            }
        }
    }

    private void printInvoiceAction() {
        int selectedRow = tblBookings.getSelectedRow();
        if (selectedRow == -1) {
            ToastNotification.show(parent, "Select a booking row to view simulated invoice details!", true);
            return;
        }
        String bookingId = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Lookup reports directory
        java.io.File file = new java.io.File("reports/invoice_" + bookingId + ".txt");
        if (file.exists()) {
            try {
                // Read invoice file and display it
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                JTextArea textArea = new JTextArea(content);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(450, 400));
                JOptionPane.showMessageDialog(this, scrollPane, "Invoice: " + bookingId, JOptionPane.PLAIN_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Could not open invoice file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invoice receipt file does not exist. Check if payments were processed for this booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtRoomNum.setText("");
        txtGuests.setText("1");
        txtSpecialReq.setText("");
        txtCoupon.setText("");
    }
}
