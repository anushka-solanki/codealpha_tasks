package com.hotel.ui;

import com.hotel.controller.HotelController;
import com.hotel.model.Room;
import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AdminPanel extends JPanel {
    private final HotelController controller;
    private final MainFrame parent;

    // Tabs
    private JTabbedPane tabbedPane;

    // Tab 1: Room Inventory Widgets
    private JTable tblRooms;
    private DefaultTableModel roomsTableModel;
    private JTextField txtRoomNum, txtPrice, txtCapacity;
    private JComboBox<String> comboType;
    private JComboBox<String> comboStatus;
    private JCheckBox chkAc, chkWifi, chkTv, chkBreakfast, chkPool, chkParking;

    // Tab 2: Reports Widgets
    private JTextArea txtReportArea;

    // Tab 3: Backups Widgets
    private JComboBox<String> comboBackups;

    // Tab 4: System Logs Widgets
    private JTextArea txtLogsArea;

    public AdminPanel(HotelController controller, MainFrame parent) {
        this.controller = controller;
        this.parent = parent;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Title
        JLabel title = new JLabel("System Administrator Panel");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.getTextPrimary());
        add(title, BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(ThemeManager.FONT_HEADER);
        add(tabbedPane, BorderLayout.CENTER);

        initRoomManagerTab();
        initReportsTab();
        initBackupsTab();
        initLogsTab();

        refreshData();
    }

    private void initRoomManagerTab() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Left side: Add/Edit Room form
        RoundedPanel formPanel = new RoundedPanel(12);
        formPanel.setBackground(ThemeManager.getCardBackground());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        formPanel.setPreferredSize(new Dimension(300, 0));
        formPanel.setLayout(new GridBagLayout());
        setupRoomForm(formPanel);
        panel.add(formPanel, BorderLayout.WEST);

        // Right side: Table list of rooms
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        String[] cols = {"Room No", "Type", "Price/Night", "Capacity", "Status", "AC", "WiFi", "TV"};
        roomsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblRooms = new JTable(roomsTableModel);
        tblRooms.setFont(ThemeManager.FONT_BODY);
        tblRooms.setRowHeight(22);
        tblRooms.getTableHeader().setFont(ThemeManager.FONT_HEADER);

        JScrollPane scroll = new JScrollPane(tblRooms);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        tableContainer.add(scroll, BorderLayout.CENTER);

        // Room action footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);

        CustomButton btnDelete = new CustomButton("Remove Room", ThemeManager.STATUS_BOOKED, new Color(220, 38, 38));
        btnDelete.setRadius(6);
        btnDelete.setPreferredSize(new Dimension(120, 28));
        btnDelete.addActionListener(e -> removeRoomAction());
        footer.add(btnDelete);

        tableContainer.add(footer, BorderLayout.SOUTH);
        panel.add(tableContainer, BorderLayout.CENTER);

        tabbedPane.addTab("🔑 Room Inventory Manager", panel);
    }

    private void setupRoomForm(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 6, 0);

        JLabel title = new JLabel("Add / Manage Room Details");
        title.setFont(ThemeManager.FONT_HEADER);
        title.setForeground(ThemeManager.getTextPrimary());
        gbc.gridy = 0;
        panel.add(title, gbc);

        txtRoomNum = new JTextField();
        txtPrice = new JTextField();
        txtCapacity = new JTextField("2");
        comboType = new JComboBox<>(new String[]{"Standard", "Deluxe", "Suite", "Executive", "Presidential Suite"});
        comboStatus = new JComboBox<>(new String[]{"AVAILABLE", "BOOKED", "MAINTENANCE", "RESERVED"});

        addFormField(panel, "Room Number", txtRoomNum, 1, gbc);
        addFormField(panel, "Room Type", comboType, 3, gbc);
        addFormField(panel, "Price per Night ($)", txtPrice, 5, gbc);
        addFormField(panel, "Guest Capacity", txtCapacity, 7, gbc);
        addFormField(panel, "Initial Status", comboStatus, 9, gbc);

        // Checkboxes for Amenities
        JPanel amenGrid = new JPanel(new GridLayout(3, 2, 5, 5));
        amenGrid.setOpaque(false);
        chkAc = new JCheckBox("AC"); chkAc.setOpaque(false); chkAc.setFont(ThemeManager.FONT_SMALL); chkAc.setForeground(ThemeManager.getTextSecondary());
        chkWifi = new JCheckBox("WiFi"); chkWifi.setOpaque(false); chkWifi.setFont(ThemeManager.FONT_SMALL); chkWifi.setForeground(ThemeManager.getTextSecondary());
        chkTv = new JCheckBox("TV"); chkTv.setOpaque(false); chkTv.setFont(ThemeManager.FONT_SMALL); chkTv.setForeground(ThemeManager.getTextSecondary());
        chkBreakfast = new JCheckBox("Breakfast"); chkBreakfast.setOpaque(false); chkBreakfast.setFont(ThemeManager.FONT_SMALL); chkBreakfast.setForeground(ThemeManager.getTextSecondary());
        chkPool = new JCheckBox("Pool"); chkPool.setOpaque(false); chkPool.setFont(ThemeManager.FONT_SMALL); chkPool.setForeground(ThemeManager.getTextSecondary());
        chkParking = new JCheckBox("Parking"); chkParking.setOpaque(false); chkParking.setFont(ThemeManager.FONT_SMALL); chkParking.setForeground(ThemeManager.getTextSecondary());

        amenGrid.add(chkAc); amenGrid.add(chkWifi);
        amenGrid.add(chkTv); amenGrid.add(chkBreakfast);
        amenGrid.add(chkPool); amenGrid.add(chkParking);

        gbc.gridy = 11;
        gbc.insets = new Insets(5, 0, 10, 0);
        panel.add(amenGrid, gbc);

        // Save action
        CustomButton btnAdd = new CustomButton("Save Room");
        btnAdd.setPreferredSize(new Dimension(0, 32));
        btnAdd.setRadius(6);
        btnAdd.addActionListener(e -> saveRoomAction());
        gbc.gridy = 12;
        gbc.insets = new Insets(10, 0, 0, 0);
        panel.add(btnAdd, gbc);
    }

    private void addFormField(JPanel panel, String label, JComponent comp, int gridy, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(ThemeManager.FONT_SMALL);
        lbl.setForeground(ThemeManager.getTextSecondary());
        gbc.gridy = gridy;
        gbc.insets = new Insets(0, 0, 2, 0);
        panel.add(lbl, gbc);

        comp.setBackground(ThemeManager.getBackground());
        comp.setForeground(ThemeManager.getTextPrimary());
        comp.setFont(ThemeManager.FONT_BODY);
        
        if (comp instanceof JTextField) {
            ((JTextField) comp).setCaretColor(ThemeManager.getTextPrimary());
            comp.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getBorderColor()),
                    BorderFactory.createEmptyBorder(4, 6, 4, 6)
            ));
        } else {
            comp.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        }

        gbc.gridy = gridy + 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(comp, gbc);
    }

    private void initReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        txtReportArea = new JTextArea();
        txtReportArea.setEditable(false);
        txtReportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReportArea.setBackground(ThemeManager.getCardBackground());
        txtReportArea.setForeground(ThemeManager.getTextPrimary());
        
        JScrollPane scroll = new JScrollPane(txtReportArea);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        controls.setOpaque(false);

        CustomButton btnPdf = new CustomButton("Export PDF", ThemeManager.COLOR_ACCENT, ThemeManager.COLOR_ACCENT_HOVER);
        btnPdf.setRadius(6);
        btnPdf.setPreferredSize(new Dimension(120, 30));
        btnPdf.addActionListener(e -> exportReportAction("PDF"));
        controls.add(btnPdf);

        CustomButton btnExcel = new CustomButton("Export Excel", ThemeManager.STATUS_RESERVED, new Color(29, 78, 216));
        btnExcel.setRadius(6);
        btnExcel.setPreferredSize(new Dimension(120, 30));
        btnExcel.addActionListener(e -> exportReportAction("EXCEL"));
        controls.add(btnExcel);

        panel.add(controls, BorderLayout.SOUTH);
        tabbedPane.addTab("📊 Reports & Analytics Generator", panel);
    }

    private void initBackupsTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(ThemeManager.getCardBackground());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(450, 260));
        card.setLayout(new GridBagLayout());

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.gridx = 0;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.weightx = 1.0;
        cardGbc.insets = new Insets(0, 0, 12, 0);

        JLabel lblTitle = new JLabel("System Database Backup & Restore Utility", JLabel.CENTER);
        lblTitle.setFont(ThemeManager.FONT_HEADER);
        lblTitle.setForeground(ThemeManager.getTextPrimary());
        cardGbc.gridy = 0;
        card.add(lblTitle, cardGbc);

        CustomButton btnBackup = new CustomButton("Trigger Manual System Backup");
        btnBackup.setPreferredSize(new Dimension(0, 36));
        btnBackup.setRadius(8);
        btnBackup.addActionListener(e -> triggerBackupAction());
        cardGbc.gridy = 1;
        card.add(btnBackup, cardGbc);

        JLabel lblCombo = new JLabel("Select Backup Timestamp to Restore:");
        lblCombo.setFont(ThemeManager.FONT_SMALL);
        lblCombo.setForeground(ThemeManager.getTextSecondary());
        cardGbc.gridy = 2;
        cardGbc.insets = new Insets(10, 0, 4, 0);
        card.add(lblCombo, cardGbc);

        comboBackups = new JComboBox<>();
        comboBackups.setBackground(ThemeManager.getBackground());
        comboBackups.setForeground(ThemeManager.getTextPrimary());
        comboBackups.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        cardGbc.gridy = 3;
        cardGbc.insets = new Insets(0, 0, 15, 0);
        card.add(comboBackups, cardGbc);

        CustomButton btnRestore = new CustomButton("Restore Selected Backup State", ThemeManager.STATUS_MAINTENANCE, new Color(217, 119, 6));
        btnRestore.setPreferredSize(new Dimension(0, 36));
        btnRestore.setRadius(8);
        btnRestore.addActionListener(e -> triggerRestoreAction());
        cardGbc.gridy = 4;
        card.add(btnRestore, cardGbc);

        panel.add(card, gbc);
        tabbedPane.addTab("💾 System Backups & Restores", panel);
    }

    private void initLogsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        txtLogsArea = new JTextArea();
        txtLogsArea.setEditable(false);
        txtLogsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtLogsArea.setBackground(ThemeManager.getCardBackground());
        txtLogsArea.setForeground(ThemeManager.getTextPrimary());

        JScrollPane scroll = new JScrollPane(txtLogsArea);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        panel.add(scroll, BorderLayout.CENTER);

        tabbedPane.addTab("🛡️ System Log Audits", panel);
    }

    public void refreshData() {
        // 1. Room Inventory Table
        roomsTableModel.setRowCount(0);
        List<Room> rooms = controller.getRoomService().getAllRooms();
        for (Room r : rooms) {
            roomsTableModel.addRow(new Object[]{
                    r.getRoomNumber(),
                    r.getType(),
                    String.format("$%.2f", r.getPrice()),
                    r.getCapacity(),
                    r.getStatus().name(),
                    r.isAc(), r.hasWifi(), r.hasTv()
            });
        }

        // 2. Reports Text
        txtReportArea.setText(controller.getReportService().getReportSummaryText());

        // 3. Available Backups Dropdown
        comboBackups.removeAllItems();
        List<String> backups = controller.getBackupService().getAvailableBackups();
        for (String b : backups) {
            comboBackups.addItem(b);
        }

        // 4. System Logs Text
        txtLogsArea.setText("");
        List<String> logs = controller.getDatabase().readLogs();
        // Show logs in reverse order (most recent first)
        for (int i = logs.size() - 1; i >= 0; i--) {
            txtLogsArea.append(logs.get(i) + "\n");
        }
    }

    private void saveRoomAction() {
        try {
            int num = Integer.parseInt(txtRoomNum.getText().trim());
            String type = (String) comboType.getSelectedItem();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int cap = Integer.parseInt(txtCapacity.getText().trim());
            Room.Status status = Room.Status.valueOf((String) comboStatus.getSelectedItem());

            if (price <= 0 || cap <= 0) {
                ToastNotification.show(parent, "Price and Capacity must be positive numbers!", true);
                return;
            }

            Room existing = controller.getRoomService().getRoomByNumber(num);
            if (existing == null) {
                // New Room
                Room r = new Room(num, type, price, cap, status,
                        chkAc.isSelected(), chkWifi.isSelected(), chkTv.isSelected(),
                        chkBreakfast.isSelected(), chkPool.isSelected(), chkParking.isSelected(),
                        5.0, 0);
                controller.getRoomService().addRoom(r);
                ToastNotification.show(parent, "Added new Room " + num + " successfully.", false);
            } else {
                // Update Existing
                existing.setType(type);
                existing.setPrice(price);
                existing.setCapacity(cap);
                existing.setStatus(status);
                existing.setAc(chkAc.isSelected());
                existing.setWifi(chkWifi.isSelected());
                existing.setTv(chkTv.isSelected());
                existing.setBreakfast(chkBreakfast.isSelected());
                existing.setPool(chkPool.isSelected());
                existing.setParking(chkParking.isSelected());
                controller.saveAllData();
                ToastNotification.show(parent, "Updated Room " + num + " successfully.", false);
            }
            refreshData();
            clearRoomForm();
        } catch (NumberFormatException e) {
            ToastNotification.show(parent, "Please input valid numeric values for room details!", true);
        }
    }

    private void removeRoomAction() {
        int selected = tblRooms.getSelectedRow();
        if (selected == -1) {
            ToastNotification.show(parent, "Select a room row from the inventory list to remove!", true);
            return;
        }

        int num = (int) roomsTableModel.getValueAt(selected, 0);
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to permanently delete Room " + num + "?",
                "Delete Room Confirm", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            controller.getRoomService().deleteRoom(num);
            ToastNotification.show(parent, "Room " + num + " removed.", false);
            refreshData();
        }
    }

    private void exportReportAction(String type) {
        String result;
        if (type.equals("PDF")) {
            result = controller.getReportService().exportPdfReport();
        } else {
            result = controller.getReportService().exportExcelReport();
        }

        if (result.startsWith("SUCCESS")) {
            String path = result.split(":")[1];
            JOptionPane.showMessageDialog(this, "Report exported successfully to:\n" + path, "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to export report: " + result.substring(6), "Error", JOptionPane.ERROR_MESSAGE);
        }
        refreshData();
    }

    private void triggerBackupAction() {
        if (controller.getBackupService().performManualBackup()) {
            ToastNotification.show(parent, "Manual database backup created successfully!", false);
            refreshData();
        } else {
            ToastNotification.show(parent, "Failed to trigger system backup. Check logs.", true);
        }
    }

    private void triggerRestoreAction() {
        String selected = (String) comboBackups.getSelectedItem();
        if (selected == null) {
            ToastNotification.show(parent, "No backup file selected!", true);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to restore system state to " + selected + "?\nThis will overwrite current data.",
                "Restore Database State", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (controller.getBackupService().restoreFromBackup(selected)) {
                JOptionPane.showMessageDialog(this, "System databases successfully restored to " + selected + "!", "Restore Complete", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                ToastNotification.show(parent, "Restore operation failed. Check console/logs.", true);
            }
        }
    }

    private void clearRoomForm() {
        txtRoomNum.setText("");
        txtPrice.setText("");
        txtCapacity.setText("2");
        comboType.setSelectedIndex(0);
        comboStatus.setSelectedIndex(0);
        chkAc.setSelected(false);
        chkWifi.setSelected(false);
        chkTv.setSelected(false);
        chkBreakfast.setSelected(false);
        chkPool.setSelected(false);
        chkParking.setSelected(false);
    }
}
