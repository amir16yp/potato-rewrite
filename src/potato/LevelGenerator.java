package potato;

import java.util.*;

public class LevelGenerator {
    private final Random random;
    private ArrayList<Room> rooms;
    public Level generatedLevel;

    // Optimized constants for small maps
    private static final int MIN_ROOM_SIZE = 5;
    private static final int MAX_ROOM_SIZE = 12;
    private static final int MIN_ROOMS = 4;
    private static final int MAX_ROOMS = 8;
    private static final int MIN_ROOM_SPACING = 2; // Minimum spaces between rooms
    private static final float ENEMY_SPAWN_CHANCE = 0.15f;
    private static final float ITEM_SPAWN_CHANCE = 0.1f;

    private enum RoomType {
        SPAWN,    // Starting room
        GOAL,     // End room (furthest from spawn)
        TREASURE, // Contains more items
        COMBAT,   // Contains more enemies
        NORMAL    // Standard room
    }

    private class Room {
        int x, y, width, height;
        RoomType type;
        List<Room> connectedRooms;

        Room(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.type = RoomType.NORMAL;
            this.connectedRooms = new ArrayList<>();
        }

        boolean intersects(Room other, int spacing) {
            return !(x + width + spacing < other.x ||
                    other.x + other.width + spacing < x ||
                    y + height + spacing < other.y ||
                    other.y + other.height + spacing < y);
        }

        int distanceTo(Room other) {
            int centerX = x + width / 2;
            int centerY = y + height / 2;
            int otherCenterX = other.x + other.width / 2;
            int otherCenterY = other.y + other.height / 2;
            return Math.abs(centerX - otherCenterX) + Math.abs(centerY - otherCenterY);
        }
    }

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

        // Fill with walls
        fillWithWalls(map);

        // Generate rooms
        generateRooms(width, height);

        // Assign room types based on position
        assignRoomTypes();

        // Create pathways between rooms
        connectRooms(map);

        // Decorate and populate rooms
        decorateRooms(map);

        // Create player spawn area
        createPlayerSpawn(map);

        this.generatedLevel = level;
        return level;
    }

    private void fillWithWalls(Wall[][] map) {
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[0].length; x++) {
                map[y][x] = new Wall(1);
            }
        }
    }

    private void generateRooms(int width, int height) {
        rooms = new ArrayList<>();
        int numRooms = MIN_ROOMS + random.nextInt(MAX_ROOMS - MIN_ROOMS + 1);
        int attempts = 0;
        int maxAttempts = 100;

        while (rooms.size() < numRooms && attempts < maxAttempts) {
            // Scale room size based on map size
            int maxWidth = Math.min(MAX_ROOM_SIZE, width / 4);
            int maxHeight = Math.min(MAX_ROOM_SIZE, height / 4);

            int roomWidth = MIN_ROOM_SIZE + random.nextInt(maxWidth - MIN_ROOM_SIZE + 1);
            int roomHeight = MIN_ROOM_SIZE + random.nextInt(maxHeight - MIN_ROOM_SIZE + 1);

            // Ensure rooms aren't placed at the very edge
            int roomX = 1 + random.nextInt(width - roomWidth - 2);
            int roomY = 1 + random.nextInt(height - roomHeight - 2);

            Room newRoom = new Room(roomX, roomY, roomWidth, roomHeight);

            boolean canPlace = true;
            for (Room existingRoom : rooms) {
                if (newRoom.intersects(existingRoom, MIN_ROOM_SPACING)) {
                    canPlace = false;
                    break;
                }
            }

            if (canPlace) {
                rooms.add(newRoom);
            }
            attempts++;
        }
    }

    private void assignRoomTypes() {
        if (rooms.isEmpty()) return;

        // First room is spawn
        rooms.get(0).type = RoomType.SPAWN;

        // Find room furthest from spawn for goal
        Room spawnRoom = rooms.get(0);
        Room furthestRoom = null;
        int maxDistance = 0;

        for (Room room : rooms) {
            if (room != spawnRoom) {
                int distance = room.distanceTo(spawnRoom);
                if (distance > maxDistance) {
                    maxDistance = distance;
                    furthestRoom = room;
                }
            }
        }

        if (furthestRoom != null) {
            furthestRoom.type = RoomType.GOAL;
        }

        // Assign other room types
        for (Room room : rooms) {
            if (room.type == RoomType.NORMAL) {
                float roll = random.nextFloat();
                if (roll < 0.3f) {
                    room.type = RoomType.TREASURE;
                } else if (roll < 0.6f) {
                    room.type = RoomType.COMBAT;
                }
            }
        }
    }

    private void connectRooms(Wall[][] map) {
        // Use a modified MST algorithm for basic connectivity
        Set<Room> connected = new HashSet<>();
        connected.add(rooms.get(0));  // Start with spawn room

        while (connected.size() < rooms.size()) {
            int minDistance = Integer.MAX_VALUE;
            Room roomA = null;
            Room roomB = null;

            for (Room connectedRoom : connected) {
                for (Room unconnectedRoom : rooms) {
                    if (!connected.contains(unconnectedRoom)) {
                        int distance = connectedRoom.distanceTo(unconnectedRoom);
                        if (distance < minDistance) {
                            minDistance = distance;
                            roomA = connectedRoom;
                            roomB = unconnectedRoom;
                        }
                    }
                }
            }

            if (roomA != null && roomB != null) {
                createConnection(roomA, roomB, map);
                roomA.connectedRooms.add(roomB);
                roomB.connectedRooms.add(roomA);
                connected.add(roomB);
            }
        }

        // Add a few extra connections for loops
        int extraConnections = Math.max(1, rooms.size() / 4);
        for (int i = 0; i < extraConnections; i++) {
            Room roomA = rooms.get(random.nextInt(rooms.size()));
            Room roomB = rooms.get(random.nextInt(rooms.size()));

            if (roomA != roomB && !roomA.connectedRooms.contains(roomB)) {
                createConnection(roomA, roomB, map);
                roomA.connectedRooms.add(roomB);
                roomB.connectedRooms.add(roomA);
            }
        }
    }

    private void createConnection(Room roomA, Room roomB, Wall[][] map) {
        // Find door positions on room boundaries
        int[] doorA = findDoorLocation(roomA, roomB);
        int[] doorB = findDoorLocation(roomB, roomA);

        // Place doors
        map[doorA[1]][doorA[0]] = new Door();
        map[doorB[1]][doorB[0]] = new Door();

        // Create corridor between doors
        createCorridor(doorA[0], doorA[1], doorB[0], doorB[1], map);
    }

    private int[] findDoorLocation(Room room, Room target) {
        int doorX, doorY;

        // Determine which wall to place the door based on target room position
        int roomCenterX = room.x + room.width / 2;
        int roomCenterY = room.y + room.height / 2;
        int targetCenterX = target.x + target.width / 2;
        int targetCenterY = target.y + target.height / 2;

        if (Math.abs(targetCenterX - roomCenterX) > Math.abs(targetCenterY - roomCenterY)) {
            // Place door on east or west wall
            doorX = (targetCenterX > roomCenterX) ? room.x + room.width - 1 : room.x;
            doorY = room.y + 1 + random.nextInt(room.height - 2);
        } else {
            // Place door on north or south wall
            doorX = room.x + 1 + random.nextInt(room.width - 2);
            doorY = (targetCenterY > roomCenterY) ? room.y + room.height - 1 : room.y;
        }

        return new int[]{doorX, doorY};
    }

    private void createCorridor(int x1, int y1, int x2, int y2, Wall[][] map) {
        // Create an L-shaped corridor
        if (random.nextBoolean()) {
            createHorizontalCorridor(x1, x2, y1, map);
            createVerticalCorridor(y1, y2, x2, map);
        } else {
            createVerticalCorridor(y1, y2, x1, map);
            createHorizontalCorridor(x1, x2, y2, map);
        }
    }

    private void createHorizontalCorridor(int x1, int x2, int y, Wall[][] map) {
        int start = Math.min(x1, x2);
        int end = Math.max(x1, x2);

        for (int x = start; x <= end; x++) {
            if (!(map[y][x] instanceof Door)) {
                map[y][x] = null;
                if (y > 0) map[y-1][x] = new Wall(1);
                if (y < map.length-1) map[y+1][x] = new Wall(1);
            }
        }
    }

    private void createVerticalCorridor(int y1, int y2, int x, Wall[][] map) {
        int start = Math.min(y1, y2);
        int end = Math.max(y1, y2);

        for (int y = start; y <= end; y++) {
            if (!(map[y][x] instanceof Door)) {
                map[y][x] = null;
                if (x > 0) map[y][x-1] = new Wall(1);
                if (x < map[0].length-1) map[y][x+1] = new Wall(1);
            }
        }
    }

    private void decorateRooms(Wall[][] map) {
        for (Room room : rooms) {
            // Clear room interior
            for (int y = room.y; y < room.y + room.height; y++) {
                for (int x = room.x; x < room.x + room.width; x++) {
                    if (!(map[y][x] instanceof Door)) {
                        map[y][x] = null;
                    }
                }
            }

            // Add room-type specific features
            switch (room.type) {
                case TREASURE:
                    decorateTreasureRoom(room, map);
                    break;
                case COMBAT:
                    decorateCombatRoom(room, map);
                    break;
                case GOAL:
                    decorateGoalRoom(room, map);
                    break;
            }
        }
    }

    private void decorateTreasureRoom(Room room, Wall[][] map) {
        // Add corner pillars
        if (room.width >= 6 && room.height >= 6) {
            int midX = room.x + room.width / 2;
            int midY = room.y + room.height / 2;

            map[midY-1][midX-1] = new Wall(2);
            map[midY-1][midX+1] = new Wall(2);
            map[midY+1][midX-1] = new Wall(2);
            map[midY+1][midX+1] = new Wall(2);
        }
    }

    private void decorateCombatRoom(Room room, Wall[][] map) {
        // Add cover blocks for combat
        if (room.width >= 7 && room.height >= 7) {
            for (int i = 0; i < 3; i++) {
                int x = room.x + 2 + random.nextInt(room.width - 4);
                int y = room.y + 2 + random.nextInt(room.height - 4);
                map[y][x] = new Wall(1);
            }
        }
    }

    private void decorateGoalRoom(Room room, Wall[][] map) {
        // Add distinctive features to goal room
        int midX = room.x + room.width / 2;
        int midY = room.y + room.height / 2;

        // Create a central structure
        if (room.width >= 7 && room.height >= 7) {
            map[midY][midX] = new Wall(3);  // Special wall type for goal
            map[midY-1][midX] = new Wall(2);
            map[midY+1][midX] = new Wall(2);
            map[midY][midX-1] = new Wall(2);
            map[midY][midX+1] = new Wall(2);
        }
    }

    private void createPlayerSpawn(Wall[][] map) {
        Room spawnRoom = rooms.get(0);
        int spawnX = spawnRoom.x + spawnRoom.width / 2;
        int spawnY = spawnRoom.y + spawnRoom.height / 2;

        // Clear a safe area around spawn point
        for (int y = spawnY - 1; y <= spawnY + 1; y++) {
            for (int x = spawnX - 1; x <= spawnX + 1; x++) {
                if (y >= 0 && y < map.length && x >= 0 && x < map[0].length) {
                    map[y][x] = null;
                }
            }
        }
    }

    public int[] getSpawnRoomCenterXY() {
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalStateException("Level has not been generated yet");
        }
        Room spawnRoom = rooms.get(0); // First room is always the spawn room
        return new int[] {
                spawnRoom.x + spawnRoom.width / 2,
                spawnRoom.y + spawnRoom.height / 2
        };
    }


    public void setSeed(long seed) {
        random.setSeed(seed);
    }
}