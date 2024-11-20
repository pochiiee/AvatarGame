package avatar;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;

public class WelcomeWindow extends JFrame {

    private Image backgroundImage;

    public WelcomeWindow() {
        // Load the background image
        ImageIcon icon = new ImageIcon("src/welcomee.png");
        backgroundImage = icon.getImage();

        // Get the image's original dimensions
        int imageWidth = backgroundImage.getWidth(this);
        int imageHeight = backgroundImage.getHeight(this);

        // Set the window size based on the image's aspect ratio
        double aspectRatio = (double) imageWidth / imageHeight;
        int windowWidth = 800; // Desired window width
        int windowHeight = (int) (windowWidth / aspectRatio);

        setTitle("Welcome Gamer");
        setSize(windowWidth, windowHeight); // Set the size based on the calculated aspect ratio
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Use null layout for absolute positioning
     
    
     // Add Start Button
        JButton startButton = new JButton("Start");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBounds((windowWidth - 150) / 2, windowHeight - 100, 150, 40); // Position the button

        // Style the button
        startButton.setBackground(new Color(227, 141, 60)); // Lighter brownish-orange color
        startButton.setOpaque(true);
        startButton.setBorderPainted(false); // Optional: Remove the border

        // Hover effect
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startButton.setBackground(new Color(181, 101, 29)); // Darker brownish-orange when hovered
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startButton.setBackground(new Color(227, 141, 60)); // Back to lighter brownish-orange
            }
        });



        add(startButton);

        // Action Listener for Start Button
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginWindow(); // Open Login Window
                dispose(); // Close this window
            }
        });

        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // Get the insets (title bar height and borders)
        Insets insets = getInsets();
        int titleBarHeight = insets.top;

        // Adjust background image to start below the title bar
        g.drawImage(backgroundImage, 0, titleBarHeight, getWidth(), getHeight() - titleBarHeight, this);

        // Set text properties
        g.setColor(new Color(205, 133, 63)); // Light brown text color
        g.setFont(new Font("Serif", Font.BOLD, 50)); // Elegant font
    }

    public static void main(String[] args) {
        new WelcomeWindow();
    }
}
