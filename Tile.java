
import java.awt.image.*;

public class Tile extends GameObject{

    public BufferedImage image;
    public boolean collision = false;


    public BufferedImage defaultFloorImage;


    @Override
    public void matchHitBoxBounds() {
        // TODO Auto-generated method stub
        
    }

    // private int x, y;
    // public static final int SIZE = 16;

    // public Tile(int x, int y){
    //     this.x = x * SIZE;
    //     this.y = y * SIZE;     
    // }

    // public void draw(Graphics2D g2d) {
    //     g2d.drawImage(image, x, y, SIZE, SIZE, null);
    // }


}