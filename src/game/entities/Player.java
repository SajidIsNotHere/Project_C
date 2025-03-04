package game.entities;

import javax.swing.*;

public class Player {
    private static final int PLAYER_SIZE = 50;
    private JPanel PLAYER;
    private int x, y;

    public Player(JPanel playerPanel) {
        this.PLAYER = playerPanel;
        this.x = 0;
        this.y = 0;
    }

    public void move(int dx, int dy, int screenWidth, int screenHeight) {
        x = Math.max(0, Math.min(screenWidth - PLAYER_SIZE, x + dx));
        y = Math.max(0, Math.min(screenHeight - PLAYER_SIZE, y + dy));
        PLAYER.setLocation(x, y);
    }

    public JPanel getPanel() {
        return PLAYER;
    }
}
