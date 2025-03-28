package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.*;

public class GameHandler extends JPanel implements Runnable {
    private final int WIDTH = 800, HEIGHT = 600;
    private final int WORLD_SIZE_WIDTH = (10000 - WIDTH), WORLD_SIZE_HEIGHT = (10000 - 600);
    private Thread gameThread;
    public static boolean running = false;
    private Player player;
    private NPC npc;
    private ArrayList<Wall> walls;
    private ArrayList<Block> blocks;
    private ArrayList<Crack> cracks;
    private ArrayList<Staircase> staircases;
    private ArrayList<Enemy> enemies;
    private long startTime;
    private final int TIME_LIMIT = 100000;
    private boolean showGrid = false;
    private Image level1Image;
    private Image level2Image;
    private Image level3Image;
    private Image level4Image;
    private Image jumpscareImage;

    private boolean startGame = false;
    private boolean nextLevel2 = false;
    private boolean nextLevel3 = false;
    private boolean nextLevel4 = false;
    private boolean endgame = false;

    private boolean canUseAbility = true;
    private long abilityCooldownEnd = 0;  // Stores cooldown end time
    private long abilityActiveEnd = 0;    // Stores freeze end time

    Block block1 = new Block(750,750,50,50,"School Secrets");
    Block block2 = new Block(250,500,50,50,"Come");
    Block block3 = new Block(600,200,50,50,"Alive");

    // every single class will be in their own independent class script
    // next week for more organized workspace

    public GameHandler() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        addKeyListener(new KeyHandler());

        jumpscareImage = new ImageIcon(getClass().getResource("/game/images/jumpscare.png")).getImage();
        level1Image = new ImageIcon(getClass().getResource("/game/images/level_1.png")).getImage();
        level2Image = new ImageIcon(getClass().getResource("/game/images/level_2.png")).getImage();
        level3Image = new ImageIcon(getClass().getResource("/game/images/level_3.png")).getImage();
        level4Image = new ImageIcon(getClass().getResource("/game/images/level_4.png")).getImage();

        player = new Player(900, 900);
        //enemy = new Enemy(100,100,32,32);
        walls = new ArrayList<>();
        blocks = new ArrayList<>();
        cracks = new ArrayList<>();
        staircases = new ArrayList<>();
        enemies = new ArrayList<>();


        // LONG AHH CODE gotta optimize prob next week
        // yeah for now its like this
        // id probably make sure that i dont make 1:50 so that i dont have to keep doing calculations in my head
        // when positioning stuff

        // Level 1

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
        walls.add(new Wall(350, 250, 50,50));

        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);

        npc = new NPC(700,550);
        // Level 2

        walls.add(new Wall(1500, 0, 1050,50));
        walls.add(new Wall(1500, 50, 50,950));
        walls.add(new Wall(1500, 950, 1050,50));
        walls.add(new Wall(2500, 50, 50,950));

        walls.add(new Wall(150 + 1500, 350, 200,50));
        walls.add(new Wall(150 + 1500, 400, 50,350));
        walls.add(new Wall(200 + 1500, 700, 100,50));
        walls.add(new Wall(400 + 1500, 700, 200,50));
        walls.add(new Wall(550 + 1500, 400, 50,350));
        walls.add(new Wall(600 + 1500, 400, 150,50));
        walls.add(new Wall(700 + 1500, 100, 50,300));
        walls.add(new Wall(350 + 1500, 100, 50,150));
        walls.add(new Wall(350 + 1500, 300, 50,100));
        walls.add(new Wall(400 + 1500, 100, 300,50));
        walls.add(new Wall(150 + 1500, 150, 200,50));
        walls.add(new Wall(150 + 1500, 150, 50,200));

        // Level 3

        walls.add(new Wall(3500, 0, 1050,50));
        walls.add(new Wall(3500, 50, 50,950));
        walls.add(new Wall(3500, 950, 1050,50));
        walls.add(new Wall(4500, 50, 50,950));

        walls.add(new Wall(100 + 3500, 150, 350,50));
        walls.add(new Wall(400 + 3500, 100, 350,50));
        walls.add(new Wall(700 + 3500, 150, 50,350));
        walls.add(new Wall(450 + 3500, 300, 300,50));
        walls.add(new Wall(750 + 3500, 450, 150,50));
        walls.add(new Wall(850 + 3500, 500, 50,150));
        walls.add(new Wall(100 + 3500, 200, 50,300));
        walls.add(new Wall(150 + 3500, 450, 300,50));
        walls.add(new Wall(400 + 3500, 500, 50,100));
        walls.add(new Wall(200 + 3500, 550, 250,50));
        walls.add(new Wall(200 + 3500, 600, 50,250));
        walls.add(new Wall(250 + 3500, 800, 250,50));
        walls.add(new Wall(450 + 3500, 700, 50,100));
        walls.add(new Wall(500 + 3500, 700, 250,50));
        walls.add(new Wall(700 + 3500, 650, 200,50));

        // Level 4

        walls.add(new Wall(5500, 0, 1050,50));
        walls.add(new Wall(5500, 50, 50,950));
        walls.add(new Wall(5500, 950, 1050,50));
        walls.add(new Wall(6500, 50, 50,950));

        walls.add(new Wall(100 + 5500, 150, 350,50));
        walls.add(new Wall(400 + 5500, 100, 350,50));
        walls.add(new Wall(700 + 5500, 150, 50,350));
        walls.add(new Wall(450 + 5500, 300, 300,50));
        walls.add(new Wall(750 + 5500, 450, 150,50));
        walls.add(new Wall(850 + 5500, 500, 50,150));
        walls.add(new Wall(100 + 5500, 200, 50,300));
        walls.add(new Wall(150 + 5500, 450, 300,50));
        walls.add(new Wall(400 + 5500, 500, 50,100));
        walls.add(new Wall(200 + 5500, 550, 250,50));
        walls.add(new Wall(200 + 5500, 600, 50,250));
        walls.add(new Wall(250 + 5500, 800, 250,50));
        walls.add(new Wall(450 + 5500, 700, 50,100));
        walls.add(new Wall(500 + 5500, 700, 50,100));
        walls.add(new Wall(650 + 5500, 700, 50,100));
        walls.add(new Wall(550 + 5500, 800, 100,50));
        walls.add(new Wall(700 + 5500, 650, 200,50));

//        blocks.add(new Block(600,300,50,50, "SET"));
//        blocks.add(new Block(250,250,50,50, "ME"));
//        blocks.add(new Block(200,350,50,50, "FREE"));
//
//        blocks.add(new Block(350,550,50,50, "SET"));
//        blocks.add(new Block(350,600,50,50, "ME"));
//        blocks.add(new Block(350,650,50,50, "FREE"));

//        blocks.add(new Block(750,750,50,50,"LET"));
//        blocks.add(new Block(800,700,50,50,"ME"));
//        blocks.add(new Block(700,800,50,50,"IN"));

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
        player.update(walls, blocks, cracks, staircases);
        npc.update(player);
        for (Enemy enemy : enemies) {
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

        long currentTime = System.currentTimeMillis();

        // Unfreeze enemy after 5 seconds
        if (currentTime >= abilityActiveEnd && abilityActiveEnd > 0) {
            for (Enemy enemy : enemies) {
                enemy.speed = 2;
                abilityActiveEnd = 0;
            }
        }

        // Reset ability cooldown after 15 seconds
        if (currentTime >= abilityCooldownEnd && abilityCooldownEnd > 0) {
            canUseAbility = true;
            abilityCooldownEnd = 0;
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

        long currentTime = System.currentTimeMillis();

        updateRules();

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(camX, camY);

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(level1Image, 0, 0, gridSize * gridWidth, gridSize * gridHeight, this);
        g.drawImage(level2Image, 1500, 0, gridSize * gridWidth, gridSize * gridHeight, this);
        g.drawImage(level3Image, 3500, 0, gridSize * gridWidth, gridSize * gridHeight, this);
        g.drawImage(level4Image, 5500, 0, gridSize * gridWidth, gridSize * gridHeight, this);

        if (showGrid) {
            Grid.drawGrid(g, WIDTH, HEIGHT);
        }

        if (nextLevel2) {
            block1.x = 300 + 1500;
            block1.y = 500;
            block1.label = "Give Me";

            block2.x = 450 + 1500;
            block2.y = 500;
            block2.label = "A";

            block3.x = 450 + 1500;
            block3.y = 200;
            block3.label = "Way";

            player.x = 1600;
            nextLevel2 = false;
        }

        if (nextLevel3) {
            staircases.add(new Staircase(650 + 1500, 150, 50, 50));
        }

        if (nextLevel3 && Config.isInStaircase) {
            Config.isInStaircase = false;

            block1.x = 150 + 3500;
            block1.y = 300;
            block1.label = "Uncover";

            block2.x = 550 + 3500;
            block2.y = 200;
            block2.label = "This";

            block3.x = 500 + 3500;
            block3.y = 600;
            block3.label = "Deepest Secret";

            player.x = 3750;
            player.y = 300;
            nextLevel3 = false;
        }

        if (nextLevel4) {
            enemies.add(new Enemy(550 + 5500,700,32,32));
            enemies.add(new Enemy(300 + 5500,650,32,32));

            block1.x = 600 + 5500;
            block1.y = 500;
            block1.label = "The";

            block2.x = 300 + 5500;
            block2.y = 600;
            block2.label = "Final Way";

            block3.x = 150 + 5500;
            block3.y = 250;
            block3.label = "Open Up";

            player.x = 5750;
            nextLevel4 = false;
        }

        g.setColor(Color.DARK_GRAY);
        for (Wall wall : walls) {
            g.fillRect(wall.x, wall.y, wall.width, wall.height);
        }

        g.setColor(Color.ORANGE);
        for (Block block : blocks) {
            block.draw(g, block.x, block.y, block.width, block.height);
        }

        g.setColor(Color.LIGHT_GRAY);
        for (Crack crack : cracks) {
            crack.draw(g);
        }

        g.setColor(Color.PINK);
        for (Staircase staircase : staircases) {
            g.fillRect(staircase.x, staircase.y, staircase.width, staircase.height);
        }

        for (Enemy enemy : enemies) {
            g.setColor(Color.RED);
            enemy.draw(g);
        }

        player.draw(g);
        npc.draw(g);

        g2d.translate(-camX, -camY);

        // Not putting this in a function cuz it aint that big
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int timeLeft = TIME_LIMIT - (int) ((System.currentTimeMillis() - startTime) / 1000);
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        g.drawString(String.format("Time Left: %d:%02d", minutes, seconds), 10, 20);

        drawStaminaBar(g, player, WIDTH);

        if (!canUseAbility) {
            int remainingCooldown = (int) ((abilityCooldownEnd - currentTime) / 1000);
            g.setColor(Color.RED);
            g.drawString("Cooldown: " + remainingCooldown + "s", getWidth() - 150, 30);
        } else {
            g.setColor(Color.GREEN);
            g.drawString("Ability Ready!", getWidth() - 150, 30);
        }

        if (!running) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(jumpscareImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", getWidth() / 2 - 120, getHeight() / 2);
        }

        if (!startGame) {
            g.setColor(Color.BLACK);
            g.fillRect(0,0,WIDTH,HEIGHT);

            //not centered im centering it later

            g.setColor(Color.RED);
            g.drawString("Horror Game", 300, 100);
            g.drawString("<Press Space to Start the game!>", 250, 200);
        }

        if (endgame) {
            endgame = false;
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("YOU WIN", getWidth() / 2 - 120, getHeight() / 2);
        }
    }

    private void drawStaminaBar(Graphics g, Player player, int screenWidth) {
        int barWidth = 150;
        int barHeight = 10;
        int x = (screenWidth - barWidth) / 2;
        int y = 20;

        double staminaPercent = player.stamina / player.maxStamina;
        int currentWidth = (int) (barWidth * staminaPercent);

        g.setColor(Color.GRAY);
        g.fillRect(x, y, barWidth, barHeight);

        g.setColor(staminaPercent > 0.3 ? Color.GREEN : Color.RED);
        g.fillRect(x, y, currentWidth, barHeight);

        g.setColor(Color.BLACK);
        g.drawRect(x, y, barWidth, barHeight);
    }


    private void updateRules() {
        // Reset colors
        for (Block block : blocks) {
            block.color = Color.ORANGE;
        }

        // Check for valid sentences using a hashmap grid system
        buildBlockMap();

        boolean foundCrackFix = false;
        boolean foundNextLevel2 = false;
        boolean foundNextLevel3 = false;
        boolean foundNextLevel4 = false;
        boolean foundEndGame = false;

        for (Block block : blocks) {
            Point pos = getGridPosition(block.x, block.y);

            Block right = blockMap.get(new Point(pos.x + 1, pos.y));
            Block right2 = blockMap.get(new Point(pos.x + 2, pos.y));

            Block down = blockMap.get(new Point(pos.x, pos.y + 1));
            Block down2 = blockMap.get(new Point(pos.x, pos.y + 2));

            if (right != null && right2 != null && isValidSJLI(block, right, right2)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                foundCrackFix = true;
            }

            if (down != null && down2 != null && isValidSJLI(block, down, down2)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                foundCrackFix = true;
            }

            if (right != null && right2 != null && isValidSSCA(block, right, right2)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                foundNextLevel2 = true;
            }

            if (down != null && down2 != null && isValidSSCA(block, down, down2)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                foundNextLevel2 = true;
            }

            if (right != null && right2 != null && isValidGMAW(block, right, right2)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                foundNextLevel3 = true;
            }

            if (down != null && down2 != null && isValidGMAW(block, down, down2)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                foundNextLevel3 = true;
            }

            if (right != null && right2 != null && isValidUTDS(block, right, right2)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                foundNextLevel4 = true;
            }

            if (down != null && down2 != null && isValidUTDS(block, down, down2)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                foundNextLevel4 = true;
            }

            if (right != null && right2 != null && isValidTFWOU(block, right, right2)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                foundEndGame = true;
            }

            if (down != null && down2 != null && isValidTFWOU(block, down, down2)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                foundEndGame = true;
            }
        }

        // Apply results after checking all blocks
        nextLevel2 = foundNextLevel2;
        nextLevel3 = foundNextLevel3;
        nextLevel4 = foundNextLevel4;
        endgame = foundEndGame;

        if (foundCrackFix) {
            for (Crack crack : cracks) {
                crack.setFixed(true); // Mark all cracks as fixed (passable)
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

    private boolean isValidSSCA(Block a, Block b, Block c) {
        List<String> words = Arrays.asList(a.label, b.label, c.label);
        return words.contains("School Secrets") && words.contains("Come") && words.contains("Alive");
    }

    private boolean isValidGMAW(Block a, Block b, Block c) {
        List<String> words = Arrays.asList(a.label, b.label, c.label);
        return words.contains("Give Me") && words.contains("A") && words.contains("Way");
    }

    private boolean isValidUTDS(Block a, Block b, Block c) {
        List<String> words = Arrays.asList(a.label, b.label, c.label);
        return words.contains("Uncover") && words.contains("This") && words.contains("Deepest Secret");
    }

    private boolean isValidTFWOU(Block a, Block b, Block c) {
        List<String> words = Arrays.asList(a.label, b.label, c.label);
        return words.contains("The") && words.contains("Final Way") && words.contains("Open Up");
    }

    private boolean isValidSJLI(Block a, Block b, Block c) {
        List<String> words = Arrays.asList(a.label, b.label, c.label);
        return words.contains("Crack") && words.contains("Is") && words.contains("Fix");
    }

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_G) {
                showGrid = !showGrid;
            }

            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                if (!startGame) {
                    startGame = true;
                    startTime = System.currentTimeMillis();
                }
            }

            if (e.getKeyCode() == KeyEvent.VK_E && canUseAbility) {
                for (Enemy enemy : enemies) {
                    long currentTime = System.currentTimeMillis();
                    enemy.speed = 0; // Freeze enemy
                    abilityActiveEnd = currentTime + 5000; // Freeze lasts 5 seconds
                    abilityCooldownEnd = currentTime + 15000; // 15 sec cooldown
                    canUseAbility = false; // Disable ability until cooldown ends
                }
            }

            player.setKey(e.getKeyCode(), true, npc);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.setKey(e.getKeyCode(), false, npc);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Project C");
        GameHandler panel = new GameHandler();
        frame.setTitle("Project C");
        frame.add(panel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class Grid {
    public static void drawGrid(Graphics g, int width, int height) {
        width += 6200;
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

    private double speed = 0;
    private double acceleration = 0.5;
    private double maxSpeed = 3;
    private double sprintSpeed = 5;
    private double deceleration = 0.4;

    private boolean up, down, left, right, sprinting;

    public double stamina = 100;
    public double maxStamina = 100;
    private double staminaRegen = 0.3;
    private double staminaDrain = 1.3;

    // Animation System
    private int animationIndex = 0;
    private int animationTimer = 0;
    private int animationSpeed = 10;
    private Color[] animationFrames = {Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED}; // 4 frames

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setKey(int key, boolean pressed, NPC npc) {
        if (key == KeyEvent.VK_W) up = pressed;
        if (key == KeyEvent.VK_S) down = pressed;
        if (key == KeyEvent.VK_A) left = pressed;
        if (key == KeyEvent.VK_D) right = pressed;
        if (key == KeyEvent.VK_SHIFT) sprinting = pressed;

        if (pressed) {
            if (key == KeyEvent.VK_F) npc.interact();
            if (key == KeyEvent.VK_SPACE && npc.isInDialogue()) npc.exitDialogue();
        }
    }

    public void update(ArrayList<Wall> walls, ArrayList<Block> blocks, ArrayList<Crack> cracks, ArrayList<Staircase> staircases) {
        double targetSpeed = sprinting && stamina > 0 ? sprintSpeed : maxSpeed;

        if (sprinting && stamina > 0) {
            stamina -= staminaDrain;
            if (stamina < 0) stamina = 0;
        } else {
            stamina += staminaRegen;
            if (stamina > 100) stamina = 100;
        }

        boolean moving = false; // Track if the player is actually moving

        if ((up && !down) || (down && !up) || (left && !right) || (right && !left)) {
            speed += acceleration;
            if (speed > targetSpeed) speed = targetSpeed;
            moving = true;
        } else {
            speed -= deceleration;
            if (speed < 0) speed = 0;
        }

        int newX = x, newY = y;

        if (speed > 0) { // Only move if speed > 0
            if (up) newY -= speed;
            if (down) newY += speed;
            if (left) newX -= speed;
            if (right) newX += speed;
        }

        if (!collides(newX, y, walls, blocks, cracks, staircases)) x = newX;
        if (!collides(x, newY, walls, blocks, cracks, staircases)) y = newY;

        // Final check: Is the player actually moving?
        if (speed > 0) moving = true;

        // Animation Logic
//        if (moving) {
//            animationTimer++;
//            int sprintFactor = sprinting ? 2 : 1; // Sprinting speeds up animation
//            if (animationTimer >= animationSpeed / sprintFactor) {
//                animationIndex = (animationIndex + 1) % animationFrames.length;
//                animationTimer = 0;
//            }
//        } else {
//            animationIndex = 0; // Reset animation when not moving
//        }
    }

    public void draw(Graphics g) {
        g.setColor(animationFrames[animationIndex]);
        g.fillOval(x, y, size, size);

    }

    private boolean collides(int newX, int newY, ArrayList<Wall> walls, ArrayList<Block> blocks, ArrayList<Crack> cracks, ArrayList<Staircase> staircases) {
        for (Wall wall : walls) {
            if (newX < wall.x + wall.width && newX + size > wall.x &&
                    newY < wall.y + wall.height && newY + size > wall.y) {
                return true;
            }
        }

        for (Crack crack : cracks) {
            if ((newX < crack.x + crack.width && newX + size > crack.x &&
                    newY < crack.y + crack.height && newY + size > crack.y) && !crack.isFixed) {
                return true;
            }
        }

        for (Staircase staircase : staircases) {
            if (newX < staircase.x + staircase.width && newX + size > staircase.x &&
                    newY < staircase.y + staircase.height && newY + size > staircase.y) {
                Config.isInStaircase = true;
                return false;
            }
        }

        for (Block block : blocks) {
            if (block.collides(newX, newY, size)) {
                block.push(newX - x, newY - y, walls, blocks, cracks);
                return true;
            }
        }
        return false;
    }
}

class NPC {
    private int x, y, size = 40;
    private boolean playerNearby = false;
    public boolean inDialogue = false;
    public String dialogue = "Hello there! Welcome to the adventure.";
    private Player player;

    public NPC(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update(Player player) {
        // Check if the player is nearby (distance threshold)
        int distance = (int) Math.sqrt(Math.pow(player.x - x, 2) + Math.pow(player.y - y, 2));
        playerNearby = distance < 60; // Adjust range as needed
        this.player = player;
    }

    public void interact() {
        if (playerNearby) {
            inDialogue = true;
        }
    }

    public void exitDialogue() {
        inDialogue = false;
    }

    public void draw(Graphics g) {
        // Draw NPC
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, size, size);

        // Draw "!" above NPC if player is nearby
        if (playerNearby && !inDialogue) {
            g.setColor(Color.WHITE);
            g.fillRect(x + size / 3, y - 20, size / 3, size / 3);
        }

        // Draw dialogue box if in dialogue
        if (inDialogue) {
            int boxWidth = 400;
            int x = player.x - 150;
            int y = player.y + 100;

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            FontMetrics fm = g.getFontMetrics();

            // Wrap text automatically
            List<String> lines = wrapText(dialogue, fm, boxWidth - 20);
            int boxHeight = 30 + (lines.size() * fm.getHeight());

            // Draw dialogue box
            g.setColor(Color.BLACK);
            g.fillRect(x, y, boxWidth, boxHeight);
            g.setColor(Color.WHITE);
            g.drawRect(x, y, boxWidth, boxHeight);

            // Draw text inside the box
            int textX = x + 10;
            int textY = y + 25;
            for (String line : lines) {
                g.drawString(line, textX, textY);
                textY += fm.getHeight();
            }
        }
    }

    public boolean isInDialogue() {
        return inDialogue;
    }

    private ArrayList<String> wrapText(String text, FontMetrics metrics, int maxWidth) {
        ArrayList<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            if (metrics.stringWidth(currentLine + word) < maxWidth) {
                currentLine.append(word).append(" ");
            } else {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder(word).append(" ");
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }
}

class Enemy {
    int x, y, width, height;
    int speed = 2;
    private ArrayList<Point> path = new ArrayList<>();

    public Enemy(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(Player player, ArrayList<Wall> walls) {
        // Calculate new path to player
        path = Pathfinder.findPath(x, y, player.x, player.y, walls);

        if (!path.isEmpty()) {
            Point nextPoint = path.get(0);

            // Move towards next point in path
            if (x < nextPoint.x) x += speed;
            if (x > nextPoint.x) x -= speed;
            if (y < nextPoint.y) y += speed;
            if (y > nextPoint.y) y -= speed;

            // Remove point if reached
            if (x == nextPoint.x && y == nextPoint.y) {
                path.remove(0);
            }

            if (collidesWithPlayer(player)) {
                System.out.println("Game Over! Enemy caught the player.");
                GameHandler.running = false;
            }
        }
    }


    private boolean collidesWithPlayer(Player player) {
        return x < player.x + player.size && x + width > player.x &&
                y < player.y + player.size && y + height > player.y;
    }



    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);

        // Debug: Draw path
//        g.setColor(Color.YELLOW);
//        for (Point p : path) {
//            g.fillRect(p.x, p.y, 5, 5);
//        }
    }
}

class Pathfinder {
    private static final int GRID_SIZE = 50;

    public static ArrayList<Point> findPath(int startX, int startY, int targetX, int targetY, ArrayList<Wall> walls) {
        Set<Point> closedSet = new HashSet<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));

        Map<Point, Point> cameFrom = new HashMap<>();
        Map<Point, Integer> gScore = new HashMap<>();

        Point start = new Point((startX / GRID_SIZE) * GRID_SIZE, (startY / GRID_SIZE) * GRID_SIZE);
        Point target = new Point((targetX / GRID_SIZE) * GRID_SIZE, (targetY / GRID_SIZE) * GRID_SIZE);

        gScore.put(start, 0);
        openSet.add(new Node(start, heuristic(start, target)));

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            Point current = currentNode.point;

            if (current.equals(target)) {
                return reconstructPath(cameFrom, current);
            }

            closedSet.add(current);

            for (Point neighbor : getNeighbors(current, walls)) {
                if (closedSet.contains(neighbor)) continue;

                int tentativeGScore = gScore.getOrDefault(current, Integer.MAX_VALUE) + GRID_SIZE;
                if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    int fScore = tentativeGScore + heuristic(neighbor, target);

                    openSet.remove(new Node(neighbor, 0)); // Remove outdated nodes
                    openSet.add(new Node(neighbor, fScore));
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    private static int heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static ArrayList<Point> reconstructPath(Map<Point, Point> cameFrom, Point current) {
        ArrayList<Point> path = new ArrayList<>();
        while (cameFrom.containsKey(current)) {
            path.add(0, current);
            current = cameFrom.get(current);
        }
        return path;
    }

    private static List<Point> getNeighbors(Point current, ArrayList<Wall> walls) {
        List<Point> neighbors = new ArrayList<>();
        int[][] moves = {{-GRID_SIZE, 0}, {GRID_SIZE, 0}, {0, -GRID_SIZE}, {0, GRID_SIZE}};

        for (int[] move : moves) {
            Point newPoint = new Point(current.x + move[0], current.y + move[1]);
            if (!isColliding(newPoint, walls)) {
                neighbors.add(newPoint);
            }
        }
        return neighbors;
    }

    private static boolean isColliding(Point point, ArrayList<Wall> walls) {
        return walls.stream().anyMatch(wall ->
                point.x < wall.x + wall.width && point.x + GRID_SIZE > wall.x &&
                        point.y < wall.y + wall.height && point.y + GRID_SIZE > wall.y);
    }

    static class Node {
        Point point;
        int f;

        Node(Point point, int f) {
            this.point = point;
            this.f = f;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Node node = (Node) obj;
            return Objects.equals(point, node.point);
        }

        @Override
        public int hashCode() {
            return Objects.hash(point);
        }
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

    public void push(int dx, int dy, ArrayList<Wall> walls, ArrayList<Block> blocks, ArrayList<Crack> cracks) {
        int newX = x + dx;
        int newY = y + dy;

        for (Wall wall : walls) {
            if (newX < wall.x + wall.width && newX + width > wall.x && newY < wall.y + wall.height && newY + height > wall.y) {
                return;
            }
        }

        for (Crack crack : cracks) {
            if (newX < crack.x + crack.width && newX + width > crack.x && newY < crack.y + crack.height && newY + height > crack.y) {
                return;
            }
        }

        for (Block block : blocks) {
            if (block != this && block.collides(newX, newY, width)) {
                block.push(dx, dy, walls, blocks, cracks);
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

class Crack {
    int x, y, width, height;
    boolean isFixed;

    public Crack(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isFixed = false; // By default, it is broken (impassable)
    }

    public void setFixed(boolean fixed) {
        this.isFixed = fixed;
    }

    public boolean isPassable() {
        return isFixed;
    }

    public void draw(Graphics g) {
        g.setColor(isFixed ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

class Staircase {
    int x, y, width, height;
    public Staircase(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}