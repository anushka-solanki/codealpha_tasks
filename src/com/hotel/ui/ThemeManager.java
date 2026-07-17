package com.hotel.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    public interface ThemeListener {
        void onThemeChanged();
    }

    private static boolean isDarkMode = true;
    private static final List<ThemeListener> listeners = new ArrayList<>();

    // Common Colors
    public static final Color COLOR_ACCENT = new Color(13, 148, 136);      // Teal Accent
    public static final Color COLOR_ACCENT_HOVER = new Color(20, 184, 166);
    
    // Status Colors
    public static final Color STATUS_AVAILABLE = new Color(16, 185, 129);   // Green
    public static final Color STATUS_BOOKED = new Color(239, 68, 68);       // Red
    public static final Color STATUS_MAINTENANCE = new Color(245, 158, 11);  // Yellow/Orange
    public static final Color STATUS_RESERVED = new Color(59, 130, 246);     // Blue

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    public static void setDarkMode(boolean dark) {
        if (isDarkMode != dark) {
            isDarkMode = dark;
            notifyListeners();
        }
    }

    public static void addThemeListener(ThemeListener l) {
        listeners.add(l);
    }

    public static void removeThemeListener(ThemeListener l) {
        listeners.remove(l);
    }

    private static void notifyListeners() {
        for (ThemeListener l : listeners) {
            l.onThemeChanged();
        }
    }

    // Dynamic Color Getters
    public static Color getBackground() {
        return isDarkMode ? new Color(15, 23, 42) : new Color(241, 245, 249); // Dark slate vs Light gray
    }

    public static Color getCardBackground() {
        return isDarkMode ? new Color(30, 41, 59) : new Color(255, 255, 255); // Dark blue-slate vs White
    }

    public static Color getTextPrimary() {
        return isDarkMode ? new Color(248, 250, 252) : new Color(15, 23, 42); // White vs Dark Slate
    }

    public static Color getTextSecondary() {
        return isDarkMode ? new Color(148, 163, 184) : new Color(100, 116, 139); // Muted gray vs Muted slate
    }

    public static Color getBorderColor() {
        return isDarkMode ? new Color(51, 65, 85) : new Color(226, 232, 240); // Dark border vs Light border
    }

    public static Color getSidebarBackground() {
        // Sidebar stays dark for premium visual contrast in both modes
        return new Color(15, 23, 42);
    }

    public static Color getSelectionColor() {
        return isDarkMode ? new Color(51, 65, 85) : new Color(226, 232, 240);
    }
}
