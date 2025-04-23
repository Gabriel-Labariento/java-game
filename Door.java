
import java.awt.Graphics2D;

public class Door extends GameObject {
    private int id;
    public static final int HEIGHT_TILES = 2;
    public static final int WIDTH_TILES = 2;
    private String direction;
    private Room roomA, roomB; // The door only appears on roomA, but is connected to another door in roomB 
    private boolean isOpen;
    private static int doorCount = 0;

    public Door(int x, int y, String direction, Room roomA, Room roomB){
        this.id = doorCount++;
        worldX = x;
        worldY = y;
        height = GameCanvas.TILESIZE * HEIGHT_TILES;
        width = GameCanvas.TILESIZE * WIDTH_TILES;
        tiles = new Tile[HEIGHT_TILES][WIDTH_TILES];
        populateTiles();
        isOpen = false;
        this.direction = direction;
        this.roomA = roomA;
        this.roomB = roomB;
    }


    public void draw(Graphics2D g2d, int offsetX, int offsetY) {
        g2d.fillRect(worldX - offsetX, worldY - offsetY, width, height);

    }

    public Room getOtherRoom(Room current){
        return (current == roomA) ? roomB : roomA;
    }

    /**
     * Returns a string containing the door data
     * @return a string with the format D:doorId,x,y,direction,roomAId,roomBId
     */
    public String serialize(){
        StringBuilder sb = new StringBuilder();
        sb.append(NetworkProtocol.DOOR)
        .append(id).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldX).append(NetworkProtocol.SUB_DELIMITER)
        .append(worldY).append(NetworkProtocol.SUB_DELIMITER)
        .append(direction).append(NetworkProtocol.SUB_DELIMITER)
        .append(roomA.getRoomId()).append(NetworkProtocol.SUB_DELIMITER)
        .append(roomB.getRoomId());

        return sb.toString();
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Room getRoomA() {
        return roomA;
    }

    public Room getRoomB() {
        return roomB;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    

    public int getDoorCount() {
        return doorCount;
    }

    public void setDoorCount(int doorCount) {
        Door.doorCount = doorCount;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}