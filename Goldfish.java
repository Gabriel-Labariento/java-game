import java.awt.*;
import java.awt.geom.*;

public class Goldfish extends Item {
    
    public Goldfish(int x, int y){
        identifier = NetworkProtocol.GOLDFISH.charAt(0);
        worldX = x;
        worldY = y;
        currentRoom = null;

        matchHitBoxBounds();
    }

    public void applyEffects(){
        double restoredHP = Math.round(owner.getMaxHealth()*0.25);
        owner.setHitPoints(owner.getHitPoints() + (int) restoredHP);
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.WHITE);
        g2d.fill(sprite);
    }
}