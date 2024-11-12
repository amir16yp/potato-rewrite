package potato;

public class Weapons {
    public static final Weapon PISTOL = new Weapon(new Textures("/potato/sprites/gun/pistol.png", 48, 48), 100, 1000, 2, "Pistol", Utils.loadImage("/potato/sprites/gun/pistol-icon.png"));
    public static final Weapon SHOTGUN = new Weapon(new Textures("/potato/sprites/gun/shotgun.png", 48, 48), 100, 1000, 2, "Shotgun", Utils.loadImage("/potato/sprites/gun/shotgun-icon.png")); ;
}
