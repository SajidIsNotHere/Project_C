package game;

import game.entities.Player;
import game.world.World;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameHandler {
    private GameWindow window;
    private Renderer renderer;
    private InputHandler inputHandler;
    private Player player;
    private World world;

    public GameHandler() {
        window = new GameWindow();
        renderer = new Renderer(window);
        inputHandler = new InputHandler();
        player = renderer.getPlayer();
        world = new World(player);

        window.add(world);
        window.addKeyListener(inputHandler);

        Timer gameLoop = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                window.repaint();

                // TO-DO
                // fix the line 31 because world.repaint() shows the walls but it glitches the whole rendering.
                // i have to fix ts because this is not it.
                // im guessing because renderer.java is not the mainstream so im fixing this.
            }
        });
        gameLoop.start();
    }

    private void update() {
        int dx = 0, dy = 0;

        if (inputHandler.isMoveUp()) dy -= 5;
        if (inputHandler.isMoveDown()) dy += 5;
        if (inputHandler.isMoveLeft()) dx -= 5;
        if (inputHandler.isMoveRight()) dx += 5;

        player.move(dx, dy, window.getWidth(), window.getHeight());
        world.checkCollision();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameHandler::new);
    }
}
