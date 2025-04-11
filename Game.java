import java.util.*;

public class Game {
    private GameMap gameMap;
    private ArrayList<Entity> players;

    public Game(){
        gameMap = new GameMap();
        gameMap.generateRooms(3);

        Room startRoom = gameMap.getStartRoom();
        
        players = new ArrayList<>();
        Player p = new Player(startRoom.getX(), startRoom.getY(), 5);
        p.setCurrentRoom(startRoom);
        players.add(p);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public ArrayList<Entity> getPlayers() {
        return players;
    }
}
