package game.physics;

import game.entities.Player;

import javax.swing.*;
import java.awt.*;

public class CollisionHandler {
    public static void preventCollision(JFrame frame, Player player) {
        Rectangle playerBounds = player.getPanel().getBounds();
        Rectangle screenBounds = frame.getContentPane().getBounds();

        int newX = playerBounds.x;
        int newY = playerBounds.y;

        if (playerBounds.x < screenBounds.x) newX = screenBounds.x;
        if (playerBounds.y < screenBounds.y) newY = screenBounds.y;
        if (playerBounds.x + playerBounds.width > screenBounds.x + screenBounds.width)
            newX = screenBounds.x + screenBounds.width - playerBounds.width;
        if (playerBounds.y + playerBounds.height > screenBounds.y + screenBounds.height)
            newY = screenBounds.y + screenBounds.height - playerBounds.height;

        player.getPanel().setLocation(newX, newY);
    }
}
