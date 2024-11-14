package potato;

import potato.entities.EnemyEntity;
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
    private double scale = 1;
    private static final double COLLISION_RADIUS = 0.2;
    private Entity shooter;
    private double damage;
    private static final Textures PROJECTILE_TEXUTRES = new Textures("/potato/sprites/gun/boolet.png", 32, 32);

    public static Projectile fireProjectile(Entity shooter, int textureId, double speed, double x, double y, double angle, double damage) {
        BufferedImage projectileTexture = PROJECTILE_TEXUTRES.getTile(textureId);
        Projectile projectile = new Projectile(shooter, projectileTexture, speed, x, y, angle, damage);
        if (shooter instanceof PlayerEntity)
        {
            Game.SOUND_MANAGER.playSoundEffect("SHOOT1");
        } else {
            Game.SOUND_MANAGER.playSoundEffect("SHOOT1", shooter);
        }
        Game.RAYCASTER.currentLevel.addEntity(projectile);

        return projectile;
    }

    public Projectile(Entity shooter, BufferedImage sprite, double speed, double x, double y, double angle, double damage) {
        super(x, y, angle, speed, 0, 1, 1);
        this.sprite = sprite;
        this.speed = speed;
        this.x = x;
        this.y = y;
        this.shooter = shooter;
        // Store the initial firing angle - this is crucial
        this.angle = angle;
        this.dead = false;
        this.damage = damage;
    }

    @Override
    public void update() {
        double deltaTime = Game.GAMELOOP.getDeltaTime();
        double dx = Math.cos(angle) * speed * deltaTime;
        double dy = Math.sin(angle) * speed * deltaTime;

        x += dx;
        y += dy;

        if (Game.RAYCASTER.currentLevel.isWall(x, y)) {
            dead = true;
            return;
        }

        // Check for enemy hits when player shoots
        if (shooter instanceof PlayerEntity) {
            for (Entity entity : Game.RAYCASTER.currentLevel.getEntities()) {
                if (entity instanceof EnemyEntity && collidesWith(entity)) {
                    entity.takeDamage(damage);
                    dead = true;
                    return;
                }
            }
        }
        // Check for player hits when enemy shoots
        else if (shooter instanceof EnemyEntity) {
            PlayerEntity player = PlayerEntity.getPlayer();
            if (collidesWith(player)) {
                player.takeDamage(damage);
                dead = true;
                // return; // idk if this is useless or not. keeping it as a comment
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
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