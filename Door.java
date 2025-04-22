
import java.awt.Graphics2D;

public class Door extends GameObject {
    private int id;
    public static final int HEIGHT_TILES = 2;
    public static final int WIDTH_TILES = 2;
    private String direction;
    private Room roomA, roomB;
    private static int doorCount = 0;

    public Door(int x, int y, String direction, Room roomA, Room roomB){
        this.id = doorCount++;
        worldX = x;
        worldY = y;
        height = GameCanvas.TILESIZE * HEIGHT_TILES;
        width = GameCanvas.TILESIZE * WIDTH_TILES;
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
    
    /**
     * Provides the opposite of the provided direction
     * @param direction the direction whose opposite is to be determined
     * @return the opposite of the passed direction "T" <-> "B" and "L" <-> "R"
     */
    public String getOppositeDirection(String direction){
        switch (direction) {
            case "T":
                return "B";
            case "R":
                return "L";
            case "B":
                return "T";
            case "L":
                return "R";
            default:
                throw new AssertionError("Assertion in getOppositeDirection() method of the Room.");
        }
    }

    public int getDoorCount() {
        return doorCount;
    }

    public void setDoorCount(int doorCount) {
        Door.doorCount = doorCount;
    }
}