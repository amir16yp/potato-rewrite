package potato;

import java.awt.image.BufferedImage;

public class TextureLOD {
    private final BufferedImage[] mipmaps;
    private final double[] distances;

    public TextureLOD(BufferedImage base, double[] distances) {
        this.distances = distances;
        int levels = Math.min(4, 1 + (int)(Math.log(Math.max(base.getWidth(), base.getHeight())) / Math.log(2)));
        mipmaps = new BufferedImage[levels];
        mipmaps[0] = base;

        for (int i = 1; i < levels; i++) {
            mipmaps[i] = generateMipmap(mipmaps[i-1]);
        }
    }

    private BufferedImage generateMipmap(BufferedImage source) {
        int width = Math.max(1, source.getWidth() / 2);
        int height = Math.max(1, source.getHeight() / 2);
        BufferedImage mipmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int x2 = x * 2;
                int y2 = y * 2;
                mipmap.setRGB(x, y, averageColors(
                        source.getRGB(x2, y2),
                        source.getRGB(Math.min(x2 + 1, source.getWidth() - 1), y2),
                        source.getRGB(x2, Math.min(y2 + 1, source.getHeight() - 1)),
                        source.getRGB(Math.min(x2 + 1, source.getWidth() - 1),
                                Math.min(y2 + 1, source.getHeight() - 1))
                ));
            }
        }
        return mipmap;
    }

    private int averageColors(int... colors) {
        int a = 0, r = 0, g = 0, b = 0;
        for (int color : colors) {
            a += (color >> 24) & 0xff;
            r += (color >> 16) & 0xff;
            g += (color >> 8) & 0xff;
            b += color & 0xff;
        }
        return ((a / colors.length) << 24) |
                ((r / colors.length) << 16) |
                ((g / colors.length) << 8) |
                (b / colors.length);
    }

    public BufferedImage getTextureForDistance(double distance) {
        int level = 0;
        for (int i = 0; i < distances.length && i < mipmaps.length - 1; i++) {
            if (distance > distances[i]) level = i + 1;
        }
        return mipmaps[level];
    }
}
