
    import java.awt.Color;
    import java.awt.Graphics2D;
    import java.awt.geom.*;

    public class RatSlash extends Attack{
        public static final int HEIGHT = 16;
        public static final int WIDTH = 16;

        public RatSlash(Entity owner, int x, int y){
            attackNum++;
            id = attackNum;
            identifier = NetworkProtocol.RATSLASH.toCharArray()[0];
            this.owner = owner;
            isFriendly = false;
            damage = 1;
            width = WIDTH;
            height = HEIGHT;
            worldX = x;
            worldY = y;

            //For checking attack duration
            duration = 400;
            setExpirationTime(duration);

            matchHitBoxBounds();
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
        public void updateEntity(ServerMaster gsm) {
            attachToOwner();
        }
    }
