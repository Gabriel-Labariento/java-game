import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class RatKing extends Enemy {
    private static final int SPRITE_FRAME_DURATION = 200;
    private long lastSpriteUpdate = 0;
    private static BufferedImage[] sprites;

    static {
        setSprites();
    }

    public RatKing(int x, int y) {
        id = -1; // Only one instance, doesn't really matter the value but there has to be one
        identifier = NetworkProtocol.RATKING;
        speed = 1;
        height = 48;
        width = 48;
        worldX = x;
        worldY = y;
        maxHealth = 10;
        hitPoints = maxHealth;
        damage = 2;
        rewardXP = 200;
        currentRoom = null;
        currSprite = 0;
    }

    private static void setSprites() {
        try {
            BufferedImage left0 = ImageIO.read(RatKing.class.getResourceAsStream("resources/Sprites/RatKing/ratking_left_0.png"));
            BufferedImage left1 = ImageIO.read(RatKing.class.getResourceAsStream("resources/Sprites/RatKing/ratking_left_1.png"));
            BufferedImage left2 = ImageIO.read(RatKing.class.getResourceAsStream("resources/Sprites/RatKing/ratking_left_blur.png"));
            BufferedImage right0 = ImageIO.read(RatKing.class.getResourceAsStream("resources/Sprites/RatKing/ratking_right_0.png"));
            BufferedImage right1 = ImageIO.read(RatKing.class.getResourceAsStream("resources/Sprites/RatKing/ratking_right_1.png"));
            BufferedImage right2 = ImageIO.read(RatKing.class.getResourceAsStream("resources/Sprites/RatKing/ratking_right_blur.png"));
            sprites = new BufferedImage[] {left0, left1, left2, right0, right1, right2};

        } catch (IOException e) {
            System.out.println("Exception in RatKing setSprites()" + e);
        }
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        g2d.drawImage(sprites[currSprite], xOffset, yOffset, width, height, null);
    }

    @Override
    public String getAssetData(boolean isUserPlayer) {
        StringBuilder sb = new StringBuilder();
        // System.out.println("In getAssetData of Rat, identifier is " + identifier);
        // String format: H,id,x,y,currentRoomId,currsprite|
        sb.append(identifier).append(NetworkProtocol.SUB_DELIMITER)
        .append(id).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldX).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldY).append(NetworkProtocol.SUB_DELIMITER)
        .append(currentRoom.getRoomId()).append(NetworkProtocol.SUB_DELIMITER)
        .append(currSprite).append(NetworkProtocol.DELIMITER);

        return sb.toString();
    }

    @Override
    public void matchHitBoxBounds() {
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY + 3;
        hitBoxBounds[1] = worldY + height - 3;
        hitBoxBounds[2]= worldX + 5;
        hitBoxBounds[3] = worldX + width - 5 ;
    }
    
    @Override
    public void updateEntity(ServerMaster gsm){
        // TODO: ENEMY AI LOGIC
        now = System.currentTimeMillis();

        Player pursued = scanForPlayer(gsm);
        if (pursued != null) pursuePlayer(pursued);
        else return;

        // Sprite walk update
        if (now - lastSpriteUpdate > SPRITE_FRAME_DURATION) {
            if (worldX > pursued.getWorldX()) {
                currSprite++;
                if (currSprite > 2) currSprite = 0;
            } else {
                currSprite++;
                if (currSprite < 3 || currSprite > 5) currSprite = 3;
            }
            lastSpriteUpdate = now;
        }

        matchHitBoxBounds();
    }

    
}
