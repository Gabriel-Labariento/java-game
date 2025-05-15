import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FrogSmash extends Attack {
    public static final int SMASH_RADIUS = GameCanvas.TILESIZE * 2;
    private static BufferedImage sprite;

    static {
        try {
            BufferedImage img = ImageIO.read(FrogSmash.class.getResourceAsStream("resources/Sprites/Frog/frog_smash.png"));
            sprite = img;
        } catch (IOException e) {
            System.out.println("Exception in FrogSmash setSprite()" + e);
        }
    }


    public FrogSmash(Entity owner, int x, int y){
        attackNum++;
        id = attackNum;
        identifier = NetworkProtocol.FROGSMASH;
        this.owner = owner;
        isFriendly = false;
        damage = 1;
        width = 2 * SMASH_RADIUS;
        height = 2 * SMASH_RADIUS;
        worldX = x;
        worldY = y;

        //For checking attack duration
        duration = 800;
        setExpirationTime(duration);

        matchHitBoxBounds();
    }

    
    @Override
    public void updateEntity(ServerMaster gsm) {}

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        g2d.drawImage(sprite, xOffset, yOffset, width, height, null);
    }

    @Override
    public void matchHitBoxBounds() {
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY;
        hitBoxBounds[1] = worldY + height;
        hitBoxBounds[2]= worldX;
        hitBoxBounds[3] = worldX + width;
    }
    
}