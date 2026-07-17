package com.hotel.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SplashScreen extends JWindow {
    private final JProgressBar progressBar;
    private final JLabel statusLabel;

    public SplashScreen() {
        setSize(500, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Custom Gradient Background Panel
        GradientPanel mainPanel = new GradientPanel(new Color(15, 23, 42), new Color(30, 41, 59)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw a nice subtle glow or design element
                g2d.setColor(new Color(13, 148, 136, 40));
                g2d.fillOval(300, -50, 300, 300);
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Logo Icon (Simulated with text/shapes)
        JLabel logoLabel = new JLabel("🏨", JLabel.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        mainPanel.add(logoLabel, gbc);

        // 2. Title
        JLabel titleLabel = new JLabel("Smart Hotel System", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 5, 0);
        mainPanel.add(titleLabel, gbc);

        // 3. Subtitle
        JLabel subtitleLabel = new JLabel("Internship Portfolio Edition", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(148, 163, 184)); // Muted text
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 30, 0);
        mainPanel.add(subtitleLabel, gbc);

        // 4. Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setForeground(ThemeManager.COLOR_ACCENT);
        progressBar.setBackground(new Color(51, 65, 85));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(350, 6));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 30, 5, 30);
        mainPanel.add(progressBar, gbc);

        // 5. Status text
        statusLabel = new JLabel("Starting reservation engine...", JLabel.CENTER);
        statusLabel.setFont(ThemeManager.FONT_SMALL);
        statusLabel.setForeground(new Color(148, 163, 184));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 30, 0, 30);
        mainPanel.add(statusLabel, gbc);
    }

    public void setProgress(int value, String message) {
        // Update on EDT
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(value);
            statusLabel.setText(message);
        });
    }

    public void startLoading(Runnable onComplete) {
        setVisible(true);

        // Multi-threaded database preloading simulation
        new Thread(() -> {
            try {
                String[] steps = {
                    "Initializing modules...",
                    "Loading file structures...",
                    "Preloading rooms database...",
                    "Resolving active reservations...",
                    "Connecting analytical dashboards...",
                    "Launching login panel..."
                };

                for (int i = 0; i < steps.length; i++) {
                    Thread.sleep(600); // Simulate network or disk latency
                    int progress = (i + 1) * (100 / steps.length);
                    setProgress(progress, steps[i]);
                }
                
                Thread.sleep(300);
                setVisible(false);
                dispose();
                
                // Fire main application load on Event Dispatch Thread
                SwingUtilities.invokeLater(onComplete);
            } catch (InterruptedException e) {
                System.err.println("Splash screen interrupted: " + e.getMessage());
            }
        }).start();
    }
}
