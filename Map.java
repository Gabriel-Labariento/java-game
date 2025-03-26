import java.awt.*;
import java.util.ArrayList;

public class Map {
    
    private Tile[][] tiles;
    private final int TILESIZE = 36; 
    private final int TILECOUNTX = (int) (720.0 / TILESIZE); // 24
    private final int TILECOUNTY = (int) (540.0 / TILESIZE); // 9
    private final int TILEHEIGHT = (int) (540.0 / TILECOUNTY);
    private final int TILEWIDTH = (int) (720.0 / TILECOUNTX);
    private ArrayList<Room> rooms;

    public Map(){
        // Instantiate the rooms array
        rooms = new ArrayList<Room>();
        // Instantiate the tiles array and populate it with tile objects
        tiles = new Tile[TILECOUNTY][TILECOUNTX];
        for (int i = 0; i < TILECOUNTY; i++) {
            for (int j = 0; j < TILECOUNTX; j++) {
                // The x and y coordinate of the tile corresponds to its index in the 2D array
                tiles[i][j] = new Tile(j * TILEWIDTH, i * TILEHEIGHT);
            }
        }
    }

    public void drawMap(Graphics2D g2d){
        // Each primary index is an array of tiles
        for (Tile[] tilesArray : tiles) {
            // Each secondary index is a tile 
            for (Tile tile : tilesArray) {
                tile.drawTile(g2d, TILEWIDTH, TILEHEIGHT);
            }
        }
        
    }

    public void generateRooms(){
        final int MAXROOMS = 8;
        int roomWidth;
        int roomHeight;

        // While the map hasn't been filled to the desired amount
        while (rooms.size() < MAXROOMS){
            // Generate a random room size based on MINROOMSIZE AND MAXROOMSIZE;
            roomWidth = generateRandomRoomSize();
            roomHeight = generateRandomRoomSize();
            
            // Find a random location in the map and check if the room will fit
            Room newRoom = checkRoomFit(roomWidth, roomHeight);
            
            // If the room is not null (meaning it fits), add it to the ArrayList 
            if (newRoom != null){
                rooms.add(newRoom);
            }
        }
            
        // Choose a room to be the starting room
        // Put the exit in the room farthest from the starting room
        // For each room in the list of rooms, populate with monsters
    }

    private int generateRandomRoomSize(){
        final int MINROOMSIZE = 2;
        final int MAXROOMSIZE = 7;
        return (int) (Math.random() * (MAXROOMSIZE - MINROOMSIZE + 1)) + MINROOMSIZE;
    }

    private Room checkRoomFit(int roomWidth, int roomHeight){
        Room r = null;
        // Come up with a random coordinate on the map
        int x = (int) (Math.random() * TILECOUNTX);
        int y = (int) (Math.random() * TILECOUNTY);

        // From x and y, add width and height and check if it exceeds the tile count
        if ((x + roomWidth <= TILECOUNTX) && (y + roomHeight <= TILECOUNTY)){
            r = new Room(x * TILESIZE, y * TILESIZE, roomWidth * TILESIZE, roomHeight * TILESIZE);
            
            // If the room collides with another room, scrap it.
            for (Room other : rooms){
                if (r.isColliding(other)){
                    return null;
                }
            }
        };

        // Return the room if it fits and does not collide with another room.
        return r;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
}
