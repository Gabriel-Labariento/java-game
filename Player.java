import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

public class Player extends Entity{
    private int screenX;
    private int screenY;

    public Player(int cid, int x, int y){
        this.clientId = cid;
        this.identifier = NetworkProtocol.PLAYER.toCharArray()[0];
        speed = 5;
        height = 16;
        width = 16;
        screenX = 720/2 - width/2;
        screenY = 540/2 - height/2;
        worldX = x;
        worldY = y;
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        Rectangle2D.Double sprite = new Rectangle2D.Double(xOffset, yOffset, width, height);
        g2d.setColor(Color.GREEN);
        g2d.fill(sprite);
    }

    public void update(char input){
        if(input == 'W') {
            if (isMoveInbound(0, -1 * speed)) worldY -= speed;
        }
        if(input == 'A') {
            if (isMoveInbound(-1 * speed, 0)) worldX -= speed;
        }
        if(input == 'S') {
            if (isMoveInbound(0, speed)) worldY += speed;
        }
        if(input == 'D') {
            if (isMoveInbound(speed, 0)) worldX += speed;
        }

    }


    /**
     * Builds a String storing room transition data in the form RC:destinationRoomId,newX,newY
     * @param room the room to transition to
     */
    private String getRoomTransitionData(Door origin, Room next) {
        StringBuilder sb = new StringBuilder();
        
        int[] newCoors = getNewPositionAfterRoomTransition(origin, next);
        int newX = newCoors[0];
        int newY = newCoors[1];

        sb.append(NetworkProtocol.ROOM_CHANGE).append(":")
        .append(next.getRoomId()).append(NetworkProtocol.SUB_DELIMITER)
        .append(newX).append(NetworkProtocol.SUB_DELIMITER)
        .append(newY);

        return sb.toString();
    }

    private int[] getNewPositionAfterRoomTransition(Door origin, Room next){
        int[] newCoordinates = new int[2];

        String otherDoorDirection = origin.getOppositeDirection(origin.getDirection());

        Door otherDoor = null;

        for (Door d : next.getDoorsArrayList()) {
            if (d.getDirection().equals(otherDoorDirection)) otherDoor = d;    
        }

        if (otherDoor == null) {
            System.out.println("Could not find corresponding door in getNewPositionAfterRoomTransition()");
            return null;
        }

        int offset = height;

        switch (otherDoorDirection) {
            case "T":
                newCoordinates[0] = next.getX() + (next.getWidth() / 2) - origin.getHeight();
                newCoordinates[1] = next.getY() + origin.getHeight() + offset;
                break;
            case "B":
                newCoordinates[0] = next.getX() + (next.getWidth() / 2) - origin.getWidth();
                newCoordinates[1] = next.getY() + next.getHeight() - (origin.getHeight() + offset);
                break;
            case "L":
                newCoordinates[0] = next.getX() + origin.getWidth() + offset;
                newCoordinates[1] = next.getY() + (next.getHeight() / 2) - origin.getHeight();
                break;
            case "R":
                newCoordinates[0] = next.getX() + next.getWidth() - (origin.getWidth() + offset);
                newCoordinates[1] = next.getY() + (next.getHeight() / 2) - origin.getHeight();
                break;
            default:
                throw new AssertionError("Assertion in getNewPositionAfterRoomTransition");
        }

        return newCoordinates;
    }

    /**
     * Checks if a player is colliding with any door in the currentRoom and returns that door
     * @return the door the player is colliding with
     */
    private Door getCollidingDoor(){
        for (Door d : currentRoom.getDoorsArrayList()) {
            if (isCollidingWithDoor(d)) return d;
        }
        return null;
    }

    /**
     * Checks if a player is colliding with a door
     * @param door the door object to be checked for collision with player
     * @return boolean true if the player and the door are colliding, false otherwise
     */
    private boolean isCollidingWithDoor(Door door) {
        return !((worldX < door.getX()) ||
                ( (worldX + width) > door.getX() + door.getWidth()) ||
                (worldY < door.getY()) ||
                ((worldY + height) > door.getY() + door.getHeight())
        );
    }


    public int[] getScreenPos() {
        int[] screenPos = {screenX, screenY};
        return screenPos;
    }
   
    @Override
    public String getAssetData(){
        StringBuilder sb = new StringBuilder();

        Door d = getCollidingDoor();
        if (d != null) {
            return getRoomTransitionData(d, d.getOtherRoom(currentRoom)); // return a different string upon room change
        } else {
            sb.append(worldX).append(NetworkProtocol.SUB_DELIMITER)
            .append(worldY).append(NetworkProtocol.SUB_DELIMITER)
            .append(currentRoom.getRoomId()).append(NetworkProtocol.DELIMITER);
        }

        return sb.toString();
    };
}
