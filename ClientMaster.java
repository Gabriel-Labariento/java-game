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
        //Player entities
        if (identifier == NetworkProtocol.HEAVYCAT.charAt(0)) return new HeavyCat(id, x, y);
        else if (identifier == NetworkProtocol.FASTCAT.charAt(0)) return new FastCat(id, x, y);
        else if (identifier == NetworkProtocol.GUNCAT.charAt(0)) return new GunCat(id, x, y);

        //Item entities
        else if (identifier == NetworkProtocol.REDFISH.charAt(0)) return new RedFish(x, y);
        else if (identifier == NetworkProtocol.CATTREAT.charAt(0)) return new CatTreat(x, y);
        else if (identifier == NetworkProtocol.MILK.charAt(0)) return new Milk(x, y);
        else if (identifier == NetworkProtocol.PREMIUMCATFOOD.charAt(0)) return new PremiumCatFood(x, y);
        else if (identifier == NetworkProtocol.GOLDFISH.charAt(0)) return new Goldfish(x, y);
        else if (identifier == NetworkProtocol.LIGHTSCARF.charAt(0)) return new LightScarf(x, y);
        else if (identifier == NetworkProtocol.THICKSWEATER.charAt(0)) return new ThickSweater(x, y);
        else if (identifier == NetworkProtocol.BAGOFCATNIP.charAt(0)) return new BagOfCatnip(x, y);
        else if (identifier == NetworkProtocol.LOUDBELL.charAt(0)) return new LoudBell(x, y);
        else if (identifier == NetworkProtocol.PRINGLESCAN.charAt(0)) return new PringlesCan(x,y);

        //Enemy entities
        //Normal 
        else if (identifier == NetworkProtocol.SPIDER.charAt(0)) return new RatKing(x, y);
        else if (identifier == NetworkProtocol.COCKROACH.charAt(0)) return new RatKing(x, y);
        else if (identifier == NetworkProtocol.RAT.charAt(0)) return new Rat(x, y);
        else if (identifier == NetworkProtocol.SMALLDOG.charAt(0)) return new Snakelet(x, y);
        else if (identifier == NetworkProtocol.BUNNY.charAt(0)) return new BagOfCatnip(x, y);
        else if (identifier == NetworkProtocol.LOUDBELL.charAt(0)) return new LoudBell(x, y);
        else if (identifier == NetworkProtocol.FROG.charAt(0)) return new PringlesCan(x,y);
        else if (identifier == NetworkProtocol.BEE.charAt(0)) return new RatKing(x, y);
        else if (identifier == NetworkProtocol.SNAKELET.charAt(0)) return new Snakelet(x, y);
        else if (identifier == NetworkProtocol.CLEANINGBOT.charAt(0)) return new BagOfCatnip(x, y);
        else if (identifier == NetworkProtocol.SECURITYBOT.charAt(0)) return new SecurityBot(x, y);
        else if (identifier == NetworkProtocol.FERALRAT.charAt(0)) return new PringlesCan(x,y);
        else if (identifier == NetworkProtocol.SCREAMERRAT.charAt(0)) return new RatKing(x, y);
        else if (identifier == NetworkProtocol.MUTATEDANCHOVY.charAt(0)) return new Snakelet(x, y);
        else if (identifier == NetworkProtocol.MUTATEDARCHERFISH.charAt(0)) return new BagOfCatnip(x, y);
        else if (identifier == NetworkProtocol.MUTATEDPUFFERFISH.charAt(0)) return new LoudBell(x, y);

        //Bosses
        else if (identifier == NetworkProtocol.RATKING.charAt(0)) return new RatKing(x,y);
        else if (identifier == NetworkProtocol.FERALDOG.charAt(0)) return new RatKing(x, y);
        else if (identifier == NetworkProtocol.MUTATEDANCHOVY.charAt(0)) return new Snakelet(x, y);
        else if (identifier == NetworkProtocol.TURTLE.charAt(0)) return new BagOfCatnip(x, y);
        else if (identifier == NetworkProtocol.SNAKE.charAt(0)) return new LoudBell(x, y);
        else if (identifier == NetworkProtocol.ADULTCAT.charAt(0)) return new PringlesCan(x,y);
        else if (identifier == NetworkProtocol.CONJOINEDRATS.charAt(0)) return new RatKing(x, y);
        else if (identifier == NetworkProtocol.FISHMONSTER.charAt(0)) return new Snakelet(x, y);

        //Attack entities
        else if (identifier == NetworkProtocol.PLAYERSMASH.charAt(0)) 
            return new PlayerSmash(id, null, x, y, 0, false);
        else if (identifier == NetworkProtocol.PLAYERSLASH.charAt(0)) 
            return new PlayerSlash(id, null, x, y, 0, false);
        else if (identifier == NetworkProtocol.PLAYERBULLET.charAt(0)) 
            return new PlayerBullet(id, null, x, y, 0, 0, 0, false);
        else return null;
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
