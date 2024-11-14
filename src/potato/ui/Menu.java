package potato.ui;

import potato.Game;
import potato.Raycaster;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Menu extends UIElement implements MouseMotionListener, MouseListener, MouseWheelListener {
    // Scroll bar variables
    private static final int SCROLL_BAR_WIDTH = 10;
    private static final int SCROLL_BAR_RIGHT_MARGIN = 10;
    private final List<UIElement> elements; // List to store all UI elements
    private final int buttonWidth = Game.INTERNAL_WIDTH;
    private final int buttonHeight = 10;
    private final int spacing = 15;
    private final ArrayList<Menu> childMenus = new ArrayList<>();
    private final String label;
    private final int visibleElements = 7; // Number of elements visible at once
    public int selectedButtonIndex = 0;
    // Scrolling variables
    private int scrollOffset = 0;
    private int maxScroll;
    private boolean isDraggingScrollBar = false;
    private int dragStartY;
    private int dragStartOffset;

    public Menu(String label) {
        super(0, 0, Game.INTERNAL_WIDTH, Game.INTERNAL_HEIGHT, false);
        this.label = label;
        this.elements = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }

    public ArrayList<Menu> getChildMenus() {
        return childMenus;
    }

    public Label addLabel(String text) {
        int x = startX();
        int y = startY() + elements.size() * spacing;
        Label label = new Label(x, y, buttonWidth, buttonHeight, text);
        label.setCentered(true);
        addElement(label);
        return label;
    }

    public Label addLabel(String text, boolean centered) {
        int x = startX();
        int y = startY() + elements.size() * spacing;
        Label label = new Label(x, y, buttonWidth, buttonHeight, text);
        label.setCentered(centered);
        addElement(label);
        return label;
    }

    public void addChildMenu(Menu menu) {
        this.childMenus.add(menu);
        menu.addButton("Back", () -> {
            Game.RENDERER.setCurrentMenu(this);
        });
        this.addButton(menu.label, () -> {
            Game.RENDERER.setCurrentMenu(menu);
        });
    }

    public Button addButton(String text, Runnable action) {
        int x = startX();
        int y = startY() + elements.size() * spacing;
        Button button = new Button(x, y, buttonWidth, buttonHeight, text);
        button.setOnSelectedAction(action);
        addElement(button);
        return button;
    }

    public Checkbox addCheckbox(String label, Runnable onTrue, Runnable onFalse) {
        int x = startX();
        int y = startY() + elements.size() * spacing;
        Checkbox checkbox = new Checkbox(x, y, buttonWidth / 4, buttonHeight, true, label, onTrue, onFalse);
        addElement(checkbox);
        return checkbox;
    }

    public RangeSlider addSlider(double minValue, double maxValue, double initialValue, int decimalPlaces) {
        int x = startX();
        int y = startY() + elements.size() * spacing;
        RangeSlider slider = new RangeSlider(x,y,Game.INTERNAL_WIDTH, buttonHeight, minValue, maxValue, initialValue, decimalPlaces);
        addElement(slider);
        return slider;
    }

    private int startX() {
        return getX() + (getWidth() - buttonWidth) / 2;
    }

    private int startY() {
        return getY();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        for (UIElement element : elements) {
            element.setVisible(visible);
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (isVisible()) {
            // Draw all visible UI elements
            for (UIElement element : elements) {
                int adjustedY = element.getY() - scrollOffset;
                if (adjustedY >= getY() && adjustedY + element.getHeight() <= getY() + getHeight()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.translate(0, -scrollOffset);
                    element.draw(g2d);
                    g2d.dispose();
                }
            }

            // Draw scroll bar if necessary
            if (elements.size() > visibleElements) {
                drawScrollBar(g);
            }
        }
    }

    @Override
    public void update() {
        super.update();
        for (UIElement element : elements) {
            element.update();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!isScrollBarClick(e.getX(), e.getY())) {
            for (UIElement element : elements) {
                if (element.isVisible() && element.containsPoint(e.getX(), e.getY(), scrollOffset)) {
                    activate(element);
                    break;
                }
            }
        }
    }

    private boolean isScrollBarClick(int x, int y) {
        int scrollBarX = getX() + getWidth() - SCROLL_BAR_WIDTH - SCROLL_BAR_RIGHT_MARGIN;
        int scrollBarY = getY() + 50;
        int scrollBarHeight = getHeight() - 100;
        return x >= scrollBarX && x <= scrollBarX + SCROLL_BAR_WIDTH &&
                y >= scrollBarY && y <= scrollBarY + scrollBarHeight;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (isScrollBarClick(e.getX(), e.getY())) {
            isDraggingScrollBar = true;
            dragStartY = e.getY();
            dragStartOffset = scrollOffset;
        } else {
            for (UIElement element : elements) {
                if (element instanceof RangeSlider) {
                    ((RangeSlider) element).handleMouseEvent(e);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDraggingScrollBar = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isDraggingScrollBar) {
            int dragDistance = e.getY() - dragStartY;
            int scrollBarHeight = getHeight() - 100;
            int newScrollOffset = dragStartOffset + (dragDistance * maxScroll / scrollBarHeight);
            scroll(newScrollOffset - scrollOffset);
        } else {
            for (UIElement element : elements) {
                if (element instanceof RangeSlider) {
                    ((RangeSlider) element).handleMouseEvent(e);
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (UIElement element : elements) {
            boolean containsPoint = element.containsPoint(e.getX(), e.getY(), scrollOffset);

            if (element instanceof Button) {
                ((Button) element).setSelected(containsPoint);
                if (containsPoint) selectedButtonIndex = elements.indexOf(element);
            } else if (element instanceof Checkbox) {
                ((Checkbox) element).setSelected(containsPoint);
                if (containsPoint) selectedButtonIndex = elements.indexOf(element);
            }
        }
    }

    private void drawScrollBar(Graphics g) {
        int scrollBarWidth = 10;
        int scrollBarHeight = getHeight() - 100;
        int scrollBarX = getX() + getWidth() - scrollBarWidth - 10;
        int scrollBarY = getY() + 50;

        // Draw background
        g.setColor(Color.GRAY);
        g.fillRect(scrollBarX, scrollBarY, scrollBarWidth, scrollBarHeight);

        // Draw scroll thumb
        int thumbHeight = Math.max(50, scrollBarHeight * visibleElements / elements.size());
        int thumbY = scrollBarY + (scrollBarHeight - thumbHeight) * scrollOffset / maxScroll;
        g.setColor(Color.WHITE);
        g.fillRect(scrollBarX, thumbY, scrollBarWidth, thumbHeight);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        scroll(notches * 20);
    }

    private void scroll(int amount) {
        scrollOffset = Math.max(0, Math.min(scrollOffset + amount, maxScroll));
        updateElementPositions();
    }

    private void updateElementPositions() {
        for (int i = 0; i < elements.size(); i++) {
            UIElement element = elements.get(i);
            int y = startY() + i * spacing - scrollOffset;
            element.setY(y);
        }
    }

    public void addElement(UIElement element) {
        elements.add(element);
        updateMaxScroll();
    }

    private void updateMaxScroll() {
        int totalHeight = elements.size() * spacing;
        int visibleHeight = visibleElements * spacing;
        maxScroll = Math.max(0, totalHeight - visibleHeight);
    }

    public List<UIElement> getElements() {
        return this.elements;
    }

    public void activate(UIElement element) {
        if (element instanceof Button) {
            ((Button) element).onOptionSelected();
        } else if (element instanceof Checkbox) {
            ((Checkbox) element).toggleChecked();
        }
    }

    private void activateSelected() {
        if (!elements.isEmpty()) {
            UIElement element = elements.get(selectedButtonIndex);
            activate(element);
        }
    }
}