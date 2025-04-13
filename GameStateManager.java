import java.util.concurrent.CopyOnWriteArrayList;

public class GameStateManager {
    private CopyOnWriteArrayList<Entity> entities;
    private int userPlayerIndex;
    private Room currentRoom;

    public GameStateManager(){
        entities = new CopyOnWriteArrayList<>();
        userPlayerIndex = -1;
        currentRoom = null;
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

    public void update(){
        for (Entity entity : entities) {
            entity.update();
        }
    }

    public String getAssetsData (int cid){
        String parseableStr = "" + cid;

            for(Entity entity : entities){
                parseableStr += entity.getAssetData();
                // If the entity is the user player
                if (isEntityTheUserPlayer(entity, cid)){
                    parseableStr += '$'; // Indicates that the userPlayerIndex comes next
                }
            }

            currentRoom = new Room('A');
            parseableStr += currentRoom.getRoomId() + "0,0%";

        return parseableStr;
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



}
