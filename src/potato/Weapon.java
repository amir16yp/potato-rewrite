package potato;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Weapon {
    private Textures textures;
    private int currentFrame;
    private long lastFrameTime;
    private int frameDelay; // Delay between frames in milliseconds
    private int bobOffset;
    private int bobDirection = 1; // 1 = up, -1 = down
    private int bobSpeed = 2; // Speed of bobbing effect
    private int bobRange = 5; // Range of bobbing effect

    public Weapon(Textures gunSprites, int frameDelay) {
        this.textures = textures;
        this.frameDelay = frameDelay;
        this.currentFrame = 0;
        this.lastFrameTime = System.currentTimeMillis();
    }

    public void update() {
        // Handle frame update for animation
        if (System.currentTimeMillis() - Game.GAMELOOP.getDeltaTimeMillis() > frameDelay) {
            currentFrame = (currentFrame + 1) % textures.getTileCount();
            lastFrameTime = System.currentTimeMillis();
        }

        // Handle bobbing effect
        bobOffset += bobSpeed * bobDirection;
        if (Math.abs(bobOffset) >= bobRange) {
            bobDirection *= -1;
        }
    }

    public void render(Graphics2D g, int x, int y) {
        // Get the current frame image from Textures
        BufferedImage frame = textures.getTile(currentFrame);

        // Draw the frame with the bobbing offset applied
        if (frame != null) {
            g.drawImage(frame, x, y + bobOffset, null);
        }
    }
}
