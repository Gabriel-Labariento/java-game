
import java.util.ArrayList;

public abstract class Enemy extends Entity {
    public ArrayList<Integer> attacksTakenById;
    public static int enemyCount = Integer.MIN_VALUE;
    public int rewardXP;
    public long lastAttackTime;


    public Enemy(){
        attacksTakenById = new ArrayList<>();
    }

    public int getRewardXP(){
        return rewardXP;
    }

    public void setRewardXP(int i){
        rewardXP = i;
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

    private double[] calculateRepulsionForce(Entity other) {
        final int repulsionFactor = 32;

        int dx = getCenterX() - other.getCenterX();
        int dy = getCenterY() - other.getCenterY();
        int distanceSquared = dx * dx + dy * dy;
        double distance = Math.sqrt(distanceSquared);


        // Avoid division by zero and extremely strong forces at small distances
        if (distance < 1e-5) {
            return new double[] {0,0};
        } 

        int forceMagnitude = repulsionFactor / distanceSquared;

        double x = (dx / distance) * forceMagnitude;
        double y = (dy / distance) * forceMagnitude;

        double[] repulsionForce = {x,y};
        return repulsionForce; 
    }

    // TODO: FIGURE OUT HOW TO MAKE ENEMIES NOT COLLIDE WITH EACH OTHER
    public void moveAwayFromOtherEntity(Entity other) {
        double[] repulsionForce = calculateRepulsionForce(other);
        worldX += repulsionForce[0];
        worldY += repulsionForce[1];
    }

    // Right now, simple logic that scans if the distance between the player and the entity is <= scanRadius.
    // Pursues if yes. Does not yet consider obstacles.
    public Player scanForPlayer(ServerMaster gsm){
        final int scanRadius = GameCanvas.TILESIZE * 12;
        Player closestPlayer = null;
        double minDistance = 10000; // Random large number

        for (Entity e : gsm.getEntities()) {
            if (e instanceof Player player) {
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

    public void runFromPlayer(Player player){
        if (player.getCenterX() > getCenterX() && isMoveInbound(-speed, 0)) worldX -= speed;
        else if (player.getCenterX() < getCenterX() && isMoveInbound(speed, 0)) worldX += speed;

        if (player.getCenterY() > getCenterY() && isMoveInbound(0, -speed)) worldY -= speed;
        else if (player.getCenterY() < getCenterY() && isMoveInbound(0, speed)) worldY += speed;
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
}
