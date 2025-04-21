import java.util.*;
import java.util.concurrent.*;

public class ServerMaster {
    private CopyOnWriteArrayList<Entity> entities;
    private int userPlayerIndex;
    private Room currentRoom;
    private ScheduledExecutorService framesCounterScheduler;

    public ServerMaster(){
        entities = new CopyOnWriteArrayList<>();
        userPlayerIndex = -1;
        currentRoom = null;
        framesCounterScheduler = Executors.newSingleThreadScheduledExecutor();

    }

    public void update(){
        //Do not update the game at start of the gameserver (no entities yet)
        if(entities.isEmpty()) return;
        
        checkCollisions();

        //Remove entities that are either depleted of health or isExpired
        entities.removeIf(entity -> (entity instanceof Attack attack) && (attack.getIsExpired()));
        // entities.removeIf(entity -> entity.getIsDead());

        
    }

    public void checkCollisions(){
        //SORT, SWEEP, AND, PRUNE DETECTION

        //Make a new arraylist containing all of the elements of entities
        ArrayList<Entity> sortedEntities = new ArrayList<>(entities);
        //Sort entities from the universal arraylist by their worldx values (left bounds)
        Collections.sort(sortedEntities, Comparator.comparingInt(e -> e.getHitBoxBounds()[2]));

        int size = sortedEntities.size();
        for(int i=0; i < size; i++){
            Entity entity1 = sortedEntities.get(i);
            int[] b1 = entity1.getHitBoxBounds();
            
            for(int j = (i+1); j < size; j++){
                Entity entity2 = sortedEntities.get(j);
                int[] b2 = entity2.getHitBoxBounds();
                
                //Skip detection if the second entity starts after the first ends on the x-axis
                if(b2[2]>b1[3]) break;

                //Check for collisions in the top, bottom, left, right of entity1 against entity2
                if (b1[0] < b2[1] && b1[1] > b2[0] && b1[2] < b2[3] && b1[3] > b2[2]){
                    resolveCollision(entity1, entity2, b1, b2);
                }
            }
        }   
    }

    public void resolveCollision(Entity e1, Entity e2, int[] b1, int[] b2){

        //ATTACK-PLAYER/ENEMY COLLISION HANDLING
        //If entity is an attack and is not friendly and if the second entity is a player, then the player takes damage.
        //If entity is an attack and is friendly and if the second entity is an enemy, then the enemy takes damage.
        if (e1 instanceof Attack attack && ((!attack.getIsFriendly() && e2 instanceof Player) 
            || (attack.getIsFriendly() && e2 instanceof Enemy)))
            e2.changeHealth(-e1.getDamage());

        else if (e2 instanceof Attack attack && ((!attack.getIsFriendly() && e1 instanceof Player) 
            || (attack.getIsFriendly() && e1 instanceof Enemy)))
            e1.changeHealth(-e2.getDamage());

        //PLAYER-PLAYER/ENEMY COLLISION HANDLING
        //If player touches enemy, take damage and prevent overlap
        else if (e1 instanceof Player && e2 instanceof Enemy){
            preventOverlap(e1, e2, b1, b2);
            e1.changeHealth(-e2.getDamage());
        }

        else if (e2 instanceof Player && e1 instanceof Enemy)
        {
            preventOverlap(e2, e1, b1, b2);
            e2.changeHealth(-e1.getDamage());
        }

        else if (e1 instanceof Player && e2 instanceof Player){
            preventOverlap(e1, e2, b1, b2);
        }

    }

    private void preventOverlap(Entity e1, Entity e2, int[] b1, int[] b2){
        //TODO: DOESNT WORK AS INTENDED
        //Directions are relative to e1's bounds
        int topOverlap = b2[1] - b1[0];
        int bottomOverlap = b1[1] - b2[0]; 
        int leftOverlap = b2[3] - b1[2];
        int rightOverlap = b1[3] - b2[2];

        //Find the minimum overlap to determine which direction you can prevent the overlap with the least movement
        int minOverlap = Math.min(Math.min(topOverlap, bottomOverlap), Math.min(leftOverlap, rightOverlap));

        if (minOverlap == topOverlap)
            e1.setWorldY(e1.getWorldY() + minOverlap);
        else if (minOverlap == bottomOverlap)
            e1.setWorldY(e1.getWorldY() - minOverlap);
        else if (minOverlap == leftOverlap)
            e1.setWorldX(e1.getWorldX() + minOverlap);
        else if (minOverlap == rightOverlap)
            e1.setWorldX(e1.getWorldY() - minOverlap);

        e1.matchHitBoxBounds();
    }

    public void playerClick(int clickX, int clickY, Player originPlayer, Attack playerAttack, int cid){
        
        //Temporary debouncing check
        if (playerAttack != null) return;

        double attackDamage = originPlayer.getDamage();
        int attackSpeed = originPlayer.getSpeed();
        int frameWidth = 720;
        int frameHeight = 540;
        int centerX = frameWidth/2;
        int centerY = frameHeight/2;

        //Get a point a set distance away from the center of the screen in the direction of the click
        int vectorX = clickX - centerX;
        int vectorY = clickY - centerY;  
        int distance = 50;
        double normalizedVector = Math.sqrt((vectorX*vectorX)+(vectorY*vectorY));
        if (normalizedVector == 0) normalizedVector = 1; 
        double normalizedX = vectorX/normalizedVector;
        double normalizedY = vectorY/normalizedVector;
        int attackScreenX = (int) (centerX + distance*normalizedX);
        int attackScreenY = (int) (centerY + distance*normalizedY);

        int playerScreenX = frameWidth/2 - originPlayer.getWidth()/2;
        int playerScreenY = frameHeight/2 - originPlayer.getHeight()/2;

        int worldX = (originPlayer.getWorldX() - playerScreenX) + attackScreenX;
        int worldY = (originPlayer.getWorldY() - playerScreenY) + attackScreenY;

        // int worldX = (int) Math.round(spawnX) + originPlayer.getWorldX() - screenX - 6;
        // int worldY = (int) Math.round(spawnY) + originPlayer.getWorldY() - screenY - 30;

        //TODO: If originPlayer is of different type, instantiate another type of attack
        int attackWidth;
        int attackHeight;
        if(true){
            attackWidth = 80;
            attackHeight = 80;
            playerAttack = new PlayerSlash(cid, worldX-attackWidth/2, worldY - attackHeight/2, 
            attackWidth, attackHeight, attackDamage, true, attackSpeed);
        }
        else{

        }
         
        entities.add(playerAttack);
    }

    public String getAssetsData (int cid){
        String parseableStr = "" + cid;

            for(Entity entity : entities){
                parseableStr += entity.getAssetData();
                // If the entity is the user player
                if (entity.getIdentifier() == 'A' && entity.getClientId() == cid)
                    parseableStr += '$'; // Indicates that the userPlayerIndex comes next
            }

            currentRoom = new Room('A');
            parseableStr += currentRoom.getRoomId() + "0,0%";

        return parseableStr;
    }

    public void addEntity(Entity e){
        entities.add(e);
    }

    public void removeEntity(Entity e){
        entities.remove(e);
    }

    public CopyOnWriteArrayList<Entity> getEntities(){
        return entities;
    }


    public Entity getPlayerFromClientId(int cid){
        for (Entity entity : entities) {
            if (entity.getIdentifier() == 'A' && entity.getClientId() == cid) return entity;
        }
        return null;
    }

    public Entity getAttackFromClientId(int cid){
        for (Entity entity : entities) {
            if (entity instanceof Attack && entity.getClientId() == cid) return entity;
        }
        return null;
    }
}
