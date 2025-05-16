import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Snakelet extends Enemy{
    public static int snakeletCount = 0;
    private int id;
    private static final int SPRITE_FRAME_DURATION = 200;
    private long lastSpriteUpdate = 0;
    private static BufferedImage[] sprites;

    static {
        setSprites();
    }

    public Snakelet(int x, int y) {
        id = snakeletCount++;
        identifier = NetworkProtocol.SNAKELET;
        speed = 2;
        height = 16;
        width = 16;
        worldX = x;
        worldY = y;
        maxHealth = 15;
        hitPoints = maxHealth;
        damage = 1;
        rewardXP = 75;
        currentRoom = null;
        currSprite = 0;
        
    }

    private static void setSprites() {
        try {
            BufferedImage left0 = ImageIO.read(Snakelet.class.getResourceAsStream("resources/Sprites/Snakelet/snakelet_left0.png"));
            BufferedImage left1 = ImageIO.read(Snakelet.class.getResourceAsStream("resources/Sprites/Snakelet/snakelet_left1.png"));
            BufferedImage left2 = ImageIO.read(Snakelet.class.getResourceAsStream("resources/Sprites/Snakelet/snakelet_left2.png"));
            BufferedImage right0 = ImageIO.read(Snakelet.class.getResourceAsStream("resources/Sprites/Snakelet/snakelet_right0.png"));
            BufferedImage right1 = ImageIO.read(Snakelet.class.getResourceAsStream("resources/Sprites/Snakelet/snakelet_right1.png"));
            BufferedImage right2 = ImageIO.read(Snakelet.class.getResourceAsStream("resources/Sprites/Snakelet/snakelet_right2.png"));
            sprites = new BufferedImage[] {left0, left1, left2, right0, right1, right2};

        } catch (IOException e) {
            System.out.println("Exception in snakelet setSprites()" + e);
        }
    }

    @Override
    public void matchHitBoxBounds() {
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY + 3;
        hitBoxBounds[1] = worldY + height;
        hitBoxBounds[2]= worldX;
        hitBoxBounds[3] = worldX + width;
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        g2d.drawRect(xOffset, yOffset, width, height);
        g2d.drawImage(sprites[currSprite], xOffset, yOffset, width, height, null);
    }

    @Override
    public String getAssetData(boolean isUserPlayer) {
        StringBuilder sb = new StringBuilder();
        // System.out.println("In getAssetData of Rat, identifier is " + identifier);
        // String format: I,id,x,y,currentRoomId,currsprite|
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
