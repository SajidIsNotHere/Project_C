package game.world;

import java.awt.*;

public class Tile {
    public static final int TILE_SIZE = 50;
    private int x, y;
    private boolean isSolid;

    public Tile(int x, int y, boolean isSolid) {
        this.x = x;
        this.y = y;
        this.isSolid = isSolid;
    }

    public void render(Graphics g) {
        if (isSolid) {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        }
    }

    public boolean isSolid() {
        return isSolid;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
    }
}
