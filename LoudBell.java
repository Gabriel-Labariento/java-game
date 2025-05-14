import java.awt.*;
import java.awt.geom.*;

public class LoudBell extends Item {
    
    public LoudBell(int x, int y){
        identifier = NetworkProtocol.LOUDBELL;
        worldX = x;
        worldY = y;
        currentRoom = null;

        matchHitBoxBounds();
    }

    @Override
    public void applyEffects(){
        initialDefense = owner.getDefense();
        owner.setDefense(initialDefense-100);

        initialCDDuration = owner.getCoolDownDuration();
        owner.setCoolDownDuration((int) Math.round(initialCDDuration*0.75));

        initialDamage = owner.getDamage();
        owner.setDamage((int) Math.round(initialDamage*1.25));
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