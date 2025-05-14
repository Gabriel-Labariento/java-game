import java.awt.*;
import java.awt.geom.*;

public class PringlesCan extends Item {
    
    public PringlesCan(int x, int y){
        identifier = NetworkProtocol.PRINGLESCAN;
        worldX = x;
        worldY = y;
        currentRoom = null;

        matchHitBoxBounds();
    }

    @Override
    public void applyEffects(){
        initialDefense = owner.getDefense();
        owner.setDefense(initialDefense+50);

        initialCDDuration = owner.getCoolDownDuration();
        owner.setCoolDownDuration((int) Math.round(initialCDDuration*1.25));

        initialDamage = owner.getDamage();
        owner.setDamage((int) Math.round(initialDamage*0.75));
    }

    @Override
    public void removeEffects(){
        owner.setDefense(initialDefense);
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