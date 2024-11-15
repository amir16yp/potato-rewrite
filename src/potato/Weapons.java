package potato;

public class Weapons {
    // Constants for texture paths
    private static final class TexturePaths {
        static final String BASE_PATH = "/potato/assets/sprites/gun/";
        static final String PISTOL_SPRITE = BASE_PATH + "pistol.png";
        static final String PISTOL_ICON = BASE_PATH + "pistol-icon.png";
        static final String SHOTGUN_SPRITE = BASE_PATH + "shotgun.png";
        static final String SHOTGUN_ICON = BASE_PATH + "shotgun-icon.png";
        static final String SMG_SPRITE = BASE_PATH + "SMG.png";
        static final String SMG_ICON = BASE_PATH + "SMG-icon.png";
    }

    // Constants for texture dimensions
    private static final int WEAPON_SPRITE_WIDTH = 48;
    private static final int WEAPON_SPRITE_HEIGHT = 48;

    public static final Weapon PISTOL = new Weapon(
            new Textures(TexturePaths.PISTOL_SPRITE, WEAPON_SPRITE_WIDTH, WEAPON_SPRITE_HEIGHT),
            ConfigManager.get().getInt(GameProperty.PISTOL_FRAME_DELAY),
            ConfigManager.get().getLong(GameProperty.PISTOL_COOLDOWN),
            ConfigManager.get().getInt(GameProperty.PISTOL_SCALE),
            "Pistol",
            Utils.loadImage(TexturePaths.PISTOL_ICON),
            ConfigManager.get().getInt(GameProperty.PISTOL_BULLET_TYPE),
            ConfigManager.get().getDouble(GameProperty.PISTOL_DAMAGE)
    );

    public static final Weapon SHOTGUN = new Weapon(
            new Textures(TexturePaths.SHOTGUN_SPRITE, WEAPON_SPRITE_WIDTH, WEAPON_SPRITE_HEIGHT),
            ConfigManager.get().getInt(GameProperty.SHOTGUN_FRAME_DELAY),
            ConfigManager.get().getLong(GameProperty.SHOTGUN_COOLDOWN),
            ConfigManager.get().getInt(GameProperty.SHOTGUN_SCALE),
            "Shotgun",
            Utils.loadImage(TexturePaths.SHOTGUN_ICON),
            ConfigManager.get().getInt(GameProperty.SHOTGUN_BULLET_TYPE),
            ConfigManager.get().getDouble(GameProperty.SHOTGUN_DAMAGE)
    );

    public static final Weapon SMG = new Weapon(
            new Textures(TexturePaths.SMG_SPRITE, WEAPON_SPRITE_WIDTH, WEAPON_SPRITE_HEIGHT),
            ConfigManager.get().getInt(GameProperty.SMG_FRAME_DELAY),
            ConfigManager.get().getLong(GameProperty.SMG_COOLDOWN),
            ConfigManager.get().getInt(GameProperty.SMG_SCALE),
            "SMG",
            Utils.loadImage(TexturePaths.SMG_ICON),
            ConfigManager.get().getInt(GameProperty.SMG_BULLET_TYPE),
            ConfigManager.get().getDouble(GameProperty.SMG_DAMAGE)
    );

    static {
        ConfigManager config = ConfigManager.get();
        PISTOL.setMaxAmmoAmount(config.getInt(GameProperty.PISTOL_AMMO_MAX));
        SHOTGUN.setMaxAmmoAmount(config.getInt(GameProperty.SHOTGUN_AMMO_MAX));
        SMG.setMaxAmmoAmount(config.getInt(GameProperty.SMG_AMMO_MAX));
    }

    // Prevent instantiation
    private Weapons() {}
}