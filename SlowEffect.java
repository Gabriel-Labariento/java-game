public class SlowEffect extends StatusEffect{

    int initialPlayerSpeed;

    public SlowEffect(){
        duration = 3000;
        expireTime = System.currentTimeMillis() + duration;
    }

    @Override
    public void applyStatusEffect(Player player) {
        initialPlayerSpeed = player.getSpeed();
        player.setSpeed(initialPlayerSpeed - 2);
    }

    @Override
    public void tick(Player player){
        if (isExpired()) {
            removeStatusEffect(player);
        }
    }

    @Override
    public void removeStatusEffect(Player player) {
        player.setSpeed(initialPlayerSpeed);
    }

    @Override
    public StatusEffect copy(){
        return new SlowEffect();
    }
}