package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MissionFailedDialog {

    private String gameName;
    private RoadMapWindow roadMapWindow;
    private Game1 game1Window;  // Reference to Game1 window

    public MissionFailedDialog(String gameName, RoadMapWindow roadMapWindow, Game1 game1Window) {
        this.gameName = gameName;
        this.roadMapWindow = roadMapWindow;
        this.game1Window = game1Window;  // Store the Game1 window reference
    }

    public void showMissionFailed() {
        // Create a modal dialog for the failed screen
        JDialog failedDialog = new JDialog((Frame) null, true); // Parent as null (no specific parent)
        failedDialog.setSize(400, 250); // Set the dialog size
        failedDialog.setLayout(null); // Use absolute positioning
        failedDialog.setUndecorated(true); // Remove the title bar
        failedDialog.setLocationRelativeTo(null); // Center the dialog

        // Add the failed image
        JLabel failedImageLabel = new JLabel();
        failedImageLabel.setBounds(0, 0, 400, 260);

        int borderSize = 3; // Size ng border
        failedImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, borderSize));

        try {
            ImageIcon failedIcon = new ImageIcon("src/failed.png");

            int imageWidth = 400 - (2 * borderSize);
            int imageHeight = 260 - (2 * borderSize);
            Image scaledFailedImage = failedIcon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);

            ImageIcon scaledIcon = new ImageIcon(scaledFailedImage);

            failedImageLabel.setIcon(scaledIcon);
            failedImageLabel.setHorizontalAlignment(JLabel.CENTER);
            failedImageLabel.setVerticalAlignment(JLabel.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Failed to load 'failed.png': " + e.getMessage(),
                    "Image Load Error", JOptionPane.ERROR_MESSAGE);
        }

        failedDialog.add(failedImageLabel);

        // Add the OK button overlaying the image
        JButton okButton = new JButton("OK");
        okButton.setBounds(165, 220, 70, 25); // Button positioning
        okButton.setFocusPainted(false);
        Color normalColor = new Color(165, 42, 42); // Original color
        Color hoverColor = normalColor.darker();    // Darker color for hover effect
        okButton.setBackground(normalColor);
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBorderPainted(false); // Remove button border
        okButton.setOpaque(true);

        // Add hover effect to the button
        addHoverEffect(okButton, normalColor, hoverColor);

        // Add action listener to transition to the respective game window
        okButton.addActionListener(e -> {
            failedDialog.dispose(); // Close the failed dialog

            // Open the respective game window based on the gameName
            if ("Game1".equals(gameName)) {
                if (game1Window != null) {
                    game1Window.dispose();  // Dispose of the Game1 window if it's not null
                }
                new WelcomeWindow().setVisible(true); // Open the WelcomeWindow
            } else {
                // Handle other game windows if needed
            }
        });

        // Add components to the label
        failedImageLabel.setLayout(null); // Enable absolute positioning for components inside the label
        failedImageLabel.add(okButton); // Add the button to the image label

        failedDialog.setVisible(true); // Show the dialog
    }

    // Method to add hover effect to the button
    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor); // Change background color on hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor); // Change background color back to normal when mouse exits
            }
        });
    }
}
