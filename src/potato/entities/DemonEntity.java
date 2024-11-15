package potato.entities;

import potato.*;

public class DemonEntity extends EnemyEntity {
    private static final Textures TEXTURES = new Textures("/potato/assets/sprites/entity/demon.png", 168, 168);
    private double attackTimer = 0;
    private boolean canAttack = true;
    private static final ConfigManager config = ConfigManager.get();

    public DemonEntity(double x, double y) {
        super(x, y, 0,
                config.getDouble(GameProperty.DEMON_MOVE_SPEED),
                config.getDouble(GameProperty.DEMON_ROTATE_SPEED),
                config.getDouble(GameProperty.DEMON_HEALTH),
                config.getDouble(GameProperty.DEMON_COLLISION_RADIUS),
                TEXTURES,
                config.getDouble(GameProperty.DEMON_SCALE));

        // Initialize stats from config
        setStat("attackRange", config.getDouble(GameProperty.DEMON_ATTACK_RANGE));
        setStat("attackDamage", config.getDouble(GameProperty.DEMON_ATTACK_DAMAGE));
        setStat("detectionRange", config.getDouble(GameProperty.DEMON_DETECTION_RANGE));
        setStat("attackCooldown", config.getDouble(GameProperty.DEMON_ATTACK_COOLDOWN));

        // Define animations from config
        defineAnimation("idle",
                config.getInt(GameProperty.DEMON_ANIM_IDLE_FRAME),
                config.getInt(GameProperty.DEMON_ANIM_IDLE_COUNT),
                config.getInt(GameProperty.DEMON_ANIM_IDLE_DELAY));

        defineAnimation("walking",
                config.getInt(GameProperty.DEMON_ANIM_WALK_FRAME),
                config.getInt(GameProperty.DEMON_ANIM_WALK_COUNT),
                config.getInt(GameProperty.DEMON_ANIM_WALK_DELAY));

        defineAnimation("attacking",
                config.getInt(GameProperty.DEMON_ANIM_ATTACK_FRAME),
                config.getInt(GameProperty.DEMON_ANIM_ATTACK_COUNT),
                config.getInt(GameProperty.DEMON_ANIM_ATTACK_DELAY),
                false,
                () -> {
                    Projectile.fireProjectile(
                            this,
                            config.getInt(GameProperty.DEMON_PROJECTILE_TYPE), // TODO: Add to config
                            config.getDouble(GameProperty.DEMON_PROJECTILE_SPEED),
                            this.getX(),
                            this.getY(),
                            getAngleToEntity(PlayerEntity.getPlayer()),
                            getStat("attackDamage", 0),
                            Weapons.SMG.getSoundName()
                    );
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

        if (this.getDistance(player) >= getStat("detectionRange",
                config.getDouble(GameProperty.DEMON_DETECTION_RANGE))) {
            canAttack = false;
        }

        if (!canAttack) {
            attackTimer += Game.GAMELOOP.getDeltaTimeMillis();
            if (attackTimer >= getStat("attackCooldown",
                    config.getDouble(GameProperty.DEMON_ATTACK_COOLDOWN))) {
                canAttack = true;
            }
        }

        if (canSee(player) && canAttack) {
            setState("attacking");
        }
    }
}