package potato.entities;

import potato.Game;
import potato.Weapon;
import potato.Weapons;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerInventory {
    private static final int MAX_SLOTS = 4; // Number of weapon slots
    private Map<Integer, Weapon> weaponSlots;
    private int currentSlot;

    public PlayerInventory() {
        weaponSlots = new HashMap<>();
        currentSlot = 0;

        // Initialize with default weapon (SMG)
        addWeaponToFirstEmptySlot(Weapons.PISTOL);
        addWeaponToFirstEmptySlot(Weapons.SHOTGUN);
        addWeaponToFirstEmptySlot(Weapons.SMG);
    }

    public List<Weapon> getWeapons()
    {
        return new ArrayList<Weapon>(weaponSlots.values());
    }

    public void addWeapon(int slot, Weapon weapon) {
        if (slot >= 0 && slot < MAX_SLOTS) {
            weaponSlots.put(slot, weapon);
        }
    }

    public Weapon getWeapon(int slot) {
        return weaponSlots.getOrDefault(slot, null);
    }

    public Weapon getCurrentWeapon() {
        return getWeapon(currentSlot);
    }

    public int getCurrentSlot() {
        return currentSlot;
    }

    public void update() {
        // Handle number key inputs for weapon switching
        for (int i = 0; i < MAX_SLOTS; i++) {
            if (Game.GAME.isKeyPressed(KeyEvent.VK_1 + i)) {
                switchToSlot(i);
                break;
            }
        }

        // Handle mouse wheel for weapon switching (if you want to add this feature)
        // You'll need to implement mouse wheel detection in your Game class
    }

    public void switchToSlot(int slot) {
        if (slot >= 0 && slot < MAX_SLOTS && weaponSlots.containsKey(slot)) {
            currentSlot = slot;
        }
    }

    public void cycleWeaponForward() {
        int nextSlot = currentSlot;
        do {
            nextSlot = (nextSlot + 1) % MAX_SLOTS;
            if (weaponSlots.containsKey(nextSlot)) {
                switchToSlot(nextSlot);
                break;
            }
        } while (nextSlot != currentSlot);
    }

    public void cycleWeaponBackward() {
        int prevSlot = currentSlot;
        do {
            prevSlot = (prevSlot - 1 + MAX_SLOTS) % MAX_SLOTS;
            if (weaponSlots.containsKey(prevSlot)) {
                switchToSlot(prevSlot);
                break;
            }
        } while (prevSlot != currentSlot);
    }

    public boolean hasWeaponInSlot(int slot) {
        return weaponSlots.containsKey(slot);
    }

    public int getMaxSlots() {
        return MAX_SLOTS;
    }

    // Method to add a new weapon to the first available slot
    public boolean addWeaponToFirstEmptySlot(Weapon weapon) {
        for (int i = 0; i < MAX_SLOTS; i++) {
            if (!weaponSlots.containsKey(i)) {
                addWeapon(i, weapon);
                return true;
            }
        }
        return false; // No empty slots available
    }

    // Method to remove a weapon from a specific slot
    public void removeWeapon(int slot) {
        weaponSlots.remove(slot);
        if (slot == currentSlot) {
            // If we removed the current weapon, switch to another available weapon
            for (int i = 0; i < MAX_SLOTS; i++) {
                if (weaponSlots.containsKey(i)) {
                    switchToSlot(i);
                    break;
                }
            }
        }
    }
}