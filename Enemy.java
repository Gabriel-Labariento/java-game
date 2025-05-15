
import java.util.ArrayList;

public abstract class Enemy extends Entity {
    protected static int enemyCount = Integer.MIN_VALUE;
    public ArrayList<Integer> attacksTakenById;
    public int rewardXP;
    public boolean isBoss;

    public Enemy(){
        attacksTakenById = new ArrayList<>();
        isBoss = false;
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


      // Right now, simple logic that scans if the distance between the player and the entity is <= scanRadius.
    // Pursues if yes. Does not yet consider obstacles.
    public Player scanForPlayer(ServerMaster gsm){
        final int scanRadius = GameCanvas.TILESIZE * 6;
        Player closestPlayer = null;
        double minDistance = 10000; // Random large number

        for (Entity e : gsm.getEntities()) {
            if (e instanceof Player player) {
                if (this.getCurrentRoom() != player.getCurrentRoom()) continue; 
                // Get the center distance between the player and the entity
                double distance = 
                Math.sqrt(
                    (Math.pow(getCenterX() - e.getCenterX(), 2) + 
                    Math.pow(getCenterY() - e.getCenterY(), 2))
                );
                
                if ( (distance <= scanRadius) && (distance < minDistance)) {
                    closestPlayer = player;
                    minDistance = distance;
                }
            }
        }
        return closestPlayer;
    }

    public void pursuePlayer(Player player) {
        if (player.getCenterX() > getCenterX()) worldX += speed;
        else if (player.getCenterX() < getCenterX()) worldX -= speed;

        if (player.getCenterY() > getCenterY()) worldY += speed;
        else if (player.getCenterY() < getCenterY()) worldY -= speed;
    }

    public void moveAwayFromPlayer(Player player) {
        if (player.getCenterX() > getCenterX()) worldX -= speed;
        else if (player.getCenterX() < getCenterX()) worldX += speed;

        if (player.getCenterY() > getCenterY()) worldY -= speed;
        else if (player.getCenterY() < getCenterY()) worldY += speed;
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
        eb.addAttackEffect(effect);
        gsm.addEntity(eb);
    }
    
    public void createBarkAttack(ServerMaster gsm, Player target){
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
        gsm.addEntity(eb);
    }
}
