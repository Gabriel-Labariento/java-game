import java.awt.Graphics2D;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TileManager {
    private GameCanvas gc;
    public Tile[] tile;

    public TileManager(GameCanvas gc) {
        this.gc = gc;
        tile = new Tile[10];
        getTileImage();
    }

    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("tile0.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw (Graphics2D g2d, Room room, int cameraX, int cameraY) {
        
        // Get the position of the room in the world
        int roomX = room.getX();
        int roomY = room.getY();
        
        // For each row and column
        for (int row = 0; row < room.getHEIGHT_TILES(); row++) {
            for (int col = 0; col < room.getWIDTH_TILES(); col++) {
                int screenX = roomX + col * gc.TILESIZE - cameraX;
                int screenY = roomY + row * gc.TILESIZE - cameraY;
                g2d.drawImage(tile[0].image, screenX, screenY, gc.TILESIZE, gc.TILESIZE, null);       
            }
        }
    }
}
