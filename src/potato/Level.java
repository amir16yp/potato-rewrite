package potato;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Level {
    private final int[][] map;
    private final int mapWidth;
    private final int mapHeight;
    private final PlayerEntity player;
    private final CopyOnWriteArrayList<Entity> entities;
    private Textures textures;
    public BufferedImage floorTexture;
    public BufferedImage ceilingTexture;
    public Color floorColor;
    public Color ceilingColor;

    /*
    TODO: Add load from file and save to file using SaveSystem
    TODO: Add dispose methods to avoid memory leaks when switching levels
     */

    public Level(int[][] map) {
        this.map = map;
        this.mapWidth = map[0].length;
        this.mapHeight = map.length;
        this.entities = new CopyOnWriteArrayList<>();
        this.player = PlayerEntity.getPlayer();
        this.textures = new Textures("/potato/sprites/textures.png", 16, 16);
       // this.floorTexture = textures.getTile(17);
        //this.ceilingTexture = textures.getTile(13);
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

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    // Getters for necessary fields
    public int[][] getMap() { return map; }
    public int getMapWidth() { return mapWidth; }
    public int getMapHeight() { return mapHeight; }
    public PlayerEntity getPlayer() { return player; }
    public BufferedImage getTexture(int id) { return textures.getTile(id); }
    public int getWallType(int x, int y) { return map[y][x]; }

    public CopyOnWriteArrayList<Entity> getEntities() {
        return entities;
    }

}