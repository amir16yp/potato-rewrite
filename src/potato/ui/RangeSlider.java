package potato.ui;

import potato.Game;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class RangeSlider extends UIElement {
    private final Color barColor;
    private final Color backgroundColor;
    private final double minValue;
    private final double maxValue;
    private double currentValue;
    private Consumer<Double> onValueChange;
    private final int decimalPlaces;

    public RangeSlider(int x, int y, int width, int height,
                       double minValue, double maxValue, double initialValue,
                       int decimalPlaces) {
        super(x, y, width, height, true);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = clampValue(initialValue);
        this.decimalPlaces = decimalPlaces;
        this.barColor = new Color(40, 167, 69);
        this.backgroundColor = new Color(200, 200, 200);
    }

    public void setOnValueChange(Consumer<Double> action) {
        this.onValueChange = action;
    }

    private double clampValue(double value) {
        return Math.max(minValue, Math.min(maxValue, value));
    }

    private double getPercentage() {
        return (currentValue - minValue) / (maxValue - minValue);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (isVisible()) {
            // Draw background
            g.setColor(backgroundColor);
            g.fillRect(getX(), getY(), getWidth(), getHeight());

            // Draw filled bar
            g.setColor(barColor);
            int filledWidth = (int) (getWidth() * getPercentage());
            g.fillRect(getX(), getY(), filledWidth, getHeight());

            // Draw border
            g.setColor(Color.BLACK);
            g.drawRect(getX(), getY(), getWidth(), getHeight());

            // Draw current value centered below the bar
            String valueText = formatValue(currentValue);
            int textWidth = g.getFontMetrics().stringWidth(valueText);
            int textX = getX() + (getWidth() - textWidth) / 2;
            int textY = getY() + getHeight() + 15;
            g.drawString(valueText, textX, textY);
        }
    }

    private String formatValue(double value) {
        if (decimalPlaces <= 0) {
            return String.valueOf(Math.round(value));
        }
        return String.format("%." + decimalPlaces + "f", value);
    }

    public void handleMouseEvent(MouseEvent e) {
        if (containsPoint(e.getX(), e.getY())) {
            // Calculate scale factors
            double scaleX = (double) Game.RENDERER.getWidth() / Game.INTERNAL_WIDTH;

            // Calculate new value based on mouse position
            int relativeX = (int) ((e.getX() / scaleX) - getX());
            double percentage = Math.max(0, Math.min(1, (double) relativeX / getWidth()));
            setValue(minValue + (percentage * (maxValue - minValue)));

            if (onValueChange != null) {
                onValueChange.accept(currentValue);
            }
        }
    }

    public double getValue() {
        return currentValue;
    }

    public void setValue(double value) {
        this.currentValue = clampValue(value);
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }
}