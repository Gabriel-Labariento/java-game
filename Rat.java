import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Rat extends Enemy{
    public static int ratCount = 0;
    private int id;
    private static final int SPRITE_FRAME_DURATION = 200;
    private long lastSpriteUpdate = 0;
    private int currSprite;
    private static BufferedImage[] sprites;

    static {
        setSprites();
    }

    public Rat(int x, int y) {
        id = ratCount++;
        identifier = NetworkProtocol.RAT.toCharArray()[0];
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
            BufferedImage left0 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/Rat/sprite_rat_left0.png"));
            BufferedImage left1 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/Rat/sprite_rat_left1.png"));
            BufferedImage left2 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/Rat/sprite_rat_left2.png"));
            BufferedImage right0 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/Rat/sprite_rat_right0.png"));
            BufferedImage right1 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites/Rat/sprite_rat_right1.png"));
            BufferedImage right2 = ImageIO.read(Rat.class.getResourceAsStream("resources/Sprites//Rat/sprite_rat_right2.png"));
            sprites = new BufferedImage[] {left0, left1, left2, right0, right1, right2};

        } catch (IOException e) {
            System.out.println("Exception in Rat setSprites()" + e);
        }
    }

    @Override
    public void matchHitBoxBounds() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
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

    public void setCurrSprite(int currSprite) {
        this.currSprite = currSprite;
    }

    @Override
    public void updateEntity(ServerMaster gsm){
        // TODO: ENEMY AI LOGIC
        long now = System.currentTimeMillis();

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
        // if (worldX > pursued.getWorldX()) {
        //     currSprite = 0;
        // } else currSprite = 3;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
