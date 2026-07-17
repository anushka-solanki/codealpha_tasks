package com.hotel.ui;

import com.hotel.controller.HotelController;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RoomPanel extends JPanel {
    private final HotelController controller;
    private final MainFrame parent;

    // Filters UI
    private JTextField txtSearch;
    private JComboBox<String> comboType;
    private JComboBox<String> comboStatus;
    private JComboBox<String> comboSort;
    
    // Amenities Checkboxes
    private JCheckBox chkAc, chkWifi, chkTv, chkBreakfast, chkPool, chkParking;
    
    // Room cards container
    private JPanel cardsGrid;

    public RoomPanel(HotelController controller, MainFrame parent) {
        this.controller = controller;
        this.parent = parent;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // 1. Panel Header & Search Panel
        JPanel northPanel = new JPanel();
        northPanel.setOpaque(false);
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Room Directory & Booking");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.getTextPrimary());
        northPanel.add(title);
        northPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Filter Bar (FlowLayout)
        JPanel filterBar = new RoundedPanel(12);
        filterBar.setBackground(ThemeManager.getCardBackground());
        filterBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterBar.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));

        // Search Input
        filterBar.add(new JLabel("🔍 Search:"));
        txtSearch = new JTextField(10);
        setupFilterInput(txtSearch);
        filterBar.add(txtSearch);

        // Type Filter
        filterBar.add(new JLabel("Type:"));
        comboType = new JComboBox<>(new String[]{"All Types", "Standard", "Deluxe", "Suite", "Executive", "Presidential Suite"});
        setupFilterCombo(comboType);
        filterBar.add(comboType);

        // Status Filter
        filterBar.add(new JLabel("Status:"));
        comboStatus = new JComboBox<>(new String[]{"All Statuses", "Available", "Booked", "Maintenance", "Reserved"});
        setupFilterCombo(comboStatus);
        filterBar.add(comboStatus);

        // Sort option
        filterBar.add(new JLabel("Sort By:"));
        comboSort = new JComboBox<>(new String[]{"Room Number", "Price: Low to High", "Price: High to Low", "Rating"});
        setupFilterCombo(comboSort);
        filterBar.add(comboSort);

        // Filter trigger button
        CustomButton btnFilter = new CustomButton("Apply Filters", ThemeManager.COLOR_ACCENT, ThemeManager.COLOR_ACCENT_HOVER);
        btnFilter.setPreferredSize(new Dimension(110, 30));
        btnFilter.setRadius(6);
        btnFilter.addActionListener(e -> refreshRooms());
        filterBar.add(btnFilter);

        northPanel.add(filterBar);
        northPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Amenities Drawer Panel
        JPanel amenitiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        amenitiesPanel.setOpaque(false);
        chkAc = createAmenityCheckbox("AC");
        chkWifi = createAmenityCheckbox("WiFi");
        chkTv = createAmenityCheckbox("TV");
        chkBreakfast = createAmenityCheckbox("Breakfast Included");
        chkPool = createAmenityCheckbox("Swimming Pool");
        chkParking = createAmenityCheckbox("Parking space");

        amenitiesPanel.add(new JLabel("Amenities:"));
        amenitiesPanel.add(chkAc);
        amenitiesPanel.add(chkWifi);
        amenitiesPanel.add(chkTv);
        amenitiesPanel.add(chkBreakfast);
        amenitiesPanel.add(chkPool);
        amenitiesPanel.add(chkParking);

        northPanel.add(amenitiesPanel);
        northPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        add(northPanel, BorderLayout.NORTH);

        // 2. Grid Container for Room Cards
        cardsGrid = new JPanel(new GridLayout(0, 3, 20, 20)); // 3 Columns
        cardsGrid.setOpaque(false);

        JPanel wrapperGrid = new JPanel(new BorderLayout());
        wrapperGrid.setOpaque(false);
        wrapperGrid.add(cardsGrid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wrapperGrid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // First Load
        refreshRooms();
    }

    private void setupFilterInput(JTextField f) {
        f.setBackground(ThemeManager.getBackground());
        f.setForeground(ThemeManager.getTextPrimary());
        f.setCaretColor(ThemeManager.getTextPrimary());
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
    }

    private void setupFilterCombo(JComboBox<String> combo) {
        combo.setBackground(ThemeManager.getBackground());
        combo.setForeground(ThemeManager.getTextPrimary());
        combo.setFont(ThemeManager.FONT_BODY);
        combo.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
    }

    private JCheckBox createAmenityCheckbox(String text) {
        JCheckBox chk = new JCheckBox(text);
        chk.setFont(ThemeManager.FONT_SMALL);
        chk.setForeground(ThemeManager.getTextSecondary());
        chk.setOpaque(false);
        chk.addActionListener(e -> refreshRooms());
        return chk;
    }

    public void refreshRooms() {
        cardsGrid.removeAll();

        // Get filters from UI inputs
        String search = txtSearch.getText();
        String type = (String) comboType.getSelectedItem();
        String status = (String) comboStatus.getSelectedItem();
        String sortBy = (String) comboSort.getSelectedItem();

        boolean filterAc = chkAc.isSelected();
        boolean filterWifi = chkWifi.isSelected();
        boolean filterTv = chkTv.isSelected();
        boolean filterBreakfast = chkBreakfast.isSelected();
        boolean filterPool = chkPool.isSelected();
        boolean filterParking = chkParking.isSelected();

        // Call search service
        List<Room> filtered = controller.getRoomService().searchAndFilterRooms(
                search, type, status,
                filterAc, filterWifi, filterTv, filterBreakfast, filterPool, filterParking,
                sortBy
        );

        if (filtered.isEmpty()) {
            JLabel emptyLabel = new JLabel("No rooms match the selected criteria.", JLabel.CENTER);
            emptyLabel.setFont(ThemeManager.FONT_SUBTITLE);
            emptyLabel.setForeground(ThemeManager.getTextSecondary());
            cardsGrid.setLayout(new FlowLayout());
            cardsGrid.add(emptyLabel);
        } else {
            cardsGrid.setLayout(new GridLayout(0, 3, 20, 20));
            for (Room r : filtered) {
                cardsGrid.add(createRoomCard(r));
            }
        }

        cardsGrid.revalidate();
        cardsGrid.repaint();
    }

    private JPanel createRoomCard(Room r) {
        RoundedPanel card = new RoundedPanel(15);
        card.setBackground(ThemeManager.getCardBackground());
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Card Top Layout
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setOpaque(false);

        JLabel lblRoomNum = new JLabel("Room " + r.getRoomNumber());
        lblRoomNum.setFont(ThemeManager.FONT_SUBTITLE);
        lblRoomNum.setForeground(ThemeManager.getTextPrimary());
        cardHeader.add(lblRoomNum, BorderLayout.WEST);

        // Status Badge Panel
        JLabel lblStatus = new JLabel(" " + r.getStatus().name() + " ");
        lblStatus.setFont(ThemeManager.FONT_SMALL);
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setOpaque(true);
        switch (r.getStatus()) {
            case AVAILABLE: lblStatus.setBackground(ThemeManager.STATUS_AVAILABLE); break;
            case BOOKED: lblStatus.setBackground(ThemeManager.STATUS_BOOKED); break;
            case MAINTENANCE: lblStatus.setBackground(ThemeManager.STATUS_MAINTENANCE); break;
            case RESERVED: lblStatus.setBackground(ThemeManager.STATUS_RESERVED); break;
        }
        cardHeader.add(lblStatus, BorderLayout.EAST);

        // Card Center Panel (Details & Amenities)
        JPanel cardCenter = new JPanel();
        cardCenter.setOpaque(false);
        cardCenter.setLayout(new BoxLayout(cardCenter, BoxLayout.Y_AXIS));
        cardCenter.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel lblType = new JLabel("Type: " + r.getType());
        lblType.setFont(ThemeManager.FONT_BODY_BOLD);
        lblType.setForeground(ThemeManager.getTextSecondary());
        cardCenter.add(lblType);
        cardCenter.add(Box.createRigidArea(new Dimension(0, 5)));

        JLabel lblCapacity = new JLabel("Capacity: " + r.getCapacity() + " Guests");
        lblCapacity.setFont(ThemeManager.FONT_BODY);
        lblCapacity.setForeground(ThemeManager.getTextSecondary());
        cardCenter.add(lblCapacity);
        cardCenter.add(Box.createRigidArea(new Dimension(0, 5)));

        // Amenities labels icons
        StringBuilder amStr = new StringBuilder("Amenities: ");
        if (r.isAc()) amStr.append("❄️ ");
        if (r.hasWifi()) amStr.append("📶 ");
        if (r.hasTv()) amStr.append("📺 ");
        if (r.hasBreakfast()) amStr.append("🍳 ");
        if (r.hasPool()) amStr.append("🏊 ");
        if (r.hasParking()) amStr.append("🚗 ");
        JLabel lblAm = new JLabel(amStr.toString());
        lblAm.setFont(ThemeManager.FONT_BODY);
        lblAm.setForeground(ThemeManager.getTextSecondary());
        cardCenter.add(lblAm);
        cardCenter.add(Box.createRigidArea(new Dimension(0, 5)));

        // Rating Stars
        JLabel lblRating = new JLabel(String.format("⭐ %.1f (%d reviews)", r.getRating(), r.getReviewsCount()));
        lblRating.setFont(ThemeManager.FONT_SMALL);
        lblRating.setForeground(ThemeManager.STATUS_MAINTENANCE);
        cardCenter.add(lblRating);

        // Card Bottom Layout (Price, Wishlist & Actions)
        JPanel cardFooter = new JPanel(new BorderLayout());
        cardFooter.setOpaque(false);

        JLabel lblPrice = new JLabel(String.format("$%.2f/night", r.getPrice()));
        lblPrice.setFont(ThemeManager.FONT_SUBTITLE);
        lblPrice.setForeground(ThemeManager.COLOR_ACCENT);
        cardFooter.add(lblPrice, BorderLayout.WEST);

        // Right side footer buttons
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionsPanel.setOpaque(false);

        // Wishlist button
        boolean isWished = false;
        if (!controller.isAdminSession() && controller.getLoggedInUser() instanceof Customer) {
            Customer c = (Customer) controller.getLoggedInUser();
            isWished = c.isInWishlist(r.getRoomNumber());
        }

        JLabel btnWish = new JLabel(isWished ? "❤️" : "🖤");
        btnWish.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btnWish.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnWish.setToolTipText("Add to Wishlist/Favorites");
        btnWish.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (controller.isAdminSession()) {
                    ToastNotification.show(parent, "Admins cannot save wishlist rooms!", true);
                    return;
                }
                boolean wished = controller.getCustomerService().toggleWishlist(controller.getLoggedInUser().getUsername(), r.getRoomNumber());
                btnWish.setText(wished ? "❤️" : "🖤");
                ToastNotification.show(parent, wished ? "Added Room " + r.getRoomNumber() + " to favorites!" : "Removed from favorites.", false);
            }
        });
        actionsPanel.add(btnWish);

        // Book Now button
        if (r.getStatus() == Room.Status.AVAILABLE) {
            CustomButton btnBook = new CustomButton("Book Now", ThemeManager.COLOR_ACCENT, ThemeManager.COLOR_ACCENT_HOVER);
            btnBook.setPreferredSize(new Dimension(90, 28));
            btnBook.setRadius(6);
            btnBook.setFont(ThemeManager.FONT_SMALL);
            btnBook.addActionListener(e -> {
                if (controller.isAdminSession()) {
                    ToastNotification.show(parent, "Admins cannot make bookings! Please use Reservation Panel.", true);
                    return;
                }
                // Programmatically switch to Reservations tab
                switchToReservationsWithRoom(r.getRoomNumber());
            });
            actionsPanel.add(btnBook);
        } else {
            JLabel lblUnavailable = new JLabel("Unavailable");
            lblUnavailable.setFont(ThemeManager.FONT_SMALL);
            lblUnavailable.setForeground(ThemeManager.getTextSecondary());
            actionsPanel.add(lblUnavailable);
        }

        cardFooter.add(actionsPanel, BorderLayout.EAST);

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(cardCenter, BorderLayout.CENTER);
        card.add(cardFooter, BorderLayout.SOUTH);

        return card;
    }

    private void switchToReservationsWithRoom(int roomNumber) {
        // Find main frame's workspace panel container and switch cards
        parent.handleLoginSuccess(); // Reload / sync
        // Trigger programmatical sidebar action
        for (Component c : parent.getContentPane().getComponents()) {
            if (c instanceof JPanel && c.getName() != null && c.getName().equals("WORKSPACE")) {
                // we have reference in parent
            }
        }
        
        // Use custom callback or parent method to trigger switch
        JOptionPane.showMessageDialog(this, "Room " + roomNumber + " selected. Navigating to Booking tab...", "Room Selected", JOptionPane.INFORMATION_MESSAGE);
        
        // Parent frame has reference to Sidebar button and card switcher
        // We will call the action on main frame to navigate to booking panel and prefill room
        // Let's implement prefilling by looking up BookingPanel
    }
}
