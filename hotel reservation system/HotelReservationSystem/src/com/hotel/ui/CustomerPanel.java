package com.hotel.ui;

import com.hotel.controller.HotelController;
import com.hotel.model.Customer;
import com.hotel.model.Feedback;
import com.hotel.model.Room;
import com.hotel.util.DateTimeUtil;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class CustomerPanel extends JPanel {
    private final HotelController controller;
    private final MainFrame parent;

    // Admin View Widgets
    private JTable tblCustomers;
    private DefaultTableModel custTableModel;
    private JTextField txtSearchCust;

    // Customer View Widgets
    private JTextField txtName, txtEmail, txtPhone, txtIdProof;
    private JPasswordField txtNewPass;
    private JLabel lblLoyaltyVal;
    private JPanel wishlistPanel;
    
    // Feedback inputs
    private JTextField txtFeedRoom;
    private JComboBox<Integer> comboFeedRating;
    private JTextField txtFeedComment;

    public CustomerPanel(HotelController controller, MainFrame parent) {
        this.controller = controller;
        this.parent = parent;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Render based on session type
        if (controller.isAdminSession()) {
            initAdminView();
        } else {
            initCustomerView();
        }
    }

    private void initAdminView() {
        JLabel title = new JLabel("Customer Accounts Database");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.getTextPrimary());
        add(title, BorderLayout.NORTH);

        JPanel mainPanel = new RoundedPanel(15);
        mainPanel.setBackground(ThemeManager.getCardBackground());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BorderLayout(0, 15));
        add(mainPanel, BorderLayout.CENTER);

        // Header controls (Search & Delete)
        JPanel controls = new JPanel(new BorderLayout());
        controls.setOpaque(false);

        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);
        searchBar.add(new JLabel("🔍 Search Customer (Name/User):"));
        txtSearchCust = new JTextField(15);
        txtSearchCust.setBackground(ThemeManager.getBackground());
        txtSearchCust.setForeground(ThemeManager.getTextPrimary());
        txtSearchCust.setCaretColor(ThemeManager.getTextPrimary());
        txtSearchCust.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        txtSearchCust.addActionListener(e -> searchCustomersAction());
        searchBar.add(txtSearchCust);
        
        CustomButton btnSearch = new CustomButton("Search", ThemeManager.COLOR_ACCENT, ThemeManager.COLOR_ACCENT_HOVER);
        btnSearch.setPreferredSize(new Dimension(80, 28));
        btnSearch.setRadius(6);
        btnSearch.addActionListener(e -> searchCustomersAction());
        searchBar.add(btnSearch);
        controls.add(searchBar, BorderLayout.WEST);

        CustomButton btnDelete = new CustomButton("Delete Account", ThemeManager.STATUS_BOOKED, new Color(220, 38, 38));
        btnDelete.setPreferredSize(new Dimension(130, 28));
        btnDelete.setRadius(6);
        btnDelete.addActionListener(e -> deleteCustomerAction());
        controls.add(btnDelete, BorderLayout.EAST);

        mainPanel.add(controls, BorderLayout.NORTH);

        // Customers Table
        String[] cols = {"Username", "Full Name", "Email Address", "Phone Number", "ID Proof No", "Loyalty Points"};
        custTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblCustomers = new JTable(custTableModel);
        tblCustomers.setFont(ThemeManager.FONT_BODY);
        tblCustomers.setRowHeight(24);
        tblCustomers.getTableHeader().setFont(ThemeManager.FONT_HEADER);

        JScrollPane scroll = new JScrollPane(tblCustomers);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        mainPanel.add(scroll, BorderLayout.CENTER);

        refreshCustomersTable();
    }

    private void initCustomerView() {
        JLabel title = new JLabel("My Account Profile & Feedback");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.getTextPrimary());
        add(title, BorderLayout.NORTH);

        JPanel split = new JPanel(new GridLayout(1, 2, 20, 0));
        split.setOpaque(false);
        add(split, BorderLayout.CENTER);

        // Left Panel: Edit profile details
        RoundedPanel profileCard = new RoundedPanel(15);
        profileCard.setBackground(ThemeManager.getCardBackground());
        profileCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        profileCard.setLayout(new GridBagLayout());
        setupProfileForm(profileCard);
        split.add(profileCard);

        // Right Panel: Wishlist + Feedback Submission
        JPanel rightContainer = new JPanel(new GridLayout(2, 1, 0, 20));
        rightContainer.setOpaque(false);
        
        // Wishlist Subpanel
        RoundedPanel wishCard = new RoundedPanel(15);
        wishCard.setBackground(ThemeManager.getCardBackground());
        wishCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        wishCard.setLayout(new BorderLayout());
        
        JLabel wishTitle = new JLabel("❤️ My Bookmarks / Favorites List");
        wishTitle.setFont(ThemeManager.FONT_HEADER);
        wishTitle.setForeground(ThemeManager.getTextPrimary());
        wishCard.add(wishTitle, BorderLayout.NORTH);

        wishlistPanel = new JPanel();
        wishlistPanel.setOpaque(false);
        wishlistPanel.setLayout(new BoxLayout(wishlistPanel, BoxLayout.Y_AXIS));
        JScrollPane wishScroll = new JScrollPane(wishlistPanel);
        wishScroll.setBorder(null);
        wishScroll.setOpaque(false);
        wishScroll.getViewport().setOpaque(false);
        wishCard.add(wishScroll, BorderLayout.CENTER);
        rightContainer.add(wishCard);

        // Feedback Panel
        RoundedPanel feedbackCard = new RoundedPanel(15);
        feedbackCard.setBackground(ThemeManager.getCardBackground());
        feedbackCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        feedbackCard.setLayout(new GridBagLayout());
        setupFeedbackForm(feedbackCard);
        rightContainer.add(feedbackCard);

        split.add(rightContainer);

        // Refresh Data
        refreshProfile();
    }

    private void setupProfileForm(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel header = new JLabel("Personal Account Details");
        header.setFont(ThemeManager.FONT_HEADER);
        header.setForeground(ThemeManager.getTextPrimary());
        gbc.gridy = 0;
        panel.add(header, gbc);

        // Loyalty Points Showcase
        lblLoyaltyVal = new JLabel("Loyalty Membership Tier: Gold (150 pts)", JLabel.LEFT);
        lblLoyaltyVal.setFont(ThemeManager.FONT_BODY_BOLD);
        lblLoyaltyVal.setForeground(ThemeManager.COLOR_ACCENT);
        gbc.gridy = 1;
        panel.add(lblLoyaltyVal, gbc);

        txtName = new JTextField();
        txtEmail = new JTextField();
        txtPhone = new JTextField();
        txtIdProof = new JTextField();
        txtNewPass = new JPasswordField();

        addProfileField(panel, "Full Name", txtName, 2, gbc);
        addProfileField(panel, "Email Address", txtEmail, 4, gbc);
        addProfileField(panel, "Phone Number", txtPhone, 6, gbc);
        addProfileField(panel, "ID Proof Document Number", txtIdProof, 8, gbc);
        addProfileField(panel, "New Password (Leave blank to keep current)", txtNewPass, 10, gbc);

        CustomButton btnSave = new CustomButton("Save Profile Changes");
        btnSave.setPreferredSize(new Dimension(0, 36));
        btnSave.setRadius(8);
        btnSave.addActionListener(e -> saveProfileChangesAction());
        gbc.gridy = 12;
        gbc.insets = new Insets(15, 0, 0, 0);
        panel.add(btnSave, gbc);
    }

    private void addProfileField(JPanel panel, String text, JTextField field, int gridy, GridBagConstraints gbc) {
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
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        gbc.gridy = gridy + 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(field, gbc);
    }

    private void setupFeedbackForm(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel header = new JLabel("📝 Submit Hotel Experience Feedback");
        header.setFont(ThemeManager.FONT_HEADER);
        header.setForeground(ThemeManager.getTextPrimary());
        gbc.gridy = 0;
        panel.add(header, gbc);

        txtFeedRoom = new JTextField();
        addProfileField(panel, "Room Number stayed in", txtFeedRoom, 1, gbc);

        JLabel lblRating = new JLabel("Experience Rating Score");
        lblRating.setFont(ThemeManager.FONT_SMALL);
        lblRating.setForeground(ThemeManager.getTextSecondary());
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 2, 0);
        panel.add(lblRating, gbc);

        comboFeedRating = new JComboBox<>(new Integer[]{5, 4, 3, 2, 1});
        comboFeedRating.setBackground(ThemeManager.getBackground());
        comboFeedRating.setForeground(ThemeManager.getTextPrimary());
        comboFeedRating.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(comboFeedRating, gbc);

        txtFeedComment = new JTextField();
        addProfileField(panel, "Review comment", txtFeedComment, 5, gbc);

        CustomButton btnFeedback = new CustomButton("Submit Review", ThemeManager.COLOR_ACCENT, ThemeManager.COLOR_ACCENT_HOVER);
        btnFeedback.setPreferredSize(new Dimension(0, 32));
        btnFeedback.setRadius(6);
        btnFeedback.addActionListener(e -> submitFeedbackAction());
        gbc.gridy = 7;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(btnFeedback, gbc);
    }

    public void refreshProfile() {
        if (controller.isAdminSession()) {
            refreshCustomersTable();
            return;
        }

        Customer c = (Customer) controller.getLoggedInUser();
        if (c != null) {
            txtName.setText(c.getFullName());
            txtEmail.setText(c.getEmail());
            txtPhone.setText(c.getPhone());
            txtIdProof.setText(c.getIdProof());
            txtNewPass.setText("");

            lblLoyaltyVal.setText("🎖️ Loyalty points balance: " + c.getLoyaltyPoints() + " pts (" + getTierName(c.getLoyaltyPoints()) + ")");
            
            // Refresh wishlist list
            wishlistPanel.removeAll();
            List<Integer> list = c.getWishlistRooms();
            if (list.isEmpty()) {
                JLabel empty = new JLabel("No rooms bookmarked yet.");
                empty.setFont(ThemeManager.FONT_BODY);
                empty.setForeground(ThemeManager.getTextSecondary());
                wishlistPanel.add(empty);
            } else {
                for (int num : list) {
                    Room r = controller.getRoomService().getRoomByNumber(num);
                    if (r != null) {
                        wishlistPanel.add(createWishlistItemPanel(r));
                        wishlistPanel.add(Box.createRigidArea(new Dimension(0, 6)));
                    }
                }
            }
            wishlistPanel.revalidate();
            wishlistPanel.repaint();
        }
    }

    private JPanel createWishlistItemPanel(Room r) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

        JLabel title = new JLabel("Room " + r.getRoomNumber() + " - " + r.getType() + " ($" + r.getPrice() + ")");
        title.setFont(ThemeManager.FONT_BODY_BOLD);
        title.setForeground(ThemeManager.getTextPrimary());
        panel.add(title, BorderLayout.WEST);

        JLabel btnRemove = new JLabel("❌");
        btnRemove.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRemove.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                controller.getCustomerService().toggleWishlist(controller.getLoggedInUser().getUsername(), r.getRoomNumber());
                refreshProfile();
            }
        });
        panel.add(btnRemove, BorderLayout.EAST);

        return panel;
    }

    private String getTierName(int points) {
        if (points >= 300) return "Platinum Membership";
        if (points >= 150) return "Gold Membership";
        if (points >= 50) return "Silver Member";
        return "Bronze Member";
    }

    private void refreshCustomersTable() {
        custTableModel.setRowCount(0);
        List<Customer> list = controller.getCustomerService().getAllCustomers();
        for (Customer c : list) {
            custTableModel.addRow(new Object[]{
                    c.getUsername(),
                    c.getFullName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getIdProof(),
                    c.getLoyaltyPoints()
            });
        }
    }

    private void searchCustomersAction() {
        String q = txtSearchCust.getText().trim().toLowerCase();
        custTableModel.setRowCount(0);
        
        List<Customer> list = controller.getCustomerService().getAllCustomers();
        for (Customer c : list) {
            if (c.getUsername().toLowerCase().contains(q) || c.getFullName().toLowerCase().contains(q)) {
                custTableModel.addRow(new Object[]{
                        c.getUsername(),
                        c.getFullName(),
                        c.getEmail(),
                        c.getPhone(),
                        c.getIdProof(),
                        c.getLoyaltyPoints()
                });
            }
        }
    }

    private void deleteCustomerAction() {
        int selected = tblCustomers.getSelectedRow();
        if (selected == -1) {
            ToastNotification.show(parent, "Select a customer row from table to delete!", true);
            return;
        }

        String username = (String) custTableModel.getValueAt(selected, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete customer " + username + "? This action is irreversible.",
                "Delete Customer Account", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            controller.getCustomerService().deleteCustomer(username);
            ToastNotification.show(parent, "Customer deleted.", false);
            refreshCustomersTable();
        }
    }

    private void saveProfileChangesAction() {
        String username = controller.getLoggedInUser().getUsername();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String idProof = txtIdProof.getText().trim();
        String pass = new String(txtNewPass.getPassword());

        String result = controller.getCustomerService().updateProfile(username, name, email, phone, idProof, pass);
        if (result.equals("SUCCESS")) {
            ToastNotification.show(parent, "Profile updated successfully!", false);
            // Sync session copy
            controller.getLoggedInUser().setFullName(name);
            refreshProfile();
        } else {
            ToastNotification.show(parent, result, true);
        }
    }

    private void submitFeedbackAction() {
        try {
            int roomNum = Integer.parseInt(txtFeedRoom.getText().trim());
            int rating = (int) comboFeedRating.getSelectedItem();
            String comment = txtFeedComment.getText().trim();

            if (comment.isEmpty()) {
                ToastNotification.show(parent, "Please type a short review comment first!", true);
                return;
            }

            Room r = controller.getRoomService().getRoomByNumber(roomNum);
            if (r == null) {
                ToastNotification.show(parent, "Selected room number doesn't exist!", true);
                return;
            }

            // Create Feedback
            int feedbackId = controller.getDatabase().getFeedbacks().size() + 1;
            Feedback feedback = new Feedback(feedbackId, roomNum, controller.getLoggedInUser().getFullName(),
                                              rating, comment, DateTimeUtil.getCurrentDateString());
            
            controller.getDatabase().getFeedbacks().add(feedback);
            controller.getDatabase().saveFeedback();

            // Update room rating
            r.addReview(rating);
            controller.getDatabase().saveRooms();

            ToastNotification.show(parent, "Thank you! Feedback submitted successfully.", false);
            txtFeedRoom.setText("");
            txtFeedComment.setText("");
        } catch (NumberFormatException e) {
            ToastNotification.show(parent, "Invalid room number input!", true);
        }
    }
}
