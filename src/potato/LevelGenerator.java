package potato;

import java.util.Random;

public class LevelGenerator {
    private final Random random;
    private static final float WALL_CHANCE = 0.15f;
    private static final float ENEMY_SPAWN_CHANCE = 0.1f;

    public LevelGenerator() {
        this.random = new Random();
    }

    public Level generateLevel(String name, int width, int height) {
        int[][] map = new int[height][width];

        // Generate mostly flat terrain with occasional walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Always have walls at the borders
                if (x == 0 || x == width-1 || y == 0 || y == height-1) {
                    map[y][x] = 1;
                }
                // Random walls elsewhere with low probability
                else {
                    map[y][x] = random.nextFloat() < WALL_CHANCE ? 1 : 0;
                }
            }
        }

        // Ensure player spawn area is clear
        clearArea(map, 1, 1, 3, 3);

        return new Level(map, name);
    }

    private void clearArea(int[][] map, int x, int y, int width, int height) {
        for (int dy = y; dy < y + height && dy < map.length; dy++) {
            for (int dx = x; dx < x + width && dx < map[0].length; dx++) {
                map[dy][dx] = 0;
            }
        }
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
    }
}