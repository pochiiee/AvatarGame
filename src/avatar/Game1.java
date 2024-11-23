package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class Game1 extends JFrame {

    public Game1() {
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new GamePanel());
        setResizable(false);
        setVisible(true);
    }

    class GamePanel extends JPanel implements ActionListener {
        private final int TILE_SIZE = 20;
        private int WIDTH;
        private int HEIGHT;
        private LinkedList<Point> snake;
        private Point food;
        private char direction;
        private boolean gameOver;
        private Timer timer;
        private int playCount = 0; // Tracks the number of games played
        private final int MAX_PLAYS = 3; // Maximum number of allowed plays

        private Image snakeHeadImage;
        private Image snakeBodyImage; // Fish image for the body
        private Image foodImage;
        private Image backgroundImage;
        private Image energyIcon;

        public GamePanel() {
            loadImages();
            setFocusable(true);
            requestFocusInWindow();
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    if (getWidth() > 0 && getHeight() > 0 && (snake == null || food == null)) {
                        startGame();
                    }
                }
            });
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP -> {
                            if (direction != 'D') direction = 'U';
                        }
                        case KeyEvent.VK_DOWN -> {
                            if (direction != 'U') direction = 'D';
                        }
                        case KeyEvent.VK_LEFT -> {
                            if (direction != 'R') direction = 'L';
                        }
                        case KeyEvent.VK_RIGHT -> {
                            if (direction != 'L') direction = 'R';
                        }
                    }
                }
            });
        }

        private void loadImages() {
            try {
                snakeHeadImage = new ImageIcon("src/boat.png").getImage().getScaledInstance(TILE_SIZE * 2, TILE_SIZE * 2, Image.SCALE_SMOOTH);
                snakeBodyImage = new ImageIcon("src/fish.png").getImage(); // Fish image for body
                foodImage = new ImageIcon("src/fish.png").getImage();
                backgroundImage = new ImageIcon("src/waterbg.jpg").getImage();
                energyIcon = new ImageIcon("src/energy.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading images: " + e.getMessage(),
                        "Image Load Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        private void startGame() {
            if (playCount >= MAX_PLAYS) {
                redirectToWelcomeWindow();
                return;
            }

            playCount++;
            WIDTH = getWidth() / TILE_SIZE;
            HEIGHT = getHeight() / TILE_SIZE;

            snake = new LinkedList<>();
            snake.add(new Point(WIDTH / 2, HEIGHT / 2)); // Start snake in the center
            direction = 'R'; // Initial direction
            spawnFood();
            gameOver = false;

            if (timer != null) {
                timer.stop();
            }
            timer = new Timer(150, this);
            timer.start();
        }

        private void spawnFood() {
            Random rand = new Random();

            if (WIDTH <= 0 || HEIGHT <= 0) return; // Ensure valid dimensions
            do {
                int x = rand.nextInt(WIDTH);
                int y = rand.nextInt(HEIGHT);
                food = new Point(x, y);
            } while (snake.contains(food)); // Avoid placing food on the snake
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!gameOver) {
                moveSnake();
                checkCollision();
                repaint();
            }
        }

        private void moveSnake() {
            Point head = snake.getFirst();
            Point newHead = new Point(head);

            switch (direction) {
                case 'U' -> newHead.y--;
                case 'D' -> newHead.y++;
                case 'L' -> newHead.x--;
                case 'R' -> newHead.x++;
            }

            snake.addFirst(newHead);

            if (newHead.equals(food)) {
                spawnFood(); // Generate new food
            } else {
                snake.removeLast(); // Remove the tail
            }
        }

        private void checkCollision() {
            Point head = snake.getFirst();

            // Check for wall collisions
            if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
                gameOver = true;
                timer.stop();
                showGameOver("You hit the wall!");
            }

            // Check for collisions with the snake itself
            for (int i = 1; i < snake.size(); i++) {
                if (head.equals(snake.get(i))) {
                    gameOver = true;
                    timer.stop();
                    showGameOver("You ran into yourself!");
                    break;
                }
            }
        }

        private void showGameOver(String message) {
            int response = JOptionPane.showOptionDialog(this,
                    "Game Over!\n" + message + "\nYour score: " + (snake.size() - 1),
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    new Object[]{"Play Again", "Exit"},
                    "Play Again");

            if (response == JOptionPane.YES_OPTION) {
                startGame();
            } else {
                System.exit(0);
            }
        }

        private void redirectToWelcomeWindow() {
            JOptionPane.showMessageDialog(this,
                    "You have reached the maximum number of plays. Returning to the welcome screen.",
                    "Limit Reached",
                    JOptionPane.INFORMATION_MESSAGE);

            SwingUtilities.invokeLater(() -> {
                new WelcomeWindow(); // Instantiate and display WelcomeWindow
                dispose(); // Close the current game window
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Ensure snake and food are initialized before painting
            if (snake == null || food == null) {
                return; // Skip painting if game components are not ready
            }

            // Draw background
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Display score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + (snake.size() - 1), 10, 30);

            // Draw energy icon and plays left
            g.drawImage(energyIcon, 10, 40, this); // Move icon higher up
            g.drawString(": " + (MAX_PLAYS - playCount), 70, 75); // Number of plays left beside icon

            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                if (i == 0) {
                    g.drawImage(snakeHeadImage, p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE * 2, TILE_SIZE * 2, this);
                } else {
                    g.drawImage(snakeBodyImage, p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
                }
            }

            // Draw food
            if (food != null) {
                g.drawImage(foodImage, food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, this);
            }
        }
    }
}
