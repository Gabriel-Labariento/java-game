import java.util.concurrent.CopyOnWriteArrayList;

public class GameStateManager {
    private CopyOnWriteArrayList<Entity> entities;
    private DungeonMap dungeonMap;
    private int userPlayerIndex;
    private Room currentRoom;

    public GameStateManager() {
        entities = new CopyOnWriteArrayList<>();
        userPlayerIndex = -1;
        dungeonMap = new DungeonMap();
        dungeonMap.generateRooms(3);
        currentRoom = dungeonMap.getStartRoom();
    }

    /**
     * Builds a string that is the serialized form of the map data.
     * 
     * @return a string in the form
     *         M:{RoomCount}|R:{roomId},{roomX},{roomY},{isStart},{isEnd}|D:{doorId},{doorX},{doorY},{direction},{roomAId},{roomBId}|...|{startingRoomId}
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
            sb.append(NetworkProtocol.USER_PLAYER).append(":")
            .append(userPlayerData);
        }

        // Entity String : E:entity1X,entity1Y,entity2X,entity2Y...|
        for (Entity entity : entities) {
            if ((entity instanceof Player) && (entity != userPlayer)) {
                // Player String: P:clientId,playerX,playerY
                sb.append(NetworkProtocol.PLAYER).append(":").append((entity.getAssetData(false)))
                .append(NetworkProtocol.DELIMITER);
            }
            // TODO: Add more implementations for other entities
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
            int clientId = Integer.parseInt(dataParts[0].substring((NetworkProtocol.ROOM_CHANGE + ":").length()));
            int newX = Integer.parseInt(dataParts[1]);
            int newY = Integer.parseInt(dataParts[2]);
            int newRoomId = Integer.parseInt(dataParts[3]);

            // Use the data to set relevant fields
            Room newRoom = dungeonMap.getRoomFromId(newRoomId);
            userPlayer.setWorldX(newX);
            userPlayer.setWorldY(newY);
            userPlayer.setCurrentRoom(newRoom);

            // Build String to be returned
            sb.append(NetworkProtocol.USER_PLAYER).append(":")
            .append(clientId).append(NetworkProtocol.SUB_DELIMITER)
            .append(newX).append(NetworkProtocol.SUB_DELIMITER)
            .append(newY).append(NetworkProtocol.SUB_DELIMITER)
            .append(newRoomId).append(NetworkProtocol.DELIMITER);

            // System.out.println("String returned by handleRoomTransition: " + sb.toString());

            return sb.toString();
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

    public void update() {
        for (Entity entity : entities) {
            entity.update();
        }
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
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.remove(e);
    }

    public CopyOnWriteArrayList<Entity> getEntities() {
        return entities;
    }
}
