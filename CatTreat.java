import java.awt.*;
import java.awt.geom.*;

public class CatTreat extends Item {
    
    public CatTreat(int x, int y){
        identifier = NetworkProtocol.CATTREAT;
        worldX = x;
        worldY = y;
        currentRoom = null;
        isConsumable = true;

        matchHitBoxBounds();
    }

    @Override
    public void applyEffects(){
        double addedXP = Math.round((owner.getCurrentXPCap() - owner.getPastXPCap())*0.10);
        owner.applyXP((int) addedXP);
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