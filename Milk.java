import java.awt.*;
import java.awt.geom.*;

public class Milk extends Item {
    
    public Milk(int x, int y){
        identifier = NetworkProtocol.MILK;
        worldX = x;
        worldY = y;
        currentRoom = null;
        isConsumable = true;

        matchHitBoxBounds();
    }

    @Override
    public void applyEffects(){
        owner.setMaxHealth(owner.getMaxHealth() + 1);
        owner.setHitPoints(owner.getHitPoints() + 1);
    }

    @Override
    public void removeEffects(){
        
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.ORANGE);
        g2d.fill(sprite);
    }
}
