package potato.ui;

import potato.*;
import potato.entities.DemonEntity;
import potato.entities.PlayerEntity;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class MainMenu extends Menu {

    private Button startResumeBtn;
    private final LevelGenerator levelGenerator = new LevelGenerator();

    public MainMenu() {
        super("Main Menu");
        Menu devMenu = new Menu("Dev Menu");
        devMenu.addButton("Spawn demon", () -> {
            PlayerEntity player = PlayerEntity.getPlayer();
            Game.RAYCASTER.currentLevel.addEntity(new DemonEntity(player.getX(), player.getY()));
        });

        startResumeBtn = addButton("Start", () -> {
            Level level = levelGenerator.generateLevel(String.valueOf(new Random().nextInt()),128, 128);
            level.floorTexture = level.getTexture(17);
            level.ceilingTexture = level.getTexture(13);

            Game.RAYCASTER.currentLevel = level;

            Game.RENDERER.setPaused(false);


            startResumeBtn.setOnSelectedAction(() -> {
                Game.RENDERER.setPaused(false);
            });
            startResumeBtn.setText("Resume");
        });
        addCheckbox("Lock fps", () -> {
            SaveSystem.SETTINGS_SAVE.setInt("CAP_FPS", 60);
        },() -> {
            SaveSystem.SETTINGS_SAVE.setInt("CAP_FPS", 0);
        }).setChecked(SaveSystem.SETTINGS_SAVE.getInt("CAP_FPS", 0) == 60);

        addChildMenu(devMenu);

        addButton("SAVE SETTINGS", () -> {
            try {
                SaveSystem.SETTINGS_SAVE.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
