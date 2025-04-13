import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class Player extends Entity{
    private int screenX;
    private int screenY;

    public Player(int cid, int x, int y){
        this.clientId = cid;
        this.identifier = 'A';
        speed = 5;
        height = 50;
        width = 50;
        screenX = 720/2 - width/2;
        screenY = 540/2 - height/2;
        worldX = x;
        worldY = y;
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.GREEN);
        g2d.fill(sprite);
    }

    public void update(char input){
        if(input == 'W')
            worldY -= speed;
        if(input == 'A')
            worldX -= speed;
        if(input == 'S')
            worldY += speed;
        if(input == 'D')
            worldX += speed;
    }

    public int[] getScreenPos() {
        int[] screenPos = {screenX, screenY};
        return screenPos;
    }
   
}
