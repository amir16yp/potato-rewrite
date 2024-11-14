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

    public void save() throws IOException {
        logger.log("Saving level: " + name);

        try {
            levelSave.setIntArray("map", flattenMap());
            logger.log("Map data saved.");

            levelSave.setInt("width", mapWidth);
            levelSave.setInt("height", mapHeight);
            logger.log("Level dimensions saved: Width = " + mapWidth + ", Height = " + mapHeight);

            saveEntities();
            levelSave.save();
            logger.log("Entities and level data successfully saved.");
        } catch (IOException e) {
            logger.log("Error while saving level: " + e.getMessage());
            throw e;  // Rethrow after logging
        }
    }

    public void load(String name) throws IOException {
        logger.log("Loading level: " + name);

        if (!name.endsWith(".save")) {
            name += ".save";
        }

        SaveSystem save = new SaveSystem(name);
        try {
            save.load();
            logger.log("Save data loaded successfully.");
        } catch (IOException e) {
            logger.log("Error loading save data for level: " + name);
            throw new IOException("Level not found: " + name, e);
        }

        int width = save.getInt("width", 0);
        int height = save.getInt("height", 0);
        if (width == 0 || height == 0) {
            logger.log("Invalid level data found: Missing dimensions.");
            throw new IOException("Level not found: " + name);
        }

        int[] flatMap = save.getIntArray("map", new int[0]);
        this.map = unflattenMap(flatMap, width, height);
        logger.log("Level map loaded. Dimensions: " + width + "x" + height);
        this.loadEntities();
        logger.log("Entities loaded.");

    }

    private void saveEntities() {
        logger.log("Saving entities...");

        try {
            levelSave.setDouble("player_x", player.getX());
            levelSave.setDouble("player_y", player.getY());
            levelSave.setDouble("player_a", player.getAngle());
            logger.log("Player position and angle saved.");

            // Save entity list size
            levelSave.setInt("entity_count", entities.size());
            logger.log("Entity count saved: " + entities.size());

            // Save each entity
            int index = 0;
            for (Entity entity : entities) {
                String prefix = "entity_" + index + "_";
                levelSave.setString(prefix + "type", entity.getClass().getName());
                levelSave.setDouble(prefix + "x", entity.getX());
                levelSave.setDouble(prefix + "y", entity.getY());
                logger.log("Entity " + index + " of type " + entity.getClass().getName() + " saved at position (" + entity.getX() + ", " + entity.getY() + ")");
                index++;
            }
        } catch (Exception e) {
            logger.log("Error saving entities: " + e.getMessage());
        }
    }

    private void loadEntities() {
        logger.log("Loading entities...");

        try {
            player.setPosition(
                    levelSave.getDouble("player_x", 1.5),
                    levelSave.getDouble("player_y", 1.5)
            );
            player.setAngle(levelSave.getDouble("player_a", 0));
            logger.log(String.format("Player position and angle loaded.%.2f.%.2f-%.2f", player.getX(), player.getY(), player.getAngle()));

            // Load entities
            int entityCount = levelSave.getInt("entity_count", 0);
            logger.log("Entity count loaded: " + entityCount);

            for (int i = 0; i < entityCount; i++) {
                String prefix = "entity_" + i + "_";
                try {
                    String type = levelSave.getString(prefix + "type", "");
                    double x = levelSave.getDouble(prefix + "x", 0);
                    double y = levelSave.getDouble(prefix + "y", 0);
                    Class<?> entityClass = Class.forName(type);
                    Entity entity = (Entity) entityClass.getDeclaredConstructor(double.class, double.class)
                            .newInstance(x, y);
                    entities.add(entity);
                    logger.log("Entity " + i + " of type " + type + " loaded at position (" + x + ", " + y + ")");
                } catch (Exception e) {
                    logger.log("Error loading entity " + i + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.log("Error while loading entities: " + e.getMessage());
        }
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
