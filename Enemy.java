import java.util.ArrayList;

public abstract class Enemy extends Entity {
    protected static final int ATTACK_COOLDOWN = 800;
    protected long lastAttackTime = 0;
    protected ArrayList<Integer> attacksTakenById;
    protected enum State { IDLE, PURSUE, ATTACK, CIRCLE, DODGE } ; 
    protected State currentState = State.IDLE;
    protected int ATTACK_RANGE = GameCanvas.TILESIZE * 3;

    public Enemy(){
        attacksTakenById = new ArrayList<>();
    }
    
    public void loadAttack(int id){
        attacksTakenById.add(id);
    }

    public boolean validateAttack(int id){
        
        return !attacksTakenById.contains(id);
    }

    // Right now, simple logic that scans if the distance between the player and the entity is <= scanRadius.
    // Pursues if yes. Does not yet consider obstacles.
    public Player scanForPlayer(ServerMaster gsm){
        final int scanRadius = GameCanvas.TILESIZE * 5; // 5 tile scan radius
        Player closestPlayer = null;
        double minDistance = Double.MAX_VALUE;

        for (Entity e : gsm.getEntities()) {
            if (e instanceof Player player) {
                if (player.getCurrentRoom() != getCurrentRoom()) continue;
                // Get the center distance between the player and the entity
                double distance = getSquaredDistanceBetween(e, player);
                
                if ( (distance <= scanRadius) && (distance < minDistance)) {
                    closestPlayer = player;
                    minDistance = distance;
                }
            }
        }
        return closestPlayer;
    }

    /**
     * Moves the enemy depending on where the pursued player is
     * @param player the player to be pursued
     */
    public void pursuePlayer(Player player) {
        if (player.getCurrentRoom() != currentRoom) return;
        
        if (player.getCenterX() > getCenterX()) worldX += speed;
        else if (player.getCenterX() < getCenterX()) worldX -= speed;

        if (player.getCenterY() > getCenterY()) worldY += speed;
        else if (player.getCenterY() < getCenterY()) worldY -= speed;
    }

    public void circlePlayer(Player player, boolean clockwise) {
        int dx = player.getCenterX() - getCenterX();
        int dy = player.getCenterY() - getCenterY();

        double distance = getDistanceBetween(this, player);

        if (distance != 0) {
            double unitX = dx / distance;
            double unitY = dy / distance;

            double perpendicularX = clockwise ? -1 * unitY : unitY;
            double perpendicularY = clockwise ? unitX : -1 * unitX;

            int newX = (int) (worldX + perpendicularX * speed);
            int newY = (int) (worldY + perpendicularY * speed);

            setPosition(newX, newY);
            matchHitBoxBounds();
        }
    }

    public void attackPlayer(Player player, double distance) { // Pass the distance to not compute again
        int dx = player.getCenterX() - getCenterX();
        int dy = player.getCenterY() - getCenterY();

        if (distance != 0) {
            double unitX = dx / distance;
            double unitY = dy / distance;

            int newX = (int) (worldX + unitX * speed * ATTACK_RANGE);
            int newY = (int) (worldY + unitY * speed * ATTACK_RANGE);
            
            setPosition(newX, newY);
        }
    }


    
}
