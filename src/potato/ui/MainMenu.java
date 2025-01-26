package potato.ui;

import potato.*;
import potato.entities.DemonEntity;
import potato.entities.PlayerEntity;
import potato.entities.PlayerInventory;

import java.awt.*;
import java.io.IOException;
import java.util.Random;
import java.util.function.Consumer;

public class MainMenu extends Menu {
    private Button startResumeBtn;

    public MainMenu() {
        super("Main Menu");

        startResumeBtn = addButton("Start", () -> {
            Game.LEVEL_GENERATOR.generatedLevel.floorTexture = Game.LEVEL_GENERATOR.generatedLevel.getTexture(17);
            //Game.LEVEL_GENERATOR.generatedLevel.ceilingTexture = Game.LEVEL_GENERATOR.generatedLevel.getTexture(13);

            Game.RENDERER.setPaused(false);

            startResumeBtn.setOnSelectedAction(() -> {
                Game.RENDERER.setPaused(false);
            });
            startResumeBtn.setText("Resume");
        });

        addCheckbox("Lock fps", () -> {
            SaveSystem.SETTINGS_SAVE.setInt("CAP_FPS", 60);
        }, () -> {
            SaveSystem.SETTINGS_SAVE.setInt("CAP_FPS", 0);
        }).setChecked(SaveSystem.SETTINGS_SAVE.getInt("CAP_FPS", 0) == 60);

        if (Game.DEV_MODE_ENABLED)
        {
            addChildMenu(createDevMenu());

        }

        addButton("SAVE SETTINGS", () -> {
            try {
                SaveSystem.SETTINGS_SAVE.save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private Menu createDevMenu() {
        Menu menu = new Menu("Cheats Menu");
        menu.addButton("Spawn demon", () -> {
            PlayerEntity player = PlayerEntity.getPlayer();
            Game.LEVEL_GENERATOR.generatedLevel.addEntity(new DemonEntity(player.getX(), player.getY()));
        });

        menu.addButton("Max health", () -> {
            PlayerEntity player = PlayerEntity.getPlayer();
            player.heal(player.getMaxHealth());
        });

        menu.addButton("Max ammo", () -> {
            PlayerInventory playerInventory = PlayerEntity.getPlayer().getInventory();
            for (Weapon weapon : playerInventory.getWeapons()) {
                weapon.setAmmoAmount(weapon.getMaxAmmoAmount());
            }
        });

        menu.addCheckbox("Godmode", () -> {
            PlayerEntity.getPlayer().setCanTakeDamage(false);
        }, () -> {
            PlayerEntity.getPlayer().setCanTakeDamage(true);
        });

        Label fovLabel = menu.addLabel("FOV");
        fovLabel.setTextColor(Color.WHITE);

        RangeSlider fovSlider = menu.addSlider(50, 180, Raycaster.FOV, 1);
        fovSlider.setOnValueChange(value -> {
            int fov = value.intValue();
            Raycaster.setFOV(fov);
            fovLabel.setText("FOV: " + fov);
        });

        return menu;
    }
}