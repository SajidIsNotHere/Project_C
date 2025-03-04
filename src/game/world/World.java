package game.world;

import game.entities.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class World extends JPanel {
    private TileMap tileMap;
    private Player player;

    public World(Player player) {
        this.player = player;
        this.tileMap = new TileMap();
        setBounds(0, 0, 800, 600);
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        tileMap.render(g);
    }

    public void checkCollision() {
        List<Tile> tiles = tileMap.getTiles();
        Rectangle playerBounds = player.getPanel().getBounds();

        for (Tile tile : tiles) {
            if (tile.isSolid() && playerBounds.intersects(tile.getBounds())) {
                // Simple collision resolution: Prevent movement into the tile
                int dx = playerBounds.x - tile.getBounds().x;
                int dy = playerBounds.y - tile.getBounds().y;

                if (Math.abs(dx) > Math.abs(dy)) {
                    if (dx > 0) playerBounds.x = tile.getBounds().x + Tile.TILE_SIZE; // Right
                    else playerBounds.x = tile.getBounds().x - playerBounds.width; // Left
                } else {
                    if (dy > 0) playerBounds.y = tile.getBounds().y + Tile.TILE_SIZE; // Down
                    else playerBounds.y = tile.getBounds().y - playerBounds.height; // Up
                }
                player.getPanel().setLocation(playerBounds.x, playerBounds.y);
            }
        }
    }
}
