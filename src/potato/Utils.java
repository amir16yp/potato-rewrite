package potato;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Utils
{
    public static final BufferedImage loadImage(String pathFromJar)
    {
        try {
            return ImageIO.read(Utils.class.getResourceAsStream(pathFromJar));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
