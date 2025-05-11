public abstract class StatusEffect {
    protected int gameTicksDuration;

    public StatusEffect(int gameTicksDuration){
        this.gameTicksDuration = gameTicksDuration;
    }

    public abstract void applyStatusEffect(Player player);

    public abstract void removeStatusEffect(Player player);

    public boolean isExpired(){
        return gameTicksDuration <= 0;
    }

    public abstract void tick(Player player);
}
