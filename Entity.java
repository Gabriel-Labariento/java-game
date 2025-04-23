import java.awt.Graphics2D;

public abstract class Entity extends GameObject {
    protected char identifier;
    protected int speed;
    protected double maxHealth;
    protected double health;
    protected double damage;
    protected int clientId;
    int[][] hitBoxBounds;
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
        return (health <= 0);
    }

    // FOR TRANSFER
    // public byte[] getByteData(){
    //     ByteBuffer buffer = ByteBuffer.allocate(40);
    //     buffer.putInt(identifier);
    //     buffer.putInt(worldX);
    //     buffer.putInt(worldY);
    //     buffer.putInt(width);
    //     buffer.putInt(height);
    //     buffer.putDouble(health);
    //     buffer.putDouble(attack);
    //     return buffer.array();
    // }

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

}
    
    
   
