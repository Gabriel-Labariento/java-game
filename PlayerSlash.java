import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PlayerSlash extends Attack{
    public static BufferedImage sprite;
    static {
        try {
            sprite = ImageIO.read(PlayerSlash.class.getResourceAsStream("resources/Sprites/Attacks/playerslash.png"));
        } catch (IOException e) {
            System.out.println("Exception in setSprites()" + e);
        }
    }

    public PlayerSlash(int cid, Entity entity, int x, int y, int d, boolean isFriendly){
        attackNum++;
        id = attackNum;
        clientId = cid;
        identifier = NetworkProtocol.PLAYERSLASH;
        owner = entity;
        this.isFriendly = isFriendly;
        damage = d;
        //Temporary hitPoints allocation
        hitPoints = 100;
        
        width = 40;
        height = 40;
        worldX = x;
        worldY = y;

        //For checking attack duration
        duration = 400;
        setExpirationTime(duration);

        matchHitBoxBounds();
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        g2d.drawImage(sprite, xOffset, yOffset, width, height, null);
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
