package potato;

import potato.ui.MainMenu;
import potato.ui.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Renderer extends JPanel {
    private BufferedImage screenBuffer;
    private boolean paused;
    private HUD hudRenderer;

    private Menu currentMenu;

    public void setCurrentMenu(Menu menu) {
        if (currentMenu != null) {
            currentMenu.setVisible(false);
            // Remove listeners from old menu
            removeMouseListener(currentMenu);
            removeMouseMotionListener(currentMenu);
            removeMouseWheelListener(currentMenu);
        }
        menu.setVisible(true);
        addMouseListener(menu);
        addMouseMotionListener(menu);
        addMouseWheelListener(menu);
        currentMenu = menu;
    }
    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean toPause)
    {
        paused = toPause;
        currentMenu.setVisible(paused);
    }

    public Renderer() {
        setCurrentMenu(new MainMenu());
        setPaused(true);
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
        Game.RAYCASTER.update();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Clear the buffer
        Graphics2D buffG = screenBuffer.createGraphics();
        buffG.setColor(Color.BLACK);
        buffG.clearRect(0, 0, Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT);

        if (!isPaused())
        {
            Game.RAYCASTER.render(buffG);
            hudRenderer.render(buffG);

        } else {
            currentMenu.draw(buffG);
        }
        buffG.dispose();
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