package com.hotel.ui;

import com.hotel.controller.HotelController;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginPanel extends GradientPanel {
    private final HotelController controller;
    private final MainFrame parent;

    // Card Switching inside Login Card
    private final CardLayout loginCardLayout;
    private final JPanel loginCardContainer;

    // Login Form Fields
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JCheckBox chkAdmin;
    private JCheckBox chkRemember;

    // Register Form Fields
    private JTextField txtRegUser;
    private JPasswordField txtRegPass;
    private JTextField txtRegName;
    private JTextField txtRegEmail;
    private JTextField txtRegPhone;
    private JTextField txtRegIdProof;

    public LoginPanel(HotelController controller, MainFrame parent) {
        super(new Color(15, 23, 42), new Color(30, 41, 59));
        this.controller = controller;
        this.parent = parent;

        setLayout(new GridBagLayout());

        // Center card configuration
        loginCardLayout = new CardLayout();
        loginCardContainer = new RoundedPanel(20);
        loginCardContainer.setPreferredSize(new Dimension(420, 520));
        loginCardContainer.setBackground(new Color(30, 41, 59)); // Card dark slate
        loginCardContainer.setLayout(loginCardLayout);
        loginCardContainer.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));

        initLoginForm();
        initRegisterForm();

        add(loginCardContainer);
    }

    private void initLoginForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(30, 35, 30, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // 1. Icon & Header
        JLabel lblIcon = new JLabel("🏨", JLabel.CENTER);
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        form.add(lblIcon, gbc);

        JLabel lblTitle = new JLabel("Smart Hotel Login", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        form.add(lblTitle, gbc);

        JLabel lblSub = new JLabel("Enter credentials to enter", JLabel.CENTER);
        lblSub.setFont(ThemeManager.FONT_SMALL);
        lblSub.setForeground(new Color(148, 163, 184));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        form.add(lblSub, gbc);

        // 2. Input Fields
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(ThemeManager.FONT_HEADER);
        lblUser.setForeground(new Color(148, 163, 184));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 5, 0);
        form.add(lblUser, gbc);

        txtUser = new JTextField();
        setupTextField(txtUser);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 15, 0);
        form.add(txtUser, gbc);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(ThemeManager.FONT_HEADER);
        lblPass.setForeground(new Color(148, 163, 184));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 5, 0);
        form.add(lblPass, gbc);

        txtPass = new JPasswordField();
        setupTextField(txtPass);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 15, 0);
        form.add(txtPass, gbc);

        // Checkboxes Layout
        JPanel chkPanel = new JPanel(new GridLayout(1, 2));
        chkPanel.setOpaque(false);
        
        chkAdmin = new JCheckBox("Admin Login");
        chkAdmin.setFont(ThemeManager.FONT_SMALL);
        chkAdmin.setForeground(new Color(148, 163, 184));
        chkAdmin.setOpaque(false);
        chkPanel.add(chkAdmin);

        chkRemember = new JCheckBox("Remember Me");
        chkRemember.setFont(ThemeManager.FONT_SMALL);
        chkRemember.setForeground(new Color(148, 163, 184));
        chkRemember.setOpaque(false);
        chkPanel.add(chkRemember);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 20, 0);
        form.add(chkPanel, gbc);

        // 3. Login Button
        CustomButton btnLogin = new CustomButton("Log In");
        btnLogin.setPreferredSize(new Dimension(0, 40));
        btnLogin.addActionListener(e -> handleLogin());
        gbc.gridy = 8;
        gbc.insets = new Insets(0, 0, 15, 0);
        form.add(btnLogin, gbc);

        // 4. Reset & Register Links
        JPanel linkPanel = new JPanel(new BorderLayout());
        linkPanel.setOpaque(false);

        JLabel lblForgot = new JLabel("Forgot Password?");
        lblForgot.setFont(ThemeManager.FONT_SMALL);
        lblForgot.setForeground(ThemeManager.COLOR_ACCENT_HOVER);
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleForgotPassword();
            }
        });
        linkPanel.add(lblForgot, BorderLayout.WEST);

        JLabel lblRegister = new JLabel("Create Account");
        lblRegister.setFont(ThemeManager.FONT_SMALL);
        lblRegister.setForeground(ThemeManager.COLOR_ACCENT_HOVER);
        lblRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loginCardLayout.show(loginCardContainer, "REGISTER");
            }
        });
        linkPanel.add(lblRegister, BorderLayout.EAST);

        gbc.gridy = 9;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(linkPanel, gbc);

        loginCardContainer.add(form, "LOGIN");
    }

    private void initRegisterForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(15, 30, 15, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblTitle = new JLabel("Register Customer", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0);
        form.add(lblTitle, gbc);

        // Fields Setup
        txtRegUser = new JTextField();
        txtRegPass = new JPasswordField();
        txtRegName = new JTextField();
        txtRegEmail = new JTextField();
        txtRegPhone = new JTextField();
        txtRegIdProof = new JTextField();

        addLabelAndField(form, "Username", txtRegUser, 1, gbc);
        addLabelAndField(form, "Password (6+ chars)", txtRegPass, 3, gbc);
        addLabelAndField(form, "Full Name", txtRegName, 5, gbc);
        addLabelAndField(form, "Email Address", txtRegEmail, 7, gbc);
        addLabelAndField(form, "Phone (+123456789)", txtRegPhone, 9, gbc);
        addLabelAndField(form, "ID Proof (Aadhar/Passport)", txtRegIdProof, 11, gbc);

        // Register Button
        CustomButton btnRegister = new CustomButton("Create Account");
        btnRegister.setPreferredSize(new Dimension(0, 36));
        btnRegister.addActionListener(e -> handleRegister());
        gbc.gridy = 13;
        gbc.insets = new Insets(15, 0, 10, 0);
        form.add(btnRegister, gbc);

        // Back Link
        JLabel lblBack = new JLabel("Already registered? Login", JLabel.CENTER);
        lblBack.setFont(ThemeManager.FONT_SMALL);
        lblBack.setForeground(ThemeManager.COLOR_ACCENT_HOVER);
        lblBack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loginCardLayout.show(loginCardContainer, "LOGIN");
            }
        });
        gbc.gridy = 14;
        gbc.insets = new Insets(0, 0, 0, 0);
        form.add(lblBack, gbc);

        loginCardContainer.add(form, "REGISTER");
    }

    private void addLabelAndField(JPanel panel, String labelText, JTextField field, int gridy, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText);
        label.setFont(ThemeManager.FONT_SMALL);
        label.setForeground(new Color(148, 163, 184));
        gbc.gridy = gridy;
        gbc.insets = new Insets(0, 0, 2, 0);
        panel.add(label, gbc);

        setupTextField(field);
        gbc.gridy = gridy + 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(field, gbc);
    }

    private void setupTextField(JTextField field) {
        field.setBackground(new Color(15, 23, 42)); // Dark field
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setFont(ThemeManager.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }

    private void handleLogin() {
        String username = txtUser.getText().trim();
        String password = new String(txtPass.getPassword());
        boolean isAdmin = chkAdmin.isSelected();

        if (username.isEmpty() || password.isEmpty()) {
            ToastNotification.show(parent, "Please enter both username and password!", true);
            return;
        }

        if (controller.login(username, password, isAdmin)) {
            parent.handleLoginSuccess();
        } else {
            ToastNotification.show(parent, "Invalid username or password!", true);
        }
    }

    private void handleRegister() {
        String user = txtRegUser.getText().trim();
        String pass = new String(txtRegPass.getPassword());
        String name = txtRegName.getText().trim();
        String email = txtRegEmail.getText().trim();
        String phone = txtRegPhone.getText().trim();
        String idProof = txtRegIdProof.getText().trim();

        String result = controller.getCustomerService().registerCustomer(user, pass, name, email, phone, idProof);
        if (result.equals("SUCCESS")) {
            ToastNotification.show(parent, "Account created! Please log in now.", false);
            clearRegisterFields();
            loginCardLayout.show(loginCardContainer, "LOGIN");
            txtUser.setText(user);
        } else {
            ToastNotification.show(parent, result, true);
        }
    }

    private void handleForgotPassword() {
        String username = JOptionPane.showInputDialog(this, "Enter your username to recover password:", "Password Recovery Simulation", JOptionPane.QUESTION_MESSAGE);
        if (username == null) return;
        username = username.trim();
        if (username.isEmpty()) return;

        boolean isAdmin = chkAdmin.isSelected();
        String response = controller.simulateForgotPassword(username, isAdmin);
        if (response.startsWith("SUCCESS")) {
            JOptionPane.showMessageDialog(this, response.substring(8), "Recovery Simulated Successfully", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, response.substring(6), "Recovery Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void clearFields() {
        txtUser.setText("");
        txtPass.setText("");
        chkAdmin.setSelected(false);
    }

    private void clearRegisterFields() {
        txtRegUser.setText("");
        txtRegPass.setText("");
        txtRegName.setText("");
        txtRegEmail.setText("");
        txtRegPhone.setText("");
        txtRegIdProof.setText("");
    }
}
