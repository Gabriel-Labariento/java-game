
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class PlayerSlash extends Attack{

    public PlayerSlash(int cid, int x, int y, int w, int h, double damage, boolean isFriendly, int speed){
        clientId = cid;
        identifier = 'B';
        this.isFriendly = isFriendly;
        this.speed = speed;
        this.damage = damage;
        //Temporary hitPoints allocation
        hitPoints = 100;
        height = h;
        width = w;
        worldX = x;
        worldY = y;

        //For checking attack duration
        duration = 400;
        setExpirationTime(duration);

        matchHitBoxBounds();
    }

    @Override
    public void draw(Graphics2D g2d, int screenX, int screenY){
        Rectangle2D.Double sprite = new Rectangle2D.Double(screenX, screenY, width, height);
        g2d.setColor(Color.RED);
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
    public String getAssetData(boolean isUserPlayer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void updateEntity(ServerMaster gsm) {
        // TODO Auto-generated method stub
    }
}
