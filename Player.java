

public class Player extends Entity{
    private final int SIZE = 10;
    private Room currentRoom;

    public Player(int x, int y, int speed) {
        super(x, y, speed);
        currentRoom = null;
    }

    @Override
    public boolean isCollidingWithDoor(Door door){
        System.out.println("is colliding with door method called");
        boolean collided =  !(getX() + SIZE <= door.getX() ||
                 getX() >= door.getX() + door.getWidth() ||
                 getY() + SIZE <= door.getY() ||
                 getY() >= door.getY() + door.getHeight());
        
        if (collided) System.out.println("Collision detected in isCollidingWithDoor method");
        return collided;
    }
}