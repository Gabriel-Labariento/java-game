public class BurnEffect extends StatusEffect {
    
    public BurnEffect(int gameTicksDuration){
        super(gameTicksDuration);
    }

    @Override
    public void applyStatusEffect(Player player) {
        player.setHitPoints(player.getHitPoints() - 1);
        System.out.println("Applied poision. Player speed: " + player.getSpeed());
       
    }

    @Override
    public void removeStatusEffect(Player player) {
        player.setSpeed(player.baseSpeed);
        System.out.println("Removed poison effect. Player speed: " + player.getSpeed());
    }

    @Override
    public void tick(Player player) {
        if (gameTicksDuration % 60 == 0) applyStatusEffect(player); // Every second
        gameTicksDuration--;
    }
}
