package potato;

import potato.entities.Entity;
import potato.entities.PlayerEntity;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Raycaster {
    public static final double PLANE_DIST = 1.0;
    public static int FOV = ConfigManager.get().getInt(GameProperty.DEFAULT_FOV);
    public static final int WALL_HEIGHT = Game.INTERNAL_HEIGHT / 2;
    private static final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
    private final ReentrantLock graphicsLock = new ReentrantLock();
    private static int FOG_COLOR = new Color(135, 206, 250)
            .getRGB();
    public static void setFOV(int fov) {
        FOV = fov;
    }


    // TODO: make this level dependant
    public static void setFogColor(Color color)
    {
        FOG_COLOR = color.getRGB();
    }

    public Raycaster()
    {
        System.out.println("USING " + THREAD_COUNT + " THREADS FOR RAYCASTING");
    }

    private static class RaycastHit {
        double distance;
        double wallX;
        Wall wall;
        boolean side;

        RaycastHit(double distance, double wallX, Wall wall, boolean side) {
            this.distance = distance;
            this.wallX = wallX;
            this.wall = wall;
            this.side = side;
        }
    }

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

    private class FloorCeilingTask implements Callable<Void> {
        private final Graphics2D graphics2D;
        private final PlayerEntity player;
        private final double startAngle;
        private final int startY;
        private final int endY;

        FloorCeilingTask(Graphics2D graphics2D, PlayerEntity player, double startAngle, int startY, int endY) {
            this.graphics2D = graphics2D;
            this.player = player;
            this.startAngle = startAngle;
            this.startY = startY;
            this.endY = endY;
        }

        @Override
        public Void call() {
            double planeX = -Math.sin(player.getAngle()) * Math.tan(Math.toRadians(FOV) / 2);
            double planeY = Math.cos(player.getAngle()) * Math.tan(Math.toRadians(FOV) / 2);

            for (int y = startY; y < endY; y++) {
                double rowDistance = (double) WALL_HEIGHT / (y - Game.INTERNAL_HEIGHT / 2);
                double floorStepX = rowDistance * (2 * planeX) / Game.INTERNAL_WIDTH;
                double floorStepY = rowDistance * (2 * planeY) / Game.INTERNAL_WIDTH;
                double floorX = player.getX() + rowDistance * Math.cos(startAngle);
                double floorY = player.getY() + rowDistance * Math.sin(startAngle);

                graphicsLock.lock();
                try {
                    for (int x = 0; x < Game.INTERNAL_WIDTH; x++) {
                        int cellX = (int) floorX;
                        int cellY = (int) floorY;

                        // Floor
                        if (Game.LEVEL_GENERATOR.generatedLevel.floorTexture != null) {
                            int tx = (int) ((floorX - cellX) * Game.LEVEL_GENERATOR.generatedLevel.floorTexture.getWidth()) & (Game.LEVEL_GENERATOR.generatedLevel.floorTexture.getWidth() - 1);
                            int ty = (int) ((floorY - cellY) * Game.LEVEL_GENERATOR.generatedLevel.floorTexture.getHeight()) & (Game.LEVEL_GENERATOR.generatedLevel.floorTexture.getHeight() - 1);
                            int color = Game.LEVEL_GENERATOR.generatedLevel.floorTexture.getRGB(tx, ty);
                            color = applyFog(color, (float) Math.min(1.0, rowDistance / 20.0));
                            graphics2D.setColor(new Color(color));
                        } else {
                            graphics2D.setColor(Game.LEVEL_GENERATOR.generatedLevel.floorColor);
                        }
                        graphics2D.drawLine(x, y, x, y);

                        // Ceiling
                        int ceilingY = Game.INTERNAL_HEIGHT - y - 1;
                        if (Game.LEVEL_GENERATOR.generatedLevel.ceilingTexture != null) {
                            int tx = (int) ((floorX - cellX) * Game.LEVEL_GENERATOR.generatedLevel.ceilingTexture.getWidth()) & (Game.LEVEL_GENERATOR.generatedLevel.ceilingTexture.getWidth() - 1);
                            int ty = (int) ((floorY - cellY) * Game.LEVEL_GENERATOR.generatedLevel.ceilingTexture.getHeight()) & (Game.LEVEL_GENERATOR.generatedLevel.ceilingTexture.getHeight() - 1);
                            int color = Game.LEVEL_GENERATOR.generatedLevel.ceilingTexture.getRGB(tx, ty);
                            color = applyFog(color, (float) Math.min(1.0, rowDistance / 10.0));
                            graphics2D.setColor(new Color(color));
                        } else {
                            graphics2D.setColor(Game.LEVEL_GENERATOR.generatedLevel.ceilingColor);
                        }
                        graphics2D.drawLine(x, ceilingY, x, ceilingY);

                        floorX += floorStepX;
                        floorY += floorStepY;
                    }
                } finally {
                    graphicsLock.unlock();
                }
            }
            return null;
        }
    }

    private class WallRenderTask implements Callable<ArrayList<WallStrip>> {
        private final int startX;
        private final int endX;
        private final double startAngle;
        private final double rayAngleStep;
        private final PlayerEntity player;

        WallRenderTask(int startX, int endX, double startAngle, double rayAngleStep, PlayerEntity player) {
            this.startX = startX;
            this.endX = endX;
            this.startAngle = startAngle;
            this.rayAngleStep = rayAngleStep;
            this.player = player;
        }

        @Override
        public ArrayList<WallStrip> call() {
            ArrayList<WallStrip> strips = new ArrayList<>();
            for (int x = startX; x < endX; x++) {
                double rayAngle = startAngle + x * rayAngleStep;
                RaycastHit hit = castRay(rayAngle, Game.LEVEL_GENERATOR.generatedLevel);
                if (hit.wall != null) {
                    double perpWallDist = hit.distance * Math.cos(rayAngle - player.getAngle());
                    strips.add(new WallStrip(x, perpWallDist, rayAngle, hit));
                }
            }
            return strips;
        }
    }

    private RaycastHit castRay(double rayAngle, Level level) {
        PlayerEntity player = PlayerEntity.getPlayer();
        double rayDirX = Math.cos(rayAngle);
        double rayDirY = Math.sin(rayAngle);

        int mapX = (int) player.getX();
        int mapY = (int) player.getY();

        double deltaDistX = Math.abs(1 / rayDirX);
        double deltaDistY = Math.abs(1 / rayDirY);

        int stepX = rayDirX < 0 ? -1 : 1;
        int stepY = rayDirY < 0 ? -1 : 1;

        double sideDistX = rayDirX < 0
                ? (player.getX() - mapX) * deltaDistX
                : (mapX + 1.0 - player.getX()) * deltaDistX;

        double sideDistY = rayDirY < 0
                ? (player.getY() - mapY) * deltaDistY
                : (mapY + 1.0 - player.getY()) * deltaDistY;

        boolean hit = false;
        boolean side = false;
        Wall wall = null;

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
                wall = new Wall(1);
            } else {
                wall = level.getWall(mapX, mapY);
                if (wall != null && wall.getCurrentTexture() != null) {
                    hit = true;
                }
            }
        }

        double wallDist = side
                ? (mapY - player.getY() + (1 - stepY) / 2) / rayDirY
                : (mapX - player.getX() + (1 - stepX) / 2) / rayDirX;

        double wallX = side
                ? player.getX() + wallDist * rayDirX
                : player.getY() + wallDist * rayDirY;
        wallX -= Math.floor(wallX);

        return new RaycastHit(wallDist, wallX, wall, side);
    }

    private void renderWallColumn(Graphics2D graphics2D, int x, double rayAngle, RaycastHit hit) {
        if (hit.wall == null || hit.wall.getCurrentTexture() == null) {
            return;
        }

        double perpWallDist = hit.distance * Math.cos(rayAngle - PlayerEntity.getPlayer().getAngle());
        int lineHeight = (int) (WALL_HEIGHT / perpWallDist);

        int drawStart = Math.max(0, -lineHeight / 2 + Game.INTERNAL_HEIGHT / 2);
        int drawEnd = Math.min(Game.INTERNAL_HEIGHT - 1, lineHeight / 2 + Game.INTERNAL_HEIGHT / 2);

        BufferedImage texture = hit.wall.getCurrentTexture();
        int texX = (int) (hit.wallX * texture.getWidth());
        if ((!hit.side && Math.cos(rayAngle) < 0) ||
                (hit.side && Math.sin(rayAngle) < 0)) {
            texX = texture.getWidth() - texX - 1;
        }

        double step = (double) texture.getHeight() / lineHeight;
        double texPos = (drawStart - Game.INTERNAL_HEIGHT / 2.0 + lineHeight / 2.0) * step;

        graphicsLock.lock();
        try {
            for (int y = drawStart; y < drawEnd; y++) {
                int texY = (int) texPos & (texture.getHeight() - 1);
                texPos += step;

                int color = texture.getRGB(texX, texY);
                float fogFactor = (float) Math.min(1.0, hit.distance / 10.0);
                color = applyFog(color, fogFactor);

                graphics2D.setColor(new Color(color));
                graphics2D.drawLine(x, y, x, y);
            }
        } finally {
            graphicsLock.unlock();
        }
    }

    private int applyFog(int color, float fogFactor) {
        int a = (color >> 24) & 0xff;
        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = color & 0xff;

        int fogR = (FOG_COLOR >> 16) & 0xff;
        int fogG = (FOG_COLOR >> 8) & 0xff;
        int fogB = FOG_COLOR & 0xff;

        r = (int) (r * (1 - fogFactor) + fogR * fogFactor);
        g = (int) (g * (1 - fogFactor) + fogG * fogFactor);
        b = (int) (b * (1 - fogFactor) + fogB * fogFactor);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public void render(Graphics2D graphics2D) {
        PlayerEntity player = PlayerEntity.getPlayer();
        double rayAngleStep = Math.toRadians(FOV) / Game.INTERNAL_WIDTH;
        double startAngle = player.getAngle() - Math.toRadians(FOV) / 2;

        // Parallelize floor and ceiling rendering
        int floorChunkSize = (Game.INTERNAL_HEIGHT / 2) / THREAD_COUNT;
        ArrayList<Future<Void>> floorCeilingFutures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            int startY = Game.INTERNAL_HEIGHT / 2 + i * floorChunkSize;
            int endY = (i == THREAD_COUNT - 1) ? Game.INTERNAL_HEIGHT : startY + floorChunkSize;
            floorCeilingFutures.add(executorService.submit(
                    new FloorCeilingTask(graphics2D, player, startAngle, startY, endY)
            ));
        }

        // Parallelize wall rendering
        int wallChunkSize = Game.INTERNAL_WIDTH / THREAD_COUNT;
        ArrayList<Future<ArrayList<WallStrip>>> wallFutures = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            int startX = i * wallChunkSize;
            int endX = (i == THREAD_COUNT - 1) ? Game.INTERNAL_WIDTH : (i + 1) * wallChunkSize;
            wallFutures.add(executorService.submit(
                    new WallRenderTask(startX, endX, startAngle, rayAngleStep, player)
            ));
        }

        // Process entities in parallel
        Future<ArrayList<Map.Entry<Entity, Double>>> entityFuture = executorService.submit(() -> {
            ArrayList<Map.Entry<Entity, Double>> entities = new ArrayList<>();
            for (Entity entity : Game.LEVEL_GENERATOR.generatedLevel.getEntities()) {
                if (entity != player) {
                    double dx = entity.getX() - player.getX();
                    double dy = entity.getY() - player.getY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    entities.add(new AbstractMap.SimpleEntry<>(entity, distance));
                }
            }
            entities.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
            return entities;
        });

        try {
            // Wait for floor/ceiling completion
            for (Future<Void> future : floorCeilingFutures) {
                future.get();
            }

            ArrayList<WallStrip> allStrips = new ArrayList<>();
            for (Future<ArrayList<WallStrip>> future : wallFutures) {
                allStrips.addAll(future.get());
            }
            allStrips.sort((a, b) -> Double.compare(b.distance, a.distance));

            // Get sorted entities
            ArrayList<Map.Entry<Entity, Double>> entities = entityFuture.get();

            // Render everything in sorted order
            while (!allStrips.isEmpty() || !entities.isEmpty()) {
                double nextWallDist = allStrips.isEmpty() ? Double.NEGATIVE_INFINITY : allStrips.get(0).distance;
                double nextEntityDist = entities.isEmpty() ? Double.NEGATIVE_INFINITY : entities.get(0).getValue();

                if (nextWallDist > nextEntityDist) {
                    WallStrip strip = allStrips.remove(0);
                    renderWallColumn(graphics2D, strip.x, strip.rayAngle, strip.hit);
                } else {
                    Map.Entry<Entity, Double> entityEntry = entities.remove(0);
                    graphicsLock.lock();
                    try {
                        entityEntry.getKey().render(graphics2D);
                    } finally {
                        graphicsLock.unlock();
                    }
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Render failed", e);
        }
    }

    public void update() {
        Game.LEVEL_GENERATOR.generatedLevel.update();
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
