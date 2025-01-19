package potato.entities;

import potato.Game;
import potato.Wall;

import java.awt.*;

// Entity.java
public abstract class Entity {
    protected double x;
    protected double y;
    protected double angle; // In radians
    protected double moveSpeed;
    protected double rotateSpeed;
    protected double health = 100.0;
    protected double maxHealth = 100.0;
    protected double radius;  // For collision detection
    protected boolean isDead;
    protected boolean canTakeDamage = true;
    protected static final double COLLISION_BUFFER = 0.2;

    public Entity(double x, double y, double angle, double moveSpeed, double rotateSpeed, double maxHealth, double radius) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.moveSpeed = moveSpeed;
        this.rotateSpeed = rotateSpeed;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.radius = radius;
        this.isDead = false;
    }

    public abstract void render(Graphics2D graphics2d);

    public abstract void update();

    protected boolean isInsideWall() {
        return Game.LEVEL_GENERATOR.generatedLevel.isWall(x, y);
    }

    protected boolean wouldCollide(double newX, double newY) {
        // Check the actual position
        if (Game.LEVEL_GENERATOR.generatedLevel.isWall(newX, newY)) {
            return true;
        }

        // Check collision buffer points around the entity
        double[][] checkPoints = {
                {newX + radius + COLLISION_BUFFER, newY},
                {newX - radius - COLLISION_BUFFER, newY},
                {newX, newY + radius + COLLISION_BUFFER},
                {newX, newY - radius - COLLISION_BUFFER},
                {newX + (radius + COLLISION_BUFFER) * 0.707, newY + (radius + COLLISION_BUFFER) * 0.707},
                {newX - (radius + COLLISION_BUFFER) * 0.707, newY + (radius + COLLISION_BUFFER) * 0.707},
                {newX + (radius + COLLISION_BUFFER) * 0.707, newY - (radius + COLLISION_BUFFER) * 0.707},
                {newX - (radius + COLLISION_BUFFER) * 0.707, newY - (radius + COLLISION_BUFFER) * 0.707}
        };

        for (double[] point : checkPoints) {
            if (Game.LEVEL_GENERATOR.generatedLevel.isWall(point[0], point[1])) {
                return true;
            }
        }

        return false;
    }

    protected void tryMove(double newX, double newY) {
        // Try the full movement first
        if (!wouldCollide(newX, newY)) {
            x = newX;
            y = newY;
            return;
        }

        // If full movement fails, try moving along each axis separately
        if (!wouldCollide(newX, y)) {
            x = newX;
        }
        if (!wouldCollide(x, newY)) {
            y = newY;
        }
    }

    public void takeDamage(double damage) {
        if (!canTakeDamage)
        {
            return;
        }
        health -= damage;
        if (health <= 0) {
            health = 0;
            isDead = true;
        }
    }

    public void setCanTakeDamage(boolean canTakeDamage)
    {
        this.canTakeDamage = canTakeDamage;
    }

    public void heal(double amount) {
        health = Math.min(health + amount, maxHealth);
    }

    public void setPosition(int[] position)
    {
        this.x = position[0];
        this.y = position[1];
    }

    public void setPosition(double x, double y)
    {
        this.x = x;
        this.y =y;
    }

    public void setPosition(Point point)
    {
        this.x = point.getX();
        this.y = point.getY();
    }

    /*
    TODO: add a function that returns a boolean if the player can see another entity or not
     */

    protected double getAngleToEntity(Entity target) {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        return Math.atan2(dy, dx);
    }

    public double getDistance(Entity other) {
        double dx = other.getX() - x;
        double dy = other.getY() - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean canSee(Entity target) {
        double dx = target.getX() - x;
        double dy = target.getY() - y;
        double distance = getDistance(target);

        double stepSize = 0.1;
        double steps = distance / stepSize;

        for (int i = 1; i < steps; i++) {
            double checkX = x + (dx / steps) * i;
            double checkY = y + (dy / steps) * i;

            if (Game.LEVEL_GENERATOR.generatedLevel.isWall(checkX, checkY)) {
                return false;
            }
        }

        return true;
    }

    public Wall getWallInFront(double maxDistance) {
        double stepSize = 0.1;  // Small steps for accuracy

        for(double dist = 0; dist <= maxDistance; dist += stepSize) {
            double checkX = x + Math.cos(angle) * dist;
            double checkY = y + Math.sin(angle) * dist;

            int mapX = (int)checkX;
            int mapY = (int)checkY;

            // Check if coordinates are within map bounds
            if (mapX < 0 || mapX >= Game.LEVEL_GENERATOR.generatedLevel.getMapWidth() ||
                    mapY < 0 || mapY >= Game.LEVEL_GENERATOR.generatedLevel.getMapHeight()) {
                return null;
            }

            Wall wall = Game.LEVEL_GENERATOR.generatedLevel.getWall(mapX, mapY);
            if (wall != null) {
                return wall;
            }
        }

        return null;
    }


    // Getters and setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public double getHealth() { return health; }
    public double getMaxHealth() { return maxHealth; }

    public double getRadius() { return radius; }
    public boolean isDead() { return isDead; }
    public void setMoveSpeed(double speed) { this.moveSpeed = speed; }
    public void setRotateSpeed(double speed) { this.rotateSpeed = speed; }
}
