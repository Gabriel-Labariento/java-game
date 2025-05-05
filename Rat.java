import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Rat extends Enemy{
    public static int ratCount = 0;
    private int id;
    private BufferedImage[] sprites;
    private static final int IDLE_DURATION = 400;
    private static final int ATTACK_DURATION = 300;
    private long lastSpriteUpdate = 0;
    private static final int SPRITE_FRAME_DURATION = 200;
    private static final int ATTACK_RANGE = GameCanvas.TILESIZE * 2; // tiles away
    private int baseSpeed = 1;
    private long lastStateChangeTime = 0;
    private int currSprite;

    public Rat(int x, int y) {
        id = ratCount++;
        identifier = NetworkProtocol.RAT.toCharArray()[0];
        speed = baseSpeed;
        height = 16;
        width = 16;
        worldX = x;
        worldY = y;
        maxHealth = 10;
        hitPoints = maxHealth;
        damage = 1;
        currentRoom = null;
        currSprite = 0;
        setSprites();
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
        g2d.drawImage(sprites[currSprite], xOffset, yOffset, width, height, null);
    }

    @Override
    public String getAssetData(boolean isUserPlayer) {
        StringBuilder sb = new StringBuilder();
        // System.out.println("In getAssetData of Rat, identifier is " + identifier);
        // String format: B,id,x,y,currentRoomId,sprite|
        sb.append(identifier).append(NetworkProtocol.SUB_DELIMITER)
        .append(id).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldX).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldY).append(NetworkProtocol.SUB_DELIMITER)
        .append(currentRoom.getRoomId()).append(NetworkProtocol.DELIMITER);
        // .append(currentRoom.getRoomId()).append(NetworkProtocol.SUB_DELIMITER)1
        // .append(currSprite).append(NetworkProtocol.DELIMITER);
        return sb.toString();
    }

    @Override
    public void updateEntity(ServerMaster gsm){
        // TODO: ENEMY AI LOGIC
        long now = System.currentTimeMillis();
    
        Player pursued = scanForPlayer(gsm);

        if (pursued == null) return;

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


        // Set distance parameters
        double distance = getDistanceBetween(this, pursued);
        
        switch (currentState) {
            case IDLE:
                // TODO: RANDOM MOVEMENT
                if (now - lastStateChangeTime > IDLE_DURATION) {
                    currentState = State.PURSUE;
                    lastStateChangeTime = now;
                }
                break;

            case PURSUE:
                pursuePlayer(pursued);
                if (distance <= ATTACK_RANGE) {
                    currentState = State.ATTACK;
                    lastStateChangeTime = now;
                }
                break;

            case ATTACK:
                if (now - lastStateChangeTime > ATTACK_DURATION) { 
                    attackPlayer(pursued, distance);
                    currentState = State.IDLE;
                    lastStateChangeTime = now;
                } 
                break;
            default:
                throw new AssertionError();
        }

        matchHitBoxBounds();
        
    }
    

    private void setSprites() {
        try {
            BufferedImage left0 = ImageIO.read(getClass().getResourceAsStream("Sprites\\Rat\\sprite_rat_left0.png"));
            BufferedImage left1 = ImageIO.read(getClass().getResourceAsStream("Sprites\\Rat\\sprite_rat_left1.png"));
            BufferedImage left2 = ImageIO.read(getClass().getResourceAsStream("Sprites\\Rat\\sprite_rat_left2.png"));
            BufferedImage right0 = ImageIO.read(getClass().getResourceAsStream("Sprites\\Rat\\sprite_rat_right0.png"));
            BufferedImage right1 = ImageIO.read(getClass().getResourceAsStream("Sprites\\Rat\\sprite_rat_right1.png"));
            BufferedImage right2 = ImageIO.read(getClass().getResourceAsStream("Sprites\\Rat\\sprite_rat_right2.png"));
            sprites = new BufferedImage[] {left0, left1, left2, right0, right1, right2};

        } catch (IOException e) {
            System.out.println("IOException in setImage of " + getClass() + getId());
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCurrSprite() {
        return currSprite;
    }

    public void setCurrSprite(int currSprite) {
        this.currSprite = currSprite;
    }


}
