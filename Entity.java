import java.awt.Graphics2D;

public abstract class Entity extends GameObject {
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

    /**
     * Checks whether an entity is dead or alive based on its health.
     * @return true if the entity is dead, false otherwise.
     */
    public boolean isDead(){
        return (hitPoints <= 0);
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

}
    
    
   
