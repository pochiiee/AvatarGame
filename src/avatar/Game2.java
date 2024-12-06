package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Game2 {

    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }
    }

    String[] cardList = {
            "pair1", "pair2", "pair3", "pair4", "pair5",
            "pair6", "pair7", "pair8", "pair9", "pair10"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 110;
    int cardHeight = 148;

    ArrayList<Card> cardSet;
    ImageIcon cardBackImageIcon;

    JFrame frame = new JFrame("Match Cards");
    int score = 0;
    int errorCount = 0;
    int energy = 3; // Initial energy
    Image energyIcon; // Icon for energy
    ArrayList<JButton> board;
    Timer hideCardTimer;
    boolean gameReady = true; // Set to true to allow immediate flips
    JButton card1Selected;
    JButton card2Selected;

    public Game2() {
        setupCards();
        shuffleCards();

        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Load energy icon
        try {
            Image energyImg = new ImageIcon(getClass().getResource("/img/energy.png")).getImage();
            energyIcon = energyImg.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Failed to load energy icon.");
            System.exit(1);
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        frame.add(backgroundPanel, BorderLayout.CENTER);

        JPanel boardPanel = new JPanel();
        board = new ArrayList<>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        boardPanel.setOpaque(false);

        for (Card card : cardSet) {
            JButton tile = new JButton(cardBackImageIcon);
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setFocusable(false);
            tile.setBorderPainted(false);
            tile.setContentAreaFilled(false);
            tile.setOpaque(false);
            tile.addActionListener(e -> handleCardFlip(tile));
            board.add(tile);
            boardPanel.add(tile);
        }

        backgroundPanel.add(boardPanel, BorderLayout.CENTER);

        frame.setVisible(true);

        hideCardTimer = new Timer(500, e -> hideCards());
        hideCardTimer.setRepeats(false);
    }

    private void handleCardFlip(JButton tile) {
        if (!gameReady) return;

        if (tile.getIcon() == cardBackImageIcon) {
            if (card1Selected == null) {
                card1Selected = tile;
                int index = board.indexOf(card1Selected);
                card1Selected.setIcon(cardSet.get(index).cardImageIcon);
            } else if (card2Selected == null) {
                card2Selected = tile;
                int index = board.indexOf(card2Selected);
                card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                if (!card1Selected.getIcon().equals(card2Selected.getIcon())) {
                    errorCount++;
                    repaintEnergy(); // Immediately update errors on the UI
                    if (errorCount % 3 == 0) {
                        energy--;
                        if (energy == 0) {
                            showMissionFailed();
                        } else {
                            handleGameOver();
                        }
                    }
                    hideCardTimer.start();
                } else {
                    score += 5;
                    if (score == 50) { // 10 pairs matched
                        showMissionComplete();
                    }
                    card1Selected = null;
                    card2Selected = null;
                }
            }
        }
    }

    private void handleGameOver() {
        JDialog gameOverDialog = createGameOverDialog();
        gameOverDialog.setVisible(true);
    }

    private JDialog createGameOverDialog() {
        JDialog gameOverDialog = new JDialog(frame, true);
        gameOverDialog.setUndecorated(true);
        gameOverDialog.setSize(400, 250);
        gameOverDialog.setLocationRelativeTo(frame);

        JLabel gameOverImageLabel = new JLabel();
        gameOverImageLabel.setBounds(0, 0, 400, 250);
        gameOverImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));

        try {
            ImageIcon gameOverIcon = new ImageIcon(getClass().getResource("/img/gameover.png"));
            Image scaledImage = gameOverIcon.getImage().getScaledInstance(394, 244, Image.SCALE_SMOOTH);
            gameOverImageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Failed to load game over image.");
        }

        JButton playAgainButton = new JButton("Play Again");
        JButton exitButton = new JButton("Exit");

        playAgainButton.setBounds(125, 200, 100, 25);
        exitButton.setBounds(235, 200, 70, 25);

        playAgainButton.setFocusPainted(false);
        playAgainButton.setBackground(new Color(165, 42, 42));
        playAgainButton.setForeground(Color.WHITE);

        exitButton.setFocusPainted(false);
        exitButton.setBackground(new Color(165, 42, 42));
        exitButton.setForeground(Color.WHITE);

        playAgainButton.addActionListener(e -> {
            gameOverDialog.dispose();
            restartGame(); // Restart the game while preserving energy
        });

        exitButton.addActionListener(e -> System.exit(0));

        gameOverImageLabel.setLayout(null);
        gameOverImageLabel.add(playAgainButton);
        gameOverImageLabel.add(exitButton);
        gameOverDialog.add(gameOverImageLabel);

        return gameOverDialog;
    }

    private void showMissionComplete() {
        JDialog missionCompleteDialog = new JDialog(frame, true);
        missionCompleteDialog.setUndecorated(true);
        missionCompleteDialog.setSize(400, 250);
        missionCompleteDialog.setLocationRelativeTo(frame);

        JLabel missionCompleteLabel = new JLabel();
        missionCompleteLabel.setBounds(0, 0, 400, 250);

        try {
            ImageIcon missionCompleteIcon = new ImageIcon(getClass().getResource("/img/missioncomplete.png"));
            Image scaledImage = missionCompleteIcon.getImage().getScaledInstance(394, 244, Image.SCALE_SMOOTH);
            missionCompleteLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Failed to load mission complete image.");
        }

        JButton continueButton = new JButton("Continue");
        continueButton.setBounds(150, 200, 100, 25);
        continueButton.setFocusPainted(false);
        continueButton.setBackground(new Color(165, 42, 42));
        continueButton.setForeground(Color.WHITE);

        continueButton.addActionListener(e -> {
            missionCompleteDialog.dispose();
            frame.dispose();
        });

        missionCompleteLabel.setLayout(null);
        missionCompleteLabel.add(continueButton);
        missionCompleteDialog.add(missionCompleteLabel);

        missionCompleteDialog.setVisible(true);
    }

    private void showMissionFailed() {
        JDialog missionFailedDialog = new JDialog(frame, true);
        missionFailedDialog.setUndecorated(true);
        missionFailedDialog.setSize(400, 250);
        missionFailedDialog.setLocationRelativeTo(frame);

        JLabel missionFailedLabel = new JLabel();
        missionFailedLabel.setBounds(0, 0, 400, 250);

        try {
            ImageIcon missionFailedIcon = new ImageIcon(getClass().getResource("/img/failed.png"));
            Image scaledImage = missionFailedIcon.getImage().getScaledInstance(394, 244, Image.SCALE_SMOOTH);
            missionFailedLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Failed to load mission failed image.");
        }

        JButton okButton = new JButton("OK");
        okButton.setBounds(165, 200, 100, 25);
        okButton.setFocusPainted(false);
        okButton.setBackground(new Color(165, 42, 42));
        okButton.setForeground(Color.WHITE);

        okButton.addActionListener(e -> {
            missionFailedDialog.dispose();
            System.exit(0);
        });

        missionFailedLabel.setLayout(null);
        missionFailedLabel.add(okButton);
        missionFailedDialog.add(missionFailedLabel);

        missionFailedDialog.setVisible(true);
    }

    private void restartGame() {
        // Reset game state but preserve energy
        score = 0;
        errorCount = 0;
        card1Selected = null;
        card2Selected = null;
        gameReady = false; // Prevent actions during the setup

        // Reset cards
        for (JButton tile : board) {
            tile.setIcon(cardBackImageIcon);
        }

        setupCards();
        shuffleCards();

        // Allow game actions after resetting
        gameReady = true;
        repaintEnergy();
    }

    private void setupCards() {
        cardSet = new ArrayList<>();
        for (String cardName : cardList) {
            Image cardImg = new ImageIcon(getClass().getResource("/img/" + cardName + ".png")).getImage();
            cardSet.add(new Card(cardName, new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH))));
        }
        cardSet.addAll(cardSet);

        Image cardBackImg = new ImageIcon(getClass().getResource("/img/cardcover.png")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
    }

    private void shuffleCards() {
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size());
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
    }

    private void hideCards() {
        if (gameReady && card1Selected != null && card2Selected != null) {
            card1Selected.setIcon(cardBackImageIcon);
            card2Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected = null;
        } else {
            for (JButton tile : board) {
                tile.setIcon(cardBackImageIcon);
            }
            gameReady = true;
        }
    }

    private void repaintEnergy() {
        frame.repaint();
    }

    class BackgroundPanel extends JPanel {
        private final Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon(getClass().getResource("/img/earth_temple.png")).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Draw energy icons
            for (int i = 0; i < energy; i++) {
                g.drawImage(energyIcon, 10 + (i * 40), 10, this);
            }

            // Display error count below energy icons
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Errors: " + errorCount, 10, 70);
        }
    }
}
