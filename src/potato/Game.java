package potato;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class Game extends JFrame {
    public static final int INTERNAL_WIDTH = 640;
    public static final int INTERNAL_HEIGHT = 480;
    public static Game GAME;
    public static GameLoop GAMELOOP;
    public static Renderer RENDERER;
    public Raycaster RAYCASTER;

    private final Set<Integer> pressedKeys = new HashSet<>();


    public Game() {
        GAME = this;

        // Window setup
        setTitle("Raycaster Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Initialize components
        RENDERER = new Renderer();
        add(RENDERER);
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    PlayerEntity player = PlayerEntity.getPlayer();
                    player.getCurrentWeapon().fire(player.getX(), player.getY(), player.getAngle());
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });
        RAYCASTER = new Raycaster();

        // Initialize and start game loop
        GAMELOOP = new GameLoop();

        // Pack and center window
        pack();

        setLocationRelativeTo(null);
        requestFocus();
        setFocusable(true);
    }

    public boolean isKeyPressed(int keycode) {
        return pressedKeys.contains(keycode);
    }

    public static void main(String[] args) {
        // Use Event Dispatch Thread for Swing components
        SwingUtilities.invokeLater(() -> {
            GAMELOOP = new GameLoop();
            RENDERER = new Renderer();
            Game game = new Game();
            game.setVisible(true);

            GAMELOOP.start();
        });
    }
}