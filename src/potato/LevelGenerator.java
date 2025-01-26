package potato;

import java.util.*;

public class LevelGenerator {
    private ArrayList<Room> rooms;
    public Level generatedLevel;
    private final Random random;

    private class Room {
        int x, y, width, height;
        Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    public LevelGenerator() {
        this.random = new Random();
        generateLevel("level", 256, 256);
    }

    public LevelGenerator(long seed) {
        this.random = new Random(seed);
        generateLevel("level", 256, 256);
    }

    public Level generateLevel(String name, int width, int height) {
        Wall[][] map = new Wall[height][width];
        Level level = new Level(map, name);
        rooms = new ArrayList<>();

        // Fill edges with walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || x == width-1 || y == 0 || y == height-1) {
                    map[y][x] = new Wall(1);
                }
            }
        }

        // Create single large room
        Room room = new Room(1, 1, width-2, height-2);
        rooms.add(room);

        this.generatedLevel = level;
        return level;
    }

    public int[] getSpawnRoomCenterXY() {
        return new int[] {
                generatedLevel.getMapWidth() / 2,
                generatedLevel.getMapHeight() / 2
        };
    }

    public void setSeed(long seed) {
        random.setSeed(seed);
    }
}