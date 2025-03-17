package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

public class GameHandler extends JPanel implements Runnable {
    private final int WIDTH = 800, HEIGHT = 600;
    private Thread gameThread;
    public static boolean running = false;
    public static boolean nextlevel = false;
    public static boolean endgame = false;
    private Player player;
    private Enemy enemy;
    private ArrayList<Wall> walls;
    private ArrayList<Block> blocks;
    private long startTime;
    private final int TIME_LIMIT = 100000;
    private boolean showGrid = false;
    private Image backgroundImage;
    private Image jumpscareImage;
    private Image levelImage;

    // every single class will be in their own independent class script
    // next week for more organized workspace

    public GameHandler() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        addKeyListener(new KeyHandler());

        backgroundImage = new ImageIcon(getClass().getResource("/game/images/background.png")).getImage();
        jumpscareImage = new ImageIcon(getClass().getResource("/game/images/realscaryshi.png")).getImage();
        levelImage = new ImageIcon(getClass().getResource("/game/images/level.png")).getImage();

        player = new Player(900, 900);
        //enemy = new Enemy(100,100,32,32);
        walls = new ArrayList<>();
        blocks = new ArrayList<>();

        int gridSize = 50;
        int gridWidth = (WIDTH + 200) / gridSize;
        int gridHeight = (HEIGHT + 400) / gridSize;

        for (int x = 0; x < gridWidth * gridSize; x += gridSize) {
            walls.add(new Wall(x, 0, gridSize, gridSize)); // Top wall
            walls.add(new Wall(x, (gridHeight - 1) * gridSize, gridSize, gridSize)); // Bottom wall
        }

        for (int y = 0; y < gridHeight * gridSize; y += gridSize) {
            walls.add(new Wall(0, y, gridSize, gridSize)); // Left wall
            walls.add(new Wall((gridWidth - 1) * gridSize, y, gridSize, gridSize)); // Right wall
        }

        // LONG AHH CODE gotta optimize prob next week
        // yeah for now its like this
        // id probably make sure that i dont make 1:50 so that i dont have to keep doing calculations in my head
        // when positioning stuff

        walls.add(new Wall(150, 350, 200,50));
        walls.add(new Wall(150, 400, 50,350));
        walls.add(new Wall(200, 700, 100,50));
        walls.add(new Wall(400, 700, 200,50));
        walls.add(new Wall(550, 400, 50,350));
        walls.add(new Wall(600, 400, 150,50));
        walls.add(new Wall(700, 100, 50,300));
        walls.add(new Wall(350, 100, 50,150));
        walls.add(new Wall(350, 300, 50,100));
        walls.add(new Wall(400, 100, 300,50));

        blocks.add(new Block(700,750,50,50,"LET"));
        blocks.add(new Block(600,200,50,50,"ME"));
        blocks.add(new Block(250,600,50,50,"IN"));
//        blocks.add(new Block(600,300,50,50, "SET"));
//        blocks.add(new Block(250,250,50,50, "ME"));
//        blocks.add(new Block(200,350,50,50, "FREE"));
//
//        blocks.add(new Block(350,550,50,50, "SET"));
//        blocks.add(new Block(350,600,50,50, "ME"));
//        blocks.add(new Block(350,650,50,50, "FREE"));

        // This is the Sandbox World
        // Just load this if you want to see ig

        //walls.add(new Wall(200, 200, 100, 100));
        //walls.add(new Wall(500, 400, 150, 150));

        //blocks.add(new Block(300, 300, 50, 50));
        //blocks.add(new Block(450, 350, 50, 50));

        startTime = System.currentTimeMillis();
        startGame();
    }

    private void startGame() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        while (running) {
            update();
            repaint();
            checkTimer();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update() {
        player.update(walls, blocks);
        if (enemy != null) {
            enemy.update(player, walls);
        }
    }

    private void checkTimer() {
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
        if (elapsedTime >= TIME_LIMIT) {
            running = false;
            repaint();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int camX = getWidth() / 2 - player.x;
        int camY = getHeight() / 2 - player.y;

        int gridSize = 50;
        int gridWidth = (WIDTH + 200) / gridSize;
        int gridHeight = (HEIGHT + 400) / gridSize;

        updateRules();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(camX, camY);

        if (levelImage != null) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(levelImage, 0, 0, gridSize * gridWidth, gridSize * gridHeight, this);
        }

        if (showGrid) {
            Grid.drawGrid(g, WIDTH, HEIGHT);
        }

//        g.setColor(Color.DARK_GRAY);
//        for (Wall wall : walls) {
//            g.fillRect(wall.x, wall.y, wall.width, wall.height);
//        }

        g.setColor(Color.ORANGE);
        for (Block block : blocks) {
            block.draw(g, block.x, block.y, block.width, block.height);
        }


        g.setColor(Color.BLUE);
        g.fillOval(player.x, player.y, player.size, player.size);

        if (enemy != null) {
            g.setColor(Color.RED);
            enemy.draw(g);
        }

        g2d.translate(-camX, -camY);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int timeLeft = TIME_LIMIT - (int) ((System.currentTimeMillis() - startTime) / 1000);
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        g.drawString(String.format("Time Left: %d:%02d", minutes, seconds), 10, 20);

        if (!running) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(jumpscareImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", getWidth() / 2 - 120, getHeight() / 2);
        }

        if (nextlevel) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(backgroundImage, 0, 0, gridSize * gridWidth, gridSize * gridHeight, this);
        }

        if (endgame) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("YOU WON!", getWidth() / 2 - 120, getHeight() / 2);
        }
    }

    private void updateRules() {
        // Reset colors
        for (Block block : blocks) {
            block.color = Color.ORANGE;
        }

        // Check for valid sentences using a hashmap grid system
        buildBlockMap();

        for (Block block : blocks) {
            Point pos = getGridPosition(block.x, block.y);

            Block right = blockMap.get(new Point(pos.x + 1, pos.y));
            Block right2 = blockMap.get(new Point(pos.x + 2, pos.y));

            Block down = blockMap.get(new Point(pos.x, pos.y + 1));
            Block down2 = blockMap.get(new Point(pos.x, pos.y + 2));

            // Check horizontal sentence (left to right)
            if (right != null && right2 != null && isValidSETMEFREE(block, right, right2)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                GameHandler.endgame = true;
            }

            // Check vertical sentence (top to bottom)
            if (down != null && down2 != null && isValidSETMEFREE(block, down, down2)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                GameHandler.endgame = true;
            }

            if (right != null && right2 != null && isValidLETMEIN(block, right, right2)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                nextLevel();
            }

            // Check vertical sentence (top to bottom)
            if (down != null && down2 != null && isValidLETMEIN(block, down, down2)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                nextLevel();
            }
        }
    }

    private HashMap<Point, Block> blockMap;

    private void buildBlockMap() {
        blockMap = new HashMap<>();
        for (Block block : blocks) {
            Point snappedPos = getGridPosition(block.x, block.y);
            blockMap.put(snappedPos, block);
        }
    }

    private Point getGridPosition(int x, int y) {
        int gridSize = 50; // Adjust this to match block size
        int gridX = Math.round(x / (float) gridSize);
        int gridY = Math.round(y / (float) gridSize);
        return new Point(gridX, gridY);
    }

    private boolean isValidSETMEFREE(Block a, Block b, Block c) {
        List<String> words = Arrays.asList(a.label, b.label, c.label);
        return words.contains("SET") && words.contains("ME") && words.contains("FREE");
    }

    private boolean isValidLETMEIN(Block a, Block b, Block c) {
        List<String> words = Arrays.asList(a.label, b.label, c.label);
        return words.contains("LET") && words.contains("ME") && words.contains("IN");
    }



    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_G) {
                showGrid = !showGrid;
            }
            player.setKey(e.getKeyCode(), true);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.setKey(e.getKeyCode(), false);
        }
    }

    private void nextLevel() {
        System.out.println("Level 2 Unlocked!");

        //enemy = new Enemy(250,250,32,32);

        walls.clear();
        walls.add(new Wall(150, 350, 200,50));
        walls.add(new Wall(150, 400, 50,350));
        walls.add(new Wall(200, 700, 100,50));
        walls.add(new Wall(400, 700, 200,50));
        walls.add(new Wall(550, 400, 50,350));
        walls.add(new Wall(600, 400, 150,50));
        walls.add(new Wall(700, 100, 50,300));
        walls.add(new Wall(350, 100, 50,150));
        walls.add(new Wall(350, 300, 50,100));
        walls.add(new Wall(400, 100, 300,50));

        walls.add(new Wall(150, 150, 200,50));
        walls.add(new Wall(150, 200, 50,150));

        blocks.clear();
        blocks.add(new Block(700,750,50,50,"SET"));
        blocks.add(new Block(600,200,50,50,"ME"));
        blocks.add(new Block(250,600,50,50,"FREE"));
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Project C");
        GameHandler panel = new GameHandler();
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class Grid {
    public static void drawGrid(Graphics g, int width, int height) {
        width += 200;
        height += 400;

        g.setColor(Color.LIGHT_GRAY);
        int gridSize = 50;
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int x = 0; x < width; x += gridSize) {
            g.drawLine(x, 0, x, height);
            for (int y = 0; y < height; y += gridSize) {
                g.drawLine(0, y, width, y);
                g.drawString("(" + x + ", " + y + ")", x + 2, y + gridSize - 2);
            }
        }
    }
}


class Player {
    int x, y, size = 40;
    private int speed = 4;
    private boolean up, down, left, right;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setKey(int key, boolean pressed) {
        if (key == KeyEvent.VK_W) up = pressed;
        if (key == KeyEvent.VK_S) down = pressed;
        if (key == KeyEvent.VK_A) left = pressed;
        if (key == KeyEvent.VK_D) right = pressed;
    }

    public void update(ArrayList<Wall> walls, ArrayList<Block> blocks) {
        int newX = x, newY = y;

        if (up) newY -= speed;
        if (down) newY += speed;
        if (left) newX -= speed;
        if (right) newX += speed;

        if (!collides(newX, y, walls, blocks)) x = newX;
        if (!collides(x, newY, walls, blocks)) y = newY;
    }

    private boolean collides(int newX, int newY, ArrayList<Wall> walls, ArrayList<Block> blocks) {
        for (Wall wall : walls) {
            if (newX < wall.x + wall.width && newX + size > wall.x &&
                    newY < wall.y + wall.height && newY + size > wall.y) {
                return true;
            }
        }
        for (Block block : blocks) {
            if (block.collides(newX, newY, size)) {
                block.push(newX - x, newY - y, walls, blocks);
                return true;
            }
        }
        return false;
    }
}

class Enemy {
    int x, y, width, height;
    int speed = 2; // Speed of movement

    public Enemy(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(Player player, ArrayList<Wall> walls) {
        int dx = 0, dy = 0;

        if (player.x > x) dx = speed;
        if (player.x < x) dx = -speed;
        if (player.y > y) dy = speed;
        if (player.y < y) dy = -speed;

        if (!collidesWithWalls(x + dx, y, walls)) {
            x += dx;
        }
        if (!collidesWithWalls(x, y + dy, walls)) {
            y += dy;
        }

        if (collidesWithPlayer(player)) {
            System.out.println("Game Over! Enemy caught the player.");
            GameHandler.running = false;
        }
    }

    private boolean collidesWithPlayer(Player player) {
        return x < player.x + player.size && x + width > player.x &&
                y < player.y + player.size && y + height > player.y;
    }

    public boolean collidesWithWalls(int newX, int newY, ArrayList<Wall> walls) {
        for (Wall wall : walls) {
            if (newX < wall.x + wall.width && newX + width > wall.x &&
                    newY < wall.y + wall.height && newY + height > wall.y) {
                return true;
            }
        }
        return false;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }
}


class Wall {
    int x, y, width, height;
    public Wall(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}

class Block {
    int x, y, width, height;
    String label; // The text inside the block
    Font font = new Font("Arial", Font.BOLD, 20);
    Color color = Color.ORANGE;

    public Block(int x, int y, int width, int height, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
    }

    public boolean collides(int newX, int newY, int size) {
        return newX < x + width && newX + size > x && newY < y + height && newY + size > y;
    }

    public void push(int dx, int dy, ArrayList<Wall> walls, ArrayList<Block> blocks) {
        int newX = x + dx;
        int newY = y + dy;

        for (Wall wall : walls) {
            if (newX < wall.x + wall.width && newX + width > wall.x && newY < wall.y + wall.height && newY + height > wall.y) {
                return;
            }
        }

        for (Block block : blocks) {
            if (block != this && block.collides(newX, newY, width)) {
                block.push(dx, dy, walls, blocks);
                return;
            }
        }

        x = newX;
        y = newY;
    }

    public void draw(Graphics g, int x, int y, int width, int height) {
        g.setColor(color); // Use dynamic color
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);
        int textX = x + (width - metrics.stringWidth(label)) / 2;
        int textY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();

        g.drawString(label, textX, textY);
    }
}

