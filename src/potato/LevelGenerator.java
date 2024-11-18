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
        Wall[][] map = new Wall[height][width];
        Level level = new Level(map, name);  // Create level first so we can pass it to doors

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Always have walls at the borders
                if (x == 0 || x == width-1 || y == 0 || y == height-1) {
                    map[y][x] = new Wall(1); // Basic wall
                }
                // Random walls elsewhere with low probability
                else if (random.nextFloat() < WALL_CHANCE) {
                    // Sometimes create doors
                    if (random.nextFloat() < 0.3f) {
                        Door door = new Door();
                        door.setPosition(level, x, y);  // Set the door's position
                        map[y][x] = door;
                    } else {
                        map[y][x] = new Wall(1); // Regular wall
                    }
                }
            }
        }

        // Ensure player spawn area is clear
        clearArea(map, 1, 1, 3, 3);

        return level;
    }

    private void clearArea(Wall[][] map, int x, int y, int width, int height) {
        for (int dy = y; dy < y + height && dy < map.length; dy++) {
            for (int dx = x; dx < x + width && dx < map[0].length; dx++) {
                map[dy][dx] = null;
            }
        }
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
    }
}