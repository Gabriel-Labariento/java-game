import java.awt.*;
import java.awt.geom.*;

public class LightScarf extends Item {

    public LightScarf(int x, int y){
        identifier = NetworkProtocol.LIGHTSCARF;
        worldX = x;
        worldY = y;
        currentRoom = null;

        matchHitBoxBounds();
    }

    @Override
    public void applyEffects(){
        initialCDDuration = owner.getCoolDownDuration();
        owner.setCoolDownDuration((int) Math.round(initialCDDuration*0.5));

        initialMaxHealth = owner.getMaxHealth();
        owner.setMaxHealth((int) Math.round(initialMaxHealth*0.75));

    }

    @Override
    public void removeEffects(){
        owner.setCoolDownDuration(initialCDDuration);
        owner.setMaxHealth(initialMaxHealth);
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.ORANGE);
        g2d.fill(sprite);
    }
}