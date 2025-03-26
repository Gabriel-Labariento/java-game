
import java.awt.Color;
import java.awt.Graphics2D;

public class Room {
    private int x, y, width, height;

    public Room(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public boolean isColliding(Room other){
        return !(this.x + this.width <= other.getX() ||
                this.x >= other.getX() + other.getWidth() ||
                this.y + this.height <= other.getY() ||
                this.y >= other.getY() + other.getHeight() );
    }

    public void drawRoom(Graphics2D g2d){
        g2d.setColor(Color.GRAY);
        g2d.fillRect(x, y, width, height);
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

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
