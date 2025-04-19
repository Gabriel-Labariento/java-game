import java.awt.Graphics2D;

public abstract class Entity {
    public char identifier;
    public int speed;
    public int worldX;
    public int worldY;
    public int width;
    public int height;
    public int maxHealth;
    public int health;
    public int attack;
    public int clientId;
    public Room currentRoom; 

    public void draw(Graphics2D g2d, int xOffset, int yOffset){}

    public abstract void updateEntity();

    public int getWorldX(){
        return worldX;
    }

    public int getWorldY(){
        return worldY;
    }

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
        return !((worldX + dx < currentRoom.getX()) ||
                ( (worldX + width) + dx > currentRoom.getX() + currentRoom.getWidth()) ||
                (worldY + dy < currentRoom.getY()) ||
                ((worldY + height) + dy > currentRoom.getY() + currentRoom.getHeight())
             );
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
    
