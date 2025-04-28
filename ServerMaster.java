import java.util.*;
import java.util.concurrent.*;

public class ServerMaster {
    private static final int TICKSPERSECOND = 60;
    private CopyOnWriteArrayList<Entity> entities;
    private int userPlayerIndex;
    private Room currentRoom;
    private HashMap<Character, Integer> keyInputQueue;
    private ArrayList<ClickInput> clickInputQueue;

    public ServerMaster(){
        entities = new CopyOnWriteArrayList<>();
        userPlayerIndex = -1;
        currentRoom = null;
        keyInputQueue = new HashMap<>();
        clickInputQueue = new ArrayList<>();
    }

    //For storing three values necessary for processing click inputs
    private static class ClickInput{
        private int x, y, cid;

        public ClickInput(int x, int y, int cid){
            this.x = x;
            this.y = y;
            this.cid = cid;
        }
    } 

    public void update(){
        //Do not update the game at start of the gameserver (no entities yet)
        if(entities.isEmpty()) return;
        
        //Update objects accordingly to the inputs
        processInputs();

        //Collision checking
        checkCollisions();

        //Remove entities that are either depleted of HitPoints or isExpired
        entities.removeIf(entity -> (entity instanceof Attack attack) && (attack.getIsExpired()));
        entities.removeIf(entity -> (entity.getHitPoints() <= 0));
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
            e2.setHitPoints(e2.getHitPoints()-e1.getDamage());

        else if (e2 instanceof Attack attack && ((!attack.getIsFriendly() && e1 instanceof Player) 
            || (attack.getIsFriendly() && e1 instanceof Enemy)))
            e1.setHitPoints(e1.getHitPoints()-e2.getDamage());

        //PLAYER/ENEMY COLLISION HANDLING
        //If player touches enemy, take damage and prevent overlap
        else if (e1 instanceof Player && e2 instanceof Enemy){
            preventOverlap(e1, e2, b1, b2);
            e1.setHitPoints(e1.getHitPoints()-e2.getDamage());
        }

        else if (e2 instanceof Player && e1 instanceof Enemy)
        {
            preventOverlap(e1, e2, b1, b2);
            e2.setHitPoints(e2.getHitPoints()-e1.getDamage());
        }

        else if (e1 instanceof Player && e2 instanceof Player)
            preventOverlap(e1, e2, b1, b2);
        else if (e1 instanceof Enemy && e2 instanceof Enemy)
            preventOverlap(e1, e2, b1, b2);
        

    }

    private void preventOverlap(Entity e1, Entity e2, int[] b1, int[] b2){

        //Get the position vectors of both entities
        int[] positionVector1 = new int[2];
        positionVector1[0] = e1.getWorldX() + e1.getWidth()/2;
        positionVector1[1] = e1.getWorldY() + e1.getHeight()/2;

        int[] positionVector2 = new int[2];
        positionVector2[0] = e2.getWorldX() + e2.getWidth()/2;
        positionVector2[1] = e2.getWorldY() + e2.getHeight()/2;

        //Find the unit normal and unit tangent vectors
        int[] normalVector = new int[2];
        normalVector[0] = positionVector2[0] - positionVector1[0];
        normalVector[1] = positionVector2[1] - positionVector1[1];

        double normalVectorMagnitude = Math.sqrt((normalVector[0]*normalVector[0]) + (normalVector[1]*normalVector[1]));
        double[] unitNormal = new double[2];
        unitNormal[0] = normalVector[0]/normalVectorMagnitude;
        unitNormal[1] = normalVector[1]/normalVectorMagnitude;

        // double[] unitTangent = new double[2];
        // unitTangent[0] = -unitNormal[1];
        // unitTangent[1] = unitNormal[0];

        // //Get the velocities of both entities prior to the collision
        // int[] initialVelocity1 = new int[2];
        // initialVelocity1[0] = TICKSPERSECOND*(e1.getWorldX() - e1.getExWorldX());
        // initialVelocity1[1] = TICKSPERSECOND*(e1.getWorldY() - e1.getExWorldY());

        // int[] initialVelocity2 = new int[2];
        // initialVelocity2[0] = TICKSPERSECOND*(e2.getWorldX() - e2.getExWorldX());
        // initialVelocity2[1] = TICKSPERSECOND*(e2.getWorldY() - e2.getExWorldY());

        // //Use dot product method to multiply the vectors in order to get the scalar velocity projections 
        // //onto the tangent and normal
        // double initialVelocityNormal1 = (unitNormal[0]*initialVelocity1[0]) + (unitNormal[1]*initialVelocity1[1]); 
        // double velocityTangent1 = (unitTangent[0]*initialVelocity1[0]) + (unitTangent[1]*initialVelocity1[1]);
            
        // double initialVelocityNormal2= (unitNormal[0]*initialVelocity2[0]) + (unitNormal[1]*initialVelocity2[1]); 
        // double velocityTangent2 = (unitTangent[0]*initialVelocity2[0]) + (unitTangent[1]*initialVelocity2[1]);

        // //Get the new normal velocities
        // double mass1 = e1.getMass();
        // double mass2 = e2.getMass();
        // double newVelocityNormal1 = (initialVelocityNormal1*(mass1-mass2) + 2*mass2*initialVelocityNormal2)
        //                             /(mass1 + mass2);
        // double newVelocityNormal2 = (initialVelocityNormal2*(mass2-mass1) + 2*mass1*initialVelocityNormal1)
        //                             /(mass1 + mass2);

        // //Conver the scalar normal and tangential velocities into vectors
        // double[] newNormalVector1 = new double[2];
        // newNormalVector1[0] = newVelocityNormal1*unitNormal[0];
        // newNormalVector1[1] = newVelocityNormal1*unitNormal[1];

        // double[] newNormalVector2 = new double[2];
        // newNormalVector2[0] = newVelocityNormal2*unitNormal[0];
        // newNormalVector2[1] = newVelocityNormal2*unitNormal[1];

        // double[] newTangentVector1 = new double[2];
        // newTangentVector1[0] = velocityTangent1*unitTangent[0];
        // newTangentVector1[1] = velocityTangent1*unitTangent[1];

        // double[] newTangentVector2 = new double[2];
        // newTangentVector2[0] = velocityTangent2*unitTangent[0];
        // newTangentVector2[1] = velocityTangent2*unitTangent[1];

        // //Find the new velocities
        // double[] newVelocityVector1 = new double[2];
        // newVelocityVector1[0] = newNormalVector1[0] + newTangentVector1[0];
        // newVelocityVector1[1] = newNormalVector1[1] + newTangentVector1[1];

        // double[] newVelocityVector2 = new double[2];
        // newVelocityVector2[0] = newNormalVector2[0] + newTangentVector2[0];
        // newVelocityVector2[1] = newNormalVector2[1] + newTangentVector2[1];

        // System.out.println(Arrays.toString(newVelocityVector1));

        // //Apply the velocities
        // e1.setWorldX((int) (newVelocityVector1[0]/TICKSPERSECOND + e1.getWorldX()) - e1.getWidth()/2);
        // e1.setWorldY((int) (newVelocityVector1[1]/TICKSPERSECOND + e1.getWorldY()) - e1.getHeight()/2);
        // e2.setWorldX((int) (newVelocityVector2[0]/TICKSPERSECOND + e2.getWorldX()) - e2.getWidth()/2);
        // e2.setWorldY((int) (newVelocityVector2[1]/TICKSPERSECOND + e2.getWorldY()) - e2.getHeight()/2);

        //Get the overlaps on both axes
        double overlapX = Math.min(b1[3], b2[3]) - Math.max(b1[2], b2[2]);
        double overlapY = Math.min(b1[1], b2[1]) - Math.max(b1[0], b2[0]);

        //Get the dot product of the overlaps and their respective unit normals
        double overlap = overlapX * Math.abs(unitNormal[0]) + overlapY * Math.abs(unitNormal[1]);

        //Add a minimum overlap threshold in order to minimize constant correction
        double overlapThreshold = 5;
        if (overlap > overlapThreshold){
            //Divide overlap by an arbitrary number to smooth out the visual resolution of the collision
            double resolutionFactor = overlap / 8;
            e1.setWorldX((int)(e1.getWorldX() - unitNormal[0] * resolutionFactor));
            e1.setWorldY((int)(e1.getWorldY() - unitNormal[1] * resolutionFactor));
            e2.setWorldX((int)(e2.getWorldX() + unitNormal[0] * resolutionFactor));
            e2.setWorldY((int)(e2.getWorldY() + unitNormal[1] * resolutionFactor));
        }

        e1.matchHitBoxBounds();
        e2.matchHitBoxBounds();

        // Vector vrel = e1.getVelocity() - e2.getVelocity();

        // double impulseMagnitude = -(Dot(vrel, contactNormal));
        // Vector impulseDirection = contactNormal;

        // Vector jn = impulseDirection * impulseMagnitude;

        // e1.applyImpulse(jn);
        // e2.applyImpulse(-jn);


        // //TODO: DOESNT WORK AS INTENDED
        // //Directions are relative to e1's bounds
        // int topOverlap = b2[1] - b1[0];
        // int bottomOverlap = b1[1] - b2[0]; 
        // int leftOverlap = b2[3] - b1[2];
        // int rightOverlap = b1[3] - b2[2];

        // //Find the minimum overlap to determine which direction you can prevent the overlap with the least movement
        // int minOverlap = Math.min(Math.min(topOverlap, bottomOverlap), Math.min(leftOverlap, rightOverlap));

        // if (minOverlap == topOverlap)
        //     e1.setWorldY(e1.getWorldY() + minOverlap);
        // else if (minOverlap == bottomOverlap)
        //     e1.setWorldY(e1.getWorldY() - minOverlap);
        // else if (minOverlap == leftOverlap)
        //     e1.setWorldX(e1.getWorldX() + minOverlap);
        // else if (minOverlap == rightOverlap)
        //     e1.setWorldX(e1.getWorldY() - minOverlap);

    }

    private void processInputs(){
        keyInputQueue.forEach((key, cid) ->{
            Player player = (Player) getPlayerFromClientId(cid);
            player.move(key);
            // player.setVelocity();
        });
        keyInputQueue.clear();

        clickInputQueue.forEach((clickInput) -> {processClickInput(clickInput.x, clickInput.y, clickInput.cid);});
        clickInputQueue.clear();
    }

    public void loadKeyInput(char input, int cid){
        keyInputQueue.put(input, cid);
    }

    public void loadClickInput(int x, int y, int cid){
        clickInputQueue.add(new ClickInput(x, y, cid));
    }

    
    public void processClickInput(int clickX, int clickY, int cid){
        
        Player originPlayer = (Player) getPlayerFromClientId(cid);
        Attack playerAttack = (Attack) getAttackFromClientId(cid);

        //Temporary debouncing check
        if (playerAttack != null) return;

        int attackDamage = originPlayer.getDamage();
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

        //Avoids 0/0 division edge case
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
        StringBuilder parseableStr = new StringBuilder();
        parseableStr.append(cid);

            for(Entity entity : entities){
                parseableStr.append(entity.getAssetData());
                // If the entity is the user player
                if (entity.getIdentifier() == 'A' && entity.getClientId() == cid)
                    parseableStr.append('$'); // Indicates that the userPlayerIndex comes next
            }

            currentRoom = new Room('A');
            parseableStr.append(currentRoom.getRoomId() + "0,0%");


        return parseableStr.toString();
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
