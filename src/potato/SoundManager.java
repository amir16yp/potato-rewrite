package potato;

import potato.entities.Entity;
import potato.entities.PlayerEntity;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {
    private final Map<String, byte[]> soundData = new HashMap<>();
    private static final float MAX_DISTANCE = 20.0f;

    public SoundManager() {
        loadSound("CHECK", "/potato/sounds/check.wav");
        loadSound("UNCHECK", "/potato/sounds/uncheck.wav");
        loadSound("CLICK", "/potato/sounds/click.wav");
        loadSound("SHOOT1", "/potato/sounds/shoot1.wav");
        loadSound("SHOOT2", "/potato/sounds/shoot2.wav");
    }

    private void loadSound(String name, String path) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(getClass().getResourceAsStream(path))
            );

            // Convert to mono PCM format
            AudioFormat stereoFormat = ais.getFormat();
            AudioFormat monoFormat = new AudioFormat(
                    stereoFormat.getSampleRate(),
                    16,
                    1,
                    true,
                    false
            );

            AudioInputStream monoStream = AudioSystem.getAudioInputStream(monoFormat, ais);

            byte[] data = new byte[monoStream.available()];
            monoStream.read(data);
            soundData.put(name, data);

            monoStream.close();
            ais.close();
        } catch (Exception e) {
            System.err.println("Failed to load sound: " + path);
            e.printStackTrace();
        }
    }

    public void playSoundEffect(String name) {
        Thread thread = new Thread(() -> playSound(name, 1.0f));
        thread.start();
    }

    public void playSoundEffect(String name, Entity source) {
        //if (!source.canSee(PlayerEntity.getPlayer())) return;
        float distance = (float) source.getDistance(PlayerEntity.getPlayer());
        float volume = calculateVolumeForDistance(distance);

        Thread thread = new Thread(() -> playSound(name, volume));
        thread.start();
    }

    private void playSound(String name, float volume) {
        if (!soundData.containsKey(name)) return;

        try {
            AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Line not supported");
                return;
            }

            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);

            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                float gain = 20f * (float) Math.log10(volume);
                gainControl.setValue(Math.max(-80f, Math.min(6f, gain)));
            }

            line.start();
            line.write(soundData.get(name), 0, soundData.get(name).length);
            line.drain();
            line.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private float calculateVolumeForDistance(float distance) {
        if (distance >= MAX_DISTANCE) return 0.0f;
        if (distance <= 0) return 1.0f;
        return 1.0f - (distance / MAX_DISTANCE);
    }

    public void close() {
        soundData.clear();
    }
}