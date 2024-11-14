package potato.ui;

import potato.*;

import java.io.IOException;

public class MainMenu extends Menu {

    private Button startResumeBtn;

    private Menu devMenu = new Menu("Dev Menu");

    public MainMenu() {
        super("Main Menu");

        devMenu.addButton("Spawn demon", () -> {
            PlayerEntity player = PlayerEntity.getPlayer();
            Game.RAYCASTER.currentLevel.addEntity(new DemonEntity(player.getX(), player.getY()));
        });

        startResumeBtn = addButton("Start", () -> {
            Level level = new Level(new int[][]{
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,1,1,1,1,0,0,0,0,0,0,0,1    ,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                    {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            });

            level.floorTexture = level.getTexture(17);
            level.ceilingTexture = level.getTexture(13);

            Game.RAYCASTER.currentLevel = level;

            Game.RENDERER.setPaused(false);

            addChildMenu(devMenu);

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



        addButton("SAVE SETTINGS", () -> {
            try {
                SaveSystem.SETTINGS_SAVE.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
