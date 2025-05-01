public abstract class Attack extends Entity{
    public int duration;
    private int id;
    public long expirationTime;
    public boolean isExpired;
    public boolean isFriendly;
    public Entity owner;

    public void setExpirationTime(int duration){
        expirationTime = System.currentTimeMillis() + duration;
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

    public Entity getOwner() {
        return owner;
    }
}