import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MutatedPufferfish extends Enemy{
    
    private static BufferedImage[] sprites;

    static {
        setSprites();
    }

    public MutatedPufferfish(int x, int y) {
        lastSpriteUpdate = 0;
        lastAttackTime = 0;
        id = enemyCount++;
        identifier = NetworkProtocol.MUTATEDPUFFERFISH;
        speed = 1;
        height = 56;
        width = 56;
        worldX = x;
        worldY = y;
        maxHealth = 10;
        hitPoints = maxHealth;
        damage = 2;
        rewardXP = 100;
        currentRoom = null;
        currSprite = 0;
        attackCDDuration = 3500;
        
    }

     private static void setSprites() {
        try {
            BufferedImage left0 = ImageIO.read(MutatedPufferfish.class.getResourceAsStream("resources/Sprites/MutatedPufferfish/left0.png"));
            BufferedImage right0 = ImageIO.read(MutatedPufferfish.class.getResourceAsStream("resources/Sprites/MutatedPufferfish/right0.png"));
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
        g2d.drawImage(sprites[currSprite], xOffset, yOffset, width, height, null);
    }

    @Override
    public void updateEntity(ServerMaster gsm){
        // TODO: ENEMY AI LOGIC
        long now = System.currentTimeMillis();
        final double ACTION_DISTANCE = (GameCanvas.TILESIZE * 5) * (GameCanvas.TILESIZE * 5);

        Player pursued = scanForPlayer(gsm);
        if (pursued == null) return;
        double distanceSquared = getSquaredDistanceBetween(this, pursued);
        if (distanceSquared <= ACTION_DISTANCE) {
            if (now - lastAttackTime > attackCDDuration) {
                createRandomSmash(gsm, pursued);
                lastAttackTime = now;
            }
            runFromPlayer(pursued);
        } else {
            pursuePlayer(pursued);
        }
        
        // Sprite walk update
        if (now - lastSpriteUpdate > SPRITE_FRAME_DURATION) {
            if (worldX > pursued.getWorldX()) {
                currSprite = 0;
            } else {
                currSprite = 1;
            }
            lastSpriteUpdate = now;
        }

        matchHitBoxBounds();
    }

    public void createRandomSmash(ServerMaster gsm, Player target){
        int vectorX = target.getCenterX() - getCenterX();
        int vectorY = target.getCenterY() - getCenterY(); 
        double normalizedVector = Math.sqrt((vectorX*vectorX)+(vectorY*vectorY));

        //Avoids 0/0 division edge case
        if (normalizedVector == 0) normalizedVector = 1; 
        double normalizedX = vectorX / normalizedVector;
        double normalizedY = vectorY / normalizedVector;

        double randomDirX = (Math.random() * 2) * normalizedX;
        double randomDirY = (Math.random() * 2) * normalizedY;

        int spawnDistance = GameCanvas.TILESIZE * 5;
        int spawnX = ((int) (this.getCenterX() + randomDirX * spawnDistance)) - PlayerSlash.WIDTH / 2;
        int spawnY = ((int) (this.getCenterY() + randomDirY * spawnDistance)) - PlayerSlash.HEIGHT / 2;
        
        gsm.addEntity(new PlayerSmash(id, null, spawnX, spawnY, damage, false));
    }
}