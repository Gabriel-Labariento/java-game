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
        //Player entities
        if (identifier.equals( NetworkProtocol.HEAVYCAT)) return new HeavyCat(id, x, y);
        else if (identifier.equals( NetworkProtocol.FASTCAT)) return new FastCat(id, x, y);
        else if (identifier.equals( NetworkProtocol.GUNCAT)) return new GunCat(id, x, y);

        //Item entities
        else if (identifier.equals( NetworkProtocol.REDFISH)) return new RedFish(x, y);
        else if (identifier.equals( NetworkProtocol.CATTREAT)) return new CatTreat(x, y);
        else if (identifier.equals( NetworkProtocol.MILK)) return new Milk(x, y);
        else if (identifier.equals( NetworkProtocol.PREMIUMCATFOOD)) return new PremiumCatFood(x, y);
        else if (identifier.equals( NetworkProtocol.GOLDFISH)) return new Goldfish(x, y);
        else if (identifier.equals( NetworkProtocol.LIGHTSCARF)) return new LightScarf(x, y);
        else if (identifier.equals( NetworkProtocol.THICKSWEATER)) return new ThickSweater(x, y);
        else if (identifier.equals( NetworkProtocol.BAGOFCATNIP)) return new BagOfCatnip(x, y);
        else if (identifier.equals( NetworkProtocol.LOUDBELL)) return new LoudBell(x, y);
        else if (identifier.equals( NetworkProtocol.PRINGLESCAN)) return new PringlesCan(x,y);

        //Enemy entities
        //Normal 
        else if (identifier.equals( NetworkProtocol.SPIDER)) return new RatKing(x, y);
        else if (identifier.equals( NetworkProtocol.COCKROACH)) return new RatKing(x, y);
        else if (identifier.equals( NetworkProtocol.RAT)) return new Rat(x, y);
        else if (identifier.equals( NetworkProtocol.SMALLDOG)) return new Snakelet(x, y);
        else if (identifier.equals( NetworkProtocol.BUNNY)) return new BagOfCatnip(x, y);
        else if (identifier.equals( NetworkProtocol.FROG)) return new PringlesCan(x,y);
        else if (identifier.equals( NetworkProtocol.BEE)) return new RatKing(x, y);
        else if (identifier.equals( NetworkProtocol.SNAKELET)) return new Snakelet(x, y);
        else if (identifier.equals( NetworkProtocol.CLEANINGBOT)) return new BagOfCatnip(x, y);
        else if (identifier.equals( NetworkProtocol.SECURITYBOT)) return new SecurityBot(x, y);
        else if (identifier.equals( NetworkProtocol.FERALRAT)) return new PringlesCan(x,y);
        else if (identifier.equals( NetworkProtocol.SCREAMERRAT)) return new RatKing(x, y);
        else if (identifier.equals( NetworkProtocol.MUTATEDANCHOVY)) return new Snakelet(x, y);
        else if (identifier.equals( NetworkProtocol.MUTATEDARCHERFISH)) return new BagOfCatnip(x, y);
        else if (identifier.equals( NetworkProtocol.MUTATEDPUFFERFISH)) return new LoudBell(x, y);

        //Bosses
        else if (identifier.equals( NetworkProtocol.RATKING)) return new RatKing(x,y);
        else if (identifier.equals( NetworkProtocol.FERALDOG)) return new RatKing(x, y);
        else if (identifier.equals( NetworkProtocol.TURTLE)) return new BagOfCatnip(x, y);
        else if (identifier.equals( NetworkProtocol.SNAKE)) return new LoudBell(x, y);
        else if (identifier.equals( NetworkProtocol.ADULTCAT)) return new PringlesCan(x,y);
        else if (identifier.equals( NetworkProtocol.CONJOINEDRATS)) return new RatKing(x, y);
        else if (identifier.equals( NetworkProtocol.FISHMONSTER)) return new Snakelet(x, y);

        //Attack entities
        else if (identifier.equals( NetworkProtocol.PLAYERSMASH)) 
            return new PlayerSmash(id, null, x, y, 0, false);
        else if (identifier.equals( NetworkProtocol.PLAYERSLASH)) 
            return new PlayerSlash(id, null, x, y, 0, false);
        else if (identifier.equals( NetworkProtocol.PLAYERBULLET)) 
            return new PlayerBullet(id, null, x, y, 0, 0, 0, false);
        else return null;
    }

    public void loadEntity(String identifier, int id, int x, int y, int roomId, int sprite){
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
