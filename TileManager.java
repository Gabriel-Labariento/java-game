import java.awt.Graphics2D;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TileManager {
    private GameCanvas gc;
    Tile[] tile;

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

    public void draw (Graphics2D g2d) {
        g2d.drawImage(tile[0].image, 0, 0, gc.TILESIZE, gc.TILESIZE, null);
    }
}
