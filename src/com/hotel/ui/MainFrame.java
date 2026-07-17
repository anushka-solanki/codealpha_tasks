package com.hotel.ui;

import com.hotel.controller.HotelController;
import com.hotel.model.Customer;
import com.hotel.model.User;
import com.hotel.util.DateTimeUtil;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame implements ThemeManager.ThemeListener {
    private final HotelController controller;
    
    // Layout Elements
    private final CardLayout mainCardLayout;
    private final JPanel mainContainer;
    
    // Sub-panels
    private LoginPanel loginPanel;
    private JPanel workspacePanel;
    private CardLayout workspaceCardLayout;
    private JPanel workspaceContainer;
    
    // Sidebar Navigation Buttons
    private CustomButton btnDash, btnRooms, btnBook, btnCust, btnPay, btnAdmin, btnLogout;
    private JLabel lblUserRole;
    private JLabel lblClock;
    private JLabel lblWeather;

    public MainFrame(HotelController controller) {
        this.controller = controller;
        
        setTitle("Smart Hotel Reservation System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(1150, 750));
        setLocationRelativeTo(null);
        
        mainCardLayout = new CardLayout();
        mainContainer = new JPanel(mainCardLayout);
        setContentPane(mainContainer);

        // Window listener to save data and backup on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmAndExit();
            }
        });

        // Initialize Panels
        initLoginPanel();
        initWorkspacePanel();

        // Add to root layout
        mainContainer.add(loginPanel, "LOGIN");
        mainContainer.add(workspacePanel, "WORKSPACE");

        // Set initial view
        mainCardLayout.show(mainContainer, "LOGIN");

        // Register theme switcher
        ThemeManager.addThemeListener(this);
        applyTheme();

        // Start Clock Thread
        startClockThread();
    }

    private void initLoginPanel() {
        loginPanel = new LoginPanel(controller, this);
    }

    private void initWorkspacePanel() {
        workspacePanel = new JPanel(new BorderLayout());
        
        // 1. Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setBackground(ThemeManager.getSidebarBackground());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 15, 25, 15));

        // Sidebar Logo Header
        JLabel logo = new JLabel("🏨 SMART HOTEL", JLabel.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        
        JLabel subLogo = new JLabel("Reservation System", JLabel.CENTER);
        subLogo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLogo.setForeground(ThemeManager.COLOR_ACCENT_HOVER);
        subLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(subLogo);

        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        // Navigation Options
        btnDash = createSidebarButton("📊  Dashboard", "DASHBOARD");
        btnRooms = createSidebarButton("🛌  Rooms", "ROOMS");
        btnBook = createSidebarButton("📅  Reservations", "BOOKINGS");
        btnCust = createSidebarButton("👤  Customers", "CUSTOMERS");
        btnPay = createSidebarButton("💳  Payment Panel", "PAYMENTS");
        btnAdmin = createSidebarButton("⚙️  Admin Panel", "ADMIN");
        
        sidebar.add(btnDash);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnRooms);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnBook);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnCust);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnPay);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(btnAdmin);

        sidebar.add(Box.createVerticalGlue());

        // Theme Switch Toggle
        CustomButton btnTheme = new CustomButton("Toggle Light/Dark Theme", new Color(51, 65, 85), new Color(71, 85, 105));
        btnTheme.setRadius(8);
        btnTheme.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnTheme.addActionListener(e -> {
            ThemeManager.setDarkMode(!ThemeManager.isDarkMode());
            ToastNotification.show(this, "Theme switched to " + (ThemeManager.isDarkMode() ? "Dark" : "Light") + " Mode!", false);
        });
        sidebar.add(btnTheme);

        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        // Logout
        btnLogout = new CustomButton("🚪 Logout", new Color(239, 68, 68), new Color(220, 38, 38));
        btnLogout.setRadius(8);
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogout.addActionListener(e -> handleLogout());
        sidebar.add(btnLogout);

        workspacePanel.add(sidebar, BorderLayout.WEST);

        // 2. Main Center Container (CardLayout)
        workspaceCardLayout = new CardLayout();
        workspaceContainer = new JPanel(workspaceCardLayout);

        workspacePanel.add(workspaceContainer, BorderLayout.CENTER);

        // 3. Top Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(ThemeManager.getBorderColor());
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        lblUserRole = new JLabel("Welcome, Guest");
        lblUserRole.setFont(ThemeManager.FONT_HEADER);
        headerPanel.add(lblUserRole, BorderLayout.WEST);

        // Header Right Panel (Clock and Weather)
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        headerRight.setOpaque(false);

        lblWeather = new JLabel("⛅ Loading Weather...");
        lblWeather.setFont(ThemeManager.FONT_BODY);
        headerRight.add(lblWeather);

        lblClock = new JLabel("Loading Time...");
        lblClock.setFont(ThemeManager.FONT_BODY_BOLD);
        headerRight.add(lblClock);

        headerPanel.add(headerRight, BorderLayout.EAST);
        workspacePanel.add(headerPanel, BorderLayout.NORTH);
    }

    private CustomButton createSidebarButton(String text, String cardName) {
        CustomButton btn = new CustomButton(text, new Color(30, 41, 59), ThemeManager.COLOR_ACCENT);
        btn.setRadius(8);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 20, 0, 0));
        btn.addActionListener(e -> {
            workspaceCardLayout.show(workspaceContainer, cardName);
            updateSelectedSidebarButton(btn);
            refreshCurrentPanel(cardName);
        });
        return btn;
    }

    private void updateSelectedSidebarButton(CustomButton selected) {
        CustomButton[] btns = {btnDash, btnRooms, btnBook, btnCust, btnPay, btnAdmin};
        for (CustomButton b : btns) {
            if (b == selected) {
                b.setBackground(ThemeManager.COLOR_ACCENT);
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(new Color(30, 41, 59));
                b.setForeground(new Color(148, 163, 184));
            }
        }
    }

    public void handleLoginSuccess() {
        mainCardLayout.show(mainContainer, "WORKSPACE");
        User user = controller.getLoggedInUser();
        if (user != null) {
            lblUserRole.setText("Logged in as: " + user.getFullName() + " (" + (controller.isAdminSession() ? "Admin" : "Customer") + ")");
        }

        // Initialize/Re-create Workspace Content Panels based on logged-in user
        workspaceContainer.removeAll();
        
        DashboardPanel dash = new DashboardPanel(controller, this);
        RoomPanel rooms = new RoomPanel(controller, this);
        BookingPanel bookings = new BookingPanel(controller, this);
        CustomerPanel customer = new CustomerPanel(controller, this);
        PaymentPanel payment = new PaymentPanel(controller, this);
        AdminPanel admin = new AdminPanel(controller, this);

        workspaceContainer.add(dash, "DASHBOARD");
        workspaceContainer.add(rooms, "ROOMS");
        workspaceContainer.add(bookings, "BOOKINGS");
        workspaceContainer.add(customer, "CUSTOMERS");
        workspaceContainer.add(payment, "PAYMENTS");
        workspaceContainer.add(admin, "ADMIN");

        // Access Control checks
        if (controller.isAdminSession()) {
            btnAdmin.setVisible(true);
            btnCust.setText("👤  Customers Database");
        } else {
            btnAdmin.setVisible(false);
            btnCust.setText("👤  My Profile");
        }

        // Show Dashboard first
        workspaceCardLayout.show(workspaceContainer, "DASHBOARD");
        updateSelectedSidebarButton(btnDash);
        
        applyTheme();
        revalidate();
        repaint();

        ToastNotification.show(this, "Welcome " + user.getFullName() + "! You have logged in successfully.", false);
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            controller.logout();
            mainCardLayout.show(mainContainer, "LOGIN");
            loginPanel.clearFields();
            ToastNotification.show(this, "You have been logged out safely.", false);
        }
    }

    private void refreshCurrentPanel(String cardName) {
        Component[] comps = workspaceContainer.getComponents();
        for (Component c : comps) {
            if (c.isVisible()) {
                if (c instanceof DashboardPanel) {
                    ((DashboardPanel) c).refreshStats();
                } else if (c instanceof RoomPanel) {
                    ((RoomPanel) c).refreshRooms();
                } else if (c instanceof BookingPanel) {
                    ((BookingPanel) c).refreshData();
                } else if (c instanceof CustomerPanel) {
                    ((CustomerPanel) c).refreshProfile();
                } else if (c instanceof AdminPanel) {
                    ((AdminPanel) c).refreshData();
                } else if (c instanceof PaymentPanel) {
                    ((PaymentPanel) c).refreshData();
                }
            }
        }
    }

    private void startClockThread() {
        new Thread(() -> {
            String[] weatherSimulations = {
                "⛅ Sunny, 72°F",
                "🌧️ Rainy, 64°F",
                "☁️ Overcast, 68°F",
                "☀️ Clear Sky, 78°F",
                "🌬️ Windy, 70°F"
            };
            int weatherIndex = 0;
            long tickCount = 0;

            while (true) {
                try {
                    String timeStr = DateTimeUtil.getCurrentDateTimeString();
                    lblClock.setText(timeStr);
                    
                    // Switch simulated weather every 60 seconds
                    if (tickCount % 60 == 0) {
                        lblWeather.setText(weatherSimulations[weatherIndex]);
                        weatherIndex = (weatherIndex + 1) % weatherSimulations.length;
                    }

                    tickCount++;
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private void confirmAndExit() {
        int choice = JOptionPane.showConfirmDialog(this, "Save changes and exit application?", "Exit Confirmation", JOptionPane.YES_NO_CANCEL_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            controller.saveAllData();
            controller.getBackupService().performAutoBackup();
            System.exit(0);
        } else if (choice == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

    @Override
    public void onThemeChanged() {
        applyTheme();
    }

    private void applyTheme() {
        Color bg = ThemeManager.getBackground();
        Color cardBg = ThemeManager.getCardBackground();
        Color text = ThemeManager.getTextPrimary();

        workspacePanel.setBackground(bg);
        workspaceContainer.setBackground(bg);
        lblUserRole.setForeground(text);
        lblClock.setForeground(text);
        lblWeather.setForeground(ThemeManager.getTextSecondary());
        
        // Find panels inside header and color them
        for (Component c : workspacePanel.getComponents()) {
            if (c instanceof JPanel && c != workspaceContainer) {
                c.setBackground(cardBg);
                for (Component child : ((JPanel) c).getComponents()) {
                    if (child instanceof JPanel) {
                        child.setBackground(cardBg);
                    }
                }
            }
        }

        SwingUtilities.updateComponentTreeUI(this);
    }
}
