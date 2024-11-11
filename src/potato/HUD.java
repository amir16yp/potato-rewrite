package potato;

import java.awt.*;

public class HUD {

    private Weapon currentWeapon;

    public void update() {
        currentWeapon =  PlayerEntity.getPlayer().getCurrentWeapon();
        currentWeapon.update();
    }

    public void render(Graphics2D g) {
        // Calculate the height of the HUD (25% of the screen height)
        int hudHeight = (int)(0.25 * Game.INTERNAL_HEIGHT);

        // Draw background for HUD (black background for HUD)
        g.setColor(Color.BLACK);  // Semi-transparent black
        g.fillRect(0, Game.INTERNAL_HEIGHT - hudHeight, Game.INTERNAL_WIDTH, hudHeight);

        // Draw FPS counter
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + Game.GAMELOOP.getFPS(), 10, Game.INTERNAL_HEIGHT - hudHeight + 20);

        // Position of the weapon above the HUD (centered horizontally)
        int weaponX = (Game.INTERNAL_WIDTH - currentWeapon.textures.getTileWidth()) / 2;  // Centered horizontally
        int weaponY = Game.INTERNAL_HEIGHT - hudHeight - currentWeapon.textures.getTileHeight() / 2;  // Above the HUD

        // Render the weapon sprite
        currentWeapon.render(g, weaponX, weaponY -40, 4);
    }
}
