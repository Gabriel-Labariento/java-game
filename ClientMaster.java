import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientMaster {
    private Player userPlayer;
    private HashMap <Integer, Room> allRooms;
    private Room currentRoom;
    private CopyOnWriteArrayList<Entity> entities; 
    private int xpBarPercent;
    private int userLvl;
    private char heldItemIdentifier;
    private static final HashMap<Character, String> idToName = new HashMap<>();
    static {
        //Initiliaze idtoname hashmap using static block
        idToName.put(NetworkProtocol.REDFISH.charAt(0), "Redfish");
        idToName.put(NetworkProtocol.CATTREAT.charAt(0), "Cat Treat");
        idToName.put(NetworkProtocol.MILK.charAt(0), "Milk");
        idToName.put(NetworkProtocol.PREMIUMCATFOOD.charAt(0), "Premium Cat Food++");
        idToName.put(NetworkProtocol.GOLDFISH.charAt(0), "Goldfish");
        idToName.put(NetworkProtocol.LIGHTSCARF.charAt(0), "Light Scarf");
        idToName.put(NetworkProtocol.THICKSWEATER.charAt(0), "Thick Sweater");
        idToName.put(NetworkProtocol.BAGOFCATNIP.charAt(0), "Bag of Catnip");
        idToName.put(NetworkProtocol.LOUDBELL.charAt(0), "Loud Bell");
        idToName.put(NetworkProtocol.PRINGLESCAN.charAt(0), "Pringles Can");
        idToName.put(NetworkProtocol.HEAVYCAT.charAt(0), "HeavyCat");
        idToName.put(NetworkProtocol.FASTCAT.charAt(0), "FastCat");
        idToName.put(NetworkProtocol.GUNCAT.charAt(0), "GunCat");
        idToName.put(NetworkProtocol.PLAYERSMASH.charAt(0), "PlayerSmash");
        idToName.put(NetworkProtocol.PLAYERSLASH.charAt(0), "PlayerSlash");
        idToName.put(NetworkProtocol.PLAYERBULLET.charAt(0), "PlayerBullet");
        idToName.put(NetworkProtocol.RAT.charAt(0), "Rat");
    }

    public ClientMaster(){
        userPlayer = null;
        allRooms = null;
        currentRoom = null;
        entities = new CopyOnWriteArrayList<>();
    }

    public void setXPBarPercent(int percent){
        xpBarPercent = percent;
    }

    public int getXPBarPercent(){
        return xpBarPercent;
    }

    public void setUserLvl(int lvl){
        userLvl = lvl;
    }

    public int getUserLvl(){
        return userLvl;
    }

    public HashMap<Integer, Room> getAllRooms() {
        return allRooms;
    }

    public void setAllRooms(HashMap<Integer, Room> allRooms) {
        this.allRooms = allRooms;
    }

    public Player getUserPlayer() {
        return userPlayer;
    }

    public void setUserPlayer(Player userPlayer) {
        this.userPlayer = userPlayer;
    }


    public Entity getEntity(char identifier, int id, int x, int y){
        //We use a hashmap to determine such for readability and since switch statements need constant values
        String name = idToName.get(identifier);
        if (name == null) return null;
        switch (name){
            case "HeavyCat":
                return new HeavyCat(id, x, y);
            case "FastCat":
                return new FastCat(id, x, y);
            case "GunCat":
                return new GunCat(id, x, y);
            case "Redfish":
                return new RedFish(x, y);
            case "Cat Treat":
                return new CatTreat(x, y);
            case "Milk":
                return new Milk(x, y);
            case "Premium Cat Food++":
                return new PremiumCatFood(x, y);
            case "Goldfish":
                return new Goldfish(x, y);
            case "Light Scarf":
                return new LightScarf(x, y);
            case "Thick Sweater":
                return new ThickSweater(x, y);
            case "Bag of Catnip":
                return new BagOfCatnip(x, y);
            case "Loud Bell":
                return new LoudBell(x, y);
            case "Pringles Can":
                return new PringlesCan(x, y);
            case "Rat":
                return new Rat(x, y);
            case "PlayerSlash":
                return new PlayerSlash(id, null, x, y, 0, false);
            case "PlayerSmash":
                return new PlayerSmash(id, null, x, y, 0, false);
            case "PlayerBullet":
                return new PlayerBullet(id, null, x, y, 0, 0, 0, false);
            default:
                return null;
        }
    }

    public void loadEntity(char identifier, int id, int x, int y, int roomId){
        // System.out.println("Loading entity " + identifier + " " + name + "at " + x + ", " + y);
        // if (name == null) System.out.println("Warning: unknown identity identifier " + identifier);
        Entity e = getEntity(identifier, id, x, y);
        if (e != null) {
            e.setCurrentRoom(getRoomById(roomId));
            entities.add(e);
        }    
    }

    public void setHeldItemIdentifier(char identifier){
        heldItemIdentifier = identifier;
    }
    
    public Entity generateUIItem(){
        return getEntity(heldItemIdentifier, 0, 0, 0);
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public CopyOnWriteArrayList<Entity> getEntities() {
        return entities;
    }

    public void setEntities(CopyOnWriteArrayList<Entity> entities) {
        this.entities = entities;
    }
    
    public void addEntity(Entity e){
        entities.add(e);
    }

    public Room getRoomById(int id) {
        return allRooms.get(id);
    }

}
