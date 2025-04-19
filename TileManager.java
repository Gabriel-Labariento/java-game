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
            // TODO : FIX TILE IMPLEMENTATION
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("tile0.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("wallTile1.png")); 

        } catch (IOException e) {
            System.out.println("IOException in getTileImage");
        }
    }

  
    public void drawRoom (Graphics2D g2d, Room room, int cameraX, int cameraY) {
        // Get the position of the room in the world
        int roomX = room.getX();
        int roomY = room.getY();
        
        // For each row and column
        for (int row = 0; row < room.getHEIGHT_TILES(); row++) {
            for (int col = 0; col < room.getWIDTH_TILES(); col++) {
                int screenX = roomX + col * GameCanvas.TILESIZE - cameraX;
                int screenY = roomY + row * GameCanvas.TILESIZE - cameraY;
                g2d.drawImage(tile[0].image, screenX, screenY, GameCanvas.TILESIZE, GameCanvas.TILESIZE, null);       
            }
        }
    }
}
