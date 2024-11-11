package potato;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Utils {

    /**
     * Loads an image from the resources packaged in the JAR.
     *
     * @param pathFromJar Path to the image within the JAR (relative to the root).
     * @return The loaded BufferedImage.
     * @throws RuntimeException if the image cannot be loaded.
     */
    public static final BufferedImage loadImage(String pathFromJar) {
        try {
            return ImageIO.read(Utils.class.getResourceAsStream(pathFromJar));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load image: " + pathFromJar, e);
        }
    }

    /**
     * Loads an audio clip from the resources packaged in the JAR.
     *
     * @param pathFromJar Path to the audio file within the JAR (relative to the root).
     * @return The loaded Clip.
     * @throws RuntimeException if the audio clip cannot be loaded.
     */
    public static final Clip loadClip(String pathFromJar) {
        try {
            // Get the audio input stream
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(Utils.class.getResourceAsStream(pathFromJar));

            // Get a clip from the audio system
            Clip clip = AudioSystem.getClip();

            // Open the clip with the audio input stream
            clip.open(audioIn);

            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException("Failed to load audio clip: " + pathFromJar, e);
        }
    }
}
