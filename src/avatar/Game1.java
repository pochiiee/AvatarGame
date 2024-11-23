package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class Game1 extends JFrame {

    public Game1() {
      
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new GamePanel());
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    class GamePanel extends JPanel implements ActionListener {
        private final int TILE_SIZE = 20;
        private final int WIDTH = 800 / TILE_SIZE;
        private final int HEIGHT = 600 / TILE_SIZE;
        private LinkedList<Point> snake;
        private Point food;
        private char direction;
        private boolean gameOver;
        private Timer timer;
        private Color snakeColor;
        private Image snakeHeadImage;
        private Image foodImage;
        private Image backgroundImage;

        public GamePanel() {
            loadImages();
            startGame();
        }

        private void loadImages() {
            try {
                snakeHeadImage = new ImageIcon("src/boat.png").getImage();
                foodImage = new ImageIcon("src/fish.png").getImage();
                backgroundImage = new ImageIcon("src/waterbg.jpg").getImage();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error loading images: " + e.getMessage(),
                        "Image Load Error", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }

        private void startGame() {
            snake = new LinkedList<>();
            snake.add(new Point(WIDTH / 2, HEIGHT / 2));
            direction = 'R'; // Initial direction
            spawnFood();
            snakeColor = Color.GREEN;
            gameOver = false;

            setBackground(new Color(50, 50, 50)); // Background color
            setFocusable(true);
            requestFocusInWindow();

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP -> { if (direction != 'D') direction = 'U'; }
                        case KeyEvent.VK_DOWN -> { if (direction != 'U') direction = 'D'; }
                        case KeyEvent.VK_LEFT -> { if (direction != 'R') direction = 'L'; }
                        case KeyEvent.VK_RIGHT -> { if (direction != 'L') direction = 'R'; }
                    }
                }
            });

            timer = new Timer(150, this);
            timer.start();
        }

        private void spawnFood() {
            Random rand = new Random();
            do {
                // Ensure food does not spawn on edges by adding a margin of 2 tiles
                int x = rand.nextInt(WIDTH - 4) + 2; // Avoid edges (margin of 2 tiles on left and right)
                int y = rand.nextInt(HEIGHT - 6) + 3; // Avoid edges (margin of 3 tiles on top, bottom includes score area)
                food = new Point(x, y);
            } while (snake.contains(food));
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
                spawnFood();
            } else {
                snake.removeLast();
            }
        }

        private void checkCollision() {
            Point head = snake.getFirst();

            // Check for wall collisions
            if (head.x < 1 || head.x >= WIDTH - 1 || head.y < 2 || head.y >= HEIGHT - 1) {
                gameOver = true;
                timer.stop();
                showGameOver("You hit the wall!");
            }

            // Check if the snake collides with itself
            for (int i = 1; i < snake.size(); i++) {
                if (head.equals(snake.get(i))) {
                    gameOver = true;
                    timer.stop();
                    showGameOver("You ran into yourself!");
                    break;
                }
            }

            // Check if the score reached 10
            if (snake.size() - 1 >= 10 && !gameOver) {
                gameOver = true;
                timer.stop();
                showGameOver("Mission Completed: Score reached 10!");
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

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw background image
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Draw score background (Sky blue bar)
            g.setColor(new Color(135, 206, 235)); // Sky blue color
            g.fillRect(0, 0, getWidth(), TILE_SIZE * 2);
            
            // Display score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + (snake.size() - 1), 10, 30);

            // Size for the food and body images
            int bodyAndFoodSize = TILE_SIZE + 5; // Small size for the body and food
            int headSize = TILE_SIZE * 2;        // Original larger size for the head

            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                if (i == 0) {
                    // Draw the snake head (boat image) with original larger size
                    g.drawImage(snakeHeadImage, p.x * TILE_SIZE, p.y * TILE_SIZE, headSize, headSize, this);
                } else {
                    // Draw the body segments (fish image) with smaller size
                    g.drawImage(foodImage, p.x * TILE_SIZE, p.y * TILE_SIZE, bodyAndFoodSize, bodyAndFoodSize, this);
                }
            }

            // Draw the food (fish image)
            g.drawImage(foodImage, food.x * TILE_SIZE, food.y * TILE_SIZE, bodyAndFoodSize, bodyAndFoodSize, this);
        }


    }
}
