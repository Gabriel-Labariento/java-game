
import java.util.ArrayList;

public abstract class Enemy extends Entity {
    public ArrayList<Integer> attacksTakenById;
    public int rewardXP;

    public Enemy(){
        attacksTakenById = new ArrayList<>();
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
        final int scanRadius = 96;
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

    
}
