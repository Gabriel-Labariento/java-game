import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SecurityBot extends Enemy{
    public static int ratCount = 0;
    private static final int SPRITE_FRAME_DURATION = 200;
    private long lastSpriteUpdate = 0;
    private static BufferedImage[] sprites;

    static {
        setSprites();
    }

    public SecurityBot(int x, int y) {
        identifier = NetworkProtocol.SECURITYBOT.toCharArray()[0];
        speed = 1;
        height = 16;
        width = 16;
        worldX = x;
        worldY = y;
        maxHealth = 10;
        hitPoints = maxHealth;
        damage = 1;
        rewardXP = 50;
        currentRoom = null;
        currSprite = 0;
        
    }

     private static void setSprites() {
        try {
            BufferedImage left0 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/SecurityBot/left0.png"));
            BufferedImage left1 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/SecurityBot/left1.png"));
            BufferedImage left2 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/SecurityBot/left2.png"));
            BufferedImage right0 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/SecurityBot/right0.png"));
            BufferedImage right1 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/SecurityBot/right1.png"));
            BufferedImage right2 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/SecurityBot/right2.png"));
            sprites = new BufferedImage[] {left0, left1, left2, right0, right1, right2};

        } catch (IOException e) {
            System.out.println("Exception in Rat setSprites()" + e);
        }
    }

    @Override
    public void matchHitBoxBounds() {
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY;
        hitBoxBounds[1] = worldY + height;
        hitBoxBounds[2]= worldX;
        hitBoxBounds[3] = worldX + width;
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        // g2d.drawRect(xOffset, yOffset, width, height);
        g2d.drawImage(sprites[currSprite], xOffset, yOffset, width, height, null);
    }

    @Override
    public String getAssetData(boolean isUserPlayer) {
        StringBuilder sb = new StringBuilder();
        // System.out.println("In getAssetData of Rat, identifier is " + identifier);
        // String format: B,id,x,y,currentRoomId,currsprite|
        sb.append(identifier).append(NetworkProtocol.SUB_DELIMITER)
        .append(id).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldX).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldY).append(NetworkProtocol.SUB_DELIMITER)
        .append(currentRoom.getRoomId()).append(NetworkProtocol.SUB_DELIMITER)
        .append(currSprite).append(NetworkProtocol.DELIMITER);

        return sb.toString();
    }

    @Override
    public void updateEntity(ServerMaster gsm){

        // TODO: ENEMY AI LOGIC
        long now = System.currentTimeMillis();

        Player pursued = scanForPlayer(gsm);
        if (pursued == null) return;
        if (!(getSquaredDistanceBetween(this, pursued) < (GameCanvas.TILESIZE * 4) * (GameCanvas.TILESIZE * 4))) 
            pursuePlayer(pursued);
        else
            runFromPlayer(pursued);
        
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
