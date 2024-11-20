package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginWindow extends JFrame {
    private Image backgroundImage;

    public LoginWindow() {
        // Load the background image
        ImageIcon icon = new ImageIcon("src/login.png");
        backgroundImage = icon.getImage();

        // Get the image's original dimensions
        int imageWidth = backgroundImage.getWidth(this);
        int imageHeight = backgroundImage.getHeight(this);

        // Set the window size based on the image's aspect ratio
        double aspectRatio = (double) imageWidth / imageHeight;
        int windowWidth = 800;
        int windowHeight = (int) (windowWidth / aspectRatio);

        setTitle("Login");
        setSize(windowWidth, windowHeight);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

     // Add UI components for login
        JLabel userLabel = new JLabel("Username ");
        userLabel.setBounds(460, 165, 190, 30);
        userLabel.setForeground(new Color(101, 67, 33)); // Brown color
        add(userLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(525, 165, 200, 30);
        add(usernameField);

        JLabel passLabel = new JLabel("Password ");
        passLabel.setBounds(460, 230, 100, 30);
        passLabel.setForeground(new Color(101, 67, 33)); // Brown color
        add(passLabel);


        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(525, 230, 200, 30);
        add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(460, 300, 100, 30);
        loginButton.setBackground(new Color(181, 101, 29));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);

        loginButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(139, 69, 19));
            }

            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(181, 101, 29));
            }
        });

        add(loginButton);

        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(580, 300, 150, 30);
        createAccountButton.setBackground(new Color(181, 101, 29));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFocusPainted(false);
        createAccountButton.setBorderPainted(false);

        createAccountButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                createAccountButton.setBackground(new Color(139, 69, 19));
            }

            public void mouseExited(MouseEvent e) {
                createAccountButton.setBackground(new Color(181, 101, 29));
            }
        });

        add(createAccountButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (AccountManager.isValidAccount(username, password)) {
                showLoadingScreen(windowWidth, windowHeight); // Show loading animation
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials! Please try again.");
            }
        });

        createAccountButton.addActionListener(e -> {
            new CreateAccountWindow();
            dispose();
        });

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, windowWidth, windowHeight);
        add(backgroundPanel);

        setVisible(true);
    }

    private void showLoadingScreen(int windowWidth, int windowHeight) {
        // Create a panel for the loading background image
        JPanel loadingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon loadingIcon = new ImageIcon("src/loading.jpg"); // Ensure path is correct
                g.drawImage(loadingIcon.getImage(), 0, 0, windowWidth, windowHeight, this);

                // Draw the text "Loading..." at the top of the progress bar
                g.setColor(Color.WHITE); // Set the color of the text
                g.setFont(new Font("Arial", Font.BOLD, 20)); // Set the font and size
                String loadingText = "Loading...";
                int textWidth = g.getFontMetrics().stringWidth(loadingText); // Calculate the width of the text
                int x = (windowWidth - textWidth) / 2; // Center the text horizontally
                int y = windowHeight - 30; // Place text just above the progress bar

                g.drawString(loadingText, x, y); // Draw the "Loading..." text
            }
        };
        loadingPanel.setLayout(null); // No layout to avoid any unwanted space or gaps
        loadingPanel.setBounds(0, 0, windowWidth, windowHeight); // Full-size panel

        // Progress Bar at the bottom of the window, stretched across the bottom and full width
        JProgressBar progressBar = new JProgressBar();
        progressBar.setBounds(0, windowHeight - 10, windowWidth, 10); // Sagad sa baba at gilid
        progressBar.setMinimum(0); // Minimum value for the progress bar
        progressBar.setMaximum(100); // Maximum value for the progress bar
        progressBar.setValue(0); // Initial value
        progressBar.setStringPainted(true); // Display the percentage as text inside the progress bar

        // Add progress bar to the panel
        loadingPanel.add(progressBar);

        // Create a frame to hold everything (loading screen)
        JFrame loadingFrame = new JFrame();
        loadingFrame.setSize(windowWidth, windowHeight);
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setUndecorated(true); // No title bar, no borders
        loadingFrame.setLayout(null); // No layout to avoid any extra space
        loadingFrame.add(loadingPanel);
        loadingFrame.setVisible(true);

        // Create the Login window (this is the one you want to dispose)
        JFrame loginFrame = new JFrame(); // Assuming the Login window is a JFrame
        loginFrame.setVisible(true); // Assuming it’s already visible when loading screen appears

        // Simulate loading with a timer (changing percentage)
        Timer timer = new Timer(100, new ActionListener() {
            int percentage = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Increase percentage gradually
                percentage += 2; // Increment by 2 for smooth animation
                if (percentage > 100) {
                    percentage = 100; // Limit to 100%
                }

                // Update the progress bar
                progressBar.setValue(percentage);

                // End loading after 100%
                if (percentage == 100) {
                    ((Timer) e.getSource()).stop(); // Stop the timer

                    // Proceed directly to the next window (Storyline)
                    SwingUtilities.invokeLater(() -> {
                        new StoryWindow().setVisible(true); // Show the StoryWindow

                        // Dispose both the Login window and Loading frame
                        loginFrame.dispose(); // Dispose the Login window
                        loadingFrame.dispose(); // Dispose the Loading screen
                    });
                }
            }
        });

        timer.start();
    }



}
