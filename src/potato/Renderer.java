package potato;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Renderer extends JPanel {
    private BufferedImage screenBuffer;

    private HUD hudRenderer;

    public Renderer() {
        // Create a buffer with the internal resolution dimensions
        screenBuffer = new BufferedImage(Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.hudRenderer = new HUD();
        // Set panel properties
        this.setPreferredSize(new Dimension(Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.requestFocus();
    }

    public void update() {
        hudRenderer.update();
        Game.GAME.RAYCASTER.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Clear the buffer
        Graphics2D buffG = screenBuffer.createGraphics();
        buffG.setColor(Color.BLACK);
        buffG.clearRect(0, 0, Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT);

        Game.GAME.RAYCASTER.render(buffG);
        hudRenderer.render(buffG);
        buffG.dispose();

        // Draw buffer to screen
        g.drawImage(screenBuffer, 0, 0, getWidth(), getHeight(), null);

        Toolkit.getDefaultToolkit().sync();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT);
    }

    // This helps ensure our component maintains its aspect ratio
    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}