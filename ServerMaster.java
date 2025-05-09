import java.util.*;
import java.util.concurrent.*;

public class ServerMaster {
    private CopyOnWriteArrayList<Entity> entities;
    private DungeonMap dungeonMap;
    private int userPlayerIndex;
    private Room currentRoom;
    private static int gameLevel;
    private static final int MAX_LEVEL = 7;
    private ConcurrentHashMap<Character, Integer> keyInputQueue;
    private ConcurrentHashMap<Integer, Integer> availableRevives;
    private ArrayList<GameServer.ConnectedPlayer> connectedPlayers;
    private ArrayList<ClickInput> clickInputQueue;
    private int playerNum;
    private int downedPlayersNum;

    private static ServerMaster singleInstance = null;

    private ServerMaster(){
        playerNum = 0;
        downedPlayersNum = 0;
        gameLevel = 0;
        entities = new CopyOnWriteArrayList<>();
        userPlayerIndex = -1;
        dungeonMap = new DungeonMap(gameLevel);
        dungeonMap.generateRooms();
        currentRoom = dungeonMap.getStartRoom();
        //-----------------------------//
        keyInputQueue = new ConcurrentHashMap<>();
        availableRevives = new ConcurrentHashMap<>();
        clickInputQueue = new ArrayList<>();
        connectedPlayers = new ArrayList<>();
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
                handleDownsAndRevives(player);
            }
            else if (entity instanceof Enemy enemy && enemy.getHitPoints() <= 0){
                //Trigger death animation;
                //Give reward xp to the player who took the last hit
                for (Entity e:entities){
                    if(e instanceof Attack attack && attack.getId() == enemy.getLastAttackID()){
                        ((Player)attack.getOwner()).applyXP(enemy.getRewardXP());
                    }
                }
                entities.remove(entity);
            }
            else if (entity instanceof Attack attack && attack.getIsExpired()){
                entities.remove(entity);
            }
            entity.updateEntity(this);
        }
        //Reset list to track available revives per frame
        availableRevives.clear();

        // Check for room clearing after all entity updates
        if (checkRoomCleared()) handleRoomCleared();
    }


    private void handleDownsAndRevives(Player player) {
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
    
    private boolean checkRoomCleared(){
        if (currentRoom.isStartRoom()) return true;
        // System.out.println("Current room is end room: " + currentRoom.isEndRoom());
        return (currentRoom.getMobSpawner().isAllKilled());
    }

    private void handleRoomCleared(){
        if (currentRoom.isStartRoom() || currentRoom.isClearedHandled()) return;

        if (!currentRoom.getMobSpawner().isSpawning()) return;

        currentRoom.getMobSpawner().stopSpawn();
        currentRoom.openDoors();

        if (currentRoom.isEndRoom()) {
            System.out.println("In handleRoomCleared, cleared endroom");
            handleBossDefeat();
        }

        currentRoom.setIsClearedHandled(true);
    }
    
    private void handleBossDefeat(){
        announceBossDefeat();
        incrementGameLevel();
        String doorData = addExitRoomGoingToNewDungeon();
        StringBuilder sb = new StringBuilder();
        sb.append(NetworkProtocol.BOSS_KILLED).append(doorData); //BK:D:doorId,x,y,direction,roomAId,roomBId
        sendMessageToClients(sb.toString());
    }

    private void sendMessageToClients(String message){
        System.out.println("Message in sendMessageToClients(): " + message);
        System.out.println("Number of connected clients: " + connectedPlayers.size());
        for (GameServer.ConnectedPlayer cp : connectedPlayers) {
            cp.promptAssetsThread(message);
        }
    }

    private void announceBossDefeat(){
        StringBuilder sb = new StringBuilder();

        sb.append("CONGRATULATIONS! YOU DEFEATED THE ");
        switch (gameLevel) {
            case 0:
                sb.append("RAT KING");
                break;
            case 1: 
                sb.append("VIPER");
                break;
            case 2:
                sb.append("FIRE MONSTER");
                break;
            default:
                break;
        }
        System.out.println(sb.toString());
    }

    private String addExitRoomGoingToNewDungeon(){
        if (!currentRoom.isEndRoom()) return null;
        String direction = null;
        while (true) {
            direction = currentRoom.chooseRandomDirection();
            if (!currentRoom.getDoors().containsKey(direction)) break;
        }
        System.out.println("Direction: " + direction);

        Door d = currentRoom.createDoorFromDirection(direction);
        d.setIsExitToNewDungeon(true);
        d.setIsOpen(true);
        d.setRoomB(currentRoom);
        currentRoom.addDoorToArrayList(d);
        
        return d.serialize();
    }

    // /**
    //  * Sends a string in the format LC:
    //  */
    // public String broadcastLevelChange(){
    //     DungeonMap next = generateNewDungeon();
        
    //     return null;
    // }


    // Checks for collisions between all objects inside the entity ArrayList
    public void checkCollisions(){
        //SORT, SWEEP, AND, PRUNE DETECTION
        try {
            //Make a new arraylist containing all of the elements of entities
            ArrayList<Entity> sortedEntities = new ArrayList<>(entities);
            // System.out.println("Entity count: " + sortedEntities.size());

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
                        // System.out.println("Detected collision between " + entity1.getClass() + " and " + entity2.getClass());
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
            System.out.println("here");
        }
            
        else if (e2 instanceof Attack attack && attack.getIsFriendly() && e1 instanceof Enemy enemy){
            damageEnemy(enemy, attack);
        }
            

        //PLAYER/ENEMY COLLISION HANDLING
        else if (e1 instanceof Player player && e2 instanceof Enemy enemy){
            preventOverlap(player, enemy, b1, b2);
            damagePlayer(player, enemy);
        }

        else if (e2 instanceof Player player && e1 instanceof Enemy enemy){
            preventOverlap(player, enemy, b1, b2);
            damagePlayer(player, enemy);
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
    }

    //Players generate i-frames when damaged
    private void damagePlayer(Player player, Entity entity){
        //Debouncing condition
        if(player.canTakeDamage()){
            player.setHitPoints(player.getHitPoints()-entity.getDamage());
            applyKnockBack(player, entity);
            player.triggerInvincibility();
        }

    }

    //Enemy can only take one instance of damage per attack
    private void damageEnemy(Enemy enemy, Attack attack){
        int id = attack.getId();
        //Debouncing condition
        if(enemy.validateAttack(id)){
            System.out.println("inside here");
            enemy.setHitPoints(enemy.getHitPoints()-attack.getDamage());
            applyKnockBack(enemy, attack);
            enemy.loadAttack(id);
        }
    }

    private void preventOverlap(Entity e1, Entity e2, int[] b1, int[] b2){
        System.out.println("Inside prevent overlap");
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
            int e1X = ((int)(e1.getWorldX() - unitNormal[0] * resolutionFactor));
            int e1Y = ((int)(e1.getWorldY() - unitNormal[1] * resolutionFactor));
            int e2X = ((int)(e2.getWorldX() + unitNormal[0] * resolutionFactor));
            int e2Y = ((int)(e2.getWorldY() + unitNormal[1] * resolutionFactor));

            e1.setPosition(e1X, e1Y);
            e2.setPosition(e2X, e2Y);
        }

        e1.matchHitBoxBounds();
        e2.matchHitBoxBounds();

    }

    private void applyKnockBack(Entity target, Entity attacker) {

        int[] entityPosition = target.getPositionVector();
        int[] attackPosition = attacker.getPositionVector();

        int[] normalVector = getNormalVector(entityPosition, attackPosition);

        double normalVectorMagnitude = Math.sqrt((normalVector[0]*normalVector[0]) + (normalVector[1]*normalVector[1]));
        double[] unitNormal = getUnitNormal(normalVector, normalVectorMagnitude);

        int knockBackStrength = 24;
        int newX = (int) (target. getWorldX() - unitNormal[0] * knockBackStrength);
        int newY = (int)(target.getWorldY() - unitNormal[1] * knockBackStrength);

        target.setPosition(newX, newY);
        target.matchHitBoxBounds();
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

        //Debouncing constraints
        if(originPlayer.getIsOnCoolDown() || originPlayer.getIsDown()) return;
        
        int attackDamage = originPlayer.getDamage();
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
        Attack playerAttack;
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


    private int[] getNormalVector(int[] v1, int[] v2){
        int[] normalVector = new int[2];
        normalVector[0] = v2[0] - v1[0];
        normalVector[1] = v2[1] - v1[1];

        return normalVector;
    }

    private double[] getUnitNormal(int[] normalVector, double normalVectorMagnitude) {
        double[] unitNormal = new double[2];
        unitNormal[0] = normalVector[0] / normalVectorMagnitude;
        unitNormal[1] = normalVector[1] / normalVectorMagnitude;

        return unitNormal;
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
    public DungeonMap generateNewDungeon(){
        Player userPlayer = null;

        if (userPlayerIndex >= 0 && userPlayerIndex < entities.size()) { // Ensure the userPlayer is in entities
            userPlayer = (Player) entities.get(userPlayerIndex);
        } 

        dungeonMap = new DungeonMap(gameLevel);
        dungeonMap.generateRooms();
        
        return dungeonMap;
        // entities.clear(); // Safe to clear, already have reference to userPlayer

        // if (userPlayer != null) {
        //     userPlayer.setWorldX(currentRoom.getCenterX());
        //     userPlayer.setWorldX(currentRoom.getCenterX());
        //     userPlayer.setCurrentRoom(currentRoom);
        //     entities.add(userPlayer);
        //     updateUserPlayerIndex(userPlayer.getClientId());
        // }
        
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

        //UI elements
        sb.append(userPlayer.getXPBarPercent()).append(NetworkProtocol.DELIMITER)
        .append(userPlayer.getCurrentLvl()).append(NetworkProtocol.DELIMITER);

        if (userPlayerData.startsWith(NetworkProtocol.ROOM_CHANGE)) {
            // Handle room change logic here on the server side
            // System.out.println("String in room change of getassetdata: " + userPlayerData); // What does this say
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
                // NPCs ex. G:B,id,x,y,currentRoomId| => Rat with id at currentRoomId (x,y)
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
            char identifier = dataParts[0].substring(NetworkProtocol.ROOM_CHANGE.length()).toCharArray()[0];
            System.out.println("identifier:" + identifier);
            int clientId = Integer.parseInt(dataParts[1]);
            int newX = Integer.parseInt(dataParts[2]);
            int newY = Integer.parseInt(dataParts[3]);
            int hp = Integer.parseInt(dataParts[4]);
            int newRoomId = Integer.parseInt(dataParts[5]);

            // Use the data to set relevant fields
            Room newRoom = dungeonMap.getRoomFromId(newRoomId);
            userPlayer.setPosition(newX, newY);
            userPlayer.setCurrentRoom(newRoom);
            userPlayer.setHitPoints(hp);
            currentRoom = newRoom;
            handleSpawnersOnRoomChange(newRoom);
            if (!currentRoom.isStartRoom()) newRoom.closeDoors();

            // Build String to be returned
            sb.append(NetworkProtocol.USER_PLAYER) 
            .append(identifier).append(NetworkProtocol.SUB_DELIMITER)
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
    }

    /**
     * Searches through the entities arrayList to look for a player object with the provided clientId
     * @param cid the clientId of the connectedPlayer
     * @return the Player object with the corresponding clientId
     */
    public Entity getPlayerFromClientId(int cid) {
        for (Entity entity : entities) {
            if (entity instanceof Player player && player.getClientId() == cid)
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

    public void setConnectedPlayers(ArrayList<GameServer.ConnectedPlayer> connectedPlayers) {
        this.connectedPlayers = connectedPlayers;
    }
 
    public void addConnectedPlayer(GameServer.ConnectedPlayer cp){
        connectedPlayers.add(cp);
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
