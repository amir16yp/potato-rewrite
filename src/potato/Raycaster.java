package potato;

import java.awt.*;

public class Raycaster {
    public static final double PLANE_DIST = 1.0; // Distance to projection plane
    private static final int FOV = 60; // Field of view in degrees
    private static final int WALL_HEIGHT = Game.INTERNAL_HEIGHT / 2;

    private final int[][] map;
    private final int mapWidth;
    private final int mapHeight;
    private final PlayerEntity player;
    private final java.util.concurrent.CopyOnWriteArrayList<Entity> entities;
    private final Textures textures; // Add textures field

    private class RaycastHit {
        double distance;
        double wallX;
        int textureId;
        boolean side;

        RaycastHit(double distance, double wallX, int textureId, boolean side) {
            this.distance = distance;
            this.wallX = wallX;
            this.textureId = textureId;
            this.side = side;
        }
    }

    public Raycaster(int[][] map) {
        this.map = map;
        this.mapWidth = map[0].length;
        this.mapHeight = map.length;
        this.entities = new java.util.concurrent.CopyOnWriteArrayList<>();
        this.player = PlayerEntity.getPlayer();
        // Initialize textures with 64x64 tile size
        this.textures = new Textures("/potato/sprites/textures.png", 16, 16);
    }

    public void render(Graphics2D graphics2D) {
        // Draw ceiling
        graphics2D.setColor(new Color(100, 100, 200));
        graphics2D.fillRect(0, 0, Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT / 2);

        // Draw floor
        graphics2D.setColor(new Color(100, 100, 100));
        graphics2D.fillRect(0, Game.INTERNAL_HEIGHT / 2, Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT / 2);

        // Cast rays
        double rayAngleStep = Math.toRadians(FOV) / Game.INTERNAL_WIDTH;
        double startAngle = player.getAngle() - Math.toRadians(FOV) / 2;

        for (int x = 0; x < Game.INTERNAL_WIDTH; x++) {
            double rayAngle = startAngle + x * rayAngleStep;
            RaycastHit hit = castRay(rayAngle);

            if (hit.textureId > 0) {
                // Calculate wall height based on distance
                double perpWallDist = hit.distance * Math.cos(rayAngle - player.getAngle());
                int lineHeight = (int) (WALL_HEIGHT / perpWallDist);

                // Calculate drawing bounds
                int drawStart = -lineHeight / 2 + Game.INTERNAL_HEIGHT / 2;
                if (drawStart < 0) drawStart = 0;
                int drawEnd = lineHeight / 2 + Game.INTERNAL_HEIGHT / 2;
                if (drawEnd >= Game.INTERNAL_HEIGHT) drawEnd = Game.INTERNAL_HEIGHT - 1;

                // Get the wall texture
                java.awt.image.BufferedImage texture = textures.getTile(hit.textureId);
                if (texture != null) {
                    // Calculate texture X coordinate
                    int texX = (int)(hit.wallX * texture.getWidth());
                    if ((!hit.side && Math.cos(rayAngle) < 0) ||
                            (hit.side && Math.sin(rayAngle) < 0)) {
                        texX = texture.getWidth() - texX - 1;
                    }

                    // Draw the textured wall strip
                    double step = (double) texture.getHeight() / lineHeight;
                    double texPos = (drawStart - Game.INTERNAL_HEIGHT / 2.0 + lineHeight / 2.0) * step;

                    for (int y = drawStart; y < drawEnd; y++) {
                        int texY = (int) texPos & (texture.getHeight() - 1);
                        texPos += step;

                        int color = texture.getRGB(texX, texY);

                        // Apply distance fog
                        float fogFactor = (float) Math.min(1.0, hit.distance / 10.0);
                        color = applyFog(color, fogFactor);

                        graphics2D.setColor(new Color(color));
                        graphics2D.drawLine(x, y, x, y);
                    }
                }
            }
        }
    }


    private int applyFog(int color, float fogFactor) {
        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;

        // Blend with fog color (gray)
        r = (int) (r * (1 - fogFactor) + 128 * fogFactor);
        g = (int) (g * (1 - fogFactor) + 128 * fogFactor);
        b = (int) (b * (1 - fogFactor) + 128 * fogFactor);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    // [Other methods remain the same]
    public boolean isWall(double x, double y) {
        int mapX = (int) x;
        int mapY = (int) y;

        if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
            return true;
        }

        return map[mapY][mapX] > 0;
    }

    public void update() {
        player.update(Game.GAMELOOP.getDeltaTime());
        entities.removeIf(Entity::isDead);
        entities.forEach(entity -> entity.update(Game.GAMELOOP.getDeltaTime()));
    }

    private RaycastHit castRay(double rayAngle) {
        double rayDirX = Math.cos(rayAngle);
        double rayDirY = Math.sin(rayAngle);

        // Current map position
        int mapX = (int) player.getX();
        int mapY = (int) player.getY();

        // Length of ray from current position to next x or y-side
        double sideDistX;
        double sideDistY;

        // Length of ray from one x or y-side to next x or y-side
        double deltaDistX = Math.abs(1 / rayDirX);
        double deltaDistY = Math.abs(1 / rayDirY);

        // What direction to step in x or y-direction (either +1 or -1)
        int stepX;
        int stepY;

        // Calculate step and initial sideDist
        if (rayDirX < 0) {
            stepX = -1;
            sideDistX = (player.getX() - mapX) * deltaDistX;
        } else {
            stepX = 1;
            sideDistX = (mapX + 1.0 - player.getX()) * deltaDistX;
        }

        if (rayDirY < 0) {
            stepY = -1;
            sideDistY = (player.getY() - mapY) * deltaDistY;
        } else {
            stepY = 1;
            sideDistY = (mapY + 1.0 - player.getY()) * deltaDistY;
        }

        // Perform DDA (Digital Differential Analysis)
        boolean hit = false;
        boolean side = false; // Was a NS or EW wall hit?
        int wallType = 0;

        while (!hit) {
            // Jump to next map square, either in x-direction, or in y-direction
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = false;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = true;
            }

            // Check if ray has hit a wall
            if (mapX < 0 || mapX >= mapWidth || mapY < 0 || mapY >= mapHeight) {
                hit = true;
                wallType = 1; // Default wall type for out of bounds
            } else if (map[mapY][mapX] > 0) {
                hit = true;
                wallType = map[mapY][mapX];
            }
        }

        // Calculate distance to wall
        double wallDist;
        if (!side) {
            wallDist = (mapX - player.getX() + (1 - stepX) / 2) / rayDirX;
        } else {
            wallDist = (mapY - player.getY() + (1 - stepY) / 2) / rayDirY;
        }

        // Calculate wall X coordinate (for texturing)
        double wallX;
        if (side) {
            wallX = player.getX() + wallDist * rayDirX;
        } else {
            wallX = player.getY() + wallDist * rayDirY;
        }
        wallX -= Math.floor(wallX);

        return new RaycastHit(wallDist, wallX, wallType, side);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public PlayerEntity getPlayer() {
        return player;
    }
}