package potato.entities;

import potato.Game;
import potato.Projectile;
import potato.Textures;

public class DemonEntity extends EnemyEntity {
    private static final Textures TEXTURES = new Textures("/potato/sprites/entity/demon.png", 168, 168);
    private double attackTimer = 0;
    private boolean canAttack = true;

    public DemonEntity(double x, double y) {
        super(x, y, 0, 2.0, Math.PI/2, 100, 0.5, TEXTURES, 1.0);;

        setStat("attackRange", 1.5);
        setStat("attackDamage", 7.5);
        setStat("detectionRange", 10.0);
        setStat("attackCooldown", 500.0); // 1000ms = 1 second

        defineAnimation("idle", 1, 1, 100);
        defineAnimation("walking", 1, 3, 100);
        defineAnimation("attacking", 4, 3, 100, false, () -> {
            Projectile.fireProjectile(this, 2, 3, this.getX(), this.getY(), getAngleToEntity(PlayerEntity.getPlayer()), getStat("attackDamage", 0));
            setState("walking");
            canAttack = false;
            attackTimer = 0;
        });


        setState("idle");
    }

    @Override
    public void update() {
        super.update();
        PlayerEntity player = PlayerEntity.getPlayer();

        if (this.getDistance(player) >= getStat("detectionRange", 20.0))
        {
            canAttack = false;
        }

        if (!canAttack) {
            attackTimer += Game.GAMELOOP.getDeltaTimeMillis();
            if (attackTimer >= getStat("attackCooldown", 1000.0)) {
                canAttack = true;
            }
        }

        if (canSee(player) && canAttack) {
            setState("attacking");
        }
    }
}