package game;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    private JPanel SCREEN;
    private JPanel PLAYER;

    public GameWindow() {
        setTitle("Project C");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        setFocusable(true);

        SCREEN.setBackground(Color.BLUE);
        SCREEN.setSize(800, 600);
        SCREEN.setLayout(null);
        add(SCREEN);

        PLAYER.setSize(50, 50);
        PLAYER.setBackground(Color.RED);
        PLAYER.setLocation(0, 0);
        SCREEN.add(PLAYER);

        setVisible(true);
    }

    public JPanel getScreen() {
        return SCREEN;
    }

    public JPanel getPlayerPanel() {
        return PLAYER;
    }
}
