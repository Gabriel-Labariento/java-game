import java.util.*;
import java.util.concurrent.*;

public class ServerMaster {
    private CopyOnWriteArrayList<Entity> entities;
    private DungeonMap dungeonMap;
    private int userPlayerIndex;
    private Room currentRoom;
    private static int gameLevel = 0; // TODO: INCREMENT WHEN DEFEAT BOSS
    private static final int MAX_LEVEL = 7;
    private ConcurrentHashMap<Character, Integer> keyInputQueue;
    private ConcurrentHashMap<Integer, Integer> availableRevives;
    private ArrayList<ClickInput> clickInputQueue;
    private int playerNum;
    private int downedPlayersNum;

    private static ServerMaster singleInstance = null;

    private ServerMaster(){
        playerNum = 0;
        downedPlayersNum = 0;
        entities = new CopyOnWriteArrayList<>();
        userPlayerIndex = -1;
        dungeonMap = new DungeonMap(gameLevel);
        dungeonMap.generateRooms(3);
        currentRoom = dungeonMap.getStartRoom();
        //-----------------------------//
        keyInputQueue = new ConcurrentHashMap<>();
        availableRevives = new ConcurrentHashMap<>();
        clickInputQueue = new ArrayList<>();
    }


    public static synchronized ServerMaster getInstance() {
        if (singleInstance == null) singleInstance = new ServerMaster();
        return singleInstance;
    }

    public void update(){
        // Do not update the game at start of the gameserver (no entities yet)
        //  System.out.println("Entities array size: " + entities.size());
        if (entities.isEmpty()) return;

        // Update objects accordingly to the inputs
        processInputs();

        //Detect and resolve collisions
        checkCollisions();

        //Process the changes and update existing entities accordingly
        updateEntities();

    }

    public void updateEntities(){
        //Check on and resolve the end of life properties of each entity
        for (Entity entity:entities){
            if(entity instanceof Player player && player.getHitPoints() <=0){
                //DOWNING AND REVIVAL MECHANICS
                //If the player has not yet been recorded as being downed, set them as such
                if (!player.getIsDown()){
                    player.setIsDown(true);

                    //If no players are left, activate end game sequence
                    downedPlayersNum++;
                    if (downedPlayersNum == playerNum){
                        System.out.println("GAME OVER");
                    }
                }

                //Search through list of available revives to see if the player can be revived
                boolean isInContact = false;
                for (int i:availableRevives.values()){
                    
                    if (i == player.getClientId()){
                        //If the player has not yet been recorded as reviving, set them as such
                        if (!player.getIsReviving()){
                            player.triggerRevival();
                            player.setIsReviving(true);
                        }

                        isInContact = true;
                        break;
                    } 
                }

                //If the other player has moved away from the downed player
                if(!isInContact){
                    player.setIsReviving(false);
                    //Insert UI indicators
                }

                //After the revivaltime and without the living player moving away, revive the downed player with one health
                if(player.getIsReviving() && player.getIsRevived()){
                    player.setHitPoints(1);
                    player.setIsDown(false);
                    //Insert revival animation
                }
            }
            else if (entity instanceof Attack attack && attack.getIsExpired()){
                entities.remove(entity);
            }
            else if (entity instanceof Enemy enemy && enemy.getHitPoints() <= 0){
                //Trigger death animation;
                entities.remove(entity);
            }
            entity.updateEntity(this);
        }
        //Reset list to track available revives per frame
        availableRevives.clear();
    }

    // Checks for collisions between all objects inside the entity ArrayList
    public void checkCollisions(){
        //SORT, SWEEP, AND, PRUNE DETECTION
        try {
            //Make a new arraylist containing all of the elements of entities
            ArrayList<Entity> sortedEntities = new ArrayList<>(entities);

            //Sort entities from the universal arraylist by their worldx values (left bounds)
            Collections.sort(sortedEntities, Comparator.comparingInt(e -> e.getHitBoxBounds()[2]));
            // System.out.println("Number of entities in sortedEntities " + sortedEntities.size());
            int size = sortedEntities.size();
            for(int i = 0; i < size; i++){
                Entity entity1 = sortedEntities.get(i);
                int[] b1 = entity1.getHitBoxBounds();

                // Get the entity at the next index
                for(int j = (i+1); j < size; j++){
                    Entity entity2 = sortedEntities.get(j);
                    if (entity2 == null) {
                        // System.out.println("entity 2 is null in sortedEntities");
                        continue;
                    }

                    int[] b2 = entity2.getHitBoxBounds();
 
                    //Skip detection if the second entity starts after the first ends on the x-axis
                    if(b2[2]>b1[3]) break;

                    //Check for collisions in the top, bottom, left, right of entity1 against entity2
                    if (b1[0] < b2[1] && b1[1] > b2[0] && b1[2] < b2[3] && b1[3] > b2[2]){
                        resolveCollision(entity1, entity2, b1, b2);
                    }
                }
            }   
        } catch (Exception e) {
            System.err.println("Exception in check collisions: " + e);
        }
        
    }

    public void resolveCollision(Entity e1, Entity e2, int[] b1, int[] b2){

        // ATTACK-PLAYER/ENEMY COLLISION HANDLING
        // If entity is an attack and is not friendly and if the second entity is a player, then the player takes damage.
        // If entity is an attack and is friendly and if the second entity is an enemy, then the enemy takes damage.
        
        if (e1 instanceof Attack attack && !attack.getIsFriendly() && e2 instanceof Player player){
            damagePlayer(player, attack);
        }

        else if (e2 instanceof Attack attack && !attack.getIsFriendly() && e1 instanceof Player player){
            damagePlayer(player, attack);
        }
            
        else if (e1 instanceof Attack attack && attack.getIsFriendly() && e2 instanceof Enemy enemy){
            damageEnemy(enemy, attack);
        }
            
        else if (e2 instanceof Attack attack && attack.getIsFriendly() && e1 instanceof Enemy enemy){
            damageEnemy(enemy, attack);
        }
            

        //PLAYER/ENEMY COLLISION HANDLING
        else if (e1 instanceof Player player && e2 instanceof Enemy enemy && player.getIsInvincible()){
            preventOverlap(player, enemy, b1, b2);
            player.setHitPoints(player.getHitPoints()- enemy.getDamage());
            player.triggerInvincibility();
        }

        else if (e2 instanceof Player player && e1 instanceof Enemy enemy && player.getIsInvincible()){
            player.setHitPoints(player.getHitPoints()- enemy.getDamage());
            player.triggerInvincibility();
        }

        else if (e1 instanceof Enemy && e2 instanceof Enemy)
            preventOverlap(e1, e2, b1, b2);

        else if (e1 instanceof Player p1 && e2 instanceof Player p2){
            //Collision detection for revival system
            int cid1 = p1.getClientId();
            int cid2 = p2.getClientId();

            //Check if living player is already in the availableRevives list to avoid duplicates
            if(!p1.getIsDown() && p2.getIsDown() && availableRevives.get(cid1) == null)
                availableRevives.put(cid1, cid2);
            else if (!p2.getIsDown() && p1.getIsDown() && availableRevives.get(cid2) == null)
                availableRevives.put(cid2, cid1);
        }

        
        // if (entity1 instanceof Player && entity2 instanceof Attack ||
        //     entity2 instanceof Player && entity1 instanceof Attack) {
        //         Player player = entity1 instanceof Player ? (Player) entity1 : (Player) entity2;
        //         Attack attack = entity1 instanceof Attack ? (Attack) entity1 : (Attack) entity2;
            
        //         if (attack.getClientId() == player.getClientId()) continue; // don't process attack is from the player
        //     }

    }

    //Players generate i-frames when damaged
    private void damagePlayer(Player player, Attack attack){
        //Debouncing condition
        if(player.getIsInvincible()){
            player.setHitPoints(player.getHitPoints()-attack.getDamage());
            player.triggerInvincibility();
        }

    }

    //Enemy can only take one instance of damage per attack
    private void damageEnemy(Enemy enemy, Attack attack){
        int id = attack.getId();
        //Debouncing condition
        if(enemy.validateAttack(id)){
            enemy.setHitPoints(enemy.getHitPoints()-attack.getDamage());
            enemy.loadAttack(id);
        }

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

        //Get the overlaps on both axes
        double overlapX = Math.min(b1[3], b2[3]) - Math.max(b1[2], b2[2]);
        double overlapY = Math.min(b1[1], b2[1]) - Math.max(b1[0], b2[0]);

        //Get the dot product of the overlaps and their respective unit normals
        double overlap = overlapX * Math.abs(unitNormal[0]) + overlapY * Math.abs(unitNormal[1]);

        //Add a minimum overlap threshold in order to minimize constant correction
        double overlapThreshold = 5;
        if (overlap > overlapThreshold){
            //Divide overlap by an arbitrary number to smoothen out the visual resolution of the collision
            double resolutionFactor = overlap / 8;
            e1.setWorldX((int)(e1.getWorldX() - unitNormal[0] * resolutionFactor));
            e1.setWorldY((int)(e1.getWorldY() - unitNormal[1] * resolutionFactor));
            e2.setWorldX((int)(e2.getWorldX() + unitNormal[0] * resolutionFactor));
            e2.setWorldY((int)(e2.getWorldY() + unitNormal[1] * resolutionFactor));
        }

        e1.matchHitBoxBounds();
        e2.matchHitBoxBounds();

    }

    private void processInputs(){
        keyInputQueue.forEach((key, cid) ->{
            // System.out.println("Processing input: " + key + "," + cid);
            Player player = (Player) getPlayerFromClientId(cid);
            //Restrain player movement if downed
            if (!player.getIsDown()) player.update(key);           
            
        });
        keyInputQueue.clear();

        clickInputQueue.forEach((clickInput) -> {processClickInput(clickInput.x, clickInput.y, clickInput.cid);});
        clickInputQueue.clear();
    }

    public void loadKeyInput(char input, int cid){
        keyInputQueue.put(input, cid);
        // System.out.println("Key: " + input);
        // System.out.println("cid: " + cid);
    }

    public void loadClickInput(int x, int y, int cid){
        clickInputQueue.add(new ClickInput(x, y, cid));
    }

    public void processClickInput(int clickX, int clickY, int cid){
        // System.out.println("Processing clickc input for player: " + cid);

        Player originPlayer = (Player) getPlayerFromClientId(cid);
        Attack playerAttack = (Attack) getAttackFromClientId(cid);

        //Debouncing constraints
        if(originPlayer.getIsOnCoolDown() || originPlayer.getIsDown()) return;
        
        double attackDamage = originPlayer.getDamage();
        int attackSpeed = originPlayer.getSpeed();
        int frameWidth = 720;
        int frameHeight = 540;
        int centerX = frameWidth/2;
        int centerY = frameHeight/2;

        //Get a point a set distance away from the center of the screen in the direction of the click
        int vectorX = clickX - centerX;
        int vectorY = clickY - centerY;  
        int distance = 20;
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

        //TODO: If originPlayer is of different type, instantiate another type of attack
        int attackWidth;
        int attackHeight;
        if (true){
            attackWidth = 40;
            attackHeight = 40;
            playerAttack = new PlayerSlash(cid, originPlayer, worldX-attackWidth/2, worldY - attackHeight/2, 
            attackWidth, attackHeight, attackDamage, true, attackSpeed);
            playerAttack.matchHitBoxBounds();
            playerAttack.setCurrentRoom(originPlayer.getCurrentRoom());
        } //else {}//

        originPlayer.triggerCoolDown();
        entities.add(playerAttack); 
        // System.out.println("Created PlayerSlash: " + playerAttack.getId() + " at (" + playerAttack.getWorldX() + ", " + playerAttack.getWorldX() + ")");
    }



    /**
     * Increments the static gameLevel field if it is less than MAX_LEVEL
     */
    public void incrementGameLevel(){
        if (gameLevel < MAX_LEVEL) gameLevel++;
    }

    /**
     * Creates a new dungeon and updates user player position
     * in the new starting room.
     */
    public void generateNewDungeon(){
        Player userPlayer = null;
        
        if (userPlayerIndex >= 0 && userPlayerIndex < entities.size()) { // Ensure the userPlayer is in entities
            userPlayer = (Player) entities.get(userPlayerIndex);
        } 

        dungeonMap = new DungeonMap(gameLevel);
        dungeonMap.generateRooms(3 + gameLevel);
        currentRoom = dungeonMap.getStartRoom();

        entities.clear(); // Safe to clear, already have reference to userPlayer

        if (userPlayer != null) {
            userPlayer.setWorldX(currentRoom.getCenterX());
            userPlayer.setWorldX(currentRoom.getCenterX());
            userPlayer.setCurrentRoom(currentRoom);
            entities.add(userPlayer);
            updateUserPlayerIndex(userPlayer.getClientId());
        }
        
    }

    /**
     * Builds a string that is the serialized form of the map data.
     * 
     * @return a string in the form
     * M:{RoomCount}|R:{roomId},{roomX},{roomY},{isStart},{isEnd}|D:{doorId},{doorX},{doorY},{direction},{roomAId},{roomBId}|...|{startingRoomId}
     */
    public String getMapData() {
        return dungeonMap.serialize();
    }

    public DungeonMapDeserializeResult parseMapData(String message) {
        return dungeonMap.deserialize(message);
    }

    /**
     * Builds a string that serializes all the asset data (player and entities).
     * In the form
     * ClientId|P:clientId,playerX,playerY,currentRoom|E:entityIdentifier,entity1X,entity1Y,entityIdentifier,entity2x,entity2Y...|
     * 
     * @param cid id of the client
     * @return a serialized string containing the data of all entities
     */
    public String getAssetsData(int cid) {
        StringBuilder sb = new StringBuilder();

        sb.append(cid).append(NetworkProtocol.DELIMITER);

        // User Player String: P$:clientId,playerX,playerY 
        Player userPlayer = (Player) getPlayerFromClientId(cid);
        String userPlayerData = userPlayer.getAssetData(true);

        if (userPlayerData.startsWith(NetworkProtocol.ROOM_CHANGE)) {
            // Handle room change logic here on the server side
            sb.append(handleRoomTransition(userPlayer, userPlayerData));
        } else {
            // Normal data without room change
            sb.append(NetworkProtocol.USER_PLAYER)
            .append(userPlayerData);
        }

        // Loop through entities array
        for (Entity entity : entities) {
            if ((entity instanceof Player) && (entity != userPlayer)) {
                // Player String: P:clientId,playerX,playerY
                sb.append(NetworkProtocol.PLAYER).append((entity.getAssetData(false)))
                .append(NetworkProtocol.DELIMITER);
            } else if (!(entity instanceof  Player)) {
                // NPCs ex. E:B,id,x,y,currentRoomId| => Rat with id at currentRoomId (x,y)
                if (entity == null) continue;
                sb.append(NetworkProtocol.ENTITY)
                .append(entity.getAssetData(false));  
            } 
            
        }
        return sb.toString();
    }


    /**
     * Called when the userPlayer's getAssetData() string indicates a room change.
     * Allows the program to handle the userPlayer's room change logic on the server side
     * by setting the userPlayer's new room and coordinates.
     * @param userPlayer the Player object of the user who indicated the room change
     * @param userPlayerData the String returned by the getAssetData() method of the userPlayer
     * @return a serialized String to be sent to the client's data handler indicating the userPlayer's new position.
     * The returned string is in the form: P$:clientId,newx,newY,newRoomId|
     */
    private String handleRoomTransition(Player userPlayer, String userPlayerData) {
            StringBuilder sb = new StringBuilder();

            // Split the userPlayerData string by ","
            String[] dataParts = userPlayerData.split(NetworkProtocol.SUB_DELIMITER);
            
            // Extract data from it
            int clientId = Integer.parseInt(dataParts[0].substring((NetworkProtocol.ROOM_CHANGE).length()));
            int newX = Integer.parseInt(dataParts[1]);
            int newY = Integer.parseInt(dataParts[2]);
            double hp = Double.parseDouble(dataParts[3]);
            int newRoomId = Integer.parseInt(dataParts[4]);

            // Use the data to set relevant fields
            Room newRoom = dungeonMap.getRoomFromId(newRoomId);
            userPlayer.setWorldX(newX);
            userPlayer.setWorldY(newY);
            userPlayer.setCurrentRoom(newRoom);
            userPlayer.setHitPoints(hp);
            currentRoom = newRoom;
            handleSpawnersOnRoomChange(newRoom);
            currentRoom.closeDoors();

            // Build String to be returned
            sb.append(NetworkProtocol.USER_PLAYER)
            .append(clientId).append(NetworkProtocol.SUB_DELIMITER)
            .append(newX).append(NetworkProtocol.SUB_DELIMITER)
            .append(newY).append(NetworkProtocol.SUB_DELIMITER)
            .append(hp).append(NetworkProtocol.SUB_DELIMITER)
            .append(newRoomId).append(NetworkProtocol.DELIMITER);

            // System.out.println("String returned by handleRoomTransition: " + sb.toString());

            return sb.toString();
    }

    public void handleSpawnersOnRoomChange(Room next){
        if (next.getMobSpawner() != null && (!next.getMobSpawner().isSpawning())) next.getMobSpawner().spawn();

        if (next.isEndRoom() && next.getMobSpawner().isAllKilled()) incrementGameLevel();
    }

    /**
     * Searches through the entities arrayList to look for a player object with the provided clientId
     * @param cid the clientId of the connectedPlayer
     * @return the Player object with the corresponding clientId
     */
    public Entity getPlayerFromClientId(int cid) {
        for (Entity entity : entities) {
            if (entity.getIdentifier() == NetworkProtocol.PLAYER.toCharArray()[0] && entity.getClientId() == cid)
                return entity;
        }
        return null;
    }

    public int getUserPlayerIndex() {
        return userPlayerIndex;
    }

    public void setUserPlayerIndex(int userPlayerIndex) {
        this.userPlayerIndex = userPlayerIndex;
    }

    public void updateUserPlayerIndex(int cid) {
        userPlayerIndex = entities.indexOf(getPlayerFromClientId(cid));
    }

      public DungeonMap getDungeonMap() {
        return dungeonMap;
    }

    public void setDungeonMap(DungeonMap dungeonMap) {
        this.dungeonMap = dungeonMap;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public void addEntity(Entity e) {
        e.setCurrentRoom(currentRoom);
        if(e instanceof Player) playerNum++;
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    public CopyOnWriteArrayList<Entity> getEntities() {
        return entities;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public Entity getAttackFromClientId(int cid){
        for (Entity entity : entities) {
            if (entity instanceof Attack && entity.getClientId() == cid) return entity;
        }
        return null;
    }

    // Stores the x and y coordinates, as well as the client id of the click input.
    private static class ClickInput{
        public int x, y, cid;

        public ClickInput(int x, int y, int cid){
            this.x = x;
            this.y = y;
            this.cid = cid;
        }
    } 
}
