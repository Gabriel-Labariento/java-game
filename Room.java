// public class Room{
//     private char id;

//     public Room(char identifier){
//         id = identifier;
//     }

//     public char getRoomId(){
//         return id;
//     }

//     // public void draw(Graphics2D g2d){
        
//     // }
// }

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*; 

public class Room {
    private int roomId, x, y;
    private boolean isStartRoom, isEndRoom;
    private final int height = 80;
    private final int width = 80;
    private ArrayList<Room> connections;
    private HashMap<String, Room> doors;
    private ArrayList<Door> doorsArrayList;

    /**
     * Creates a Room object with an ID, x and y coordinates, ArrayList of connections, and HashMap of doors.
     * @param roomId the unique int used to identify the room
     * @param x the x-coordinate of the room
     * @param y the y-coordinate of the room
     */
    public Room(int roomId, int x, int y){
        this.roomId = roomId;
        this.x = x;
        this.y = y;
        isStartRoom = false;
        isEndRoom = false;
        connections = new ArrayList<>();
        doors = new HashMap<>();
        doorsArrayList = new ArrayList<>();
    }

    /**
     * Returns a string containing the room data
     * @return a string in the format roomId,x,y,isStart,isEnd|door1Data|door2Data|...
     */
    public String serialize(){
        StringBuilder sb = new StringBuilder();

        // roomId,x,y,isStart,isEnd|
        sb.append(roomId).append(NetworkProtocol.SUB_DELIMITER)
        .append(x).append(NetworkProtocol.SUB_DELIMITER)
        .append(y).append(NetworkProtocol.SUB_DELIMITER)
        .append(isStartRoom).append(NetworkProtocol.SUB_DELIMITER)
        .append(isEndRoom);

        for (Door door : doorsArrayList) {
            sb.append(NetworkProtocol.DELIMITER).append(door.serialize());
        }

        return sb.toString();
    }

    /**
     * Adds the "other" argument to the list of connections of the calling room and adds the calling room to the connections of "other."
     * Puts the corresponding room and direction of the other room to this room's doors hashmap, does the vice versa as well.
     * @param other the room to be connected to the calling room
     */
    public void connectRooms(String direction, Room other){
        // Add the other room to the list of connections
        connections.add(other);

        // Put in the room's hashmap, the direction and the connected room
        doors.put(direction, other);
        // doors.add(new Door)

        // Add this room to the other's connections 
        other.getConnections().add(this);
        
        // Determine the opposite direction of "direction" argument
        String oppositeDirection = getOppositeDirection(direction);

        // Put in other's hashmap, the opposite direction and this room.
        other.getDoors().put(oppositeDirection, this);
    }
    
    /**
     * Adds a door at a random direction of the room. Only one door can be placed in one direction.
     */
    public void addDoor(){
        // Choose a random direction
        String direction;
        while (true){
            direction = chooseRandomDirection();
            if ((direction != null) && (!doors.containsKey(direction))){
                // If direction is not null, and the room does not yet have a door at that direction, add a door there
                doors.put(direction, null);
                break;
            }
        }
    }

    /**
     * Chooses one random direction.
      * @return a String indicating the chosen direction: "T" "R" "B" "L". 
     */
    private String chooseRandomDirection(){
        int rand = (int) (Math.random() * 4);
        switch (rand) {
            case 0:
                return "T";
            case 1: 
                return "R";
            case 2:
                return "B";
            case 3:
                return "L";
            default:
                break;
        }
        return null;
    }

    /**
     * Gets the doors hashmap of the room object.
     * @return the doors hashmap in the form <String s, Room r> where s is the direction and r is the connected room
     */
    public HashMap<String, Room> getDoors(){
        return doors;
    }

    public ArrayList<Room> getConnections(){
        return connections;
    }

    /**
     * Provides the opposite of the provided direction
     * @param direction the direction whose opposite is to be determined
     * @return the opposite of the passed direction "T" <-> "B" and "L" <-> "R"
     */
    private String getOppositeDirection(String direction){
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

    /**
     * Checks whether the calling room has a direct connection to the passed room
     * @param other the room to which the calling room is checked for connection
     * @return
     */
    public boolean isConnected(Room other){
        return (connections.contains(other) && other.getConnections().contains(this));
    }

    /**
     * Checks if two rooms can be connected via the ff:
     * 1. Two rooms aren't the same room
     * 2. Two rooms aren't already connected
     * 3. The calling room doesn't already have a door at the specified direction.
     * 4. The passed room doesn't already have a door at the opposite direction.
     * @param direction where in the calling room the connection is to be made
     * @param other the room to be connected to the calling room
     * @return true / false whether or not the rooms can be connected
     */
    public boolean isConnectable(String direction, Room other){
        // Don't connect a room to itself
        if (this == other) return false;
        
        // Don't connect rooms if they're already connected
        if (isConnected(other)) return false;

        // Don't connect rooms at a direction that already has another door
        if ((doors.containsKey(direction)) && (doors.get(direction) != null)) return false;

        // Don't connect rooms if the other room already has a door at the opposite direction
        String oppositeDirection = getOppositeDirection(direction);
        if ( (other.getDoors().containsKey(oppositeDirection)) && (other.getDoors().get(oppositeDirection) != null)) return false;

        System.out.printf("Room %d and Room %d  can be connected.\n", roomId, other.getRoomId());
        return true;
    }

    /**
     * Return the ID of the room instance
     * @return the room's ID
     */
    public int getRoomId(){
        return roomId;
    }

    public void draw(Graphics2D g2d){
        // Inside
        if (isStartRoom) {
            g2d.setColor(Color.RED);
        } else if (isEndRoom){
            g2d.setColor(Color.BLUE);
        } else g2d.setColor(Color.DARK_GRAY);

        g2d.fillRect(x, y, width, height);

        // Border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, width, height);
        
        // Draw doors
        drawDoors(g2d);
    }

    /**
     * Draws the doors inside doorsArrayList 
     * @param g2d object used to draw
     */
    private void drawDoors(Graphics2D g2d){
        for (Door door : doorsArrayList) {
            door.draw(g2d);
        }
    }

    /**
     * Populates the doorsArrayList with new Door objects based on 
     * the contents of the doors HashMap. Called inside populateAllDoorsArrayList 
     */
    public void populateDoorsArrayList(){
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        int doorHeight = 10;
        int doorWidth = 10;
        Door d;

        for (HashMap.Entry<String, Room> door : doors.entrySet()) {
             switch (door.getKey()) {
                case "T":
                    d = new Door(centerX - doorWidth, y, door.getKey(), this, door.getValue());
                    doorsArrayList.add(d);
                    break;
                case "B":
                    d = new Door(centerX - doorWidth, y + height - doorHeight, door.getKey(), this, door.getValue());
                    doorsArrayList.add(d);
                    break;
                case "L":
                    d = new Door(x, centerY - doorHeight, door.getKey(), this, door.getValue());
                    doorsArrayList.add(d);
                    break;
                case "R":
                    d = new Door(x + width - doorWidth, centerY - doorHeight, door.getKey(), this, door.getValue());
                    doorsArrayList.add(d);
                    break;
                default:
                    throw new AssertionError("Error in populateDoorsArrayList method of Room " + roomId);
            }
        }
    }

    /**
     * Checks if a door has less than two rooms
     * @return boolean true/false indicating whether a room has less than two doors
     */
    public boolean canAddMoreDoors(){
        return (doors.size() < 2);
    }

    public void setIsStartRoom(boolean isStartRoom) {
        this.isStartRoom = isStartRoom;
    }

    public void setIsEndRoom(boolean isEndRoom) {
        this.isEndRoom = isEndRoom;
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

    
    public ArrayList<Door> getDoorsArrayList() {
        return doorsArrayList;
    }

    public void setDoorsArrayList(ArrayList<Door> doorsArrayList) {
        this.doorsArrayList = doorsArrayList;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}