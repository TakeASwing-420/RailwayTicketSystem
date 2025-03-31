package com.example;

import com.example.ui.MainFrame;
import com.example.utils.FileUtils;
import javax.swing.*;

public class RailwayTicketSystem {
    public static void main(String[] args) {
        try {
            // Initialize data files
            FileUtils.initializeFiles();
            
            // Start the GUI in the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame frame = new MainFrame();
                    // Configure frame properties
                    frame.setLocationRelativeTo(null); // Center on screen
                    frame.setVisible(true);
                } catch (Exception e) {
                    showFatalError("Failed to initialize UI: " + e.getMessage());
                    e.printStackTrace();
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            showFatalError("Failed to initialize application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void showFatalError(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Critical Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}