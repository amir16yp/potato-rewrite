package potato;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import potato.entities.Entity;
import potato.entities.PlayerEntity;

public class Level {
    private int[][] map;
    private final int mapWidth;
    private final int mapHeight;
    private final PlayerEntity player;
    private final CopyOnWriteArrayList<Entity> entities;
    private Textures textures;
    public BufferedImage floorTexture;
    public BufferedImage ceilingTexture;
    public Color floorColor;
    public Color ceilingColor;
    private final String name;
    private final SaveSystem levelSave;
    protected static Logger logger;

    public Level(int[][] map, String name) {
        logger = new Logger(this.getClass().getName());
        logger.addPrefix(name);
        this.map = map;
        this.mapWidth = map[0].length;
        this.mapHeight = map.length;
        this.entities = new CopyOnWriteArrayList<>();
        this.player = PlayerEntity.getPlayer();
        this.textures = new Textures("/potato/sprites/textures.png", 16, 16);
        this.name = name;
        this.levelSave = new SaveSystem(name + ".save");

        // Default colors if textures aren't loaded
        this.floorColor = new Color(100, 100, 100);
        this.ceilingColor = new Color(50, 50, 50);
    }

    public void update() {
        player.update();
        entities.removeIf(Entity::isDead);
        entities.forEach(Entity::update);
    }

    public boolean isWall(double x, double y) {
        int mapX = (int) x;
        int mapY = (int) y;

        if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
            return true;
        }

        return map[mapY][mapX] > 0;
    }

    private int[] flattenMap() {
        int[] flat = new int[mapHeight * mapWidth];
        for (int y = 0; y < mapHeight; y++) {
            System.arraycopy(map[y], 0, flat, y * mapWidth, mapWidth);
        }
        return flat;
    }

    private static int[][] unflattenMap(int[] flat, int width, int height) {
        int[][] map = new int[height][width];
        for (int y = 0; y < height; y++) {
            System.arraycopy(flat, y * width, map[y], 0, width);
        }
        return map;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void dispose() {
        textures = null;
        floorTexture = null;
        ceilingTexture = null;
        entities.clear();
        levelSave.clear();
    }

    // Getters
    public int[][] getMap() { return map; }
    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public PlayerEntity getPlayer() { return player; }
    public BufferedImage getTexture(int id) { return textures.getTile(id); }
    public int getWallType(int x, int y) { return map[y][x]; }
    public CopyOnWriteArrayList<Entity> getEntities() { return entities; }
    public String getName() { return name; }
}
