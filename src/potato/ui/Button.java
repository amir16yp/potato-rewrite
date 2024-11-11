package potato.ui;

import potato.Game;

import java.awt.*;

public class Button extends UIElement {
    public boolean selected;
    private String text;
    private Color textColor;
    private Color backgroundColor;
    private Color highlightColor;
    private Runnable onSelectedAction;

    public Button(int x, int y, int width, int height, String text) {
        super(x, y, width, height, true);
        this.text = text;
        this.textColor = Color.WHITE;
        this.backgroundColor = new Color(108, 117, 125);
        this.highlightColor = new Color(40, 167, 69);
        this.selected = false;
    }

    public void setHighlightColor(Color highlightColor) {
        this.highlightColor = highlightColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setOnSelectedAction(Runnable action) {
        this.onSelectedAction = action;
    }

    public void onOptionSelected()
    {
        if (onSelectedAction != null && this.isVisible() && Game.RENDERER.isPaused())
        {
            Game.SOUND_MANAGER.playSoundEffect("CLICK");
            onSelectedAction.run();
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void draw(Graphics g) {

        g.setColor(selected ? highlightColor : backgroundColor);
        g.fillRect(getX(), getY(), getWidth(), getHeight());

        // Draw text
        //g.setFont(this.font);
        g.setColor(textColor);
        int stringWidth = g.getFontMetrics().stringWidth(text);
        int stringHeight = g.getFontMetrics().getHeight();
        // Center text horizontally and vertically within the button
        int x = getX() + (getWidth() - stringWidth) / 2;
        int y = getY() + (getHeight() - stringHeight) / 2 + g.getFontMetrics().getAscent();
        g.drawString(text, x, y);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean setSelected) {
        selected = setSelected;
    }
}