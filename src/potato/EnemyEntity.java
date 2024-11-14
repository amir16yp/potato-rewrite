package potato;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class EnemyEntity extends Entity {
    // Animation data structure
    protected static class AnimationData {
        final int startTileId;
        final int frameCount;
        final double frameDuration;

        public AnimationData(int startTileId, int frameCount, double frameDuration) {
            this.startTileId = startTileId;
            this.frameCount = frameCount;
            this.frameDuration = frameDuration;
        }
    }

    // Core properties
    protected final Textures textures;
    protected double scale;
    protected boolean flipHorizontal;

    // Animation state
    protected String currentState;
    protected int currentFrame;
    protected double animationTimer;

    // Animation definitions
    protected final Map<String, AnimationData> animations;

    // Enemy properties
    protected final Map<String, Double> stats;

    public EnemyEntity(double x, double y, double angle, double moveSpeed, double rotateSpeed,
                       double maxHealth, double radius, Textures textures, double scale) {
        super(x, y, angle, moveSpeed, rotateSpeed, maxHealth, radius);

        this.textures = textures;
        this.scale = scale;
        this.flipHorizontal = false;

        this.currentFrame = 0;
        this.animationTimer = 0;

        // Initialize maps
        this.animations = new HashMap<>();
        this.stats = new HashMap<>();

        // Set default state
        this.currentState = "idle";
    }

    protected void defineAnimation(String state, int startTileId, int frameCount, double frameDuration) {
        animations.put(state, new AnimationData(startTileId, frameCount, frameDuration));
    }

    protected void setStat(String statName, double value) {
        stats.put(statName, value);
    }

    protected double getStat(String statName, double defaultValue) {
        return stats.getOrDefault(statName, defaultValue);
    }

    public void setState(String newState) {
        if (animations.containsKey(newState) && !newState.equals(currentState)) {
            currentState = newState;
            currentFrame = 0;
            animationTimer = 0;
        }
    }

    @Override
    public void update() {
        // Update animation
        double deltaTime = Game.GAMELOOP.getDeltaTimeMillis();
        AnimationData currentAnim = animations.get(currentState);
        if (currentAnim != null) {
            animationTimer += deltaTime;
            if (animationTimer >= currentAnim.frameDuration) {
                animationTimer -= currentAnim.frameDuration;
                currentFrame = (currentFrame + 1) % currentAnim.frameCount;
            }
        }
    }


    @Override
    public void render(Graphics2D g) {
        // Get current animation data
        AnimationData currentAnim = animations.get(currentState);
        if (currentAnim == null) return;

        // Calculate current tile ID
        int tileId = currentAnim.startTileId + currentFrame;
        BufferedImage sprite = textures.getTile(tileId);
        if (sprite == null) return;

        PlayerEntity player = Game.RAYCASTER.currentLevel.getPlayer();

        // Calculate vector from player to sprite
        double dx = x - player.getX();
        double dy = y - player.getY();

        // Calculate direct distance to sprite
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Calculate sprite's angle relative to player's view direction
        double spriteAngle = Math.atan2(dy, dx);
        double playerAngle = player.getAngle();
        double relativeAngle = spriteAngle - playerAngle;

        // Normalize angle to [-PI, PI]
        while (relativeAngle > Math.PI) relativeAngle -= 2 * Math.PI;
        while (relativeAngle < -Math.PI) relativeAngle += 2 * Math.PI;

        // Don't render if outside FOV (with small margin)
        double halfFOV = Math.toRadians(Raycaster.FOV) / 2;
        if (Math.abs(relativeAngle) > halfFOV * 1.2) {
            return;
        }

        // Calculate screen x position
        double screenX = Game.INTERNAL_WIDTH / 2 + (Game.INTERNAL_WIDTH * relativeAngle / Math.toRadians(Raycaster.FOV));

        // Calculate sprite height based on distance (using same scale as walls)
        double spriteHeight = (Raycaster.WALL_HEIGHT / distance) * Raycaster.PLANE_DIST * scale;
        double spriteWidth = spriteHeight * (sprite.getWidth() / (double)sprite.getHeight());

        // Calculate drawing positions
        int drawX = (int)(screenX - spriteWidth / 2);
        int drawY = (int)((Game.INTERNAL_HEIGHT - spriteHeight) / 2);

        // Don't render if completely off screen
        if (drawX + spriteWidth < 0 || drawX >= Game.INTERNAL_WIDTH) {
            return;
        }

        // Draw the sprite
        if (flipHorizontal) {
            g.scale(-1, 1);
            g.drawImage(sprite,
                    -drawX - (int)spriteWidth, drawY,
                    -drawX, (int)(drawY + spriteHeight),
                    0, 0,
                    sprite.getWidth(), sprite.getHeight(),
                    null);
            g.scale(-1, 1);
        } else {
            g.drawImage(sprite,
                    drawX, drawY,
                    (int)(drawX + spriteWidth), (int)(drawY + spriteHeight),
                    0, 0,
                    sprite.getWidth(), sprite.getHeight(),
                    null);
        }
    }
    // Utility methods
    public void setFlipHorizontal(boolean flip) {
        this.flipHorizontal = flip;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    protected boolean isInRange(double range, Entity target) {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distanceSquared = dx * dx + dy * dy;
        return distanceSquared <= range * range;
    }
}