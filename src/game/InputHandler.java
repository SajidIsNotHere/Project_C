package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {
    private boolean moveUp, moveDown, moveLeft, moveRight;

    public InputHandler() {
        moveUp = moveDown = moveLeft = moveRight = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> moveUp = true;
            case KeyEvent.VK_S -> moveDown = true;
            case KeyEvent.VK_A -> moveLeft = true;
            case KeyEvent.VK_D -> moveRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W -> moveUp = false;
            case KeyEvent.VK_S -> moveDown = false;
            case KeyEvent.VK_A -> moveLeft = false;
            case KeyEvent.VK_D -> moveRight = false;
        }
    }

    public boolean isMoveUp() { return moveUp; }
    public boolean isMoveDown() { return moveDown; }
    public boolean isMoveLeft() { return moveLeft; }
    public boolean isMoveRight() { return moveRight; }
}
