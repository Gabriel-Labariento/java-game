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
        return !((worldX + dx < currentRoom.getX()) ||
                ( (worldX + width) + dx > currentRoom.getX() + currentRoom.getWidth()) ||
                (worldY + dy < currentRoom.getY()) ||
                ((worldY + height) + dy > currentRoom.getY() + currentRoom.getHeight())
             );
    }

    /**
     * Checks for collision with other entity
     * @param other entity colliding/not colliding with
     * @return true if colliding with other, false otherwise.
     */
    public boolean isCollidingWithOtherEntity(Entity other){
        return !((worldX < other.getWorldX()) ||
                ( (worldX + width) > other.getWorldX() + other.getWidth()) ||
                (worldY < other.getWorldY()) ||
                ((worldY + height) > other.getWorldY() + other.getHeight())
        );
    }

    // TODO: JAVADOC
    private double[] calculateRepulsionForce(Entity other) {
        final int repulsionFactor = 32;

        int dx = getCenterX() - other.getCenterX();
        int dy = getCenterY() - other.getCenterY();
        int distanceSquared = dx * dx + dy * dy;
        double distance = Math.sqrt(distanceSquared);


        // Avoid division by zero and extremely strong forces at small distances
        if (distance < 1e-5) {
            return new double[] {0,0};
        } 

        int forceMagnitude = repulsionFactor / distanceSquared;

        double x = (dx / distance) * forceMagnitude;
        double y = (dy / distance) * forceMagnitude;

        double[] repulsionForce = {x,y};
        return repulsionForce; 
    }

    // TODO: FIGURE OUT HOW TO MAKE ENEMIES NOT COLLIDE WITH EACH OTHER
    public void moveAwayFromOtherEntity(Entity other) {
        double[] repulsionForce = calculateRepulsionForce(other);
        worldX += repulsionForce[0];
        worldY += repulsionForce[1];
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
    
    
   
