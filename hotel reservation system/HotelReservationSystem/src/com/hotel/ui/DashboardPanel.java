package com.hotel.ui;

import com.hotel.controller.HotelController;
import com.hotel.model.Reservation;
import com.hotel.model.Room;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DashboardPanel extends JPanel {
    private final HotelController controller;
    private final MainFrame parent;

    // GUI Elements to update on refresh
    private JLabel lblRevenueVal, lblOccupancyVal, lblAvailableVal, lblBookedVal;
    private JPanel recentBookingsPanel;
    private JPanel popularPanel;
    
    // Currency Converter simulation widgets
    private JTextField txtUsd;
    private JLabel lblEur, lblInr, lblGbp;

    // Custom Java2D Chart reference
    private BookingChart bookingChart;

    public DashboardPanel(HotelController controller, MainFrame parent) {
        this.controller = controller;
        this.parent = parent;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Top Dashboard Banner
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Hotel Analytics & Dashboard");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.getTextPrimary());
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Center ScrollPane containing dashboard sections
        JPanel scrollContent = new JPanel();
        scrollContent.setOpaque(false);
        scrollContent.setLayout(new BoxLayout(scrollContent, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(scrollContent);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        add(scroll, BorderLayout.CENTER);

        // 1. Statistics Cards Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        lblRevenueVal = new JLabel("$0.00", JLabel.CENTER);
        lblOccupancyVal = new JLabel("0.0%", JLabel.CENTER);
        lblAvailableVal = new JLabel("0", JLabel.CENTER);
        lblBookedVal = new JLabel("0", JLabel.CENTER);

        statsPanel.add(createStatCard("💵  Total Revenue", lblRevenueVal, new Color(13, 148, 136)));
        statsPanel.add(createStatCard("📈  Room Occupancy", lblOccupancyVal, new Color(59, 130, 246)));
        statsPanel.add(createStatCard("🛌  Available Rooms", lblAvailableVal, new Color(16, 185, 129)));
        statsPanel.add(createStatCard("🔒  Booked Rooms", lblBookedVal, new Color(239, 68, 68)));

        scrollContent.add(statsPanel);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 25)));

        // 2. Middle Row: Graph + Right Sidebar (Currency Converter)
        JPanel middleRow = new JPanel(new BorderLayout(20, 0));
        middleRow.setOpaque(false);
        middleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        middleRow.setPreferredSize(new Dimension(0, 300));

        // Graph Panel (Custom-drawn)
        bookingChart = new BookingChart();
        RoundedPanel chartCard = new RoundedPanel(15);
        chartCard.setBackground(ThemeManager.getCardBackground());
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel chartTitle = new JLabel("Booking Frequency by Room Type");
        chartTitle.setFont(ThemeManager.FONT_HEADER);
        chartTitle.setForeground(ThemeManager.getTextPrimary());
        chartCard.add(chartTitle, BorderLayout.NORTH);
        chartCard.add(bookingChart, BorderLayout.CENTER);

        middleRow.add(chartCard, BorderLayout.CENTER);

        // Currency Converter Side Panel
        RoundedPanel currencyCard = new RoundedPanel(15);
        currencyCard.setBackground(ThemeManager.getCardBackground());
        currencyCard.setPreferredSize(new Dimension(280, 0));
        currencyCard.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        currencyCard.setLayout(new GridBagLayout());
        setupCurrencyConverter(currencyCard);

        middleRow.add(currencyCard, BorderLayout.EAST);

        scrollContent.add(middleRow);
        scrollContent.add(Box.createRigidArea(new Dimension(0, 25)));

        // 3. Bottom Row: Recent Bookings + Popular Rooms
        JPanel bottomRow = new JPanel(new GridLayout(1, 2, 20, 0));
        bottomRow.setOpaque(false);
        bottomRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        bottomRow.setPreferredSize(new Dimension(0, 220));

        // Recent Bookings Panel
        RoundedPanel recentCard = new RoundedPanel(15);
        recentCard.setBackground(ThemeManager.getCardBackground());
        recentCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        recentCard.setLayout(new BorderLayout());
        
        JLabel recTitle = new JLabel("Recent Hotel Bookings");
        recTitle.setFont(ThemeManager.FONT_HEADER);
        recTitle.setForeground(ThemeManager.getTextPrimary());
        recentCard.add(recTitle, BorderLayout.NORTH);

        recentBookingsPanel = new JPanel();
        recentBookingsPanel.setOpaque(false);
        recentBookingsPanel.setLayout(new BoxLayout(recentBookingsPanel, BoxLayout.Y_AXIS));
        recentCard.add(recentBookingsPanel, BorderLayout.CENTER);

        bottomRow.add(recentCard);

        // Popular / Hot Features Panel
        RoundedPanel popCard = new RoundedPanel(15);
        popCard.setBackground(ThemeManager.getCardBackground());
        popCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        popCard.setLayout(new BorderLayout());

        JLabel popTitle = new JLabel("Frequently Booked Rooms & Recommendations");
        popTitle.setFont(ThemeManager.FONT_HEADER);
        popTitle.setForeground(ThemeManager.getTextPrimary());
        popCard.add(popTitle, BorderLayout.NORTH);

        popularPanel = new JPanel();
        popularPanel.setOpaque(false);
        popularPanel.setLayout(new BoxLayout(popularPanel, BoxLayout.Y_AXIS));
        popCard.add(popularPanel, BorderLayout.CENTER);

        bottomRow.add(popCard);

        scrollContent.add(bottomRow);

        // Populate initial data
        refreshStats();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        RoundedPanel card = new RoundedPanel(15);
        card.setBackground(ThemeManager.getCardBackground());
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.FONT_HEADER);
        titleLabel.setForeground(ThemeManager.getTextSecondary());
        card.add(titleLabel, BorderLayout.NORTH);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void setupCurrencyConverter(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel lblTitle = new JLabel("💵  Currency Converter Widget");
        lblTitle.setFont(ThemeManager.FONT_HEADER);
        lblTitle.setForeground(ThemeManager.getTextPrimary());
        gbc.gridy = 0;
        panel.add(lblTitle, gbc);

        JLabel lblDesc = new JLabel("Enter USD to convert (Simulation):");
        lblDesc.setFont(ThemeManager.FONT_SMALL);
        lblDesc.setForeground(ThemeManager.getTextSecondary());
        gbc.gridy = 1;
        panel.add(lblDesc, gbc);

        txtUsd = new JTextField("100");
        txtUsd.setBackground(ThemeManager.getBackground());
        txtUsd.setForeground(ThemeManager.getTextPrimary());
        txtUsd.setCaretColor(ThemeManager.getTextPrimary());
        txtUsd.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridy = 2;
        panel.add(txtUsd, gbc);

        CustomButton btnConvert = new CustomButton("Convert");
        btnConvert.setPreferredSize(new Dimension(0, 30));
        btnConvert.addActionListener(e -> updateCurrencyCalculations());
        gbc.gridy = 3;
        panel.add(btnConvert, gbc);

        lblEur = new JLabel("EUR: €0.00");
        lblEur.setFont(ThemeManager.FONT_BODY);
        lblEur.setForeground(ThemeManager.getTextPrimary());
        gbc.gridy = 4;
        panel.add(lblEur, gbc);

        lblInr = new JLabel("INR: ₹0.00");
        lblInr.setFont(ThemeManager.FONT_BODY);
        lblInr.setForeground(ThemeManager.getTextPrimary());
        gbc.gridy = 5;
        panel.add(lblInr, gbc);

        lblGbp = new JLabel("GBP: £0.00");
        lblGbp.setFont(ThemeManager.FONT_BODY);
        lblGbp.setForeground(ThemeManager.getTextPrimary());
        gbc.gridy = 6;
        panel.add(lblGbp, gbc);

        updateCurrencyCalculations();
    }

    private void updateCurrencyCalculations() {
        try {
            double usd = Double.parseDouble(txtUsd.getText().trim());
            lblEur.setText(String.format("EUR: €%.2f", usd * 0.92));
            lblInr.setText(String.format("INR: ₹%.2f", usd * 83.50));
            lblGbp.setText(String.format("GBP: £%.2f", usd * 0.78));
        } catch (NumberFormatException e) {
            ToastNotification.show(parent, "Please enter a valid numeric USD amount", true);
        }
    }

    public void refreshStats() {
        // Calculate Dynamic Metrics
        double revenue = controller.getReportService().calculateTotalRevenue();
        double occupancy = controller.getReportService().calculateOccupancyPercentage();
        int available = controller.getReportService().getAvailableRoomsCount();
        int booked = controller.getReportService().getBookedRoomsCount();

        lblRevenueVal.setText(String.format("$%.2f", revenue));
        lblOccupancyVal.setText(String.format("%.1f%%", occupancy));
        lblAvailableVal.setText(String.valueOf(available));
        lblBookedVal.setText(String.valueOf(booked));

        // Refresh dynamic chart data
        bookingChart.updateData(controller.getReservationService().getAllBookings(), controller.getRoomService().getAllRooms());

        // Refresh Recent Bookings Panel
        recentBookingsPanel.removeAll();
        List<Reservation> bookingsList = controller.getReservationService().getAllBookings();
        int limit = Math.min(bookingsList.size(), 3);
        
        if (bookingsList.isEmpty()) {
            JLabel empty = new JLabel("No bookings registered yet.");
            empty.setFont(ThemeManager.FONT_BODY);
            empty.setForeground(ThemeManager.getTextSecondary());
            recentBookingsPanel.add(empty);
        } else {
            // Display last 3 bookings (reverse order)
            for (int i = 0; i < limit; i++) {
                Reservation b = bookingsList.get(bookingsList.size() - 1 - i);
                recentBookingsPanel.add(createRecentBookingCard(b));
                recentBookingsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
            }
        }

        // Refresh Popular Recommendations Panel
        popularPanel.removeAll();
        List<Room> rooms = controller.getRoomService().getAllRooms();
        int itemsAdded = 0;
        for (Room r : rooms) {
            if (r.getRating() >= 4.5 && itemsAdded < 3) {
                popularPanel.add(createRecommendationCard(r));
                popularPanel.add(Box.createRigidArea(new Dimension(0, 8)));
                itemsAdded++;
            }
        }
        if (itemsAdded == 0) {
            JLabel empty = new JLabel("No high rated rooms recommended yet.");
            empty.setFont(ThemeManager.FONT_BODY);
            empty.setForeground(ThemeManager.getTextSecondary());
            popularPanel.add(empty);
        }

        revalidate();
        repaint();
    }

    private JPanel createRecentBookingCard(Reservation b) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel icon = new JLabel("📅", JLabel.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        card.add(icon, BorderLayout.WEST);

        JPanel details = new JPanel(new GridLayout(2, 1));
        details.setOpaque(false);
        
        JLabel mainText = new JLabel(b.getBookingId() + " - Cust: " + b.getCustomerId() + " (Room " + b.getRoomNumber() + ")");
        mainText.setFont(ThemeManager.FONT_BODY_BOLD);
        mainText.setForeground(ThemeManager.getTextPrimary());
        details.add(mainText);

        JLabel subText = new JLabel("Check-in: " + b.getCheckInDate() + " | Status: " + b.getStatus());
        subText.setFont(ThemeManager.FONT_SMALL);
        subText.setForeground(ThemeManager.getTextSecondary());
        details.add(subText);

        card.add(details, BorderLayout.CENTER);

        JLabel cost = new JLabel(String.format("$%.2f", b.getTotalCost()), JLabel.RIGHT);
        cost.setFont(ThemeManager.FONT_BODY_BOLD);
        cost.setForeground(ThemeManager.COLOR_ACCENT);
        card.add(cost, BorderLayout.EAST);

        return card;
    }

    private JPanel createRecommendationCard(Room r) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setOpaque(false);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        JLabel icon = new JLabel("⭐", JLabel.CENTER);
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        card.add(icon, BorderLayout.WEST);

        JPanel details = new JPanel(new GridLayout(2, 1));
        details.setOpaque(false);

        JLabel mainText = new JLabel("Room " + r.getRoomNumber() + " - " + r.getType());
        mainText.setFont(ThemeManager.FONT_BODY_BOLD);
        mainText.setForeground(ThemeManager.getTextPrimary());
        details.add(mainText);

        JLabel subText = new JLabel(String.format("Rating: %.1f stars (%d reviews) | WiFi: %b", r.getRating(), r.getReviewsCount(), r.hasWifi()));
        subText.setFont(ThemeManager.FONT_SMALL);
        subText.setForeground(ThemeManager.getTextSecondary());
        details.add(subText);

        card.add(details, BorderLayout.CENTER);

        JLabel price = new JLabel(String.format("$%.0f/night", r.getPrice()), JLabel.RIGHT);
        price.setFont(ThemeManager.FONT_BODY_BOLD);
        price.setForeground(ThemeManager.STATUS_AVAILABLE);
        card.add(price, BorderLayout.EAST);

        return card;
    }

    // Custom Graphics2D Bar Chart Component
    private static class BookingChart extends JPanel {
        private final Map<String, Integer> chartData = new HashMap<>();

        public BookingChart() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 200));
        }

        public void updateData(List<Reservation> bookings, List<Room> rooms) {
            chartData.clear();
            
            // Build type helper mapping
            Map<Integer, String> roomTypes = new HashMap<>();
            for (Room r : rooms) {
                roomTypes.put(r.getRoomNumber(), r.getType());
            }

            // Aggregate bookings by room type
            for (Reservation b : bookings) {
                if (b.getStatus() == Reservation.Status.CONFIRMED || b.getStatus() == Reservation.Status.COMPLETED) {
                    String type = roomTypes.getOrDefault(b.getRoomNumber(), "Standard");
                    chartData.put(type, chartData.getOrDefault(type, 0) + 1);
                }
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 40;
            int chartWidth = width - 2 * padding;
            int chartHeight = height - 2 * padding;

            // Draw Y-Axis and X-Axis lines
            g2.setColor(ThemeManager.getBorderColor());
            g2.drawLine(padding, height - padding, width - padding, height - padding); // X-Axis
            g2.drawLine(padding, padding, padding, height - padding); // Y-Axis

            String[] categories = {"Standard", "Deluxe", "Suite", "Executive", "Presidential Suite"};
            int maxVal = 5; // Default max Y axis limit
            for (String cat : categories) {
                maxVal = Math.max(maxVal, chartData.getOrDefault(cat, 0));
            }
            // round max to nearest multiple of 5
            maxVal = ((maxVal / 5) + 1) * 5;

            // Draw Y-axis labels and grids
            g2.setFont(ThemeManager.FONT_SMALL);
            for (int i = 0; i <= 5; i++) {
                int yVal = (maxVal * i) / 5;
                int yCoord = height - padding - (chartHeight * i) / 5;
                g2.setColor(ThemeManager.getTextSecondary());
                g2.drawString(String.valueOf(yVal), padding - 20, yCoord + 4);

                // Grid lines
                if (i > 0) {
                    g2.setColor(new Color(ThemeManager.getBorderColor().getRed(), ThemeManager.getBorderColor().getGreen(), ThemeManager.getBorderColor().getBlue(), 50));
                    g2.drawLine(padding, yCoord, width - padding, yCoord);
                }
            }

            // Draw Bars
            int barWidth = chartWidth / categories.length - 20;
            for (int i = 0; i < categories.length; i++) {
                String cat = categories[i];
                int val = chartData.getOrDefault(cat, 0);

                int barHeight = (chartHeight * val) / maxVal;
                int xCoord = padding + 15 + i * (chartWidth / categories.length);
                int yCoord = height - padding - barHeight;

                // Draw bar using Gradient
                GradientPaint gp = new GradientPaint(xCoord, yCoord, ThemeManager.COLOR_ACCENT, xCoord, height - padding, new Color(59, 130, 246));
                g2.setPaint(gp);
                g2.fillRoundRect(xCoord, yCoord, barWidth, barHeight, 8, 8);

                // Value labels on top of bars
                g2.setColor(ThemeManager.getTextPrimary());
                g2.setFont(ThemeManager.FONT_BODY_BOLD);
                g2.drawString(String.valueOf(val), xCoord + barWidth / 2 - 5, yCoord - 5);

                // Draw X-axis Labels
                g2.setColor(ThemeManager.getTextSecondary());
                g2.setFont(ThemeManager.FONT_SMALL);
                String shortLabel = cat.length() > 10 ? cat.substring(0, 8) + ".." : cat;
                g2.drawString(shortLabel, xCoord + 2, height - padding + 15);
            }
        }
    }
}
