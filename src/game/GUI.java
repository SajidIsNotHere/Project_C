package game;

import game.GameHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GUI extends JFrame
{
    // JComponents
    private JPanel PLAYER;
    private JPanel SCREEN;

    // Sizes of Components
    private static final int PLAYER_SIZE = 50;
    private static final int SCREEN_SIZE_X = 800, SCREEN_SIZE_Y = 600;

    // Main Class
    public GUI()
    {
        // Setup the JFrame
        setTitle("WASD Movement Example");
        setSize(SCREEN_SIZE_X, SCREEN_SIZE_Y);
        setContentPane(SCREEN);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        setLayout(null);
        setFocusable(true); // Make sure the JFrame can receive key events
        setVisible(true);

        // Create the panel that will move
        SCREEN.setBackground(Color.BLUE);
        SCREEN.setSize(SCREEN_SIZE_X, SCREEN_SIZE_Y);
        SCREEN.setLocation(0, 0); // Set initial position of the panel

        PLAYER.setBackground(Color.RED);
        PLAYER.setSize(PLAYER_SIZE, PLAYER_SIZE);
        PLAYER.setLocation(0, 0); // Set initial position of the panel


        GameHandler.Initiate(this, PLAYER, SCREEN);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(GUI::new);
    }
}
