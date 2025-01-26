package potato;

import potato.entities.PlayerEntity;

import java.util.*;

public class LevelGenerator {
    public Level generatedLevel;
    private final Random random;

    public LevelGenerator() {
        this.random = new Random();
        generateLevel("level", 64, 64);
    }

    public LevelGenerator(long seed) {
        this.random = new Random(seed);
        generateLevel("level", 64, 64);
    }

    public Level generateLevel(String name, int width, int height) {
        Wall[][] map = new Wall[height][width];
        Level level = new Level(map, name);
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