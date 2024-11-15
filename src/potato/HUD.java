package potato;

import potato.entities.PlayerEntity;
import potato.entities.PlayerInventory;

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
    private static final Color HIGH_HEALTH = new Color(0, 255, 0); // Full health is green
    private static final Color LOW_HEALTH = new Color(255, 0, 0);  // Low health is red

    // Constants for HUD layout
    private static final int PADDING = 5;
    private static final int ICON_SIZE = 50;
    private static final int SLOT_SIZE = 20;
    private static final int AMMO_BAR_HEIGHT = 4;
    private static final int LOW_AMMO_THRESHOLD = 3;
    private static final int HEALTH_BAR_WIDTH = 10;
    private static final int HEALTH_BAR_HEIGHT = 80;
    private static final int LOW_HEALTH_THRESHOLD = 30;


    public void update() {
        currentWeapon = PlayerEntity.getPlayer().getInventory().getCurrentWeapon();
        inventory = PlayerEntity.getPlayer().getInventory();
        if (currentWeapon != null) {
            currentWeapon.update();
        }
    }

    public void render(Graphics2D g) {
        Color originalColor = g.getColor();

        if (ConfigManager.get().getBoolean(GameProperty.DEBUG_SHOW_FPS))
        {
            drawFPSCounter(g);
        }
        drawWeaponInfo(g);
        drawAmmoDisplay(g);
        drawWeaponSlots(g);
        drawWeaponSprite(g);
        drawHealthBar(g);

        g.setColor(originalColor);
    }



    private void drawHealthBar(Graphics2D g) {
        int health = (int) PlayerEntity.getPlayer().getHealth();
        int maxHealth = (int) PlayerEntity.getPlayer().getMaxHealth();

        int barX = Game.INTERNAL_WIDTH - HEALTH_BAR_WIDTH - PADDING * 3 - SLOT_SIZE;
        int barY = Game.INTERNAL_HEIGHT - HEALTH_BAR_HEIGHT - PADDING;

        // Background
        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);

        // Health level
        float healthPercent = (float)health / maxHealth;
        g.setColor(healthPercent > 0.3f ? HIGH_HEALTH : LOW_HEALTH);

        int filledHeight = (int)(healthPercent * HEALTH_BAR_HEIGHT);
        int currentBarY = barY + (HEALTH_BAR_HEIGHT - filledHeight);
        g.fillRect(barX, currentBarY, HEALTH_BAR_WIDTH, filledHeight);

        // Border
        g.setColor(HUD_BORDER);
        g.drawRect(barX, barY, HEALTH_BAR_WIDTH, HEALTH_BAR_HEIGHT);
    }

    private void drawFPSCounter(Graphics2D g) {
        g.setColor(Color.RED);
        FontMetrics fontMetrics = g.getFontMetrics();
        g.drawString(String.valueOf(Game.GAMELOOP.getFPS()), 0, fontMetrics.getHeight());
    }

    private void drawWeaponInfo(Graphics2D g) {
        if (currentWeapon != null) {
            BufferedImage weaponIcon = currentWeapon.getIcon();
            if (weaponIcon != null) {
                g.drawImage(weaponIcon,
                        PADDING, Game.INTERNAL_HEIGHT - ICON_SIZE - PADDING, null);
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
            g.drawString(ammoText, 30, Game.INTERNAL_HEIGHT - 5);

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
            int weaponX = (Game.INTERNAL_WIDTH - currentWeapon.textures.getTileWidth()) / 2;
            int weaponY = Game.INTERNAL_HEIGHT - currentWeapon.textures.getTileHeight() - 20;
            currentWeapon.render(g, weaponX, weaponY);
        }
    }

    private void drawWeaponSlots(Graphics2D g) {
        int startX = Game.INTERNAL_WIDTH - SLOT_SIZE - PADDING;
        int bottomY = Game.INTERNAL_HEIGHT - PADDING;

        for (int i = 0; i < inventory.getMaxSlots(); i++) {
            int slotY = bottomY - ((inventory.getMaxSlots() - i) * SLOT_SIZE);

            // Draw slot background
            g.setColor(SELECTED_SLOT);
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