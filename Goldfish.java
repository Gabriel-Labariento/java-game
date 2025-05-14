import java.awt.*;
import java.awt.geom.*;

public class Goldfish extends Item {
    
    public Goldfish(int x, int y){
        identifier = NetworkProtocol.GOLDFISH;
        worldX = x;
        worldY = y;
        currentRoom = null;

        matchHitBoxBounds();
    }

    @Override
    public void applyEffects(){
    }

    @Override
    public void removeEffects(){
        
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.WHITE);
        g2d.fill(sprite);
    }
}