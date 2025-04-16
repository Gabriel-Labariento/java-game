import java.util.concurrent.CopyOnWriteArrayList;

public class GameStateManager {
    private CopyOnWriteArrayList<Entity> entities;
    private DungeonMap dungeonMap;
    private int userPlayerIndex;
    private Room currentRoom;


    public GameStateManager(){
        entities = new CopyOnWriteArrayList<>();
        userPlayerIndex = -1;
        currentRoom = null;
        dungeonMap = new DungeonMap();
        dungeonMap.generateRooms(3);
    }

    /**
     * Builds a string that is the serialized form of the map data.
     * @return a string in the form M:{RoomCount}|R:{roomId},{roomX},{roomY},{isStart},{isEnd}|D:{doorId},{doorX},{doorY},{direction},{roomAId},{roomBId}|...|{startingRoomId}
     */
    public String getMapData(){
        return dungeonMap.serialize();
    }

    public Room parseMapData(String message){
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
        Entity player = getPlayerFromClientId(cid);
        sb.append(NetworkProtocol.PLAYER).append(":");
        sb.append(player.getWorldX()).append(NetworkProtocol.SUB_DELIMITER).append(player.getWorldY()).append(NetworkProtocol.DELIMITER);

        // Entity String : E:entity1X,entity1Y,entity2X,entity2Y...|
        sb.append(NetworkProtocol.ENTITY).append(":");
            for(Entity entity : entities){
                if (entity != player) sb.append(entity.getAssetData()).append(NetworkProtocol.SUB_DELIMITER);
            }

            // TODO: implement currentRoom based on update of Room class
            // currentRoom = new Room('A'); 
            // parseableStr += currentRoom.getRoomId() + "0,0%";

        return sb.toString();
    }

    private boolean isEntityTheUserPlayer(Entity e, int cid){
        return (e.getIdentifier() == 'A' && e.getClientId() == cid);
    }
    public Entity getPlayerFromClientId(int cid){
        for (Entity entity : entities) {
            if (entity.getIdentifier() == 'A' && entity.getClientId() == cid) return entity;
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
        entities.add(e);
    }

    public void removeEntity(Entity e){
        entities.remove(e);
    }

    public CopyOnWriteArrayList<Entity> getEntities(){
        return entities;
    }
}
