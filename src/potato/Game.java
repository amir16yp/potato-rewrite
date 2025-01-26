package potato;

import potato.entities.PlayerEntity;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Game extends JFrame {


    static {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");
    }

    public static final int INTERNAL_WIDTH = ConfigManager.get().getInt(GameProperty.INTERNAL_WIDTH);
    public static final int INTERNAL_HEIGHT = ConfigManager.get().getInt(GameProperty.INTERNAL_HEIGHT);
    public static Game GAME;
    public static GameLoop GAMELOOP;
    public static Renderer RENDERER;
    public static Raycaster RAYCASTER;
    public static boolean DEV_MODE_ENABLED;
    public static final LevelGenerator LEVEL_GENERATOR = new LevelGenerator();
    private static final Logger logger = new Logger(Game.class.getName());
    public static final SoundManager SOUND_MANAGER = new SoundManager();
    private final Set<Integer> pressedKeys = new HashSet<>();
    private static final Textures DEFAULT_TEXTURES = new Textures("/potato/assets/sprites/textures.png", 16, 16);

    public Game() {
        GAME = this;
        Wall.setDefaultTextures(DEFAULT_TEXTURES);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Potato");
        setIconImage(Utils.loadImage("/potato/assets/sprites/icon.png"));
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

                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (!RENDERER.isPaused())
                    {
                        RENDERER.setPaused(true);
                    }
                }

                if (RENDERER.isPaused())
                {
                    return;
                }

                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    PlayerEntity player = PlayerEntity.getPlayer();
                    player.getInventory().getCurrentWeapon().fire(player.getX(), player.getY(), player.getAngle());
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
        if (RENDERER.isPaused())
        {
            return false;
        }
        return pressedKeys.contains(keycode);
    }

    public static void main(String[] args) {
        for (String arg : args)
        {
            if (arg.equals("--iamsolame"))
            {
                DEV_MODE_ENABLED = true;
            }
        }
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