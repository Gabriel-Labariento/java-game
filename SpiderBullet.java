
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

 public class SpiderBullet extends Attack {
        public static final int HEIGHT = 16;
        public static final int WIDTH = 16;
        double normalizedX, normalizedY;

        public SpiderBullet(Entity owner, int x, int y, double nX, double nY){
            attackNum++;
            id = attackNum;
            identifier = NetworkProtocol.SPIDERBULLET.toCharArray()[0];
            this.owner = owner;
            isFriendly = false;
            damage = 1;
            //Temporary hitPoints allocation
            width = 16;
            height = 16;
            worldX = x;
            worldY = y;
            speed = 3;
            normalizedX = nX;
            normalizedY = nY;

            //For checking attack duration
            duration = 2000;
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
        public void updateEntity(ServerMaster gsm) {
            moveBullet();
        }

        @Override
        public void matchHitBoxBounds() {
            hitBoxBounds = new int[4];
            hitBoxBounds[0]= worldY;
            hitBoxBounds[1] = worldY + height;
            hitBoxBounds[2]= worldX;
            hitBoxBounds[3] = worldX + width;
        }

        private void moveBullet(){
            worldX += (int) speed* normalizedX;
            worldY += (int) speed* normalizedY;
            matchHitBoxBounds();
        }
        
    }