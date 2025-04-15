
import java.awt.Graphics2D;

public class Door {
    private int id, x, y;
    private final int height = 10;
    private final int width = 10;
    private String direction;
    private Room roomA, roomB;
    private int doorCount = 0;

    public Door(int x, int y, String direction, Room roomA, Room roomB){
        this.id = doorCount++;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.roomA = roomA;
        this.roomB = roomB;
    }

    public void draw(Graphics2D g2d) {
        g2d.fillRect(x, y, width, height);
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
        sb.append(NetworkProtocol.DOOR).append(":")
        .append(id).append(",")
        .append(x).append(",")
        .append(y).append(",")
        .append(direction).append(",")
        .append(roomA.getRoomId()).append(",")
        .append(roomB.getRoomId());

        return sb.toString();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
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

    public void setRoomA(Room roomA) {
        this.roomA = roomA;
    }

    public Room getRoomB() {
        return roomB;
    }

    public void setRoomB(Room roomB) {
        this.roomB = roomB;
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
        this.doorCount = doorCount;
    }
}