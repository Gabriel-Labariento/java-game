public abstract class Item extends Entity{
    public long despawnTime;
    public int isDespawned;
    public static final int dropDuration = 60000;
    public Player owner;
    public boolean isConsumable;

    public Item(){
        despawnTime = System.currentTimeMillis() + dropDuration;
        width = 16;
        height = 16;
    }

    public void setOwner(Player player){
        owner = player;
    }

    public abstract void applyEffects();

    // public abstract void removeEffects();

    public boolean getIsConsumable(){
        return isConsumable;
    }

    public boolean getIsDespawned(){
        return System.currentTimeMillis() >= despawnTime;
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
    public void matchHitBoxBounds() {
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY;
        hitBoxBounds[1] = worldY + height;
        hitBoxBounds[2]= worldX;
        hitBoxBounds[3] = worldX + width;
    }

    @Override
    public void updateEntity(ServerMaster gsm){
    }


}