import java.awt.*;

public abstract class Entity {
    private int x, y, speed, roomId;

    public Entity(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        roomId = -1;
    }

    public void draw(Graphics2D g2d){
        g2d.fillRect(x, y, 10, 10);
    }
    
    public void move(int dx, int dy){
        this.x += this.speed * dx;
        this.y += this.speed * dy;
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

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    
    
} 