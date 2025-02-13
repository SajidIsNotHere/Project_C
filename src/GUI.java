import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GUI extends JFrame {
    private static final int PLAYER_SIZE = 50;
    private static final int SCREEN_SIZE_X = 800, SCREEN_SIZE_Y = 600;
    private int x = 0, y = 0; // Initial position of the panel
    private JPanel PLAYER;
    private JPanel SCREEN;

    // Movement direction flags
    private boolean moveUp = false, moveDown = false, moveLeft = false, moveRight = false;

    public GUI() {
        // Setup the JFrame
        setContentPane(SCREEN);
        setTitle("WASD Movement Example");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create the panel that will move
        SCREEN.setBackground(Color.BLUE);
        SCREEN.setSize(SCREEN_SIZE_X, SCREEN_SIZE_Y);
        SCREEN.setLocation(x, y); // Set initial position of the panel

        PLAYER.setBackground(Color.RED);
        PLAYER.setSize(PLAYER_SIZE, PLAYER_SIZE);
        PLAYER.setLocation(x, y); // Set initial position of the panel

        // Setup the layout of the frame
        setLayout(null); // Use absolute positioning
        add(PLAYER);

        // Add a KeyListener to detect WASD keys
        addKeyListener(new KeyAdapter() {
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

        setFocusable(true); // Make sure the JFrame can receive key events

        // Create a Timer that calls the move() method every 16 ms (~60 FPS)
        new Timer(16, e -> loop()).start();
    }

    private void loop()
    {
        move();
        updateBackgroundColor();
    }

    private void updateBackgroundColor()
    {

    }

    // Function that updates the panel's position based on the direction flags
    private void move() {
        if (moveUp) y -= 5; // Move up
        if (moveDown) y += 5; // Move down
        if (moveLeft) x -= 5; // Move left
        if (moveRight) x += 5; // Move right

        // Update the panel's location
        PLAYER.setLocation(x, y);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUI().setVisible(true);
        });
    }
}
