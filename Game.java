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

    public void changePlayerRoomLocation(){
        for (Entity player : players) {
            for (Door door : player.getCurrentRoom().getDoorsArrayList()) {

                if (player.isCollidingWithDoor(door)){
                    Room prev = player.getCurrentRoom();
                    Room next = door.getOtherRoom(prev);
                    player.setCurrentRoom(next);
                    
                    String exitDirection = door.getOppositeDirection(door.getDirection());
                    int offsetFromDoor = 5;
                    switch (exitDirection) {
                    case "T" :
                        player.setX(next.getX() + (next.getWidth() / 2) - door.getWidth());
                        player.setY(next.getY() + door.getHeight() + offsetFromDoor);
                        break;
                    case "B" :
                        player.setX(next.getX() + (next.getWidth() / 2) - door.getWidth());
                        player.setY(next.getY() + next.getHeight() - (door.getHeight() + offsetFromDoor));
                        break;
                    case "L":
                        player.setX(next.getX() + door.getWidth() + offsetFromDoor);
                        player.setY(next.getY() + (next.getHeight() / 2) - door.getHeight());
                        break;
                    case "R":
                        player.setX(next.getX() + next.getWidth() - (door.getWidth() + offsetFromDoor));
                        player.setY(next.getY() + (next.getHeight() / 2) - door.getHeight());
                        break;
                    default:
                        throw new AssertionError("AssertionError from changePlayerRoomLocation() method");

                    }
                }    
            }
        }
    }
}
