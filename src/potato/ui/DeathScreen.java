package potato.ui;

import potato.Game;
import potato.entities.PlayerEntity;

import java.awt.*;
import java.awt.event.KeyEvent;

public class DeathScreen {
    private static final String DEATH_MESSAGE = "YOU DIED";

    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 48));

        int deathX = (Game.INTERNAL_WIDTH - g.getFontMetrics().stringWidth(DEATH_MESSAGE)) / 2;
        g.drawString(DEATH_MESSAGE, deathX, Game.INTERNAL_HEIGHT / 3);
    }

    public void update()
    {
        if (Game.GAME.isKeyPressed(KeyEvent.VK_ENTER))
        {
            Game.RENDERER.showDeathScreen = false;
            PlayerEntity player = PlayerEntity.getPlayer();
            player.heal(100);
        }
    }
}