import java.awt.*;

public class Tile {
    private int x, y;
    private Color color = Color.BLACK;
    
    public Tile( int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void drawTile(Graphics2D g2d, int tileWidth, int tileHeight){
        g2d.setColor(color);
        g2d.fillRect(x, y, tileWidth, tileHeight);
    }

}