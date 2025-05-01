public abstract class Enemy extends Entity {
    

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

    /**
     * Moves the enemy depending on where the pursued player is
     * @param player the player to be pursued
     */
    public void pursuePlayer(Player player) {
        if (player.getCenterX() > getCenterX()) worldX += speed;
        else if (player.getCenterX() < getCenterX()) worldX -= speed;

        if (player.getCenterY() > getCenterY()) worldY += speed;
        else if (player.getCenterY() < getCenterY()) worldY -= speed;
    }


    
}
