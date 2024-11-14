package potato.entities;

import potato.Game;
import potato.Projectile;
import potato.Textures;

import java.awt.event.KeyEvent;

public class DemonEntity extends EnemyEntity {

    /*
    TODO: add ranged entity class for common methods for ranged attacking entities
    TODO: the same for melee
     */

    public DemonEntity(double x, double y) {
        super(x, y, 0, 2.0, Math.PI/2, 100, 0.5, new Textures("/potato/sprites/entity/demon.png", 168, 168), 1.0);

        defineAnimation("idle", 1, 1, 100);
        defineAnimation("walking", 1, 3, 100);
        defineAnimation("attacking", 4, 3, 100, false, () -> {
            Projectile.fireProjectile(this,2, 3, this.getX(), this.getY(), getAngleToEntity(PlayerEntity.getPlayer()));
            setState("walking");

        });
        // Define stats
        setStat("attackRange", 1.5);
        setStat("attackDamage", 25.0);
        setStat("detectionRange", 10.0);
        setStat("attackCooldown", 1.0);
        setState("idle");
    }

    @Override
    public void update() {
        super.update();
        if (Game.GAME.isKeyPressed(KeyEvent.VK_X))
        {
            this.setState("attacking");
        }
    }
}