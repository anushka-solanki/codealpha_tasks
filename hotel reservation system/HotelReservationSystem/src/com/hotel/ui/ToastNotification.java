package com.hotel.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class ToastNotification extends JWindow {
    private float opacity = 1.0f;
    private final Timer fadeOutTimer;

    public ToastNotification(Window parent, String message, boolean isError) {
        super(parent);
        
        setLayout(new GridBagLayout());
        getContentPane().setBackground(isError ? new Color(239, 68, 68, 230) : new Color(16, 185, 129, 230)); // Red vs Green translucent
        
        JLabel label = new JLabel(message);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(label);
        
        pack();
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));

        // Center relative to parent
        if (parent != null) {
            int x = parent.getX() + (parent.getWidth() - getWidth()) / 2;
            int y = parent.getY() + parent.getHeight() - getHeight() - 50; // 50px above bottom
            setLocation(x, y);
        }

        setVisible(true);

        // Auto-close and fade out timer after 2 seconds
        fadeOutTimer = new Timer(50, null);
        fadeOutTimer.addActionListener(e -> {
            opacity -= 0.05f;
            if (opacity <= 0.0f) {
                fadeOutTimer.stop();
                dispose();
            } else {
                setOpacity(opacity);
            }
        });

        Timer startFadeTimer = new Timer(2000, e -> fadeOutTimer.start());
        startFadeTimer.setRepeats(false);
        startFadeTimer.start();
    }

    public static void show(Window parent, String message, boolean isError) {
        // Run on Event Dispatch Thread to prevent concurrency issues
        javax.swing.SwingUtilities.invokeLater(() -> new ToastNotification(parent, message, isError));
    }
}
