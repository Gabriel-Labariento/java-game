import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class PlayerSmash extends Attack{

    public PlayerSmash(int cid, Entity entity, int x, int y, int d, boolean isFriendly){
        attackNum++;
        id = attackNum;
        clientId = cid;
        identifier = NetworkProtocol.PLAYERSMASH;
        owner = entity;
        this.isFriendly = isFriendly;
        damage = d;
        width = 80;
        height = 80;
        worldX = x;
        worldY = y;
        //For checking attack duration
        duration = 800;
        setExpirationTime(duration);

        matchHitBoxBounds();
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.PINK);
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
    public void updateEntity(ServerMaster gsm) {
    }

}