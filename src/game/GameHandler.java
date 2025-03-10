package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameHandler extends JPanel implements Runnable {
    private final int WIDTH = 800, HEIGHT = 600;
    private Thread gameThread;
    private boolean running = false;
    private Player player;
    private ArrayList<Wall> walls;
    private ArrayList<Block> blocks;
    private long startTime;
    private final int TIME_LIMIT = 120;
    private boolean showGrid = false;

    // every single class will be in their own independent class script
    // next week for more organized workspace

    public GameHandler() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        addKeyListener(new KeyHandler());

        player = new Player(900, 900);
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

        walls.add(new Wall(350, 200, 400,50));
        walls.add(new Wall(350, 250, 50,200));
        walls.add(new Wall(700, 250, 50,200));
        walls.add(new Wall(500, 400, 200,50));
        walls.add(new Wall(550, 450, 50,250));
        walls.add(new Wall(200, 650, 150,50));
        walls.add(new Wall(450, 650, 100,50));
        walls.add(new Wall(200, 400, 50,250));
        walls.add(new Wall(250, 400, 100,50));

        blocks.add(new Block(600,300,50,50));
        blocks.add(new Block(250,250,50,50));
        blocks.add(new Block(200,350,50,50));
        blocks.add(new Block(350,550,50,50));

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

        int camX = WIDTH / 2 - player.x;
        int camY = HEIGHT / 2 - player.y;

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(camX, camY);

        if (showGrid) {
            Grid.drawGrid(g, WIDTH, HEIGHT);
        }

        g.setColor(Color.DARK_GRAY);
        for (Wall wall : walls) {
            g.fillRect(wall.x, wall.y, wall.width, wall.height);
        }

        g.setColor(Color.ORANGE);
        for (Block block : blocks) {
            g.fillRect(block.x, block.y, block.width, block.height);
        }

        g.setColor(Color.BLUE);
        g.fillOval(player.x, player.y, player.size, player.size);

        g2d.translate(-camX, -camY);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int timeLeft = TIME_LIMIT - (int) ((System.currentTimeMillis() - startTime) / 1000);
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        g.drawString(String.format("Time Left: %d:%02d", minutes, seconds), 10, 20);

        if (!running) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", WIDTH / 2 - 120, HEIGHT / 2);
        }
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
    public Block(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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
}
