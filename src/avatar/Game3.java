package avatar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;

public class Game3 extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;
    private final RoadMapWindow roadMapWindow;
    private Image energyIcon;

    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U'; // U D L R
        int velocityX = 0, velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            int speed = tileSize / 4; // Keeps the original speed
            switch (this.direction) {
                case 'U' -> { 
                    this.velocityX = 0; 
                    this.velocityY = -speed; 
                }
                case 'D' -> { 
                    this.velocityX = 0; 
                    this.velocityY = speed; 
                }
                case 'L' -> { 
                    this.velocityX = -speed; 
                    this.velocityY = 0; 
                }
                case 'R' -> { 
                    this.velocityX = speed; 
                    this.velocityY = 0; 
                }
            }
        }



        void reset() {
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private final int rowCount = 21;
    private final int columnCount = 19;
    private final int tileSize = 32;

    private final Image wallImage;
    private final Image lordOzaiImage;
    private final Image zukoImage;
    private final Image princessAzulaImage;
    private final Image kuviraImage;
    private final Image goldCoinImage;
    private final Image avatarUpImage;
    private final Image avatarDownImage;
    private final Image avatarLeftImage;
    private final Image avatarRightImage;

    private final String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "X       bpo       X",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private HashSet<Block> ghosts;
    private Block pacman;

    private Image backgroundImage;

    private Timer gameLoop;
    private char[] directions = {'U', 'D', 'L', 'R'};
    private Random random = new Random();
    private int score;
    private boolean gameOver;

    

    public Game3(RoadMapWindow roadMapWindow) {
    	
    	 this.roadMapWindow = roadMapWindow;
        JFrame frame = new JFrame("Pac-Man");

        goldCoinImage = new ImageIcon("src/powerFood.png").getImage();
        backgroundImage = new ImageIcon("src/bgfire.png").getImage();

        setLayout(new BorderLayout());
        addKeyListener(this);
        setFocusable(true);

        frame.setLayout(new BorderLayout());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);

        wallImage = new ImageIcon(getClass().getResource("/img/wall.jpg")).getImage();
        lordOzaiImage = new ImageIcon(getClass().getResource("/img/lordOzai.png")).getImage();
        zukoImage = new ImageIcon(getClass().getResource("/img/zuko.png")).getImage();
        princessAzulaImage = new ImageIcon(getClass().getResource("/img/princessAzula.png")).getImage();
        kuviraImage = new ImageIcon(getClass().getResource("/img/kuvira.png")).getImage();
        avatarUpImage = new ImageIcon(getClass().getResource("/img/up.png")).getImage();
        avatarDownImage = new ImageIcon(getClass().getResource("/img/down.png")).getImage();
        avatarLeftImage = new ImageIcon(getClass().getResource("/img/left.png")).getImage();
        avatarRightImage = new ImageIcon(getClass().getResource("/img/right.png")).getImage();
        energyIcon = new ImageIcon("/img/energy.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        
        
        
        new StartScreen(
            frame,
            "src/img/fireMission.png",
            new Color(124, 15, 15),
            null
        ).setVisible(true);

        startGame();

        frame.setVisible(true);
    }

    private void startGame() {
        loadMap();
        resetPositions();
        score = 0;
        gameOver = false;
        if (gameLoop != null) {
    gameLoop.stop();
}
gameLoop = new Timer(60, e -> {
    move();
    repaint();
    if (gameOver) {
        gameLoop.stop();
    }
});
gameLoop.setRepeats(true);
gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char tileMapChar = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                switch (tileMapChar) {
                    case 'X':
                        walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                        break;
                    case 'b':
                        ghosts.add(new Block(lordOzaiImage, x, y, tileSize, tileSize));
                        break;
                    case 'o':
                        ghosts.add(new Block(zukoImage, x, y, tileSize, tileSize));
                        break;
                    case 'p':
                        ghosts.add(new Block(princessAzulaImage, x, y, tileSize, tileSize));
                        break;
                    case 'r':
                        ghosts.add(new Block(kuviraImage, x, y, tileSize, tileSize));
                        break;
                    case 'P':
                        pacman = new Block(avatarRightImage, x, y, tileSize, tileSize);
                        break;
                    case ' ':
                        foods.add(new Block(goldCoinImage, x + 14, y + 14, 6, 6));
                        break;
                }
            }
        }
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    public void paintComponent(Graphics g) {
    	

    	 
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
    	
    	 // Draw energy icons at the top
        for (int i = 0; i < GameOverDialog.getEnergy(); i++) {
            g.drawImage(energyIcon, 10 + (i * 40), 40, 30, 30, this);
        }
    	
    	
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        int mapWidth = columnCount * tileSize;
        int mapHeight = rowCount * tileSize;
        int xOffset = (getWidth() - mapWidth) / 7;
        int yOffset = (getHeight() - mapHeight) / 7;

        g.drawImage(pacman.image, pacman.x + xOffset, pacman.y + yOffset, pacman.width, pacman.height, null);
        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x + xOffset, ghost.y + yOffset, ghost.width, ghost.height, null);
        }
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x + xOffset, wall.y + yOffset, wall.width, wall.height, null);
        }
        for (Block food : foods) {
            g.drawImage(food.image, food.x + xOffset, food.y + yOffset, food.width, food.height, null);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over", tileSize / 2, tileSize / 2);
        } else {
            g.drawString("Score: " + score, tileSize / 2, tileSize / 2);
        }
    }

    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        for (Block ghost : ghosts) {
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            for (Block wall : walls) {
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                }
            }

            if (collision(pacman, ghost)) {
                gameOver = true;
                GameOverDialog.handleGameOver(SwingUtilities.getWindowAncestor(this), this::startGame);
                return;
            }
        }

        foods.removeIf(food -> {
    if (collision(pacman, food)) {
        score++;
        return true;
    }
    return false;
});
        if (score >= 10 && !gameOver) {
        	gameOver = true;
        	
        	
            MissionCompleteDialog missionDialog = new MissionCompleteDialog(null, roadMapWindow);
            missionDialog.showMissionComplete();
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.dispose();
            }
            this.setVisible(false);
             roadMapWindow.unlockGame4();
        
        
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            GameOverDialog.handleGameOver(SwingUtilities.getWindowAncestor(this), this::startGame);
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> pacman.updateDirection('U');
            case KeyEvent.VK_DOWN -> pacman.updateDirection('D');
            case KeyEvent.VK_LEFT -> pacman.updateDirection('L');
            case KeyEvent.VK_RIGHT -> pacman.updateDirection('R');
        }

        switch (pacman.direction) {
            case 'U' -> pacman.image = avatarUpImage;
            case 'D' -> pacman.image = avatarDownImage;
            case 'L' -> pacman.image = avatarLeftImage;
            case 'R' -> pacman.image = avatarRightImage;
        }
    }
}
