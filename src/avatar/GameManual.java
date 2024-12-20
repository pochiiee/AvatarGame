package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.event.ActionListener;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GameManual extends JDialog {
    private BufferedImage backgroundImage;
    private int currentImageIndex = 0;
    private Timer autoNextTimer;
    private JLabel backgroundLabel;
    private String[] imagePaths;
    private boolean isUserSkipped = false; // Flag to check if user manually skipped
    private int sizeWidth = 1240;
    private int sizeHeight = 650;
    private JProgressBar progressBar;
    private JPanel loadingPanel;
    private Clip currentMusicClip;

    public GameManual(JFrame parent, String imagePath, Color buttonColor, Runnable onStartAction) {
        super(parent, true);

        // Remove title bar and add border
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // Dialog setup
        setSize(sizeWidth, sizeHeight);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(null);

        // Load the image for this instance
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            backgroundImage = null; // Use null as a fallback
        }

        // Custom panel for background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                // Enable high-quality rendering
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Draw the image
                if (backgroundImage != null) {
                    Image scaledImage = backgroundImage.getScaledInstance(680, 520, Image.SCALE_SMOOTH);
                    g2.drawImage(scaledImage, 10, 10, null);
                } else {
                    g2.setColor(Color.RED);
                    g2.drawString("Error: Image not found", 10, 20);
                }
            }
        };
        backgroundPanel.setBounds(0, 0, sizeWidth, sizeHeight);
        backgroundPanel.setLayout(null);
        backgroundPanel.setOpaque(false);
        add(backgroundPanel);

        // Add a JLabel to display images for game2Manual
        backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, sizeWidth, sizeHeight);
        backgroundPanel.add(backgroundLabel);

        // Start Button with hover effect
        JButton skipButton = new JButton("Next");
        skipButton.setFont(new Font("Arial", Font.BOLD, 20));
        skipButton.setFocusPainted(false);
        skipButton.setBackground(buttonColor);
        skipButton.setForeground(Color.WHITE);
        skipButton.setBounds(sizeWidth - 150, sizeHeight - 80, 100, 30);
        skipButton.setBorderPainted(false);
        skipButton.setOpaque(true);

        // Start button action
        skipButton.addActionListener(e -> {
            isUserSkipped = true; // User manually skipped
            goToNextImage();
        });
        backgroundPanel.add(skipButton);

        // Hover effect
        Color originalColor = buttonColor;
        Color hoverColor = buttonColor.darker();
        skipButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                skipButton.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                skipButton.setBackground(originalColor);
            }
        });

     // Initialize ProgressBar
        progressBar = new JProgressBar(0, 100);
        progressBar.setBounds(0, sizeHeight - 25, sizeWidth, 20); // Positioned dynamically
        progressBar.setValue(0);
        progressBar.setStringPainted(true); // Display progress percentage
        progressBar.setVisible(false); // Initially hidden
        add(progressBar);


        // Initialize Loading Screen Panel
        loadingPanel = new JPanel();
        loadingPanel.setBounds(0, 0, sizeWidth, sizeHeight);
        loadingPanel.setBackground(Color.BLACK);
        loadingPanel.setLayout(new BorderLayout());
        loadingPanel.setVisible(false); // Hidden by default

        JLabel loadingLabel = new JLabel("Loading, please wait...", SwingConstants.CENTER);
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loadingPanel.add(loadingLabel, BorderLayout.CENTER);
        add(loadingPanel);
    }

    private Clip playMusic(String musicFilePath) {
        Clip clip = null;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(musicFilePath));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop music continuously
        } catch (Exception e) {
            System.out.println("Error playing music: " + musicFilePath + " - " + e.getMessage());
        }
        return clip;
    }

    public void game1Manual() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
        }
        currentMusicClip = playMusic("src/wavfile/manualmusic.wav");

        imagePaths = new String[]{
            "src/Manual/water0.png",
            "src/Manual/water1.png",
            "src/Manual/water2.png",
            "src/Manual/water3.png",
            "src/Manual/water4.png"
        };

        setBackgroundImage(0); // Start with the first image
        autoTimer(); // Automatically advance through images

      
    }

   
  

    public void game2Manual() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
        }
        currentMusicClip = playMusic("src/wavfile/manualmusic.wav");

        imagePaths = new String[]{
            "src/Manual/earth0.png",
            "src/Manual/earth1.png",
            "src/Manual/earth2.png",
            "src/Manual/earth3.png",
            "src/Manual/earth4.png"
        };

        setBackgroundImage(0); // Start with the first image
        autoTimer(); // Automatically advance through images

      
    }
    
    public void game3Manual() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
        }
        currentMusicClip = playMusic("src/wavfile/manualmusic.wav");

        // Define the 5 image paths for the slideshow
        imagePaths = new String[]{
                "src/Manual/fire0.png",
                "src/Manual/fire1.png",
                "src/Manual/fire2.png",
                "src/Manual/fire3.png",
                "src/Manual/fire4.png",
                "src/Manual/fire5.png",
                "src/Manual/fire6.png",
                "src/Manual/fire7.png",
                "src/Manual/fire8.png"
        };
        setBackgroundImage(0); // Start with the first image
        autoTimer(); // Automatically advance through images

      
    }

    public void game4Manual() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
        }
        currentMusicClip = playMusic("src/wavfile/manualmusic.wav");

        // Define the 5 image paths for the slideshow
        imagePaths = new String[]{
                "src/Manual/air0.png",
                "src/Manual/air1.png",
                "src/Manual/air2.png",
                "src/Manual/air3.png",
                "src/Manual/air4.png",
                "src/Manual/air5.png",
                "src/Manual/air6.png"
        };
        
        setBackgroundImage(0); // Start with the first image
        autoTimer(); // Automatically advance through images

        
    }


    private void showLoadingScreenWithInigame() {
        try {
            // Load the inigame.png image
            BufferedImage img = ImageIO.read(new File("src/Manual/inigame.png"));
            if (img != null) {
                backgroundImage = img;

                // Update the background label with the inigame.png image
                Image scaledImage = img.getScaledInstance(backgroundLabel.getWidth(), backgroundLabel.getHeight(), Image.SCALE_SMOOTH);
                backgroundLabel.setIcon(new ImageIcon(scaledImage));

                // Show and simulate the progress bar
                progressBar.setVisible(true);
                simulateProgress();
            }
        } catch (IOException e) {
            System.out.println("Error loading inigame.png: " + e.getMessage());
        }
    }



    private void setBackgroundImage(int index) {
        try {
            BufferedImage img = ImageIO.read(new File(imagePaths[index]));
            if (img != null) {
                backgroundImage = img;

                // Display progress bar only for inigame.png
                if (imagePaths[index].endsWith("inigame.png")) {
                    progressBar.setVisible(true);
                    simulateProgress(); // Simulate loading progress
                } else {
                    progressBar.setVisible(false); // Hide progress bar for other images
                }

                Image scaledImage = img.getScaledInstance(backgroundLabel.getWidth(), backgroundLabel.getHeight(), Image.SCALE_SMOOTH);
                backgroundLabel.setIcon(new ImageIcon(scaledImage));
            }
        } catch (IOException e) {
            System.out.println("Error loading image: " + imagePaths[index]);
        }
    }


    private void simulateProgress() {
        Timer progressTimer = new Timer(100, null); // Smooth progress
        progressTimer.addActionListener(e -> {
            int currentValue = progressBar.getValue();
            if (currentValue < 100) {
                progressBar.setValue(currentValue + 2); // Increment for smoother progress
            } else {
                progressTimer.stop(); // Stop when complete
                progressBar.setVisible(false); // Hide progress bar
                if (currentMusicClip != null && currentMusicClip.isRunning()) {
                    currentMusicClip.stop(); // Ensure music stops
                }
                // Transition only after progress completes
                new Timer(1000, evt -> dispose()).start(); // Close dialog after 1 second
            }
        });
        progressBar.setVisible(true); // Make progress bar visible
        progressTimer.start();
    }

    private void autoTimer() {
        autoNextTimer = new Timer(5000, e -> {
            if (currentImageIndex < imagePaths.length - 1) {
                currentImageIndex++;
                setBackgroundImage(currentImageIndex);
            } else {
                autoNextTimer.stop();
                showLoadingScreenWithInigame(); // Transition explicitly handled here
            }
        });
        autoNextTimer.start();
    }


    @Override
    public void dispose() {
        if (currentMusicClip != null && currentMusicClip.isRunning()) {
            currentMusicClip.stop();
        }
        super.dispose();
    }


    private void showLoadingScreen() {
        loadingPanel.setVisible(true);
        new Timer(3000, e -> {
            loadingPanel.setVisible(false);
            dispose();
        }).start();
    }

    private void goToNextImage() {
        if (autoNextTimer != null) {
            autoNextTimer.stop(); // Stop the timer to avoid interference
        }

        if (currentImageIndex < imagePaths.length - 1) {
            currentImageIndex++;
            setBackgroundImage(currentImageIndex);
        } else {
            // Show the loading screen with progress bar
            showLoadingScreenWithInigame();
            new Timer(4000, e -> dispose()).start(); // Dispose after showing loading screen
        }
    }


}
