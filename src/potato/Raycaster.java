package potato;

import potato.entities.Entity;
import potato.entities.PlayerEntity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

public class Raycaster {
    public static final double PLANE_DIST = 1.0; // Distance to projection plane
    public static final int FOV = 60; // Field of view in degrees
    public static final int WALL_HEIGHT = Game.INTERNAL_HEIGHT / 2;

    public Level currentLevel;


    private static class RaycastHit {
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

    // Store wall strips for depth sorting
    private static class WallStrip {
        int x;
        double distance;
        double rayAngle;
        RaycastHit hit;

        WallStrip(int x, double distance, double rayAngle, RaycastHit hit) {
            this.x = x;
            this.distance = distance;
            this.rayAngle = rayAngle;
            this.hit = hit;
        }
    }


    public void render(Graphics2D graphics2D) {
        PlayerEntity player = currentLevel.getPlayer();
        double rayAngleStep = Math.toRadians(FOV) / Game.INTERNAL_WIDTH;
        double startAngle = player.getAngle() - Math.toRadians(FOV) / 2;

        // Render floor and ceiling first
        renderFloorAndCeiling(graphics2D, player, startAngle);

        // Rest of the existing rendering code for walls and entities...
        ArrayList<WallStrip> wallStrips = new ArrayList<>();
        ArrayList<Map.Entry<Entity, Double>> entities = new ArrayList<>();

        // First pass: collect all wall strips
        for (int x = 0; x < Game.INTERNAL_WIDTH; x++) {
            double rayAngle = startAngle + x * rayAngleStep;
            RaycastHit hit = castRay(rayAngle, currentLevel);

            if (hit.textureId > 0) {
                double perpWallDist = hit.distance * Math.cos(rayAngle - player.getAngle());
                wallStrips.add(new WallStrip(x, perpWallDist, rayAngle, hit));
            }
        }

        // Collect entities...
        for (Entity entity : currentLevel.getEntities()) {
            if (entity != player) {
                double dx = entity.getX() - player.getX();
                double dy = entity.getY() - player.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                entities.add(new AbstractMap.SimpleEntry<>(entity, distance));
            }
        }

        // Sort and render everything from back to front...
        wallStrips.sort((a, b) -> Double.compare(b.distance, a.distance));
        entities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        while (!wallStrips.isEmpty() || !entities.isEmpty()) {
            double nextWallDist = wallStrips.isEmpty() ? Double.NEGATIVE_INFINITY : wallStrips.get(0).distance;
            double nextEntityDist = entities.isEmpty() ? Double.NEGATIVE_INFINITY : entities.get(0).getValue();

            if (nextWallDist > nextEntityDist) {
                WallStrip strip = wallStrips.remove(0);
                renderWallColumn(graphics2D, strip.x, strip.rayAngle, strip.hit);
            } else {
                Map.Entry<Entity, Double> entityEntry = entities.remove(0);
                entityEntry.getKey().render(graphics2D);
            }
        }
    }

    private void renderFloorAndCeiling(Graphics2D graphics2D, PlayerEntity player, double startAngle) {
        // Camera plane
        double planeX = -Math.sin(player.getAngle()) * Math.tan(Math.toRadians(FOV) / 2);
        double planeY = Math.cos(player.getAngle()) * Math.tan(Math.toRadians(FOV) / 2);

        // For each horizontal line on screen
        for (int y = Game.INTERNAL_HEIGHT / 2; y < Game.INTERNAL_HEIGHT; y++) {
            // Current distance from camera to floor
            double rowDistance = (double) WALL_HEIGHT / (y - Game.INTERNAL_HEIGHT / 2);

            // Calculate stepping values for ray directions
            double floorStepX = rowDistance * (2 * planeX) / Game.INTERNAL_WIDTH;
            double floorStepY = rowDistance * (2 * planeY) / Game.INTERNAL_WIDTH;

            // Starting position for floor casting
            double floorX = player.getX() + rowDistance * Math.cos(startAngle);
            double floorY = player.getY() + rowDistance * Math.sin(startAngle);

            for (int x = 0; x < Game.INTERNAL_WIDTH; x++) {
                // Get the exact position on the floor/ceiling
                int cellX = (int) floorX;
                int cellY = (int) floorY;

                // Floor pixel
                if (currentLevel.floorTexture != null) {
                    // Calculate texture coordinates
                    int tx = (int) ((floorX - cellX) * currentLevel.floorTexture.getWidth()) & (currentLevel.floorTexture.getWidth() - 1);
                    int ty = (int) ((floorY - cellY) * currentLevel.floorTexture.getHeight()) & (currentLevel.floorTexture.getHeight() - 1);

                    int color = currentLevel.floorTexture.getRGB(tx, ty);
                    color = applyFog(color, (float) Math.min(1.0, rowDistance / 10.0));
                    graphics2D.setColor(new Color(color));
                } else {
                    graphics2D.setColor(currentLevel.floorColor);
                }
                graphics2D.drawLine(x, y, x, y);

                // Ceiling pixel (mirror of floor)
                int ceilingY = Game.INTERNAL_HEIGHT - y - 1;
                if (currentLevel.ceilingTexture != null) {
                    int tx = (int) ((floorX - cellX) * currentLevel.ceilingTexture.getWidth()) & (currentLevel.ceilingTexture.getWidth() - 1);
                    int ty = (int) ((floorY - cellY) * currentLevel.ceilingTexture.getHeight()) & (currentLevel.ceilingTexture.getHeight() - 1);

                    int color = currentLevel.ceilingTexture.getRGB(tx, ty);
                    color = applyFog(color, (float) Math.min(1.0, rowDistance / 10.0));
                    graphics2D.setColor(new Color(color));
                } else {
                    graphics2D.setColor(currentLevel.ceilingColor);
                }
                graphics2D.drawLine(x, ceilingY, x, ceilingY);

                floorX += floorStepX;
                floorY += floorStepY;
            }
        }
    }

    public void update()
    {
        currentLevel.update();
    }

    private void renderWallColumn(Graphics2D graphics2D, int x, double rayAngle, RaycastHit hit) {
        // Calculate wall height based on distance
        double perpWallDist = hit.distance * Math.cos(rayAngle - currentLevel.getPlayer().getAngle());
        int lineHeight = (int) (WALL_HEIGHT / perpWallDist);

        // Calculate drawing bounds
        int drawStart = -lineHeight / 2 + Game.INTERNAL_HEIGHT / 2;
        if (drawStart < 0) drawStart = 0;
        int drawEnd = lineHeight / 2 + Game.INTERNAL_HEIGHT / 2;
        if (drawEnd >= Game.INTERNAL_HEIGHT) drawEnd = Game.INTERNAL_HEIGHT - 1;

        // Get the wall texture
        BufferedImage texture = currentLevel.getTexture(hit.textureId);
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

    private RaycastHit castRay(double rayAngle, Level level) {
        PlayerEntity player = level.getPlayer();
        double rayDirX = Math.cos(rayAngle);
        double rayDirY = Math.sin(rayAngle);

        // Current map position
        int mapX = (int) player.getX();
        int mapY = (int) player.getY();

        // Length of ray from current position to next x or y-side
        double deltaDistX = Math.abs(1 / rayDirX);
        double deltaDistY = Math.abs(1 / rayDirY);

        // Calculate step and initial sideDist
        int stepX = rayDirX < 0 ? -1 : 1;
        int stepY = rayDirY < 0 ? -1 : 1;

        double sideDistX = rayDirX < 0
                ? (player.getX() - mapX) * deltaDistX
                : (mapX + 1.0 - player.getX()) * deltaDistX;

        double sideDistY = rayDirY < 0
                ? (player.getY() - mapY) * deltaDistY
                : (mapY + 1.0 - player.getY()) * deltaDistY;

        // Perform DDA
        boolean hit = false;
        boolean side = false;
        int wallType = 0;

        while (!hit) {
            if (sideDistX < sideDistY) {
                sideDistX += deltaDistX;
                mapX += stepX;
                side = false;
            } else {
                sideDistY += deltaDistY;
                mapY += stepY;
                side = true;
            }

            if (mapX < 0 || mapX >= level.getMapWidth() ||
                    mapY < 0 || mapY >= level.getMapHeight()) {
                hit = true;
                wallType = 1;
            } else if (level.getWallType(mapX, mapY) > 0) {
                hit = true;
                wallType = level.getWallType(mapX, mapY);
            }
        }

        // Calculate distance and wall X
        double wallDist = side
                ? (mapY - player.getY() + (1 - stepY) / 2) / rayDirY
                : (mapX - player.getX() + (1 - stepX) / 2) / rayDirX;

        double wallX = side
                ? player.getX() + wallDist * rayDirX
                : player.getY() + wallDist * rayDirY;
        wallX -= Math.floor(wallX);

        return new RaycastHit(wallDist, wallX, wallType, side);
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
}