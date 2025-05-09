import java.awt.Graphics2D;

public class Door extends GameObject implements Tileable {
    private int id;
    public static final int HEIGHT_TILES = 2;
    public static final int WIDTH_TILES = 2;
    private String direction;
    private Room roomA, roomB; // The door only appears on roomA, but is connected to another door in roomB 
    private boolean isOpen, isExitToNewDungeon;
    
    

    private static int doorCount = 0;
    private static final int[][] CLOSED_DOOR_LAYOUT = {{16,17}, {18,19}};
    private static final int[][] OPEN_DOOR_LAYOUT = {{20,21}, {22,23}};
    

    public Door(int x, int y, String direction, Room roomA, Room roomB){
        this.id = doorCount++;
        worldX = x;
        worldY = y;
        height = GameCanvas.TILESIZE * HEIGHT_TILES;
        width = GameCanvas.TILESIZE * WIDTH_TILES;
        tiles = new Tile[HEIGHT_TILES][WIDTH_TILES];
        populateTiles();
        isOpen = true;
        isExitToNewDungeon = false;
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

    @Override
    public void matchHitBoxBounds() {
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY;
        hitBoxBounds[1] = worldY + height;
        hitBoxBounds[2]= worldX;
        hitBoxBounds[3] = worldX + width;
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

    @Override
    public int[][] loadLayout(){
        return (isOpen) ? OPEN_DOOR_LAYOUT : CLOSED_DOOR_LAYOUT;
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

    public void setRoomB(Room roomB) {
        this.roomB = roomB;
    }

    public boolean isExitToNewDungeon() {
        return isExitToNewDungeon;
    }

    public void setIsExitToNewDungeon(boolean isExitToNewDungeon) {
        this.isExitToNewDungeon = isExitToNewDungeon;
    }
}