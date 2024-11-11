package potato.ui;

import potato.Game;

import java.awt.*;

public class Checkbox extends UIElement {
    private final String label;
    public Runnable onTrue;
    public Runnable onFalse;
    private boolean checked;

    public Checkbox(int x, int y, int width, int height, boolean visible, String label, Runnable onTrue, Runnable onFalse) {
        super(x, y, width, height, visible);
        this.checked = false;
        this.label = label;
        setBackgroundColor(new Color(108, 117, 125)); // Default background color of Button
        setHighlightColor(new Color(40, 167, 69)); // Default highlight color of Button
        setTextColor(Color.WHITE); // Default text color of Button
        this.onFalse = onFalse;
        this.onTrue = onTrue;
        logger.log("Created Checkbox with x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + ", visible=" + visible);
    }

    @Override
    public String getType() {
        return "Checkbox";
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean toCheck) {
        this.checked = toCheck;
    }

    public void toggleChecked() {
        if (!isVisible()) {
            return;
        }
        logger.log("Toggle " + !this.checked);
        this.checked = !this.checked;
        if (this.checked) {
            Game.SOUND_MANAGER.playSoundEffect("CHECK");
            if (onTrue != null)
            {
                onTrue.run();
            }
        } else {
            Game.SOUND_MANAGER.playSoundEffect("UNCHECK");
            if (onFalse != null) {
                onFalse.run();
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (isVisible()) {
            g.setColor(checked ? getHighlightColor() : getBackgroundColor());
            g.fillRect(getX(), getY(), getWidth(), getHeight());

            // Draw checkbox outline
            g.setColor(getTextColor());
            g.drawRect(getX(), getY(), getWidth(), getHeight());

            // Draw checkbox check mark if checked
            if (checked) {
                g.drawLine(getX(), getY(), getX() + getWidth(), getY() + getHeight());
                g.drawLine(getX() + getWidth(), getY(), getX(), getY() + getHeight());
            }

            // Draw label
            g.drawString(label, getX() + getWidth() + 10, getY() + getHeight() / 2 + g.getFontMetrics().getHeight() / 2);
        }
    }

    @Override
    public void update() {
        // Update logic for checkbox if needed
    }

    // Implement setSelected method for Checkbox
    public void setSelected(boolean selected) {
        if (selected != this.isSelected()) {
            super.setSelected(selected);
        }
    }

    // Methods to set colors from Button
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }
}