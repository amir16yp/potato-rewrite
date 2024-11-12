package potato;

import java.awt.*;
import java.awt.event.KeyEvent;

// PlayerEntity.java
public class PlayerEntity extends Entity {
    private static final double DEFAULT_MOVE_SPEED = 2.0;
    private static final double DEFAULT_ROTATE_SPEED = Math.PI;
    private static final double DEFAULT_MAX_HEALTH = 100.0;
    private static final double DEFAULT_RADIUS = 0.2;

    private static PlayerEntity player;
    private Weapon currentWeapon = Weapons.PISTOL;

    public PlayerEntity(double x, double y, double angle) {
        super(x, y, angle, DEFAULT_MOVE_SPEED, DEFAULT_ROTATE_SPEED, DEFAULT_MAX_HEALTH, DEFAULT_RADIUS);
    }

    public static PlayerEntity getPlayer()
    {
        if (player == null)
        {
            player = new PlayerEntity(1.5, 1.5, 0.0);
        }
        return player;
    }

    @Override
    public void render(Graphics2D graphics2d) {
        return;
    }

    @Override
    public void update(double deltaTime) {
        double actualMoveSpeed = moveSpeed * deltaTime;
        double actualRotateSpeed = rotateSpeed * deltaTime;

        // Store the original position in case we need to revert
        double originalX = x;
        double originalY = y;

        // Calculate direction vectors
        double dirX = Math.cos(angle);
        double dirY = Math.sin(angle);
        // Calculate perpendicular vectors for strafing
        double strafeX = -dirY;
        double strafeY = dirX;

// Calculate the desired movement vector
        double moveX = 0, moveY = 0;

        if (Game.GAME.isKeyPressed(KeyEvent.VK_W)) {
            moveX += dirX;
            moveY += dirY;
        }
        if (Game.GAME.isKeyPressed(KeyEvent.VK_S)) {
            moveX -= dirX;
            moveY -= dirY;
        }
        if (Game.GAME.isKeyPressed(KeyEvent.VK_A)) {
            moveX -= strafeX;
            moveY -= strafeY;
        }
        if (Game.GAME.isKeyPressed(KeyEvent.VK_D)) {
            moveX += strafeX;
            moveY += strafeY;
        }

// Normalize the movement vector if we're moving
        double length = Math.sqrt(moveX * moveX + moveY * moveY);
        if (length > 0) {
            moveX = moveX / length * actualMoveSpeed;
            moveY = moveY / length * actualMoveSpeed;
            tryMove(x + moveX, y + moveY);
        }

        // Rotation
        if (Game.GAME.isKeyPressed('Q')) {
            angle -= actualRotateSpeed;
        }
        if (Game.GAME.isKeyPressed('E')) {
            angle += actualRotateSpeed;
        }

        // If we're somehow inside a wall, revert to original position
        if (isInsideWall()) {
            x = originalX;
            y = originalY;
        }

        // Handle death
        if (isDead) {
            // Implement death behavior (game over, respawn, etc.)
        }
    }

    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }

    public void setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
    }
}
