package potato;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Weapon {
    public Textures textures;
    private int currentFrame;
    private long lastFrameTime;
    private int frameDelay; // Delay between frames in milliseconds
    private double bobOffset;  // Use a double for smoother calculations
    private int bobSpeed = 2; // Speed of bobbing effect (controls frequency)
    private int bobRange = 5; // Maximum range of bobbing effect (controls amplitude)
    private long cooldownTime;
    private long lastFireTime;  // Track when we last fired
    private boolean isAnimating = false;

    public Weapon(Textures gunSprites, int frameDelay, long cooldownTime) {
        this.textures = gunSprites;
        this.frameDelay = frameDelay;
        this.currentFrame = 1; // Start at the first frame
        this.lastFrameTime = System.currentTimeMillis();
        this.cooldownTime = cooldownTime;
        this.lastFireTime = 0;
    }

    public void update() {
        // Smooth bobbing effect using a sine wave
        long timeElapsed = System.currentTimeMillis() - lastFrameTime;

        // Sine wave function for smooth up and down movement
        bobOffset = Math.sin(timeElapsed * 0.005 * bobSpeed) * bobRange;

        if (!isAnimating) {
            currentFrame = 1;  // Reset to the first frame when animation stops
            return;
        }

        // Handle frame updates
        if (System.currentTimeMillis() - lastFrameTime > frameDelay) {
            currentFrame++;

            // Check if we've reached the last frame
            if (currentFrame >= textures.getTileCount()) {
                isAnimating = false;  // Stop animating
                currentFrame = 1;     // Reset to the first frame
            }

            lastFrameTime = System.currentTimeMillis();
        }
    }

    public void render(Graphics2D g, int x, int y, int scale) {
        // Get the current frame image from Textures
        BufferedImage frame = textures.getTile(currentFrame);

        // Check if the frame is not null
        if (frame != null) {
            // Scale the image size
            int scaledWidth = frame.getWidth() * scale;
            int scaledHeight = frame.getHeight() * scale;

            // Adjust the position to account for scaling (centering the scaled image)
            int adjustedX = x - (scaledWidth - frame.getWidth()) / 2;
            int adjustedY = y + (int) bobOffset - (scaledHeight - frame.getHeight()) / 2;

            // Draw the scaled image with the smooth bobbing offset applied
            g.drawImage(frame.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH),
                    adjustedX, adjustedY, null);
        }
    }

    public boolean canFire() {
        return System.currentTimeMillis() - lastFireTime >= cooldownTime;
    }

    public void fire(double x, double y, double angle) {
        if (canFire()) {
            this.isAnimating = true;
            Projectile.fireProjectile(1, 5, x, y, angle);
            lastFireTime = System.currentTimeMillis();
        }
    }
}