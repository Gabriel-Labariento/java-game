public abstract class Attack extends Entity{
    public int duration;
    public long expirationTime;
    public boolean isExpired;
    public boolean isFriendly;

    public void setExpirationTime(int duration){
        expirationTime = System.currentTimeMillis() + duration;
    }

    public boolean getIsExpired(){
        return System.currentTimeMillis() >= expirationTime;
    }

    public boolean getIsFriendly(){
        return isFriendly;
    }
}