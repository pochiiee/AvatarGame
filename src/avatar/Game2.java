package avatar;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Game2 extends JPanel implements ActionListener, KeyListener {

    int boardWidth;
    int boardHeight;

    // Images
    Image backgroundImg;
    Image birdImg;
    Image cloudImg;
    Image energyIcon;
    Image missionCompleteImg;
    Image missionFailedImg;

    // Bird dimensions
    int birdX;
    int birdY;
    int birdWidth = 75;
    int birdHeight = 55;

    // Cloud dimensions
    int cloudWidth;
    int cloudHeight;

    // Game entities
    Bird bird;
    ArrayList<Obstacle> obstacles;
    Random random = new Random();

    // Timers
    Timer gameLoop;
    Timer placeObstacleTimer;

    // Game state
    boolean gameOver = false;
    double score = 0;
    boolean gameStarted = false;

    // Buttons
    JButton playButton;

    // Physics
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    // Energy management
    private int energy = 3; // Player starts with 3 energy points

    // Mission
    private final int MISSION_SCORE = 200;

    public Game2() {
        // Get screen dimensions
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        boardWidth = screenSize.width;
        boardHeight = screenSize.height;

        birdX = boardWidth / 8;
        birdY = boardHeight / 2;

        // Load images
        try {
            backgroundImg = new ImageIcon(getClass().getResource("/air/flappybirdbg.jpg")).getImage();
            birdImg = new ImageIcon(getClass().getResource("/air/flappybird.png")).getImage();
            cloudImg = new ImageIcon(getClass().getResource("/air/cloud.png")).getImage();
            energyIcon = new ImageIcon(getClass().getResource("/energy.png")).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            missionCompleteImg = new ImageIcon(getClass().getResource("/missioncomplete.png")).getImage().getScaledInstance(305, 220, Image.SCALE_SMOOTH);
            missionFailedImg = new ImageIcon(getClass().getResource("/failed.png")).getImage().getScaledInstance(305, 220, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading images: " + e.getMessage(),
                    "Image Load Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Adjust cloud dimensions
        cloudWidth = boardWidth / 12;
        cloudHeight = (int) ((double) cloudImg.getHeight(null) / cloudImg.getWidth(null) * cloudWidth);

        bird = new Bird(birdImg);
        obstacles = new ArrayList<>();

        // Set layout to null for absolute positioning
        setLayout(null);
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        // Initialize Timers
        gameLoop = new Timer(16, this); // Around 60 FPS
        placeObstacleTimer = new Timer(2000, e -> placeObstacles());
        
        // Show the start screen first before the main game window
        SwingUtilities.invokeLater(() -> {
            StartScreen startScreen = new StartScreen(null, this);
            startScreen.setVisible(true);

            // Once start screen is disposed, set up the game frame
            JFrame frame = new JFrame("Flappy Bird Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setUndecorated(false); // Keep the title bar
            frame.setResizable(false); // Disable resizing
            frame.setSize(boardWidth, boardHeight); // Ensure window size matches game size

            frame.add(this);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });


      
    }

    // Method to dispose of the play button
    private void disposePlayButton() {
        remove(playButton);
        repaint();
        requestFocusInWindow(); // Request focus so the panel is ready for key events
    }

    class StartScreen extends JDialog {
        public StartScreen(JFrame parent, Game2 game) {
            super(parent, true);

            // Remove title bar
            setUndecorated(true);

            // Add a custom border for styling
            getRootPane().setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

            setSize(400, 380); // Define dialog size
            setLocationRelativeTo(parent); // Center the dialog on the parent
            setResizable(false); // Disable resizing
            setLayout(null);

            // Custom panel for background and text rendering
            JPanel backgroundPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;

                    // Enable high-quality rendering
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                    // Draw the main image
                    try {
                        BufferedImage image = ImageIO.read(getClass().getResource("/air/airmission.png"));
                        Image scaledImage = image.getScaledInstance(380, 310, Image.SCALE_SMOOTH);
                        g2.drawImage(scaledImage, 10, 10, null);
                    } catch (IOException | NullPointerException e) {
                        g2.setColor(Color.RED);
                        g2.drawString("Error: air mission image not found", 10, 20);
                    }
                }
            };
            backgroundPanel.setBounds(0, 0, 400, 380);
            backgroundPanel.setLayout(null);
            backgroundPanel.setOpaque(false);
            add(backgroundPanel);

            // Start Button with hover effect
            JButton startButton = new JButton("Start");
            startButton.setFont(new Font("Arial", Font.BOLD, 14));
            startButton.setFocusPainted(false);
            startButton.setBackground(new Color(227, 141, 60));
            startButton.setForeground(Color.WHITE);
            startButton.setBounds(150, 330, 100, 40);
            startButton.setBorderPainted(false);
            startButton.setOpaque(true);

            // Hover effect
            Color originalColor = startButton.getBackground();
            Color hoverColor = originalColor.darker();
            startButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    startButton.setBackground(hoverColor);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    startButton.setBackground(originalColor);
                }
            });

            startButton.addActionListener(e -> {
                dispose(); // Close the start screen
                game.playbutton(); // Trigger the game's start method
            });
            backgroundPanel.add(startButton);
        }
    }

    class Bird {
        int x, y, width, height;
        Image img;

        Bird(Image img) {
            this.img = img;
            this.x = birdX;
            this.y = birdY;
            this.width = birdWidth;
            this.height = birdHeight;
        }

        Rectangle getBounds() {
            int marginX = width / 8;
            int marginY = height / 8;
            return new Rectangle(x + marginX, y + marginY, width - 2 * marginX, height - 2 * marginY);
        }
    }

    class Obstacle {
        int x, y, width, height;
        Image img;

        Obstacle(Image img, int x, int y, int width, int height) {
            this.img = img;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        Rectangle getBounds() {
            int marginX = width / 8;
            int marginY = height / 8;
            return new Rectangle(x + marginX, y + marginY, width - 2 * marginX, height - 2 * marginY);
        }
    }

    void playbutton() {
    	
    	  // Start Button
        playButton = new JButton("Play");
        playButton.setBounds((boardWidth - 80) / 2, (boardHeight / 2) + 200, 80, 40);

        playButton.setFocusable(false);
        playButton.setFont(new Font("Arial", Font.BOLD, 20));
        playButton.setBackground(new Color(227, 141, 60));
        playButton.setForeground(Color.WHITE);
        playButton.setBorderPainted(false);
        playButton.setOpaque(true);
        playButton.addActionListener(e -> {
            disposePlayButton(); // Dispose of the play button
            startGame(); // Start the game after disposing of the play button
 
        });

        add(playButton);
       
    }

    void startGame() {
  
        gameStarted = true;
        bird.y = birdY;
        velocityY = 0; // Set initial velocity to 0
        obstacles.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        placeObstacleTimer.start();
    }

    void placeObstacles() {
        int numberOfClouds = 3;
        for (int i = 0; i < numberOfClouds; i++) {
            int cloudX = boardWidth + (i * 300) + random.nextInt(100);
            int cloudY = random.nextInt(boardHeight - cloudHeight);
            obstacles.add(new Obstacle(cloudImg, cloudX, cloudY, cloudWidth, cloudHeight));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        for (Obstacle obstacle : obstacles) {
            g.drawImage(obstacle.img, obstacle.x, obstacle.y, obstacle.width, obstacle.height, null);
        }

        // Draw the score
        g.setColor(Color.ORANGE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString(String.valueOf((int) score), boardWidth / 2 - 25, 60);

        // Draw energy icons at the top-left corner
        for (int i = 0; i < energy; i++) {
            g.drawImage(energyIcon, 10 + (i * 40), 10, 30, 30, this);
        }

        if (gameOver) {
            // Game Over text is handled in the game over dialog
            // Optionally, you can draw some overlay here
        }
    }

    public void move() {
        if (!gameStarted) return; // Exit if the game has not started

        velocityY += gravity;
        bird.y += velocityY;

        if (bird.y < 0) bird.y = 0; // Prevent bird from going above the screen

        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            obstacle.x += velocityX;

            if (obstacle.x + obstacle.width < 0) {
                score += 5;
                obstacles.remove(i);
                i--;
                continue;
            }

            if (collision(bird, obstacle)) {
                gameOver = true;
                gameStarted = false;
                handleGameOver("You hit a cloud!");
                break;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
            gameStarted = false;
            handleGameOver("You fell down!");
        }

        if (score >= MISSION_SCORE) {
            gameLoop.stop();
            placeObstacleTimer.stop();
            showMissionComplete();
        }
    }

    boolean collision(Bird bird, Obstacle obstacle) {
        BufferedImage birdImage = toBufferedImage(bird.img);
        BufferedImage obstacleImage = toBufferedImage(obstacle.img);

        // Get the intersection rectangle
        Rectangle birdBounds = new Rectangle(bird.x, bird.y, bird.width, bird.height);
        Rectangle obstacleBounds = new Rectangle(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        Rectangle intersection = birdBounds.intersection(obstacleBounds);

        // If there is no intersection, return false
        if (intersection.isEmpty()) {
            return false;
        }

        // Check each pixel in the intersection for non-transparent overlap
        for (int i = 0; i < intersection.width; i++) {
            for (int j = 0; j < intersection.height; j++) {
                int birdPixelX = intersection.x + i - bird.x;
                int birdPixelY = intersection.y + j - bird.y;

                int obstaclePixelX = intersection.x + i - obstacle.x;
                int obstaclePixelY = intersection.y + j - obstacle.y;

                // Get RGBA values of the pixels
                int birdPixel = birdImage.getRGB(birdPixelX * birdImage.getWidth() / bird.width, birdPixelY * birdImage.getHeight() / bird.height);
                int obstaclePixel = obstacleImage.getRGB(obstaclePixelX * obstacleImage.getWidth() / obstacle.width, obstaclePixelY * obstacleImage.getHeight() / obstacle.height);

                // Check if both pixels are not transparent
                if ((birdPixel >> 24) != 0x00 && (obstaclePixel >> 24) != 0x00) {
                    return true; // Collision detected
                }
            }
        }

        return false; // No collision
    }
    
    private BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image onto the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Ignore key presses if the game is over
        if (gameOver) return;

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameStarted) {
                velocityY = -10; // Allow jumping only if the game has started
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    private void handleGameOver(String message) {
        energy--; // Decrease energy by 1

        if (energy > 0) {
            // Get the parent window (if applicable)
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            JDialog gameOverDialog = new JDialog(parentWindow);
            gameOverDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            gameOverDialog.setUndecorated(true); // Remove the title bar and window decorations

            // Load and scale the game over image
            ImageIcon icon = new ImageIcon(getClass().getResource("/gameover.png"));
            Image scaledImage = icon.getImage().getScaledInstance(305, 220, Image.SCALE_SMOOTH);

            // Create a custom panel for the image and buttons
            JPanel imagePanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(scaledImage, 0, 0, getWidth(), getHeight(), this);
                }
            };
            imagePanel.setLayout(null); // Use absolute positioning
            imagePanel.setPreferredSize(new Dimension(305, 220));
            imagePanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5)); // Optional border

            // Create and style Play Again button
            JButton playAgainButton = new JButton("Play Again");
            playAgainButton.setBackground(new Color(165, 42, 42)); // Brown background
            playAgainButton.setForeground(Color.WHITE);           // White text
            playAgainButton.setFont(new Font("Arial", Font.BOLD, 14));
            playAgainButton.setFocusPainted(false);
            playAgainButton.setBorderPainted(false);
            playAgainButton.setOpaque(true);

            playAgainButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    playAgainButton.setBackground(playAgainButton.getBackground().darker());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    playAgainButton.setBackground(new Color(165, 42, 42));
                }
            });

            // Create and style Exit button
            JButton exitButton = new JButton("Exit");
            exitButton.setBackground(new Color(165, 42, 42)); // Brown background
            exitButton.setForeground(Color.WHITE);           // White text
            exitButton.setFont(new Font("Arial", Font.BOLD, 14));
            exitButton.setFocusPainted(false);
            exitButton.setBorderPainted(false);
            exitButton.setOpaque(true);

            exitButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    exitButton.setBackground(exitButton.getBackground().darker());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    exitButton.setBackground(new Color(165, 42, 42));
                }
            });

            // Position buttons
            int buttonWidth = 80;
            int buttonHeight = 25;
            int gap = 10; // Gap between buttons
            int totalButtonsWidth = (buttonWidth * 2) + gap;
            int xStart = (305 - totalButtonsWidth) / 2;
            int yPosition = 220 - buttonHeight - 10;

            playAgainButton.setBounds(xStart, yPosition, buttonWidth, buttonHeight);
            exitButton.setBounds(xStart + buttonWidth + gap, yPosition, buttonWidth, buttonHeight);

            // Add action listeners to buttons
            playAgainButton.addActionListener(e -> {
                gameOverDialog.dispose();
                startGame(); // Restart the game only when this button is clicked
            });

            exitButton.addActionListener(e -> {
                gameOverDialog.dispose();
                System.exit(0); // Exit the application
            });

            // Add buttons to the image panel
            imagePanel.add(playAgainButton);
            imagePanel.add(exitButton);

            // Set the image panel as the dialog's content
            gameOverDialog.setContentPane(imagePanel);
            gameOverDialog.pack();
            gameOverDialog.setLocationRelativeTo(this);
            gameOverDialog.setVisible(true);
        } else {
            // Show mission failed screen when out of energy
            showMissionFailed();
        }
    }

    // Show Mission Complete Dialog
    private void showMissionComplete() {
        JDialog missionCompleteDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Mission Complete", true);
        missionCompleteDialog.setUndecorated(true); // Remove title bar

        // Create a panel to hold the mission complete image and buttons
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(missionCompleteImg, 0, 0, 305, 220, null);
            }
        };
        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(305, 220));

        // Add border
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));

        // Continue Button
        JButton continueButton = new JButton("Continue");
        continueButton.setBounds(105, 170, 90, 30);
        continueButton.setBackground(new Color(165, 42, 42));
        continueButton.setForeground(Color.WHITE);
        continueButton.setFocusPainted(false);
        continueButton.setBorderPainted(false);
        continueButton.setOpaque(true);
        addHoverEffect(continueButton, new Color(165, 42, 42), new Color(100, 30, 30));
        continueButton.addActionListener(e -> {
            missionCompleteDialog.dispose();
            // Transition to the next part of your application, e.g., RoadMapWindow
            // For example:
            // new RoadMapWindow().setVisible(true);
            JOptionPane.showMessageDialog(this, "Mission Completed! Proceeding to the next level.");
            System.exit(0); // Placeholder action
        });

        panel.add(continueButton);

        missionCompleteDialog.getContentPane().add(panel);
        missionCompleteDialog.pack();
        missionCompleteDialog.setLocationRelativeTo(this);
        missionCompleteDialog.setVisible(true);
    }

    private void showMissionFailed() {
        // Create a modal dialog for the mission failed screen
        JDialog missionFailedDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), true);
        missionFailedDialog.setUndecorated(true); // Remove the title bar
        missionFailedDialog.setSize(400, 250); // Set dialog size
        missionFailedDialog.setLayout(null); // Use absolute positioning
        missionFailedDialog.setLocationRelativeTo(this); // Center dialog relative to parent

        // Add the mission failed image label
        JLabel missionFailedLabel = new JLabel();
        missionFailedLabel.setBounds(0, 0, 400, 250);

        // Add a border to the image label
        int borderSize = 3; // Border size
        missionFailedLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, borderSize));

        try {
            ImageIcon missionFailedIcon = new ImageIcon(getClass().getResource("/failed.png"));

            // Adjust the image size for the border
            int imageWidth = 400 - (2 * borderSize);
            int imageHeight = 250 - (2 * borderSize);
            Image scaledImage = missionFailedIcon.getImage().getScaledInstance(imageWidth, imageHeight, Image.SCALE_SMOOTH);

            // Set the scaled image to the label
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            missionFailedLabel.setIcon(scaledIcon);
            missionFailedLabel.setHorizontalAlignment(JLabel.CENTER);
            missionFailedLabel.setVerticalAlignment(JLabel.CENTER);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load 'missionFailed.png': " + e.getMessage(),
                    "Image Load Error", JOptionPane.ERROR_MESSAGE);
        }

        missionFailedDialog.add(missionFailedLabel);

        // Add the OK button overlaying the image
        JButton okButton = new JButton("OK");
        okButton.setBounds(165, 220, 70, 25); // Adjusted position and size
        okButton.setFocusPainted(false);
        Color normalColor = new Color(165, 42, 42); // Original button color
        Color hoverColor = normalColor.darker(); // Hover color
        okButton.setBackground(normalColor);
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Arial", Font.BOLD, 14));
        okButton.setBorderPainted(false);
        okButton.setOpaque(true);

        // Add hover effect to the OK button
        addHoverEffect(okButton, normalColor, hoverColor);

        // Add action listener to close the dialog and transition to the WelcomeWindow
        okButton.addActionListener(e -> {
            missionFailedDialog.dispose(); // Close the dialog
            new WelcomeWindow().setVisible(true); // Open the WelcomeWindow
            ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose(); // Close the current frame
        });

        // Add components to the label
        missionFailedLabel.setLayout(null); // Allow absolute positioning within the label
        missionFailedLabel.add(okButton); // Add the button to the label

        missionFailedDialog.setVisible(true); // Display the dialog
    }

    // Helper method to add hover effect to buttons
    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });
    }
}
