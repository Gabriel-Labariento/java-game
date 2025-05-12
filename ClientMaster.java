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
    private static final HashMap<Character, String> IDENTIFIERTONAME = new HashMap<>();
    static {
        //Initiliaze IDENTIFIERTONAME hashmap using static block
        IDENTIFIERTONAME.put(NetworkProtocol.REDFISH.charAt(0), "Redfish");
        IDENTIFIERTONAME.put(NetworkProtocol.CATTREAT.charAt(0), "Cat Treat");
        IDENTIFIERTONAME.put(NetworkProtocol.MILK.charAt(0), "Milk");
        IDENTIFIERTONAME.put(NetworkProtocol.PREMIUMCATFOOD.charAt(0), "Premium Cat Food++");
        IDENTIFIERTONAME.put(NetworkProtocol.GOLDFISH.charAt(0), "Goldfish");
        IDENTIFIERTONAME.put(NetworkProtocol.LIGHTSCARF.charAt(0), "Light Scarf");
        IDENTIFIERTONAME.put(NetworkProtocol.THICKSWEATER.charAt(0), "Thick Sweater");
        IDENTIFIERTONAME.put(NetworkProtocol.BAGOFCATNIP.charAt(0), "Bag of Catnip");
        IDENTIFIERTONAME.put(NetworkProtocol.LOUDBELL.charAt(0), "Loud Bell");
        IDENTIFIERTONAME.put(NetworkProtocol.PRINGLESCAN.charAt(0), "Pringles Can");
        IDENTIFIERTONAME.put(NetworkProtocol.HEAVYCAT.charAt(0), "HeavyCat");
        IDENTIFIERTONAME.put(NetworkProtocol.FASTCAT.charAt(0), "FastCat");
        IDENTIFIERTONAME.put(NetworkProtocol.GUNCAT.charAt(0), "GunCat");
        IDENTIFIERTONAME.put(NetworkProtocol.PLAYERSMASH.charAt(0), "PlayerSmash");
        IDENTIFIERTONAME.put(NetworkProtocol.PLAYERSLASH.charAt(0), "PlayerSlash");
        IDENTIFIERTONAME.put(NetworkProtocol.PLAYERBULLET.charAt(0), "PlayerBullet");
        IDENTIFIERTONAME.put(NetworkProtocol.RAT.charAt(0), "Rat");
        IDENTIFIERTONAME.put(NetworkProtocol.RATKING.charAt(0), "RatKing");
        IDENTIFIERTONAME.put(NetworkProtocol.SNAKE.charAt(0), "Snake");
        IDENTIFIERTONAME.put(NetworkProtocol.SNAKELET.charAt(0), "Snakelet");
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
        String name = IDENTIFIERTONAME.get(identifier);
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
            case "RatKing":
                return new RatKing(x, y);
            case "Snake":
                return new Snake(x, y);
            case "Snakelet":
                return new Snakelet(x, y);
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

    public void loadEntity(char identifier, int id, int x, int y, int roomId, int sprite){
        // System.out.println("Loading entity " + identifier + " " + name + "at " + x + ", " + y);
        // if (name == null) System.out.println("Warning: unknown identity identifier " + identifier);
        Entity e = getEntity(identifier, id, x, y);
        if (e != null) {
            e.setId(id);
            e.setCurrSprite(sprite);
            e.matchHitBoxBounds();
            e.setCurrentRoom(getRoomById(roomId));
            entities.add(e);
        }    
    }

    public void setHeldItemIdentifier(char identifier){
        heldItemIdentifier = identifier;
        // System.out.println(heldItemIdentifier);
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
