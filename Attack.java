public abstract class Attack extends Entity{
    public static int attackNum;
    public int duration;
    public int id;
    public int xOffset;
    public int yOffset;
    public long expirationTime;
    public boolean isExpired;
    public boolean isFriendly;
    public boolean isOffsetInitialized;
    public Entity owner;

    public void setExpirationTime(int duration){
        expirationTime = System.currentTimeMillis() + duration;
    }

    public void attachToOwner(){    
        if (owner != null){
            int ownerX = owner.getWorldX();
            int ownerY = owner.getWorldY();
            int prevOwnerX = owner.getPrevWorldX();
            int prevOwnerY = owner.getPrevWorldY();

            // if(owner.getHasMoved()){
            //     worldX += ownerX - prevOwnerX;
            //     worldY += ownerY - prevOwnerY;
            // }

            // Initialize attack-owner offset
            if (!isOffsetInitialized){
                isOffsetInitialized = true;
                xOffset = worldX - ownerX;
                yOffset = worldY - ownerY;
            }

            // //If owner has moved, only then should you move attack
            if (prevOwnerX != ownerX || prevOwnerY != ownerY){
                worldX = ownerX + xOffset;
                worldY = ownerY + yOffset;
            }

            matchHitBoxBounds();
        }
    }

    public boolean getIsExpired(){
        return System.currentTimeMillis() >= expirationTime;
    }

    public boolean getIsFriendly(){
        return isFriendly;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}