import java.awt.Graphics2D;

public abstract class Entity extends GameObject {
    protected static final int INVINCIBILITY_DURATION = 800;
    protected long invincibilityEnd;

    protected char identifier;
    protected int prevWorldX;
    protected int prevWorldY;
    protected int speed;
    protected double maxHealth;
    protected double hitPoints;
    protected double damage;
    protected int clientId;
    Room currentRoom; 

    public void draw(Graphics2D g2d, int xOffset, int yOffset){}

    public abstract void updateEntity(ServerMaster gsm);
    
    public int getClientId(){
        return clientId;
    }

    public char getIdentifier(){
        return identifier;
    }

    public abstract String getAssetData(boolean isUserPlayer);

    /**
     * Checks if the move an entity will make will keep them inside the room they are currently in
     * @param dx the change in x.
     * @param dy the change in y
     * @return a boolean that is true when the move is inbound, false when not.
     */
    public boolean isMoveInbound(int dx, int dy) {
        return !((worldX + dx < currentRoom.getWorldX()) ||
                ( (worldX + width) + dx > currentRoom.getWorldX() + currentRoom.getWidth()) ||
                (worldY + dy < currentRoom.getWorldY()) ||
                ((worldY + height) + dy > currentRoom.getWorldY() + currentRoom.getHeight())
             );
    }

    public void setPosition(int x, int y) {
        int[] roomBounds = currentRoom.getHitBoxBounds();

        if (y < roomBounds[0]) y = roomBounds[0];           // Top Boundary
        if (y + height > roomBounds[1]) y = roomBounds[1] - height;  // Bottom Boundary
        if (x < roomBounds[2]) x = roomBounds[2];           // Left Boundary
        if (x + width > roomBounds[3]) x = roomBounds[3] - width;   // Right Boundary
    
        worldX = x;
        worldY = y;

        matchHitBoxBounds();
    }

    /**
     * Checks whether an entity is dead or alive based on its health.
     * @return true if the entity is dead, false otherwise.
     */
    public boolean isDead(){
        return (hitPoints <= 0);
    }

    public void takeDamageFromEntity(Entity attacker){
        if (getIsInvincible()) return;
        hitPoints -= attacker.getDamage();
        System.out.println(getClass() + " takes damage from " + attacker.getClass() + " hp now " + (hitPoints) );
    }
    
    /**
     * 
     */
    public void triggerInvincibility(){
        invincibilityEnd = System.currentTimeMillis() + Entity.INVINCIBILITY_DURATION;
    }

    public boolean getIsInvincible(){
        return System.currentTimeMillis() < invincibilityEnd;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void setWorldX(int worldX) {
        this.worldX = worldX;
    }

    public void setWorldY(int worldY) {
        this.worldY = worldY;
    }

    public int getPrevWorldX() {
        return prevWorldX;
    }

    public int getPrevWorldY() {
        return prevWorldY;
    }

    public int getSpeed() {
        return speed;
    }

    public double getHitPoints() {
        return hitPoints;
    }

    public double getDamage() {
        return damage;
    }

    public void setHitPoints(double hP) {
        hitPoints = hP;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

}
    
    
   
