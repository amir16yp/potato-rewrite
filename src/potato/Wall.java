package potato;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Wall {
    protected static Textures DEFAULT_TEXTURES = null;
    protected int type;
    private static final Map<Integer, TextureLOD> lodCache = new HashMap<>();
    private static final double[] LOD_DISTANCES = {5.0, 10.0, 15.0, 20.0};

    public Wall(int type) {
        this.type = type;
    }

    public static void setDefaultTextures(Textures textures) {
        DEFAULT_TEXTURES = textures;
        lodCache.clear();
    }

    public int getType() {
        return type;
    }

    public void update() {
        // Base wall has no update logic
    }

    public BufferedImage getCurrentTexture(double distance) {
        TextureLOD lod = lodCache.computeIfAbsent(type, k -> {
            BufferedImage baseTexture = DEFAULT_TEXTURES.getTile(k);
            return new TextureLOD(baseTexture, LOD_DISTANCES);
        });
        return lod.getTextureForDistance(distance);
    }

    // Maintain compatibility with old code
    public BufferedImage getCurrentTexture() {
        return DEFAULT_TEXTURES.getTile(type);
    }
}