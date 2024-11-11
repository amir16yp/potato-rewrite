package potato;

import java.awt.*;

public class HUD {

    // Draws the HUD elements like health bar, FPS, and other UI elements
    public void render(Graphics g) {
        // Calculate the height of the HUD (25% of the screen height)
        int hudHeight = (int)(0.25 * Game.INTERNAL_HEIGHT);

        // Draw background for HUD (black background for HUD)
        g.setColor(Color.BLACK);  // Semi-transparent black
        g.fillRect(0, Game.INTERNAL_HEIGHT - hudHeight, Game.INTERNAL_WIDTH, hudHeight);

        // Draw FPS counter
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + Game.GAMELOOP.getFPS(), 10, Game.INTERNAL_HEIGHT - hudHeight + 20);

        }

    // Draw a health bar
    private void drawHealthBar(Graphics g, int x, int y, int width, int height, int currentHealth) {
        int maxHealth = 100; // Assuming max health is 100
        int healthBarWidth = (int)((double)currentHealth / maxHealth * width);

        // Background of the health bar (dark red)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, y, width, height);

        // Foreground (current health) of the health bar (green)
        g.setColor(Color.GREEN);
        g.fillRect(x, y, healthBarWidth, height);

        // Health text (optional)
        g.setColor(Color.WHITE);
        g.drawString("Health: " + currentHealth + "/" + maxHealth, x + 10, y + height - 5);
    }

    // Draw an item counter (e.g., 5/10 items)
    private void drawItemCounter(Graphics g, int x, int y, int width, int height, int currentItemCount, int maxItemCount) {
        g.setColor(Color.WHITE);
        g.drawString("Items: " + currentItemCount + "/" + maxItemCount, x + 10, y + height - 5);
    }
}
