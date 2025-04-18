import java.util.concurrent.CopyOnWriteArrayList;

public class GameStateManager {
    private CopyOnWriteArrayList<Entity> entities;
    private DungeonMap dungeonMap;
    private int userPlayerIndex;
    private Room currentRoom;


    public GameStateManager(){
        entities = new CopyOnWriteArrayList<>();
        userPlayerIndex = -1;
        dungeonMap = new DungeonMap();
        dungeonMap.generateRooms(3);
        currentRoom = dungeonMap.getStartRoom();
    }

    /**
     * Builds a string that is the serialized form of the map data.
     * @return a string in the form M:{RoomCount}|R:{roomId},{roomX},{roomY},{isStart},{isEnd}|D:{doorId},{doorX},{doorY},{direction},{roomAId},{roomBId}|...|{startingRoomId}
     */
    public String getMapData(){
        return dungeonMap.serialize();
    }

    public DungeonMapDeserializeResult parseMapData(String message){
        return dungeonMap.deserialize(message);
    }

    /**
     * Builds a string that serializes all the asset data (player and entities).
     * In the form ClientId|P:playerX,playerY|E:entityIdentifier,entity1X,entity1Y,entityIdentifier,entity2x,entity2Y...|
     * @param cid id of the client 
     * @return a serialized string containing the data of all entities
     */
    public String getAssetsData (int cid){
        StringBuilder sb  = new StringBuilder();
        
        sb.append(cid).append(NetworkProtocol.DELIMITER);

        // Player String : P:playerX,playerY|
        Entity userPlayer = getPlayerFromClientId(cid);
        sb.append(NetworkProtocol.USER_PLAYER).append(":");
        sb.append(userPlayer.getWorldX()).append(NetworkProtocol.SUB_DELIMITER)
        .append(userPlayer.getWorldY()).append(NetworkProtocol.DELIMITER);

        // Entity String : E:entity1X,entity1Y,entity2X,entity2Y...|
        for(Entity entity : entities){
            if ( (entity instanceof Player) && (entity != userPlayer) ) {
                sb.append(NetworkProtocol.PLAYER).append(":").append(entity.getAssetData()).append(NetworkProtocol.DELIMITER);
            } 
            // TODO: Add more implementations for other entities
        }
        return sb.toString();
    }

    public Entity getPlayerFromClientId(int cid){
        for (Entity entity : entities) {
            if (entity.getIdentifier() == NetworkProtocol.PLAYER.toCharArray()[0] && entity.getClientId() == cid) return entity;
        }
        return null;
    }

    public int getUserPlayerIndex() {
        return userPlayerIndex;
    }

    public void setUserPlayerIndex(int userPlayerIndex) {
        this.userPlayerIndex = userPlayerIndex;
    }

    public void updateUserPlayerIndex(int cid){
        userPlayerIndex = entities.indexOf(getPlayerFromClientId(cid));
    }

    public void update(){
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

    public void addEntity(Entity e){
        e.setCurrentRoom(currentRoom);
        entities.add(e);
    }

    public void removeEntity(Entity e){
        entities.remove(e);
    }

    public CopyOnWriteArrayList<Entity> getEntities(){
        return entities;
    }
}
