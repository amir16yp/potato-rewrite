package potato;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.CopyOnWriteArrayList;
import potato.entities.Entity;
import potato.entities.PlayerEntity;

public class Level {
    private Wall[][] map;
    private final int mapWidth;
    private final int mapHeight;
    private final CopyOnWriteArrayList<Entity> entities;
    private Textures textures;
    public BufferedImage floorTexture;
    public BufferedImage ceilingTexture;
    public Color floorColor;
    public Color ceilingColor;
    private final String name;
    private final SaveSystem levelSave;
    protected static Logger logger;

    public Level(Wall[][] map, String name) {
        logger = new Logger(this.getClass().getName());
        logger.addPrefix(name);
        this.map = map;
        this.mapWidth = map[0].length;
        this.mapHeight = map.length;
        this.entities = new CopyOnWriteArrayList<>();
        this.textures = new Textures("/potato/assets/sprites/textures.png", 16, 16);
        this.name = name;
        this.levelSave = new SaveSystem(name + ".save");

        // Default colors if textures aren't loaded
        this.floorColor = new Color(100, 100, 100);
        this.ceilingColor = new Color(50, 50, 50);
    }

    public void update() {
        entities.removeIf(Entity::isDead);
        entities.forEach(Entity::update);
        for (Wall[] wallRow : map)
        {
            for (Wall wall : wallRow)
            {
                if (wall != null)
                {
                    wall.update();
                }
            }
        }
    }

    public boolean isWall(double x, double y) {
        int mapX = (int) x;
        int mapY = (int) y;

        if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
            return true;
        }

        Wall wall = getWall(mapX, mapY);
        if (wall == null) {
            return false;
        }

        // If it's a door, check if it's passable
        if (wall instanceof Door) {
            return wall.getCurrentTexture() != null;
        }

        return true;
    }

    public Wall getWall(int x, int y) {
        return map[y][x];
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
    public Wall[][] getMap() { return map; }
    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public BufferedImage getTexture(int id) { return textures.getTile(id); }
    public int getWallType(int x, int y) {
        Wall wall = map[y][x];
        return wall != null ? wall.getType() : 0;  // Return 0 for empty space
    }
    public CopyOnWriteArrayList<Entity> getEntities() { return entities; }
    public String getName() { return name; }
}
