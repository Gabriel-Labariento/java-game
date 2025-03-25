
import java.awt.*;

public abstract class MovableCharacter {
    private int x, y, speed;

    public MovableCharacter(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void draw(Graphics2D g2d){
        g2d.fillRect(x, y, 10, 10);
    }
    
    public void move(int dx, int dy){
        this.x += this.speed * dx;
        this.y += this.speed * dy;
    }
    
}
