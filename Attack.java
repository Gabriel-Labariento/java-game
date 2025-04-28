public abstract class Attack extends Entity{
    public int duration;
    public long expirationTime;
    public boolean isExpired;
    public boolean isFriendly;
    public Entity owner;

    public void setExpirationTime(int duration){
        expirationTime = System.currentTimeMillis() + duration;
    }

    public void setOwner(Entity entity, int offsetX, int offsetY){
        
    }

    public boolean getIsExpired(){
        return System.currentTimeMillis() >= expirationTime;
    }

    public boolean getIsFriendly(){
        return isFriendly;
    }
}