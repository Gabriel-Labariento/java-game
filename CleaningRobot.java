import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class CleaningRobot extends Enemy{
    public static int cleaningRobotNum = 0;
    private static final int SPRITE_FRAME_DURATION = 200;
    private static BufferedImage[] sprites;

    static {
        setSprites();
    }

    public CleaningRobot(int x, int y) {
        lastSpriteUpdate = 0;
        lastAttackTime = 0;
        id = cleaningRobotNum++;
        identifier = NetworkProtocol.CLEANINGBOT;
        speed = 1;
        height = 32;
        width = 32;
        worldX = x;
        worldY = y;
        maxHealth = 10;
        hitPoints = maxHealth;
        damage = 1;
        rewardXP = 50;
        currentRoom = null;
        currSprite = 0;
        attackCDDuration = 250;
        
    }

     private static void setSprites() {
        try {
            BufferedImage left0 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/Rat/rat_left0.png"));
            BufferedImage right0 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/Rat/rat_right0.png"));
            sprites = new BufferedImage[] {left0, right0};

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
        g2d.drawRect(xOffset, yOffset, width, height);
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
        // // TODO: ENEMY AI LOGIC
        // long now = System.currentTimeMillis();
        // final double AGGRO_DISTANCE = GameCanvas.TILESIZE;

        // Player pursued = scanForPlayer(gsm);
        // if (pursued == null) return;
        // double distanceSquared = getSquaredDistanceBetween(this, pursued);
        // if (distanceSquared <= AGGRO_DISTANCE * AGGRO_DISTANCE) {
        //     if (now - lastAttackTime > attackCDDuration) {
        //         createLaserBullet()
        //         lastAttackTime = now;
        //     }
        // } else {
        //     pursuePlayer(pursued);
        // }
        

        // // Sprite walk update
        // if (now - lastSpriteUpdate > SPRITE_FRAME_DURATION) {
        //     if (worldX > pursued.getWorldX()) {
        //         currSprite = 0;
        //     } else {
        //         currSprite = 1;
        //     }
        //     lastSpriteUpdate = now;
        // }

        // matchHitBoxBounds();
    }
}

