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
    private Weapon currentWeapon = new Weapon(new Textures("/potato/sprites/gun/shotgun.png", 48, 48), 50, 2000);

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

        // Forward/Backward movement
        if (Game.GAME.isKeyPressed(KeyEvent.VK_W)) {
            double nextX = x + dirX * actualMoveSpeed;
            double nextY = y + dirY * actualMoveSpeed;
            tryMove(nextX, nextY);
        }
        if (Game.GAME.isKeyPressed(KeyEvent.VK_S)) {
            double nextX = x - dirX * actualMoveSpeed;
            double nextY = y - dirY * actualMoveSpeed;
            tryMove(nextX, nextY);
        }

        // Strafe movement
        if (Game.GAME.isKeyPressed(KeyEvent.VK_A)) {
            double nextX = x - strafeX * actualMoveSpeed;
            double nextY = y - strafeY * actualMoveSpeed;
            tryMove(nextX, nextY);
        }
        if (Game.GAME.isKeyPressed(KeyEvent.VK_D)) {
            double nextX = x + strafeX * actualMoveSpeed;
            double nextY = y + strafeY * actualMoveSpeed;
            tryMove(nextX, nextY);

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
