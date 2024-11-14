package potato.ui;

import potato.Game;
import potato.Logger;

import java.awt.*;

public class UIElement {
    private static int totalElementCount = 0;
    private static Font font;
    public final Logger logger = new Logger(this.getClass().getName());
    protected Color backgroundColor;
    protected Color highlightColor;
    protected Color textColor;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean visible;
    private boolean selected = false;
    private boolean shouldDrawBackgroundColor = true;

    public UIElement(int x, int y, int width, int height, boolean visible) {
        logger.addPrefix("E" + totalElementCount);
        totalElementCount++;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.visible = visible;
        logger.log("Created UIElement with x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", visible=" + visible);
    }

    public String getType() {
        return "UIElement";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        //logger.Log("Setting visible to " + visible);
        this.visible = visible;
    }

    public void update() {

    }

    public void draw(Graphics g) {
        if (isVisible()) {
            g.setFont(font);
            if (shouldDrawBackgroundColor()) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(getBackgroundColor());
                g2d.fillRect(getX(), getY(), getWidth(), getHeight());
            }

        }

    }

    public boolean containsPoint(int x, int y) {
        // Calculate scale factors
        double scaleX = (double) Game.RENDERER.getWidth() / Game.INTERNAL_WIDTH;
        double scaleY = (double) Game.RENDERER.getHeight() / Game.INTERNAL_HEIGHT;

        // Adjust mouse coordinates for scale
        int scaledX = (int) (x / scaleX);
        int scaledY = (int) (y / scaleY);

        // Check if the scaled coordinates are within the button bounds
        return scaledX >= getX() && scaledX <= getX() + getWidth() &&
                scaledY >= getY() && scaledY <= getY() + getHeight();
    }

    public boolean containsPoint(int x, int y, int scrollOffset) {
        // Calculate scale factors
        double scaleX = (double) Game.RENDERER.getWidth() / Game.INTERNAL_WIDTH;
        double scaleY = (double) Game.RENDERER.getHeight() / Game.INTERNAL_HEIGHT;

        // Adjust mouse coordinates for scale
        int scaledX = (int) (x / scaleX);
        int scaledY = (int) (y / scaleY);

        // Check if the scaled coordinates are within the button bounds, accounting for scroll offset
        return scaledX >= getX() && scaledX <= getX() + getWidth() &&
                scaledY >= getY() - scrollOffset && scaledY <= getY() - scrollOffset + getHeight();
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean toSetSelected) {
        this.selected = toSetSelected;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getHighlightColor() {
        return highlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public boolean shouldDrawBackgroundColor() {
        return shouldDrawBackgroundColor;
    }

    public void setShouldDrawBackgroundColor(boolean shouldDrawBackgroundColor) {
        this.shouldDrawBackgroundColor = shouldDrawBackgroundColor;
    }
}