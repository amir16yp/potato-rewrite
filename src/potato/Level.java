package potato;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

public class Level {
    private final int[][] map;
    private final int mapWidth;
    private final int mapHeight;
    private final PlayerEntity player;
    private final CopyOnWriteArrayList<Entity> entities;
    private final Textures textures;

    public Level(int[][] map) {
        this.map = map;
        this.mapWidth = map[0].length;
        this.mapHeight = map.length;
        this.entities = new CopyOnWriteArrayList<>();
        this.player = PlayerEntity.getPlayer();
        this.textures = new Textures("/potato/sprites/textures.png", 16, 16);
    }

    public void render(Graphics2D graphics2D) {
        // Draw ceiling
        graphics2D.setColor(new Color(100, 100, 200));
        graphics2D.fillRect(0, 0, Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT / 2);

        // Draw floor
        graphics2D.setColor(new Color(100, 100, 100));
        graphics2D.fillRect(0, Game.INTERNAL_HEIGHT / 2, Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT / 2);

        // Render walls using raycaster
        //raycaster.renderWalls(graphics2D);

        // Sort entities by distance (furthest first)
        ArrayList<Entity> sortedEntities = new ArrayList<>(entities);
        sortedEntities.sort((e1, e2) -> {
            double dist1 = Math.pow(e1.getX() - player.getX(), 2) + Math.pow(e1.getY() - player.getY(), 2);
            double dist2 = Math.pow(e2.getX() - player.getX(), 2) + Math.pow(e2.getY() - player.getY(), 2);
            return Double.compare(dist2, dist1);
        });

        // Render all entities except player
        for (Entity entity : sortedEntities) {
            if (!(entity instanceof PlayerEntity)) {
                entity.render(graphics2D);
            }
        }
    }

    public void update() {
        player.update(Game.GAMELOOP.getDeltaTime());
        entities.removeIf(Entity::isDead);
        entities.forEach(entity -> entity.update(Game.GAMELOOP.getDeltaTime()));
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