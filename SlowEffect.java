public class SlowEffect extends StatusEffect{

    public SlowEffect(int gameTicksDuration){
        super(gameTicksDuration);
    }

    @Override
    public void applyStatusEffect(Player player) {
        // player.setHitPoints(damagePerTick);
        player.setSpeed(player.getBaseSpeed() - 2);
        System.out.println("Applied poision. Player speed: " + player.getSpeed());  
    }

    @Override
    public void tick(Player player){
        applyStatusEffect(player);
        gameTicksDuration--;
    }

    @Override
    public void removeStatusEffect(Player player) {
        player.setSpeed(player.baseSpeed);
        System.out.println("Removed poison effect. Player speed: " + player.getSpeed());
    }

    
}
