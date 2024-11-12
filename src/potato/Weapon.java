package potato;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Weapon {
    public Textures textures;
    private int currentFrame;
    private long lastFrameTime;
    private int frameDelay; // Delay between frames in milliseconds
    private String weaponName;
    private double bobOffset;  // Use a double for smoother calculations
    private int bobSpeed = 2; // Speed of bobbing effect (controls frequency)
    private int bobRange = 5; // Maximum range of bobbing effect (controls amplitude)
    private long cooldownTime;
    private long lastFireTime;  // Track when we last fired
    private boolean isAnimating = false;
    private int scaleFactor;
    private BufferedImage icon;
    private int ammoAmount = 30;
    private int maxAmmoAmount = 30;

    public Weapon(Textures gunSprites, int frameDelay, long cooldownTime, int scaleFactor, String weaponName, BufferedImage icon) {
        this.textures = gunSprites;
        this.frameDelay = frameDelay;
        this.weaponName = weaponName;
        this.currentFrame = 1; // Start at the first frame
        this.lastFrameTime = System.currentTimeMillis();
        this.cooldownTime = cooldownTime;
        this.lastFireTime = 0;
        this.scaleFactor = scaleFactor;
        this.weaponName = weaponName;
        this.icon = icon;
    }

    public void update() {

        if (PlayerEntity.getPlayer().isMoving())
        {
            long timeElapsed = System.currentTimeMillis() - lastFrameTime;

            bobOffset = Math.sin(timeElapsed * 0.005 * bobSpeed) * bobRange;
        }


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

    public void render(Graphics2D g, int x, int y) {
        // Get the current frame image from Textures
        BufferedImage frame = textures.getTile(currentFrame);

        // Check if the frame is not null
        if (frame != null) {
            // Scale the image size
            double scaledWidth = frame.getWidth() * this.scaleFactor;
            double scaledHeight = frame.getHeight() * this.scaleFactor;

            // Adjust the position to account for scaling (centering the scaled image)
            double adjustedX = x - (scaledWidth - frame.getWidth()) / 2;
            double adjustedY = y + (int) bobOffset - (scaledHeight - frame.getHeight()) / 2;

            // Draw the scaled image with the smooth bobbing offset applied
            g.drawImage(frame.getScaledInstance((int) scaledWidth, (int) scaledHeight, Image.SCALE_SMOOTH),
                    (int) adjustedX, (int) adjustedY, null);
        }
    }

    public boolean canFire()
    {
        if (ammoAmount != 0)
        {
            return System.currentTimeMillis() - lastFireTime >= cooldownTime;
        } else {
            return false;
        }

    }

    public void fire(double x, double y, double angle) {
        if (canFire()) {
            Projectile.fireProjectile(1, 10, x, y, angle);
            this.isAnimating = true;
            lastFireTime = System.currentTimeMillis();
            ammoAmount--;
            if (0 > ammoAmount)
            {
                ammoAmount = 0;
            }
        }
    }

    public int getAmmoAmount() {
        return ammoAmount;
    }

    public void setAmmoAmount(int ammoAmount) {
        this.ammoAmount = ammoAmount;
    }

    public void setMaxAmmoAmount(int maxAmmoAmount) {
        this.maxAmmoAmount = maxAmmoAmount;
    }

    public int getMaxAmmoAmount() {
        return maxAmmoAmount;
    }

    public BufferedImage getIcon() {
        return icon;
    }
}