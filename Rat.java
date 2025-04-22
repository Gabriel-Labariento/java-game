import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Rat extends Entity{
    public static int ratCount = 0;
    private int id;
    private boolean isIdle;

    public Rat(int x, int y) {
        id = ratCount++;
        identifier = NetworkProtocol.RAT.toCharArray()[0];
        speed = 1;
        height = 16;
        width = 16;
        worldX = x;
        worldY = y;
        centerX = calculateCenterX(x);
        centerY = calculateCenterY(y);
        maxHealth = 10;
        health = maxHealth;
        currentRoom = null;
        isIdle = true;
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.RED);
        g2d.fill(sprite);
    }

    @Override
    public String getAssetData(boolean isUserPlayer) {
        StringBuilder sb = new StringBuilder();
        // System.out.println("In getAssetData of Rat, identifier is " + identifier);
        // String format: B,id,x,y,currentRoomId|
        sb.append(identifier).append(NetworkProtocol.SUB_DELIMITER)
        .append(id).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldX).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldY).append(NetworkProtocol.SUB_DELIMITER)
        .append(currentRoom.getRoomId()).append(NetworkProtocol.DELIMITER);

        return sb.toString();
    }

    @Override
    public void updateEntity(GameStateManager gsm){
        // TODO: ENEMY AI LOGIC
        updateCenterCoordinates();

        Player pursued = scanForPlayer(gsm);
        if (pursued != null) pursuePlayer(pursued);

        for (Entity e : gsm.getEntities()) {
            if (e == this) continue;
            if (e instanceof Player) continue;
            if (isCollidingWithOtherEntity(e)) moveAwayFromOtherEntity(e);
        }
        
    }

    // Right now, simple logic that scans if the distance between the player and the entity is <= scanRadius.
    // Pursues if yes. Does not yet consider obstacles.
    private Player scanForPlayer(GameStateManager gsm){
        final int scanRadius = 96;
        Player closestPlayer = null;
        double minDistance = 10000; // Random large number

        for (Entity e : gsm.getEntities()) {
            if (e instanceof Player player) {
                // Get the center distance between the player and the entity
                double distance = 
                Math.sqrt(
                    (Math.pow(centerX - e.getCenterX(), 2) + 
                    Math.pow(centerY - e.getCenterY(), 2))
                );
                
                if ( (distance <= scanRadius) && (distance < minDistance)) {
                    closestPlayer = player;
                    minDistance = distance;
                }
            }
        }
        return closestPlayer;
    }

    private void pursuePlayer(Player player) {
        if (player.getCenterX() > centerX) worldX += speed;
        else if (player.getCenterX() < centerX) worldX -= speed;

        if (player.getCenterY() > centerY) worldY += speed;
        else if (player.getCenterY() < centerY) worldY -= speed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
