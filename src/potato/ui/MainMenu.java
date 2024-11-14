package potato.ui;

import potato.DemonEntity;
import potato.Game;
import potato.PlayerEntity;
import potato.SaveSystem;

import java.io.IOException;

public class MainMenu extends Menu {
    public MainMenu() {
        super("Main Menu");
        addButton("Resume", () -> {
            Game.RENDERER.setPaused(false);
        });
        addCheckbox("Lock fps", () -> {
            SaveSystem.SETTINGS_SAVE.setInt("CAP_FPS", 60);
        },() -> {
            SaveSystem.SETTINGS_SAVE.setInt("CAP_FPS", 0);
        }).setChecked(SaveSystem.SETTINGS_SAVE.getInt("CAP_FPS", 0) == 60);

        addButton("Spawn demon", () -> {
            PlayerEntity player = PlayerEntity.getPlayer();
            Game.RAYCASTER.currentLevel.addEntity(new DemonEntity(player.getX(), player.getY()));
        });

        addButton("SAVE SETTINGS", () -> {
            try {
                SaveSystem.SETTINGS_SAVE.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
