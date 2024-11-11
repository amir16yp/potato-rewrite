package potato.ui;

import potato.Game;

import java.awt.*;
import java.awt.event.MouseEvent;

public class VolumeBar extends UIElement {
    private final String label;
    private final Color barColor;
    private final Color backgroundColor;
    private float volume;
    private Runnable onVolumeChange;

    public VolumeBar(int x, int y, int width, int height, String label, float initialVolume) {
        super(x, y, width, height, true);
        this.label = label;
        this.volume = initialVolume;
        this.barColor = new Color(40, 167, 69);
        this.backgroundColor = new Color(200, 200, 200);
    }

    public void setOnVolumeChange(Runnable action) {
        this.onVolumeChange = action;
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (isVisible()) {
            // Draw background
            g.setColor(backgroundColor);
            g.fillRect(getX(), getY(), getWidth(), getHeight());

            // Draw volume bar
            g.setColor(barColor);
            int filledWidth = (int) (getWidth() * volume);
            g.fillRect(getX(), getY(), filledWidth, getHeight());

            // Draw border
            g.setColor(Color.BLACK);
            g.drawRect(getX(), getY(), getWidth(), getHeight());

            // Draw label
            g.setColor(Color.BLACK);
            g.drawString(label, getX(), getY() - 5);

            // Draw volume percentage
            String volumeText = Math.round(volume * 100) + "%";
            int textWidth = g.getFontMetrics().stringWidth(volumeText);
            g.drawString(volumeText, getX() + getWidth() - textWidth, getY() + getHeight() + 15);
        }
    }

    public void handleMouseEvent(MouseEvent e) {
        if (containsPoint(e.getX(), e.getY())) {
            // Calculate scale factors
            double scaleX = (double) Game.RENDERER.getWidth() / Game.INTERNAL_WIDTH;

            // Calculate new volume based on mouse position
            int relativeX = (int) ((e.getX() / scaleX) - getX());
            setVolume(Math.max(0, Math.min(1, (float) relativeX / getWidth())));

            if (onVolumeChange != null) {
                onVolumeChange.run();
            }
        }
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));
    }
}