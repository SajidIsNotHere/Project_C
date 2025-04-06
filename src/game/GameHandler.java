package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.*;

import javax.sound.sampled.*;
import java.io.File;

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
    private ArrayList<Gate> gates;
    private ArrayList<Enemy> enemies;
    private long startTime;
    private final int TIME_LIMIT = 8 * 60;
    private boolean showGrid = false;
    private Image level1Image;
    private Image level2Image;
    private Image level3Image;
    private Image level4Image;
    private Image level5Image;
    private Image jumpscareImage;
    private Image mainMenuImage;

    private Image controlImage;
    private Image mechanicImage;
    private Image storyImage;

    private Image crossImage;
    private Image npcImage;
    private Image titleImage;
    private Image crackImage;
    private Image keyImage;
    private Image gateImage;
    private Image staircaseImage;

    private FullScreenBlinkingLight blinkingLight;

    private int startStep = 0;

    private boolean startGame = false;
    private boolean nextLevel2 = false;
    private boolean nextLevel3 = false;
    private boolean nextLevel4 = false;
    private boolean nextLevel5 = false;

    private boolean canUseAbility = true;
    private long abilityCooldownEnd = 0;  // Stores cooldown end time
    private long abilityActiveEnd = 0;    // Stores freeze end time

    ScreenTransition transition = new ScreenTransition();
    SoundPlayer backgroundMusic = new SoundPlayer("src/game/music/bg.wav");
    SoundPlayer guideAudio = new SoundPlayer("src/game/music/first.wav");

    Buttons startButton = new Buttons(300, 250, 200, 100, Color.GREEN, "Start");
    Buttons exitButton = new Buttons(300,500,200,100, Color.RED, "Exit");

    Block block1 = new Block(750,750,50,50,"School");
    Block block2 = new Block(250,500,50,50,"Secrets");
    Block block3 = new Block(600,200,50,50,"Come");
    Block block4 = new Block(150,150,50,50,"Alive");
    Block block5 = new Block(0, 2000,50,50,"Alive");

    Block crackWord_1 = new Block(250 + 3500,250,50,50,"Fix");
    Block crackWord_2 = new Block(500 + 3500,500,50,50,"The");
    Block crackWord_3 = new Block(300 + 3500, 650,50,50,"Way");

    Gate endGate = new Gate(700, 850, 200, 150);

    Crack crack_1 = new Crack(450 + 3500, 200, 50,50);
    Crack crack_2 = new Crack(450 + 3500, 250, 50,50);
    Crack crack_3 = new Crack(700 + 3500, 500, 50,50);
    Crack crack_4 = new Crack(700 + 3500, 550, 50,50);
    Crack crack_5 = new Crack(700 + 3500, 600, 50,50);
    Crack crack_6 = new Crack(450 + 3500, 2000, 50,50);
    Crack crack_7 = new Crack(450 + 3500, 2000, 50,50);

    // every single class will be in their own independent class script
    // next week for more organized workspace

    public GameHandler() {
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
        addKeyListener(new KeyHandler());
        addMouseListener(new MouseHandler());

        blinkingLight = new FullScreenBlinkingLight();

        mainMenuImage = new ImageIcon(getClass().getResource("/game/images/main_menu.png")).getImage();
        jumpscareImage = new ImageIcon(getClass().getResource("/game/images/jumpscare.png")).getImage();
        level1Image = new ImageIcon(getClass().getResource("/game/images/level_1.png")).getImage();
        level2Image = new ImageIcon(getClass().getResource("/game/images/level_2.png")).getImage();
        level3Image = new ImageIcon(getClass().getResource("/game/images/level_3.png")).getImage();
        level4Image = new ImageIcon(getClass().getResource("/game/images/level_4.png")).getImage();
        level5Image = new ImageIcon(getClass().getResource("/game/images/level_5.png")).getImage();

        controlImage = new ImageIcon(getClass().getResource("/game/images/controls.png")).getImage();
        storyImage = new ImageIcon(getClass().getResource("/game/images/story.png")).getImage();
        mechanicImage = new ImageIcon(getClass().getResource("/game/images/mechanics.png")).getImage();

        crossImage = new ImageIcon(getClass().getResource("/game/images/cross.png")).getImage();
        npcImage = new ImageIcon(getClass().getResource("/game/images/npc.png")).getImage();
        titleImage = new ImageIcon(getClass().getResource("/game/images/title.png")).getImage();
        crackImage = new ImageIcon(getClass().getResource("/game/images/crack.png")).getImage();
        keyImage = new ImageIcon(getClass().getResource("/game/images/key.png")).getImage();
        gateImage = new ImageIcon(getClass().getResource("/game/images/gate.png")).getImage();
        staircaseImage = new ImageIcon(getClass().getResource("/game/images/staircase.png")).getImage();

        player = new Player(450, 850);
        //enemy = new Enemy(100,100,32,32);
        //enemies.add(new Enemy(100,100,32,32));
        walls = new ArrayList<>();
        blocks = new ArrayList<>();
        cracks = new ArrayList<>();
        staircases = new ArrayList<>();
        gates = new ArrayList<>();
        enemies = new ArrayList<>();
        backgroundMusic.play();

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

//        walls.add(new Wall(150, 350, 200, 10)); // Thinner height
//        walls.add(new Wall(150, 400, 10, 350)); // Thinner width
//        walls.add(new Wall(200, 700, 100, 10));
//        walls.add(new Wall(400, 700, 200, 10));
//        walls.add(new Wall(550, 400, 10, 350));
//        walls.add(new Wall(600, 400, 150, 10));
//        walls.add(new Wall(700, 100, 10, 300));
//        walls.add(new Wall(350, 100, 10, 150));
//        walls.add(new Wall(350, 300, 10, 100));
//        walls.add(new Wall(400, 100, 300, 10));
//        walls.add(new Wall(350, 250, 10, 10)); // Even smaller



        cracks.add(crack_1);
        cracks.add(crack_2);
        cracks.add(crack_3);
        cracks.add(crack_4);
        cracks.add(crack_5);
        cracks.add(crack_6);
        cracks.add(crack_7);

        blocks.add(block1);
        blocks.add(block2);
        blocks.add(block3);
        blocks.add(block4);
        blocks.add(block5);
        blocks.add(crackWord_1);
        blocks.add(crackWord_2);
        blocks.add(crackWord_3);

        gates.add(endGate);

        npc = new NPC(350 + 3700,200);
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

        // Level 5

        walls.add(new Wall(7500, 0, 1050,50));
        walls.add(new Wall(7500, 50, 50,950));
        walls.add(new Wall(7500, 950, 1050,50));
        walls.add(new Wall(8500, 50, 50,950));

        walls.add(new Wall(100 + 7500, 150, 350,50));
        walls.add(new Wall(400 + 7500, 100, 350,50));
        walls.add(new Wall(700 + 7500, 150, 50,350));
        walls.add(new Wall(450 + 7500, 300, 300,50));
        walls.add(new Wall(750 + 7500, 450, 150,50));
        walls.add(new Wall(850 + 7500, 500, 50,150));
        walls.add(new Wall(100 + 7500, 200, 50,300));
        walls.add(new Wall(150 + 7500, 450, 300,50));
        walls.add(new Wall(400 + 7500, 500, 50,100));
        walls.add(new Wall(200 + 7500, 550, 250,50));
        walls.add(new Wall(200 + 7500, 600, 50,250));
        walls.add(new Wall(250 + 7500, 800, 250,50));
        walls.add(new Wall(450 + 7500, 700, 50,100));
        walls.add(new Wall(500 + 7500, 700, 50,100));
        walls.add(new Wall(650 + 7500, 700, 50,100));
        walls.add(new Wall(550 + 7500, 800, 100,50));
        walls.add(new Wall(700 + 7500, 650, 200,50));

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
        player.update(walls, blocks, cracks, staircases, gates);
        npc.update(player);
        transition.update();
        blinkingLight.update();
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
        g.drawImage(level5Image, 7500, 0, gridSize * gridWidth, gridSize * gridHeight, this);

        if (showGrid) {
           Grid.drawGrid(g, WIDTH, HEIGHT);
        }

        if (nextLevel2) {
            transition.startTransition();
            block1.x = 300 + 1500;
            block1.y = 500;
            block1.label = "Give";

            block2.x = 450 + 1500;
            block2.y = 500;
            block2.label = "A";

            block3.x = 600 + 1500;
            block3.y = 200;
            block3.label = "Way";

            block4.x = 250 + 1500;
            block4.y = 100;
            block4.label = "Me";

            endGate.x = endGate.x + 1500;

            player.x = player.x + 1500;
            nextLevel2 = false;
        }

        if (nextLevel3) {
            staircases.add(Config.startEndStair);
        }

        if (nextLevel3 && Config.isInStaircase) {
            transition.startTransition();
            Config.isInStaircase = false;

            block1.x = 550 + 3500;
            block1.y = 250;
            block1.label = "Uncover";

            block2.x = 200 + 3500;
            block2.y = 350;
            block2.label = "This";

            block3.x = 750 + 3500;
            block3.y = 550;
            block3.label = "Deepest";

            block4.x = 500 + 3500;
            block4.y = 600;
            block4.label = "Secret";

            player.x = 3750;
            player.y = 300;
            nextLevel3 = false;
        }

        if (nextLevel4) {
            transition.startTransition();
            npc.interact("Form a word that makes you able to pass through the Stone Blocks that blocks your \"WAY\" :)", true);
            enemies.add(new Enemy(550 + 5500,700,32,32));
            enemies.add(new Enemy(300 + 5500,650,32,32));

            crack_1.x = 5650;
            crack_1.y = 300;

            crack_2.x = 5700;
            crack_2.y = 300;

            crack_3.x = 5750;
            crack_3.y = 350;

            crack_4.x = 5750;
            crack_4.y = 400;

            crack_5.x = 6200;
            crack_5.y = 500;

            crack_6.x = 6200;
            crack_6.y = 550;

            crack_7.x = 6200;
            crack_7.y = 600;

            crackWord_1.x = 5850;
            crackWord_1.y = 250;

            crackWord_2.x = 6050;
            crackWord_2.y = 500;

            crackWord_3.x = 5900;
            crackWord_3.y = 650;

            block1.x = 5700;
            block1.y = 350;
            block1.label = "The";

            block2.x = 6100;
            block2.y = 200;
            block2.label = "Final";

            block3.x = 6100;
            block3.y = 400;
            block3.label = "Way";

            block4.x = 6250;
            block4.y = 550;
            block4.label = "Open";

            block5.x = 5800;
            block5.y = 600;
            block5.label = "Up";

            player.x = 5850;
            nextLevel4 = false;
        }

        if (nextLevel5) {
            transition.startTransition();

            block1.x = 7750;
            block1.y = 350;
            block1.label = "The";

            block2.x = 8050;
            block2.y = 500;
            block2.label = "Key";

            block3.x = 7800;
            block3.y = 600;
            block3.label = "Is";

            block4.x = 8050;
            block4.y = 200;
            block4.label = "A";

            block5.x = 8250;
            block5.y = 550;
            block5.label = "Secret";

            player.x = 7850;
            nextLevel5 = false;
        }

        if (Config.isInStaircase && Config.hasKey) {
            transition.startTransition();
            player.x = 2150;
            player.y = 150;
        }

        g.setColor(Color.DARK_GRAY);
        for (Wall wall : walls) {
            g.fillRect(wall.x, wall.y, wall.width, wall.height);
        }

        g.setColor(Color.GREEN);
        for (Gate gate : gates) {
            g.drawImage(gateImage, gate.x, gate.y, gate.width, gate.height, this);
        }

        g.setColor(Color.ORANGE);
        for (Block block : blocks) {
            block.draw(g, block.x, block.y, block.width, block.height);
        }

        g.setColor(Color.LIGHT_GRAY);
        for (Crack crack : cracks) {
            crack.draw(g, crackImage);
        }

        g.setColor(Color.PINK);
        for (Staircase staircase : staircases) {
            g.drawImage(staircaseImage, staircase.x - 25, staircase.y - 25, staircase.width + 50, staircase.height + 50, this);
        }

        for (Enemy enemy : enemies) {
            g.setColor(Color.RED);
            enemy.draw(g);
        }

        player.draw(g, crossImage, keyImage);
        npc.draw(g, npcImage);

        if (Config.gateKey != null) {
            g.drawImage(keyImage, Config.gateKey.x, Config.gateKey.y, 50,50, this);
        }

        blinkingLight.draw(g, player.x, player.y);

        g2d.translate(-camX, -camY);

        // Not putting this in a function cuz it aint that big
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        int timeLeft = TIME_LIMIT - (int) ((System.currentTimeMillis() - startTime) / 1000);
        int minutes = timeLeft / 60;
        int seconds = timeLeft % 60;
        g.drawString(String.format("Time Left: %d:%02d", minutes, seconds), 10, 20);

        drawStaminaBar(g, player, WIDTH);

        if (Config.hasAbility) {
            if (!canUseAbility) {
                int remainingCooldown = (int) ((abilityCooldownEnd - currentTime) / 1000);
                g.setColor(Color.RED);
                g.drawString("Cooldown: " + remainingCooldown + "s", getWidth() - 150, 30);
            } else {
                g.setColor(Color.GREEN);
                g.drawString("Ability Ready!", getWidth() - 150, 30);
            }
        }

        if (!running) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(jumpscareImage, 0, 0, getWidth(), getHeight(), this);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("GAME OVER", getWidth() / 2 - 120, getHeight() / 2);
        }

        if (!startGame && startStep == 0) {
//            g.setColor(Color.BLACK);
//            g.fillRect(0,0,WIDTH,HEIGHT);
//
//            //not centered im centering it later
//
//            g.setColor(Color.RED);
//            g.drawString("Horror Game", 300, 100);
//            g.drawString("<Press Space to Start the game!>", 250, 200);

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(level1Image, 0, 0, WIDTH, HEIGHT, this);
            g.drawImage(mainMenuImage, 0, 0, WIDTH, HEIGHT, this);
            g.drawImage(titleImage, 300, -50, 200, 400, this);
        }

        if (!startGame && startStep == 1) {
            g.setColor(Color.BLACK);
            g.fillRect(0,0,WIDTH,HEIGHT);
            g.drawImage(storyImage, 267, 0, 266, 600, this);
            g.drawImage(controlImage, 0, 0, 266, 600, this);
            g.drawImage(mechanicImage, 533, 0, 266, 600, this);
            g.setColor(Color.yellow);
            g.drawString("Press Space to Continue to the next", 0,50);
        }

        if (Config.endGame) {
            Config.endGame = false;
            g.setColor(Color.BLACK);
            g.fillRect(0,0,WIDTH,HEIGHT);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("THE END...", getWidth() / 2 - 120, getHeight() / 2);
        }

        if (transition.transitionActive) {
            transition.render(g2d);
        }
//        startButton.draw(g);
//        exitButton.draw(g);

//        if (showGrid) {
//            Grid.drawGrid(g, WIDTH, HEIGHT);
//        }
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
        boolean foundNextLevel5 = false;

        for (Block block : blocks) {
            Point pos = getGridPosition(block.x, block.y);

            Block right = blockMap.get(new Point(pos.x + 1, pos.y));
            Block right2 = blockMap.get(new Point(pos.x + 2, pos.y));
            Block right3 = blockMap.get(new Point(pos.x + 3, pos.y));
            Block right4 = blockMap.get(new Point(pos.x + 4, pos.y));

            Block down = blockMap.get(new Point(pos.x, pos.y + 1));
            Block down2 = blockMap.get(new Point(pos.x, pos.y + 2));
            Block down3 = blockMap.get(new Point(pos.x, pos.y + 3));
            Block down4 = blockMap.get(new Point(pos.x, pos.y + 4));

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

            if (right != null && right2 != null && right3 != null && isValidSSCA(block, right, right2, right3)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                foundNextLevel2 = true;
            }

            if (down != null && down2 != null && down3 != null && isValidSSCA(block, down, down2, down3)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                foundNextLevel2 = true;
            }

            if (right != null && right2 != null && right3 != null && isValidGMAW(block, right, right2, right3)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                foundNextLevel3 = true;
            }

            if (down != null && down2 != null && down3 != null && isValidGMAW(block, down, down2, down3)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                foundNextLevel3 = true;
            }

            if (right != null && right2 != null && right3 != null && isValidUTDS(block, right, right2, right3)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                foundNextLevel4 = true;
            }

            if (down != null && down2 != null && down3 != null && isValidUTDS(block, down, down2, down3)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                foundNextLevel4 = true;
            }

            if (right != null && right2 != null && right3 != null && right4 != null && isValidTFWOU(block, right, right2, right3, right4)) {
                block.color = Color.BLUE;
                right.color = Color.BLUE;
                right2.color = Color.BLUE;
                foundNextLevel5 = true;
            }

            if (down != null && down2 != null && down3 != null && down4 != null && isValidTFWOU(block, down, down2, down3, down4)) {
                block.color = Color.BLUE;
                down.color = Color.BLUE;
                down2.color = Color.BLUE;
                foundNextLevel5 = true;
            }

            if (right != null && right2 != null && right3 != null && right4 != null && isValidTKIAS(block, right, right2, right3, right4)) {
                block.y = 5000;
                right.y = 2500;
                right2.y = 6000;
                right3.y = 7000;
                right4.y = 8000;
                Config.gateKey = new Key(8050, 500, 50,50);
            }

            if (down != null && down2 != null && down3 != null && down4 != null && isValidTKIAS(block, down, down2, down3, down4)) {
                block.y = 5000;
                down.y = 2500;
                down2.y = 6000;
                down3.y = 7000;
                down4.y = 8000;
                Config.gateKey = new Key(8050, 500, 50,50);
            }
        }

        // Apply results after checking all blocks
        nextLevel2 = foundNextLevel2;
        nextLevel3 = foundNextLevel3;
        nextLevel4 = foundNextLevel4;
        nextLevel5 = foundNextLevel5;

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

    private boolean isValidSSCA(Block a, Block b, Block c, Block d) {
        List<String> correctOrder = Arrays.asList("School", "Secrets", "Come", "Alive");
        List<String> currentOrder = Arrays.asList(a.label, b.label, c.label, d.label);

        return correctOrder.equals(currentOrder);
    }

    private boolean isValidGMAW(Block a, Block b, Block c, Block d) {
        List<String> correctOrder = Arrays.asList("Give", "Me", "A", "Way");
        List<String> currentOrder = Arrays.asList(a.label, b.label, c.label, d.label);

        return correctOrder.equals(currentOrder);
    }

    private boolean isValidUTDS(Block a, Block b, Block c, Block d) {
        List<String> correctOrder = Arrays.asList("Uncover", "This", "Deepest", "Secret");
        List<String> currentOrder = Arrays.asList(a.label, b.label, c.label, d.label);

        return correctOrder.equals(currentOrder);
    }

    private boolean isValidTFWOU(Block a, Block b, Block c, Block d, Block e) {
        List<String> correctOrder = Arrays.asList("The", "Final", "Way", "Open", "Up");
        List<String> currentOrder = Arrays.asList(a.label, b.label, c.label, d.label, e.label);

        return correctOrder.equals(currentOrder);
    }

    private boolean isValidTKIAS(Block a, Block b, Block c, Block d, Block e) {
        List<String> correctOrder = Arrays.asList("The", "Key", "Is", "A", "Secret");
        List<String> currentOrder = Arrays.asList(a.label, b.label, c.label, d.label, e.label);

        return correctOrder.equals(currentOrder);
    }

    private boolean isValidSJLI(Block a, Block b, Block c) {
        List<String> words = Arrays.asList(a.label, b.label, c.label);
        return words.contains("Fix") && words.contains("The") && words.contains("Way");
    }

    private class KeyHandler extends KeyAdapter {

        private boolean spacePressed = false; // Track SPACE key press
        private boolean kPressed = false;
        private boolean alignVertical = false;

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            player.setKey(e.getKeyCode(), true, npc);

            if (keyCode == KeyEvent.VK_G) {
                showGrid = !showGrid;
            }

            if (e.getKeyCode() == KeyEvent.VK_L) {
                alignVertical = !alignVertical; // Toggle state
            }

            if (e.getKeyCode() == KeyEvent.VK_K && !kPressed) {
                int gridSize = 50; // Assuming walls are placed on a grid
                int wallX, wallY;

                kPressed = true;

                if (alignVertical) {
                    wallX = player.x; // Keep X where the player is
                    wallY = Math.round(player.y / (float) gridSize) * gridSize; // Snap Y to grid
                    walls.add(new Wall(wallX, wallY, 15, 50));
                    System.out.println("walls.add(new Wall(" + wallX + ", " + wallY + ", 15, 50));");
                } else {
                    wallX = Math.round(player.x / (float) gridSize) * gridSize; // Snap X to grid
                    wallY = player.y; // Keep Y where the player is
                    walls.add(new Wall(wallX, wallY, 50, 15));
                    System.out.println("walls.add(new Wall(" + wallX + ", " + wallY + ", 50, 15));");
                }
            }

            if (keyCode == KeyEvent.VK_SPACE) {
                if (!startGame && startStep == 0 && !spacePressed) {
                    spacePressed = true;
                    startStep += 1;
                    guideAudio.play();
                }
                if (!startGame && startStep == 1 && !spacePressed) {
                    spacePressed = true;
                    startStep += 1;
                    guideAudio.stop();
                }
                if (!startGame && startStep == 2 && !spacePressed) {
                    spacePressed = true;
                    startGame = true;
                    guideAudio.stop();
                }
            }

            if (keyCode == KeyEvent.VK_E && canUseAbility && Config.hasAbility) {
                for (Enemy enemy : enemies) {
                    long currentTime = System.currentTimeMillis();
                    enemy.speed = 0; // Freeze enemy
                    abilityActiveEnd = currentTime + 5000; // Freeze lasts 5 seconds
                    abilityCooldownEnd = currentTime + 15000; // 15 sec cooldown
                    canUseAbility = false; // Disable ability until cooldown ends
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.setKey(e.getKeyCode(), false, npc);
            int keyCode = e.getKeyCode();

            // Reset flags when keys are released
            if (keyCode == KeyEvent.VK_SPACE) spacePressed = false;
            if (keyCode == KeyEvent.VK_K) kPressed = false;
        }
    }

    private boolean mouseClicked = false; // Prevents double-clicking

    private class MouseHandler extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();

            // Check if button is clicked
            if (startButton.isClicked(mouseX, mouseY) && !mouseClicked) {
                if (!startGame && startStep == 0 && !mouseClicked) {
                    mouseClicked = true;
                    startStep += 1;
                    guideAudio.play();
                }
                if (!startGame && startStep == 1 && !mouseClicked) {
                    mouseClicked = true;
                    startStep += 1;
                    guideAudio.stop();
                }
                if (!startGame && startStep == 2 && !mouseClicked) {
                    mouseClicked = true;
                    startGame = true;
                    guideAudio.stop();
                }
            }

            if (exitButton.isClicked(mouseX, mouseY)) {
                System.exit(0);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mouseClicked = false;
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
        width += 9200;
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
    int x, y;
    int sizeX = 40;
    int sizeY = 60;

    private double speed = 0;
    private double acceleration = 0.5;
    public double maxSpeed = 5;
    public double sprintSpeed = maxSpeed + 1;
    private double deceleration = 0.4;

    private boolean up, down, left, right, sprinting;

    private boolean rightOrLeft = false;

    public double stamina = 100;
    public double maxStamina = 100;
    private double staminaRegen = 0.3;
    private double staminaDrain = 1.3;

    // Animation System
    private int animationIndex = 0;
    private int animationTimer = 0;
    private int animationSpeed = 10;
    private Image[] animationFrames = {
            new ImageIcon(getClass().getResource("/game/images/player/walk1g.png")).getImage(),
            new ImageIcon(getClass().getResource("/game/images/player/walk2g.png")).getImage(),
            new ImageIcon(getClass().getResource("/game/images/player/walk3g.png")).getImage(),
            new ImageIcon(getClass().getResource("/game/images/player/walk4g.png")).getImage(),
            new ImageIcon(getClass().getResource("/game/images/player/walk5g.png")).getImage(),
            new ImageIcon(getClass().getResource("/game/images/player/walk6g.png")).getImage(),
            new ImageIcon(getClass().getResource("/game/images/player/walk7g.png")).getImage(),
            new ImageIcon(getClass().getResource("/game/images/player/walk8g.png")).getImage()
    }; // 8 frames

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setKey(int key, boolean pressed, NPC npc) {
        if (key == KeyEvent.VK_W) up = pressed;
        if (key == KeyEvent.VK_S) down = pressed;

        if (key == KeyEvent.VK_A) {
            left = pressed;
            if (pressed) rightOrLeft = false; // A was last pressed
        }
        if (key == KeyEvent.VK_D) {
            right = pressed;
            if (pressed) rightOrLeft = true; // D was last pressed
        }

        if (key == KeyEvent.VK_SHIFT) sprinting = pressed;

        if (pressed) {
            if (key == KeyEvent.VK_F) npc.interact("Hello Zelda, I am giving you this cross. This cross will scare the evil away. Be careful the evil always finds its way to hunt you down...", false);
            if (key == KeyEvent.VK_SPACE && npc.isInDialogue()) npc.exitDialogue();
        }
    }

    public void update(ArrayList<Wall> walls, ArrayList<Block> blocks, ArrayList<Crack> cracks, ArrayList<Staircase> staircases, ArrayList<Gate> gates) {
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

        if (!collides(newX, y, walls, blocks, cracks, staircases, gates)) x = newX;
        if (!collides(x, newY, walls, blocks, cracks, staircases, gates)) y = newY;

        // Final check: Is the player actually moving?
        if (speed > 0) moving = true;

        // Animation Logic
        if (moving) {
            animationTimer++;
            int sprintFactor = sprinting ? 2 : 1; // Sprinting speeds up animation
            if (animationTimer >= animationSpeed / sprintFactor) {
                animationIndex = (animationIndex + 1) % animationFrames.length;
                animationTimer = 0;
            }
        } else {
            animationIndex = 0; // Reset animation when not moving
        }
    }

    public void draw(Graphics g, Image crossImage, Image keyImage) {
        g.setColor(Color.BLUE);
        g.fillRect(x,y,40,40);
        if (!rightOrLeft) {
            g.drawImage( (animationFrames[animationIndex]), x - 25, y + sizeY - 90, sizeX + 40, sizeY + 40, null);
        } else {
            g.drawImage( (animationFrames[animationIndex]), (x - 25) + (sizeY + 40), y + sizeY - 90, -(sizeX + 40), sizeY + 40, null);
        }

        if (Config.hasAbility) {
            g.drawImage(crossImage, x - 25, y - 25, 40,40,null);
        }

        if (Config.hasKey) {
            g.drawImage(keyImage, x + 25, y - 25, 40,40,null);
        }
    }

    private boolean collides(int newX, int newY, ArrayList<Wall> walls, ArrayList<Block> blocks, ArrayList<Crack> cracks, ArrayList<Staircase> staircases, ArrayList<Gate> gates) {
        for (Wall wall : walls) {
            if (newX < wall.x + wall.width && newX + sizeX > wall.x &&
                    newY < wall.y + wall.height && newY + sizeY > wall.y) {
                return false;
            }
        }

        for (Crack crack : cracks) {
            if ((newX < crack.x + crack.width && newX + sizeX > crack.x &&
                    newY < crack.y + crack.height && newY + sizeY > crack.y) && !crack.isFixed) {
                return true;
            }
        }

        for (Staircase staircase : staircases) {
            if (newX < staircase.x + staircase.width && newX + sizeX > staircase.x &&
                    newY < staircase.y + staircase.height && newY + sizeY > staircase.y) {
                Config.isInStaircase = true;
                return false;
            }
        }

        for (Block block : blocks) {
            if (block.collides(newX, newY, sizeX, sizeY)) {
                block.push(newX - x, newY - y, walls, blocks, cracks);
                return true;
            }
        }

        for (Gate gate : gates) {
            if ((newX < gate.x + gate.width && newX + sizeX > gate.x &&
                    newY < gate.y + gate.height && newY + sizeY > gate.y) && !Config.hasKey) {
                return true;
            } else if (((newX < gate.x + gate.width && newX + sizeX > gate.x &&
                    newY < gate.y + gate.height && newY + sizeY > gate.y) && Config.hasKey)) {
                Config.endGame = true;
                return false;
            }
        }

        if (Config.gateKey != null && (newX < Config.gateKey.x + Config.gateKey.width && newX + sizeX > Config.gateKey.x &&
                newY < Config.gateKey.y + Config.gateKey.height && newY + sizeY > Config.gateKey.y)) {
            Config.gateKey.x = 10000;
            Config.hasKey = true;
            Config.startEndStair.x = 7650;
            Config.startEndStair.y = 200;
            return false;
        }

        return false;
    }
}

class NPC {
    private int x, y, size = 100;
    private boolean playerNearby = false;
    public boolean inDialogue = false;
    public String dialogue = "Hello Zelda, I am giving you this cross. This cross will scare the evil away. Be careful the evil always finds its way to hunt you down...";
    private Player player;
    private boolean bypass;

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

    public void interact(String message, boolean bypass) {
        this.bypass = bypass;
        if (playerNearby || bypass) {
            dialogue = message;
            inDialogue = true;
        }
    }

    public void exitDialogue() {
        inDialogue = false;
    }

    public void draw(Graphics g, Image npcImage) {
        // Draw NPC
        g.drawImage(npcImage, x, y, size, size, null);

        // Draw "!" above NPC if player is nearby
        if (playerNearby && !inDialogue) {
            g.setColor(Color.YELLOW);
            g.drawString("!",x + size / 3, y - 20);
            g.drawString("Press F to Interact!",x + size / 3, y - 50);
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

            if (!playerNearby) {
                g.setColor(Color.BLACK);
                g.fillRect(x, y, boxWidth, boxHeight);
                g.setColor(Color.WHITE);
                g.drawRect(x, y, boxWidth, boxHeight);
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(x, y, boxWidth, boxHeight);
                g.setColor(Color.WHITE);
                g.drawRect(x, y, boxWidth, boxHeight);
            }

            // Draw text inside the box
            int textX = x + 10;
            int textY = y + 25;
            for (String line : lines) {
                g.drawString(line, textX, textY);
                textY += fm.getHeight();
            }

            if (!Config.hasAbility && !bypass) {
                Config.hasAbility = true;
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

    private boolean moving = false;

    private int animationIndex = 0;
    private int animationTimer = 0;
    private int animationSpeed = 10;
    private Image[] animationFrames = {
            new ImageIcon(getClass().getResource("/game/images/enemy/enemy.png")).getImage(),
            new ImageIcon(getClass().getResource("/game/images/enemy/walk1m1.png")).getImage(),
            new ImageIcon(getClass().getResource("/game/images/enemy/walk2m1.png")).getImage()
    }; // 3 frames

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

            // Final check: Is the enemy actually moving?
            if (speed > 0) moving = true;

            // Animation Logic
            if (moving) {
                animationTimer++;
                if (animationTimer >= animationSpeed) {
                    animationIndex = (animationIndex + 1) % animationFrames.length;
                    animationTimer = 0;
                }
            } else {
                animationIndex = 0; // Reset animation when not moving
            }
        }

        if (collidesWithPlayer(player)) {
            System.out.println("Game Over! Enemy caught the player.");
            GameHandler.running = false;
        }
    }


    private boolean collidesWithPlayer(Player player) {
        return x < player.x + player.sizeX && x + width > player.x &&
                y < player.y + player.sizeY && y + height > player.y;
    }



    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);

        g.drawImage( (animationFrames[animationIndex]), x - 25, y + height - 90, width + 60, height + 60, null);

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

    public boolean collides(int newX, int newY, int sizeX, int sizeY) {
        return newX < x + width && newX + sizeX > x && newY < y + height && newY + sizeY > y;
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
            if (newX < crack.x + crack.width && newX + width > crack.x && newY < crack.y + crack.height && newY + height > crack.y && !crack.isFixed) {
                return;
            }
        }

        for (Block block : blocks) {
            if (block != this && block.collides(newX, newY, width, height)) {
                block.push(dx, dy, walls, blocks, cracks);
                return;
            }
        }

        x = newX;
        y = newY;
    }

    public void draw(Graphics g, int x, int y, int width, int height) {
        Image blockImage;

        URL blockURL = getClass().getResource("/game/images/words/"+ label +".png");

        if (blockURL != null) {
            blockImage = new ImageIcon(blockURL).getImage();

            g.drawImage(blockImage, x, y, width, height, null);
        } else {
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

    public void draw(Graphics g, Image crackImage) {
        g.setColor(isFixed ? Color.LIGHT_GRAY : Color.DARK_GRAY);
        g.drawImage(crackImage, x, y, width, height, null);
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

class Gate {
    int x, y, width, height;
    public Gate(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}

class Key {
    int x, y, width, height;
    public Key(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}

class ScreenTransition {
    private float alpha = 0.0f;  // Transparency (0 = no effect, 1 = full black)
    private boolean fadingOut = true;  // Start with fade-out
    public boolean transitionActive = false;  // Is the transition running?
    private int transitionStep = 0;  // Controls each phase of transition

    public void startTransition() {
        transitionActive = true;
        fadingOut = true;
        alpha = 0.0f;
        transitionStep = 0;
    }

    public void update() {
        if (!transitionActive) return;  // If not active, do nothing

        if (fadingOut) {
            alpha += 0.05f;  // Increase alpha (fade to black)
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                transitionStep++;
                if (transitionStep > 20) {  // Short black screen pause
                    fadingOut = false;  // Switch to fade-in
                }
            }
        } else {
            alpha -= 0.05f;  // Decrease alpha (fade back to normal)
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                transitionActive = false;  // Transition complete
            }
        }
    }

    public void render(Graphics2D g) {
        if (!transitionActive) return;  // If not active, do nothing

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 800, 600);  // Adjust to screen size
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));  // Reset transparency
    }
}

class Buttons {
    int x, y, width, height;
    Color color;
    String label;

    public Buttons(int x, int y, int width, int height, Color color, String label) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.label = label;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);

        // Draw Label
        g.setColor(Color.BLACK);
        g.drawString(label, x + width / 4, y + height / 2);
    }

    public boolean isClicked(int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height);
    }
}

class SoundPlayer {
    private Clip clip;

    // Constructor: Load the sound file when creating a SoundPlayer object
    public SoundPlayer(String filePath) {
        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.out.println("Sound file not found: " + filePath);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to play the sound
    public void play() {
        if (clip != null) {
            clip.setFramePosition(0); // Rewind to start
            clip.start();
        }
    }

    // Method to stop the sound
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}

class FullScreenBlinkingLight {
    private boolean lightOn = true;  // Whether the screen-wide light is on or off
    private long lastBlinkTime = 0;  // Last time the light blinked
    private long blinkInterval;  // Interval for blinking (randomized)
    private final long MIN_BLINK_INTERVAL = 500;  // Minimum blink interval in milliseconds
    private final long MAX_BLINK_INTERVAL = 1500;  // Maximum blink interval in milliseconds
    private Random rand = new Random();

    // Constructor
    public FullScreenBlinkingLight() {
        // Set a random blink interval between min and max
        this.blinkInterval = rand.nextInt((int) (MAX_BLINK_INTERVAL - MIN_BLINK_INTERVAL)) + (int) MIN_BLINK_INTERVAL;
    }

    // Update the blinking state based on time
    public void update() {
        long currentTime = System.currentTimeMillis();

        // Check if enough time has passed to toggle the light state
        if (currentTime - lastBlinkTime > blinkInterval) {
            lightOn = !lightOn;  // Toggle the light's state (on/off)
            lastBlinkTime = currentTime;  // Update the last blink time

            // Randomize the blink interval for unpredictability
            blinkInterval = rand.nextInt((int) (MAX_BLINK_INTERVAL - MIN_BLINK_INTERVAL)) + (int) MIN_BLINK_INTERVAL;
        }
    }

    // Draw the full-screen blinking effect
    public void draw(Graphics g, int x, int y) {
        int camX = 800 / 2 - x;
        int camY = 600 / 2 - y;

        if (lightOn) {
            // Draw a bright overlay (light on)
            g.setColor(new Color(0, 0, 0, 200));  // Semi-transparent white overlay
            g.fillRect(-camX, -camY, g.getClipBounds().width + 5000, g.getClipBounds().height + 5000);
        } else {
            // Optionally, you can draw a dimmed or transparent overlay (light off)
            g.setColor(new Color(0, 0, 0, 100));  // Fully transparent overlay
            g.fillRect(-camX - 5000, -camY - 5000, g.getClipBounds().width + 5000, g.getClipBounds().height + 5000);
        }
    }
}

