package potato;

import java.awt.image.BufferedImage;

public class Door extends Wall {
    private static final int DOOR_TYPE = 25;
    private static final Textures DOOR_TEXTURES = new Textures("/potato/assets/sprites/door.png", 16, 16);

    private Level level;
    private int mapX, mapY;
    private int frame = 1;
    private boolean isOpening = false;
    private boolean isFullyOpen = false;  // New flag to track fully open state
    private static final int FRAME_COUNT = 16;
    private static final double FRAME_DURATION = 50;
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
        if (!isOpening && !isFullyOpen) {
            isOpening = true;
            frame = 1;
            frameTimer = 0;
        }
    }

    @Override
    public void update() {
        if (isOpening && !isFullyOpen) {
            frameTimer += Game.GAMELOOP.getDeltaTimeMillis();
            if (frameTimer >= FRAME_DURATION) {
                frameTimer -= FRAME_DURATION;
                frame++;

                if (frame > FRAME_COUNT) {
                    isFullyOpen = true;
                    isOpening = false;
                    if (level != null) {
                        try {
                            Wall[][] map = level.getMap();
                            if (map != null && mapY >= 0 && mapY < map.length
                                    && mapX >= 0 && mapX < map[mapY].length) {
                                map[mapY][mapX] = null;
                            }
                        } catch (Exception e) {
                            // Handle any potential array access issues
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public BufferedImage getCurrentTexture() {
        if (isFullyOpen) {
            return null;  // Return null when fully open
        }
        if (isOpening) {
            int safeFrame = Math.min(frame, FRAME_COUNT);
            return DOOR_TEXTURES.getTile(safeFrame);
        }
        return DEFAULT_TEXTURES.getTile(type);
    }
}   