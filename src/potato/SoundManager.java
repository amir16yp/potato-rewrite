package potato;

import javax.sound.sampled.*;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    /*
    TODO: volume adjustment based on distance from the player, for in-game sounds
     */
    
    
    private final Map<String, Clip> soundEffects; // For storing sound effect clips
    private Clip backgroundMusic;                 // For storing the background music clip
    private boolean isMusicPlaying = false;        // Flag to check if music is playing

    public SoundManager() {
        soundEffects = new HashMap<>();
        loadSoundEffect("CHECK", "/potato/sounds/check.wav");
        loadSoundEffect("UNCHECK", "/potato/sounds/uncheck.wav");
        loadSoundEffect("CLICK", "/potato/sounds/click.wav");
    }

    /**
     * Load a sound effect into memory from resources.
     * @param name The name of the sound effect.
     * @param pathFromJar The path to the sound effect file inside the JAR.
     */
    public void loadSoundEffect(String name, String pathFromJar) {
        try {
            Clip clip = Utils.loadClip(pathFromJar);
            soundEffects.put(name, clip);
        } catch (Exception e) {
            System.err.println("Error loading sound effect: " + pathFromJar);
            e.printStackTrace();
        }
    }

    /**
     * Play a sound effect by name.
     * @param name The name of the sound effect to play.
     */
    public void playSoundEffect(String name) {
        Clip clip = soundEffects.get(name);
        if (clip != null) {
            clip.setFramePosition(0);  // Rewind to the start of the sound effect
            new Thread(() -> {
                clip.start();
            }).start();
        } else {
            System.err.println("Sound effect not found: " + name);
        }
    }

    /**
     * Load background music and start it from resources.
     * @param pathFromJar The path to the background music file inside the JAR.
     */
    public void loadBackgroundMusic(String pathFromJar) {
        try {
            backgroundMusic = Utils.loadClip(pathFromJar);
        } catch (Exception e) {
            System.err.println("Error loading background music: " + pathFromJar);
            e.printStackTrace();
        }
    }

    /**
     * Play the background music in a loop.
     */
    public void playBackgroundMusic() {
        if (backgroundMusic != null && !isMusicPlaying) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);  // Play indefinitely
            isMusicPlaying = true;
        } else {
            System.err.println("Background music is either not loaded or already playing.");
        }
    }

    /**
     * Stop the background music from playing.
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && isMusicPlaying) {
            backgroundMusic.stop();
            isMusicPlaying = false;
        } else {
            System.err.println("No background music is currently playing.");
        }
    }

    /**
     * Pause the background music (will resume from where it left off).
     */
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && isMusicPlaying) {
            backgroundMusic.stop();
            isMusicPlaying = false;
        } else {
            System.err.println("No background music is currently playing.");
        }
    }

    /**
     * Resume the paused background music.
     */
    public void resumeBackgroundMusic() {
        if (backgroundMusic != null && !isMusicPlaying) {
            backgroundMusic.start();
            isMusicPlaying = true;
        } else {
            System.err.println("Background music is either not loaded or already playing.");
        }
    }

    /**
     * Stop all sound effects.
     */
    public void stopAllSoundEffects() {
        for (Clip clip : soundEffects.values()) {
            clip.stop();
        }
    }

    /**
     * Close all audio resources (important to call when exiting).
     */
    public void close() {
        stopAllSoundEffects();
        if (backgroundMusic != null) {
            backgroundMusic.close();
        }
        for (Clip clip : soundEffects.values()) {
            clip.close();
        }
    }
}
