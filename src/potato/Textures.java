package potato;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Textures {
    private final Map<Integer, BufferedImage> tiles;
    private final int tileWidth;
    private final int tileHeight;
    private final Logger logger;
    private final String tilesetPath;
    private BufferedImage tilesetImage;

    public Textures(String tilesetPath, int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tiles = new HashMap<>();
        this.logger = new Logger(this.getClass().getName());
        this.logger.addPrefix(tilesetPath);
        this.tilesetImage = Utils.loadImage(tilesetPath);
        this.tilesetPath = tilesetPath;
    }

    private BufferedImage loadTile(int id) {
        int cols = tilesetImage.getWidth() / tileWidth;
        int row = (id - 1) / cols;
        int col = (id - 1) % cols;

        return tilesetImage.getSubimage(
                col * tileWidth, row * tileHeight, tileWidth, tileHeight);
    }

    public BufferedImage getTile(int id) {
        BufferedImage tile = tiles.get(id);
        if (tile == null) {
            try {
                tile = loadTile(id);
                tiles.put(id, tile);
                logger.log("Loaded tile with ID " + id);
            } catch (Exception e) {
                logger.log("Warning: Failed to load tile with ID " + id + ": " + e.getMessage());
                tile = createPlaceholderTile();
            }
        }
        return tile;
    }

    private BufferedImage createPlaceholderTile() {
        BufferedImage placeholder = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
        // Fill with a noticeable color or pattern
        // For example, a magenta and black checkered pattern
        for (int y = 0; y < tileHeight; y++) {
            for (int x = 0; x < tileWidth; x++) {
                placeholder.setRGB(x, y, ((x + y) % 2 == 0) ? 0xFFFF00FF : 0xFF000000);
            }
        }
        return placeholder;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public int getTileCount() {
        int cols = tilesetImage.getWidth() / tileWidth;
        int rows = tilesetImage.getHeight() / tileHeight;
        return cols * rows;
    }
}