import java.awt.*;
import java.util.*;

public class Map {
    private ArrayList<Room> rooms;
 
    public Map(){        
        rooms = new ArrayList<>();
    }

    public void generateRooms(int numRooms){
        final int MAXTRIES = 100;
        int tries = 0;
           
        while (tries < MAXTRIES) { 
            rooms.clear();
            createRoomsNoConnections(numRooms);
            System.out.println("Rooms created");

            for (Room room : rooms) {
                // In the current room, generate 2 doors at random directions
                int maxDoors = 2;
                while (room.getDoors().size() < maxDoors){
                    room.addDoor();
                }
                
                HashMap<String, Room> doors = room.getDoors();

                // For each door in the current room
                for (HashMap.Entry<String, Room> door : doors.entrySet()){
                    
                    String direction = door.getKey();

                    // Skip the door if a room is already conected to it
                    if (door.getValue() != null) continue;
                    
                    final int MAXATTEMPTS = 100;
                    int attempts = 0;
                    Room randomRoom;

                    // Try to add a room at the specified direction of the room 
                    while (attempts < MAXATTEMPTS){
                        randomRoom = chooseRandomRoom();
                        if (room.isConnectable(direction, randomRoom) && (randomRoom.canAddMoreDoors())) {
                            room.connectRooms(direction, randomRoom);
                            System.out.println("Successfully connected Room " + room.getRoomId() + " to Room " + randomRoom.getRoomId() );
                            break;
                        } else attempts++;
                    }
                    
                    // If all rooms have been connected, stop.
                    if (areAllRoomsConnected()) {
                        System.out.println("All rooms connected.");
                        return;
                    } else {
                        System.out.println("Failed to connect all rooms. Retyring...");
                        tries++;
                    }
                }
            }
        }
        System.out.println("Failed to connect all rooms after " + MAXTRIES + " attempts. Please restart the game.");    
    }

    /**
     * Uses a BFS algorithm to check if all the rooms of the map can be traversed to from any other room.
     * @return a boolean corresponding that says if all the rooms are traversable
     */
    private boolean areAllRoomsConnected(){

        Queue<Room> queue = new LinkedList<>();
        Set<Room> visited = new HashSet<>();

        Room startingRoom = rooms.get(0);
        queue.add(startingRoom);
        visited.add(startingRoom);

        while (!queue.isEmpty()){
            // Get (also removes) the first element of the queue
            Room currentRoom = queue.poll();
            
            for (Room neighbor : currentRoom.getConnections()) {
                // Check if all the rooms connected to this room have been visited
                if (!visited.contains(neighbor)){
                    visited.add(neighbor);
                    // If this is the first time visiting the neighbor, that means we haven't dealt with ITS neighbors so we add to the queue
                    queue.add(neighbor);
                }
            }
        }

        // If the number of visited rooms == the number of rooms there are, then all rooms are reachable.
        return visited.size() == rooms.size();
    }

    /**
     * Returns a random room from the rooms ArrayList.
     * @return the random room chosen
     */
    private Room chooseRandomRoom(){
        return rooms.get((int)(Math.random() * rooms.size()));
    }

    /**
     * Creates a number of rooms separated by at least 100px and at most 400px.
     * @param numRooms the number of rooms to be made
     */
    private void createRoomsNoConnections(int numRooms){
        int x = 0;
        int y = 0;
        int distance = 100; // Rooms will never collide unless roomSize > 100

        // Create the rooms, no connections yet
        for (int i = 0; i < numRooms; i++) {
            Room r = new Room(i, x, y);

            // Separate the room by 100px to never collide
            x += distance;
            y += distance;

            rooms.add(r);
        }
    }

    
    public void draw(Graphics2D g2d){
        for (Room room : rooms) {
            room.draw(g2d);
        }
    }
}
