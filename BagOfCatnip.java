import java.awt.*;
import java.awt.geom.*;

public class BagOfCatnip extends Item {
    
    public BagOfCatnip(int x, int y){
        identifier = NetworkProtocol.BAGOFCATNIP;
        worldX = x;
        worldY = y;
        currentRoom = null;

        matchHitBoxBounds();
    }

    @Override
    public void applyEffects(){
        initialCDDuration = owner.getCoolDownDuration();
        owner.setCoolDownDuration((int) Math.round(initialCDDuration*1.5));

        initialDamage = owner.getDamage();
        owner.setDamage((int) Math.round(initialDamage*2.0));
    }

    @Override
    public void removeEffects(){
        owner.setCoolDownDuration(initialCDDuration);
        owner.setDamage(initialDamage);
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.ORANGE);
        g2d.fill(sprite);
    }
}
