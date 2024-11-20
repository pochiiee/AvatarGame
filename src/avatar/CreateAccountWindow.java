package avatar;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class CreateAccountWindow extends JFrame {
    public CreateAccountWindow() {
        setTitle("Create Account");
        
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel userLabel = new JLabel("New Username:");
        userLabel.setBounds(50, 50, 150, 30);
        add(userLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(150, 50, 200, 30);
        add(usernameField);

        JLabel passLabel = new JLabel("New Password:");
        passLabel.setBounds(50, 100, 150, 30);
        add(passLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 30);
        add(passwordField);

        JButton saveButton = new JButton("Save");
        saveButton.setBounds(50, 150, 100, 30);
        add(saveButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(200, 150, 100, 30);
        add(backButton);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (!username.isEmpty() && !password.isEmpty()) {
                    AccountManager.saveAccount(username, password);
                    JOptionPane.showMessageDialog(null, "Account Created!");
                    new LoginWindow(); // Go back to Login
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields!");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginWindow(); // Return to Login
                dispose();
            }
        });

        setVisible(true);
    }
}
