import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Cockroach extends Enemy{
    private static final int SPRITE_FRAME_DURATION = 200;
    private static final int IDLE_DURATION = 400;
    private static final int ATTACK_DURATION = 300;
    private long lastSpriteUpdate = 0;
    private long lastStateChangeTime = 0;
     private static final int ATTACK_RANGE = GameCanvas.TILESIZE * 2;
    private static BufferedImage[] sprites;
    private enum State {IDLE, PURSUE, ATTACK}; 
    private State currentState;
    
    static {
        setSprites();
    }

    public Cockroach(int x, int y) {
        id = enemyCount++;
        identifier = NetworkProtocol.COCKROACH;
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
        currentState = State.IDLE;
    }

     private static void setSprites() {
        try {
            BufferedImage up0 = ImageIO.read(Cockroach.class.getResourceAsStream("resources/Sprites/Cockroach/cockroach_up0.png"));
            BufferedImage up1 = ImageIO.read(Cockroach.class.getResourceAsStream("resources/Sprites/Cockroach/cockroach_up1.png"));
            BufferedImage down0 = ImageIO.read(Cockroach.class.getResourceAsStream("resources/Sprites/Cockroach/cockroach_down0.png"));
            BufferedImage down1 = ImageIO.read(Cockroach.class.getResourceAsStream("resources/Sprites/Cockroach/cockroach_down1.png"));

            sprites = new BufferedImage[] {up0, up1, down0, down1};

        } catch (IOException e) {
            System.out.println("Exception in Cockroach setSprites()" + e);
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
        // TODO: ENEMY AI LOGIC
        long now = System.currentTimeMillis();

        Player pursued = scanForPlayer(gsm);
        if (pursued == null) return;

        // Sprite walk update
        if (now - lastSpriteUpdate > SPRITE_FRAME_DURATION) {
            if (worldY > pursued.getWorldY()) {
                currSprite = (currSprite == 1) ? 0 : 1;
            } else if (worldY < pursued.getWorldY()) {
                currSprite = (currSprite == 2) ? 3 : 2;
            }
            lastSpriteUpdate = now;
        }

        double distanceSquared = getSquaredDistanceBetween(this, pursued);

        switch (currentState) {
            case IDLE:
                if (now - lastStateChangeTime > IDLE_DURATION) {
                    currentState = State.PURSUE;
                    lastStateChangeTime = now;
                }
                break;

            case PURSUE:
                pursuePlayer(pursued);
                if (distanceSquared <= ATTACK_RANGE * ATTACK_RANGE) {
                    currentState = State.ATTACK;
                    lastStateChangeTime = now;
                }
                break;

            case ATTACK:
                if (now - lastStateChangeTime > ATTACK_DURATION) { 
                    initiateJump(pursued);
                    currentState = State.IDLE;
                    lastStateChangeTime = now;
                } 
                break;

            default:
                throw new AssertionError();
        }

        matchHitBoxBounds();
    }

    private void initiateJump(Player target){
        int vectorX = target.getCenterX() - getCenterX();
        int vectorY = target.getCenterY() - getCenterY(); 
        double normalizedVector = Math.sqrt((vectorX*vectorX)+(vectorY*vectorY));

        //Avoids 0/0 division edge case
        if (normalizedVector == 0) normalizedVector = 1; 
        double normalizedX = vectorX / normalizedVector;
        double normalizedY = vectorY / normalizedVector;

        int jumpDistance = GameCanvas.TILESIZE * 3;

        int newX = (int) (worldX + normalizedX * jumpDistance);
        int newY = (int) (worldY + normalizedY * jumpDistance);

        setPosition(newX, newY);
    }
}
