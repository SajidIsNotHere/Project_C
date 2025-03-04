package game.world;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TileMap {
    private List<Tile> tiles;

    public TileMap() {
        tiles = new ArrayList<>();
        generateWalls();
    }

    private void generateWalls() {
        // Example: Creating a simple wall at the top of the screen
        for (int i = 0; i < 16; i++) {
            tiles.add(new Tile(i * Tile.TILE_SIZE, 0, true));  // Wall tiles
        }
    }

    public void render(Graphics g) {
        for (Tile tile : tiles) {
            tile.render(g);
        }
    }

    public List<Tile> getTiles() {
        return tiles;
    }
}
