import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class PlayerBullet extends Attack{
    public PlayerBullet(){            //For checking attack duration
            duration = 400;
            setExpirationTime(duration);

            matchHitBoxBounds();
            // TODO: temporarily commented, trying to see if causing bug 
        }

        @Override
        public void draw(Graphics2D g2d, int xOffset, int yOffset){
            Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
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
    public void updateEntity(ServerMaster gsm) {}
}