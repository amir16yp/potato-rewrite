package potato;

public enum GameProperty {
    // Display properties
    INTERNAL_WIDTH("display.internal.width", 256),
    INTERNAL_HEIGHT("display.internal.height", 240),
    DEFAULT_FOV("display.fov.default", 60),
    WALL_HEIGHT("display.wall.height", 120),

    // Weapon properties - Pistol
    PISTOL_DAMAGE("weapon.pistol.damage", 10.0),
    PISTOL_COOLDOWN("weapon.pistol.cooldown", 500L),
    PISTOL_AMMO_MAX("weapon.pistol.ammo.max", 30),
    PISTOL_FRAME_DELAY("weapon.pistol.frame.delay", 100),
    PISTOL_SCALE("weapon.pistol.scale", 2),
    PISTOL_BULLET_TYPE("weapon.pistol.bullet.type", 3),
    PISTOL_SOUND("weapon.pistol.sound", "SHOOT1"),
    // Weapon properties - Shotgun
    SHOTGUN_DAMAGE("weapon.shotgun.damage", 30.0),
    SHOTGUN_COOLDOWN("weapon.shotgun.cooldown", 1000L),
    SHOTGUN_AMMO_MAX("weapon.shotgun.ammo.max", 30),
    SHOTGUN_FRAME_DELAY("weapon.shotgun.frame.delay", 100),
    SHOTGUN_SCALE("weapon.shotgun.scale", 2),
    SHOTGUN_BULLET_TYPE("weapon.shotgun.bullet.type", 1),
    SHOTGUN_SOUND("weapon.shotgun.sound", "SHOOT3"),
    // Weapon properties - SMG
    SMG_DAMAGE("weapon.smg.damage", 15.0),
    SMG_COOLDOWN("weapon.smg.cooldown", 150L),
    SMG_AMMO_MAX("weapon.smg.ammo.max", 30),
    SMG_FRAME_DELAY("weapon.smg.frame.delay", 25),
    SMG_SCALE("weapon.smg.scale", 2),
    SMG_BULLET_TYPE("weapon.smg.bullet.type", 2),
    SMG_SOUND("weapon.smg.sound", "SHOOT2"),
    // Entity properties
    PLAYER_MOVE_SPEED("entity.player.move.speed", 2.0),
    PLAYER_ROTATE_SPEED("entity.player.rotate.speed", Math.PI / 2),
    PLAYER_MAX_HEALTH("entity.player.health.max", 100.0),
    PLAYER_COLLISION_RADIUS("entity.player.collision.radius", 0.2),

    DEMON_MOVE_SPEED("entity.demon.move.speed", 2.0),
    DEMON_ATTACK_RANGE("entity.demon.attack.range", 1.5),
    DEMON_ATTACK_DAMAGE("entity.demon.attack.damage", 7.5),
    DEMON_DETECTION_RANGE("entity.demon.detection.range", 10.0),
    DEMON_ATTACK_COOLDOWN("entity.demon.attack.cooldown", 500.0),
    DEMON_HEALTH("entity.demon.health.max", 100.0),
    DEMON_COLLISION_RADIUS("entity.demon.collision.radius", 0.5),
    DEMON_SCALE("entity.demon.scale", 1.0),
    DEMON_ROTATE_SPEED("entity.demon.rotate.speed", Math.PI / 2),

    // Animation properties - Demon
    DEMON_ANIM_IDLE_FRAME("entity.demon.anim.idle.frame", 1),
    DEMON_ANIM_IDLE_COUNT("entity.demon.anim.idle.count", 1),
    DEMON_ANIM_IDLE_DELAY("entity.demon.anim.idle.delay", 100),
    DEMON_ANIM_WALK_FRAME("entity.demon.anim.walk.frame", 1),
    DEMON_ANIM_WALK_COUNT("entity.demon.anim.walk.count", 3),
    DEMON_ANIM_WALK_DELAY("entity.demon.anim.walk.delay", 100),
    DEMON_ANIM_ATTACK_FRAME("entity.demon.anim.attack.frame", 4),
    DEMON_ANIM_ATTACK_COUNT("entity.demon.anim.attack.count", 3),
    DEMON_ANIM_ATTACK_DELAY("entity.demon.anim.attack.delay", 100),
    DEMON_PROJECTILE_SPEED("entity.demon.projectile.speed", 3.0),
    DEMON_PROJECTILE_TYPE("entity.demon.projectile.type", 2),
    DEBUG_SHOW_FPS("debug.show.fps", false),

    MAX_SOUND_DISTANCE("sound.max.distance", 20.0);

    private final String key;
    private final Object defaultValue;  // Changed to Object to accommodate both Number and Boolean

    // Constructor to handle both Number and Boolean types
    GameProperty(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
