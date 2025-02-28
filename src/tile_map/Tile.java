package tile_map;

import java.awt.image.BufferedImage;

public class Tile {
    public BufferedImage tileImage;
    public boolean colDetector = false;
    public boolean isBreakable = false;
    public boolean collable = false;
    public boolean playerColDetector = false;
    public String activateString = "null";
}
