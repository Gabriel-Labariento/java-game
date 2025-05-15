public abstract class Item extends Entity{
    public long despawnTime;
    public long pickUpCDTime;
    public static final int DROPDURATION = 60000;
    public static final int PICKUPCDDURATION = 5000;
    public Player owner;
    public boolean isConsumable;
    public boolean isHeld;
    public int initialCDDuration;
    public int initialDamage;
    public int initialMaxHealth;
    public int initialHitPoints;
    public int initialSpeed;
    public int initialDefense;

    public Item(){
        triggerDespawnTimer();
        width = 16;
        height = 16;
    }

    public void setOwner(Player player){
        owner = player;
    }

    public void triggerDespawnTimer(){
        despawnTime = System.currentTimeMillis() + DROPDURATION;
    }

    public void setIsHeld(boolean b){
        isHeld = b;
    }

    public abstract void applyEffects();

    public abstract void removeEffects();

    public void triggerPickUpCD(){
        pickUpCDTime = System.currentTimeMillis() + PICKUPCDDURATION;
    }

    public boolean getIsOnPickUpCD(){
        return System.currentTimeMillis() < pickUpCDTime;
    }

    public boolean getIsConsumable(){
        return isConsumable;
    }

    public boolean getIsDespawned(){
        if (isHeld) return false;
        return System.currentTimeMillis() >= despawnTime;
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