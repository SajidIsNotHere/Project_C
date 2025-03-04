package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameHandler
{
    // Player Size
    private static final int PLAYER_SIZE = 50;

    // Movement direction flags
    private static boolean moveUp = false, moveDown = false, moveLeft = false, moveRight = false;
    private static boolean lockUp = false, lockDown = false, lockLeft = false, lockRight = false;

    // Player Position
    private static int x = 0, y = 0; // Initial position of the panel

    // Setup the whole game
    public static void Initiate(JFrame frame, JPanel PLAYER, JPanel SCREEN)
    {
        Listeners(frame);
        new Timer(16, e -> Loop(frame, PLAYER, SCREEN)).start();
    }

    // Setup Loops
    private static void Loop(JFrame frame, JPanel PLAYER, JPanel SCREEN)
    {
        move(PLAYER, SCREEN);
        preventCollision(frame, PLAYER);
    }

    // Setup Listeners
    private static void Listeners(JFrame frame)
    {
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                println("Debug");
            }
        });

        // Add a KeyListener to detect WASD keys
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_W: // Move up
                        moveUp = true;
                        break;
                    case KeyEvent.VK_S: // Move down
                        moveDown = true;
                        break;
                    case KeyEvent.VK_A: // Move left
                        moveLeft = true;
                        break;
                    case KeyEvent.VK_D: // Move right
                        moveRight = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                switch (key) {
                    case KeyEvent.VK_W: // Stop moving up
                        moveUp = false;
                        break;
                    case KeyEvent.VK_S: // Stop moving down
                        moveDown = false;
                        break;
                    case KeyEvent.VK_A: // Stop moving left
                        moveLeft = false;
                        break;
                    case KeyEvent.VK_D: // Stop moving right
                        moveRight = false;
                        break;
                }
            }
        });
    }

    private static void preventCollision(JFrame frame, JPanel PLAYER)
    {
        Rectangle playerBounds = PLAYER.getBounds();
        Rectangle screenBounds = frame.getContentPane().getBounds();

        int playerRight = playerBounds.x + playerBounds.width;
        int playerBottom = playerBounds.y + playerBounds.height;
        int screenRight = screenBounds.x + screenBounds.width;
        int screenBottom = screenBounds.y + screenBounds.height;

        // Check and lock movements when hitting the edges
        if (playerBounds.x <= screenBounds.x) {
            moveLeft = false;
            lockLeft = true;
        } else if (lockLeft) {
            lockLeft = false;
        }

        if (playerBounds.y <= screenBounds.y) {
            moveUp = false;
            lockUp = true;
        } else if (lockUp) {
            lockUp = false;
        }

        if (playerRight >= screenRight) {
            moveRight = false;
            lockRight = true;
        } else if (lockRight) {
            lockRight = false;
        }

        if (playerBottom >= screenBottom) {
            moveDown = false;
            lockDown = true;
        } else if (lockDown) {
            lockDown = false;
        }

        // Compute new player position
        int newX = playerBounds.x + (moveRight ? 1 : moveLeft ? -1 : 0);
        int newY = playerBounds.y + (moveDown ? 1 : moveUp ? -1 : 0);

        // Update player position only once
        PLAYER.setLocation(newX, newY);
    }


    // Function that updates the panel's position based on the direction flags
    private static void move(JPanel PLAYER, JPanel SCREEN) {
        if (moveUp) y -= 5;
        if (moveDown) y += 5;
        if (moveLeft) x -= 5;
        if (moveRight) x += 5;

        // Keep the player within the screen bounds
        x = Math.max(0, Math.min(SCREEN.getWidth() - PLAYER_SIZE, x));
        y = Math.max(0, Math.min(SCREEN.getHeight() - PLAYER_SIZE, y));

        PLAYER.setLocation(x, y);
    }

    private static void println(String x)
    {
        System.out.println(x);
    }

//    private void centerPlayer() {
//        int screenWidth = getContentPane().getWidth();  // Get actual window content size
//        int screenHeight = getContentPane().getHeight();
//
//        if (screenWidth > 0 && screenHeight > 0) { // Ensure valid dimensions
//            int newX = (screenWidth - PLAYER_SIZE) / 2;
//            int newY = (screenHeight - PLAYER_SIZE) / 2;
//            PLAYER.setLocation(newX, newY);
//
//            SCREEN.revalidate();
//            SCREEN.repaint();
//            System.out.println("Player Centered at: " + PLAYER.getLocation());
//        }
//    }
}
