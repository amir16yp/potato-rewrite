package potato;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SpriteEntity extends Entity {
    protected BufferedImage sprite;
    protected double scale;
    protected boolean flipHorizontal;

    public SpriteEntity(double x, double y, double angle, double moveSpeed, double rotateSpeed,
                        double maxHealth, double radius, BufferedImage sprite, double scale) {
        super(x, y, angle, moveSpeed, rotateSpeed, maxHealth, radius);
        this.sprite = sprite;
        this.scale = scale;
        this.flipHorizontal = false;
    }

    @Override
    public void update(double deltaTime) {
        // Override with specific behavior in subclasses if needed
    }

    @Override
    public void render(Graphics2D g) {
        if (sprite == null) return;

        // Get distance to player for scaling
        PlayerEntity player = PlayerEntity.getPlayer();
        double dx = x - player.getX();
        double dy = y - player.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Calculate angle between player and sprite
        double spriteAngle = Math.atan2(dy, dx);

        // Adjust for player's viewing angle
        double relativeAngle = spriteAngle - player.getAngle();

        // Normalize angle to [-PI, PI]
        while (relativeAngle > Math.PI) relativeAngle -= 2 * Math.PI;
        while (relativeAngle < -Math.PI) relativeAngle += 2 * Math.PI;

        // Don't render if behind player (with some margin for peripheral vision)
        if (Math.abs(relativeAngle) > Math.PI * 0.75) {
            return;
        }

        // Calculate screen position
        double projectedX = Game.INTERNAL_WIDTH / 2 * (1 + relativeAngle / (Raycaster.FOV / 2));

        // Calculate sprite size based on distance
        double size = (Game.INTERNAL_HEIGHT * scale) / distance;

        // Calculate sprite dimensions maintaining aspect ratio
        int spriteWidth = (int)(size * (sprite.getWidth() / (double)sprite.getHeight()));
        int spriteHeight = (int)size;

        // Calculate screen coordinates
        int screenX = (int)(projectedX - spriteWidth / 2);
        int screenY = (int)((Game.INTERNAL_HEIGHT - spriteHeight) / 2);

        // Only render if on screen
        if (screenX + spriteWidth >= 0 && screenX <= Game.INTERNAL_WIDTH) {
            // Handle horizontal flipping if needed
            if (flipHorizontal) {
                g.scale(-1, 1);
                g.drawImage(sprite, -screenX - spriteWidth, screenY, spriteWidth, spriteHeight, null);
                g.scale(-1, 1);
            } else {
                g.drawImage(sprite, screenX, screenY, spriteWidth, spriteHeight, null);
            }
        }
    }

    // Utility methods
    public void setSprite(BufferedImage newSprite) {
        this.sprite = newSprite;
    }

    public void setFlipHorizontal(boolean flip) {
        this.flipHorizontal = flip;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getScale() {
        return scale;
    }

    public BufferedImage getSprite() {
        return sprite;
    }
}