import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class Player extends Entity{
    private static final int invincibilityDuration = 800;
    private long invincibilityEnd;

    public Player(int cid, int x, int y){
        this.clientId = cid;
        this.identifier = 'A';
        speed = 5;
        height = 50;
        width = 50;
        hitPoints = 3;
        damage = 5;
        worldX = x;
        worldY = y;
        matchHitBoxBounds();
    }
 
    public void triggerInvincibility(){
        invincibilityEnd = System.currentTimeMillis() + invincibilityDuration;
    }

    public boolean getIsInvincible(){
        return System.currentTimeMillis() >= invincibilityEnd;
    }

    @Override
    public void draw(Graphics2D g2d, int screenX, int screenY){
        Rectangle2D.Double sprite = new Rectangle2D.Double(screenX, screenY, width, height);
        g2d.setColor(Color.GREEN);
        g2d.fill(sprite);
    }

    @Override
    public void matchHitBoxBounds(){
        // Bounds array is formatted as such: top, bottom, left, right; SIZES SUBJECT TO CHANGE PER ENTITY
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY;
        hitBoxBounds[1] = worldY + height;
        hitBoxBounds[2]= worldX;
        hitBoxBounds[3] = worldX + width;
    }

    @Override
    public String getAssetData(){
        return "" + identifier + worldX + ',' + worldY + '@' + hitPoints;
    }
}
