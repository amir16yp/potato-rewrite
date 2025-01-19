package potato.entities;

import potato.Game;
import potato.Raycaster;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PickupEntity extends Entity{

    private BufferedImage sprite;
    private Runnable pickupAction;
    private double scale = 1;

    public PickupEntity(double x, double y, int pickupTextureID, Runnable pickupAction) {
        super(x, y, 0, 0, 0, 1, 1);
        this.sprite = sprite;
        this.pickupAction = pickupAction;
    }

    @Override
    public void render(Graphics2D g) {
        if (sprite == null) return;

        PlayerEntity player = PlayerEntity.getPlayer();

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
        //double halfFOV = Math.toRadians(Raycaster.FOV) / 2;
        //if (Math.abs(relativeAngle) > halfFOV * 1.2) {
        //    return;
        //}

        // Calculate screen x position
        double screenX = (double) Game.INTERNAL_WIDTH / 2 + (Game.INTERNAL_WIDTH * relativeAngle / Math.toRadians(Raycaster.FOV));

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

        g.drawImage(sprite,
                drawX, drawY,
                (int)(drawX + spriteWidth), (int)(drawY + spriteHeight),
                0, 0,
                sprite.getWidth(), sprite.getHeight(),
                null);
    }

    @Override
    public void update() {

    }
}
