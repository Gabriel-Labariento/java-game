public class Door {
    private Room roomA, roomB;

    public Door(Room roomA, Room roomB){
        this.roomA = roomA;
        this.roomB = roomB;
    }

    public Room getOtherRoom(Room current){
        return (current == roomA) ? roomB : roomA;
    }

    
}
