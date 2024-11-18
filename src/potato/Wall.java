package potato;

import java.awt.image.BufferedImage;

public class Wall {
    protected static Textures DEFAULT_TEXTURES = null;

    protected int type;

    public Wall(int type) {
        this.type = type;
    }

    public static void setDefaultTextures(Textures textures) {
        DEFAULT_TEXTURES = textures;
    }

    public int getType() {
        return type;
    }

    public void update() {
        // Base wall has no update logic
    }

    public BufferedImage getCurrentTexture() {
        return DEFAULT_TEXTURES.getTile(type);
    }
}

