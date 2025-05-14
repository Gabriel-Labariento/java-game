import java.awt.*;
import java.awt.geom.*;

public class ThickSweater extends Item {
    private long regenTime;
    private static final int REGENCDDURATION = 5000;
    private boolean isFirstTimeUse;

    
    public ThickSweater(int x, int y){
        identifier = NetworkProtocol.THICKSWEATER;
        worldX = x;
        worldY = y;
        currentRoom = null;
        isFirstTimeUse = true;
        regenTime = 0;

        matchHitBoxBounds();

    }

    @Override
    public void applyEffects(){
        initialSpeed = owner.getSpeed();
        owner.setSpeed((int) Math.floor(initialSpeed*0.5));
    }

    @Override
    public void removeEffects(){
        owner.setSpeed(initialSpeed);
    }

    public void triggerRegenSystem(){
        if(regenTime < System.currentTimeMillis()){     
            triggerRegenTimer();

            //Stop healspam when dropping and picking up item
            if (!isFirstTimeUse) owner.setHitPoints(owner.getHitPoints() + 1);
            isFirstTimeUse = false;
        }
    }

    private void triggerRegenTimer(){
        regenTime = System.currentTimeMillis() + REGENCDDURATION;
    }

    public void setIsFirstTimeUse(boolean b){
        isFirstTimeUse = b;
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.ORANGE);
        g2d.fill(sprite);
    }
}