package potato;

import java.awt.image.BufferedImage;

public class Door extends Wall {
    private static final int DOOR_TYPE = 25;
    private static final Textures DOOR_TEXTURES = new Textures("/potato/assets/sprites/door.png", 16, 16);

    private Level level;
    private int mapX, mapY;
    private int frame = 1;
    private boolean isOpening = false;
    private static final int FRAME_COUNT = 16;
    private static final double FRAME_DURATION = 50; // milliseconds
    private double frameTimer = 0;

    public Door() {
        super(DOOR_TYPE);
    }

    public void setPosition(Level level, int x, int y) {
        this.level = level;
        this.mapX = x;
        this.mapY = y;
    }

    public void open() {
        if (!isOpening) {
            isOpening = true;
            frame = 1;
            frameTimer = 0;
        }
    }

    @Override
    public void update() {
        if (isOpening) {
            frameTimer += Game.GAMELOOP.getDeltaTimeMillis();
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frame++;

                if (frame > FRAME_COUNT) {
                    // Animation complete, remove door
                    if (level != null) {
                        Wall[][] map = level.getMap();
                        map[mapY][mapX] = null;
                    }
                }
            }
        }
    }

    @Override
    public BufferedImage getCurrentTexture() {
        if (isOpening) {
            return DOOR_TEXTURES.getTile(frame);
        }
        return DEFAULT_TEXTURES.getTile(type);
    }
}

