package potato;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HUD {
    private PlayerInventory inventory;
    private Weapon currentWeapon;
    private static final Color HUD_BACKGROUND = new Color(0, 0, 0, 200);
    private static final Color HUD_BORDER = new Color(128, 128, 128);
    private static final Color AMMO_TEXT = new Color(255, 255, 255);
    private static final Color LOW_AMMO = new Color(255, 50, 50);
    private static final Color SELECTED_SLOT = new Color(72, 72, 72);

    // Constants for HUD layout
    private static final int PADDING = 5;
    private static final int ICON_SIZE = 50;
    private static final int SLOT_SIZE = 20;
    private static final int AMMO_BAR_HEIGHT = 4;
    private static final int LOW_AMMO_THRESHOLD = 3;

    public void update() {
        currentWeapon = PlayerEntity.getPlayer().getInventory().getCurrentWeapon();
        inventory = PlayerEntity.getPlayer().getInventory();
        if (currentWeapon != null) {
            currentWeapon.update();
        }
    }

    public void render(Graphics2D g) {
        // Store the original color
        Color originalColor = g.getColor();

        // Draw main hud elements
        drawFPSCounter(g);
        drawWeaponInfo(g);
        drawAmmoDisplay(g);
        drawWeaponSlots(g);
        drawWeaponSprite(g);

        // Restore original color
        g.setColor(originalColor);
    }

    private void drawFPSCounter(Graphics2D g) {
        g.setColor(Color.RED);
        FontMetrics fontMetrics = g.getFontMetrics();
        //g.drawString("FPS: " + Game.GAMELOOP.getFPS() + " deltaTime:" + Game.GAMELOOP.getDeltaTimeMillis(),0, fontMetrics.getHeight());
        g.drawString(String.valueOf(Game.GAMELOOP.getFPS()), 0, fontMetrics.getHeight());
    }

    private void drawWeaponInfo(Graphics2D g) {
        if (currentWeapon != null) {
            BufferedImage weaponIcon = currentWeapon.getIcon();
            if (weaponIcon != null) {
                g.drawImage(weaponIcon,
                        PADDING, 240 - ICON_SIZE - PADDING, null);
            }
        }
    }

    private void drawAmmoDisplay(Graphics2D g) {
        if (currentWeapon != null) {
            int ammo = currentWeapon.getAmmoAmount();
            int maxAmmo = currentWeapon.getMaxAmmoAmount();

            // Draw ammo counter
            g.setColor(ammo <= LOW_AMMO_THRESHOLD ? LOW_AMMO : AMMO_TEXT);
            String ammoText = ammo + " / " + maxAmmo;
            g.drawString(ammoText, 30, 240 - 5);

            // Draw ammo bar
            int barWidth = 50;
            int barX = 10;
            int barY = Game.INTERNAL_HEIGHT - 30;

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
    }

    private void drawWeaponSprite(Graphics2D g) {
        if (currentWeapon != null) {
            int weaponX = (256 - currentWeapon.textures.getTileWidth()) / 2;
            int weaponY = 240 - currentWeapon.textures.getTileHeight() - 20;
            currentWeapon.render(g, weaponX, weaponY);
        }
    }

    private void drawWeaponSlots(Graphics2D g) {
        // Slots stacked vertically on far right
        int startX = 256 - SLOT_SIZE - 5; // 5 pixels from right edge
        int bottomY = 240 - 5; // 5 pixels from bottom

        for (int i = 0; i < inventory.getMaxSlots(); i++) {
            int slotY = bottomY - ((inventory.getMaxSlots() - i) * SLOT_SIZE);

            // Draw slot background
            g.setColor(SELECTED_SLOT); // Default gray background
            g.fillRect(startX, slotY, SLOT_SIZE, SLOT_SIZE);

            // Highlight selected slot with border
            if (i == inventory.getCurrentSlot()) {
                g.setColor(HUD_BORDER);
                g.drawRect(startX, slotY, SLOT_SIZE-1, SLOT_SIZE-1);
            }

            // Draw weapon icon if slot has weapon
            Weapon weapon = inventory.getWeapon(i);
            if (weapon != null) {
                BufferedImage icon = weapon.getIcon();
                if (icon != null) {
                    g.drawImage(icon.getScaledInstance(SLOT_SIZE-4, SLOT_SIZE-4,
                            Image.SCALE_SMOOTH), startX+2, slotY+2, null);
                }
            }
        }
    }
}