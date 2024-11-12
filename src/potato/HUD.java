package potato;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HUD {
    private Weapon currentWeapon;
    private static final Color HUD_BACKGROUND = new Color(0, 0, 0, 200); // Semi-transparent black
    private static final Color HUD_BORDER = new Color(128, 128, 128);    // Gray border
    private static final Color AMMO_TEXT = new Color(255, 255, 255);     // White text
    private static final Color LOW_AMMO = new Color(255, 50, 50);        // Red for low ammo
    private static final Font HUD_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font AMMO_FONT = new Font("Arial", Font.BOLD, 24);

    // Constants for HUD layout
    private static final int PADDING = 20;
    private static final int ICON_SIZE = 40;
    private static final int AMMO_BAR_HEIGHT = 10;
    private static final int LOW_AMMO_THRESHOLD = 3;

    public void update() {
        currentWeapon = PlayerEntity.getPlayer().getCurrentWeapon();
        currentWeapon.update();
    }

    public void render(Graphics2D g) {
        // Store the original font and color
        Font originalFont = g.getFont();
        Color originalColor = g.getColor();

        // Calculate HUD dimensions
        int hudHeight = (int)(0.25 * Game.INTERNAL_HEIGHT);
        int hudY = Game.INTERNAL_HEIGHT - hudHeight;

        // Draw main HUD background with border
        drawHUDBackground(g, hudHeight, hudY);

        // Draw FPS counter with improved visibility
        drawFPSCounter(g);

        // Draw weapon information
        drawWeaponInfo(g, hudHeight, hudY);

        // Draw ammo display
        drawAmmoDisplay(g, hudY);

        // Draw weapon sprite
        drawWeaponSprite(g, hudHeight, hudY);

        // Restore original graphics settings
        g.setFont(originalFont);
        g.setColor(originalColor);
    }

    private void drawHUDBackground(Graphics2D g, int hudHeight, int hudY) {
        // Draw main background
        g.setColor(HUD_BACKGROUND);
        g.fillRect(0, hudY, Game.INTERNAL_WIDTH, hudHeight);

        // Draw border
        g.setColor(HUD_BORDER);
        g.drawRect(0, hudY, Game.INTERNAL_WIDTH - 1, hudHeight - 1);
    }

    private void drawFPSCounter(Graphics2D g) {
        g.setFont(HUD_FONT);
        g.setColor(Color.RED);
        g.drawString("FPS: " + Game.GAMELOOP.getFPS(), PADDING, PADDING);
    }

    private void drawWeaponInfo(Graphics2D g, int hudHeight, int hudY) {
        // Draw weapon icon
        BufferedImage weaponIcon = currentWeapon.getIcon();
        if (weaponIcon != null) {
            g.drawImage(weaponIcon.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH),
                    PADDING, hudY + PADDING, null);
        }
    }

    private void drawAmmoDisplay(Graphics2D g, int hudY) {
        int ammo = currentWeapon.getAmmoAmount();
        int maxAmmo = currentWeapon.getMaxAmmoAmount();

        // Set color based on ammo amount
        g.setColor(ammo <= LOW_AMMO_THRESHOLD ? LOW_AMMO : AMMO_TEXT);

        // Draw ammo counter
        g.setFont(AMMO_FONT);
        String ammoText = ammo + " / " + maxAmmo;
        g.drawString(ammoText, PADDING * 3 + ICON_SIZE, hudY + PADDING * 2);

        // Draw ammo bar
        int barWidth = 150;
        int barX = PADDING * 3 + ICON_SIZE;
        int barY = hudY + PADDING * 2 + 10;

        // Background bar
        g.setColor(Color.GRAY);
        g.fillRect(barX, barY, barWidth, AMMO_BAR_HEIGHT);

        // Ammo level bar
        g.setColor(ammo <= LOW_AMMO_THRESHOLD ? LOW_AMMO : Color.GREEN);
        int filledWidth = (int)((float)ammo / maxAmmo * barWidth);
        g.fillRect(barX, barY, filledWidth, AMMO_BAR_HEIGHT);

        // Bar border
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, AMMO_BAR_HEIGHT);
    }

    private void drawWeaponSprite(Graphics2D g, int hudHeight, int hudY) {
        // Position weapon sprite above HUD
        int weaponX = (Game.INTERNAL_WIDTH - currentWeapon.textures.getTileWidth()) / 2;
        int weaponY = hudY - currentWeapon.textures.getTileHeight() / 2 - 40;

        // Render the weapon
        currentWeapon.render(g, weaponX, weaponY);
    }
}