package potato.ui;

import potato.Game;

public class MainMenu extends Menu {
    public MainMenu() {
        super("Main Menu");
        addButton("Resume", () -> {
            Game.RENDERER.setPaused(false);
        });
        addCheckbox("Lock fps", () -> {
            Game.GAMELOOP.setTargetFPS(60);
        },() -> {
            Game.GAMELOOP.setTargetFPS(0);
        });
    }
}
