package com.hotel.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class CustomButton extends JButton {
    private Color baseColor = ThemeManager.COLOR_ACCENT;
    private Color hoverColor = ThemeManager.COLOR_ACCENT_HOVER;
    private Color textColor = Color.WHITE;
    private boolean isHovered = false;
    private int radius = 10;

    public CustomButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(textColor);
        setFont(ThemeManager.FONT_BODY_BOLD);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    public CustomButton(String text, Color baseColor, Color hoverColor) {
        this(text);
        this.baseColor = baseColor;
        this.hoverColor = hoverColor;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (isEnabled()) {
            g2.setColor(isHovered ? hoverColor : baseColor);
        } else {
            g2.setColor(ThemeManager.getBorderColor());
        }
        
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        
        // Paint label text
        super.paintComponent(g);
    }
}
