package game;

import game.entities.Player;

import javax.swing.*;

public class Renderer {
    private JPanel SCREEN;
    private Player player;

    public Renderer(GameWindow window) {
        this.SCREEN = window.getScreen();
        this.player = new Player(window.getPlayerPanel());
    }

    public Player getPlayer() {
        return player;
    }
}
