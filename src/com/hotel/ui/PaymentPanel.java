package com.hotel.ui;

import com.hotel.controller.HotelController;
import com.hotel.model.Payment;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PaymentPanel extends JPanel {
    private final HotelController controller;
    private final MainFrame parent;

    private JTable tblPayments;
    private DefaultTableModel tableModel;
    
    // Analytics Metrics
    private JLabel lblTotalTransactionsVal;
    private JLabel lblAverageBillVal;
    private JLabel lblTaxGstRateVal;

    public PaymentPanel(HotelController controller, MainFrame parent) {
        this.controller = controller;
        this.parent = parent;

        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = new JLabel("Payment Ledger & Billing Panel");
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(ThemeManager.getTextPrimary());
        headerPanel.add(title, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Top Metrics Ribbon
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.setPreferredSize(new Dimension(0, 90));

        lblTotalTransactionsVal = new JLabel("$0.00", JLabel.CENTER);
        lblAverageBillVal = new JLabel("$0.00", JLabel.CENTER);
        lblTaxGstRateVal = new JLabel("18%", JLabel.CENTER);

        metricsPanel.add(createMetricCard("💳 Total Funds Processed", lblTotalTransactionsVal, ThemeManager.COLOR_ACCENT));
        metricsPanel.add(createMetricCard("📊 Average Transaction Value", lblAverageBillVal, ThemeManager.STATUS_RESERVED));
        metricsPanel.add(createMetricCard("⚖️ Standard GST Rate", lblTaxGstRateVal, ThemeManager.STATUS_MAINTENANCE));
        
        add(metricsPanel, BorderLayout.CENTER);

        // Main table panel
        RoundedPanel mainCard = new RoundedPanel(15);
        mainCard.setBackground(ThemeManager.getCardBackground());
        mainCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainCard.setLayout(new BorderLayout(0, 15));
        mainCard.setPreferredSize(new Dimension(0, 380));

        JLabel tableTitle = new JLabel("Transaction History Logs");
        tableTitle.setFont(ThemeManager.FONT_HEADER);
        tableTitle.setForeground(ThemeManager.getTextPrimary());
        mainCard.add(tableTitle, BorderLayout.NORTH);

        String[] cols = {"Receipt ID", "Booking ID", "Final Paid (Inc Tax)", "Method", "Tax GST", "Discount Code/Loyalty", "Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPayments = new JTable(tableModel);
        tblPayments.setFont(ThemeManager.FONT_BODY);
        tblPayments.setRowHeight(24);
        tblPayments.getTableHeader().setFont(ThemeManager.FONT_HEADER);

        JScrollPane scroll = new JScrollPane(tblPayments);
        scroll.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor()));
        mainCard.add(scroll, BorderLayout.CENTER);

        // Bottom view receipt action
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        CustomButton btnView = new CustomButton("Show Detailed Receipt", ThemeManager.COLOR_ACCENT, ThemeManager.COLOR_ACCENT_HOVER);
        btnView.setRadius(6);
        btnView.setPreferredSize(new Dimension(160, 30));
        btnView.addActionListener(e -> viewInvoiceDetailsAction());
        footer.add(btnView);
        mainCard.add(footer, BorderLayout.SOUTH);

        add(mainCard, BorderLayout.SOUTH);

        refreshData();
    }

    private JPanel createMetricCard(String title, JLabel val, Color col) {
        RoundedPanel card = new RoundedPanel(12);
        card.setBackground(ThemeManager.getCardBackground());
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(ThemeManager.FONT_SMALL);
        titleLbl.setForeground(ThemeManager.getTextSecondary());
        card.add(titleLbl, BorderLayout.NORTH);

        val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        val.setForeground(col);
        card.add(val, BorderLayout.CENTER);

        return card;
    }

    public void refreshData() {
        tableModel.setRowCount(0);
        List<Payment> list = controller.getPaymentService().getAllPayments();
        
        double total = 0;
        int count = 0;

        for (Payment p : list) {
            tableModel.addRow(new Object[]{
                    p.getPaymentId(),
                    p.getBookingId(),
                    String.format("$%.2f", p.getAmount()),
                    p.getMethod(),
                    String.format("$%.2f", p.getGstAmount()),
                    String.format("$%.2f", p.getDiscountAmount()),
                    p.getDate(),
                    p.getStatus().name()
            });

            if (p.getStatus() == Payment.Status.COMPLETED) {
                total += p.getAmount();
                count++;
            }
        }

        double avg = count > 0 ? total / count : 0.0;
        lblTotalTransactionsVal.setText(String.format("$%.2f", total));
        lblAverageBillVal.setText(String.format("$%.2f", avg));
        
        String gst = controller.getDatabase().getSettings().getOrDefault("gstRate", "18.0");
        lblTaxGstRateVal.setText(gst + "%");
    }

    private void viewInvoiceDetailsAction() {
        int selectedRow = tblPayments.getSelectedRow();
        if (selectedRow == -1) {
            ToastNotification.show(parent, "Select a payment receipt row first!", true);
            return;
        }

        String bookingId = (String) tableModel.getValueAt(selectedRow, 1);
        
        // Open simulated receipt
        java.io.File file = new java.io.File("reports/invoice_" + bookingId + ".txt");
        if (file.exists()) {
            try {
                String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
                JTextArea textArea = new JTextArea(content);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(450, 400));
                JOptionPane.showMessageDialog(this, scrollPane, "Invoice: " + bookingId, JOptionPane.PLAIN_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Could not load invoice receipt: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Receipt text file does not exist on disk.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
