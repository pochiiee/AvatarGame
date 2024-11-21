package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginWindow extends JFrame {

    private Image backgroundImage;
    private JProgressBar progressBar;

    public LoginWindow() {
        
       
        setSize(Toolkit.getDefaultToolkit().getScreenSize()); // Set to full-screen size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Disable resizing
        setResizable(false);

        // Set the frame to full-screen mode
        setExtendedState(JFrame.MAXIMIZED_BOTH); 


        // Load the background image
        ImageIcon icon = new ImageIcon("src/login.png");
        backgroundImage = icon.getImage();

        // Create a custom panel for the background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel); // Set as content pane for the frame

     // Add UI Components
        JLabel userLabel = new JLabel("Username");
        userLabel.setBounds(940, 350, 200, 40); // Moved further to the left
        userLabel.setForeground(new Color(101, 67, 33));
        userLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger font for better visibility
        backgroundPanel.add(userLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(1120, 350, 250, 40); // Moved further to the left for more right margin
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16)); // Font size for better readability
        backgroundPanel.add(usernameField);

        JLabel passLabel = new JLabel("Password");
        passLabel.setBounds(940, 470, 200, 40); // Moved further to the left
        passLabel.setForeground(new Color(101, 67, 33));
        passLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger font for better visibility
        backgroundPanel.add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(1120, 470, 250, 40); // Moved further to the left for more right margin
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16)); // Font size for better readability
        backgroundPanel.add(passwordField);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(935, 600, 150, 40); // Adjusted further to the left
        loginButton.setBackground(new Color(181, 101, 29));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger font for better visibility
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        backgroundPanel.add(loginButton);



        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(139, 69, 19));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(181, 101, 29));
            }
        });

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (AccountManager.isValidAccount(username, password)) {
                showLoadingScreen(); // Show loading animation
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials! Please try again.");
            }
        });
        backgroundPanel.add(loginButton);

    
     // Create Account Button
        JButton createAccountButton = new JButton("Create Account");
        createAccountButton.setBounds(1150, 600, 200, 40); // Positioned left of the Login button
        createAccountButton.setBackground(new Color(181, 101, 29));
        createAccountButton.setForeground(Color.WHITE);
        createAccountButton.setFont(new Font("Arial", Font.BOLD, 18)); // Bigger font for better visibility
        createAccountButton.setBorderPainted(false);
        createAccountButton.setFocusPainted(false);
        backgroundPanel.add(createAccountButton);



        createAccountButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                createAccountButton.setBackground(new Color(139, 69, 19));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                createAccountButton.setBackground(new Color(181, 101, 29));
            }
        });

        createAccountButton.addActionListener(e -> {
            new CreateAccountWindow(); // Assuming this class exists
            dispose();
        });
        backgroundPanel.add(createAccountButton);

        setVisible(true);
    }
   
    private void showLoadingScreen() {
        // Create a panel for the loading background image
        JPanel loadingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon loadingIcon = new ImageIcon("src/loading.jpg"); // Ensure path is correct
                g.drawImage(loadingIcon.getImage(), 0, 0, getWidth(), getHeight(), this);

                // Draw the text "Loading..." or "Completed" based on progress
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                String loadingText = (progressBar.getValue() == 100) ? "Completed" : "Loading...";
                int textWidth = g.getFontMetrics().stringWidth(loadingText);
                int x = (getWidth() - textWidth) / 2;
                int y = getHeight() - 30;

                g.drawString(loadingText, x, y);
            }
        };
        loadingPanel.setLayout(null);
        loadingPanel.setBounds(0, 0, 300, 200); // Set to 300x200 size
        add(loadingPanel);

        // Progress Bar at the bottom of the window (fixed position at the bottom of the image)
        progressBar = new JProgressBar();
        progressBar.setBounds(0, 190, 300, 10); // Set the progress bar to the bottom (190 is just before the bottom)
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        loadingPanel.add(progressBar);

        // Create a frame to hold everything (loading screen)
        JFrame loadingFrame = new JFrame();
        loadingFrame.setSize(300, 200); // Set to 300x200 size
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setUndecorated(true);
        loadingFrame.setLayout(null);
        loadingFrame.add(loadingPanel);
        loadingFrame.setVisible(true);

        // Simulate loading with a timer
        Timer timer = new Timer(100, new ActionListener() {
            int percentage = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                percentage += 2;
                if (percentage > 100) {
                    percentage = 100;
                }
                progressBar.setValue(percentage);

                // Redraw the panel to update the text
                loadingPanel.repaint();

                if (percentage == 100) {
                    ((Timer) e.getSource()).stop();

                    // Hide the progress bar and loading screen
                    progressBar.setVisible(false);

                    // Proceed to the next window
                    SwingUtilities.invokeLater(() -> {
                        new StoryWindow().setVisible(true);
                        loadingFrame.dispose();
                    });
                }
            }
        });
        timer.start();
    }



}
