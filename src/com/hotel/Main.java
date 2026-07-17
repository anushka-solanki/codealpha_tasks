package com.hotel;

import com.hotel.controller.HotelController;
import com.hotel.ui.MainFrame;
import com.hotel.ui.SplashScreen;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // Set cross-platform look and feel for consistent rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not establish system Look & Feel: " + e.getMessage());
        }

        // 1. Instantiate Central MVC Controller
        HotelController controller = new HotelController();

        // 2. Launch Splash Screen
        SplashScreen splash = new SplashScreen();
        splash.startLoading(() -> {
            // 3. Callback upon completion: Load Main App Frame
            MainFrame mainFrame = new MainFrame(controller);
            mainFrame.setVisible(true);
        });
    }
}
