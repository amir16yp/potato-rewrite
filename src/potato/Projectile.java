package potato;

import potato.entities.Entity;
import potato.entities.PlayerEntity;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Projectile extends Entity {
    private BufferedImage sprite;
    private double speed;
    private double x;
    private double y;
    private double angle;
    private boolean dead;
    private static final double COLLISION_RADIUS = 0.2;

    private static final Textures PROJECTILE_TEXUTRES = new Textures("/potato/sprites/gun/boolet.png", 32, 32);

    public static Projectile fireProjectile(Entity shooter, int textureId, double speed, double x, double y, double angle) {
        BufferedImage projectileTexture = PROJECTILE_TEXUTRES.getTile(textureId);
        Projectile projectile = new Projectile(shooter, projectileTexture, speed, x, y, angle);
        Game.RAYCASTER.currentLevel.addEntity(projectile);
        return projectile;
    }

    public Projectile(Entity shooter, BufferedImage sprite, double speed, double x, double y, double angle) {
        super(x, y, angle, speed, 0, 1, 1);
        this.sprite = sprite;
        this.speed = speed;
        this.x = x;
        this.y = y;
        // Store the initial firing angle - this is crucial
        this.angle = angle;
        this.dead = false;
    }

    @Override
    public void update() {
        double deltaTime = Game.GAMELOOP.getDeltaTime();
        // Movement is now based on the initial firing angle
        double dx = Math.cos(angle) * speed * deltaTime;
        double dy = Math.sin(angle) * speed * deltaTime;
        
        x += dx;
        y += dy;

        if (Game.RAYCASTER.currentLevel.isWall(x, y)) {
            dead = true;
        }
    }
    @Override
    public void render(Graphics2D g) {
        if (sprite != null) {
            PlayerEntity player = PlayerEntity.getPlayer();

            // Calculate relative position to player
            double relativeX = x - player.getX();
            double relativeY = y - player.getY();

            // Transform coordinates based on player's view angle
            double playerAngle = player.getAngle();
            double transformX = relativeX * Math.cos(-playerAngle) - relativeY * Math.sin(-playerAngle);
            double transformY = relativeX * Math.sin(-playerAngle) + relativeY * Math.cos(-playerAngle);

            // Don't render if behind player
            if (transformX <= 0) return;

            // Calculate distance for depth scaling
            double distance = Math.sqrt(transformX * transformX + transformY * transformY);

            // Calculate sprite dimensions with proper perspective scaling
            // Use WALL_HEIGHT similar to how walls are rendered
            double spriteHeight = (Raycaster.WALL_HEIGHT / distance) * Raycaster.PLANE_DIST;
            double spriteWidth = spriteHeight * (sprite.getWidth() / (double)sprite.getHeight());

            // Calculate screen position with proper perspective transformation
            double screenX = (transformY / transformX) * Raycaster.PLANE_DIST;
            screenX = (Game.INTERNAL_WIDTH / 2) + (screenX * Game.INTERNAL_WIDTH / 2);

            // Calculate vertical position using the same wall drawing logic
            int lineHeight = (int)(spriteHeight);
            int drawStart = -lineHeight / 2 + Game.INTERNAL_HEIGHT / 2;
            if (drawStart < 0) drawStart = 0;

            // Calculate drawing coordinates
            int drawX = (int)(screenX - spriteWidth / 2);

            // Draw the sprite with proper scaling
            g.drawImage(sprite,
                    drawX, drawStart,
                    (int)spriteWidth, (int)spriteHeight,
                    null);
        }
    }


    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public boolean collidesWith(Entity other) {
        double dx = other.getX() - this.x;
        double dy = other.getY() - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance < COLLISION_RADIUS;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}