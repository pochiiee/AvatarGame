package avatar;

import javax.swing.*;
import java.awt.*;

public class RoadMapWindow extends JFrame {
    private Image backgroundImage;
    private boolean game1Completed = false;
    private boolean game2Completed = false;
    private boolean game3Completed = false;
    private boolean game4Completed = false;

    public RoadMapWindow() {
        // Load the background image
        ImageIcon icon = new ImageIcon("src/roadmap.png");
        backgroundImage = icon.getImage();

        // Get the image's original dimensions
        int imageWidth = backgroundImage.getWidth(this);
        int imageHeight = backgroundImage.getHeight(this);
        // Set up the frame
        setTitle("Road Map");
        setSize(Toolkit.getDefaultToolkit().getScreenSize()); // Set to full-screen size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Disable resizing
        setResizable(false);

        // Set the frame to full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH); 

        // Create a panel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        backgroundPanel.setLayout(null);

        // Create buttons and add to the panel
        JButton game1Button = new JButton("Game 1");
        game1Button.setBounds(95, 357, 100, 30);
        backgroundPanel.add(game1Button);

        JButton game2Button = new JButton("Game 2");
        game2Button.setBounds(172, 199, 100, 30);
        game2Button.setEnabled(false); // Initially locked
        backgroundPanel.add(game2Button);

        JButton game3Button = new JButton("Game 3");
        game3Button.setBounds(390, 137, 100, 30);
        game3Button.setEnabled(false); // Initially locked
        backgroundPanel.add(game3Button);

        JButton game4Button = new JButton("Game 4");
        game4Button.setBounds(501, 357, 100, 30);
        game4Button.setEnabled(false); // Initially locked
        backgroundPanel.add(game4Button);

        // Add action listeners for the buttons
        game1Button.addActionListener(e -> {
            playGame1();
        });

        game2Button.addActionListener(e -> {
            playGame2();
        });

        game3Button.addActionListener(e -> {
            playGame3();
        });

        game4Button.addActionListener(e -> {
            playGame4();
        });

        // Add the background panel to the frame
        getContentPane().add(backgroundPanel);

        setVisible(true);
    }
    
    private void playGame1() {
        // Launch Game 1
        new Game1(); // Initialize Game 1
        dispose(); // Close the current window (RoadMapWindow)
    }

    private void playGame2() {
        // Play Game 2 logic
        JOptionPane.showMessageDialog(null, "Playing Game 2...");
        game2Completed = true; // Set game2 completion flag
        unlockNextGame(); // Unlock next game button
    }

    private void playGame3() {
        // Play Game 3 logic
        JOptionPane.showMessageDialog(null, "Playing Game 3...");
        game3Completed = true; // Set game3 completion flag
        unlockNextGame(); // Unlock next game button
    }

    private void playGame4() {
        // Play Game 4 logic
        JOptionPane.showMessageDialog(null, "Playing Game 4...");
        game4Completed = true; // Set game4 completion flag
        unlockNextGame(); // All games completed
    }

    private void unlockNextGame() {
        if (game1Completed) {
            // Unlock Game 2
            ((JButton) getComponent(1)).setEnabled(true);
        }
        if (game2Completed) {
            // Unlock Game 3
            ((JButton) getComponent(2)).setEnabled(true);
        }
        if (game3Completed) {
            // Unlock Game 4
            ((JButton) getComponent(3)).setEnabled(true);
        }
    }
}
