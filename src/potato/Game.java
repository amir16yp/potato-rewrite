package potato;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Game extends JFrame {
    public static final int INTERNAL_WIDTH = 256;
    public static final int INTERNAL_HEIGHT = 240;
    public static Game GAME;
    public static GameLoop GAMELOOP;
    public static Renderer RENDERER;
    public static Raycaster RAYCASTER;

    private static Logger logger = new Logger(Game.class.getName());
    public static final SoundManager SOUND_MANAGER = new SoundManager();
    private final Set<Integer> pressedKeys = new HashSet<>();


    public Game() {
        GAME = this;

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Potato");
        setIconImage(Utils.loadImage("/potato/sprites/icon.png"));
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
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    PlayerEntity player = PlayerEntity.getPlayer();
                    player.getInventory().getCurrentWeapon().fire(player.getX(), player.getY(), player.getAngle());
                }

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    RENDERER.setPaused(!RENDERER.isPaused());
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

        try {
            SaveSystem.SETTINGS_SAVE.load();
        } catch (IOException e) {
            logger.error(e);
        }
        SwingUtilities.invokeLater(() -> {

            GAMELOOP = new GameLoop();
            RENDERER = new Renderer();
            Game game = new Game();
            game.setVisible(true);

            GAMELOOP.start();
        });
    }
}