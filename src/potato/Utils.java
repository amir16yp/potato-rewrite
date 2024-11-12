package potato;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
    public static Clip loadClip(String pathFromJar) {
        try {
            // Get resource as stream
            InputStream inputStream = Utils.class.getResourceAsStream(pathFromJar);
            if (inputStream == null) {
                throw new RuntimeException("Resource not found: " + pathFromJar);
            }

            // Convert to byte array first to allow mark/reset
            byte[] buffer = toByteArray(inputStream);
            InputStream bufferedInput = new ByteArrayInputStream(buffer);

            // Get and open the audio input stream
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInput);

            // Create and open the clip
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException("Failed to load audio clip: " + pathFromJar, e);
        }
    }

    // Helper method to read input stream to byte array
    private static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
}
