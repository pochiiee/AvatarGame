package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class StoryWindow extends JFrame {

    private int currentImageIndex = 0; // Para malaman kung nasan na sa images
    private JLabel backgroundLabel;   // Background label para sa images
    private String[] imagePaths = {   // Image paths
        "src/story/story1.png",
        "src/story/story2.png",
        "src/story/story3.png",
        "src/story/story4.png",
    };
    
    private Clip clip; // Clip para sa background music
    private long lastStopPosition = 0; // Variable to store last stop position in microseconds

    public StoryWindow() {
        // I-remove yung decorations ng window (para walang resizable at X/minimize/maximize buttons)
        setUndecorated(true);

        // I-set to fullscreen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(this);

        // Hindi na pwedeng i-resize ang window
        setResizable(false);

        // Pag walang decoration, pwede natin i-center ang window manually
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height); // Fullscreen window dimensions

        // I-setup ang layout ng JFrame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Add ng background label
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, screenSize.width, screenSize.height); // Fullscreen background
        setBackgroundImage(currentImageIndex); // First background image
        add(backgroundLabel);

        // Add ng "Next" button
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(screenSize.width - 180, screenSize.height - 100, 100, 30); // Adjusted X and Y position
        nextButton.setBackground(new Color(173, 216, 230)); // Light blue
        nextButton.setOpaque(true);
        nextButton.setBorderPainted(false);

        // Hover effect para sa "Next" button
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

        // Action listener para sa "Next" button
        nextButton.addActionListener(e -> {
            if (currentImageIndex == 1) { // After 0.17 seconds
                stopBackgroundMusic(); // Stop music at 0.17
            } else if (currentImageIndex == 2) { // After 0.24 seconds
                stopBackgroundMusic(); // Stop music at 0.24
            }

            if (currentImageIndex < imagePaths.length - 1) {
                currentImageIndex++;
                setBackgroundImage(currentImageIndex); // Update image
                playBackgroundMusic(); // Continue music from the next point after the stop
            } else {
                // Proceed to next window or close
                new RoadMapWindow();
                dispose();
            }
        });

        backgroundLabel.add(nextButton); // Attach the button to the background
        setVisible(true); // I-display ang window

        // Play the background music for the first time
        playBackgroundMusic();
    }

    // Method para mag-load ng background image
    private void setBackgroundImage(int index) {
        ImageIcon icon = new ImageIcon(imagePaths[index]);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Scale image para magfit sa buong screen
        Image scaledImage = icon.getImage().getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
        backgroundLabel.setIcon(new ImageIcon(scaledImage));
    }

    // Method para mag-play ng background music
    private void playBackgroundMusic() {
        try {
            File musicPath = new File("src/.wav"); // Path to your .wav file
            if (musicPath.exists()) {
                if (clip == null || !clip.isRunning()) { // If music is not already playing, start it
                    AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                    clip = AudioSystem.getClip();
                    clip.open(audioInput);
                    clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music continuously
                    clip.setMicrosecondPosition(lastStopPosition); // Start from the last stopped position
                }
            } else {
                System.out.println("Music file not found.");
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method para mag-stop ng music
    private void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            lastStopPosition = clip.getMicrosecondPosition(); // Save the current position
            clip.stop(); // Stop the music
        }
    }
}
