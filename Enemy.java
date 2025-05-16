
import java.util.ArrayList;

public abstract class Enemy extends Entity {
    protected static int enemyCount = Integer.MIN_VALUE;
    public ArrayList<Integer> attacksTakenById;
    public int rewardXP;
    public boolean isBoss;
    protected boolean isBuffed;

    public Enemy(){
        attacksTakenById = new ArrayList<>();
        isBoss = false;
        isBuffed = false;
    }

    public int getRewardXP(){
        return rewardXP;
    }
    
    public void loadAttack(int id){
        attacksTakenById.add(id);
    }

    public boolean validateAttack(int id){
        return !attacksTakenById.contains(id);
    }

    public int getLastAttackID(){
        return attacksTakenById.get(attacksTakenById.size()-1);
    }


    /**
     * Scans for and returns the closest player to the enemy.
     * @param gsm servermaster instance
     * @return the player closest to the enemy
     */ 
    public Player scanForPlayer(ServerMaster gsm){
        final int scanRadius = GameCanvas.TILESIZE * 6;
        Player closestPlayer = null;
        double minDistance = Integer.MAX_VALUE;

        for (Entity e : gsm.getEntities()) {
            if (e instanceof Player player) {
                if (this.getCurrentRoom() != player.getCurrentRoom()) continue; 
                // Get the center distance between the player and the entity
                double distanceSquared = 
                    (Math.pow(getCenterX() - e.getCenterX(), 2) + Math.pow(getCenterY() - e.getCenterY(), 2));
                
                if ( (distanceSquared <= scanRadius * scanRadius) && (distanceSquared < minDistance)) {
                    closestPlayer = player;
                    minDistance = distanceSquared;
                }
            }
        }
        return closestPlayer;
    }

    public void pursuePlayer(Player player) {

        int newX = worldX;
        int newY = worldY;

        if (player.getCenterX() > getCenterX()) newX += speed;
        else if (player.getCenterX() < getCenterX()) newX -= speed;

        if (player.getCenterY() > getCenterY()) newY += speed;
        else if (player.getCenterY() < getCenterY()) newY -= speed;

        setPosition(newX, newY);
    }

    public void moveAwayFromPlayer(Player player) {       
        int newX = worldX;
        int newY = worldY;
        
        if (player.getCenterX() > getCenterX()) newX -= speed;
        else if (player.getCenterX() < getCenterX()) newX += speed;

        if (player.getCenterY() > getCenterY()) newY -= speed;
        else if (player.getCenterY() < getCenterY()) newY += speed;

        setPosition(newX, newY);
    }

    public void createBiteAttack(ServerMaster gsm, Player target, StatusEffect effect){
        int vectorX = target.getCenterX() - getCenterX();
        int vectorY = target.getCenterY() - getCenterY(); 
        double normalizedVector = Math.sqrt((vectorX*vectorX)+(vectorY*vectorY));

        //Avoids 0/0 division edge case
        if (normalizedVector == 0) normalizedVector = 1; 
        double normalizedX = vectorX / normalizedVector;
        double normalizedY = vectorY / normalizedVector;

        int biteDistance = GameCanvas.TILESIZE;
        int biteX = (int) (this.getCenterX() + normalizedX * biteDistance);
        int biteY = (int) (this.getCenterY() + normalizedY * biteDistance);
        biteX -= EnemyBite.WIDTH / 2;
        biteY -= EnemyBite.HEIGHT / 2;

        EnemyBite eb = new EnemyBite(this, biteX, biteY);
        if (effect != null) eb.addAttackEffect(effect);
        gsm.addEntity(eb);
    }
    
    public void createBarkAttack(ServerMaster gsm, Player target, StatusEffect effect){
        int vectorX = target.getCenterX() - getCenterX();
        int vectorY = target.getCenterY() - getCenterY(); 
        double normalizedVector = Math.sqrt((vectorX*vectorX)+(vectorY*vectorY));

        //Avoids 0/0 division edge case
        if (normalizedVector == 0) normalizedVector = 1; 
        double normalizedX = vectorX / normalizedVector;
        double normalizedY = vectorY / normalizedVector;

        double barkDistance = GameCanvas.TILESIZE * 2.5;
        int barkX = (int) (this.getCenterX() + normalizedX * barkDistance);
        int barkY = (int) (this.getCenterY() + normalizedY * barkDistance);

        barkX -= EnemyBark.WIDTH / 2;
        barkY -= EnemyBark.HEIGHT / 2;

        EnemyBark eb = new EnemyBark(this, barkX, barkY);
        if (effect != null) eb.addAttackEffect(effect);
        gsm.addEntity(eb);
        System.out.println("Created bark attack");
    }

    public void performSmashAttack(ServerMaster gsm){
        int smashX = worldX - EnemySmash.SMASH_RADIUS;
        int smashY = worldY - EnemySmash.SMASH_RADIUS;

        EnemySmash fs = new EnemySmash(this, smashX, smashY);
        fs.addAttackEffect(new SlowEffect());
        gsm.addEntity(fs);
    }

    public void initiateJump(Player target){
        int vectorX = target.getCenterX() - getCenterX();
        int vectorY = target.getCenterY() - getCenterY(); 
        double normalizedVector = Math.sqrt((vectorX*vectorX)+(vectorY*vectorY));

        //Avoids 0/0 division edge case
        if (normalizedVector == 0) normalizedVector = 1; 
        double normalizedX = vectorX / normalizedVector;
        double normalizedY = vectorY / normalizedVector;

        int jumpDistance = GameCanvas.TILESIZE * 3;

        int newX = (int) (worldX + normalizedX * jumpDistance);
        int newY = (int) (worldY + normalizedY * jumpDistance);

        setPosition(newX, newY);
    }

    public void spawnMinions(){
        if (currentRoom != null && currentRoom.getMobSpawner() != null) {
        Enemy newSpawn = currentRoom.getMobSpawner().createNormalEnemy(currentRoom.getGameLevel());
        currentRoom.getMobSpawner().spawnEnemy(newSpawn);
        }
    }

    public void applyBuff(){
        if (!isBuffed){
            speed += 1;
            damage += 1;
        }
        isBuffed = true;
    }
}
