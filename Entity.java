import java.awt.*;

public abstract class Entity {
    private int x, y, speed;
    private final int SIZE = 10;
    private Room currentRoom;

    public Entity(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        currentRoom = null;
    }

    public void draw(Graphics2D g2d){
        g2d.fillRect(x, y, 10, 10);
    }
    
    public void move(int dx, int dy){
        int newX = x + speed * dx;
        int newY = y + speed * dy;

        if ((currentRoom != null) && (isMoveInbound(dx, dy))) {
            x = newX;
            y = newY;
        }
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        currentRoom = room;
    }
    
    private boolean isMoveInbound(int dx, int dy) {
        return !((x + dx < currentRoom.getX()) ||
                ( (x + SIZE) + dx > currentRoom.getX() + currentRoom.getSize()) ||
                (y + dy < currentRoom.getY()) ||
                ((y + SIZE) + dy > currentRoom.getY() + currentRoom.getSize())
             );
    }
    
} 