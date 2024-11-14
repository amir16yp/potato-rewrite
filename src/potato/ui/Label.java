package potato.ui;

import java.awt.*;

public class Label extends UIElement {
    private String text;
    private boolean centered;

    public Label(int x, int y, int width, int height, String text) {
        super(x, y, width, height, true);
        this.text = text;
        this.centered = false;
        setBackgroundColor(new Color(0, 0, 0, 0)); // Transparent background
        setShouldDrawBackgroundColor(false);
    }

    public Label(int x, int y, String text) {
        this(x, y, 0, 0, text); // Width and height will be set based on text dimensions
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (isVisible()) {
            g.setColor(getTextColor() != null ? getTextColor() : Color.BLACK);

            FontMetrics metrics = g.getFontMetrics();
            int textWidth = metrics.stringWidth(text);
            int textHeight = metrics.getHeight();

            // Auto-size if width/height not set
            if (getWidth() == 0) setWidth(textWidth);
            if (getHeight() == 0) setHeight(textHeight);

            int textX;
            if (centered) {
                textX = getX() + (getWidth() - textWidth) / 2;
            } else {
                textX = getX();
            }

            // Draw text at baseline
            int textY = getY() + ((getHeight() + metrics.getAscent() - metrics.getDescent()) / 2);
            g.drawString(text, textX, textY);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    @Override
    public String getType() {
        return "Label";
    }
}