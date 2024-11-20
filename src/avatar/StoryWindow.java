package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class StoryWindow extends JFrame {

    private int currentImageIndex = 0; // Track current image
    private JLabel backgroundLabel;   // Label for the background
    private String[] imagePaths = {   // Paths for the images
        "src/story/story1.png",
        "src/story/story2.png",
        "src/story/story3.png",
        "src/story/story4.png",
    };
    private Image backgroundImage;    // Current background image

    public StoryWindow() {
        // Load the first image to get its dimensions
        loadImage(currentImageIndex); // Load initial image

        int imageWidth = backgroundImage.getWidth(null);
        int imageHeight = backgroundImage.getHeight(null);

        // Adjust the window size to match the LoginWindow
        int windowWidth = 800; // Same width as LoginWindow
        int windowHeight = 490;

        // Set the window size based on the calculated aspect ratio
        setTitle("Story");
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Use null layout for absolute positioning

        // Add the background label
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, windowWidth, windowHeight); // Match the label size with window size
        backgroundLabel.setVerticalAlignment(SwingConstants.BOTTOM); // Align image to bottom
        setBackgroundImage(currentImageIndex); // Set initial background image
        add(backgroundLabel);
    
        // Next button
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(windowWidth - 120, windowHeight - 60, 100, 30); // Smaller size and lower position
        nextButton.setBackground(new Color(173, 216, 230)); // Light blue
        nextButton.setOpaque(true);
        nextButton.setBorderPainted(false);

        nextButton.setBorderPainted(false);

        // Hover effect
        nextButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                nextButton.setBackground(new Color(135, 206, 235)); // Lighter blue
            }

            @Override
            public void mouseExited(MouseEvent e) {
                nextButton.setBackground(new Color(173, 216, 230)); // Original blue
            }
        });

        // Next button action
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentImageIndex < imagePaths.length - 1) {
                    currentImageIndex++;
                    loadImage(currentImageIndex); // Update image
                    setBackgroundImage(currentImageIndex); // Update background label
                } else {
                    new RoadMapWindow(); // Proceed to Road Map
                    dispose();
                }
            }
        });

        backgroundLabel.add(nextButton); // Add the button to the background label
    }

    // Method to load and set image
    private void loadImage(int index) {
        ImageIcon icon = new ImageIcon(imagePaths[index]);
        backgroundImage = icon.getImage();
    }

    private void setBackgroundImage(int index) {
        ImageIcon icon = new ImageIcon(imagePaths[index]);
        int windowWidth = getWidth();
        int windowHeight = getHeight();

        // Scale the image to fit the entire window, preserving the aspect ratio
        Image scaledImage = icon.getImage().getScaledInstance(windowWidth, windowHeight, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
    }

}
