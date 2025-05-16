import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientMaster {
    private Player userPlayer;
    private HashMap <Integer, Room> allRooms;
    private Room currentRoom;
    private CopyOnWriteArrayList<Entity> entities; 
    private int xpBarPercent;
    private int userLvl;
    private String heldItemIdentifier;
    private static final HashMap<String, String> IDENTIFIERTONAME = new HashMap<>();
    static {
        //Initiliaze IDENTIFIERTONAME hashmap using static block
        IDENTIFIERTONAME.put(NetworkProtocol.REDFISH, "Redfish");
        IDENTIFIERTONAME.put(NetworkProtocol.CATTREAT, "Cat Treat");
        IDENTIFIERTONAME.put(NetworkProtocol.MILK, "Milk");
        IDENTIFIERTONAME.put(NetworkProtocol.PREMIUMCATFOOD, "Premium Cat Food++");
        IDENTIFIERTONAME.put(NetworkProtocol.GOLDFISH, "Goldfish");
        IDENTIFIERTONAME.put(NetworkProtocol.LIGHTSCARF, "Light Scarf");
        IDENTIFIERTONAME.put(NetworkProtocol.THICKSWEATER, "Thick Sweater");
        IDENTIFIERTONAME.put(NetworkProtocol.BAGOFCATNIP, "Bag of Catnip");
        IDENTIFIERTONAME.put(NetworkProtocol.LOUDBELL, "Loud Bell");
        IDENTIFIERTONAME.put(NetworkProtocol.PRINGLESCAN, "Pringles Can");
        IDENTIFIERTONAME.put(NetworkProtocol.HEAVYCAT, "HeavyCat");
        IDENTIFIERTONAME.put(NetworkProtocol.FASTCAT, "FastCat");
        IDENTIFIERTONAME.put(NetworkProtocol.GUNCAT, "GunCat");
        IDENTIFIERTONAME.put(NetworkProtocol.PLAYERSMASH, "PlayerSmash");
        IDENTIFIERTONAME.put(NetworkProtocol.PLAYERSLASH, "PlayerSlash");
        IDENTIFIERTONAME.put(NetworkProtocol.PLAYERBULLET, "PlayerBullet");
        IDENTIFIERTONAME.put(NetworkProtocol.RAT, "Rat");
        IDENTIFIERTONAME.put(NetworkProtocol.ENEMYBITE, "EnemyBite");
        IDENTIFIERTONAME.put(NetworkProtocol.ENEMYBARK, "EnemyBark");
        IDENTIFIERTONAME.put(NetworkProtocol.RATKING, "RatKing");
        IDENTIFIERTONAME.put(NetworkProtocol.SNAKE, "Snake");
        IDENTIFIERTONAME.put(NetworkProtocol.SNAKELET, "Snakelet");
        IDENTIFIERTONAME.put(NetworkProtocol.SPIDER, "Spider");
        IDENTIFIERTONAME.put(NetworkProtocol.SPIDERBULLET, "SpiderBullet");
        IDENTIFIERTONAME.put(NetworkProtocol.SNAKEBULLET, "SnakeBullet");
        IDENTIFIERTONAME.put(NetworkProtocol.COCKROACH, "Cockroach");
        IDENTIFIERTONAME.put(NetworkProtocol.SMALLDOG, "SmallDog");
        IDENTIFIERTONAME.put(NetworkProtocol.BUNNY, "Bunny");
        IDENTIFIERTONAME.put(NetworkProtocol.FROG, "Frog");
        IDENTIFIERTONAME.put(NetworkProtocol.ENEMYSMASH, "EnemySmash");
        IDENTIFIERTONAME.put(NetworkProtocol.BEE, "Bee");
        IDENTIFIERTONAME.put(NetworkProtocol.FERALDOG, "FeralDog");
        IDENTIFIERTONAME.put(NetworkProtocol.TURTLE, "Turtle");

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


    public Entity getEntity(String identifier, int id, int x, int y){
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
            case "EnemyBite":
                return new EnemyBite(null, x, y);
            case "RatKing":
                return new RatKing(x, y);
            case "Snake":
                return new Snake(x, y);
            case "Snakelet":
                return new Snakelet(x, y);
            case "Spider":
                return new Spider(x, y);
            case "Cockroach":
                return new Cockroach(x, y);
            case "SmallDog":
                return new SmallDog(x, y);
            case "FeralDog":
                return new FeralDog(x, y);
            case "Bunny":
                return new Bunny(x, y);
            case "Bee":
                return new Bee(x, y);
            case "Frog":
                return new Frog(x, y);
            case "Turtle":
                return new Turtle(x, y);
            case "EnemySmash":
                return new EnemySmash(null, x, y);
            case "EnemyBark":
                return new EnemyBark(null, x, y);
            case "PlayerSlash":
                return new PlayerSlash(id, null, x, y, 0, false);
            case "PlayerSmash":
                return new PlayerSmash(id, null, x, y, 0, false);
            case "PlayerBullet":
                return new PlayerBullet(null, x, y, 0, 0, 0);
            case "SpiderBullet":
                return new SpiderBullet(null, x, y, 0, 0);
            case "SnakeBullet":
                return new SnakeBullet(null, x, y, 0, 0);
            default:
                return null;
        }
    }

    public void loadEntity(String identifier, int id, int x, int y, int roomId, int sprite, int zIndex){
        // System.out.println("Loading entity " + identifier + " " + name + "at " + x + ", " + y);
        // if (name == null) System.out.println("Warning: unknown identity identifier " + identifier);
        Entity e = getEntity(identifier, id, x, y);
        if (e != null) {
            e.setId(id);
            e.setCurrSprite(sprite);
            e.matchHitBoxBounds();
            e.setCurrentRoom(getRoomById(roomId));
            e.setzIndex(zIndex);
            entities.add(e);
        }    
    }

    public void setHeldItemIdentifier(String identifier){
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
